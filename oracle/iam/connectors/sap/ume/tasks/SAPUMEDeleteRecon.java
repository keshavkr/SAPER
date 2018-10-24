/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/ume/tasks/SAPUMEDeleteRecon.java /main/1 2010/05/11 03:23:16 ddkumar Exp $ */

/* Copyright (c) 2009, 2010, Oracle and/or its affiliates. 
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
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/ume/tasks/SAPUMEDeleteRecon.java /main/1 2010/05/11 03:23:16 ddkumar Exp $
 *  @author  ddkumar 
 *  @since   release specific (what release of product did this appear in)
 */

package oracle.iam.connectors.sap.ume.tasks;

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
import oracle.iam.connectors.sap.cup.integration.SAPCUPProxyUserProvisionManager;
import oracle.iam.connectors.sap.ume.util.UMEConstants;
import oracle.iam.connectors.sap.usermgmt.util.UMUtility;
import Thor.API.Operations.tcFormDefinitionOperationsIntf;
import Thor.API.Operations.tcFormInstanceOperationsIntf;
import Thor.API.Operations.tcITResourceInstanceOperationsIntf;
import Thor.API.Operations.tcLookupOperationsIntf;
import Thor.API.Operations.tcObjectOperationsIntf;
import Thor.API.Operations.tcReconciliationOperationsIntf;
import Thor.API.Operations.tcSchedulerOperationsIntf;
import Thor.API.Operations.tcUserOperationsIntf;
import Thor.API.tcResultSet;
import Thor.API.Exceptions.tcAPIException;
import Thor.API.Exceptions.tcColumnNotFoundException;
import Thor.API.Exceptions.tcObjectNotFoundException;
import Thor.API.Operations.tcITResourceInstanceOperationsIntf;

import com.oracle.oim.gcp.exceptions.ConnectionServiceException;
import com.oracle.oim.gcp.pool.ConnectionService;
import oracle.iam.connectors.common.vo.ProcessForm;
import com.thortech.xl.scheduler.tasks.SchedulerBaseTask;

/**
 * Description: Mapped to the scheduled task that is configured for delete reconciliation of users
 */
public class SAPUMEDeleteRecon extends SchedulerBaseTask implements
UMEConstants {
	private boolean isStopRecon = false;
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
	private Hashtable<String, String> htITRattributes;
	private Hashtable<String, String> htTaskAttributes;
	private HashMap<String, String> hmConstants;
	private HashMap<String, String> hmUMConfig;

	/**
	 * Description: Initializes the scheduled task attributes and lookup definitions. It also checks if all required parameters are correctly set in the IT resource and scheduled task. 
	 */
	public void init() {
		String sMethodName = "init()";
		isStopRecon = false;
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

			/*			String sBatchSize = (String) htTaskAttributes.get(BATCH_SIZE);
			boolean isBatchSize = oNUtil.isNumeric(sBatchSize);
			if (!isBatchSize) {
				throw new ConnectorException(
						"Batch Size should be positive number");
			}*/
			sITResourceName = (String) htTaskAttributes.get(IT_RESOURCE_NAME);
			sObjectName = (String) htTaskAttributes.get(RESOURCE_OBJECT);
			sDisabledUser = (String) htTaskAttributes.get(DISABLE_USER);
			/*			sExecutionTime = (String) htTaskAttributes.get(EXECUTION_TIMESTAMP);*/
			logger.info("Task Scheduler Attributes initialised");

			// Get the IT Resource attributes in HashTable
			ITResource oITResource = new ITResource(sITResourceName, resAPI,
					logger);
			htITRattributes = oITResource.getITResourceDetails();
			lITResourceKey = oUtil.getITResourceKey(sITResourceName);


			// Initialize HashMap for Configuration Lookup and Constant Lookup
			String sConfigLookup = (String) htITRattributes
			.get(CONFIG_LOOKUP);
			hmUMConfig = oUtil.getLookUpMap(sConfigLookup);
			hmConstants = oUtil.getLookUpMap((String) hmUMConfig
					.get(CONSTANTS_LOOKUP));
			// Validate if all required IT Resource attributes are set properly
			boolean isMandatoryITRSet = oITResource
			.validateMandatoryITResource(mandatoryUMITRes);

			if (!isMandatoryITRSet) {
				throw new ConnectorException(
				"Mandatory IT Resource values not set");
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
				HashMap<String, String> hmExclusionList = oUtil
				.getLookUpMap((String) hmUMConfig.get(EXCLUSION_LIST));

				/*
				 * Call getDeletedReconEvents() to reconcile the deleted
				 * accounts and update the tasks scheduler with time taken at
				 * the beginning of recon if the recon run is successful
				 */
				//performDeleteReconciliation(hmExclusionList);

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
		} 
	}


	/**
	 * Description: Stops the execution of the reconciliation process by setting the Boolean flag to true. 
	 * 
	 * @return boolean 
	 * 		Returns true if the scheduled task is manually stopped during the reconciliation run
	 * 
	 */
	public boolean stop() {
		isStopRecon = true;
		logger.info("Stopping the reconciliation process forcefully......");
		return isStopRecon;
	}

	/**
	 * Description:This method verifies the reads all the available OIM users 
	 * and searches on the RSA target system. If the user is deleted on the 
	 * target system, Revoke the same user Resource Object in OIM.
	 * 
	 * @param sIdentitySource
	 * 		Identity Source to which RSA AuthManager is Configured.
	 */
	public void performDeleteReconciliation(){

		String sMethodName = "performDeleteReconciliation()";
		logger.setMethodStartLog(className, sMethodName);		
		HashMap hmROMap = new HashMap();

		ProcessForm processFormVO = null;
		HashMap hmProcessFormData = null;
		long lProcInstanceKey;

		hmROMap.put(OBJECT_NAME, htTaskAttributes.get(UME_RESOURCE_OBJECT));
		tcResultSet rsROs;
		tcResultSet rsTokenROs;
		HashMap user = new HashMap();	

		try {
			rsROs = oUtil.getObjAPI().findObjects(hmROMap);

			long key= rsROs.getLongValue(OBJECT_KEY);
			//long processFormKey =  
			tcResultSet rsUsers = oUtil.getObjAPI().getAssociatedUsers(key, user);

			int size = rsUsers.getTotalRowCount();

			logger.debug(className, sMethodName,"Number of Users attached to" 
					+" the ResouceObject"+htTaskAttributes.get
					(UME_RESOURCE_OBJECT)+"are"+size);

			for(int j=0;j<rsUsers.getTotalRowCount();j++){
				rsUsers.goToRow(j);

				if(!(rsUsers.getStringValue(OBJECT_STATUS).
						equalsIgnoreCase(STATUS_REVOKED)||
						rsUsers.getStringValue(OBJECT_STATUS).
						equalsIgnoreCase(STATUS_PROVISIONING)||
						rsUsers.getStringValue(OBJECT_STATUS).
						equalsIgnoreCase(STATUS_WAITING))){

					lProcInstanceKey = rsUsers.getLongValue(PROCESS_INSTANCE_KEY);
					processFormVO = new ProcessForm
					(lProcInstanceKey,oUtil.getFormAPI(),logger);
					hmProcessFormData = processFormVO.getFormDataMap
					(lProcInstanceKey);
					String userID =(String)hmProcessFormData.get
					(hmUMConfig.get(USER_FORMFIELD_USERID));
					long lProcessFrmITResKey = Long.parseLong((String)
							hmProcessFormData.get(hmUMConfig.
									get(USER_FORMFIELD_ITRES)));

					tcITResourceInstanceOperationsIntf tcITResOprIntf = 
						oUtil.getResAPI();
					long lTaskSchITResKey = oUtil.getITResourceKey(
							(String)htTaskAttributes.get(IT_RESOURCE_NAME));
					logger
					.info(className,sMethodName,"Searching the User  "+
							userID+"  on "+htTaskAttributes.get(IT_RESOURCE_NAME)+
					" RSA  system");

					if(lProcessFrmITResKey==lTaskSchITResKey){
						SAPCUPProxyUserProvisionManager sAPProxyUserProvisionManager 
						= new SAPCUPProxyUserProvisionManager(hmConstants, hmUMConfig, htITRattributes);

						if (! sAPProxyUserProvisionManager.findUser(userID)) {

							HashMap hmUser = new HashMap();
							hmUser.put(hmUMConfig.get(USER_ROFIELD_USERID),
									userID);

							oUtil.createTargetDeleteReconEvent((String)htTaskAttributes.
									get(UME_RESOURCE_OBJECT),
									hmUser, userID);

						}
					}

				}
			}

		}catch (tcAPIException tcAPIExcp) {
			logger.error(className, sMethodName, tcAPIExcp.getMessage());
			logger.setStackTrace(tcAPIExcp, className, sMethodName, tcAPIExcp
					.getMessage());
			throw new ConnectorException(tcAPIExcp);
		}catch (tcColumnNotFoundException columnNotFoundExcp) {
			logger.
			error(className, sMethodName, columnNotFoundExcp.getMessage());
			logger.
			setStackTrace(columnNotFoundExcp, className, 
					sMethodName, columnNotFoundExcp.getMessage());
			throw new ConnectorException(columnNotFoundExcp);
		}  catch (tcObjectNotFoundException objNotFundExcp) {
			logger.error(className,sMethodName,objNotFundExcp.getMessage());
			logger.
			setStackTrace(objNotFundExcp, className, sMethodName,
					objNotFundExcp.getMessage());
			throw new ConnectorException(objNotFundExcp);
		} catch (Exception genExcp) {

			logger.error(className, sMethodName, genExcp.getMessage());
			logger.setStackTrace(genExcp, className, sMethodName, genExcp
					.getMessage());	
			throw new ConnectorException(genExcp);
		} 

		logger.setMethodFinishLog(className, sMethodName);
	}

}
