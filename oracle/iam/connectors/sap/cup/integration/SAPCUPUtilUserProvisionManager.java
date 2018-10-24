/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/cup/integration/SAPCUPUtilUserProvisionManager.java /main/1 2010/05/11 03:23:16 ddkumar Exp $ */

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
 ddkumar     07/10/09 - Creation
 */

package oracle.iam.connectors.sap.cup.integration;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.dao.OIMUtil;
import oracle.iam.connectors.common.util.DateUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.common.vo.ITResource;
import oracle.iam.connectors.sap.common.connection.SAPConnection;
import oracle.iam.connectors.sap.common.util.SAPUtil;
import oracle.iam.connectors.sap.usermgmt.integration.SAPUMProxyUserProvisionManager;
import oracle.iam.connectors.sap.usermgmt.util.SAPUMAttributeMapBean;
import oracle.iam.connectors.sap.ume.util.UMEConstants;
import oracle.iam.connectors.sap.usermgmt.util.UMUtility;
import Thor.API.tcResultSet;
import Thor.API.Exceptions.tcAPIException;
import Thor.API.Exceptions.tcColumnNotFoundException;
import Thor.API.Exceptions.tcFormNotFoundException;
import Thor.API.Exceptions.tcInvalidLookupException;
import Thor.API.Exceptions.tcInvalidValueException;
import Thor.API.Exceptions.tcNotAtomicProcessException;
import Thor.API.Exceptions.tcProcessNotFoundException;
import Thor.API.Exceptions.tcRequiredDataMissingException;
import Thor.API.Exceptions.tcVersionNotFoundException;
import Thor.API.Operations.tcFormDefinitionOperationsIntf;
import Thor.API.Operations.tcFormInstanceOperationsIntf;
import Thor.API.Operations.tcITResourceInstanceOperationsIntf;
import Thor.API.Operations.tcLookupOperationsIntf;

import com.thortech.xl.dataaccess.tcDataProvider;

/**
 * Description:Contain methods that are mapped to adapters for performing
 * various provisioning functions
 * 
 */

public class SAPCUPUtilUserProvisionManager implements UMEConstants {

	private ConnectorLogger logger = new ConnectorLogger(UM_LOGGER);
	private Hashtable<String, String> htITRattributes;
	private Hashtable<String, String> htGRCITRattributes;
	private Hashtable<String, String> htCUPITRattributes;
	private HashMap<String, String> hmCUPConfig;
	private HashMap<String, String> hmCUPConstants;
	private HashMap<String, String> configurationMap;
	private OIMUtil oUtil = null;
	private long lProcInstanceKey;
	private tcFormInstanceOperationsIntf fiIntf;
	private tcFormDefinitionOperationsIntf fdIntf;
	private static tcLookupOperationsIntf lookIntf;
	private String className = this.getClass().getName();
	private StringUtil StrUtil = new StringUtil();
	private DateUtil dtUtil = new DateUtil(logger);
	private HashMap<String, String> hmConstants;
	long sITResourceKey;
	long sGRCITResourceKey;
	private HashMap<String, Object> hmFormData = new HashMap<String, Object>();
	private HashMap<String, Object> hmChildDetails = new HashMap<String, Object>();
	private Hashtable<String, String> htGRCITRattr = new Hashtable<String, String>();;
	boolean isInnitialized = true;

	/**
	 * Description:Used by the addDummyResourceToUser method to initialize
	 * OIMUtil
	 * 
	 * @param pDataBaseRef
	 *            Data Provider
	 * 
	 */
	public SAPCUPUtilUserProvisionManager(tcDataProvider pDataBaseRef) {
		String sMethodName = "SAPUMUtilUserProvisionManager()";
		try {
			oUtil = new OIMUtil(pDataBaseRef, logger);
		} catch (tcAPIException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		}
	}

	/**
	 * Description: Used to initialize the IT Resource HashMap, configuration
	 * lookup hashMap, and other global variables used in all methods
	 * 
	 * @param pDataBaseRef
	 *            Data Provider
	 * @param pParentFormProcessInstanceKey
	 *            Parent process key
	 * @param sITResourceUDField
	 *            UDField corresponding to the IT resource specified on process
	 *            form. For example: UD_SAP_ITRESOURCE
	 * @throws ConnectorException
	 */
	public SAPCUPUtilUserProvisionManager(tcDataProvider pDataBaseRef,
			String pParentFormProcessInstanceKey, String sITResourceUDField )
	throws ConnectorException {
		String sMethodName = "SAPUMUtilUserProvisionManager()";
		logger.setMethodStartLog(className, sMethodName);
		String sITRMapping = null;
		try {
			if (pDataBaseRef == null
					|| StrUtil.isEmpty(pParentFormProcessInstanceKey)
					|| StrUtil.isEmpty(sITResourceUDField)) {
				throw new ConnectorException("Empty parameters");
			}
			oUtil = new OIMUtil(pDataBaseRef, logger);
			tcITResourceInstanceOperationsIntf resAPI = oUtil.getResAPI();
			fiIntf = oUtil.getFormAPI();
			fdIntf = oUtil.getFormDefAPI();
			lookIntf = oUtil.getLookIntf();
			lProcInstanceKey = new Long(pParentFormProcessInstanceKey)
			.longValue();
			hmFormData = oUtil.getFormDataMap(lProcInstanceKey);
			String pITResourceFieldName = hmFormData.get(sITResourceUDField)
			.toString();
			sITResourceKey = new Long(pITResourceFieldName).longValue();
			ITResource oITResource = new ITResource(sITResourceKey, resAPI,
					logger);
			htITRattributes = oITResource.getITResourceDetails();

			String sConfigLookupName = htITRattributes.get(CONFIG_LOOKUP);
			logger.debug(className, sMethodName, "Configuration Lookup Name : "
					+ sConfigLookupName);
			configurationMap = oUtil.getLookUpMap(sConfigLookupName);
			if (configurationMap != null) {
				try {
					hmConstants = oUtil.getLookUpMap((String) configurationMap
							.get(CONSTANTS_LOOKUP));

					try {
						logger.debug(className, sMethodName, "Innitializing CUP data");
						String sCUPConfigLookup = configurationMap
						.get(CUP_CONFIG_LOOKUP);
						logger.debug(className, sMethodName, "CUP Configuration Lookup "+sCUPConfigLookup);
						hmCUPConfig = oUtil.getLookUpMap(sCUPConfigLookup);
						logger.debug(className, sMethodName, "CUP Configuration lookup hashmap innitialized"); 
						String sCUPITres = hmCUPConfig.get(ITR_FIELD);

						long sCUPITResourceKey = oUtil.getITResourceKey(sCUPITres);
						ITResource oCUPITResource = new ITResource(sCUPITResourceKey,
								resAPI, logger);
						htCUPITRattributes = oCUPITResource.getITResourceDetails();
						logger.debug(className, sMethodName, "CUP ITResource hashmap innitialized"); 
						String sCUPConstantsLookup = hmCUPConfig.get(CONSTANTS_LOOKUP);
						hmCUPConstants = oUtil.getLookUpMap(sCUPConstantsLookup);
						logger.debug(className, sMethodName, "CUP Constants lookup hashmap innitialized"); 
						String sOverwriteStatus = hmCUPConfig.get(CUP_IGNORE_STATUS);
						logger.debug(className, sMethodName, CUP_IGNORE_STATUS+": " + sOverwriteStatus); 
						boolean isOverwriteCUPFlag = sOverwriteStatus.equalsIgnoreCase(hmConstants.get(YES))?true:false;
						if(!isOverwriteCUPFlag) {
							logger.info(className, sMethodName, "Checking if request is currently OPEN");
							if (hmFormData.get(hmCUPConstants.get(UD_REQUEST_STATUS)).toString()
									.equalsIgnoreCase(hmCUPConstants.get(OPEN))
									|| hmFormData.get(hmCUPConstants.get(UD_REQUEST_STATUS)).toString()
									.equalsIgnoreCase(hmCUPConstants.get(HOLD))) {
								logger.error(className, sMethodName, "Request ID "
										+ hmFormData.get(hmCUPConstants.get(UD_REQUEST_ID))
										+ " is in OPEN status");
								isInnitialized = false;
							}
						}
					} catch (Exception e) {
						logger.error(className, sMethodName,
						"Error innitializing CUP values");
						throw new ConnectorException(e);
					}
				} catch (Exception e) {
					logger.error(className, sMethodName,
					"Invalid configuration lookup field");
					throw new ConnectorException(e);
				}
			} else {
				logger.error(className, sMethodName,
				"Configuration lookup not innitialized");
				throw new ConnectorException(
				"Configuration lookup not innitialized");
			}

			boolean isMandatoryITRSet = oITResource
			.validateMandatoryITResource(mandatoryUMITRes);
			if (!isMandatoryITRSet) {
				throw new ConnectorException(
				"Mandatory IT Resource values not set");
			}
			logger.info(className, sMethodName,
			"IT Resource values initialised");
		} catch (tcAPIException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			isInnitialized = false;
		} catch (ConnectorException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			isInnitialized = false;
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			isInnitialized = false;
		}
		logger.setMethodFinishLog(className, sMethodName);
	}



	/**
	 * Description:Used to provision a user on the target system
	 * 
	 * @param sUserID
	 *            User ID of the user to be created 
	 *    		  For example:  John.Doe 
	 * @param sPassword
	 *            Password entered on the process form
	 * 
	 * @return String Returns the response code that is mapped in the adapter 
	 * 
	 * @throws ConnectorException
	 */
	public String createUser(String sUserID, String sPassword)
	throws ConnectorException {
		String sResponse = null;
		String sUseValidation = null;
		String sValidationLookupName = null;
		boolean isValid = true;	

		String sCUPAttrMapLookupName = null; 
		HashMap<String, String> hmCUPAttrMap = new HashMap<String, String>();
		String sReqType= null;
		String sApplication= null;
		String sPriority = null;

		HashMap<String, ArrayList<SAPUMAttributeMapBean>> attrMap = new HashMap<String, ArrayList<SAPUMAttributeMapBean>>();
		HashMap<String, String> hmExclusionList = new HashMap<String, String>();
		String sExclusionListLookup;
		String sMethodName = "createUser()";
		logger.setMethodStartLog(className, sMethodName);

		try {
			if(!isInnitialized){
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Innitialization failed");
				throw new ConnectorException(sResponse);
			}
			if (StrUtil.isEmpty(sUserID) ) {
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Provide User ID:");
				throw new ConnectorException(sResponse);
			}
			if (StrUtil.isEmpty(sPassword)) {
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Provide password for User ID:"+sUserID);
				throw new ConnectorException(sResponse);
			}

			if (configurationMap != null) {
				try {			
					sExclusionListLookup = configurationMap.get(EXCLUSION_LIST);
					logger.debug(className, sMethodName, EXCLUSION_LIST + ":"
							+ sExclusionListLookup);
					sUseValidation = configurationMap.get(USE_PROV_VALIDATION);
					logger.debug(className, sMethodName, USE_PROV_VALIDATION
							+ ":" + sUseValidation);
					sValidationLookupName = configurationMap
					.get(VALIDATE_LOOKUP_PROV);
					logger.debug(className, sMethodName, VALIDATE_LOOKUP_PROV
							+ ":" + sValidationLookupName);

					sCUPAttrMapLookupName = hmCUPConfig.get(PARENT_ATTR_LOOKUP);
					logger.debug(className, sMethodName, PARENT_ATTR_LOOKUP
							+ ":" + sCUPAttrMapLookupName);
					sReqType = hmCUPConfig.get(CREATE_USER);
					logger.debug(className, sMethodName, "Request type for create user"
							+ ":" + sReqType);
					sApplication = hmCUPConfig.get(APPLICATION);
					logger.debug(className, sMethodName, APPLICATION
							+ ":" + sApplication);
					sPriority = hmCUPConfig.get(PRIORITY);
					logger.debug(className, sMethodName, PRIORITY
							+ ":" + sPriority);
				} catch (ConnectorException e) {
					sResponse = INSUFFICIENT_INFORMATION;
					logger.error(className, sMethodName,
					"Invalid configuration lookup field");
					throw new ConnectorException(sResponse);
				} catch (Exception e) {
					sResponse = INSUFFICIENT_INFORMATION;
					logger.error(className, sMethodName,
					"Invalid configuration lookup field");
					throw new ConnectorException(sResponse);
				}
			} else {
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, sResponse);
				throw new ConnectorException(sResponse);
			}

			/*
			 * If validation is required,then call
			 * validateSingleOrMultivaluedData() to transform the data
			 */
			if (hmConstants.get(YES).equalsIgnoreCase(sUseValidation)) {
				isValid = oUtil.validateSingleOrMultivaluedData(hmFormData,
						hmChildDetails, sValidationLookupName);
			}
			if (isValid) {
				hmExclusionList = oUtil.getLookUpMap(sExclusionListLookup);
				if (!hmExclusionList.containsKey(sUserID)) {
					logger.debug(className, sMethodName, "Performing attribute mapping for CUP");
					hmCUPAttrMap = populateCUPUserDataToHash(sCUPAttrMapLookupName, null);

					logger.info(className, sMethodName, sUserID + "to be provisioned");
					SAPCUPProxyUserProvisionManager sAPProxyUserProvisionManager 
					= new SAPCUPProxyUserProvisionManager(hmConstants, configurationMap, htITRattributes);
					// check if user exists in SAP system before create
					if (!sAPProxyUserProvisionManager.findUser(sUserID)) {
						String sResponseID = sAPProxyUserProvisionManager.createCUPRequest(
								hmCUPAttrMap, htCUPITRattributes, hmCUPConstants, null , sReqType, sApplication, sPriority);
						HashMap<String,String> hmUpdate = new HashMap<String,String>();
						if(sResponseID != null) {
							String sCUPStatus = sAPProxyUserProvisionManager.getStatus(sResponseID,htCUPITRattributes, hmCUPConstants);
							logger.debug(className, sMethodName, "Starting update to process form");
							hmUpdate.put(hmCUPConstants.get(UD_REQUEST_ID), sResponseID);
							hmUpdate.put(hmCUPConstants.get(UD_REQUEST_STATUS), sCUPStatus);
							fiIntf.setProcessFormData(lProcInstanceKey, hmUpdate);
							logger.debug(className, sMethodName, "Process form updated");
							boolean isCreated = sAPProxyUserProvisionManager.findUser(sUserID);
							if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(REJECT))) {
								sResponse = REQUEST_REJECTED;
								logger.info(className, sMethodName,"The request has been rejected");
							}
							else if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(OPEN))) {
								sAPProxyUserProvisionManager.auditTrail(sResponseID,htCUPITRattributes, hmCUPConstants);
								sResponse = REQUEST_CREATION_SUCCESSFUL;
								logger.info(className, sMethodName,"The request has been submitted");
							}
							else if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(CLOSED))) {
								sResponse = REQUEST_CLOSED_SUCCESSFUL;
								logger.info(className, sMethodName,"The request has been submitted and user is created");
							}
						}else {
							logger.error(className, sMethodName,"The request ID is null");
							throw new ConnectorException();
						}
					}// if user in not found in SAP system
					else {
						sResponse = USER_ALREADY_EXISTS;
						logger.error(className, sMethodName,"Create user failed as user already exists in target");
					}
				} else {
					sResponse = USER_IN_EXCLUSION_LIST;
					logger.info(className, sMethodName,
							"Account not provisioned as " + sUserID
							+ " is in exclusion list");
				}
			} else {
				sResponse = VALIDATION_FAILED;
				logger.info(className, sMethodName,
				"Account not provisioned as validation failed");
			}
		} catch (ConnectorException e) {
			logger.error(className, sMethodName, e.getMessage());
			if(e.getMessage().startsWith("Connection")){
				sResponse = CONNECTION_ERROR;	
			}
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			if(StrUtil.isEmpty(sResponse))
				sResponse = USER_CREATION_FAILED;			
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			sResponse = USER_CREATION_FAILED;
		}
		logger.info(className, sMethodName, sResponse);
		logger.setMethodFinishLog(className, sMethodName);
		return sResponse;
	}

	/**
	 * Description:Used to modify a user on the target system
	 * 
	 * @param sUserID
	 *            User ID of the user to be modified. 
	 *            For example: John.Doe
	 * @param sFieldName
	 *            SAP field name OR CUP Field name
	 *            For example: TEL1_NUMBR OR 
	 *            CUP Telephone Number
	 * @param sBAPIStructure
	 *            SAP Structure name
	 *            For example: ADDRESS
	 * 
	 * @return String Returns the response code that is mapped in the adapter 
	 * @throws ConnectorException
	 */
	public String modifyUser(String sUserID, String sFieldName
	) throws ConnectorException {
		String sResponse = null;
		HashMap<String, ArrayList<SAPUMAttributeMapBean>> attrMap = new HashMap<String, ArrayList<SAPUMAttributeMapBean>>();

		String sMethodName = "modifyUser()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		// Merge CUP connector change starts
		String sCUPAttrMapLookupName = null;
		HashMap<String, String> hmCUPAttrMap = new HashMap<String, String>();
		String sReqType = null;
		String sApplication = null;
		String sPriority = null;
		// Merge CUP connector change ends
		try {
			if (!isInnitialized) {
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Innitialization failed");
				throw new ConnectorException(sResponse);
			}
			if (StrUtil.isEmpty(sUserID) || StrUtil.isEmpty(sFieldName)) {
				logger.error(className, sMethodName,
				"Incorrect information sent to method");
				sResponse = INSUFFICIENT_INFORMATION;
				throw new ConnectorException(sResponse);
			}

			SAPCUPProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPCUPProxyUserProvisionManager(
					hmConstants, configurationMap, htITRattributes);
			sCUPAttrMapLookupName = hmCUPConfig.get(PARENT_ATTR_LOOKUP);
			logger.debug(className, sMethodName, PARENT_ATTR_LOOKUP
					+ ":" + sCUPAttrMapLookupName);
			sReqType = hmCUPConfig.get(MODIFY_USER);
			logger.debug(className, sMethodName, "Request type for modify user"
					+ ":" + sReqType);
			sApplication = hmCUPConfig.get(APPLICATION);
			logger.debug(className, sMethodName, APPLICATION
					+ ":" + sApplication);
			sPriority = hmCUPConfig.get(PRIORITY);
			logger.debug(className, sMethodName, PRIORITY
					+ ":" + sPriority);
			logger.debug(className, sMethodName, "Performing attribute mapping for CUP");
			hmCUPAttrMap = populateCUPUserDataToHash(sCUPAttrMapLookupName,
					sFieldName);

			if (sAPProxyUserProvisionManager.findUser(sUserID)) {
				logger.info(className, sMethodName,
						"User Existence Check Complete");

				// Merge CUP connector change starts
				String sResponseID = sAPProxyUserProvisionManager
				.createCUPRequest(hmCUPAttrMap, htCUPITRattributes,
						hmCUPConstants,null , sReqType, sApplication, sPriority);
				HashMap<String, String> hmUpdate = new HashMap<String, String>();
				if (sResponseID != null) {
					String sCUPStatus = sAPProxyUserProvisionManager
					.getStatus(sResponseID, htCUPITRattributes,
							hmCUPConstants);
					logger.debug(className, sMethodName,
							"Starting update to process form");
					hmUpdate.put(hmCUPConstants.get(UD_REQUEST_ID),
							sResponseID);
					hmUpdate.put(hmCUPConstants.get(UD_REQUEST_STATUS),
							sCUPStatus);
					fiIntf.setProcessFormData(lProcInstanceKey,
							hmUpdate);
					logger.debug(className, sMethodName,
							"Process form updated");
					if (sCUPStatus.equalsIgnoreCase(hmCUPConstants
							.get(REJECT))) {
						sResponse = REQUEST_REJECTED;
					} else if (sCUPStatus
							.equalsIgnoreCase(hmCUPConstants.get(OPEN))) {
						sAPProxyUserProvisionManager.auditTrail(
								sResponseID, htCUPITRattributes,
								hmCUPConstants);
						sResponse = REQUEST_CREATION_SUCCESSFUL;
					} else if (sCUPStatus
							.equalsIgnoreCase(hmCUPConstants
									.get(CLOSED))) {
						sAPProxyUserProvisionManager.auditTrail(
								sResponseID, htCUPITRattributes,
								hmCUPConstants);
						sResponse = REQUEST_CLOSED_SUCCESSFUL;
					}
				} else {
					logger.error(className, sMethodName,
							"Response ID is null");
				}
			} else {
				sResponse = USER_DOES_NOT_EXIST;
				logger.error(className, sMethodName, "User id :" + sUserID
						+ " does not exist in target SAP system.");
			}
		} catch (ConnectorException bException) {
			logger.error(className, sMethodName, bException.getMessage());
			if (bException.getMessage().startsWith("Connection")) {
				sResponse = CONNECTION_ERROR;
			}
			logger.setStackTrace(bException, className, sMethodName, bException
					.getMessage());
			if (StrUtil.isEmpty(sResponse))
				sResponse = USER_MODIFICATION_FAILED;
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			if (StrUtil.isEmpty(sResponse))
				sResponse = USER_MODIFICATION_FAILED;
		} 
		logger.setMethodFinishLog(className, sMethodName);
		logger.info(className, sMethodName, sResponse);
		return sResponse;
	}

	/**
	 * Description : This method modifies Lock / Unlock status of the user in
	 * target SAP System
	 * 
	 * @param sUserId
	 *            User which gets locked/unlocked. For example: John.Doe
	 * @param sLockUnLockValue
	 *            The value which decides whether the user should be locked or
	 *            unlocked.(Possible values are Lock/UnLock)
	 * @return String Returns the response code which is mapped in the Adapter.
	 *         Returns the response code which is mapped in the Adapter.
	 * 
	 * @throws ConnectorException
	 */
	public String modifyLockUnlockUser(String sUserId, String sLockUnLockValue)
	throws ConnectorException {
		String sMethodName = "modifyLockUnlockUser()";
		logger.setMethodStartLog(className, sMethodName);
		String sResponse = null;

		String sCUPAttrMapLookupName = null;
		HashMap<String, String> hmCUPAttrMap = new HashMap<String, String>();
		String sReqType = null;
		String sApplication = null;
		String sPriority = null;

		try {
			if(!isInnitialized){
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Innitialization failed");
				throw new ConnectorException(sResponse);
			}

			if (StrUtil.isEmpty(sUserId) || StrUtil.isEmpty(sLockUnLockValue) ) {
				logger.error(className, sMethodName, "Mandatory values not sent to method");
				sResponse= INSUFFICIENT_INFORMATION;
				throw new ConnectorException(INSUFFICIENT_INFORMATION);
			}
	
			sCUPAttrMapLookupName = hmCUPConfig.get(PARENT_ATTR_LOOKUP);
			logger.debug(className, sMethodName, PARENT_ATTR_LOOKUP
					+ ":" + sCUPAttrMapLookupName);
			if (sLockUnLockValue.equals(hmConstants.get(ONE))) {
				sReqType = hmCUPConfig.get(LOCK_USER);
			}else {
				sReqType = hmCUPConfig.get(UNLOCK_USER);
			}			
			logger.debug(className, sMethodName, "Request type for Lock/UnLock user"
					+ ":" + sReqType);
			//sApplication = hmCUPConfig.get(APPLICATION);
			sApplication = hmFormData.get(APPLICATION_NAME).toString();
			
			if (StrUtil.isEmpty(sApplication)) {
				logger.error(className, sMethodName, "Mandatory values not sent to method");
				sResponse= INSUFFICIENT_INFORMATION;
				throw new ConnectorException(INSUFFICIENT_INFORMATION);
			}
			
			logger.debug(className, sMethodName, APPLICATION
					+ ":" + sApplication);
			sPriority = hmCUPConfig.get(PRIORITY);
			logger.debug(className, sMethodName, PRIORITY
					+ ":" + sPriority);

			SAPCUPProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPCUPProxyUserProvisionManager(
					hmConstants, configurationMap, htITRattributes);
			hmCUPAttrMap = populateCUPUserDataToHash(sCUPAttrMapLookupName,null);

			if (sAPProxyUserProvisionManager.findUser(sUserId)) {
				String sResponseID = sAPProxyUserProvisionManager
				.createCUPRequest(hmCUPAttrMap, htCUPITRattributes,
						hmCUPConstants,null , sReqType, sApplication, sPriority);
				HashMap<String, String> hmUpdate = new HashMap<String, String>();
				if (sResponseID != null) {
					String sCUPStatus = sAPProxyUserProvisionManager
					.getStatus(sResponseID, htCUPITRattributes, hmCUPConstants);
					logger.debug(className, sMethodName, "Starting update to process form");
					hmUpdate.put(hmCUPConstants.get(UD_REQUEST_ID), sResponseID);
					hmUpdate.put(hmCUPConstants.get(UD_REQUEST_STATUS), sCUPStatus);
					fiIntf.setProcessFormData(lProcInstanceKey, hmUpdate);
					logger.debug(className, sMethodName, "Process form updated");
					if (sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(REJECT))) {
						sResponse = REQUEST_REJECTED;
						logger.info(className, sMethodName, "Request rejected");
					} else if (sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(OPEN))) {
						sAPProxyUserProvisionManager.auditTrail(
								sResponseID, htCUPITRattributes, hmCUPConstants);
						sResponse = REQUEST_CREATION_SUCCESSFUL;
						logger.info(className, sMethodName, "Request creation successful");
					} else if (sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(CLOSED))) {
						sAPProxyUserProvisionManager.auditTrail(
								sResponseID, htCUPITRattributes, hmCUPConstants);
						if (sLockUnLockValue.equals(hmConstants.get(ONE))) {
							sResponse = USER_LOCKED_SUCCESSFUL;
						}else {
							sResponse = USER_UNLOCKED_SUCCESSFUL;
						}
						logger.info(className, sMethodName, "Request closed successful");
					}
				} else {
					logger.error(className, sMethodName,
					"Response ID is null");
				}
			} else {
				logger.error(className, sMethodName, "User id :" + sUserId
						+ " does not exist in target SAP system.");
				sResponse = USER_DOES_NOT_EXIST;
			}
		} catch (ConnectorException bException) {
			if (sLockUnLockValue.equals(hmConstants.get(ONE))) {
				logger.error(className, sMethodName,
						"Lock request Exception for user id: " + sUserId);
				if(StrUtil.isEmpty(sResponse))
					sResponse = USER_LOCK_FAILED;
			} else {
				logger.error(className, sMethodName,
						"UnLock request Exception for user id: " + sUserId);
				if(StrUtil.isEmpty(sResponse))
					sResponse = USER_UNLOCK_FAILED;
			}
			if(bException.getMessage().startsWith("Connection")){
				sResponse =CONNECTION_ERROR;
			}
		} catch (Exception exception) {
			if (sLockUnLockValue.equals(hmConstants.get(ONE))) {
				logger.error(className, sMethodName,
						"Lock request Exception for user id: " + sUserId);
				sResponse = USER_LOCK_FAILED;
			} else {
				logger.error(className, sMethodName,
						"UnLock request Exception for user id: " + sUserId);
				sResponse = USER_UNLOCK_FAILED;
			}
		} 
		logger.setMethodFinishLog(className, sMethodName);
		logger.info(className, sMethodName, sResponse);
		return sResponse;
	}


	/**
	 * Description:Used to disable a user on the target system. This method
	 * updates the ValidThro process form field, which in turn transfers the
	 * update to target system.
	 * 
	 * @param sUserID
	 *            User ID of the user to be modified For example: John.Doe
	 * @param ValidThroUD
	 *            UDField of the attribute to be set. For example:
	 *            UD_SAP_VALIDTHRO
	 * 
	 * @return String Returns the response code which is mapped in the adapter.
	 * 
	 * @throws ConnectorException
	 */
	public String disableUser(String sUserID, String ValidThroUD)
	throws ConnectorException {
		String sResponse = null;
		Date dtValidThro = null;

		String sMethodName = "disableUser()";
		logger.setMethodStartLog(className, sMethodName);
		logger.info(className, sMethodName, "Disable User: " + sUserID);
		try {

			if (!isInnitialized) {
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Innitialization failed");
				throw new ConnectorException(sResponse);
			}

			if (StrUtil.isEmpty(sUserID) && StrUtil.isEmpty(ValidThroUD)) {
				sResponse = INSUFFICIENT_INFORMATION;
				throw new ConnectorException(sResponse);
			}

			SAPCUPProxyUserProvisionManager sAPProxyUserProvisionManager = 
				new SAPCUPProxyUserProvisionManager(hmConstants, configurationMap, htITRattributes);


			if (sAPProxyUserProvisionManager.findUser(sUserID)) {
				// ValidThro in SAP system is set yesterdays date
				dtValidThro = new Date();
				long mymillidate=dtValidThro.getTime();
				//mymillidate=mymillidate - 24*60*60*1000;
				dtValidThro= new Date(mymillidate);
				String sDate = dtUtil.parseTime(dtValidThro, hmConstants
						.get(EnableDisableDF));
				dtValidThro = dtUtil.returnDate(sDate, hmConstants
						.get(EnableDisableDF));
				Timestamp tStart = new Timestamp(dtValidThro.getTime());
				String formattedDateTime = tStart.toString();
				HashMap<String, String> hmMap = new HashMap<String, String>();
				hmMap.put(ValidThroUD, formattedDateTime);
				fiIntf.setProcessFormData(lProcInstanceKey, hmMap);
				sResponse = DISABLE_USER_SUCCESSFUL;	
			} else {
				logger.error(className, sMethodName, "UserId : " + sUserID
						+ " does not exist in target SAP system");
				sResponse = USER_DOES_NOT_EXIST;

			}
		} catch (ConnectorException Exception) {
			logger.error(className, sMethodName, "Disable User "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, Exception.getMessage());
			if(Exception.getMessage().startsWith("Connection")){
				sResponse =CONNECTION_ERROR;
			}
			if(StrUtil.isEmpty(sResponse))
				sResponse = DISABLE_USER_FAILED;
		} catch (tcAPIException e) {
			logger.error(className, sMethodName, "Disable User "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, e.getMessage());
			sResponse = DISABLE_USER_FAILED;
		} catch (tcInvalidValueException e) {
			logger.error(className, sMethodName, "Disable User "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, e.getMessage());
			sResponse = DISABLE_USER_FAILED;
		} catch (tcNotAtomicProcessException e) {
			logger.error(className, sMethodName, "Disable User "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, e.getMessage());
			sResponse = DISABLE_USER_FAILED;
		} catch (tcFormNotFoundException e) {
			logger.error(className, sMethodName, "Disable User "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, e.getMessage());
			sResponse = DISABLE_USER_FAILED;
		} catch (tcRequiredDataMissingException e) {
			logger.error(className, sMethodName, "Disable User "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, e.getMessage());
			sResponse = DISABLE_USER_FAILED;
		} catch (tcProcessNotFoundException e) {
			logger.error(className, sMethodName, "Disable User "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, e.getMessage());
			sResponse = DISABLE_USER_FAILED;
		} 
		logger.setMethodFinishLog(className, sMethodName);
		logger.info(className, sMethodName, sResponse);
		return sResponse;
	}

	/**
	 * Description:Used to enable a user on the target system. This method
	 * updates the ValidThro process form field, which in turn transfers the
	 * update to target system.
	 * 
	 * @param sUserID
	 *            User ID of the user to be modified For example: John.Doe
	 * @param ValidThroUD
	 *            UDField of the attribute to be set. For example:
	 *            UD_SAP_VALIDTHRO
	 * 
	 * @return String Returns the response code that is mapped in the adapter.
	 * 
	 * @throws ConnectorException
	 */
	public String enableUser(String sUserID, String ValidThroUD)
	throws ConnectorException {
		String sResponse = null;
		Date dtValidThro = null;
		String sMethodName = "enableUser()";
		logger.setMethodStartLog(className, sMethodName);
		logger.info(className, sMethodName, "Enable User: " + sUserID);

		try {

			if(!isInnitialized){
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Innitialization failed");
				throw new ConnectorException(sResponse);
			}

			if (StrUtil.isEmpty(sUserID) && StrUtil.isEmpty(ValidThroUD)) {
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName,
				"Incorrect information sent to method");
				throw new ConnectorException(sResponse);
			}

			SAPCUPProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPCUPProxyUserProvisionManager(
					hmConstants, configurationMap, htITRattributes);

			if (sAPProxyUserProvisionManager.findUser(sUserID)) {
				// update validThro to max date so as to enable it
				dtValidThro = dtUtil.returnDate(hmConstants.get(EnableDate), hmConstants.get(EnableDisableDF));
				String sDate = dtUtil.parseTime(dtValidThro, hmConstants.get(EnableDisableDF));
				dtValidThro = dtUtil.returnDate(sDate, hmConstants.get(EnableDisableDF));
				Timestamp tStart = new Timestamp(dtValidThro.getTime());
				String formattedDateTime = tStart.toString();
				HashMap<String, String> hmMap = new HashMap<String, String>();
				hmMap.put(ValidThroUD, formattedDateTime);

				fiIntf.setProcessFormData(lProcInstanceKey, hmMap);
				sResponse = ENABLE_USER_SUCCESSFUL;				

			} else {
				logger.error(className, sMethodName, "UserId : " + sUserID
						+ " does not exist in target SAP system");
				sResponse = USER_DOES_NOT_EXIST;

			}
		} catch (ConnectorException Exception) {
			logger.error(className, sMethodName, "Enable User "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, Exception.getMessage());
			if(Exception.getMessage().startsWith("Connection")){
				sResponse =CONNECTION_ERROR;
			}
			if(StrUtil.isEmpty(sResponse))
				sResponse = ENABLE_USER_FAILED;
		} catch (tcAPIException e) {
			logger.error(className, sMethodName, "Enable User "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, e.getMessage());
			sResponse = ENABLE_USER_FAILED;
		} catch (tcInvalidValueException e) {
			logger.error(className, sMethodName, "Enable User "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, e.getMessage());
			sResponse = ENABLE_USER_FAILED;
		} catch (tcNotAtomicProcessException e) {
			logger.error(className, sMethodName, "Enable User "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, e.getMessage());
			sResponse = ENABLE_USER_FAILED;
		} catch (tcFormNotFoundException e) {
			logger.error(className, sMethodName, "Enable User "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, e.getMessage());
			sResponse = ENABLE_USER_FAILED;
		} catch (tcRequiredDataMissingException e) {
			logger.error(className, sMethodName, "Enable User "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, e.getMessage());
			sResponse = ENABLE_USER_FAILED;
		} catch (tcProcessNotFoundException e) {
			logger.error(className, sMethodName, "Enable User "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, e.getMessage());
			sResponse = ENABLE_USER_FAILED;
		} 
		logger.setMethodFinishLog(className, sMethodName);
		logger.info(className, sMethodName, sResponse);
		return sResponse;
	}

	/**
	 * Description:Used to delete a user on the target system 
	 * 
	 * @param sUserID
	 *            User ID of the user to be modified
	 * 			  For example: John.Doe
	 * @return String Returns the response code that is mapped in the adapter 
	 * @throws ConnectorException
	 */
	public String deleteUser(String sUserID) throws ConnectorException {
		String sResponse = null;
		String sMethodName = "deleteUser()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger,hmConstants);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		//Merge CUP connector change starts 
		String sCUPAttrMapLookupName = null; 
		HashMap<String, String> hmCUPAttrMap = new HashMap<String, String>();
		String sReqType= null;
		String sApplication= null;
		String sPriority = null;
		//Merge CUP connector change ends

		try {
			if (StrUtil.isEmpty(sUserID)) {
				logger.error(className, sMethodName, "User Id can't be null");
				sResponse = INSUFFICIENT_INFORMATION;
				throw new ConnectorException(sResponse);
			}
			if(!isInnitialized){
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Innitialization failed");
				throw new ConnectorException(sResponse);
			}


			SAPCUPProxyUserProvisionManager sAPProxyUserProvisionManager 
			= new SAPCUPProxyUserProvisionManager(hmConstants, configurationMap, htITRattributes);

			sCUPAttrMapLookupName = hmCUPConfig.get(PARENT_ATTR_LOOKUP);
			logger.debug(className, sMethodName, PARENT_ATTR_LOOKUP
					+ ":" + sCUPAttrMapLookupName);
			sReqType = hmCUPConfig.get(DELETE_USER);
			logger.debug(className, sMethodName, "Request type for delete user"
					+ ":" + sReqType);
			sApplication = hmCUPConfig.get(APPLICATION);
			logger.debug(className, sMethodName, APPLICATION
					+ ":" + sApplication);
			sPriority = hmCUPConfig.get(PRIORITY);
			logger.debug(className, sMethodName, PRIORITY
					+ ":" + sPriority);
			hmCUPAttrMap = populateCUPUserDataToHash(sCUPAttrMapLookupName, null);
			logger.debug(className, sMethodName, "Innitialized parent attribute map");

			if (sAPProxyUserProvisionManager.findUser(sUserID)) {
				logger.info("User Existence Check Complete");

				String sResponseID = sAPProxyUserProvisionManager.createCUPRequest(
						hmCUPAttrMap, htCUPITRattributes, hmCUPConstants ,null , sReqType, sApplication, sPriority);
				HashMap<String,String> hmUpdate = new HashMap<String,String>();
				if(sResponseID != null) {
					String sCUPStatus = sAPProxyUserProvisionManager.getStatus(sResponseID,htCUPITRattributes, hmCUPConstants);
					logger.debug(className, sMethodName, "Starting update to process form");
					hmUpdate.put(hmCUPConstants.get(UD_REQUEST_ID), sResponseID);
					hmUpdate.put(hmCUPConstants.get(UD_REQUEST_STATUS), sCUPStatus);
					fiIntf.setProcessFormData(lProcInstanceKey, hmUpdate);
					logger.debug(className, sMethodName, "Process form updated");
					/*
					 * Check if account is deleted in SAP.If so then return
					 * true to delete the account in OIM
					 */
					//TODO  Convert to SPML call
					//boolean isNotExists = oUMUtil.findUser(jcoFunction);
					boolean isNotExists = false;
					if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(REJECT))) {
						sResponse = REQUEST_REJECTED;
					}
					else if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(OPEN))) {
						sAPProxyUserProvisionManager.auditTrail(sResponseID,htCUPITRattributes, hmCUPConstants);
						sResponse = REQUEST_CREATION_SUCCESSFUL;
						logger.info(className, sMethodName,"The request has been submitted");
					}
					else if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(CLOSED)) && isNotExists) {
						sResponse = REQUEST_CLOSED_SUCCESSFUL;
						logger.info(className, sMethodName,"The request has been submitted and user is deleted successfully");
					}
					else if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(CLOSED)) && !isNotExists) {
						sAPProxyUserProvisionManager.auditTrail(sResponseID, htCUPITRattributes, hmCUPConstants);
						logger.error(className, sMethodName, "The request has been submitted but User not deleted in backend");
						sResponse = REQUEST_CREATION_FAILED;
					}
				}else {
					logger.error(className, sMethodName,"The request ID is null");
					throw new ConnectorException();
				}
			} else {
				sResponse = USER_DOES_NOT_EXIST;
				logger.error(className, sMethodName, "User id :" + sUserID
						+ " does not exist in target SAP system.");
			}
		} catch (ConnectorException bException) {
			logger.error(className, sMethodName,
					"Delete Account Exception for user id :" + sUserID);
			if(bException.getMessage().startsWith("Connection")){
				sResponse =CONNECTION_ERROR;
			}
			logger.error(className, sMethodName, bException.getMessage());
		} catch (Exception e) {
			logger.error(className, sMethodName,
					"Delete Account Base Exception for user id :" + sUserID);
			logger.error(className, sMethodName, e.getMessage());
		} 
		logger.setMethodFinishLog(className, sMethodName);
		logger.info(className, sMethodName, sResponse);
		return sResponse;
	}

	/**
	 * Description:This method is used to add a multi-value attribute to a user,
	 * like role, profile, parameters, etc..
	 * 
	 * @param sUserID
	 *            User ID of the user to be modified. For example: John.Doe
	 * @param sAttributeName
	 *            Key multi-value Attribute value in the child form. For example:
	 *            1~E60~V_SYS_ADMIN
	 * @param sChildPrimaryKey
	 *            UDField of key child Attribute. For example: UD_SAP_PRO
	 * @param sChildTableName
	 *            Child form table name. For example: UD_SAP
	 * @param sBapiFieldName
	 *            SAP field name of Key multivalued attribute in the child form.
	 *            For example: BAPIPROF
	 * @param sBapiStructureName
	 *            Structure name in the BAPI. Within this the BAPI field name
	 *            exists. For example: PROFILE
	 * 
	 * @return String Returns the response code which is mapped in the Adapter.
	 * @throws ConnectorException
	 */
	public String addMultiValueData(String sUserID, String sAttributeName,
			String sChildPrimaryKey, String sChildTableName)
	throws ConnectorException {
		String sResponse = null;
		String sMethodName = "addMultiValueData()";
		logger.setMethodStartLog(className, sMethodName);
		HashMap<String, ArrayList<SAPUMAttributeMapBean>> attrMap = new HashMap<String, ArrayList<SAPUMAttributeMapBean>>();
		String sCUPAttrMapLookupName = null; 
		String sCUPChildAttrMapLookupName = null; 
		HashMap<String, String> hmCUPAttrMap = new HashMap<String, String>();
		HashMap<String, String> hmCUPChildAttrMap = new HashMap<String, String>();
		String sReqType= null;
		String sApplication= null;
		String sPriority = null;
		String isIgnoreUserCreatedCheck = null;

		try {
			if(!isInnitialized){
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Innitialization failed");
				throw new ConnectorException(sResponse);
			}

			if (StrUtil.isEmpty(sUserID) || StrUtil.isEmpty(sAttributeName)
					|| StrUtil.isEmpty(sChildPrimaryKey)
					|| StrUtil.isEmpty(sChildTableName)) {
				logger.error(className, sMethodName,
				"Insufficient Information : Provide compulsory fields.");
				logger.error(className, sMethodName, "userId :" + sUserID
						+ ", Attr Name:" + sAttributeName + ", ChildPrimaryKey:"
						+ sChildPrimaryKey + ", ChildTableName:" + sChildTableName);
				sResponse = INSUFFICIENT_INFORMATION;
				throw new ConnectorException(sResponse);
			}

			String sAttrMapLookupName = configurationMap
			.get(PROV_CHILD_ATTR_MAP);

			SAPCUPProxyUserProvisionManager sAPProxyUserProvisionManager 
			= new SAPCUPProxyUserProvisionManager(hmConstants, configurationMap, htITRattributes);

			sCUPAttrMapLookupName = hmCUPConfig.get(PARENT_ATTR_LOOKUP);
			logger.debug(className, sMethodName, PARENT_ATTR_LOOKUP
					+ ":" + sCUPAttrMapLookupName);
			sCUPChildAttrMapLookupName = hmCUPConfig.get(CHILD_ATTR_LOOKUP);
			logger.debug(className, sMethodName, CHILD_ATTR_LOOKUP
					+ ":" + sCUPChildAttrMapLookupName);
			sReqType = hmCUPConfig.get(ASSIGN_ROLE);
			logger.debug(className, sMethodName, "Request type for modify user"
					+ ":" + sReqType);
			sApplication = hmCUPConfig.get(APPLICATION);
			logger.debug(className, sMethodName, APPLICATION
					+ ":" + sApplication);
			sPriority = hmCUPConfig.get(PRIORITY);
			logger.debug(className, sMethodName, PRIORITY
					+ ":" + sPriority);
			isIgnoreUserCreatedCheck = hmCUPConfig.get(IGNORE_CHECK);
			logger.debug(className, sMethodName, IGNORE_CHECK
					+ ":" + isIgnoreUserCreatedCheck);

			hmCUPAttrMap = populateCUPUserDataToHash(sCUPAttrMapLookupName,null);
			hmCUPChildAttrMap = populateCUPChildDataToCreate(sCUPChildAttrMapLookupName,
					sAttributeName, sChildPrimaryKey, sChildTableName);
			hmCUPChildAttrMap.put(hmCUPConstants.get(SYSID), hmFormData.get(APPLICATION_NAME).toString());
			if (isIgnoreUserCreatedCheck.equalsIgnoreCase(YES)
					|| sAPProxyUserProvisionManager.findUser(sUserID)) {

				String sResponseID = sAPProxyUserProvisionManager.createCUPRequest(
						hmCUPAttrMap, htCUPITRattributes, hmCUPConstants, hmCUPChildAttrMap , sReqType, sApplication, sPriority);
				HashMap<String,String> hmUpdate = new HashMap<String,String>();
				if(sResponseID != null) {
					String sCUPStatus = sAPProxyUserProvisionManager.getStatus(sResponseID,htCUPITRattributes, hmCUPConstants);
					logger.debug(className, sMethodName, "Starting update to process form");
					hmUpdate.put(hmCUPConstants.get(UD_REQUEST_ID), sResponseID);
					hmUpdate.put(hmCUPConstants.get(UD_REQUEST_STATUS), sCUPStatus);
					fiIntf.setProcessFormData(lProcInstanceKey, hmUpdate);
					logger.debug(className, sMethodName, "Process form updated");
					if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(REJECT))) {
						sResponse = REQUEST_REJECTED;
					}
					else if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(OPEN))) {
						sAPProxyUserProvisionManager.auditTrail(sResponseID,htCUPITRattributes, hmCUPConstants);
						sResponse = REQUEST_CREATION_SUCCESSFUL;
						logger.info(className, sMethodName,"The request has been submitted");
					}
					else if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(CLOSED))) {
						sResponse = REQUEST_CLOSED_SUCCESSFUL;
						logger.info(className, sMethodName,"The request has been submitted and closed");
					}
				}else {
					logger.error(className, sMethodName,"The request ID is null");
					throw new ConnectorException();
				}
			} else {
				sResponse = USER_DOES_NOT_EXIST;
				logger.error(className, sMethodName, "User id :" + sUserID
						+ " does not exist in target SAP system.");
			}
		} catch (ConnectorException exception) {
			if(StrUtil.isEmpty(sResponse))
				sResponse = ADD_MULTIVALUE_DATA_FAILED;
			if(exception.getMessage().startsWith("Connection")){
				sResponse =CONNECTION_ERROR;
			}
			logger.error(className, sMethodName, exception.getMessage());
		} catch (Exception e) {
			if(StrUtil.isEmpty(sResponse))
				sResponse = ADD_MULTIVALUE_DATA_FAILED;
			logger.error(className, sMethodName, e.getMessage());
		}
		logger.setMethodFinishLog(className, sMethodName);
		logger.info(className, sMethodName, sResponse);
		return sResponse;
	}

	/**
	 * Description:Used to remove a multivalued attribute from a user, such as
	 * role, profile, or parameter.
	 * 
	 * @param sUserID
	 *            User ID of the user to be modified. For example: John.Doe
	 * @param sAttributeName
	 *            MultiValued Attribute value in the child form which is to be
	 *            deleted. For example: 1~E60~V_SYS_ADMIN
	 * @param sChildPrimaryKey
	 *            SAP field name of Key multivalued attribute in the child form.
	 *            For example: BAPIPROF
	 * @return String Returns the response code that is mapped in the adapter
	 * @throws ConnectorException
	 */
	public String removeMultivalueData(String sUserID, String sAttributeName,
			String sChildPrimaryKey)
	throws ConnectorException {
		String sResponse = null;
		String sMethodName = "removeMultivalueData()";
		logger.setMethodStartLog(className, sMethodName);

		String sCUPAttrMapLookupName = null; 
		HashMap<String, String> hmCUPAttrMap = new HashMap<String, String>();
		HashMap<String, String> hmCUPChildAttrMap = new HashMap<String, String>();
		String sReqType= null;
		String sApplication= null;
		String sPriority = null;

		try {
			if(!isInnitialized){
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Innitialization failed");
				throw new ConnectorException(sResponse);
			}

			if (StrUtil.isEmpty(sUserID) || StrUtil.isEmpty(sAttributeName)
					|| StrUtil.isEmpty(sChildPrimaryKey)) {
				logger.error(className, sMethodName,
				"Incorrect information sent to method");
				sResponse = INSUFFICIENT_INFORMATION;
				throw new ConnectorException(sResponse);
			}

			SAPCUPProxyUserProvisionManager sAPProxyUserProvisionManager 
			= new SAPCUPProxyUserProvisionManager(hmConstants, configurationMap, htITRattributes);
			sCUPAttrMapLookupName = hmCUPConfig.get(PARENT_ATTR_LOOKUP);
			logger.debug(className, sMethodName, PARENT_ATTR_LOOKUP
					+ ":" + sCUPAttrMapLookupName);
			sReqType = hmCUPConfig.get(ASSIGN_ROLE);
			logger.debug(className, sMethodName, "Request type for assign role"
					+ ":" + sReqType);
			sApplication = hmCUPConfig.get(APPLICATION);
			logger.debug(className, sMethodName, APPLICATION
					+ ":" + sApplication);
			sPriority = hmCUPConfig.get(PRIORITY);
			logger.debug(className, sMethodName, PRIORITY
					+ ":" + sPriority);

			if (sAPProxyUserProvisionManager.findUser(sUserID)) {
				logger.debug(className, sMethodName, "Performing attribute mapping for CUP");
				hmCUPAttrMap = populateCUPUserDataToHash(sCUPAttrMapLookupName,null);
				StringTokenizer st = new StringTokenizer(sAttributeName, "~");
				String[] arr = new String[st.countTokens()];
				int i = 0;
				while(st.hasMoreTokens()){
					arr[i] = st.nextToken();
					i++;
				}
				logger.debug(className, sMethodName, "Populating CUP child hashmap");
				//hmCUPChildAttrMap.put(hmCUPConstants.get(SYSID), sApplication);
				hmCUPChildAttrMap.put(hmCUPConstants.get(SYSID), hmFormData.get(APPLICATION_NAME).toString());
				hmCUPChildAttrMap.put(hmCUPConstants.get(ROLEID), arr[1]);
				hmCUPChildAttrMap.put(hmCUPConstants.get(ACTION), hmCUPConstants.get(REMOVE));
				String sResponseID = sAPProxyUserProvisionManager.createCUPRequest(
						hmCUPAttrMap, htCUPITRattributes,hmCUPConstants ,hmCUPChildAttrMap , sReqType, sApplication, sPriority);
				HashMap<String,String> hmUpdate = new HashMap<String,String>();
				if(sResponseID != null) {
					String sCUPStatus = sAPProxyUserProvisionManager.getStatus(sResponseID,htCUPITRattributes, hmCUPConstants);
					logger.debug(className, sMethodName, "Starting update to process form");
					logger.debug(className, sMethodName, "Starting update to process form");
					hmUpdate.put(hmCUPConstants.get(UD_REQUEST_ID), sResponseID);
					hmUpdate.put(hmCUPConstants.get(UD_REQUEST_STATUS), sCUPStatus);
					fiIntf.setProcessFormData(lProcInstanceKey, hmUpdate);
					logger.debug(className, sMethodName, "Process form updated");
					logger.debug(className, sMethodName, "Process form updated");
					if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(REJECT))) {
						sResponse = REQUEST_REJECTED;
					}
					else if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(OPEN))) {
						sAPProxyUserProvisionManager.auditTrail(sResponseID,htCUPITRattributes, hmCUPConstants);
						sResponse = REQUEST_CREATION_SUCCESSFUL;
						logger.info(className, sMethodName,"The request has been submitted");
					}
					else if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(CLOSED))) {
						sResponse = REQUEST_CLOSED_SUCCESSFUL;
						logger.info(className, sMethodName,"The request has been submitted and closed");
					}
				}else {
					logger.error(className, sMethodName,"The request ID is null");
					throw new ConnectorException();
				}

			} else {
				sResponse = USER_DOES_NOT_EXIST;
				logger.error(className, sMethodName, "User id :" + sUserID
						+ " does not exist in target SAP system.");
			}
		} catch (ConnectorException sException) {
			if(StrUtil.isEmpty(sResponse))
				sResponse = MULTI_DATA_REMOVE_FAILED;

			if(sException.getMessage().startsWith("Connection")){
				sResponse =CONNECTION_ERROR;
			}
			logger.error(className, sMethodName, sException.getErrorMessage());
		} catch (Exception e) {
			if(StrUtil.isEmpty(sResponse))
				sResponse = MULTI_DATA_REMOVE_FAILED;
			logger.error(className, sMethodName, e.getMessage());
		} 
		logger.setMethodFinishLog(className, sMethodName);
		logger.info(className, sMethodName, sResponse);
		return sResponse;
	}

	/**
	 * Description:This method is used to create a HashMap of attribute mapped
	 * values for parent form
	 * 
	 * @param sAttrMapLookupName
	 *            Parent form attribute map lookup name
	 * 
	 * @return HashMap<String,ArrayList<SAPUMAttributeMapBean>>
	 * @throws ConnectorException
	 */
	private HashMap<String, String> populateCUPUserDataToHash(
			String sAttrMapLookupName,String sFieldToValidate) throws ConnectorException {
		HashMap<String, String> attrMap = new HashMap<String, String>();
		String sCode = null;
		String sDecode = null;
		String sFormField = null;
		String sFormValue = null;
		boolean isCheckIfCUP = false;
		String sMethodName = "populateCUPUserDataToHash()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			tcResultSet formInstResults = fiIntf
			.getProcessFormData(lProcInstanceKey);

			long lFormDefKey = fiIntf
			.getProcessFormDefinitionKey(lProcInstanceKey);
			tcResultSet formDefResults = fdIntf.getFormFields(lFormDefKey,
					fiIntf.getProcessFormVersion(lProcInstanceKey));

			tcResultSet lookupResults = lookIntf
			.getLookupValues(sAttrMapLookupName);

			int ilookupResCount = lookupResults.getRowCount();
			int iformDefResCount = formDefResults.getRowCount();

			for (int iIndex = 0; iIndex < ilookupResCount; iIndex++) {
				lookupResults.goToRow(iIndex);

				for (int iCount = 0; iCount < iformDefResCount; iCount++) {
					formDefResults.goToRow(iCount);

					sCode = lookupResults.getStringValue(LOOKUP_CODE);
					sDecode = lookupResults.getStringValue(LOOKUP_DECODE);				
					String[] sDecodeVal = sDecode.split(";");

					if(!StrUtil.isEmpty(sFieldToValidate)) {
						isCheckIfCUP =true;
					}
					sFormField = formDefResults
					.getStringValue(STRUCT_UTIL_FIELD_LABEL);
					String sStd = sDecodeVal[2];
					if (sFormField.equalsIgnoreCase(sCode.trim())) {
						sFormValue = formInstResults
						.getStringValue(formDefResults
								.getStringValue(STRUCT_UTIL_COLUMN_NAME));
						if(sDecodeVal[1].equalsIgnoreCase(hmCUPConstants.get(DATE))) {
							DateFormat dateFormat = new SimpleDateFormat(hmCUPConstants.get(CUP_DATE_FORMAT));
							if(!StrUtil.isEmpty(sFormValue)) {
								//EnableDisableDF is actually OIM date format
								Date dtFormValue = dtUtil.returnDate(sFormValue, hmConstants.get(EnableDisableDF));
								sFormValue = dateFormat.format(dtFormValue);
							}else if(StrUtil.isEmpty(sFormValue)) {
								Calendar calendar = Calendar.getInstance();
								sFormValue = dateFormat.format(calendar.getTime());
							}
						}
						/*We check if it is Standard and a mandatory before we add it to HashMap.
						 * Otherwise it should be the field currently being modified  
						 * */
						if(sStd.equalsIgnoreCase(STANDARD) && (sDecodeVal[4].equalsIgnoreCase(MANDATORY) 
								|| isCheckIfCUP)) {
							attrMap.put(sDecodeVal[0],sFormValue);
							iCount = iformDefResCount;
						}
						else if(sStd.equalsIgnoreCase(CUSTOM)) {
							// we set a marker if custom value is required, so
							// as to handle creating request
							attrMap.put(sStd +";"+ sDecodeVal[0],sFormValue);
							iCount = iformDefResCount;
						}
					}
				}
			}
			/*During modify operation, even though isCUP mode is enabled
			 * we need to verify if this field is in CUP attribute map else 
			 * we toggle the CUP mode to false  
			 * */
			if(!StrUtil.isEmpty(sFieldToValidate) && !isCheckIfCUP ) {
				logger.info(className, sMethodName, "Field not present in CUP attribute map");
				//isCUP = false;
			}

		} catch (tcProcessNotFoundException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} catch (tcAPIException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} catch (tcNotAtomicProcessException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} catch (tcFormNotFoundException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} catch (tcVersionNotFoundException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} catch (tcInvalidLookupException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} catch (tcColumnNotFoundException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		}catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		}
		logger.setMethodFinishLog(className, sMethodName);
		logger.info(className, sMethodName, attrMap.toString());
		return attrMap;
	}

	/**
	 * Description:This method is used to create a HashMap of attribute mapped
	 * values for child form
	 * 
	 * @param sLookupName
	 *            Child Attribute map lookup name
	 * @param sAttributeName
	 *            Key Attribute value in the child form
	 * @param sChildPrimaryKey
	 *            UDField of key child Attribute
	 * @param childTableName
	 *            Child table name
	 * @param attrMap
	 * 			   Attribute map containing parent details
	 * @return HashMap<String,String>
	 */
	private HashMap<String, String> populateCUPChildDataToCreate(
			String sLookupName, String sAttributeName, String sChildPrimaryKey,
			String childTableName) throws ConnectorException {
		String sCode = null;
		String sDecode = null;
		String sFormField = null;
		String sFormValue = null;
		int iEntryCount = 0;
		HashMap<String,String> attrMap = new HashMap<String, String>(); 
		String sMethodName = "populateCUPChildDataToCreate()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		OIMUtil OIMUtil = new OIMUtil(logger);
		try {

			long lChildFormDefKey = OIMUtil.getChildFormDefKey(
					lProcInstanceKey, childTableName);
			//this resultset contains the definitions of the child form
			tcResultSet childFormDef = fdIntf.getFormFields(lChildFormDefKey,
					(int) OIMUtil.getChildFormVersion(lProcInstanceKey,
							childTableName));
			//this contains child form process data values
			tcResultSet formInstChildData = fiIntf.getProcessFormChildData(
					lChildFormDefKey, lProcInstanceKey);

			tcResultSet lookupResults = lookIntf.getLookupValues(sLookupName);

			int iFormDataCount = formInstChildData.getRowCount();
			int iChildRecordCount = childFormDef.getRowCount();
			int iLookUpCount = lookupResults.getRowCount();
			//iterate thru number of child defn fields
			for (int iCount = 0; iCount < iFormDataCount; iCount++) {
				formInstChildData.goToRow(iCount);
				//condition to get only row that is added latest from OIM
				if (!sAttributeName.equalsIgnoreCase(formInstChildData
						.getStringValue(sChildPrimaryKey)))
					continue;
				iEntryCount++;
				if(iEntryCount>1) {
					logger.error(className, sMethodName, "Duplicate child added");
					throw new ConnectorException();
				}
				//code to checking value of primary key
				//iterate thru all reconciled multi-valued data results
				for (int jCount = 0; jCount < iLookUpCount; jCount++) {
					lookupResults.goToRow(jCount);
					//iterate thru child process data records 
					for (int kCount = 0; kCount < iChildRecordCount; kCount++) {
						childFormDef.goToRow(kCount);

						sCode = lookupResults.getStringValue(LOOKUP_CODE);
						sDecode = lookupResults.getStringValue(LOOKUP_DECODE);

						String[] sDecodeVal = sDecode.split(";");

						sFormField = childFormDef
						.getStringValue(STRUCT_UTIL_FIELD_LABEL);

						if (sFormField.equalsIgnoreCase(sCode.trim())) {
							sFormValue = formInstChildData
							.getStringValue(childFormDef
									.getStringValue(STRUCT_UTIL_COLUMN_NAME));

							if (!StrUtil.isEmpty(sFormValue)) {
								if (sDecodeVal[1].startsWith(hmConstants
										.get(FIELDTYPE_LOOKUP))) {
									sFormValue = oUMUtil
									.getFormattedAttributeValue(sFormValue);
								}
								else if(sDecodeVal[1].equalsIgnoreCase(hmCUPConstants.get(DATE))) {
									//EnableDisableDF is actually OIM date format
									Date dtFormValue = dtUtil.returnDate(sFormValue,hmConstants.get(EnableDisableDF));
									DateFormat dateFormat = new SimpleDateFormat(hmCUPConstants.get(CUP_DATE_FORMAT));
									sFormValue = dateFormat.format(dtFormValue);
								}
							}else if(StrUtil.isEmpty(sFormValue) && sDecodeVal[1].equalsIgnoreCase(hmCUPConstants.get(DATE))) {
								Calendar calendar = Calendar.getInstance();
								DateFormat dateFormat = new SimpleDateFormat(hmCUPConstants.get(CUP_DATE_FORMAT));
								sFormValue = dateFormat.format(calendar.getTime());
							}
							attrMap.put(sDecodeVal[0], sFormValue);
							kCount = iChildRecordCount;
						}
					}
				}
			}
		} catch (tcProcessNotFoundException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} catch (tcAPIException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} catch (tcFormNotFoundException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} catch (tcInvalidLookupException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} catch (tcColumnNotFoundException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		}catch (ConnectorException e) {
			logger.error(className, sMethodName, "Operation on mulivalued data failed");
			throw new ConnectorException();
		}catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		}
		logger.setMethodFinishLog(className, sMethodName);
		logger.info(className, sMethodName, attrMap.toString());
		return attrMap;
	}

	/**
	 * Description:This method is used to create a HashMap of attribute mapped
	 * values for child form
	 * 
	 * @param sLookupName
	 *            Child Attribute map lookup name
	 * @param sAttributeName
	 *            Key Attribute value in the child form
	 * @param sChildPrimaryKey
	 *            UDField of key child Attribute
	 * @param childTableName
	 *            Child table name
	 * 
	 * @return HashMap<String,ArrayList<SAPUMAttributeMapBean>>
	 */
	private HashMap<String, ArrayList<SAPUMAttributeMapBean>> populateChildDataToCreate(
			String sLookupName, String sAttributeName, String sChildPrimaryKey,
			String childTableName) throws ConnectorException {
		HashMap<String, ArrayList<SAPUMAttributeMapBean>> attrMap = new HashMap<String, ArrayList<SAPUMAttributeMapBean>>();
		String sCode = null;
		String sDecode = null;
		String sFormField = null;
		String sFormValue = null;
		String sStructureName = null;
		String sFieldName = null;
		ArrayList<SAPUMAttributeMapBean> userDataList = null;
		String sMethodName = "populateChildDataToCreate()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		OIMUtil OIMUtil = new OIMUtil(logger);
		try {

			long lChildFormDefKey = OIMUtil.getChildFormDefKey(
					lProcInstanceKey, childTableName);
			//this resultset contains the definitions of the child form
			tcResultSet childFormDef = fdIntf.getFormFields(lChildFormDefKey,
					(int) OIMUtil.getChildFormVersion(lProcInstanceKey,
							childTableName));
			//this contains child form process data values
			tcResultSet formInstChildData = fiIntf.getProcessFormChildData(
					lChildFormDefKey, lProcInstanceKey);

			tcResultSet lookupResults = lookIntf.getLookupValues(sLookupName);

			int iFormDataCount = formInstChildData.getRowCount();
			int iChildRecordCount = childFormDef.getRowCount();
			int iLookUpCount = lookupResults.getRowCount();
			//iterate thru number of child defn fields
			for (int iCount = 0; iCount < iFormDataCount; iCount++) {
				formInstChildData.goToRow(iCount);
				//condition to get only row that is added latest from OIM
				if (!sAttributeName.equalsIgnoreCase(formInstChildData
						.getStringValue(sChildPrimaryKey)))
					continue;
				//code to checking value of primary key
				//iterate thru all reconciled multi-valued data results
				for (int jCount = 0; jCount < iLookUpCount; jCount++) {
					lookupResults.goToRow(jCount);
					//iterate thru child process data records 
					for (int kCount = 0; kCount < iChildRecordCount; kCount++) {
						childFormDef.goToRow(kCount);

						sCode = lookupResults.getStringValue(LOOKUP_CODE);
						sDecode = lookupResults.getStringValue(LOOKUP_DECODE);

						String[] sDecodeVal = sDecode.split(";");

						sFieldName = sDecodeVal[1];
						if (sFieldName.contains("|")) {
							StringTokenizer st = new StringTokenizer(
									sFieldName, "|");
							int iTokenCount = st.countTokens();
							int i = 0;
							String[] formattedAttr = new String[iTokenCount];
							while (st.hasMoreTokens()) {
								formattedAttr[i++] = st.nextToken();
							}
						}
						sStructureName = sDecodeVal[2];

						sFormField = childFormDef
						.getStringValue(STRUCT_UTIL_FIELD_LABEL);

						if (sFormField.equalsIgnoreCase(sCode.trim())) {
							sFormValue = formInstChildData
							.getStringValue(childFormDef
									.getStringValue(STRUCT_UTIL_COLUMN_NAME));

							if (sDecodeVal[0].equalsIgnoreCase(hmConstants
									.get(FIELDTYPE_CHECKBOX))) {
								HashMap<String, String> hmCheckBoxMapping = oUtil
								.getLookUpMap((String) configurationMap
										.get(CHECK_BOX_LOOKUP));
								sFormValue = oUMUtil.getCheckBoxValueForSAP(
										sDecodeVal[1], sFormValue,
										hmCheckBoxMapping);
							}

							if (!StrUtil.isEmpty(sFormValue)) {
								if (sDecodeVal[0].startsWith(hmConstants
										.get(FIELDTYPE_LOOKUP))) {
									sFormValue = oUMUtil
									.getFormattedAttributeValue(sFormValue);
								}
							}

							if ((attrMap != null) && (sStructureName != null)
									&& attrMap.containsKey(sStructureName)) {
								userDataList = (ArrayList<SAPUMAttributeMapBean>) attrMap
								.get(sStructureName);
							} else {
								userDataList = new ArrayList<SAPUMAttributeMapBean>();
							}

							SAPUMAttributeMapBean sAPAttributeMapBean = new SAPUMAttributeMapBean();
							sAPAttributeMapBean.setFieldType(sDecodeVal[0]);
							sAPAttributeMapBean
							.setBapiStructure(sStructureName);
							sAPAttributeMapBean.setBapiFieldName(sFieldName);
							sAPAttributeMapBean.setFieldValue(sFormValue);
							if (sDecodeVal.length > 3) {
								sAPAttributeMapBean
								.setBapiFieldNameX(sDecodeVal[3]);
								sAPAttributeMapBean
								.setBapiStructureX(sDecodeVal[4]);
							}
							logger.info(" FieldName : " + sCode
									+ " FieldType : " + sDecodeVal[0]
									                               + " BapiStructure : " + sStructureName
									                               + " BapiFieldName : " + sFieldName
									                               + " FieldValue : " + sFormValue);

							userDataList.add(sAPAttributeMapBean);
							attrMap.put(sStructureName, userDataList);
							//stop loop
							kCount = iChildRecordCount;
						}
					}
				}
			}
		} catch (tcProcessNotFoundException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} catch (tcAPIException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} catch (tcFormNotFoundException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} catch (tcInvalidLookupException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		} catch (tcColumnNotFoundException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		}catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		}
		logger.setMethodFinishLog(className, sMethodName);
		logger.info(className, sMethodName, attrMap.toString());
		return attrMap;
	}

	public String modifyPassword(String sUserID, String sPassword) throws ConnectorException {
		String sResponse = null;
		String sDummyPassword = null;
		String sMethodName = "modifyPassword()";
		logger.setMethodStartLog(className, sMethodName);

		try {
			if(!isInnitialized){
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Innitialization failed");
				throw new ConnectorException(sResponse);
			}

			if (StrUtil.isEmpty(sUserID) || StrUtil.isEmpty(sPassword)) {
				logger.error(className, sMethodName,
				"Incorrect information sent to method");
				throw new ConnectorException(INSUFFICIENT_INFORMATION);
			}

			SAPCUPProxyUserProvisionManager sAPProxyUserProvisionManager 
			= new SAPCUPProxyUserProvisionManager(hmConstants, configurationMap, htITRattributes);
			
			if (sAPProxyUserProvisionManager.findUser(sUserID)) {
				sDummyPassword = htITRattributes.get(DUMMY_PASSWORD);
				sAPProxyUserProvisionManager.resetPassword(
						sUserID, sDummyPassword);
				sResponse = sAPProxyUserProvisionManager.modifyPassword(
						sUserID, sDummyPassword,sPassword);
			} else {
				sResponse = USER_DOES_NOT_EXIST;
				logger.error(className, sMethodName, "User id :" + sUserID
						+ " does not exist in target SAP system.");
			}
		} catch (ConnectorException bException) {
			sResponse = PASSWORD_CHANGE_FAILED;
		} catch (Exception e) {
			sResponse = PASSWORD_CHANGE_FAILED;
		} 
		logger.setMethodFinishLog(className, sMethodName);
		logger.info(className, sMethodName, sResponse);
		return sResponse;
	}
}
