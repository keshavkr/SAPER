

/* Copyright (c) 2009, 2014, Oracle and/or its affiliates. 
All rights reserved.*/

/*
 DESCRIPTION
 <short description of component this file declares/defines>

 PRIVATE CLASSES
 <list of private classes defined - with one-line descriptions>

 NOTES
 <other useful comments, qualifications, etc.>

 MODIFIED    (MM/DD/YY)



 ddkumar     01/21/11 - Backport ddkumar_bug-10408848 from main
 ddkumar     07/10/09 - Creation
 Dinesh		 21/10/2010 - BUG Id 9475592 - SAP DISABLE USER FAILED
 K S Santosh    04/29/11 - Bug 12334992 - Password Propagation to Child System 
 Akshata.Kulkarni 12/08/11 -BUG 13475350 - SYSTEM IS NOT GET REMOVED WHEN ALL THE ROLES & PROFILES FOR THAT SYSTEM GET REMOVED
 K S Santosh    06/28/12 - BUG 14209711 - SAP UM CONNECTOR: VALID TROUGH DATE UPDATE ISSUE DURING ENABLE AND DISABLE  
 K S Santosh    09/28/12 - Bug 12675870 - NEED TO REMOVE SECOND ENTITLEMENT=TRUE PROPERTY FROM CHILD FORM  
 */

package oracle.iam.connectors.sap.usermgmt.integration;
 
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.Constants;
import oracle.iam.connectors.common.dao.OIMUtil;
import oracle.iam.connectors.common.util.DateUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.common.vo.ITResource;
import oracle.iam.connectors.sap.common.connection.SAPConnection;
import oracle.iam.connectors.sap.common.connection.SAPResourceImpl;
import oracle.iam.connectors.sap.common.util.SAPUtil;
import oracle.iam.connectors.sap.usermgmt.util.SAPUMAttributeMapBean;
import oracle.iam.connectors.sap.usermgmt.util.UMConstants;
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

import com.oracle.oim.gcp.exceptions.ConnectionServiceException;
import com.oracle.oim.gcp.pool.ConnectionService;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.thortech.xl.dataaccess.tcDataProvider;

/**
 * Description:Contain methods that are mapped to adapters for performing
 * various provisioning functions
 * 
 */

public class SAPUMUtilUserProvisionManager implements UMConstants {


	private ConnectorLogger logger = new ConnectorLogger(UM_LOGGER);
	private Hashtable<String, String> htITRattributes;

	//CUP Merge code change starts
	private Hashtable<String, String> htCUPITRattributes;
	private HashMap<String, String> hmCUPConfig;
	private HashMap<String, String> hmCUPConstants;
	//CUP Merge code change ends

	private HashMap<String, String> htITRMapping;
	private HashMap<String, String> configurationMap;
	private OIMUtil oUtil = null;
	private long lProcInstanceKey;
	private tcFormInstanceOperationsIntf fiIntf;
	private tcFormDefinitionOperationsIntf fdIntf;
	private static tcLookupOperationsIntf lookIntf;
	private JCoDestination jcoConnection;
	private String className = this.getClass().getName();
	private StringUtil StrUtil = new StringUtil();
	private DateUtil dtUtil = new DateUtil(logger);
	private String sCUAEnable = null;
	private HashMap<String, String> hmConstants;
	private boolean isPoolingEnabled = false;
	private ConnectionService ser;
	private SAPResourceImpl rc = null;
	long sITResourceKey;
	private HashMap<String, Object> hmFormData = new HashMap<String, Object>();
	private HashMap<String, Object> hmChildDetails = new HashMap<String, Object>();
	boolean isInnitialized = true;
	private boolean isPswdDisabled = false;
	private boolean isCUP = false;

	/**
	 * Description:Used by the addDummyResourceToUser method to initialize
	 * OIMUtil
	 * 
	 * @param pDataBaseRef
	 *            Data Provider
	 * 
	 */
	public SAPUMUtilUserProvisionManager(tcDataProvider pDataBaseRef) {
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
	public SAPUMUtilUserProvisionManager(tcDataProvider pDataBaseRef,
			String pParentFormProcessInstanceKey, String sITResourceUDField, String sConfigLookup, String connPollingSupported,
			String dummyPassword, String clientLogon, String adminLogon, String adminPassword, String lang, String sncMode,
			String masterSystemName, String sncPartnerName, String sncMyName, String sncLib, String sncQop)
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
			//long sITResourceKey =  oUtil.getITResourceKey(pITResourceFieldName);
			
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
					sCUAEnable = configurationMap.get(CUA_ENABLED);
					if (sCUAEnable.equalsIgnoreCase(hmConstants.get(YES))) {
						logger.info(className, sMethodName, "CUA is enabled");
					}
					sITRMapping = configurationMap.get(IT_RESOURCE_MAPPING);
					htITRMapping = oUtil.getLookUpMap(sITRMapping);
					String sPswdDisabled = configurationMap.get(PASSWRD_DISABLED);
					if(sPswdDisabled.equalsIgnoreCase(hmConstants.get(YES))){
						logger.info(className, sMethodName, "Password is disabled");
						isPswdDisabled = true;
					}
					//SAP CUP code change starts 
					try {
					String sIsCUP = configurationMap.get(CUP_MODE_ENABLED);
					logger.debug(className, sMethodName, CUP_MODE_ENABLED + sIsCUP);
					isCUP = sIsCUP.equalsIgnoreCase(hmConstants.get(YES))?true:false;
					if (isCUP) {
						logger.info(className, sMethodName, "CUP mode is enabled");
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
					}
					} catch (Exception e) {
						logger.error(className, sMethodName,
								"Error innitializing CUP values");
						throw new ConnectorException(e);
					}
					//SAP CUP code change ends 
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
			/*
			 * Check if connection pooling is enabled.If enabled get the
			 * connection from the class which implements
			 * ResourceConnection.Else create a new Connection
			 * 
			 */
			String isPool = (String) htITRattributes
					.get(USE_CONNECTION_POOLING);
			logger.debug(className, sMethodName, "isPool : " + isPool);
			if (!StrUtil.isEmpty(isPool)
					&& (isPool.equalsIgnoreCase(hmConstants.get(YES)) || isPool
							.equalsIgnoreCase(hmConstants.get(TRUE)))) {
				logger.info(className, sMethodName,
								"Connection Pooling has been enabled. Will use Connection Pooling Service");
				isPoolingEnabled = true;
			} else {
				logger.info(className,sMethodName,
								"Connection Pooling has not been enabled. Will not use Connection Pooling Service");
			}
			
			boolean isMandatoryITRSet = oITResource
					.validateMandatoryITResource(mandatoryUMITRes);
			if (!isMandatoryITRSet) {
				throw new ConnectorException(
						"Mandatory IT Resource values not set");
			}
			boolean isMandatorySNCITRSet = oITResource
					.validateConditionalMandatory(mandatoryITResSnc, "SNC mode"
							+ ";" + (String) hmConstants.get(YES), ";");
			if (!isMandatorySNCITRSet) {
				throw new ConnectorException(
						"SNC Related Mandatory IT Resource values not set");
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
	public SAPUMUtilUserProvisionManager(tcDataProvider pDataBaseRef,
			String pParentFormProcessInstanceKey, String sITResourceUDField)
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
			//long sITResourceKey =  oUtil.getITResourceKey(pITResourceFieldName);			
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
					sCUAEnable = configurationMap.get(CUA_ENABLED);
					if (sCUAEnable.equalsIgnoreCase(hmConstants.get(YES))) {
						logger.info(className, sMethodName, "CUA is enabled");
					}
					sITRMapping = configurationMap.get(IT_RESOURCE_MAPPING);
					htITRMapping = oUtil.getLookUpMap(sITRMapping);
					String sPswdDisabled = configurationMap.get(PASSWRD_DISABLED);
					if(sPswdDisabled.equalsIgnoreCase(hmConstants.get(YES))){
						logger.info(className, sMethodName, "Password is disabled");
						isPswdDisabled = true;
					}
					//SAP CUP code change starts 
					try {
					String sIsCUP = configurationMap.get(CUP_MODE_ENABLED);
					logger.debug(className, sMethodName, CUP_MODE_ENABLED + sIsCUP);
					isCUP = sIsCUP.equalsIgnoreCase(hmConstants.get(YES))?true:false;
					if (isCUP) {
						logger.info(className, sMethodName, "CUP mode is enabled");
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
					}
					} catch (Exception e) {
						logger.error(className, sMethodName,
								"Error innitializing CUP values");
						throw new ConnectorException(e);
					}
					//SAP CUP code change ends 
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
			/*
			 * Check if connection pooling is enabled.If enabled get the
			 * connection from the class which implements
			 * ResourceConnection.Else create a new Connection
			 * 
			 */
			String isPool = (String) htITRattributes
					.get(USE_CONNECTION_POOLING);
			logger.debug(className, sMethodName, "isPool : " + isPool);
			if (!StrUtil.isEmpty(isPool)
					&& (isPool.equalsIgnoreCase(hmConstants.get(YES)) || isPool
							.equalsIgnoreCase(hmConstants.get(TRUE)))) {
				logger.info(className, sMethodName,
								"Connection Pooling has been enabled. Will use Connection Pooling Service");
				isPoolingEnabled = true;
			} else {
				logger.info(className,sMethodName,
								"Connection Pooling has not been enabled. Will not use Connection Pooling Service");
			}
			boolean isMandatoryITRSet = oITResource
					.validateMandatoryITResource(mandatoryUMITRes);
			if (!isMandatoryITRSet) {
				throw new ConnectorException(
						"Mandatory IT Resource values not set");
			}
			boolean isMandatorySNCITRSet = oITResource
					.validateConditionalMandatory(mandatoryITResSnc, "SNC mode"
							+ ";" + (String) hmConstants.get(YES), ";");
			if (!isMandatorySNCITRSet) {
				throw new ConnectorException(
						"SNC Related Mandatory IT Resource values not set");
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
		String sAttrLookupName = null;
		String sDummyPaswrd = null;
		String sSystemName = null;
		String sSyncPswrd = null;		
		String sUseValidation = null;
		String sValidationLookupName = null;
		boolean isValid = true;	

		//Merge CUP connector change starts 
		String sCUPAttrMapLookupName = null; 
		HashMap<String, String> hmCUPAttrMap = new HashMap<String, String>();
		String sReqType= null;
		String sApplication= null;
		String sPriority = null;
		//Merge CUP connector change ends 

		HashMap<String, ArrayList<SAPUMAttributeMapBean>> attrMap = new HashMap<String, ArrayList<SAPUMAttributeMapBean>>();
		HashMap<String, String> hmExclusionList = new HashMap<String, String>();
		String sExclusionListLookup;
		String sMethodName = "createUser()";
		logger.setMethodStartLog(className, sMethodName);
		SAPConnection sAPConnection = new SAPConnection(logger);
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
			if (StrUtil.isEmpty(sPassword)&& !isPswdDisabled) {
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Provide password for User ID:"+sUserID);
				throw new ConnectorException(sResponse);
			}

			if (configurationMap != null) {
				try {
					sAttrLookupName = configurationMap.get(PROV_ATTR_MAP);
					sSystemName = htITRattributes.get(SYSTEM_NAME);
					logger.debug(className, sMethodName, "System Name: "
							+ sSystemName);
					sSyncPswrd = configurationMap.get(CHANGE_PASSWRD);
					logger.debug(className, sMethodName, CHANGE_PASSWRD + ":"
							+ sSyncPswrd);					
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
					
					//Merge CUP connector change starts 
					if(isCUP) {
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
					}
					//Merge CUP connector change ends 
					
					//checking for some mandatory fields used in provisioning
					if(StrUtil.isEmpty(sAttrLookupName) || StrUtil.isEmpty(sSyncPswrd)
							||StrUtil.isEmpty(sUseValidation) ){
						logger.error(className, sMethodName,"Invalid configuration lookup field");
						throw new ConnectorException(sResponse);
					}
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
			if (htITRattributes != null) {
				// get dummy password from IT Resource
				sDummyPaswrd = htITRattributes.get(DUMMY_PASSWORD);
				if (StrUtil.isEmpty(sDummyPaswrd)
						&& sSyncPswrd.equalsIgnoreCase(hmConstants.get(YES))) {
					logger.info(className, sMethodName,
							"Dummy Password not supplied");
					sResponse = INSUFFICIENT_INFORMATION;
					throw new ConnectorException(sResponse);
				}
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
					//Merge CUP connector change starts
					if(isCUP){
						logger.debug(className, sMethodName, "Performing attribute mapping for CUP");
						hmCUPAttrMap = populateCUPUserDataToHash(sCUPAttrMapLookupName, null);
					}else{
						attrMap = populateUserDataToHash(sAttrLookupName);
					}
					//Merge CUP connector change ends 

					logger.info(className, sMethodName, sUserID
							+ "to be provisioned");
					try {
						jcoConnection = connect(sAPConnection);
					} catch (ConnectorException e) {
						sResponse = CONNECTION_ERROR;
						throw new ConnectorException(e);
					}
					SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
							jcoConnection, hmConstants);
					// check if user exists in SAP system before create
					if (!sAPProxyUserProvisionManager.findUser(sUserID)) {
						if(isCUP){
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
								else if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(CLOSED)) && isCreated) {
									sResponse = REQUEST_CLOSED_SUCCESSFUL;
									logger.info(className, sMethodName,"The request has been submitted and user is created");
								}
								else if(sCUPStatus.equalsIgnoreCase(hmCUPConstants.get(CLOSED)) && !isCreated) {
									sAPProxyUserProvisionManager.auditTrail(sResponseID, htCUPITRattributes, hmCUPConstants);
									logger.error(className, sMethodName, "Request created but User not created in backend");
									sResponse = USER_CREATION_FAILED;
								}
							}else {
								logger.error(className, sMethodName,"The request ID is null");
								throw new ConnectorException();
							}
						}else{
							sResponse = sAPProxyUserProvisionManager.createUser(
									attrMap, sUserID, sPassword, sDummyPaswrd,
									sCUAEnable, sSystemName, sSyncPswrd);						
						}
					}// if user in not found in SAP system
					else {
						sResponse = USER_ALREADY_EXISTS;
						logger.error(className, sMethodName,"Create user failed as user already exists in target");
					}
					try {
						if (jcoConnection != null) {
							if (isPoolingEnabled) {
								ser.releaseConnection(rc);
							} else {
								sAPConnection.closeSAPConnection(jcoConnection);
							}
						}
					} catch (Exception e) {
						sResponse = CONNECTION_ERROR;
						logger.error(className, sMethodName, e.getMessage());
						logger.setStackTrace(e, className, sMethodName, e
								.getMessage());
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
		logger.setMethodFinishLog(className, sMethodName);
		return sResponse;
	}
	
	/**
	 * Description:Used to create a link between an SAP UM user and an SAP HR
	 * user
	 * 
	 * @param UserID
	 *            UM User ID to be linked For example: John.Doe
	 * @param sEmpID
	 *            Personnel number to be linked For example: 00001204
	 * @return String Returns the response code that is mapped in the Adapter.
	 * @throws ConnectorException
	 */
	public String createLink(String UserID, String sEmpID) throws ConnectorException {
		boolean isLinking;
		boolean isValidatePernr;
		boolean isOverWriteLink;
		String sResponse = null;
		String sMethodName = "createLink()";
		logger.setMethodStartLog(className, sMethodName);
		SAPConnection sAPConnection = new SAPConnection(logger);
		boolean isLink = false;
		// check if trusted user is from HRMS
		try{
			if(!isInnitialized){
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Innitialization failed");
				throw new ConnectorException(sResponse);
			}
		if (!StrUtil.isEmpty(sEmpID)) {
			logger.debug(className, sMethodName,
					"Personnel Number: " + sEmpID);
		try{
		isLinking = configurationMap.get(SUPPORT_LINKING).equalsIgnoreCase(
				hmConstants.get(YES)) ? true : false;
		logger.debug(className, sMethodName, SUPPORT_LINKING + ":" + isLinking);
		isValidatePernr = configurationMap.get(VALIDATE_PERNR)
				.equalsIgnoreCase(hmConstants.get(YES)) ? true : false;
		logger.debug(className, sMethodName, VALIDATE_PERNR + ":"
				+ isValidatePernr);
		isOverWriteLink = configurationMap.get(OVERWRITE_LINKING)
				.equalsIgnoreCase(hmConstants.get(YES)) ? true : false;
		logger.debug(className, sMethodName, OVERWRITE_LINKING + ":"
				+ isOverWriteLink);
		} catch (ConnectorException e) {
			sResponse = INSUFFICIENT_INFORMATION;
			logger.error(className, sMethodName,
					"Invalid configuration lookup field");
			throw new ConnectorException(e.getMessage());
		} catch (Exception e) {
			sResponse = INSUFFICIENT_INFORMATION;
			logger.error(className, sMethodName,
					"Invalid configuration lookup field");
			throw new ConnectorException(e.getMessage());
		}
		try {
			jcoConnection = connect(sAPConnection);
		} catch (ConnectorException e) {
			sResponse = CONNECTION_ERROR;
			logger.error(className, sMethodName,
			"Connection Error");
			throw new ConnectorException(e);
		}
			SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
					jcoConnection, hmConstants);
				/*We check if connector is configured to allow
				 *linking
				 * */
				if (isLinking){
					logger.info(className, sMethodName, UserID
							+ " will be linked to " + sEmpID);
				isLink = true;
				// if linking is allowed we will validate personnel
				// number in target
				if (isLink && isValidatePernr) {
					isLink = sAPProxyUserProvisionManager
							.checkExistance(sEmpID);
				} else if (!isValidatePernr) {
					logger.debug(className, sMethodName,
							"Not validating Personnel number");
				}
				if (isLink && !isOverWriteLink) {
					isLink = sAPProxyUserProvisionManager
							.linkedBefore(sEmpID);
				} else if (isOverWriteLink) {
					logger.debug(className, sMethodName,
							"If user is linked it will be overwritten");
				}
				if (!isLink && !isOverWriteLink) {
					logger.info(className, sMethodName,
									"User will not be linked as Personnel number is already linked");
					sResponse = USER_ALREADY_LINKED;
				}
				if (isLink)
					isLink = sAPProxyUserProvisionManager
							.createLink(UserID, sEmpID);
				if (isLink) {
					logger.info(className, sMethodName,
							"User Linked successfully");
					sResponse =USER_LINK_SUCCESSFUL;
				} else {
					logger.error(className, sMethodName,
							"User Linked not done");
					if(StrUtil.isEmpty(sResponse))
					sResponse =USER_LINK_FAILED;
				}
			try {
				if (jcoConnection != null) {
					if (isPoolingEnabled) {
						ser.releaseConnection(rc);
					} else {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}
			} catch (Exception e) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, e.getMessage());
				logger.setStackTrace(e, className, sMethodName, e
						.getMessage());
			}
		}else{
			logger.debug(className, sMethodName,"Support HRMS 0105 Infotype Linking is set to no");
			sResponse = LINKING_NOT_ENABLED;
		}
		}else{
			logger.debug(className, sMethodName,"personnel number UDF is empty");
			sResponse = LINKING_NOT_ENABLED;
		}
		}catch (ConnectorException e) {
			logger.error(className, sMethodName, e.getMessage());
			if(e.getMessage().startsWith("Connection")){
				sResponse =CONNECTION_ERROR;
			}
			if(StrUtil.isEmpty(sResponse))
			sResponse =USER_LINK_FAILED;
		}catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			sResponse =USER_LINK_FAILED;
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sResponse;
	}

	/**
	 * Description:This method is used to provision a user to SAP system
	 * @param sAPConnection  Sap connection class object
	 * 
	 * @return JCoDestination 
	 * */
	private JCoDestination connect(SAPConnection sAPConnection) {
		String sMethodName = "connect()";
		logger.setMethodStartLog(className, sMethodName);
		if (isPoolingEnabled) {
			ser = new ConnectionService();
			try {
				logger.debug(className, sMethodName, "ITResource Key:"
						+ sITResourceKey);
				rc = (SAPResourceImpl) ser.getConnection(sITResourceKey);
				jcoConnection = rc.mConnection;
			} catch (ConnectionServiceException dbe) {
				throw new ConnectorException(
						"Unable to get a connection from connection pool", dbe);
			}
		} else {
			try {
				jcoConnection = sAPConnection.addDestination(htITRMapping,
						htITRattributes);
			} catch (ConnectorException e) {
				logger.error(className, sMethodName, e.getMessage());
				logger.setStackTrace(e, className, sMethodName, e.getMessage());
				throw new ConnectorException("Connection Error");
			} catch (Exception e) {
				logger.error(className, sMethodName, e.getMessage());
				logger.setStackTrace(e, className, sMethodName, e.getMessage());
				throw new ConnectorException("Connection Error");
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
		return jcoConnection;
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
	public String modifyUser(String sUserID, String sFieldName,
			String sBAPIStructure) throws ConnectorException {
		String sResponse = null;
		HashMap<String, ArrayList<SAPUMAttributeMapBean>> attrMap = new HashMap<String, ArrayList<SAPUMAttributeMapBean>>();
		SAPConnection sAPConnection = new SAPConnection(logger);
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
			if (StrUtil.isEmpty(sUserID) || StrUtil.isEmpty(sFieldName)
					|| StrUtil.isEmpty(sBAPIStructure)) {
				logger.error(className, sMethodName,
						"Incorrect information sent to method");
				sResponse = INSUFFICIENT_INFORMATION;
				throw new ConnectorException(sResponse);
			}
			String sAttrMapLookupName = configurationMap.get(PROV_ATTR_MAP);
			logger.debug(className, sMethodName, PROV_ATTR_MAP + ":"
					+ sAttrMapLookupName);
			logger.info(className, sMethodName, "User to be modified: "
					+ sUserID);
			try {
				jcoConnection = connect(sAPConnection);
			} catch (ConnectorException e) {
				sResponse = CONNECTION_ERROR;
				throw new ConnectorException(e);
			}

			SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
					jcoConnection, hmConstants);;
			// Merge CUP connector change starts
			if (isCUP) {
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
				}
			// Merge CUP connector change ends

			// Merge CUP connector change starts
			if (isCUP) {
				logger.debug(className, sMethodName, "Performing attribute mapping for CUP");
				hmCUPAttrMap = populateCUPUserDataToHash(sCUPAttrMapLookupName,
						sFieldName);
			}
			if (!isCUP) {
				try {
					logger.debug(className, sMethodName, "Performing attribute mapping");
					attrMap = populateUserDataToHash(sAttrMapLookupName);
				} catch (ConnectorException e) {
					sResponse = ATTRIBUTE_MAPPING_FAILED;
					logger.error(className, sMethodName, e.getMessage());
					throw new ConnectorException(sResponse);
				}
			}
			// Merge CUP connector change ends
			if (sAPProxyUserProvisionManager.findUser(sUserID)) {
				logger.info(className, sMethodName,
						"User Existence Check Complete");

				// Merge CUP connector change starts
				if (isCUP) {

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
					SAPUMAttributeMapBean sAPAttributeMapBean = oUMUtil
							.getUserBean(attrMap, sFieldName, sBAPIStructure);
					if (sAPAttributeMapBean == null) {
						sResponse = USER_MODIFICATION_FAILED;
						throw new ConnectorException(sResponse);
					}
					String sBAPIFieldValue = sAPAttributeMapBean
							.getFieldValue();
					String sBAPIFieldNameX = sAPAttributeMapBean
							.getBapiFieldNameX();
					String sBAPIStructureX = sAPAttributeMapBean
							.getBapiStructureX();
					if (sAPAttributeMapBean.getFieldType().equalsIgnoreCase(
							hmConstants.get(FIELDTYPE_LOOKUP))) {
						sBAPIFieldValue = oUMUtil
								.getFormattedAttributeValue(sBAPIFieldValue);
					}
					logger.info(className, sMethodName, "Modifying attribute: "
							+ sAPAttributeMapBean.getOIMfieldName());
					sResponse = sAPProxyUserProvisionManager.modifyUser(
							sUserID, sFieldName, sBAPIStructure,
							sBAPIFieldNameX, sBAPIStructureX, sBAPIFieldValue);
                     // Start :: 10408848    
      				if("GLTGB".equalsIgnoreCase(sFieldName)){
						updateValidThrDate(sUserID,sBAPIFieldValue);  
					}
					// End :: 10408848
				}
				// Merge CUP connector change ends
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
		} finally {
			try {
				if (jcoConnection != null) {
					if (isPoolingEnabled) {
						ser.releaseConnection(rc);
					} else {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}
			} catch (ConnectorException exception) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, exception.getMessage());
				logger.setStackTrace(exception, className, sMethodName,
						exception.getMessage());
			} catch (Exception e) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, e.getMessage());
				logger.setStackTrace(e, className, sMethodName, e.getMessage());
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
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
		SAPConnection sAPConnection = new SAPConnection(logger);
		// Merge CUP connector change starts
		String sCUPAttrMapLookupName = null;
		HashMap<String, String> hmCUPAttrMap = new HashMap<String, String>();
		String sReqType = null;
		String sApplication = null;
		String sPriority = null;
		// Merge CUP connector change ends
		
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
		try {
			jcoConnection = connect(sAPConnection);
		} catch (ConnectorException e) {
			sResponse = CONNECTION_ERROR;
			throw new ConnectorException(e);
		}
		// Merge CUP connector change starts
		if (isCUP) {
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
			sApplication = hmCUPConfig.get(APPLICATION);
			logger.debug(className, sMethodName, APPLICATION
					+ ":" + sApplication);
			sPriority = hmCUPConfig.get(PRIORITY);
			logger.debug(className, sMethodName, PRIORITY
					+ ":" + sPriority);
			}
		// Merge CUP connector change ends
		SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
				jcoConnection, hmConstants);


		 //Merge CUP connector change starts
		if(isCUP){
			hmCUPAttrMap = populateCUPUserDataToHash(sCUPAttrMapLookupName,null);
		}
		//Merge CUP connector change ends 


			if (sAPProxyUserProvisionManager.findUser(sUserId)) {
				 //Merge CUP connector change starts
				if (isCUP) {
					
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

				}else{
					sResponse = sAPProxyUserProvisionManager.modifyLockUnlockUser(
							sUserId, sLockUnLockValue);					
				}
				//Merge CUP connector change ends 
				
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
		} finally {
			try {
				if (jcoConnection != null) {
					if (isPoolingEnabled) {
						ser.releaseConnection(rc);
					} else {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}
			} catch (ConnectorException exception) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, exception.getMessage());
				logger.setStackTrace(exception, className, sMethodName,
						exception.getMessage());
			} catch (Exception e) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, e.getMessage());
				logger.setStackTrace(e, className, sMethodName, e.getMessage());
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sResponse;
	}
	/**
	 * Description:This method is used to modify a user in SAP system
	 * 
	 * @param sUserID
	 *            User ID of the user to be modified
	 *            For example: John.Doe
	 * @param sBAPIFieldName
	 *            SAP field name
	 *            For example: BAPIPWD
	 * @param sBAPIStructure
	 *            SAP structure name
	 * 			  For example: PASSWORD
	 * @return String Returns the response code that is mapped in the Adapter.
	 * 
	 * @throws ConnectorException
	 */
	public String modifyPassword(String sUserID, String sBAPIFieldName,
			String sBAPIStructure) throws ConnectorException {
		String sResponse = null;
		String sSyncPswrd = null;
		String isPasswordPropagate = null;
		SAPConnection sAPConnection = new SAPConnection(logger);
		String sDummyPassword = null;
		String sPasswordPropagateDelay=null;
		HashMap<String, ArrayList<SAPUMAttributeMapBean>> attrMap = new HashMap<String, ArrayList<SAPUMAttributeMapBean>>();
		String sMethodName = "modifyPassword()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		try {
			if(!isInnitialized){
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Innitialization failed");
				throw new ConnectorException(sResponse);
			}
			
			if (StrUtil.isEmpty(sBAPIFieldName) || StrUtil.isEmpty(sUserID)
				|| StrUtil.isEmpty(sBAPIStructure)) {
			logger.error(className, sMethodName,
					"Incorrect information sent to method");
			throw new ConnectorException(INSUFFICIENT_INFORMATION);
		}
		sSyncPswrd = configurationMap.get(CHANGE_PASSWRD);
		logger.debug(className, sMethodName, CHANGE_PASSWRD + ":" + sSyncPswrd);
		String sAttrMapLookupName = configurationMap.get(PROV_ATTR_MAP);
		logger.debug(className, sMethodName, PROV_ATTR_MAP + ":"
				+ sAttrMapLookupName);

		isPasswordPropagate = configurationMap.get(IS_PASSWORD_PROPAGATE_TO_CHILD_SYSTEM);
		logger.debug(className, sMethodName, IS_PASSWORD_PROPAGATE_TO_CHILD_SYSTEM + ":" + isPasswordPropagate);
		//Start:: Added for Password propagate Delay - BUG 17159673
		sPasswordPropagateDelay = configurationMap.get(PASSWORD_PROPAGATE_DELAY);
		logger.debug(className, sMethodName, PASSWORD_PROPAGATE_DELAY + ":" + sPasswordPropagateDelay);
		//End :: Added for Password propagate Delay - BUG 17159673
		try {
			jcoConnection = connect(sAPConnection);
		} catch (ConnectorException e) {
			sResponse = CONNECTION_ERROR;
			throw new ConnectorException(e);
		}
		attrMap = populateUserDataToHash(sAttrMapLookupName);
		logger.info(className, sMethodName, "Modify password for User ID: "
				+ sUserID);
		SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
				jcoConnection, hmConstants);
			if (sAPProxyUserProvisionManager.findUser(sUserID)) {
				SAPUMAttributeMapBean sAPAttributeMapBean = oUMUtil
						.getUserBean(attrMap, sBAPIFieldName, sBAPIStructure);
				if (sAPAttributeMapBean == null) {
					logger.error(className, sMethodName,
							"Attribute mapping failed");
					throw new ConnectorException(PASSWORD_CHANGE_FAILED);
				}
				sDummyPassword = htITRattributes.get(DUMMY_PASSWORD);
				String sBAPIFieldValue = sAPAttributeMapBean.getFieldValue();
				String sBAPIFieldNameX = sAPAttributeMapBean
						.getBapiFieldNameX();
				String sBAPIStructureX = sAPAttributeMapBean
						.getBapiStructureX();
				//Start :: BUG 17159673
				sResponse = sAPProxyUserProvisionManager.modifyPassword(
						sUserID, sBAPIFieldName, sBAPIStructure,
						sBAPIFieldNameX, sBAPIStructureX, sDummyPassword,
						sBAPIFieldValue, sSyncPswrd, isPasswordPropagate, sPasswordPropagateDelay);
				//End :: BUG 17159673
			} else {
				sResponse = USER_DOES_NOT_EXIST;
				logger.error(className, sMethodName, "User id :" + sUserID
						+ " does not exist in target SAP system.");
			}
		} catch (ConnectorException bException) {
			if(bException.getMessage().startsWith("Connection")){
				sResponse =CONNECTION_ERROR;
			}
			if(StrUtil.isEmpty(sResponse))
			sResponse = PASSWORD_CHANGE_FAILED;
		} catch (Exception e) {
			sResponse = PASSWORD_CHANGE_FAILED;
		} finally {
			try {
				if (jcoConnection != null) {
					if (isPoolingEnabled) {
						ser.releaseConnection(rc);
					} else {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}
			} catch (ConnectorException bException) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, bException.getMessage());
				logger.setStackTrace(bException, className, sMethodName,
						bException.getMessage());
			} catch (Exception exception) {
				logger.error(className, sMethodName, exception.getMessage());
				logger.setStackTrace(exception, className, sMethodName,
						exception.getMessage());
				sResponse = CONNECTION_ERROR;
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
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
		SAPConnection sAPConnection = new SAPConnection(logger);
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
			try {
				jcoConnection = connect(sAPConnection);
			} catch (ConnectorException e) {
				sResponse = CONNECTION_ERROR;
				throw new ConnectorException(e);
			}

			SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
					jcoConnection, hmConstants);
					

			if (sAPProxyUserProvisionManager.findUser(sUserID)) {
				// ValidThro in SAP system is set yesterdays date
				dtValidThro = new Date();
				long mymillidate=dtValidThro.getTime();
				if(!isCUP) {
				  mymillidate=mymillidate - 24*60*60*1000;
				}
			    dtValidThro= new Date(mymillidate);
				String sDate = dtUtil.parseTime(dtValidThro, hmConstants
						.get(EnableDisableDF));
				dtValidThro = dtUtil.returnDate(sDate, hmConstants
						.get(EnableDisableDF));
				Timestamp tStart = new Timestamp(dtValidThro.getTime());
				String formattedDateTime = tStart.toString();
				HashMap<String, String> hmMap = new HashMap<String, String>();
				hmMap.put(ValidThroUD, formattedDateTime);
				logger.info("Setting the ValidThro date : "
						+ formattedDateTime);
					fiIntf.setProcessFormData(lProcInstanceKey, hmMap);
				//Commented the below logic because the updateValidthrou would be called in Modify user adapter :: 10408848
					/*// BUG Id 9475592 - SAP - DISABLE USER FAILED
				// Code for Checking the Target System Valid thru date and sync
				// the same date in OIM

				try {
					SAPUtil oSAPUtil = new SAPUtil(logger);
					JCoFunction jcoFunction = oSAPUtil.getJCOFunction(
							jcoConnection, (String) hmConstants
									.get(BAPI_USER_GET_DETAIL));
					JCoParameterList input = jcoFunction
							.getImportParameterList();
					input.setValue((String) hmConstants.get(USERNAME), sUserID);
					jcoFunction.execute(jcoConnection);
					JCoStructure sapStructure = jcoFunction
							.getExportParameterList().getStructure("LOGONDATA");
					Object fieldValue = (sapStructure.getValue("GLTGB"));
					Date dtValidThroFromTarget = (Date) fieldValue;
					String strTargetDate = dtUtil.parseTime(
							dtValidThroFromTarget, hmConstants
									.get(EnableDisableDF));
					dtValidThroFromTarget = dtUtil.returnDate(strTargetDate,
							hmConstants.get(EnableDisableDF));

					tStart = new Timestamp(dtValidThroFromTarget.getTime());
					String strFormattedDateForTarget = tStart.toString();

					if (dtValidThro.before(dtValidThroFromTarget)) {
						hmMap.put(ValidThroUD, strFormattedDateForTarget);
						logger.info("Setting the ValidThro date : "
								+ strFormattedDateForTarget);
						// ValidThro in OIM is set as Target ValidThro date
						fiIntf.setProcessFormData(lProcInstanceKey, hmMap);
					}

				} catch (Exception ex) {
					logger.error(className, sMethodName, "Disable User "
							+ " Exception for user id :" + sUserID);
					logger.error(className, sMethodName, ex.getMessage());
					ex.printStackTrace();
				}
				// End of the Code
				// End of the BUG Id 9475592 - SAP - DISABLE USER FAILED*/

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
		} finally {
			try {
				if (jcoConnection != null) {
					if (isPoolingEnabled) {
						ser.releaseConnection(rc);
					} else {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}
			} catch (ConnectorException bException) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, bException.getMessage());
				logger.setStackTrace(bException, className, sMethodName,
						bException.getMessage());
			} catch (Exception exception) {
				logger.error(className, sMethodName, exception.getMessage());
				logger.setStackTrace(exception, className, sMethodName,
						exception.getMessage());
				sResponse = CONNECTION_ERROR;
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
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
		SAPConnection sAPConnection = new SAPConnection(logger);
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
		try {
			jcoConnection = connect(sAPConnection);
		} catch (ConnectorException e) {
			sResponse = CONNECTION_ERROR;
			throw new ConnectorException(e);
		}

		SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
				jcoConnection, hmConstants);
		
			if (sAPProxyUserProvisionManager.findUser(sUserID)) {
				// Start :: Added for the bug 14209711
				if(configurationMap.get(ENABLE_USER_USING_OIM_END_DATE).equalsIgnoreCase(NO)){
				// End :: Added for the bug 14209711
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
				// Start :: Added for the bug 14209711		
				} else {
						sResponse = ENABLE_USER_SUCCESSFUL;
				}
				// End :: Added for the bug 14209711
				//Start: Bug 18342752- WHEN AN USER FROM CUA IS DISABLED AND ENABLED AGAIN, THE USER IS STILL DISABLED
				if(configurationMap.get(UnlockWhileEnable).equalsIgnoreCase("True")){
				String lockStatus = getUserLockStatus(sUserID);
				if(lockStatus.equalsIgnoreCase(hmConstants.get(DISABLE_LOCK_STATUS)))
	        	{
	        		sAPProxyUserProvisionManager.modifyLockUnlockUser(sUserID, "0");
	        	}
				}
				//End: Bug 18342752- WHEN AN USER FROM CUA IS DISABLED AND ENABLED AGAIN, THE USER IS STILL DISABLED 
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
		} finally {
			try {
				if (jcoConnection != null) {
					if (isPoolingEnabled) {
						ser.releaseConnection(rc);
					} else {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}
			} catch (ConnectorException bException) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, bException.getMessage());
				logger.setStackTrace(bException, className, sMethodName,
						bException.getMessage());
			} catch (Exception exception) {
				logger.error(className, sMethodName, exception.getMessage());
				logger.setStackTrace(exception, className, sMethodName,
						exception.getMessage());
				sResponse = CONNECTION_ERROR;
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
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
		SAPConnection sAPConnection = new SAPConnection(logger);
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
		try {
			jcoConnection = connect(sAPConnection);
		} catch (ConnectorException e) {
			sResponse = CONNECTION_ERROR;
			throw new ConnectorException(e);
		}

		SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
				jcoConnection, hmConstants);
		//Merge CUP connector change starts 
		if(isCUP) {
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
		}
		//Merge CUP connector change ends 

			if (sAPProxyUserProvisionManager.findUser(sUserID)) {
				logger.info("User Existence Check Complete");
				
				//Merge CUP connector change starts 
				if(isCUP)
				{

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
						JCoFunction jcoFunction = oSAPUtil.getJCOFunction(
								jcoConnection, hmConstants
										.get(BAPI_USER_GET_DETAIL));
						JCoParameterList input = jcoFunction
								.getImportParameterList();
						input.setValue(hmConstants.get(USERNAME), sUserID);
						jcoFunction.execute(jcoConnection);
						boolean isNotExists = oUMUtil.findUser(jcoFunction);
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

				}else{
					sResponse = sAPProxyUserProvisionManager.deleteUser(sUserID);					
				}				
				//Merge CUP connector change ends 				
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
		} finally {
			try {
				if (jcoConnection != null) {
					if (isPoolingEnabled) {
						ser.releaseConnection(rc);
					} else {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}
			} catch (ConnectorException bException) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, bException.getMessage());
				logger.setStackTrace(bException, className, sMethodName,
						bException.getMessage());
			} catch (Exception exception) {
				logger.error(className, sMethodName, exception.getMessage());
				logger.setStackTrace(exception, className, sMethodName,
						exception.getMessage());
				sResponse = CONNECTION_ERROR;
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
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
			String sChildPrimaryKey, String sChildTableName,
			String sBapiStructureName, String sBapiFieldName)
			throws ConnectorException {
		String sResponse = null;
		String sTempResponse = null;
		String sTimeoutCount = null;
		String sTimeoutRetryCnt = null;
		int iTimeoutCount;
		int iTimeoutRetryCount;
		int iRetryCount = 0;
		String sMethodName = "addMultiValueData()";
		logger.setMethodStartLog(className, sMethodName);
		SAPConnection sAPConnection = new SAPConnection(logger);
		HashMap<String, ArrayList<SAPUMAttributeMapBean>> attrMap = new HashMap<String, ArrayList<SAPUMAttributeMapBean>>();
		//Merge CUP connector change starts 
		String sCUPAttrMapLookupName = null; 
		String sCUPChildAttrMapLookupName = null; 
		HashMap<String, String> hmCUPAttrMap = new HashMap<String, String>();
		HashMap<String, String> hmCUPChildAttrMap = new HashMap<String, String>();
		String sReqType= null;
		String sApplication= null;
		String sPriority = null;
		String isIgnoreUserCreatedCheck = null;
		//Merge CUP connector change ends 


		try {
		if(!isInnitialized){
			sResponse = INSUFFICIENT_INFORMATION;
			logger.error(className, sMethodName, "Innitialization failed");
			throw new ConnectorException(sResponse);
		}
		
		if (StrUtil.isEmpty(sUserID) || StrUtil.isEmpty(sAttributeName)
				|| StrUtil.isEmpty(sChildPrimaryKey)
				|| StrUtil.isEmpty(sChildTableName)
				|| StrUtil.isEmpty(sBapiStructureName)
				|| StrUtil.isEmpty(sBapiFieldName)) {
			logger.error(className, sMethodName,
					"Insufficient Information : Provide compulsory fields.");
			logger.error(className, sMethodName, "userId :" + sUserID
					+ ", Attr Name:" + sAttributeName + ", ChildPrimaryKey:"
					+ sChildPrimaryKey + ", ChildTableName:" + sChildTableName
					+ ", BapiStructureName:" + sBapiStructureName
					+ ", BapiFieldName:" + sBapiFieldName);
			sResponse = INSUFFICIENT_INFORMATION;
			throw new ConnectorException(sResponse);
		}

			if (sBapiFieldName.contains("|")) {
				StringTokenizer st = new StringTokenizer(sBapiFieldName, "|");
				int iTokenCount = st.countTokens();
				int i = 0;
				String[] formattedAttr = new String[iTokenCount];
				while (st.hasMoreTokens()) {
					formattedAttr[i++] = st.nextToken();
				}
				if (sCUAEnable.equalsIgnoreCase(hmConstants.get(YES))) {
					sBapiFieldName = formattedAttr[0];
				} else {
					sBapiFieldName = formattedAttr[1];
				}
			}

			sTimeoutCount = htITRattributes.get(TIMEOUT_COUNT);
			sTimeoutRetryCnt = htITRattributes.get(TIMEOUT_RETRY_COUNT);
			iTimeoutRetryCount = Integer.parseInt(sTimeoutRetryCnt);
			iTimeoutCount = Integer.parseInt(sTimeoutCount);
			String sAttrMapLookupName = configurationMap
					.get(PROV_CHILD_ATTR_MAP);

			try {
				jcoConnection = connect(sAPConnection);
			} catch (ConnectorException e) {
				sResponse = CONNECTION_ERROR;
				throw new ConnectorException(e);
			}
			SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
					jcoConnection, hmConstants);
			
			//Merge CUP connector change starts 
			if(isCUP) {
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
			}
			//Merge CUP connector change ends 
			
			 //Merge CUP connector change starts
			if(isCUP){
				hmCUPAttrMap = populateCUPUserDataToHash(sCUPAttrMapLookupName,null);
				hmCUPChildAttrMap = populateCUPChildDataToCreate(sCUPChildAttrMapLookupName,
						sAttributeName, sChildPrimaryKey, sChildTableName);
			}else{
				attrMap = populateChildDataToCreate(sAttrMapLookupName,
						sAttributeName, sChildPrimaryKey, sChildTableName);
			}
			//Merge CUP connector change ends 

			if ((isCUP && isIgnoreUserCreatedCheck.equalsIgnoreCase(YES))
					|| sAPProxyUserProvisionManager.findUser(sUserID)) {

				//Merge CUP connector change starts
				if(isCUP){

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

				}else{
					ArrayList<SAPUMAttributeMapBean> dataList = new ArrayList<SAPUMAttributeMapBean>();

					if (!StrUtil.isEmpty(sBapiStructureName)) {
						dataList = attrMap.get(sBapiStructureName);
					}
					
					sTempResponse = sAPProxyUserProvisionManager
					.addMultiValueData(dataList, sUserID,
							sBapiStructureName, sBapiFieldName,
							sCUAEnable);				
					sResponse = sTempResponse;
					while (iRetryCount < iTimeoutRetryCount
							&& sTempResponse.equalsIgnoreCase(RETRY)) {
						logger.info(className, sMethodName,
								"********Retried addMultiValueData() for" + sUserID
										+ " " + String.valueOf(iRetryCount)
										+ " times******");
						iRetryCount++;
						Thread.sleep(iTimeoutCount);
							sTempResponse = sAPProxyUserProvisionManager
							.addMultiValueData(dataList, sUserID,
									sBapiStructureName, sBapiFieldName,
									sCUAEnable);		
						sResponse = sTempResponse;
					}
				}
				//Merge CUP connector change ends
				
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
		} finally {
			try {
				if (jcoConnection != null) {
					if (isPoolingEnabled) {
						ser.releaseConnection(rc);
					} else {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}
			} catch (ConnectorException bException) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, bException.getMessage());
				logger.setStackTrace(bException, className, sMethodName,
						bException.getMessage());
			} catch (Exception exception) {
				logger.error(className, sMethodName, exception.getMessage());
				logger.setStackTrace(exception, className, sMethodName,
						exception.getMessage());
				sResponse = CONNECTION_ERROR;
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
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
	 * @param sBapiStructureName
	 *            Structure name in the BAPI. The BAPI field name is part of
	 *            this structure. For example: PROFILE
	 * 
	 * @return String Returns the response code that is mapped in the adapter
	 * @throws ConnectorException
	 */
	public String removeMultivalueData(String sUserID, String sAttributeName,
			String sBapiStructureName, String sChildPrimaryKey)
			throws ConnectorException {
		String sResponse = null;
		String sTempResponse = null;
		String sTimeoutCount = null;
		String sTimeoutRetryCnt = null;
		int iTimeoutCount = 0;
		int iTimeoutRetryCount = 0;
		int iRetryCount = 0;
		SAPConnection sAPConnection = new SAPConnection(logger);
		String sMethodName = "removeMultivalueData()";
		logger.setMethodStartLog(className, sMethodName);
		//Merge CUP connector change starts 
		String sCUPAttrMapLookupName = null; 
		HashMap<String, String> hmCUPAttrMap = new HashMap<String, String>();
		HashMap<String, String> hmCUPChildAttrMap = new HashMap<String, String>();
		String sReqType= null;
		String sApplication= null;
		String sPriority = null;
		//Merge CUP connector change ends
		try {
		if(!isInnitialized){
			sResponse = INSUFFICIENT_INFORMATION;
			logger.error(className, sMethodName, "Innitialization failed");
			throw new ConnectorException(sResponse);
		}
		
		if (StrUtil.isEmpty(sUserID) || StrUtil.isEmpty(sBapiStructureName)
				|| StrUtil.isEmpty(sAttributeName)
				|| StrUtil.isEmpty(sChildPrimaryKey)) {
			logger.error(className, sMethodName,
					"Incorrect information sent to method");
			sResponse = INSUFFICIENT_INFORMATION;
			throw new ConnectorException(sResponse);
		}

		try {
			jcoConnection = connect(sAPConnection);
		} catch (ConnectorException e) {
			sResponse = CONNECTION_ERROR;
			throw new ConnectorException(e);
		}
		
			SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
					jcoConnection, hmConstants);
			//Merge CUP connector change starts 
			if(isCUP) {
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
			}
			//Merge CUP connector change ends
			
			if (sAPProxyUserProvisionManager.findUser(sUserID)) {
				
				 //Merge CUP connector change starts
				if(isCUP){
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
					hmCUPChildAttrMap.put(hmCUPConstants.get(SYSID)+";TEXT", arr[1]);
					hmCUPChildAttrMap.put(hmCUPConstants.get(ROLEID)+";TEXT", arr[2]);
					hmCUPChildAttrMap.put(hmCUPConstants.get(ACTION)+";TEXT", hmCUPConstants.get(REMOVE));
				}else{
				if (sChildPrimaryKey.contains("|")) {
					StringTokenizer st = new StringTokenizer(sChildPrimaryKey,
							"|");
					int iTokenCount = st.countTokens();
					int i = 0;
					String[] formattedAttr = new String[iTokenCount];
					while (st.hasMoreTokens()) {
						formattedAttr[i++] = st.nextToken();
					}
					if (sCUAEnable.equalsIgnoreCase(hmConstants.get(YES))) {
						sChildPrimaryKey = formattedAttr[0];
					} else {
						sChildPrimaryKey = formattedAttr[1];
					}
				}
				sTimeoutCount = htITRattributes.get(TIMEOUT_COUNT);
				sTimeoutRetryCnt = htITRattributes.get(TIMEOUT_RETRY_COUNT);
				if (StrUtil.isEmpty(sTimeoutCount)
						|| StrUtil.isEmpty(sTimeoutRetryCnt)) {
					logger.error(className, sMethodName,
							"Time Out related attributes must be populated");
					sResponse = INSUFFICIENT_INFORMATION;
					throw new ConnectorException(sResponse);
				}
				iTimeoutRetryCount = Integer.parseInt(sTimeoutRetryCnt);
				iTimeoutCount = Integer.parseInt(sTimeoutCount);
				}
				//Merge CUP connector change ends
				
				//Merge CUP connector change starts
				if(isCUP){
					
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
					
				}else{
					sTempResponse = sAPProxyUserProvisionManager
					.removeMultiValueData( sUserID,
							sBapiStructureName,sAttributeName, sChildPrimaryKey,
							sCUAEnable);				
					//To ReTry attempt in case of a time out response from SAP
					sResponse = sTempResponse;
					while (iRetryCount < iTimeoutRetryCount
							&& sTempResponse.equalsIgnoreCase(RETRY)) {
						logger.info(className, sMethodName,
								"********Retried removeMultivalueData() for"
										+ sUserID + " "
										+ String.valueOf(iRetryCount)
										+ " times******");
						iRetryCount++;
						Thread.sleep(iTimeoutCount);

							sTempResponse = sAPProxyUserProvisionManager
							.removeMultiValueData( sUserID,
									sBapiStructureName,sAttributeName, sChildPrimaryKey,
									sCUAEnable);				
						sResponse = sTempResponse;
					}
				}
				//Merge CUP connector change ends
				

				// START :Code Modification for BUG 13475350

				// On Success of Role/Profile removal trigger removeSystem
				if (sResponse.equalsIgnoreCase(MULTI_DATA_REMOVE_SUCCESSFUL)
						&& (sBapiStructureName.equalsIgnoreCase(hmConstants
								.get(ACTIVITYGROUPS)) || sBapiStructureName
								.equalsIgnoreCase(hmConstants.get(PROFILES)))
						&& (configurationMap.get(CUA_ENABLED))
								.equalsIgnoreCase(hmConstants.get(YES))
						&& (configurationMap.get(IS_SYSTEM_NEED_TO_REMOVE)
								.equalsIgnoreCase(YES))) {
					removeSystem(sUserID, sAttributeName);
				}

				// END :Code Modification for BUG 13475350


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
		} finally {
			try {
				if (jcoConnection != null) {
					if (isPoolingEnabled) {
						ser.releaseConnection(rc);
					} else {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}
			} catch (ConnectorException bException) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, bException.getMessage());
				logger.setStackTrace(bException, className, sMethodName,
						bException.getMessage());
			} catch (Exception exception) {
				logger.error(className, sMethodName, exception.getMessage());
				logger.setStackTrace(exception, className, sMethodName,
						exception.getMessage());
				sResponse = CONNECTION_ERROR;
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sResponse;
	}

	/**
	 * Description: Used to update a multivalued attribute for a user, such as
	 * role or parameter.
	 * 
	 * @param sUserID
	 *            User ID of the user to be modified. For example: John.Doe
	 * @param sAttributeName
	 *            Multivalued attribute value to be updated. For example:
	 *            1~E60~V_SYS_ADMIN
	 * @param sChildPrimaryKey
	 *            UDField of the key child attribute. For example: UD_SAP_RL
	 * @param sChildTableName
	 *            Child form table name. For example: UD_SAP
	 * @param sBapiFieldName
	 *            Field name of the key value of the multivalued attribute in
	 *            the BAPI. For example: AGR_NAME
	 * @param sBapiStructureName
	 *            Structure name in the BAPI. The BAPI field is part of this
	 *            structure. For example: ROLE
	 * @return String Returns the response code that is mapped in the adapter
	 * @throws ConnectorException
	 */
	public String updateMultiValueData(String sUserID, String sAttributeName,
			String sChildPrimaryKey, String sChildTableName,
			String sBapiStructureName, String sBapiFieldName)
			throws ConnectorException {
		String sResponse = null;
		String sMethodName = "updateMultiValueData()";
		logger.setMethodStartLog(className, sMethodName);
		SAPConnection sAPConnection = new SAPConnection(logger);
		HashMap<String, ArrayList<SAPUMAttributeMapBean>> attrMap = new HashMap<String, ArrayList<SAPUMAttributeMapBean>>();
		
		//Merge CUP connector change starts 
		String sCUPAttrMapLookupName = null; 
		String sCUPChildAttrMapLookupName = null; 
		HashMap<String, String> hmCUPAttrMap = new HashMap<String, String>();
		HashMap<String, String> hmCUPChildAttrMap = new HashMap<String, String>();
		String sReqType= null;
		String sApplication= null;
		String sPriority = null;
		//Merge CUP connector change ends 
		
		try {		
		if(!isInnitialized){
			sResponse = INSUFFICIENT_INFORMATION;
			logger.error(className, sMethodName, "Innitialization failed");
			throw new ConnectorException(sResponse);
		}
		
		if (StrUtil.isEmpty(sUserID) || StrUtil.isEmpty(sAttributeName)
				|| StrUtil.isEmpty(sChildPrimaryKey)
				|| StrUtil.isEmpty(sChildTableName)
				|| StrUtil.isEmpty(sBapiStructureName)
				|| StrUtil.isEmpty(sBapiFieldName)) {
			logger.error(className, sMethodName,
					"Insufficient Information : Provide compulsory fields.");
			logger.error(className, sMethodName, "userId :" + sUserID
					+ ", Role Name:" + sAttributeName + ", ChildPrimaryKey:"
					+ sChildPrimaryKey + ", ChildTableName:" + sChildTableName
					+ ", BapiStructureName:" + sBapiStructureName
					+ ", BapiFieldName:" + sBapiFieldName);
			sResponse = INSUFFICIENT_INFORMATION;
			throw new ConnectorException(sResponse);
		}
		//Merge CUP connector change starts 
		if(isCUP) {
		sCUPAttrMapLookupName = hmCUPConfig.get(PARENT_ATTR_LOOKUP);
		logger.debug(className, sMethodName, PARENT_ATTR_LOOKUP
				+ ":" + sCUPAttrMapLookupName);
		sCUPChildAttrMapLookupName = hmCUPConfig.get(CHILD_ATTR_LOOKUP);
		logger.debug(className, sMethodName, CHILD_ATTR_LOOKUP
				+ ":" + sCUPChildAttrMapLookupName);
		sReqType = hmCUPConfig.get(ASSIGN_ROLE);
		logger.debug(className, sMethodName, "Request type for assign role"
				+ ":" + sReqType);
		sApplication = hmCUPConfig.get(APPLICATION);
		logger.debug(className, sMethodName, APPLICATION
				+ ":" + sApplication);
		sPriority = hmCUPConfig.get(PRIORITY);
		logger.debug(className, sMethodName, PRIORITY
				+ ":" + sPriority);
		}
		//Merge CUP connector change ends
		
		 //Merge CUP connector change starts
		if(isCUP){
			hmCUPAttrMap = populateCUPUserDataToHash(sCUPAttrMapLookupName,null);
			hmCUPChildAttrMap = populateCUPChildDataToCreate(sCUPChildAttrMapLookupName,
					sAttributeName, sChildPrimaryKey, sChildTableName);
			hmCUPChildAttrMap.put(hmCUPConstants.get(ACTION), "ADD");
		}else {
			String sAttrMapLookupName = configurationMap
					.get(PROV_CHILD_ATTR_MAP);
			attrMap = populateChildDataToCreate(sAttrMapLookupName,
					sAttributeName, sChildPrimaryKey, sChildTableName);
		}
		//Merge CUP connector change ends
			try {
				jcoConnection = connect(sAPConnection);
			} catch (ConnectorException e) {
				sResponse = CONNECTION_ERROR;
				throw new ConnectorException(e);
			}

			SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
					jcoConnection, hmConstants);

			if (sAPProxyUserProvisionManager.findUser(sUserID)) {
				ArrayList<SAPUMAttributeMapBean> dataList = new ArrayList<SAPUMAttributeMapBean>();

				if (!StrUtil.isEmpty(sBapiStructureName)) {
					dataList = attrMap.get(sBapiStructureName);
				}
				
				//Merge CUP connector change starts
				if(isCUP){
					
					String sResponseID = sAPProxyUserProvisionManager.createCUPRequest(
							hmCUPAttrMap, htCUPITRattributes, hmCUPConstants , hmCUPChildAttrMap, sReqType, sApplication, sPriority);
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
					
				}else{
					sResponse = sAPProxyUserProvisionManager.updateMultiValueData(
							dataList, sAttributeName, sUserID, sBapiStructureName,
							sBapiFieldName, sCUAEnable);				

				}
				//Merge CUP connector change ends
			} else {
				sResponse = USER_DOES_NOT_EXIST;
				logger.error(className, sMethodName, "User id :" + sUserID
						+ " does not exist in target SAP system.");
			}
		} catch (ConnectorException exception) {
			if(StrUtil.isEmpty(sResponse))
			sResponse = UPDATE_MULTIVALUE_DATA_FAILED;
			
			logger.error(className, sMethodName, exception.getMessage());
			if(exception.getMessage().startsWith("Connection")){
				sResponse =CONNECTION_ERROR;
			}
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
		} catch (Exception e) {
			if(StrUtil.isEmpty(sResponse))
			sResponse = UPDATE_MULTIVALUE_DATA_FAILED;
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
		} finally {
			try {
				if (jcoConnection != null) {
					if (isPoolingEnabled) {
						ser.releaseConnection(rc);
					} else {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}
			} catch (ConnectorException bException) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, bException.getMessage());
				logger.setStackTrace(bException, className, sMethodName,
						bException.getMessage());
			} catch (Exception exception) {
				logger.error(className, sMethodName, exception.getMessage());
				logger.setStackTrace(exception, className, sMethodName,
						exception.getMessage());
				sResponse = CONNECTION_ERROR;
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
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
	private HashMap<String, ArrayList<SAPUMAttributeMapBean>> populateUserDataToHash(
			String sAttrMapLookupName) throws ConnectorException {
		HashMap<String, ArrayList<SAPUMAttributeMapBean>> attrMap = new HashMap<String, ArrayList<SAPUMAttributeMapBean>>();
		String sCode = null;
		String sDecode = null;
		String sFormField = null;
		String sFormValue = null;
		String sStructureName = null;
		String sFieldName = null;
		ArrayList<SAPUMAttributeMapBean> userDataList = null;
		String sMethodName = "populateUserDataToHash()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
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

					sFieldName = sDecodeVal[1];
					sStructureName = sDecodeVal[2];

					sFormField = formDefResults
							.getStringValue(STRUCT_UTIL_FIELD_LABEL);

					if (sFormField.equalsIgnoreCase(sCode.trim())) {
						sFormValue = formInstResults
								.getStringValue(formDefResults
										.getStringValue(STRUCT_UTIL_COLUMN_NAME));

						if (sDecodeVal[0].equals(hmConstants
								.get(FIELDTYPE_CHECKBOX))) {
							HashMap<String, String> hmCheckBoxMapping = oUtil
									.getLookUpMap((String) configurationMap
											.get(PROV_CHECKBOX));
							sFormValue = oUMUtil.getCheckBoxValueForSAP(
									sCode, sFormValue,
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

						sAPAttributeMapBean.setOIMfieldName(sCode);
						sAPAttributeMapBean.setFieldType(sDecodeVal[0]);
						sAPAttributeMapBean.setBapiStructure(sStructureName);
						sAPAttributeMapBean.setBapiStructureX(sDecodeVal[4]);
						sAPAttributeMapBean.setBapiFieldName(sFieldName);
						sAPAttributeMapBean.setFieldValue(sFormValue);
						sAPAttributeMapBean.setBapiFieldNameX(sDecodeVal[3]);
						userDataList.add(sAPAttributeMapBean);
						attrMap.put(sStructureName, userDataList);
						//stop loop
						iCount =iformDefResCount;
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
		return attrMap;
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
					/*If this condition is true this field is being currently modified
					 * */
					if(!StrUtil.isEmpty(sFieldToValidate) && sFieldToValidate.equalsIgnoreCase(sDecodeVal[3])) {
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
						attrMap.put(sDecodeVal[1]+";"+sDecodeVal[0],sFormValue);
						iCount = iformDefResCount;
						}
						else if(sStd.equalsIgnoreCase(CUSTOM)) {
							// we set a marker if custom value is required, so
							// as to handle creating request
							attrMap.put(sDecodeVal[1] + ";" + sStd + ";" + sDecodeVal[0],sFormValue);
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
				isCUP = false;
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
		try {

			long lChildFormDefKey = oUtil.getChildFormDefKey(
					lProcInstanceKey, childTableName);
			//this resultset contains the definitions of the child form
			tcResultSet childFormDef = fdIntf.getFormFields(lChildFormDefKey,
					(int) oUtil.getChildFormVersion(lProcInstanceKey,
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
							attrMap.put(sDecode, sFormValue);
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
		// Below line added for Bug 12675870 
		String sSubsystem = null;
		ArrayList<SAPUMAttributeMapBean> userDataList = null;
		String sMethodName = "populateChildDataToCreate()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		try {

			long lChildFormDefKey = oUtil.getChildFormDefKey(
					lProcInstanceKey, childTableName);
			//this resultset contains the definitions of the child form
			tcResultSet childFormDef = fdIntf.getFormFields(lChildFormDefKey,
					(int) oUtil.getChildFormVersion(lProcInstanceKey,
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
							//if is CUA we need PROFILE to provision profiles
							if (sCUAEnable.equalsIgnoreCase(hmConstants
									.get(YES))) {
								sFieldName = formattedAttr[0];
							} else {//if is UM we need BAPIPROF to provision profiles
								sFieldName = formattedAttr[1];
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
									sSubsystem = sFormValue;
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
							

							// Start: Bug 12675870 
							if(!StrUtil.isEmpty(sFieldName) && ("AGR_NAME".equalsIgnoreCase(sFieldName) ||
									"BAPIPROF".equalsIgnoreCase(sStructureName) || "BAPIPROF".equalsIgnoreCase(sFieldName))){
								SAPUMAttributeMapBean sAPAttributeMapBean = new SAPUMAttributeMapBean();
								sAPAttributeMapBean.setFieldType("LOOKUP");
								if("ACTIVITYGROUPS".equalsIgnoreCase(sStructureName)){ 
									sAPAttributeMapBean
											.setBapiStructure("ACTIVITYGROUPS");
								} else {
									sAPAttributeMapBean
										.setBapiStructure("PROFILES");
								}
								sAPAttributeMapBean.setBapiFieldName("SUBSYSTEM");
								sAPAttributeMapBean.setFieldValue(sSubsystem.substring(sSubsystem.indexOf('~')+1, sSubsystem.lastIndexOf('~')));
								logger.info("Setting Subsystem value for Role Or Profile -"+sSubsystem.substring(sSubsystem.indexOf('~')+1, sSubsystem.lastIndexOf('~')));

								userDataList.add(sAPAttributeMapBean);
								attrMap.put(sStructureName, userDataList);
							}

							// End: Bug 12675870 
							
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
		return attrMap;
	}

	/**
	 * Description : Adds the dummy resource object to a user
	 * 
	 * @param pUserId
	 *            User ID of the user requesting resource
	 * @param pResourceObjectName
	 *            Name of the resource object requested. E.g: SAP UM Resource
	 *            Object
	 * @param pDummyParentFormName
	 *            Dummy parent form name. E.g: UD_SAPROL_P
	 * @param pDummyChildFormName
	 *            Dummy child form name. E.g: UD_SAPROL_C
	 * @param pActualParentFormName
	 *            Actual parent form name. E.g:- UD_SAP
	 * @param pActualChildFormName
	 *            Actual child form name. E.g:- UD_SAP_ROL
	 * @param pDummyProcessInstanceKey
	 *            Dummy process instance key
	 * @param pLookupName
	 *            Name of the lookup definition that holds field mappings. For
	 *            example: Lookup.SAP.UM.RoleChildFormMapping
	 * @param sITResourceFormNameParent
	 *            Form field name of the IT Resource on the parent form. For
	 *            example:: UD_SAP_ITRESOURCE
	 * @param sITResourceFormNameDummyParent
	 *            Form field name of the IT resource on the dummy parent form
	 *            For example:: UD_SAPROL_P_ITRESOURCE
	 * @return String Status message that indicates either SUCESS or ERROR
	 * @throws ConnectorException
	 */
	public String addDummyResourceToUser(String pUserId,
			String pResourceObjectName, String pDummyParentFormName,
			String pDummyChildFormName, String pActualParentFormName,
			String pActualChildFormName, long pDummyProcessInstanceKey,
			String pLookupName, String sITResourceFormNameParent,
			String sITResourceFormNameDummyParent) throws ConnectorException {

		String methodName = "addDummyResourceToUser()";

		logger.setMethodStartLog(className, methodName);

		long procInstanceKey = 0;
		String userKey = null;
		HashMap<String, String> filterMap = new HashMap<String, String>();

		logger.info(className, methodName, "User ID : " + pUserId);
		logger.info(className, methodName, "Resource object name: "
				+ pResourceObjectName);
		logger.info(className, methodName, "Dummy Parent Form Name : "
				+ pDummyParentFormName);
		logger.info(className, methodName, "Actual Parent Form Name : "
				+ pActualParentFormName);
		logger.info(className, methodName, "Dummy Child Form Name : "
				+ pDummyChildFormName);
		logger.info(className, methodName, "Actual Child Form Name : "
				+ pActualChildFormName);
		logger.info(className, methodName, "Process Instance Key : "
				+ pDummyProcessInstanceKey);
		logger.info(className, methodName, "Lookup Name : " + pLookupName);
		logger.info(className, methodName,
				"Actual Parent Form ITResource Field Name  : "
						+ sITResourceFormNameParent);
		logger.info(className, methodName,
				"Dummy Parent Form ITResource Field Name  : "
						+ sITResourceFormNameDummyParent);

		try {
			String[] sFields = {pUserId, pResourceObjectName,
					pDummyParentFormName, pDummyChildFormName,
					pActualParentFormName, pActualChildFormName, pLookupName,
					sITResourceFormNameParent};
			boolean isValid = oUtil.validateMandatoryValues(sFields);

			if (!isValid) {
				return MANDATORY_FIELD_VALUE_BLANK;
			}

			filterMap.put(Constants.OIM_USER_ID, pUserId);

			tcResultSet rset = oUtil.getUserAPI().findAllUsers(filterMap);
			userKey = rset.getStringValue(Constants.USER_KEY);

			if (userKey == null) {
				return Constants.USER_NOT_FOUND;
			}
			// List of ROs provisioned for a user.
			tcResultSet objResultSet = oUtil.getUserAPI().getObjects(
					Long.parseLong(userKey));

			// Get the count of the particular RO
			int objCount = oUtil.getObjectCount(pResourceObjectName,
					objResultSet);

			logger.debug(className, methodName, "Object Count : " + objCount);

			if (objCount == 0) {
				return Constants.RESOURCE_NOT_PROVISIONED;
			} else if (objCount > 1) {
				return Constants.MORE_THAN_ONE_RO_FUNCTION_NOT_SUPPORTED;
			} else if (objCount == 1) {
				logger.debug(className, methodName, "Received object count:"
						+ objCount);
				logger.debug(className, methodName, "User has "
						+ objResultSet.getRowCount() + " objects");

				for (int i = 0; i < objResultSet.getRowCount(); i++) {
					objResultSet.goToRow(i);

					String objName = objResultSet
							.getStringValue(Constants.OBJECT_NAME);
					String objStatus = objResultSet
							.getStringValue(Constants.OBJECT_STATUS);
					logger.debug(className, methodName, "Object name:"
							+ objName + " Object status:" + objStatus);

					if (((objName != null) && objName
							.equalsIgnoreCase(pResourceObjectName))
							&& ((objStatus != null) && (objStatus
									.equalsIgnoreCase(Constants.PROVISIONED_STATUS) || objStatus
									.equalsIgnoreCase(Constants.ENABLED_STATUS)))) {
						// Get the process instance key of the RO with
						// provisioned / enabled status. This will be
						// used to set the data from dummy process form
						// to actual process form
						procInstanceKey = objResultSet
								.getLongValue(Constants.PROCESS_INSTANCE_KEY);
						logger.debug(className, methodName, "User Process Key:"
								+ procInstanceKey);

						break;
					}
				}

				if (procInstanceKey == 0) {
					logger.debug(className, methodName,
							"Process instance key is zero");

					return Constants.PROCESS_NOT_FOUND;
				}
			}
		} catch (Exception e) {
			logger.error(className, methodName, "Process instance key is zero");

			return "ERROR_OCCURRED";
		}

		// Get the dummy form data from parent
		HashMap hmGetFormData = oUtil.getFormDataMap(pDummyProcessInstanceKey);
		logger.debug(className, methodName,
				"Getting Data from Dummy Parent Form");

		HashMap hmParentFormData = oUtil.getFormDataMap(procInstanceKey);
		String sITResourceKeyActualParentFormName = (String) hmParentFormData
				.get(sITResourceFormNameParent);
		long sITResourceKeyActualParentKey = Long
				.parseLong(sITResourceKeyActualParentFormName);
		logger.info(className, methodName,
				"IT Resource Key used in Actual Parent Form : "
						+ sITResourceKeyActualParentFormName);
		ITResource oITR = new ITResource(sITResourceKeyActualParentKey, oUtil
				.getResAPI(), logger);

		Hashtable hTITR = oITR.getITResourceDetails();
		String sConfigLookup = (String) hTITR.get("SOD Configuration lookup");
		HashMap hmConfig = oUtil.getLookUpMap(sConfigLookup);

		String sodCheckStatus = (String) hmConfig.get("SOD Check Status");
		String sodTrackingID = (String) hmConfig
				.get("SOD Check Tracking ID");
		String sodCheckResult = (String) hmConfig.get("SOD Check Result");
		String sodCheckViolation = (String) hmConfig
				.get("SOD Check Violation");
		String sodCheckTimestamp = (String) hmConfig
				.get("SOD Check Timestamp");

		String sodVal1 = (String) hmGetFormData.get(pDummyParentFormName + "_"
				+ sodCheckStatus);
		String sodVal2 = (String) hmGetFormData.get(pDummyParentFormName + "_"
				+ sodTrackingID);
		String sodVal3 = (String) hmGetFormData.get(pDummyParentFormName + "_"
				+ sodCheckResult);
		String sodVal4 = (String) hmGetFormData.get(pDummyParentFormName + "_"
				+ sodCheckViolation);
		String sodVal5 = (String) hmGetFormData.get(pDummyParentFormName + "_"
				+ sodCheckTimestamp);

		String sITResourceKeyDummyParentFormName = (String) hmGetFormData
				.get(sITResourceFormNameDummyParent);
		logger.info(className, methodName,
				"IT Resource Key used in Dummy Parent Form : "
						+ sITResourceKeyDummyParentFormName);

		// Check if IT Resource key is same in both the requested resource and
		// that present in Actual Parent form.If not equal return error message       
		if (!sITResourceKeyDummyParentFormName
				.equalsIgnoreCase(sITResourceKeyActualParentFormName)) {
			logger
					.info(
							className,
							methodName,
							"IT Resource Name selected during request is different from that in Actual Parent Form");

			return Constants.INVALID_ITRESOURCE;
		}

		// Create a new hashmap to set in actual process form data.
		HashMap<String, String> hmActualParentForm = new HashMap<String, String>();

		hmActualParentForm.put(pActualParentFormName + "_" + sodCheckStatus,
				sodVal1);
		hmActualParentForm.put(pActualParentFormName + "_" + sodTrackingID,
				sodVal2);
		hmActualParentForm.put(pActualParentFormName + "_" + sodCheckResult,
				sodVal3);
		hmActualParentForm.put(pActualParentFormName + "_" + sodCheckViolation,
				sodVal4);
		hmActualParentForm.put(pActualParentFormName + "_" + sodCheckTimestamp,
				sodVal5);

		logger.debug(className, methodName, "hmActualParentForm: "
				+ hmActualParentForm);

		try {
			// Set the actual parent process form data with newly created
			// hashmap Actual RO
			oUtil.getFormAPI().setProcessFormData(procInstanceKey,
					hmActualParentForm);

			// Test RO
			long dummyProcessFormDefKey = 0;
			dummyProcessFormDefKey = oUtil.getFormAPI()
					.getProcessFormDefinitionKey(pDummyProcessInstanceKey);

			long dummyChildFormDefKey = 0;
			String dummyFormName = null;
			logger.debug(className, methodName, "process form version : "
					+ oUtil.getFormAPI().getActiveVersion(
							dummyProcessFormDefKey));

			// Get child form definition result set of the given dummy process
			// form def key
			tcResultSet dummyChildFormDefRslt = oUtil.getFormAPI()
					.getChildFormDefinition(
							dummyProcessFormDefKey,
							oUtil.getFormAPI().getActiveVersion(
									dummyProcessFormDefKey));

			// Get child form definition key from child form definition result
			// set
			if (dummyChildFormDefRslt.getRowCount() > 0) {
				for (int k = 0; k < dummyChildFormDefRslt.getRowCount(); k++) {
					dummyChildFormDefRslt.goToRow(k);
					dummyFormName = dummyChildFormDefRslt
							.getStringValue(STRUCT_UTIL_TABLE_NAME);
					logger.debug(className, methodName, "dummyFormName : "
							+ dummyFormName);
					logger.debug(className, methodName,
							"pDummyChildFormName : " + pDummyChildFormName);

					if (dummyFormName.equalsIgnoreCase(pDummyChildFormName)) {
						dummyChildFormDefKey = dummyChildFormDefRslt
								.getLongValue(STRUCT_UTIL_CHILDTABLE_KEY);

						break;
					}
				}

				logger.debug(className, methodName, "dummyChildFormDefKey : "
						+ dummyChildFormDefKey);
			}

			// Get child form data
			tcResultSet dummyChildFormDataRslt = oUtil.getFormAPI()
					.getProcessFormChildData(dummyChildFormDefKey,
							pDummyProcessInstanceKey);

			// Get the row Count.
			int rowcount = dummyChildFormDataRslt.getRowCount() - 1;

			// If row Count is -1,then we are sending ROW_BLANK.This is done because
			// server returns blank row extra when it triggers the task
			if (rowcount == -1) {
				return "ROW_BLANK";
			}

			// Going to last row so that same role does not get added always
			dummyChildFormDataRslt.goToRow(rowcount);

			HashMap<String, String> hmDummyChildForm = new HashMap<String, String>();
			HashMap<String, String> hmActualChildForm = new HashMap<String, String>();

			String[] columnName = dummyChildFormDataRslt.getColumnNames();
			logger.debug(className, methodName, "columnName : " + columnName);

			if (columnName != null) {
				logger.debug(className, methodName, "columnName.length : "
						+ columnName.length);

				// Loop through child form data and create a new hashmap with the
				// data
				for (int i = 0; i < columnName.length; i++) {
					String formData = dummyChildFormDataRslt
							.getStringValue(columnName[i]);
					hmDummyChildForm.put(columnName[i].trim(), formData.trim());
				}

				logger.debug(className, methodName,
						"Child Form Data Hash map : " + hmDummyChildForm);
			}

			HashMap lookupMap = new HashMap();
			lookupMap = oUtil.getLookUpMap(pLookupName);

			Object attrValue = null;

			// Loop thro lookup map and create a new hashmap with actual child
			// table column name as key and value got from dummy childform
			for (Iterator itr = lookupMap.keySet().iterator(); itr.hasNext();) {
				attrValue = null;

				String mapInfoKey = (String) itr.next();
				logger.debug(className, methodName, "Lookup code value: "
						+ mapInfoKey);

				String mapInfoValue = (String) lookupMap.get(mapInfoKey);
				logger.debug(className, methodName, "Lookup decode value: "
						+ mapInfoValue);
				attrValue = (Object) hmDummyChildForm.get(mapInfoKey);
				logger.debug(className, methodName, "attrValue: " + attrValue);

				if (!StrUtil.isEmpty(mapInfoValue) && (attrValue != null)) {
					StringTokenizer token = new StringTokenizer(mapInfoValue,
							",");
					String dataType = null;

					if (token.hasMoreTokens()) {
						logger.debug(className, methodName, "hasMoreTokens 1 ");
						mapInfoValue = token.nextToken(",");

						if (token.hasMoreTokens()) {
							logger.debug(className, methodName,
									"hasMoreTokens 2 ");
							dataType = token.nextToken(",");
						}
					}

					logger.debug(className, methodName,
							"mapInfoValue modified: " + mapInfoValue);
					logger
							.debug(className, methodName, "dataType: "
									+ dataType);

					if ("DATE".equalsIgnoreCase(dataType)) {
						logger.debug(className, methodName, "attrValue: "
								+ attrValue);

						Date dt = new Date();

						if (!StrUtil.isEmpty((String) attrValue)) {
							DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");

							dt = dft.parse((String) attrValue);
							logger.debug(className, methodName, "Date dt: "
									+ dt.toString());
							Timestamp tStart = new Timestamp(dt.getTime());
							String formattedDateTime = tStart.toString();
							logger.debug(className, methodName,
									"String formattedDateTime: "
											+ formattedDateTime);
							hmActualChildForm.put(mapInfoValue,
									formattedDateTime);
						}
					} else {
						if (!StrUtil.isEmpty(attrValue.toString())) {
							hmActualChildForm.put(mapInfoValue,
									(String) attrValue);
						}
					}
				}
			}

			logger.debug(className, methodName,
					"Hash map prepared for actual process form: "
							+ hmActualChildForm);

			if ((hmActualChildForm == null) || (hmActualChildForm.size() <= 0)) {
				return Constants.PREPARE_ACTUAL_CHILD_DATA_FAILED;
			}

			long processFormDefKey = 0;
			processFormDefKey = oUtil.getFormAPI().getProcessFormDefinitionKey(
					procInstanceKey);

			// Get actual child form definition
			long childFormKey = 0;
			String formName = null;
			tcResultSet childFormDefRslt = oUtil.getFormAPI()
					.getChildFormDefinition(
							processFormDefKey,
							oUtil.getFormAPI().getActiveVersion(
									processFormDefKey));

			if (childFormDefRslt.getRowCount() > 0) {
				for (int k = 0; k < childFormDefRslt.getRowCount(); k++) {
					childFormDefRslt.goToRow(k);
					formName = childFormDefRslt
							.getStringValue(STRUCT_UTIL_TABLE_NAME);

					if (formName.equalsIgnoreCase(pActualChildFormName)) {
						childFormKey = childFormDefRslt
								.getLongValue(STRUCT_UTIL_CHILDTABLE_KEY);

						break;
					}
				}
			}

			logger.info(className, methodName, "Child Form Key..."
					+ childFormKey);

			if (childFormKey == 0) {
				return Constants.CHILD_FORM_NOT_FOUND;
			}

			// add the newly created child form data
			oUtil.getFormAPI().addProcessFormChildData(childFormKey,
					procInstanceKey, hmActualChildForm);
		} catch (Exception e) {
			logger.error(className, methodName, "Row add failed");
			logger.setStackTrace(e, className, methodName, "Row add failed");

			return Constants.ROW_ADD_FAILED;
		}
		logger.setMethodFinishLog(className, methodName);
		return Constants.SUCCESS;

	}
	
	/**
	 * Description : The target system allows you to create custom fields for
	 * users. This method provisions custom attributes for users on the target
	 * system.
	 * 
	 * @param sUserId
	 *            User ID of the user to be modified. For example: John.Doe
	 * @param sUserIDBAPIName
	 *            Attribute in the custom table that holds user ID values. For
	 *            example: BNAME
	 * @param BAPIName
	 *            Name of the custom BAPI that you created for fetching values
	 *            from the custom attribute. For example:
	 *            ZXLCBAPI_ZXLCUSR_USERCHANGE
	 * @param BAPIfieldname
	 *            Name of the attribute in the custom table. For example:
	 *            JOB_DESC
	 * @param BAPIStructurename
	 *            BAPI Structure name in which BAPIfieldname might exist. For
	 *            example: ADDRESS
	 * @param FieldValue
	 *            Value for the custom field to be set on the target system. For
	 *            example: Developer
	 * @param sFieldType
	 *            Type of data that is stored in the custom attribute. It can be
	 *            TEXT, DATE, or CHECKBOX.
	 * @param FormFieldName
	 *            Name of the field on the process form. For example: Job
	 *            Description
	 * @return String Returns the response code that is mapped in the adapter
	 * 
	 * @throws ConnectorException
	 */
	public String modifyCustomAttr(String sUserId, String sUserIDBAPIName,
			String BAPIName, String BAPIfieldname, String BAPIStructurename,
			String FieldValue,String sFieldType, String FormFieldName) throws ConnectorException {
		String sMethodName = "modifyCustomAttr()";
		logger.setMethodStartLog(className, sMethodName);
		String sResponse = null;
		SAPConnection sAPConnection = new SAPConnection(logger);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		try {
			logger.debug(className, sMethodName, "Check if constructor is innitialized");
			if(!isInnitialized){
				sResponse = INSUFFICIENT_INFORMATION;
				logger.error(className, sMethodName, "Innitialization failed");
				throw new ConnectorException(sResponse);
			}
			logger.debug(className, sMethodName, "Validate fields sent from adaptor");
			if (StrUtil.isEmpty(sUserId) || StrUtil.isEmpty(sUserIDBAPIName)
					|| StrUtil.isEmpty(BAPIName)
					|| StrUtil.isEmpty(BAPIfieldname)
					|| StrUtil.isEmpty(BAPIStructurename)
					|| StrUtil.isEmpty(sFieldType)) {
				logger.debug(className, sMethodName, "UserID: " + sUserId
						+ " UserID BAPI FieldName: " + sUserIDBAPIName
						+ " BAPI Name: " + BAPIName
						+ " BAPI Custom Field Name: " + BAPIfieldname
						+ " BAPI Structure Name: " + BAPIStructurename
						+ " Field Type: " + sFieldType);
				logger.error(className, sMethodName,
						"Mandatory values not sent to method");
				sResponse = INSUFFICIENT_INFORMATION;
				throw new ConnectorException(INSUFFICIENT_INFORMATION);
			}
		try {
			logger.debug(className, sMethodName, "connect to SAP");
			jcoConnection = connect(sAPConnection);
		} catch (ConnectorException e) {
			sResponse = CONNECTION_ERROR;
			throw new ConnectorException(e);
		}
		logger.debug(className, sMethodName, "FieldType is checked for checkbox ");
		if (sFieldType.equals(hmConstants
				.get(FIELDTYPE_CHECKBOX))) {
			HashMap<String, String> hmCheckBoxMapping = oUtil
					.getLookUpMap((String) configurationMap
							.get(PROV_CHECKBOX));
			FieldValue = oUMUtil.getCheckBoxValueForSAP(
					FormFieldName, FieldValue,
					hmCheckBoxMapping);
			logger.info(className, sMethodName, "Modifying custom field: "+FormFieldName);
		}
		SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
				jcoConnection, hmConstants);
		logger.debug(className, sMethodName, "Validate if user exists");
			if (sAPProxyUserProvisionManager.findUser(sUserId)) {
				sResponse = sAPProxyUserProvisionManager.modifyCustomAttr(sUserId, sUserIDBAPIName,
						BAPIName, BAPIfieldname, BAPIStructurename,
						FieldValue);
			} else {
				logger.error(className, sMethodName, "User id :" + sUserId
						+ " does not exist in target SAP system.");
				sResponse = USER_DOES_NOT_EXIST;
			}
		} catch (ConnectorException bException) {
			logger.error(className, sMethodName, bException.getMessage());
			if(bException.getMessage().startsWith("Connection")){
				sResponse =CONNECTION_ERROR;
			}
			logger.setStackTrace(bException, className, sMethodName, bException
					.getMessage());
			if(StrUtil.isEmpty(sResponse))
			sResponse = USER_MODIFICATION_FAILED;
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			sResponse = USER_MODIFICATION_FAILED;
		} finally {
			try {
				if (jcoConnection != null) {
					if (isPoolingEnabled) {
						ser.releaseConnection(rc);
					} else {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}
			} catch (ConnectorException exception) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, exception.getMessage());
				logger.setStackTrace(exception, className, sMethodName,
						exception.getMessage());
			} catch (Exception e) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, e.getMessage());
				logger.setStackTrace(e, className, sMethodName, e.getMessage());
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sResponse;
	} 
	
	/**
	 * Description:This method is used to add custom multi-value attribute to a user,
	 * like structural profiles
	 * 
	 * @param sUserID
	 *            User ID of the user to be modified. For example: John.Doe
	 * @param UserIDFieldName
	 * 			  User ID field in BAPI . For example: USERNAME
	 * @param AttributeName
	 *            Key multi-value Attribute value in the child form. For example:
	 *            1~E60~V_SYS_ADMIN
	 * @param ChildPrimaryKey
	 *            UDField of key child Attribute. For example: UD_SAP_PRO
	 * @param ChildTableName
	 *            Child form table name. For example: UD_SAP
	 * @param BapiFieldName
	 *            SAP field name of Key multivalued attribute in the child form.
	 *            For example: PROFILE
	 * @param BapiStructureName
	 *            Structure name in the BAPI. Within this the BAPI field name
	 *            exists. For example: PROFILES
	 * @param GetAttrBAPIName
	 * 			  Custom BAPI which will retrieve all the child multivalued attributes
	 * 			  from the target. For example: ZBAPI_USER_STRUCTPROFILES_READ
	 * @param BAPI
	 * 			  Name of the custom BAPI which will add the child multivalued attribute
	 * 
	 * @return String Returns the response code which is mapped in the Adapter.
	 * @throws ConnectorException
	 */
	public String addCustomChildAttributes(String sUserID,String UserIDFieldName,
			String AttributeName, String ChildPrimaryKey,
			String ChildTableName, String BapiStructureName,
			String BapiFieldName, String GetAttrBAPIName, String BAPI)
			throws ConnectorException {
		String sResponse = null;
		String sMethodName = "addCustomChildAttributes()";
		logger.setMethodStartLog(className, sMethodName);
		SAPConnection sAPConnection = new SAPConnection(logger);
		HashMap<String, ArrayList<SAPUMAttributeMapBean>> attrMap = new HashMap<String, ArrayList<SAPUMAttributeMapBean>>();
		try {
		if(!isInnitialized){
			sResponse = INSUFFICIENT_INFORMATION;
			logger.error(className, sMethodName, "Innitialization failed");
			throw new ConnectorException(sResponse);
		}
		
		if (StrUtil.isEmpty(sUserID) || StrUtil.isEmpty(UserIDFieldName)
				|| StrUtil.isEmpty(ChildPrimaryKey)
				|| StrUtil.isEmpty(ChildTableName)
				|| StrUtil.isEmpty(BapiStructureName)
				|| StrUtil.isEmpty(BapiFieldName)) {
			logger.error(className, sMethodName,
					"Insufficient Information : Provide compulsory fields.");
			logger.error(className, sMethodName, "userId :" + sUserID
					+ ", UserID Field Name:" + UserIDFieldName + ", ChildPrimaryKey:"
					+ ChildPrimaryKey + ", ChildTableName:" + ChildTableName
					+ ", BapiStructureName:" + BapiStructureName
					+ ", BapiFieldName:" + BapiFieldName);
			sResponse = INSUFFICIENT_INFORMATION;
			throw new ConnectorException(sResponse);
		}

			String sAttrMapLookupName = configurationMap
					.get(PROV_CHILD_ATTR_MAP);
			attrMap = populateChildDataToCreate(sAttrMapLookupName,
					AttributeName, ChildPrimaryKey, ChildTableName);
			try {
				jcoConnection = connect(sAPConnection);
			} catch (ConnectorException e) {
				sResponse = CONNECTION_ERROR;
				throw new ConnectorException(e);
			}
			SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
					jcoConnection, hmConstants);
			if (sAPProxyUserProvisionManager.findUser(sUserID)) {
				ArrayList<SAPUMAttributeMapBean> dataList = new ArrayList<SAPUMAttributeMapBean>();

				if (!StrUtil.isEmpty(BapiStructureName)) {
					dataList = attrMap.get(BapiStructureName);
				}
			sResponse = sAPProxyUserProvisionManager.addCustomMultiValueData(
					dataList, sUserID,UserIDFieldName,GetAttrBAPIName, BapiStructureName, BapiFieldName,
					BAPI, sCUAEnable);
			} else {
				sResponse = USER_DOES_NOT_EXIST;
				logger.error(className, sMethodName, "User id :" + sUserID
						+ " does not exist in target SAP system.");
			}
		} catch (ConnectorException exception) {
			sResponse = ADD_MULTIVALUE_DATA_FAILED;
			if(exception.getMessage().startsWith("Connection")){
				sResponse =CONNECTION_ERROR;
			}
			logger.error(className, sMethodName, exception.getMessage());
		} catch (Exception e) {
			sResponse = ADD_MULTIVALUE_DATA_FAILED;
			logger.error(className, sMethodName, e.getMessage());
		} finally {
			try {
				if (jcoConnection != null) {
					if (isPoolingEnabled) {
						ser.releaseConnection(rc);
					} else {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}
			} catch (ConnectorException bException) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, bException.getMessage());
				logger.setStackTrace(bException, className, sMethodName,
						bException.getMessage());
			} catch (Exception exception) {
				logger.error(className, sMethodName, exception.getMessage());
				logger.setStackTrace(exception, className, sMethodName,
						exception.getMessage());
				sResponse = CONNECTION_ERROR;
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sResponse;
	}
	
	/**
	 * Description:Used to remove a custom multivalued attribute from a user
	 * 
	 * @param sUserID
	 *            User ID of the user to be modified. For example: John.Doe
	 * @param sUserIdBAPIName
	 * 			  The corresponding field label for the user name in the BAPI.
	 * 			  e.g. USERNAME          
	 * @param sAttributeName
	 *            MultiValued Attribute value in the child form which is to be
	 *            deleted. For example: 1~E60~V_SYS_ADMIN
	 * @param sBapiFieldName
	 *            Structure name in the BAPI. The BAPI field name is part of
	 *            this structure. For example: PROFILE
	 * @param sBAPIStructureName
	 * 			  Table name in the BAPI which is to be populated with the multi-
	 * 			  valued attribute to be deleted  e.g. PROFILES
	 * @param sBAPIName
	 * 			  Name of the custom BAPI created to delete the attribute.
	 * 			  e.g. ZBAPI_REMOVE_ATTR
	 * @return String Returns the response code that is mapped in the adapter
	 * @throws ConnectorException
	 */
	public String removeCustomMultivalueData(String sUserID, String sUserIdBAPIName,
			String sAttributeName, String sBapiFieldName,String sBAPIStructureName,
			String sBAPIName) throws ConnectorException {
		String sResponse = null;
		SAPConnection sAPConnection = new SAPConnection(logger);
		String sMethodName = "removeCustomMultivalueData()";
		logger.setMethodStartLog(className, sMethodName);		
		try {
		if(!isInnitialized){
			sResponse = INSUFFICIENT_INFORMATION;
			logger.error(className, sMethodName, "Innitialization failed");
			throw new ConnectorException(sResponse);
		}
		
		if (StrUtil.isEmpty(sUserID) || StrUtil.isEmpty(sBapiFieldName)
				|| StrUtil.isEmpty(sAttributeName)|| StrUtil.isEmpty(sUserIdBAPIName)
				|| StrUtil.isEmpty(sBAPIName)|| StrUtil.isEmpty(sBAPIStructureName)) {
			logger.error(className, sMethodName,
					"Incorrect information sent to method");
			sResponse = INSUFFICIENT_INFORMATION;
			throw new ConnectorException(sResponse);
		}
		
	try {
			jcoConnection = connect(sAPConnection);
		} catch (ConnectorException e) {
			sResponse = CONNECTION_ERROR;
			throw new ConnectorException(e);
		}
		
			SAPUMProxyUserProvisionManager sAPProxyUserProvisionManager = new SAPUMProxyUserProvisionManager(
					jcoConnection, hmConstants);			
			if (sAPProxyUserProvisionManager.findUser(sUserID)) {				
				sResponse = sAPProxyUserProvisionManager
					.removeCustomMultiValueData( sUserID,sUserIdBAPIName,sBAPIStructureName,sBAPIName,
							sBapiFieldName,sAttributeName, sCUAEnable);							
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
		} finally {
			try {
				if (jcoConnection != null) {
					if (isPoolingEnabled) {
						ser.releaseConnection(rc);
					} else {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}
			} catch (ConnectorException bException) {
				sResponse = CONNECTION_ERROR;
				logger.error(className, sMethodName, bException.getMessage());
				logger.setStackTrace(bException, className, sMethodName,
						bException.getMessage());
			} catch (Exception exception) {
				logger.error(className, sMethodName, exception.getMessage());
				logger.setStackTrace(exception, className, sMethodName,
						exception.getMessage());
				sResponse = CONNECTION_ERROR;
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sResponse;
	}
// Start :: Added this method for the bug 10408848
	/**
	 * Description:Used to update the valid through field in OIM Process Form as that of      
           Target SAP
	 * 
	 * @param sUserID
	 *            User ID of the user to be modified. For example: John.Doe
	 * @param sBAPIFieldValue
	 * 			  Valid Through value
	 * @throws Exception
	 */
	
	private void updateValidThrDate(String sUserID, String sBAPIFieldValue){
		Date dtValidThro = null;
		String sMethodName = "updateValidThrDate()";
		try {
			logger.setMethodStartLog(className, sMethodName);
			SAPUtil oSAPUtil = new SAPUtil(logger);
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(
					jcoConnection, (String) hmConstants
							.get(BAPI_USER_GET_DETAIL));
			JCoParameterList input = jcoFunction
					.getImportParameterList();
			input.setValue((String) hmConstants.get(USERNAME), sUserID);
			jcoFunction.execute(jcoConnection);
			JCoStructure sapStructure = jcoFunction
					.getExportParameterList().getStructure("LOGONDATA");
			Object fieldValue = (sapStructure.getValue("GLTGB"));
			Date dtValidThroFromTarget = (Date) fieldValue;
			String strTargetDate = dtUtil.parseTime(
					dtValidThroFromTarget, hmConstants
							.get(EnableDisableDF));
			dtValidThroFromTarget = dtUtil.returnDate(strTargetDate,
					hmConstants.get(EnableDisableDF));

			Timestamp tStart = new Timestamp(dtValidThroFromTarget.getTime());
			String strFormattedDateForTarget = tStart.toString();
			dtValidThro = dtUtil.returnDate(sBAPIFieldValue, hmConstants
					.get(EnableDisableDF));
			long mymillidate=dtValidThro.getTime();
			dtValidThro= new Date(mymillidate);
			String sDate = dtUtil.parseTime(dtValidThro, hmConstants
					.get(EnableDisableDF));
			dtValidThro = dtUtil.returnDate(sDate, hmConstants
					.get(EnableDisableDF));
			HashMap<String, String> hmMap = new HashMap<String, String>();
			if (dtValidThro.before(dtValidThroFromTarget)) {
				//hmMap.put(ValidThroUD, strFormattedDateForTarget);
				hmMap.put(configurationMap.get(UDFIELD_FOR_VALIDTHROUGH), strFormattedDateForTarget);
				// ValidThro in OIM is set as Target ValidThro date
				fiIntf.setProcessFormData(lProcInstanceKey, hmMap);
			}

		} catch (Exception ex) {
			logger.error(className, sMethodName, "Update Valid Through Date "
					+ " Exception for user id :" + sUserID);
			logger.error(className, sMethodName, ex.getMessage());
			ex.printStackTrace();
		}
	}
// End :: Added this method for the bug 10408848
 //Start: Bug 18342752- WHEN AN USER FROM CUA IS DISABLED AND ENABLED AGAIN, THE USER IS STILL DISABLED
private  String getUserLockStatus(String sUserID){
		String lockStatus=null;


		String sMethodName = "getUserLockStatus()";
		final JCoTable jcoTable;
		final StringBuffer whereClause;
        String fieldName;
	    int offSet=0;
	    int length=0;
		try {
			logger.setMethodStartLog(className, sMethodName);
			SAPUtil oSAPUtil = new SAPUtil(logger);
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(
					jcoConnection, (String) hmConstants
							.get(RFC_READ_TABLE));
			JCoParameterList input = jcoFunction.getImportParameterList();
			input.setValue(QUERY_TABLE,USR02_TABLE);			
				jcoTable = jcoFunction.getTableParameterList().getTable(OPTIONS);
				jcoTable.appendRow();
		        
		        whereClause = new StringBuffer(25);
		        whereClause.append("BNAME EQ '");
		        whereClause.append(sUserID);
		        whereClause.append("'");
		        jcoTable.setValue(TEXT, whereClause.toString());
		        JCoTable jcoTable1 = jcoFunction.getTableParameterList().getTable("FIELDS");
		        jcoTable1.appendRow();
		        jcoTable1.setValue(FIELDNAME,"UFLAG");
			    jcoFunction.execute(jcoConnection);
			    JCoParameterList tableParameterList = jcoFunction.getTableParameterList();
		        JCoTable datum = tableParameterList.getTable(DATA);
		        final int numRows;
		        numRows = datum.getNumRows();
		        if (numRows == 1){
			    	   lockStatus = datum.getString("WA");
			       }			
		} catch (Exception ex) {
			logger.error(className, sMethodName," Exception for user id :" + sUserID);
			logger.error(className, sMethodName, ex.getMessage());
			ex.printStackTrace();
		}		
		return lockStatus;
	}
 //End: Bug 18342752- WHEN AN USER FROM CUA IS DISABLED AND ENABLED AGAIN, THE USER IS STILL DISABLED


	// START :Code Modification for BUG 13475350
	/**
	 * Description:Triggered to remove the System from User from SAP Target.

	 * 
	 * @param sUserID

	 *            User ID of the user to be modified. For example: John.Doe
	 * @param sAttributeValue
	 *            Role/profile which is removed
	 * @throws Exception
	 */




	public void removeSystem(String sUserID, String sAttributeValue) {
		String sMethodName = "removeSystem()";






































		UMUtility util = new UMUtility(logger, hmConstants);
		SAPConnection sAPConnection = new SAPConnection(logger);
		jcoConnection = connect(sAPConnection);
		String SystemName = null;
		// get System Name from the attribute

		StringTokenizer stProf = new StringTokenizer(sAttributeValue, "~");
		int iProfTokenCount = stProf.countTokens();
		int iprof = 0;
		String[] formattedProfAttr = new String[iProfTokenCount];
		while (stProf.hasMoreTokens()) {
			formattedProfAttr[iprof++] = stProf.nextToken();
		}
		SystemName = formattedProfAttr[1];
		new SAPUMProxyUserProvisionManager(jcoConnection, hmConstants)
				.removeSystem(sUserID, SystemName);
	}


	// END:Code Modification for BUG 13475350
}

