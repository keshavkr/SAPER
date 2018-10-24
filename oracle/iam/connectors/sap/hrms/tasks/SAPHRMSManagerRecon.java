/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/hrms/tasks/SAPHRMSManagerRecon.java /main/20 2016/07/11 22:08:47 srkale Exp $ */
/* Copyright (c) 2009, 2016, Oracle and/or its affiliates. 
All rights reserved.*/
/*
 DESCRIPTION
 <short description of component this file declares/defines>

 PRIVATE CLASSES
 <list of private classes defined - with one-line descriptions>

 NOTES
 <other useful comments, qualifications, etc.>

 MODIFIED    (MM/DD/YY)
 ddkumar     04/06/09 - This class will be mapped to task scheduler that is
 configured for reconciling unprocessed managers in OIM
 ddkumar     04/06/09 - Creation
 Swaroopa   07/05/13 - BUG 16941269 - OIM SAP EMPLOYEE RECONCILIATION CONNECTOR 9.1.2.6 - MANAGER ID NOT POPULATED
 Chanthosh  07/09/13 - BUG 17047491 - SAP HRMS UPDATE MANAGER SCHEDULED TASK
 Chanthosh	07/23/13 - Bug 17211711 - SCHEDULED TASK: SAP HRMS UPDATE MANAGER NOT WORKING PROPERLY 
 */
/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/hrms/tasks/SAPHRMSManagerRecon.java /main/20 2016/07/11 22:08:47 srkale Exp $
 *  @author  ddkumar
 *  @since   release specific (what release of product did this appear in)
 */
package oracle.iam.connectors.sap.hrms.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.dao.OIMUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.common.vo.ScheduledTask;
import oracle.iam.connectors.sap.common.util.SAPUtil;
import oracle.iam.connectors.sap.hrms.util.HRMSConstants;
import Thor.API.tcResultSet;
import Thor.API.Exceptions.tcAPIException;
import Thor.API.Exceptions.tcColumnNotFoundException;
import Thor.API.Operations.tcFormDefinitionOperationsIntf;
import Thor.API.Operations.tcFormInstanceOperationsIntf;
import Thor.API.Operations.tcITResourceInstanceOperationsIntf;
import Thor.API.Operations.tcLookupOperationsIntf;
import Thor.API.Operations.tcObjectOperationsIntf;
import Thor.API.Operations.tcReconciliationOperationsIntf;
import Thor.API.Operations.tcSchedulerOperationsIntf;
import Thor.API.Operations.tcUserOperationsIntf;

import com.thortech.xl.scheduler.tasks.SchedulerBaseTask;

/**
 * Description: Mapped to the scheduled task that is configured for reconciling unprocessed managers in Oracle Identity Manager 
 * 
 * 
 */
public class SAPHRMSManagerRecon extends SchedulerBaseTask implements
		HRMSConstants {

	private boolean isValid = false;
	private String className = this.getClass().getName();
	private String sObject;
	private String sUpdateEmptyManagerOnly;
	private OIMUtil oUtil = null;
	private SAPUtil oSAPUtil = null;
	private HashMap hmConstants;
	private HashMap hmHRMSConfig;
	private HashMap hmRuleAttr;
	private HashMap hmOrgHierarchy;
	private HashMap hmManagerHierarchy;
	private tcUserOperationsIntf userAPI;
	private StringUtil stringUtil = new StringUtil();
	private ConnectorLogger logger = new ConnectorLogger(HRMS_LOGGER);

	/**
	 * Description: Called each time the scheduled task runs and initializes the scheduled task attributes and lookup definitions. It also validates whether all the required parameters are correctly set in the scheduled task. 
	 */
	public void init() {
		String sMethodName = "init()";
		logger.setMethodStartLog(className, sMethodName);
		logger.info(className, sMethodName,
				"Start of SAP HRMS Update Manager Reconciliation process");
		try {
			// Initialise all OIM API's
			userAPI = (tcUserOperationsIntf) super.getUtility(USER_API);
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
			// oUtil contains all the API references
			oUtil = new OIMUtil(userAPI, objAPI, formAPI, formDefAPI, resAPI,
					schedulerAPI, lookIntf, reconOperAPI, logger);
			oSAPUtil = new SAPUtil(logger);

			if (schedulerAPI == null) {
				throw new ConnectorException("Scheduler API is not getting initialised");
			}
			if (lookIntf == null) {
				throw new ConnectorException(
						"Lookup Interface is not getting initialised");
			}

			// Get the task scheduler name and validate it
			String sScheduleTaskName = super.getAttribute(SCHEDULE_TASK_NAME);
			if (stringUtil.isEmpty(sScheduleTaskName)) {
				throw new ConnectorException(
						"Task Scheduler Name value is not set in task scheduler");
			}

			// Get the task scheduler attributes in Hashtable
			// Validate if all required task attributes are set properly
			ScheduledTask oTaskAttributes = new ScheduledTask(sScheduleTaskName,
					logger);
			Hashtable htTaskAttributes = oTaskAttributes
					.getScheduledTaskDetails(schedulerAPI);
			boolean isMandatoryTaskAttrSet = oTaskAttributes
					.validateMandatoryTaskAttrs(mandatoryManagerAttrs);
			sObject = (String) htTaskAttributes.get(RESOURCE_OBJECT);
			sUpdateEmptyManagerOnly = (String) htTaskAttributes
					.get(UPDATE_EMPTY_MANAGER_ONLY);
			
			if (!isMandatoryTaskAttrSet) {
				throw new ConnectorException("Mandatory Task Scheduler values not set");
			}

			// Initialise HashMap for Configuration Lookup and Constant Lookup
			hmHRMSConfig = oUtil.getLookUpMap((String) htTaskAttributes
					.get(CONFIGURATION_LOOKUP));
			hmConstants = oUtil.getLookUpMap((String) hmHRMSConfig
					.get(CONSTANTS_LOOKUP));
			hmOrgHierarchy = oUtil.getLookUpMap((String) hmHRMSConfig
					.get(ORG_LOOKUP_NAME));
			hmManagerHierarchy = oUtil.getLookUpMap((String) hmHRMSConfig
					.get(MANAGER_LOOKUP_NAME));

			isValid = true;
			logger.setMethodFinishLog(className, sMethodName);
		} catch (ConnectorException e) {
			isValid = false;
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException("Inititialization Exeception :"+className+","+sMethodName+","+ e.getMessage() );
			
		} catch (Exception e) {
			isValid = false;
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException("Initialization Exception :"+className+","+sMethodName+", "+ e.getMessage());
			
		}
	}

	/**
	 * Description: Called each time the scheduled task runs and executes the reconciliation process
	 * 
	 */
	public void execute() {
		String sMethodName = "execute()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			if (isValid) {
				//Start :: BUG 16941269
				// Initialise all required constants
				String sPersonnelNumberColumn = (String) hmConstants
						.get(PERSONNEL_NOS_UDF);
				String sOrgUDFColumn = (String) hmConstants.get(ORGANIZATION_UDF_COL);
				String sManagerIDColumn = (String) hmConstants.get(MANAGER_LOGIN_COL);
				//Start: Bug 17211711
				String sManagerKeyColumn = (String) hmConstants.get(MANAGER_KEY_COL);
				//End: Bug 17211711
				String sUserCreatedFromHRMS = (String) hmConstants
						.get(USER_CREATED_FROM_HRMS_UDF);
				String sPersonnelNumber = (String) hmConstants.get(PERSONNEL_NUMBER);
				String sUserIDColumn = (String) hmConstants.get(USER_ID_OIM);
				String sManagerID = (String) hmConstants.get(MANAGER_ID);
				String sUserId = (String) hmConstants.get(USER_LOGIN_FIELD);

				logger.debug(className, sMethodName, "sOrganizationUDFColumn "
						+ sOrgUDFColumn);
				logger.debug(className, sMethodName, "sManagerIDColumn "
						+ sManagerIDColumn);
				logger.debug(className, sMethodName, "sManagerKeyColumn "
						+ sManagerKeyColumn);
				logger.debug(className, sMethodName, "sUserIDColumn "
			                        + sUserIDColumn);

				logger.debug(className, sMethodName, "sPersonnelNumberColumn "
						+ sPersonnelNumberColumn);
				logger.debug(className, sMethodName, "sManagerID " + sManagerID);

				// Filter users created from HRMS where Manager Login is empty
				HashMap<String, String> hmFilterData = new HashMap<String, String>();
				if (sUpdateEmptyManagerOnly.equalsIgnoreCase(YES)) {
				//Start: Bug 17211711
					hmFilterData.put(sManagerKeyColumn, null);
					logger.debug(className, sMethodName, "sManagerKeyColumn inside filter " + sManagerKeyColumn);
				//End: Bug 17211711
				}
				hmFilterData.put(sUserCreatedFromHRMS, (String) hmConstants.get(ONE));
				logger.debug(className, sMethodName, "sUserCreatedFromHRMS inside filter " + sUserCreatedFromHRMS);
  			        //Start: BUG 17047491 - Matching Rule Bug	
				hmRuleAttr= oUtil.getLookUpMap((String) hmHRMSConfig
						.get(RULE_ATTR_LOOKUP));
				Set keySet = hmRuleAttr.keySet();
				ArrayList<String> sFieldLabel = new ArrayList<String>();
				ArrayList<String> sFieldName = new ArrayList<String>();
				Iterator keySetIterator = keySet.iterator();
				while (keySetIterator.hasNext()) {
					String codeKey = keySetIterator.next().toString();
					sFieldLabel.add(codeKey);
					sFieldName.add(hmRuleAttr.get(codeKey).toString());

				}							
				String [] mandatoryAttr = {sPersonnelNumberColumn,sOrgUDFColumn,sManagerIDColumn,sUserIDColumn};

				for (int i = 0; i < mandatoryAttr.length; i++) {
					if(!hmRuleAttr.containsValue(mandatoryAttr[i])){
						sFieldName.add(mandatoryAttr[i]);
					}
				}					
				String [] pasFieldList  = sFieldName.toArray(new String[sFieldName.size()]);					
				for (int i = 0; i < pasFieldList.length; i++) {
					logger.debug(className, sMethodName, "pasFieldList " + pasFieldList[i]);
				}//End: BUG 17047491 - Matching Rule Bug
				tcResultSet set = userAPI.findUsersFiltered(hmFilterData, pasFieldList);
				hmFilterData.clear();
				int iNoOfRows = set.getRowCount();
				//Start: Bug 17211711
				logger.debug(className, sMethodName, "No of users whose manager id has to be updated: " + iNoOfRows);
				//End: Bug 17211711
				for (int iRow = 0; iRow < iNoOfRows; iRow++) {
					if (!isStopped()) {
						set.goToRow(iRow);
						String sManagerLogin = set.getStringValue(sManagerIDColumn);
						String sSAPPersonnelNumber = set
								.getStringValue(sPersonnelNumberColumn);
						String sUserID = set
					                        .getStringValue(sUserIDColumn);
						logger.debug(className, sMethodName, "sSAPPersonnelNumber "
								+ sSAPPersonnelNumber);
						logger.debug(className, sMethodName, "sUserID "
				                                + sUserID);
						String sManager = oSAPUtil.getManagerIDFromOrg(set
								.getStringValue(sOrgUDFColumn), hmConstants,
								sSAPPersonnelNumber, oUtil, hmOrgHierarchy, hmManagerHierarchy);

						logger.debug(className, sMethodName, "sManager " + sManager);
						// Go inside the loop only if Manager in UDF field
						// and Personnel Number UDF fields in OIM are not
						// empty
						if (!stringUtil.isEmpty(sManager)
								&& !stringUtil.isEmpty(sSAPPersonnelNumber)) {
							hmFilterData.put(sPersonnelNumber, sSAPPersonnelNumber);
							hmFilterData.put(sUserId, sUserID);
							hmFilterData.put(sManagerID, sManager);
							//Start: BUG 17047491 - Matching Rule Bug
							keySetIterator = keySet.iterator();
							while (keySetIterator.hasNext()) {
								String codeKey = keySetIterator.next().toString();
								hmFilterData.put(codeKey, set.getStringValue(hmRuleAttr.get(codeKey).toString()));
							}//End: BUG 17047491 - Matching Rule Bug
							oUtil.createTrustedReconEvent(sObject, hmFilterData,
									sSAPPersonnelNumber);
						}
					}
				}//End :: BUG 16941269
			} else {
				logger.error(className, sMethodName,
						"Please set all the required fields values properly in "
								+ " task scheduler and run reconciliation again");
				throw new ConnectorException(className+", "+sMethodName+" Please set all the required fields values properly in "
						+ " task scheduler/ IT Resource run reconciliation again");
			}
			logger.setMethodFinishLog(className, sMethodName);
		} catch (tcAPIException e) {
			logger.error(className, sMethodName, "InvalidValueException");
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException("APIException : "+className +" : "+ sMethodName+ " :"  + e.getMessage());	
			
		} catch (tcColumnNotFoundException e) {
			logger.error(className, sMethodName, "InvalidValueException");
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException("ColumnNotFoundException : "+className +" : "+ sMethodName+ " :"  + e.getMessage());
			
		} catch (Exception e) {
			logger.error(className, sMethodName, "InvalidValueException");
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e.getMessage());
			
		} finally {
			logger.info("End of SAP HRMS Update Manager Reconciliation process");
		}
	}
}
