/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/usermgmt/tasks/SAPUMUserRecon.java /main/11 2011/06/01 03:24:53 hhaque Exp $ */

/* Copyright (c) 2009, 2011, Oracle and/or its affiliates. 
All rights reserved. */

/*
 DESCRIPTION
 <short description of component this file declares/defines>

 PRIVATE CLASSES
 <list of private classes defined - with one-line descriptions>

 NOTES
 <other useful comments, qualifications, etc.>

 MODIFIED    (MM/DD/YY)
 ddkumar     07/10/09 - This class will be mapped to task scheduler that is
 configured for account reconciliation
 ddkumar     	07/10/09 - Creation
 K S Santosh    01/19/11 - Bug 11070597 - Added logger to print BAPI Name & its parameter
 */

/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/usermgmt/tasks/SAPUMUserRecon.java /main/6 2010/07/08 02:55:19 ddkumar Exp $
 *  @author  ddkumar 
 *  @since   release specific (what release of product did this appear in)
  * S.No. Date      Bug fix no.
  *  1   Bug 10373020 - SAP UM CONNECTOR: ISSUE WITH TARGET RECONCILIATIONS 
 */ 

package oracle.iam.connectors.sap.usermgmt.tasks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.dao.OIMUtil;
import oracle.iam.connectors.common.util.DateUtil;
import oracle.iam.connectors.common.util.NumberUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.common.vo.ITResource;
import oracle.iam.connectors.common.vo.ScheduledTask;
import oracle.iam.connectors.sap.common.connection.SAPConnection;
import oracle.iam.connectors.sap.common.connection.SAPResourceImpl;
import oracle.iam.connectors.sap.common.util.SAPUtil;
import oracle.iam.connectors.sap.usermgmt.util.SAPUMAttributeMapBean;
import oracle.iam.connectors.sap.usermgmt.util.UMConstants;
import oracle.iam.connectors.sap.usermgmt.util.UMUtility;
import Thor.API.Exceptions.tcAPIException;
import Thor.API.Exceptions.tcObjectNotFoundException;
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
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.thortech.xl.scheduler.tasks.SchedulerBaseTask;

/**
 * Description: Mapped to the scheduled task for user reconciliation
 * 
 */
/*
 *	<br>Modification History:</br>
 *  S.No.                 Date                              Bug fix no.
 *  1.                  03 March 2011                     10627537 - SAP UM INCREMENTAL RECONCILIATION 
 */
public class SAPUMUserRecon extends SchedulerBaseTask implements UMConstants {
	//private boolean isStopRecon = false;
	private boolean isValid = false;
	private boolean isUM = false;
	private String sITResourceName;
	private String sObjectName;
	private String sSubsystem;
	private String sScheduleTaskName;
	private String sExecutionTime;
	private String sQuery;
	private String sITResourceKey;
	private String className = this.getClass().getName();
	private Hashtable<String, String> htITRattributes;
	private Hashtable<String, String> htTaskAttributes;
	private HashMap<String, String> hmConstants;
	private HashMap<String, String> hmUMConfig;
	private HashMap<String, String> hmCheckBoxMapping;
	private HashMap<String, ArrayList> hmCustomAttrMap;
	private HashMap<String, ArrayList> hmCustomChildAttrMap;
	private JCoDestination jcoConnection;
	private ConnectionService ser;
	private SAPResourceImpl rc = null;
	private OIMUtil oUtil = null;
	private StringUtil stringUtil = new StringUtil();
	private ConnectorLogger logger = new ConnectorLogger(UM_LOGGER);

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
				"Start of SAP User Management Reconciliation");

		try {
			// Initialize all OIM API's
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
			// oUtil contains all the API references
			oUtil = new OIMUtil(userAPI, objAPI, formAPI, formDefAPI, resAPI,
					schedulerAPI, lookIntf, reconOperAPI, logger);

			if (schedulerAPI == null || reconOperAPI == null
					|| lookIntf == null || userAPI == null || objAPI == null
					|| formAPI == null || formDefAPI == null || resAPI == null) {
				throw new ConnectorException(
						"OIM API's are not getting initialised");
			}

			// Get the task scheduler name and validate it
			sScheduleTaskName = super.getAttribute(SCHEDULE_TASK_NAME);
			if (stringUtil.isEmpty(sScheduleTaskName)) {
				logger
						.error(className, sMethodName,
								"Task Scheduler Name value is not set in task scheduler");
				throw new ConnectorException(
						"Task Scheduler Name value is not set in task scheduler");
			}

			// Get the task scheduler attributes in HashTable
			ScheduledTask oTaskAttributes = new ScheduledTask(
					sScheduleTaskName, logger);
			htTaskAttributes = oTaskAttributes
					.getScheduledTaskDetails(schedulerAPI);
			logger.debug(className, sMethodName, "Task Scheduler Attributes  "
					+ htTaskAttributes);

			// Validate if all required task attributes are set properly
			boolean isMandatoryTaskAttrSet = oTaskAttributes
					.validateMandatoryTaskAttrs(mandatoryUserReconSchedulerAttrs);
			if (!isMandatoryTaskAttrSet) {
				throw new ConnectorException(
						"Mandatory Task Scheduler values not set");
			}
			NumberUtil oNUtil = new NumberUtil();

			sITResourceName = (String) htTaskAttributes.get(IT_RESOURCE_NAME);
			sObjectName = (String) htTaskAttributes.get(RESOURCE_OBJECT);
			sExecutionTime = (String) htTaskAttributes.get(EXECUTION_TIMESTAMP);
			logger.info("Task Scheduler Attributes initialised");
			String sBatchSize = (String) htTaskAttributes.get(BATCH_SIZE);
			boolean isBatchSize = oNUtil.isNumeric(sBatchSize);
			if (!isBatchSize) {
				throw new ConnectorException(
						"Batch Size should be positive number");
			}

			// Get the IT Resource attributes in HashTable
			ITResource oITResource = new ITResource(sITResourceName, resAPI,
					logger);
			htITRattributes = oITResource.getITResourceDetails();

			// Initialize HashMap for Configuration Lookup and Constant Lookup
			String sConfigLookup = (String) htITRattributes.get(CONFIG_LOOKUP);
			hmUMConfig = oUtil.getLookUpMap(sConfigLookup);
			String sUM = (String) hmUMConfig.get(CUA_ENABLED);
			if (sUM.equalsIgnoreCase(NO)) {
				isUM = true;
				sSubsystem = (String) htITRattributes.get(SYSTEM_NAME);
			}
			logger.debug(className, sMethodName, "is CUA Enabled " + isUM);
			hmConstants = oUtil.getLookUpMap((String) hmUMConfig
					.get(CONSTANTS_LOOKUP));
			hmCheckBoxMapping = oUtil.getLookUpMap((String) hmUMConfig
					.get(CHECK_BOX_LOOKUP));
			long lITResourceKey = oUtil.getITResourceKey(sITResourceName);
			sITResourceKey = lITResourceKey + "";

			// Validate if all required IT Resource attributes are set properly
			boolean isMandatoryITRSet = oITResource
					.validateMandatoryITResource(mandatoryUMITRes);

			if (!isMandatoryITRSet) {
				throw new ConnectorException(
						"Mandatory IT Resource values not set");
			}

			// Validate if all required SNC related IT Resource attributes are
			// set properly
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
						"SNC Related Mandatory IT Resource values not set");
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
	 * Description: Starts the reconciliation process
	 * 
	 */
	public void execute() {
		String sMethodName = "execute()";
		logger.setMethodStartLog(className, sMethodName);
		SAPConnection sAPConnection = null;
		boolean isPoolingEnabled = false;
		try {
			if (isValid) {
				SAPUtil oSAPUtil = new SAPUtil(logger);
				StringUtil oStringUtil = new StringUtil();
				DateUtil oDateutil = new DateUtil(logger);
				Date date = new Date();
				/*
				 * Get the recon date and time to be updated in task scheduler
				 */
				String sReconTime = oDateutil.parseTime(date,
						(String) hmConstants.get(DATE_FORMAT), htTaskAttributes
								.get(TIMEZONEFORMAT));
				/*
				 * Populate HashMap for both parent and child data
				 */
				String sAttributeMapingLookup = (String) htTaskAttributes
						.get(ATTRIBUTE_MAPPING_LOOKUP);
				String sChildAttributeMapingLookup = (String) htTaskAttributes
						.get(CHILD_ATTRIBUTE_MAPPING_LOOKUP);
				String sCustomAttributeMappingLookup = (String) hmUMConfig
						.get(CUSTOM_ATTRIBUTE_MAPPING_LOOKUP);
				String sCustomChildAttributeMappingLookup = (String) hmUMConfig
				.get(CUSTOM_CHILD_ATTRIBUTE_MAPPING_LOOKUP);
				HashMap hmAttrMap = oSAPUtil.initializeTargetReconAttrMap(
						sAttributeMapingLookup, oUtil.getLookIntf(), isUM);
				HashMap hmChildAttrMap = oSAPUtil.initializeTargetReconAttrMap(
						sChildAttributeMapingLookup, oUtil.getLookIntf(), isUM);
				hmCustomAttrMap = oSAPUtil.initializeCustomAttrMap(
						sCustomAttributeMappingLookup, oUtil.getLookIntf());
				hmCustomChildAttrMap = oSAPUtil.initializeCustomChildAttrMap(
						sCustomChildAttributeMappingLookup, oUtil.getLookIntf());
				sQuery = (String) htTaskAttributes.get(CUSTOM_QUERY);
				if (!oStringUtil.isEmpty(sQuery)) {
					HashMap<String, String> hmQueryMap = oUtil
							.getLookUpMap(sAttributeMapingLookup);
					HashMap<String, String> hmQueryCustomAttrMap = oUtil
							.getLookUpMap(sCustomAttributeMappingLookup);
					if (hmQueryCustomAttrMap.size() > 0) {
						hmQueryMap.putAll(hmQueryCustomAttrMap);
					}
					oSAPUtil.validateQuery(sQuery, hmQueryMap);
					hmQueryMap.clear();
				}

				HashMap<String, String> hmExclusionList = oUtil
						.getLookUpMap((String) hmUMConfig.get(EXCLUSION_LIST));

				/*
				 * Check if connection pooling is enabled.If enabled get the
				 * connection from the class which implements
				 * ResourceConnection.Else create a new Connection
				 */
				String isPool = (String) htITRattributes
						.get(USE_CONNECTION_POOLING);
				logger.info(className, sMethodName, "isPool : " + isPool);

				if ((isPool != null)
						&& (isPool.equalsIgnoreCase((String) hmConstants
								.get(YES)) || isPool
								.equalsIgnoreCase((String) hmConstants
										.get(TRUE)))) {
					logger
							.info(className, sMethodName,
									"Connection Pooling has been enabled. Will use Connection Pooling Service");
					isPoolingEnabled = true;
				} else {
					logger
							.info(
									className,
									sMethodName,
									"Connection Pooling has not been enabled. Will not use Connection Pooling Service");
				}

				if (isPoolingEnabled) {
					ser = new ConnectionService();
					long itResourceKey;
					try {
						itResourceKey = Long.parseLong(sITResourceKey);
					} catch (NumberFormatException nfe) {
						throw new ConnectorException("Invalid IT Resource key");
					}

					try {
						rc = (SAPResourceImpl) ser.getConnection(itResourceKey);
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
				/*
				 * Call getFirstTimeReconEvents() if last execution timestamp is
				 * 0 in task scheduler. Call getIncrementalReconEventsforUM()
				 * for incremental recon events in case of UM target system Call
				 * getIncrementalReconEventsforCUA() for incremental recon
				 * events in case of CUA target system
				 */
				if (htTaskAttributes.get(EXECUTION_TIMESTAMP).equalsIgnoreCase(
						(String) hmConstants.get(ZERO))) {
					getFirstTimeReconEvents(hmAttrMap, hmChildAttrMap,
							hmExclusionList);
				} else {
					getIncrementalReconEvents(hmAttrMap, hmChildAttrMap,
							hmExclusionList);
				}
				/*
				 * Update the timestamp in task scheduler if recon run is
				 * successful
				 */
				oUtil.updateTaskAttribute(sScheduleTaskName,
						EXECUTION_TIMESTAMP, sReconTime);

			} else {
				logger
						.error(
								className,
								sMethodName,
								"Please set all the required fields values properly in "
										+ " task scheduler/ IT Resource and run reconciliation again");
			}

			logger.setMethodFinishLog(className, sMethodName);
		} catch (ConnectorException e) {
			logger.error(className, sMethodName, e.getMessage());
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
		} finally {
			try {
				if (isPoolingEnabled) {
					ser.releaseConnection(rc);
				} else {
					if (jcoConnection != null) {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}
			} catch (Exception e) {
				logger.error(className, sMethodName, e.getMessage());
			}
			logger.info(className, sMethodName,
					"End of SAP User Management Reconciliation");
		}
	}

	/**
	 * Description: Stops the execution of the reconciliation process by setting
	 * the Boolean flag to true
	 * 
	 * @return boolean
	 * 		   Returns true if the scheduled task is manually stopped during a
	 *         reconciliation run
	 * 
	 */
	/*public boolean stop() {
		isStopRecon = true;
		logger.info("Stopping the reconciliation process forcefully......");
		return isStopRecon;
	}*/

	/**
	 * Description : This method is used to reconcile the first time recon
	 * events
	 * 
	 * @param hmAttrMap
	 *            HashMap containing list of parent attributes that needs to be
	 *            reconciled
	 * @param hmChildAttrMap
	 *            HashMap containing list of child attributes that needs to be
	 *            reconciled
	 * @param hmExclusionList
	 *            HashMap containing list of accounts for which account recon
	 *            must not be done
	 * 
	 */
	private void getFirstTimeReconEvents(HashMap hmAttrMap,
			HashMap hmChildAttrMap, HashMap<String, String> hmExclusionList) {
		String sMethodName = "getFirstTimeReconEvents()";
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		logger.setMethodStartLog(className, sMethodName);
		try {
			/*
			 * Get all the accounts based on the batch size value in HashMap by
			 * querying the USR02 table. Loop through the HashMap and get each
			 * Account ID and call getDetails to get each account and
			 * entitlement information
			 */
			boolean isMoreRecordsFound = true;
			int iStartRecord = 0;
			String sBatchSize = (String) htTaskAttributes.get(BATCH_SIZE);
			int iBatchSize = Integer.parseInt(sBatchSize);
			while (isMoreRecordsFound && !isStopped()) {
				HashMap<String, String> hmAccounts = oUMUtil.getAccounts(
						(String) hmConstants.get(USR02_TABLE), hmExclusionList,
						true, iStartRecord, iBatchSize, sExecutionTime,
						jcoConnection, true);
				if (hmAccounts.size() > 0) {
					logger.debug(className, sMethodName,
							"Account ID's to be reconciled  " + hmAccounts);
					Iterator iterator = hmAccounts.keySet().iterator();
					while (iterator.hasNext() && !isStopped()) {
						String sUserID = (String) iterator.next();
						String sLockStatus = (String) hmAccounts.get(sUserID);
						HashMap<String, HashMap> hmDetailsMap = getDetails(
								sUserID, sLockStatus, hmAttrMap, hmChildAttrMap);
						if (hmDetailsMap.size() > 0)
							reconcileUser(hmDetailsMap);
					}
					iStartRecord += iBatchSize;
					if (iBatchSize == 0) {
						isMoreRecordsFound = false;
					}
				} else {
					isMoreRecordsFound = false;
				}
			}
		} catch (ConnectorException e) {
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException();
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException();
		}
		logger.setMethodFinishLog(className, sMethodName);
	}

	/**
	 * Description : This method is used to reconcile the modified or newly
	 * created CUA target accounts after the running the full recon for creating
	 * recon events
	 * 
	 * @param hmAttrMap
	 *            HashMap containing list of parent attributes that needs to be
	 *            reconciled
	 * @param hmChildAttrMap
	 *            HashMap containing list of child attributes that needs to be
	 *            reconciled
	 * @param hmExclusionList
	 *            HashMap containing list of accounts for which account
	 *            reconciliation must not be done
	 * 
	 */
	private void getIncrementalReconEvents(HashMap hmAttrMap,
			HashMap hmChildAttrMap, HashMap<String, String> hmExclusionList) {
		String sMethodName = "getIncrementalReconEvents()";
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		logger.setMethodStartLog(className, sMethodName);
		try {
			/*
			 * Get all the accounts based on batch size value specified in
			 * HashMap by querying the USZBVSYS/USR04 table. Loop through the
			 * HashMap and get each Account ID and call getDetails to get each
			 * account and entitlement information
			 */
			boolean isMoreRecordsFound = true;
			int iStartRecord = 0;
			String sBatchSize = (String) htTaskAttributes.get(BATCH_SIZE);
			int iBatchSize = Integer.parseInt(sBatchSize);
			HashMap<String, String> hmAccounts = new HashMap<String, String>();
			
			//Start :: BUG 10627537 
			HashMap hmAccountsFromTable = oUMUtil.getAccounts(
					(String) hmConstants.get(USH02_TABLE), hmExclusionList,
					false, 0, 0, sExecutionTime, jcoConnection, true);

			//End :: BUG 10627537 
			
			while (isMoreRecordsFound && !isStopped()) {
				if (isUM) {
					hmAccounts = oUMUtil.getAccounts((String) hmConstants
							.get(USR04_TABLE), hmExclusionList, false,
							iStartRecord, iBatchSize, sExecutionTime,
							jcoConnection, true);
				} else {
					hmAccounts = oUMUtil.getAccounts((String) hmConstants
							.get(USZBVSYS_TABLE), hmExclusionList, false,
							iStartRecord, iBatchSize, sExecutionTime,
							jcoConnection, false);
				}
				if (hmAccounts.size() > 0) {
					logger.debug(className, sMethodName,
							"Account ID's to be reconciled  " + hmAccounts);
					Iterator iterator = hmAccounts.keySet().iterator();
					while (iterator.hasNext()) {
						String sUserID = (String) iterator.next();
						String sLockStatus = (String) hmAccounts.get(sUserID);
						
						//Start :: BUG 10627537 
						if(hmAccountsFromTable.get(sUserID) != null)
						{
							sLockStatus = (String) hmAccountsFromTable.get(sUserID);
						}
						//End :: BUG 10627537 
						
						HashMap<String, HashMap> hmDetailsMap = getDetails(
								sUserID, sLockStatus, hmAttrMap, hmChildAttrMap);
						if (hmDetailsMap.size() > 0)
							reconcileUser(hmDetailsMap);
					}
					iStartRecord += iBatchSize;
					if (iBatchSize == 0) {
						isMoreRecordsFound = false;
					}
				} else {
					isMoreRecordsFound = false;
				}
			}

			/*
			 * Get all the accounts lock and unlock status in HashMap by
			 * querying the USH02 table. Loop through the HashMap and get each
			 * Account ID and create recon event for updating the lock/unlock
			 * status
			 */
			
			//Start :: commented for BUG 10627537 
			/*HashMap hmAccountsFromTable = oUMUtil.getAccounts(
					(String) hmConstants.get(USH02_TABLE), hmExclusionList,
					false, 0, 0, sExecutionTime, jcoConnection, true);
			if (hmAccountsFromTable.size() > 0 && !isStopped())
				reconcileAccountLock(hmAccountsFromTable);*/
			//End :: commented for BUG 10627537 
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException();
		}
		logger.setMethodFinishLog(className, sMethodName);
	}

	/**
	 * Description : This method is used to get the account and entitlement
	 * information for the User ID passed
	 * 
	 * @param userID
	 *            User ID for which we need the account details
	 * @param lockStatus
	 *            Lock status of the user
	 * @param hmAttrMap
	 *            HashMap containing list of parent attributes that needs to be
	 *            reconciled
	 * @param hmChildAttrMap
	 *            HashMap containing list of child attributes that needs to be
	 *            reconciled
	 * @return HashMap HashMap containing list of account and entitlement
	 *         details
	 * 
	 */
	private HashMap<String, HashMap> getDetails(String userID,
			String lockStatus, HashMap hmAttrMap, HashMap hmChildAttrMap) {
		HashMap<String, HashMap> hmUserDetails = new HashMap<String, HashMap>();
		String sMethodName = "getDetails()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			Date dtValidThro = null;
			Date dtToday = new Date();
			SAPUtil oSAPUtil = new SAPUtil(logger);
			UMUtility oUMUtil = new UMUtility(logger, hmConstants);
			StringUtil oStringUtil = new StringUtil();
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					(String) hmConstants.get(BAPI_USER_GET_DETAIL));
			HashMap<String, Object> hmUser = new HashMap<String, Object>();
			JCoParameterList input = jcoFunction.getImportParameterList();
			input.setValue((String) hmConstants.get(USERNAME), userID);
			jcoFunction.execute(jcoConnection);
			boolean isUserExists = oUMUtil.findUser(jcoFunction);
			if (isUserExists) {
				hmUser.put((String) hmConstants.get(USER_ID), userID);
				hmUser.put((String) hmConstants.get(IT_RESOURCE),
						sITResourceName);

				Set keySet = hmAttrMap.keySet();
				if (keySet != null) {
					Iterator keySetIterator = keySet.iterator();
					JCoStructure sapStructure = null;
					while (keySetIterator.hasNext()) {
						String sStructure = (String) keySetIterator.next();
						if (!sStructure.equalsIgnoreCase(UCLASSSYS)) {
							sapStructure = jcoFunction.getExportParameterList()
									.getStructure(sStructure);
						}
						ArrayList attrMapList = (ArrayList) hmAttrMap
								.get(sStructure);
						Object fieldValue;
						for (int index = 0; index < attrMapList.size(); index++) {
							SAPUMAttributeMapBean oAttrMapBean = (SAPUMAttributeMapBean) attrMapList
									.get(index);
							if (sStructure.equalsIgnoreCase(UCLASSSYS)) {
								JCoTable multiValuesTable = jcoFunction
										.getTableParameterList().getTable(
												sStructure);
								if (multiValuesTable.getNumRows() > 0) {
									multiValuesTable.setRow(0);
									fieldValue = multiValuesTable
											.getValue(oAttrMapBean
													.getBapiFieldName());
								} else {
									fieldValue = "";
								}

							} else {
								fieldValue = (sapStructure
										.getValue(oAttrMapBean
												.getBapiFieldName()));
							}
							if (oAttrMapBean.getFieldType().equalsIgnoreCase(
									(String) hmConstants
											.get(FIELDTYPE_CHECKBOX))) {
								fieldValue = oUMUtil.getCheckBoxValueForOIM(
										oAttrMapBean.getOIMfieldName(),
										fieldValue.toString(),
										hmCheckBoxMapping);

							} else if (oAttrMapBean.getFieldType()
									.equalsIgnoreCase(
											(String) hmConstants
													.get(FIELDTYPE_DATE))) {
								if (fieldValue != null) {
									if (oAttrMapBean.getBapiFieldName().equals(
											GLTGB)) {
										dtValidThro = (Date) fieldValue;
									}
									DateFormat sdf = new SimpleDateFormat(oUtil
											.getReconOperAPI()
											.getDefaultDateFormat());
									fieldValue = sdf.format(fieldValue);
								}
							} else if (oAttrMapBean.getFieldType()
									.equalsIgnoreCase(
											(String) hmConstants
													.get(FIELDTYPE_LOOKUP))) {
								fieldValue = (String) fieldValue;
								if (!oStringUtil.isEmpty((String) fieldValue)) {
									fieldValue = sITResourceKey + '~'
											+ fieldValue;
								}
							} else {
								if (fieldValue != null)
									fieldValue = fieldValue.toString();
							}
							if (fieldValue == null) {
								fieldValue = "";
							}
							hmUser.put(oAttrMapBean.getOIMfieldName(),
									fieldValue);
						}
					}

					// Lock/Unlock
					if (!lockStatus.equalsIgnoreCase((String) hmConstants
							.get(NONE))) {
						if (lockStatus.equalsIgnoreCase((String) hmConstants
								.get(ZERO))) {
							hmUser.put((String) hmConstants.get(LOCK),
									(String) hmConstants.get(ZERO));
						} else {
							hmUser.put((String) hmConstants.get(LOCK),
									(String) hmConstants.get(ONE));
						}
					}
					/*
					 * If valid Through is < current date, then user should be
					 * disabled
					 */
					if ((dtValidThro != null) && dtValidThro.before(dtToday)) {
						hmUser.put((String) hmConstants.get(STATUS),
								(String) hmConstants.get(DISABLED_STATUS));
					} else {
						hmUser.put((String) hmConstants.get(STATUS),
								(String) hmConstants.get(ENABLED_STATUS));
					}
					if (hmCustomAttrMap.size() > 0) {
						Set keyCustomAttrSet = hmCustomAttrMap.keySet();
						if (keyCustomAttrSet != null) {
							Iterator keyCustomAttrSetIterator = keyCustomAttrSet
									.iterator();
							while (keyCustomAttrSetIterator.hasNext()) {
								String sBAPIName = (String) keyCustomAttrSetIterator
										.next();
								ArrayList attrMapList = (ArrayList) hmCustomAttrMap
										.get(sBAPIName);
								if (sBAPIName.equalsIgnoreCase(RFC_READ_TABLE)) {
									HashMap hmCustomRFC = getCustomAttributesRFC(
											sBAPIName, userID, attrMapList);
									hmUser.putAll(hmCustomRFC);
								} else {
									HashMap hmCustomBAPI = getCustomAttributesBAPI(
											sBAPIName, userID, attrMapList);
									hmUser.putAll(hmCustomBAPI);
								}
							}
						}
					}
					hmUserDetails.put("USER", hmUser);
				}

				/*
				 * Get all the multi valued attributes in HashMap
				 */
				HashMap hmMap = setMultiValueData(hmChildAttrMap, jcoFunction,
						userID);
				hmUserDetails.putAll(hmMap);
				if (hmCustomChildAttrMap.size() > 0) {
					HashMap hmData = new HashMap();
					Set keyCustomChildAttrSet = hmCustomChildAttrMap.keySet();
					if (keyCustomChildAttrSet != null) {
						Iterator keyCustomAttrSetIterator = keyCustomChildAttrSet
								.iterator();
						while (keyCustomAttrSetIterator.hasNext()) {
							String sTableName = (String) keyCustomAttrSetIterator
									.next();
							ArrayList attrMapList = (ArrayList) hmCustomChildAttrMap
									.get(sTableName);
							SAPUMAttributeMapBean attrmapBean = (SAPUMAttributeMapBean) attrMapList
									.get(0);
							String sChildTableName = attrmapBean
									.getChildTableName();
							ArrayList alValues = getCustomChildAttributes(
									sTableName, userID, attrMapList);
							hmData.put(sChildTableName, alValues);
						}
					}
					hmUserDetails.putAll(hmData);
				}
				logger.debug(className, sMethodName,
						"Account's details are :-  " + hmUserDetails);
			}
			logger.setMethodFinishLog(className, sMethodName);
		} catch (JCoException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e.getMessage());
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e.getMessage());
		}
		return hmUserDetails;
	}

	/**
	 * Description : This method is used to reconcile the account and
	 * entitlement information by creating recon events
	 * 
	 * @param hmReconDetails
	 *            HashMap containing account and entitlement information
	 * 
	 * 
	 */
	private void reconcileUser(HashMap hmReconDetails) {
		String sMethodName = "reconcileUser()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			UMUtility oUMUtil = new UMUtility(logger, hmConstants);
			boolean bTargetReconFlag = false;
			boolean bMultivalueFlag = false;
			HashMap hmAccountDetails = (HashMap) hmReconDetails.get("USER");
			hmReconDetails.remove("USER");
			String sUserID = (String) hmAccountDetails.get((String) hmConstants
					.get(USER_ID));
			/*
			 * If transform mapping is required,then call
			 * transformSingleOrMultivaluedData() to transform the data
			 */
			if (hmConstants.get(YES).equalsIgnoreCase(
					(String) hmUMConfig.get(USE_TRANSFORM_MAPPING))) {
				hmAccountDetails = oUtil.transformSingleOrMultivaluedData(
						hmAccountDetails, hmReconDetails, (String) hmUMConfig
								.get(TRANSFORM_LOOKUP));
			}
			/*
			 * If validation is required,then call
			 * validateSingleOrMultivaluedData() to transform the data
			 */
			if (hmConstants.get(YES).equalsIgnoreCase(
					(String) hmUMConfig.get(USE_RECON_VALIDATION))) {
				isValid = oUtil.validateSingleOrMultivaluedData(
						hmAccountDetails, hmReconDetails, (String) hmUMConfig
								.get(VALIDATE_LOOKUP));
			}
			if (isValid && !stringUtil.isEmpty(sQuery)) {
				isValid = oUMUtil.executeCustomQuery(hmAccountDetails, sQuery);
			}
			/*
			 * Check if account data being reconciled is same as that in OIM.In
			 * such case ignore event will prevent the account from being
			 * reconciled
			 */
			if (isValid) {
				try {
					if (!oUtil.getReconOperAPI().ignoreEvent(sObjectName,
							hmAccountDetails)) {
						bTargetReconFlag = true;
					}
				} catch (tcAPIException e) {
					isValid = false;
					logger.debug(className, sMethodName, "Error");
				} catch (tcObjectNotFoundException e) {
					isValid = false;
					logger.error(className, sMethodName, e.getMessage());
					logger.setStackTrace(e, className, sMethodName, e
							.getMessage());
					throw new ConnectorException(e);
				} catch (Exception e) {
					isValid = false;
				}
				if (isValid && !bTargetReconFlag) {
					Iterator iterator = hmReconDetails.keySet().iterator();
					while (iterator.hasNext()) {
						String sChildtablename = (String) iterator.next();
						ArrayList alEntitlementList = (ArrayList) hmReconDetails
								.get(sChildtablename);
						int iSize = alEntitlementList.size();
						HashMap[] hmChildData = new HashMap[iSize];
						for (int i = 0; i < iSize; i++) {
							HashMap hmList = (HashMap) alEntitlementList.get(i);
							hmChildData[i] = hmList;
						}

						try {
							if (!oUtil.getReconOperAPI()
									.ignoreEventAttributeData(sObjectName,
											hmAccountDetails, sChildtablename,
											hmChildData)) {
								bMultivalueFlag = true;
							}
						} catch (tcAPIException e) {
							isValid = false;
							logger
									.error(className, sMethodName, e
											.getMessage());
						} catch (tcObjectNotFoundException e) {
							isValid = false;
							logger
									.error(className, sMethodName, e
											.getMessage());
							throw new ConnectorException(e);
						}
					}
				}
				if ((bTargetReconFlag || bMultivalueFlag)) {
					if (!isStopped()&& isValid)
						oUtil.createTargetReconEvent(sObjectName,
								hmAccountDetails, hmReconDetails, sUserID);
				} else {
					logger
							.info("No change in data being reconciled and that present in OIM. "
									+ " Hence not creating Recon Event for user with User Login::"
									+ sUserID);
				}
			} else {
				logger
						.info("Not creating Recon Event for user with User Login::"
								+ sUserID);
			}
			logger.setMethodFinishLog(className, sMethodName);
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} finally {
			hmReconDetails.clear();
			isValid = true;
		}
	}

	/**
	 * Description: Used to get multi-valued attribute data from SAP target
	 * system for the attributes passed in the child attribute map lookup
	 * 
	 * @param hmChildAttrMap
	 *          HashMap populated from child attribute map lookup containing
	 *          the child form fields to be reconciled
	 * @param jcoFunction
	 *          Holds the JCoFunction reference
	 * @param sUserID
	 *          User ID for which the entitlement details are required
	 * @return HashMap 
	 * 			HashMap containing the entitlement information to be
	 *         reconciled
	 * @throws ConnectorException
	 * 
	 */
	public HashMap setMultiValueData(HashMap hmChildAttrMap,
			JCoFunction jcoFunction, String sUserID) throws ConnectorException {
		HashMap hmData = new HashMap();
		String sMethodName = "setMultiValueData()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			Object fieldValue;
			Set keykeyChildSet = hmChildAttrMap.keySet();
			if (keykeyChildSet != null) {
				Iterator keyChildSetIterator = keykeyChildSet.iterator();
				while (keyChildSetIterator.hasNext()) {
					ArrayList alMultiValues = new ArrayList();
					String sStructure = (String) keyChildSetIterator.next();
					JCoTable multiValuesTable = null;
					if (!isUM
							&& (sStructure.equalsIgnoreCase(ACTIVITYGROUPS) || sStructure
									.equalsIgnoreCase(PROFILES))) {
						UMUtility oUMUtil = new UMUtility(logger, hmConstants);
						multiValuesTable = oUMUtil.getRoleorProfile(sUserID,
								sStructure, jcoConnection, hmConstants);
					} else {
						multiValuesTable = jcoFunction.getTableParameterList()
								.getTable(sStructure);
					}
					ArrayList attrMapList = (ArrayList) hmChildAttrMap
							.get(sStructure);
					SAPUMAttributeMapBean attrmapBean = (SAPUMAttributeMapBean) attrMapList
							.get(0);
					String sChildTableName = attrmapBean.getChildTableName();
					for (int index = 0; index < multiValuesTable.getNumRows(); index++) {
						multiValuesTable.setRow(index);
						HashMap hmChildData = new HashMap();
						for (int index1 = 0; index1 < attrMapList.size(); index1++) {
							attrmapBean = (SAPUMAttributeMapBean) attrMapList
									.get(index1);
							if (attrmapBean.getBapiFieldName() != null) {
								if (isUM
										&& attrmapBean
												.getBapiFieldName()
												.equalsIgnoreCase(
														(hmConstants)
																.get(SUBSYSTEM))) {
									fieldValue = sSubsystem;
								} else {
									fieldValue = multiValuesTable
											.getValue(attrmapBean
													.getBapiFieldName());
								}
								// If attribute is type date, format as in
								// System Configuration
								if (attrmapBean
										.getFieldType()
										.equalsIgnoreCase(
												hmConstants.get(FIELDTYPE_DATE))) {
									// Get the date format defined in System
									// Configuration
									DateFormat sdf = new SimpleDateFormat(oUtil
											.getReconOperAPI()
											.getDefaultDateFormat());
									fieldValue = sdf.format(fieldValue);
								} else if (attrmapBean.getFieldType()
										.equalsIgnoreCase(
												hmConstants
														.get(FIELDTYPE_LOOKUP))) {
									fieldValue = (String) fieldValue;
									if (attrmapBean.getBapiFieldName()
											.equalsIgnoreCase(
													hmConstants.get(AGR_NAME))
											|| attrmapBean
													.getBapiFieldName()
													.equalsIgnoreCase(
															hmConstants
																	.get(PROFILE))
											|| attrmapBean.getBapiFieldName()
													.equalsIgnoreCase(BAPIPROF)) {
										if (!isUM) {
											sSubsystem = (String) multiValuesTable
													.getValue((hmConstants)
															.get(SUBSYSTEM));
										}
										fieldValue = sITResourceKey + '~'
												+ sSubsystem + '~' + fieldValue;
									} else {
										fieldValue = sITResourceKey + '~'
												+ fieldValue;
									}
								}
								fieldValue = (String) fieldValue;
								hmChildData.put(attrmapBean.getOIMfieldName(),
										fieldValue);
							}
						}
						
						/*
						 * Start Code Modified for Role 10373020
						 */
						
						Boolean isValid = true;
						if(sStructure.equalsIgnoreCase(ACTIVITYGROUPS))
						{
							isValid = isValidRole(hmChildData);
						}
						if(isValid)
						{
							alMultiValues.add(hmChildData);
						}
						
						/*
						 * End Code Modified for Role 10373020
						 */
					}
					hmData.put(sChildTableName, alMultiValues);
				}
			}
		} catch (Exception exception) {
			throw new ConnectorException(exception.getMessage());
		}
		logger.setMethodFinishLog(className, sMethodName);
		return hmData;
	}

	/**
	 * Description : This method is used to get the attributes in SAP executed
	 * through custom RFC table
	 * 
	 * @param sRFCName
	 *            Name of the RFC used
	 * @param sUserID
	 *            User ID being reconciled for which we need the attributes
	 * @param attrMapList
	 *            ArrayList containing the set of attributes being reconciled.It
	 *            contains bean having OIM Field Name,Field Type,Table name,User
	 *            ID Field
	 * @return HashMap 
	 * 			  HashMap containing the custom attributes
	 * 
	 * 
	 */
	private HashMap<String, Object> getCustomAttributesRFC(String sRFCName,
			String sUserID, ArrayList attrMapList) {
		HashMap<String, Object> hmCustomAttributes = new HashMap<String, Object>();
		String sMethodName = "getCustomAttributesRFC()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			if (jcoConnection != null) {
				SAPUtil oSAPUtil = new SAPUtil(logger);
				StringUtil oStringUtil = new StringUtil();
				UMUtility oUMUtil = new UMUtility(logger, hmConstants);
				for (int index = 0; index < attrMapList.size(); index++) {
					SAPUMAttributeMapBean oAttrMapBean = (SAPUMAttributeMapBean) attrMapList
							.get(index);
					JCoFunction jcoFunction = oSAPUtil.getJCOFunction(
							jcoConnection, sRFCName);
					JCoParameterList parameterList = jcoFunction
							.getImportParameterList();
					logger.debug(className, sMethodName, "Setting " + hmConstants.get(QUERY_TABLE) +" to "+oAttrMapBean.getBapiStructure());
					parameterList.setValue((String) hmConstants
							.get(QUERY_TABLE), oAttrMapBean.getBapiStructure());
					logger.debug(className, sMethodName, "Setting " + hmConstants.get(ROWSKIPS) +" to 0");
					parameterList.setValue((String) hmConstants.get(ROWSKIPS),
							0);
					logger.debug(className, sMethodName, "Setting " +hmConstants.get(ROWCOUNT) +" to 0");
					parameterList.setValue((String) hmConstants.get(ROWCOUNT),
							0);
					JCoTable returnOption1 = jcoFunction
							.getTableParameterList().getTable(
									(String) hmConstants.get(OPTIONS));
					returnOption1.appendRow();
					returnOption1.setValue(hmConstants.get(TEXT), oAttrMapBean
							.getUserIDKeyField()
							+ " EQ '" + sUserID + "'");
					JCoTable returnOption = jcoFunction.getTableParameterList()
							.getTable((String) hmConstants.get(FIELDS));
					returnOption.appendRow();
					returnOption.setValue((String) hmConstants.get(FIELDNAME),
							oAttrMapBean.getBapiFieldName());
					jcoFunction.execute(jcoConnection);
					JCoTable jcoTable = jcoFunction.getTableParameterList()
							.getTable((String) hmConstants.get(FIELDS));
					JCoTable jcoTable1 = jcoFunction.getTableParameterList()
							.getTable((String) hmConstants.get(DATA));
					int icodeOffSet = 0;
					int icodeLength = 0;
					String sValue = "";
					int iReturnValueTable1 = jcoTable1.getNumRows();
					if (iReturnValueTable1 != 0) {
						jcoTable.setRow(0);
						icodeOffSet = Integer.parseInt(jcoTable
								.getString((String) hmConstants.get(OFFSET)));
						icodeLength = Integer.parseInt(jcoTable
								.getString((String) hmConstants.get(LENGTH)));
						icodeLength = icodeLength + icodeOffSet;
						jcoTable1.setRow(0);
						String sMessage = jcoTable1.getString(hmConstants
								.get(WA));
						sValue = sMessage.substring(icodeOffSet, icodeLength)
								.trim();
						if (oAttrMapBean.getFieldType().equalsIgnoreCase(
								hmConstants.get(FIELDTYPE_LOOKUP))) {
							if (!oStringUtil.isEmpty(sValue)) {
								sValue = sITResourceKey + '~' + sValue;
							}
						} else if (oAttrMapBean.getFieldType()
								.equalsIgnoreCase(
										hmConstants.get(FIELDTYPE_DATE))) {
							DateFormat sdf = new SimpleDateFormat(oUtil
									.getReconOperAPI().getDefaultDateFormat());
							sValue = sdf.format(sValue);
						} else if (oAttrMapBean.getFieldType()
								.equalsIgnoreCase(
										hmConstants.get(FIELDTYPE_CHECKBOX))) {
							sValue = oUMUtil.getCheckBoxValueForOIM(
									oAttrMapBean.getOIMfieldName(), sValue,
									hmCheckBoxMapping);
						}
					} else {
						sValue = "";
					}
					hmCustomAttributes.put(oAttrMapBean.getOIMfieldName(),
							sValue);
				}

			} else {
				logger.error(className, sMethodName,
						"jcoConnection not initialised");
				throw new ConnectorException("jcoConnection not initialised");
			}
		} catch (JCoException e) {
			logger.error(className, sMethodName, e.getMessage());
		} catch (Exception e) {
			throw new ConnectorException(e.getMessage());
		}
		logger.debug(className, sMethodName, "HashMap containing the custom attributes -" +hmCustomAttributes);
		logger.setMethodFinishLog(className, sMethodName);
		return hmCustomAttributes;

	}

	/**
	 * Description : This method is used to get the attributes in SAP executed
	 * through custom BAPI's
	 * 
	 * @param sBAPIName
	 *            Name of the BAPI being used
	 * @param sUserID
	 *            User ID being reconciled for which we need the attributes
	 * @param attrMapList
	 *            ArrayList containing the set of attributes being reconciled.It
	 *            contains bean having OIM Field Name,Field Type,Table name,User
	 *            ID Field
	 * @return HashMap 
	 * 			  HashMap containing the custom attributes
	 * 
	 * 
	 */
	private HashMap<String, Object> getCustomAttributesBAPI(String sBAPIName,
			String sUserID, ArrayList attrMapList) {
		String sMethodName = "getCustomAttributesBAPI()";
		HashMap<String, Object> hmCustomAttributes = new HashMap<String, Object>();
		logger.setMethodStartLog(className, sMethodName);
		try {
			if (jcoConnection != null) {
				HashMap hmTable = new HashMap();
				JCoTable table;
				String sValue = "";
				String sTable;
				SAPUtil oSAPUtil = new SAPUtil(logger);
				UMUtility oUMUtil = new UMUtility(logger, hmConstants);
				StringUtil oStringUtil = new StringUtil();
				for (int index = 0; index < attrMapList.size(); index++) {
					SAPUMAttributeMapBean oAttrMapBean = (SAPUMAttributeMapBean) attrMapList
							.get(index);
					sTable = oAttrMapBean.getBapiStructure();
					if (!hmTable.containsKey(sTable)) {
						JCoFunction jcoFunction = oSAPUtil.getJCOFunction(
								jcoConnection, sBAPIName);
						JCoParameterList input = jcoFunction
								.getImportParameterList();
						logger.debug(className, sMethodName, "Setting " + oAttrMapBean.getUserIDKeyField() +" to "+sUserID);
						input.setValue(oAttrMapBean.getUserIDKeyField(),
								sUserID);
						jcoFunction.execute(jcoConnection);
						table = jcoFunction.getTableParameterList().getTable(
								sTable);
						hmTable.put(sTable, table);
					} else {
						table = (JCoTable) hmTable.get(sTable);
					}
					if (table.getNumRows() > 0) {
						table.setRow(0);
						sValue = (String) table.getValue(oAttrMapBean
								.getBapiFieldName());
						if (oAttrMapBean.getFieldType().equalsIgnoreCase(
								hmConstants.get(FIELDTYPE_LOOKUP))) {
							if (!oStringUtil.isEmpty(sValue)) {
								sValue = sITResourceKey + '~' + sValue;
							}
						} else if (oAttrMapBean.getFieldType()
								.equalsIgnoreCase(
										hmConstants.get(FIELDTYPE_DATE))) {
							DateFormat sdf = new SimpleDateFormat(oUtil
									.getReconOperAPI().getDefaultDateFormat());
							sValue = sdf.format(sValue);
						} else if (oAttrMapBean.getFieldType()
								.equalsIgnoreCase(
										hmConstants.get(FIELDTYPE_CHECKBOX))) {
							sValue = oUMUtil.getCheckBoxValueForOIM(
									oAttrMapBean.getOIMfieldName(), sValue,
									hmCheckBoxMapping);
						}
					} else {
						sValue = "";
					}
					hmCustomAttributes.put(oAttrMapBean.getOIMfieldName(),
							sValue);
				}
			} else {
				logger.error(className, sMethodName,
						"jcoConnection not initialised");
				throw new ConnectorException("jcoConnection not initialised");
			}
		} catch (JCoException e) {
			logger.error(className, sMethodName, e.getMessage());
		} catch (Exception e) {
			throw new ConnectorException(e.getMessage());
		}
		logger.debug(className, sMethodName, "List of Custom Attributes " + hmCustomAttributes);
		logger.setMethodFinishLog(className, sMethodName);
		return hmCustomAttributes;
	}
	
	/**
	 * Description : This method is used to get the child attributes in SAP executed
	 * through custom BAPI's
	 * 
	 * @param  sTable
	 *            Name of the Table name to be queried
	 * @param sUserID
	 *            User ID being reconciled for which we need the attributes
	 * @param attrMapList
	 *            ArrayList containing the set of attributes being reconciled.It
	 *            contains bean having OIM Field Name,Field Type,BAPI Name,User
	 *            ID Field,Child Table Name
	 * @return ArrayList 
	 * 			  ArrayList containing list of the custom attributes for the given table
	 * 
	 * 
	 */
	private ArrayList getCustomChildAttributes(String sTable,
			String sUserID, ArrayList attrMapList) {
		String sMethodName = "getCustomChildAttributes()";
		ArrayList alMultiValues = new ArrayList();
		logger.setMethodStartLog(className, sMethodName);
		try {
			if (jcoConnection != null) {
				Object fieldValue = null;
				JCoTable table = null;
				JCoTable table1 = null;
				String sBAPIName;
				SAPUtil oSAPUtil = new SAPUtil(logger);			
				SAPUMAttributeMapBean attrmapBean = (SAPUMAttributeMapBean) attrMapList
						.get(0);
				sBAPIName = attrmapBean.getSBAPINAME();
				if (sBAPIName.equalsIgnoreCase(hmConstants.get(RFC_READ_TABLE))) {
					JCoFunction jcoFunction = oSAPUtil.getJCOFunction(
							jcoConnection, sBAPIName);
					JCoParameterList parameterList = jcoFunction
							.getImportParameterList();
					logger.debug(className, sMethodName, "Setting " + hmConstants
							.get(QUERY_TABLE) +" to "+attrmapBean.getBapiStructure());
					parameterList.setValue((String) hmConstants
							.get(QUERY_TABLE), attrmapBean.getBapiStructure());
					logger.debug(className, sMethodName, "Setting " + hmConstants.get(ROWSKIPS) +" to 0");
					parameterList.setValue((String) hmConstants.get(ROWSKIPS),
							0);
					logger.debug(className, sMethodName, "Setting " + hmConstants.get(ROWCOUNT) +" to 0");
					parameterList.setValue((String) hmConstants.get(ROWCOUNT),
							0);
					JCoTable returnOption1 = jcoFunction
							.getTableParameterList().getTable(
									(String) hmConstants.get(OPTIONS));
					returnOption1.appendRow();
					returnOption1.setValue(hmConstants.get(TEXT), attrmapBean
							.getUserIDKeyField()
							+ " EQ '" + sUserID + "'");
					JCoTable returnOption;
					for (int index1 = 0; index1 < attrMapList.size(); index1++) {
						attrmapBean = (SAPUMAttributeMapBean) attrMapList
								.get(index1);
						 returnOption = jcoFunction.getTableParameterList()
						.getTable((String) hmConstants.get(FIELDS));
						returnOption.appendRow();
						returnOption.setValue((String) hmConstants.get(FIELDNAME),
						attrmapBean.getBapiFieldName());
					}				
					jcoFunction.execute(jcoConnection);
					table = jcoFunction.getTableParameterList().getTable(
							(String) hmConstants.get(DATA));
					table1 = jcoFunction.getTableParameterList().getTable(
							(String) hmConstants.get(FIELDS));
				} else {
					JCoFunction jcoFunction = oSAPUtil.getJCOFunction(
							jcoConnection, sBAPIName);
					JCoParameterList input = jcoFunction
							.getImportParameterList();
					input.setValue(attrmapBean.getUserIDKeyField(), sUserID);
					jcoFunction.execute(jcoConnection);
					table = jcoFunction.getTableParameterList()
							.getTable(sTable);
				}
				for (int j = 0; j < table.getNumRows(); j++) {
					table.setRow(j);
					HashMap hmChildData = new HashMap();
					for (int index1 = 0; index1 < attrMapList.size(); index1++) {
						attrmapBean = (SAPUMAttributeMapBean) attrMapList
								.get(index1);
						if (sBAPIName.equalsIgnoreCase(hmConstants
								.get(RFC_READ_TABLE))) {							
							for (int k = 0; k < table1.getNumRows(); k++) {
								table1.setRow(k);
								String sField = table1.getString((hmConstants).get(FIELDNAME));
								if (sField.equals(attrmapBean.getBapiFieldName())) {
									int icodeOffSet = Integer.parseInt(table1
											.getString((hmConstants).get(OFFSET)));
									int icodeLength = Integer.parseInt(table1
											.getString((hmConstants).get(LENGTH)));
									icodeLength = icodeLength + icodeOffSet;
									String sMessage = table.getString(hmConstants
											.get(WA));
									if (sMessage.length() < icodeLength) {
										fieldValue = sMessage.substring(icodeOffSet)
										.trim();
									}else{
										fieldValue = sMessage.substring(icodeOffSet, icodeLength)
										.trim();
									}									
									break;
								}
							}							
						} else {
							fieldValue = table.getValue(attrmapBean
									.getBapiFieldName());
						}
						// If attribute is type date, format as in
						// System Configuration
						if (attrmapBean.getFieldType().equalsIgnoreCase(
								hmConstants.get(FIELDTYPE_DATE))) {
							// Get the date format defined in System
							// Configuration
							DateFormat sdf = new SimpleDateFormat(oUtil
									.getReconOperAPI().getDefaultDateFormat());
							fieldValue = sdf.format(fieldValue);
						} else if (attrmapBean.getFieldType().equalsIgnoreCase(
								hmConstants.get(FIELDTYPE_LOOKUP))) {
							fieldValue = sITResourceKey + '~' + fieldValue;

						} else {
							fieldValue = (String) fieldValue;
						}
						hmChildData.put(attrmapBean.getOIMfieldName(),
								fieldValue);
					}
					alMultiValues.add(hmChildData);
				}

			} else {
				logger.error(className, sMethodName,
						"jcoConnection not initialised");
				throw new ConnectorException("jcoConnection not initialised");
			}
		} catch (JCoException e) {
			logger.error(className, sMethodName, e.getMessage());
		} catch (Exception e) {
			throw new ConnectorException(e.getMessage());
		}
		logger.setMethodFinishLog(className, sMethodName);
		return alMultiValues;
	}
	
	/**
	 * Description : This method is used to reconcile account lock/unlock
	 * 
	 * @param hmLockDetails
	 *            HashMap containing the account lock/unlock status for the user
	 * 
	 * 
	 */
	private void reconcileAccountLock(HashMap<String, String> hmLockDetails) {
		String sMethodName = "reconcileAccountLock";
		logger.setMethodStartLog(className, sMethodName);
		HashMap<String, String> hmRecon = new HashMap<String, String>();
		if (hmLockDetails.size() > 0) {
			logger.debug(className, sMethodName, "Account ID Lock details "
					+ hmLockDetails);
			Iterator iterator = hmLockDetails.keySet().iterator();
			while (iterator.hasNext() && !isStopped()) {
				String sUserID = (String) iterator.next();
				String sLockStatus = (String) hmLockDetails.get(sUserID);
				hmRecon.put(hmConstants.get(USER_ID), sUserID);
				hmRecon.put(hmConstants.get(IT_RESOURCE), sITResourceName);
				if (sLockStatus.equalsIgnoreCase(hmConstants.get(ZERO))) {
					hmRecon.put(hmConstants.get(LOCK), hmConstants.get(ZERO));
				} else {
					hmRecon.put(hmConstants.get(LOCK), hmConstants.get(ONE));
				}
				try {
					if (!oUtil.getReconOperAPI().ignoreEvent(sObjectName,
							hmRecon)) {
						oUtil.createTargetReconEvent(sObjectName, hmRecon,
								null, sUserID);
					}
				} catch (tcAPIException e) {
					throw new ConnectorException(e.getMessage());
				} catch (tcObjectNotFoundException e) {
					throw new ConnectorException(e.getMessage());
				}
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
		hmRecon.clear();
	}
/*
	 * Start Code Modified for 10373020 
	 */
	public boolean isValidRole(HashMap hmChildData)
	{
		boolean isValid = true;
		DateUtil oDateutil = new DateUtil(logger);
		String startDate= hmChildData.get(START_DATE).toString();
	    String endDate = hmChildData.get(END_DATE).toString();
	    String futureRole = hmUMConfig.get(RECONCILE_FUTURE_DATED_ROLES).toString();
	    String pastRole = hmUMConfig.get(RECONCILE_PAST_DATED_ROLES).toString();
	    Date dtEndDate = null;
    	Date dtStartDate = null;
    	try{
	    	dtStartDate = oDateutil.returnDate(startDate,oUtil.getReconOperAPI().getDefaultDateFormat());
		    dtEndDate = oDateutil.returnDate(endDate,oUtil.getReconOperAPI().getDefaultDateFormat());
	    }catch (tcAPIException e) {
			throw new ConnectorException(e.getMessage());
		}
	   	Date today = new Date();
	    if(!stringUtil.isEmpty(futureRole) && NO.equalsIgnoreCase(futureRole))
	    {
	    	if(dtStartDate != null && dtStartDate.after(today))
	    		{
	    		isValid = false;
	    		}
	    }
	    if(!stringUtil.isEmpty(pastRole) && NO.equalsIgnoreCase(pastRole))
	    {
	    	if(dtEndDate != null && dtEndDate.before(today))
	    		{
	    		isValid = false;
	    		}
	    }
		return isValid;
		}
	/*
	 * End Code Modified for Role 10373020
	 */
}
