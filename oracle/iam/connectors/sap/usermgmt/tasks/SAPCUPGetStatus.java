package oracle.iam.connectors.sap.usermgmt.tasks;

import java.util.HashMap;
import java.util.Hashtable;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.dao.OIMUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.common.vo.ITResource;
import oracle.iam.connectors.common.vo.ScheduledTask;
import oracle.iam.connectors.sap.usermgmt.integration.SAPUMProxyUserProvisionManager;
import oracle.iam.connectors.sap.usermgmt.util.UMConstants;
import Thor.API.tcResultSet;
import Thor.API.Operations.tcFormInstanceOperationsIntf;
import Thor.API.Operations.tcITResourceInstanceOperationsIntf;
import Thor.API.Operations.tcSchedulerOperationsIntf;
import Thor.API.Operations.tcUserOperationsIntf;

import com.thortech.xl.scheduler.tasks.SchedulerBaseTask;
/**
 * Description:Mapped to the scheduled task that is configured for sap cup status synchronization 
 */
public class SAPCUPGetStatus extends SchedulerBaseTask implements UMConstants {
	//private boolean isStopRecon = false;
	private boolean isValid = false;
	private ConnectorLogger logger = new ConnectorLogger("OIMCP.SAPU");
	private String className = this.getClass().getName();
	private Hashtable<String, String> htCUPITRattributes;
	private HashMap<String, String> hmCUPConstants;
	private tcUserOperationsIntf userAPI = null;
	private tcSchedulerOperationsIntf schedulerAPI = null;
	private tcITResourceInstanceOperationsIntf resAPI = null;
	private tcFormInstanceOperationsIntf fiIntf = null;
	private StringUtil stringUtil = new StringUtil();
	private Hashtable<String, String> htTaskAttributes;
	private String ResourceObjName = null;
	private OIMUtil OIMUtil;
	
	/**
	 * Description: Initializes the scheduled task attributes and lookup
	 * definitions. It also checks if all required parameters are correctly set
	 * in the IT resource and scheduled task.
	 */
	public void init() {
		String sMethodName = "init()";
		logger.setMethodStartLog(className, sMethodName);
		logger.info(className, sMethodName,
				"Start of SAP CUP Status Recon Process");
		try {
			userAPI = (tcUserOperationsIntf) super.getUtility(USER_API);
			schedulerAPI = (tcSchedulerOperationsIntf) super
					.getUtility(SCHED_TASK_API);
			resAPI = (tcITResourceInstanceOperationsIntf) super
					.getUtility(IT_RESOURCE_API);
			fiIntf = (tcFormInstanceOperationsIntf) super.getUtility(FORM_API);
			OIMUtil = new OIMUtil(logger);

			logger.debug(className,sMethodName,"Get the task scheduler name and validate it");
			String sScheduleTaskName = super.getAttribute(SCHEDULE_TASK_NAME);
			if (stringUtil.isEmpty(sScheduleTaskName)) {
				logger.error(className, sMethodName,
								"Task Scheduler Name value is not set in task scheduler");
				throw new ConnectorException(
						"Task Scheduler Name value is not set in task scheduler");
			}
			logger.debug(className,sMethodName,"Get the task scheduler attributes in Hashtable");
			ScheduledTask oTaskAttributes = new ScheduledTask(
					sScheduleTaskName, logger);
			htTaskAttributes = oTaskAttributes
					.getScheduledTaskDetails(schedulerAPI);
			logger.debug(className,sMethodName,"Get the IT Resource attributes in Hashtable");
			ITResource oITResource = new ITResource((String) htTaskAttributes
					.get(IT_RESOURCE_NAME), resAPI, logger);
			htCUPITRattributes = oITResource.getITResourceDetails();
			//CUP Constants
			String sCUPConstantsLookup = htTaskAttributes
					.get(CONSTANTS_LOOKUP);
			ResourceObjName = htTaskAttributes
			.get(RESOURCE_OBJECT);
			logger.debug(className,sMethodName,"OIM Resource Object Name: "+ResourceObjName);
			hmCUPConstants = OIMUtil.getLookUpMap(sCUPConstantsLookup);
		} catch (Exception e) {
			isValid = false;
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
		}
	}
	
	/**
	 *Description: Executed from scheduled task after all the values are initialized
	 * 
	 */
	public void execute() {
		String sMethodName = "execute()";
		isValid = true;
		logger.setMethodStartLog(className, sMethodName);
		long ProcessInstanceKey;
		try {
			logger.debug(className, sMethodName, "Innitialization is "+isValid);
			if (isValid) {
				HashMap<String, String> hmUserIdToSearch = new HashMap<String, String>();
				HashMap<String, String> hmFormData = new HashMap<String, String>();
				logger.debug(className,sMethodName,"get all active users in OIM");
				hmUserIdToSearch.put(OIM_USER_ID, "*");
				tcResultSet userSet = userAPI.getActiveUsers(hmUserIdToSearch);
				logger.debug(className, sMethodName, "Number of active users: "+userSet.getRowCount());
				//Iterate through users
				if (userSet != null && userSet.getRowCount() > 0) {
					for (int i = 0; i < userSet.getRowCount(); i++) {
						userSet.goToRow(i);
						long userKeyLong = userSet.getLongValue(USER_KEY);
						logger.debug(className,sMethodName,"Get the set of RO's with status: provisioned");
						tcResultSet objectSet1 = userAPI
								.getObjectsByTypeStatus(userKeyLong,
										APPLICATION, PROVISIONED_STATUS);
						/*Iterate through provisioned RO's to check if 
						*any task is open. If so update form
						*/ 
						logger.debug(className, sMethodName, "Number of RO's: "+objectSet1.getRowCount());
						for (int k = 0; k < objectSet1.getRowCount(); k++) {
							//get RO Name of the object and compare if it is from SAP
							String sROName = objectSet1.getStringValue(OBJECT_NAME);
							logger.debug(className,sMethodName,"Resource Object Name: "+sROName);
							if(ResourceObjName.equalsIgnoreCase(sROName)){
							objectSet1.goToRow(k);
							String sReqID = null;
							HashMap<String, String> hmFormUpdateData = new HashMap<String, String>();
							/*Get Process Instance key
							 * */
							ProcessInstanceKey = objectSet1
									.getLongValue(PROCESS_INSTANCE_KEY);
							logger.debug(className,sMethodName,"Process Instance Key:"+ProcessInstanceKey);
							if (ProcessInstanceKey == 0) {
								continue;
							}
							//get process form data
							hmFormData = OIMUtil
									.getFormDataMap(ProcessInstanceKey);
							SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
									null, null);
							if(hmFormData.get(hmCUPConstants.get(UD_REQUEST_STATUS)) == null){
								logger.debug(sROName, sMethodName, "Process form does not have CUP request fields");
								continue;
							}
							/*If the status is 'open' or 'hold' then handle it
							 * */
							String sFormReqStatus = hmFormData.get(hmCUPConstants.get(UD_REQUEST_STATUS));
							if (sFormReqStatus.equalsIgnoreCase(hmCUPConstants.get(OPEN))
									|| sFormReqStatus.equalsIgnoreCase(hmCUPConstants.get(HOLD))) {
								sReqID = hmFormData.get(hmCUPConstants.get(UD_REQUEST_ID));
								logger.info(sROName, sMethodName, "Request ID: "+sReqID);
								hmFormUpdateData
										.put(hmCUPConstants.get(UD_REQUEST_ID), sReqID);
								if (sReqID != null) {
								/*Get the current status from Target of the request.
								 * If the status is still open do nothing.
								 * */
									String sCUPStatus = sAPProxyUserProvisionManager
											.getStatus(sReqID, htCUPITRattributes,
													hmCUPConstants);
									if (sCUPStatus.equalsIgnoreCase(hmCUPConstants
											.get(REJECT))) {
										hmFormUpdateData.put(
												hmCUPConstants.get(UD_REQUEST_STATUS), hmCUPConstants.get(REJECT));
										sAPProxyUserProvisionManager.auditTrail(
												sReqID, htCUPITRattributes,
												hmCUPConstants);
										logger.info(className, sMethodName,
												"The request has been rejected");
									} else if (sCUPStatus
											.equalsIgnoreCase(hmCUPConstants
													.get(OPEN))
											|| sCUPStatus
													.equalsIgnoreCase(hmCUPConstants
															.get(HOLD))) {
										sAPProxyUserProvisionManager.auditTrail(
												sReqID, htCUPITRattributes,
												hmCUPConstants);
										logger.info(className, sMethodName,
												"The request is still open");
										continue;
									} else if (sCUPStatus
											.equalsIgnoreCase(hmCUPConstants
													.get(CLOSED))) {
										hmFormUpdateData.put(
												hmCUPConstants.get(UD_REQUEST_STATUS), hmCUPConstants.get(CLOSED));
										sAPProxyUserProvisionManager.auditTrail(
												sReqID, htCUPITRattributes,
												hmCUPConstants);
										logger.info(className, sMethodName,
												"The request has been closed");
									}
									fiIntf.setProcessFormData(ProcessInstanceKey,
											hmFormUpdateData);
								
							} else {
								logger.info(sReqID, sMethodName, "No Request ID exists");
								continue;
							}		

						}
						}else{
							logger.debug(sROName, sMethodName, "Not SAP Resource Object");
							continue;
						}
					}
				}
			} else {
				logger.info(sMethodName, sMethodName, "No user exists");
			}
			}
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
		} finally {
			logger.setMethodFinishLog(className, sMethodName);
			logger.info("End of SAP CUP Status Reconciliation");
		}
	}

	/**
	 * Description : This method stops the reconciliation whenever the stop
	 * reconciliation is checked in the Task Scheduler.
	 * 
	 * @return boolean
	 * 
	 */
	/*public boolean stop() {
		isStopRecon = true;
		logger.info("Stopping SAP CUP Status Reconciliation........");
		return isStopRecon;
	}*/

}
