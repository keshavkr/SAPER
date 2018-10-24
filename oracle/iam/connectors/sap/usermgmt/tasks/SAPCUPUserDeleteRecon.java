package oracle.iam.connectors.sap.usermgmt.tasks;

/*
	(MM/DD/YY)    BUG Description
    05/26/2011	  BUG 12586222 - SAP UM RECON LOOKUP WITH NON UNIQUE VALUES
 
 */
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.dao.OIMUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.common.vo.ITResource;
import oracle.iam.connectors.common.vo.ScheduledTask;
import oracle.iam.connectors.sap.common.connection.SAPConnection;
import oracle.iam.connectors.sap.common.connection.SAPResourceImpl;
import oracle.iam.connectors.sap.common.util.SAPUtil;
import oracle.iam.connectors.sap.usermgmt.util.UMConstants;
import Thor.API.tcResultSet;
import Thor.API.Operations.tcFormDefinitionOperationsIntf;
import Thor.API.Operations.tcFormInstanceOperationsIntf;
import Thor.API.Operations.tcITResourceInstanceOperationsIntf;
import Thor.API.Operations.tcLookupOperationsIntf;
import Thor.API.Operations.tcObjectOperationsIntf;
import Thor.API.Operations.tcReconciliationOperationsIntf;
import Thor.API.Operations.tcSchedulerOperationsIntf;
import Thor.API.Operations.tcUserOperationsIntf;

import com.oracle.oim.gcp.exceptions.ConnectionServiceException;
import com.oracle.oim.gcp.pool.ConnectionService;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.thortech.xl.scheduler.tasks.SchedulerBaseTask;

public class SAPCUPUserDeleteRecon  extends SchedulerBaseTask implements UMConstants {
	//private boolean isStopRecon = false;
	private boolean isValid = false;
	private ConnectorLogger logger = new ConnectorLogger("OIMCP.SAPU");
	private String className = this.getClass().getName();
	private JCoDestination jcoConnection;
	private StringUtil stringUtil = new StringUtil();
	private HashMap<String, String> hmConstants;
	private long lITResourceKey;
	private OIMUtil oUtil = null;
	private String sITResourceName;
	private String sObjectName;
	private String sScheduleTaskName;
	private Hashtable<String, String> htTaskAttributes;
	private HashMap<String, String> hmUMConfig;
	private Hashtable<String, String> htITRattributes;
	private ConnectionService ser;
	private SAPResourceImpl rc = null;
	
	/**
	 * Description: Initializes the scheduled task attributes and lookup
	 * definitions. It also checks if all required parameters are correctly set
	 * in the IT resource and scheduled task.
	 */
	public void init() {
		String sMethodName = "init()";
		//isStopRecon = false;
		logger.setMethodStartLog(className, sMethodName);
		logger.info(className, sMethodName,
		"Start of SAP CUP User Management Delete Reconciliation");
		
		try {
			logger.debug(className,sMethodName,"Initialize all OIM API's");
			tcUserOperationsIntf userAPI = (tcUserOperationsIntf) super
					.getUtility(USER_API);
			tcObjectOperationsIntf objAPI = (tcObjectOperationsIntf) super
					.getUtility(OBJECT_API);
			tcFormInstanceOperationsIntf formAPI = (tcFormInstanceOperationsIntf) super
					.getUtility(FORM_API);
			tcFormDefinitionOperationsIntf formDefAPI = (tcFormDefinitionOperationsIntf) super
					.getUtility(FORM_DEF_API);
			tcITResourceInstanceOperationsIntf resAPI = (tcITResourceInstanceOperationsIntf) super
					.getUtility(IT_RESOURCE_API);
			tcSchedulerOperationsIntf schedulerAPI = (tcSchedulerOperationsIntf) super
					.getUtility(SCHED_TASK_API);
			tcLookupOperationsIntf lookIntf = (tcLookupOperationsIntf) super
					.getUtility(LOOKUP_API);
			tcReconciliationOperationsIntf reconOperAPI = (tcReconciliationOperationsIntf) super
					.getUtility(RECON_API);
			logger.debug(className,sMethodName,"innitialize oUtil: contains all the API references");
			oUtil = new OIMUtil(userAPI, objAPI, formAPI, formDefAPI, resAPI,
					schedulerAPI, lookIntf, reconOperAPI, logger);

			if (schedulerAPI == null || reconOperAPI == null
					|| lookIntf == null || userAPI == null || objAPI == null
					|| formAPI == null || formDefAPI == null || resAPI == null) {
				throw new ConnectorException(
						"OIM API's are not getting initialised");
			}
			logger.debug(className,sMethodName,"Get the task scheduler name and validate it");
			sScheduleTaskName = super.getAttribute(SCHEDULE_TASK_NAME);
			if (stringUtil.isEmpty(sScheduleTaskName)) {
				logger.error(className, sMethodName,
								"Task Scheduler Name value is not set in task scheduler");
				throw new ConnectorException(
						"Task Scheduler Name value is not set in task scheduler");
			}

			logger.debug(className,sMethodName,"Get the task scheduler attributes in HashTable");
			ScheduledTask oTaskAttributes = new ScheduledTask(
					sScheduleTaskName, logger);
			htTaskAttributes = oTaskAttributes
					.getScheduledTaskDetails(schedulerAPI);

			logger.debug(className,sMethodName,"Validate if all required task attributes are set properly");
			boolean isMandatoryTaskAttrSet = oTaskAttributes
					.validateMandatoryTaskAttrs(mandatoryCUPUserDeleteReconSchedulerAttrs);
			if (!isMandatoryTaskAttrSet) {
				throw new ConnectorException(
						"Mandatory Task Scheduler values not set");
			}
			sITResourceName = (String) htTaskAttributes.get(IT_RESOURCE_NAME);
			sObjectName = (String) htTaskAttributes.get(RESOURCE_OBJECT);
			logger.info("Task Scheduler Attributes initialised");
			logger.debug(className,sMethodName,"Get the IT Resource attributes in HashTable");
			ITResource oITResource = new ITResource(sITResourceName, resAPI,
					logger);
			htITRattributes = oITResource.getITResourceDetails();
			
			logger.debug(className,sMethodName,"Initialize HashMap for Configuration Lookup and Constant Lookup");
			String sConfigLookup = (String) htITRattributes
					.get(CONFIG_LOOKUP);
			hmUMConfig = oUtil.getLookUpMap(sConfigLookup);
			hmConstants = oUtil.getLookUpMap((String) hmUMConfig
					.get(CONSTANTS_LOOKUP));
			
		// Start :: BUG 12586222 - SAP UM RECON LOOKUP WITH NON UNIQUE VALUES
			//lITResourceKey = oUtil.getITResourceKey(sITResourceName);
			lITResourceKey = Long.parseLong(htITRattributes.get(hmConstants.get(IT_RESOURCE_KEY)));
        // End   :: BUG 12586222 - SAP UM RECON LOOKUP WITH NON UNIQUE VALUES
			
			
			
			logger.debug(className,sMethodName,"Validate if all required IT Resource attributes are set properly");
			boolean isMandatoryITRSet = oITResource
					.validateMandatoryITResource(mandatoryUMITRes);

			if (!isMandatoryITRSet) {
				throw new ConnectorException(
						"Mandatory IT Resource values not set");
			}

			logger.debug(className, sMethodName,
					"Validate if all required SNC related IT Resource attributes are"
							+ "set properly");
			boolean isMandatorySNCITRSet = oITResource
					.validateConditionalMandatory(mandatoryITResSnc, "SNC mode"
							+ ";" + (String) hmConstants.get(YES), ";");

			if (!isMandatorySNCITRSet) {
				throw new ConnectorException(
						"SNC Related Mandatory IT Resource values not set");
			}
			// Validate if all Fixed IT Resource attributes are set properly
			boolean isFixedITRSet = oITResource.validateFixedValues(
					sFixedUMITResFileValues, ",");

			if (!isFixedITRSet) {
				throw new ConnectorException(
						"SNC Related Mandatory IT Resource values not set properly");
			}
			logger.info("IT Resource values initialised");
			// Making isValid to true since IT Resource and Task Attributes are
			// initialized properly
			isValid = true;
			logger.setMethodFinishLog(className, sMethodName);
		} catch (ConnectorException e) {
			isValid = false;
			logger.error(className, sMethodName, e.getMessage());
		} catch (Exception e) {
			isValid = false;
			logger.error(className, sMethodName, e.getMessage());
		}
	}

	/**
	 *Description: Executed from scheduled task after all the values are initialized
	 * 
	 */
	public void execute() {		
		String sMethodName = "execute()";
		isValid=true;
		logger.setMethodStartLog(className, sMethodName);
		boolean isPoolingEnabled = false;
		SAPConnection sAPConnection = null;
		Set<?> Diff = null;
		try {
			if (isValid) {
				String isPool = (String) htITRattributes
				.get(USE_CONNECTION_POOLING);
				logger.info(className, sMethodName, "isPool : " + isPool);
				/*
				 * Check if connection pooling is enabled.If enabled get the
				 * connection from the class which implements
				 * ResourceConnection.Else create a new Connection
				 */
				if ((isPool != null)
						&& (isPool.equalsIgnoreCase(hmConstants.get(YES)) || isPool
								.equalsIgnoreCase(hmConstants.get(TRUE)))) {
					logger.info(className, sMethodName,
									"Connection Pooling has been enabled. Will use Connection Pooling Service");
					isPoolingEnabled = true;
				} else {
				logger.info(className,sMethodName,
									"Connection Pooling has not been enabled. Will not use Connection Pooling Service");
				}
				if (isPoolingEnabled) {
					ser = new ConnectionService();
					try {
						rc = (SAPResourceImpl) ser
								.getConnection(lITResourceKey);
						this.jcoConnection = rc.mConnection;
					} catch (ConnectionServiceException dbe) {
						throw new ConnectorException(
								"Unable to get a connection from connection pool",
								dbe);
					}
				} else {
					HashMap<String, String> hmITRMapping = oUtil
							.getLookUpMap((String) hmUMConfig
									.get(IT_RESOURCE_MAPPING));
					sAPConnection = new SAPConnection(logger);
					jcoConnection = sAPConnection.addDestination(hmITRMapping,
							htITRattributes);
					hmITRMapping.clear();
				}
				htITRattributes.clear();						
				HashMap<String, String>[] hmReconDetailArr = (HashMap<String,String>[]) getAllUsers(jcoConnection);
				oUtil.createTargetDeleteDetectedReconEvent(sObjectName,hmReconDetailArr);
				logger.debug(className, sMethodName, "Finished running createTargetDeleteDetectedReconEvent");
				}	
		}  catch (ConnectorException e) {
			logger.error(className, sMethodName, e.getMessage());
		}catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
		} finally {
			logger.info("End of SAP CUP Delete Reconciliation");
		}
	}
	/**
	 * Description : This method is used to get all the users in the SAP backend
	 * 
	 * @param jcoConnection
	 *            Connection Object
	 * @throws ConnectorException
	 * 
	 */
	private HashMap<String, String>[] getAllUsers(
			JCoDestination jcoConnection2) throws ConnectorException{
		String sMethodName = "getAllUsers()";
		logger.setMethodStartLog(className, sMethodName);
		JCoTable outTable1 = null;
		HashMap<String, String>[] hmReconDetailArr = null;
		try {
		SAPUtil oSAPUtil = new SAPUtil(logger);
		JCoFunction jcoFunction = oSAPUtil.getJCOFunction(
				jcoConnection, hmConstants
						.get(BAPI_USER_GET_LIST));	
		logger.debug(sMethodName, sMethodName, "Executing BAPI");
		jcoFunction.execute(jcoConnection);
		// get the output from the SAP
		outTable1 = jcoFunction.getTableParameterList().getTable(USERLIST);
		int iNoRows = outTable1.getNumRows();
		logger.debug(sMethodName, sMethodName, "Number of rows: " +iNoRows);
		hmReconDetailArr = (HashMap<String,String>[]) new HashMap[iNoRows];
		if(iNoRows>0){
		for(int i = 0;i<iNoRows;i++){
			outTable1.setRow(i);
			HashMap<String,String> hmAllUserDetails=new HashMap<String,String>();
			hmAllUserDetails.put(CUP_USER_ID,outTable1.getValue(USERNAME).toString());
			hmReconDetailArr[i]= hmAllUserDetails;
		}
		}else{
			logger.info(className, sMethodName,"No users present in the target");
		}
		logger.debug(sMethodName, sMethodName, "All target user have been added to Hashmap Array");
		logger.setMethodFinishLog(className, sMethodName);
		} catch (JCoException e) {
			logger.error(className, sMethodName, e.getMessage());
		} catch (ConnectorException e) {
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException(e.getMessage());
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException(e.getMessage());
		}
		return hmReconDetailArr;
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
		logger.info("Stopping SAP CUP Delete Reconciliation........");
		return isStopRecon;
	}*/

	
}
