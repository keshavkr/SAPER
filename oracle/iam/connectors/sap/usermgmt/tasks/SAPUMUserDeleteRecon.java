/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/usermgmt/tasks/SAPUMUserDeleteRecon.java /main/6 2011/05/26 04:09:16 hhaque Exp $ */

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
 configured for delete reconciliation
 ddkumar     07/10/09 - Creation
 */

/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/usermgmt/tasks/SAPUMUserDeleteRecon.java /main/5 2010/07/08 02:55:19 ddkumar Exp $
 *  @author  ddkumar 
 *  @since   release specific (what release of product did this appear in)
 */


package oracle.iam.connectors.sap.usermgmt.tasks;

/*
	(MM/DD/YY)    BUG Description
	05/26/2011	  BUG 12586222 - SAP UM RECON LOOKUP WITH NON UNIQUE VALUES

*/
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

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
import oracle.iam.connectors.sap.usermgmt.util.UMConstants;
import oracle.iam.connectors.sap.usermgmt.util.UMUtility;
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
import com.thortech.xl.scheduler.tasks.SchedulerBaseTask;

/**
 * Description: Mapped to the scheduled task that is configured for delete reconciliation of users
 */
public class SAPUMUserDeleteRecon extends SchedulerBaseTask implements
		UMConstants {
	//private boolean isStopRecon = false;
	private boolean isValid = false;
	private long lITResourceKey;
	private String sITResourceName;
	private String sObjectName;
	private String sScheduleTaskName;
	private String sExecutionTime;
	private String sDisabledUser;
	private ConnectionService ser;
	private SAPResourceImpl rc = null;
	private OIMUtil oUtil = null;
	private StringUtil stringUtil = new StringUtil();
	private ConnectorLogger logger = new ConnectorLogger(UM_LOGGER);
	private String className = this.getClass().getName();
	private JCoDestination jcoConnection;
	private Hashtable<String, String> htITRattributes;
	private Hashtable<String, String> htTaskAttributes;
	private HashMap<String, String> hmConstants;
	private HashMap<String, String> hmUMConfig;

	/**
	 * Description: Initializes the scheduled task attributes and lookup definitions. It also checks if all required parameters are correctly set in the IT resource and scheduled task. 
	 */
	public void init() {
		String sMethodName = "init()";
		//isStopRecon = false;
		logger.setMethodStartLog(className, sMethodName);
		logger.info(className, sMethodName,
				"Start of SAP User Management Delete Reconciliation");

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

			// Validate if all required task attributes are set properly
			boolean isMandatoryTaskAttrSet = oTaskAttributes
					.validateMandatoryTaskAttrs(mandatoryUserDeleteReconSchedulerAttrs);
			if (!isMandatoryTaskAttrSet) {
				throw new ConnectorException(
						"Mandatory Task Scheduler values not set");
			}

			// Validate if all Fixed IT Resource attributes are set properly
			boolean isFixedTaskAttributes = oTaskAttributes
					.validateFixedValues(sFixedTaskValues, ",");

			NumberUtil oNUtil = new NumberUtil();

			if (!isFixedTaskAttributes) {
				throw new ConnectorException(
						"Task Scheduler Attribute values not set properly");
			}
			
			String sBatchSize = (String) htTaskAttributes.get(BATCH_SIZE);
			boolean isBatchSize = oNUtil.isNumeric(sBatchSize);
			if (!isBatchSize) {
				throw new ConnectorException(
						"Batch Size should be positive number");
			}
			sITResourceName = (String) htTaskAttributes.get(IT_RESOURCE_NAME);
			sObjectName = (String) htTaskAttributes.get(RESOURCE_OBJECT);
			sDisabledUser = (String) htTaskAttributes.get(DISABLE_USER);
			sExecutionTime = (String) htTaskAttributes.get(EXECUTION_TIMESTAMP);
			logger.info("Task Scheduler Attributes initialised");

			// Get the IT Resource attributes in HashTable
			ITResource oITResource = new ITResource(sITResourceName, resAPI,
					logger);
			htITRattributes = oITResource.getITResourceDetails();
			// Initialize HashMap for Configuration Lookup and Constant Lookup
			String sConfigLookup = (String) htITRattributes
					.get(CONFIG_LOOKUP);
			hmUMConfig = oUtil.getLookUpMap(sConfigLookup);
			hmConstants = oUtil.getLookUpMap((String) hmUMConfig
					.get(CONSTANTS_LOOKUP));
		// Start :: BUG 12586222 - SAP UM RECON LOOKUP WITH NON UNIQUE VALUES	
			//lITResourceKey = oUtil.getITResourceKey(sITResourceName);
			lITResourceKey = Long.parseLong(htITRattributes.get(hmConstants.get(IT_RESOURCE_KEY)));
		// End   :: BUG 12586222 - SAP UM RECON LOOKUP WITH NON UNIQUE VALUES	
			
			
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
	 * Description: Starts the delete reconciliation process
	 * 
	 */
	public void execute() {
		String sMethodName = "execute()";
		logger.setMethodStartLog(className, sMethodName);
		boolean isPoolingEnabled = false;
		SAPConnection sAPConnection = null;
		try {
			if (isValid) {
				DateUtil oDateutil = new DateUtil(logger);
				Date date = new Date();
				String sReconTime = oDateutil.parseTime(date,
						(String) hmConstants.get(DATE_FORMAT), htTaskAttributes
								.get(TIMEZONEFORMAT));
				
				HashMap<String, String> hmExclusionList = oUtil
						.getLookUpMap((String) hmUMConfig.get(EXCLUSION_LIST));
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
				/*
				 * Call getDeletedReconEvents() to reconcile the deleted
				 * accounts and update the tasks scheduler with time taken at
				 * the beginning of recon if the recon run is successful
				 */
				getDeletedReconEvents(hmExclusionList);
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
					"End of SAP User Management Delete Reconciliation");
		}
	}

	/**
	 * Description : This method is called to reconcile the deleted accounts in
	 * target system
	 * 
	 * @param hmExclusionList
	 *            HashMap containing list of users for which delete recon event
	 *            should not be done
	 * 
	 */
	private void getDeletedReconEvents(HashMap<String, String> hmExclusionList) {
		String sMethodName = "getDeletedReconEvents()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger,hmConstants);
		try {
			boolean isMoreRecordsFound = true;
			boolean isFirstTime = false;
			int iStartRecord = 0;
			String sBatchSize = (String) htTaskAttributes.get(BATCH_SIZE);
			int iBatchSize = Integer.parseInt(sBatchSize);
			if (sExecutionTime.equalsIgnoreCase(hmConstants.get(ZERO))) {
				isFirstTime = true;
			}
			while (isMoreRecordsFound && !isStopped()) {
				/*
				 * Get all accounts modified by querying the USH04 table
				 */
				HashMap<String, String> hmAccounts = oUMUtil.getAccounts(
						hmConstants.get(USH04_TABLE), hmExclusionList,
						isFirstTime, iStartRecord, iBatchSize, sExecutionTime,
						jcoConnection, true);
				if (hmAccounts.size() > 0) {
					Iterator iterator = hmAccounts.keySet().iterator();
					while (iterator.hasNext() && !isStopped()) {
						String sUserID = (String) iterator.next();
						SAPUtil oSAPUtil = new SAPUtil(logger);
						/*
						 * Check if account is deleted in SAP.If so then return
						 * true to delete the account in OIM
						 */
						JCoFunction jcoFunction = oSAPUtil.getJCOFunction(
								jcoConnection, hmConstants
										.get(BAPI_USER_GET_DETAIL));
						HashMap<String, Object> hmUser = new HashMap<String, Object>();
						JCoParameterList input = jcoFunction
								.getImportParameterList();
						input.setValue(hmConstants.get(USERNAME), sUserID);
						jcoFunction.execute(jcoConnection);
						boolean isUserExists = oUMUtil.findUser(jcoFunction);
						if (!isUserExists && !isStopped()) {
							hmUser.put(hmConstants.get(USER_ID), sUserID);
							hmUser.put(hmConstants.get(IT_RESOURCE),
									sITResourceName);
							if (sDisabledUser.equalsIgnoreCase(hmConstants
									.get(YES))) {
								logger
										.info("Creating Recon Event for user with User ID::"
												+ sUserID);
								hmUser.put(hmConstants.get(STATUS), hmConstants
										.get(DISABLED_STATUS));
								long rce = oUtil.getReconOperAPI()
										.createReconciliationEvent(sObjectName,
												hmUser, false);
								oUtil.getReconOperAPI()
										.finishReconciliationEvent(rce);
							} else {
								oUtil.createTargetDeleteReconEvent(sObjectName,
										hmUser, sUserID);
							}
							hmUser.clear();
						}
					}
					iStartRecord += iBatchSize;
					if (iBatchSize == 0) {
						isMoreRecordsFound = false;
					}
				} else {
					isMoreRecordsFound = false;
				}
			}
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
	}

	/**
	 * Description: Stops the execution of the reconciliation process by setting the Boolean flag to true. 
	 * 
	 * @return boolean 
	 * 		Returns true if the scheduled task is manually stopped during the reconciliation run
	 * 
	 */
	/*public boolean stop() {
		isStopRecon = true;
		logger.info("Stopping the reconciliation process forcefully......");
		return isStopRecon;
	}*/
}
