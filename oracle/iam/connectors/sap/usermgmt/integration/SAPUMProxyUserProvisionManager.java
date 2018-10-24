/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/usermgmt/integration/SAPUMProxyUserProvisionManager.java /main/75 2017/12/01 02:55:31 samrgupt Exp $ */

/* Copyright (c) 2009, 2017, Oracle and/or its affiliates. 
All rights reserved.*/

/*
 DESCRIPTION
 <short description of component this file declares/defines>

 PRIVATE CLASSES
 <list of private classes defined - with one-line descriptions>

 NOTES
 <other useful comments, qualifications, etc.>

 MODIFIED    	(MM/DD/YY)
 ddkumar     	07/10/09 - Creation
 K S Santosh    01/19/11 - Bug 11070597 - Added logger to print BAPI Name & its parameter
 Niranjan K R   05/04/11 - BUG 12334992 - SAP UM CONNECTOR TO PROPAGATE PASSWORD CHANGES TO CUA CHILD SYSTEMS 
 Akshata.Kulkarni 12/08/11 -BUG 13475350 - SYSTEM IS NOT GET REMOVED WHEN ALL THE ROLES & PROFILES FOR THAT SYSTEM GET REMOVED
 */
package oracle.iam.connectors.sap.usermgmt.integration;
 
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.util.DateUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.sap.common.util.SAPUtil;
import oracle.iam.connectors.sap.cup.ws.audit.ArrayOfAuditLogDTO1;
import oracle.iam.connectors.sap.cup.ws.audit.ArrayOfRequestHistoryDTO;
import oracle.iam.connectors.sap.cup.ws.audit.AuditLogDTO;
import oracle.iam.connectors.sap.cup.ws.audit.AuditLogResult;
import oracle.iam.connectors.sap.cup.ws.audit.RequestHistoryDTO;
import oracle.iam.connectors.sap.cup.ws.audit.SAPGRCACIDMAUDITTRAIL;
import oracle.iam.connectors.sap.cup.ws.audit.SAPGRCACIDMAUDITTRAILViDocument;
import oracle.iam.connectors.sap.cup.ws.getstatus.RequestStatusDTO;
import oracle.iam.connectors.sap.cup.ws.getstatus.SAPGRCACIDMREQUESTSTATUS;
import oracle.iam.connectors.sap.cup.ws.getstatus.SAPGRCACIDMREQUESTSTATUSViDocument;
import oracle.iam.connectors.sap.cup.ws.submitreq.ArrayOfCustomFieldsDTO;
import oracle.iam.connectors.sap.cup.ws.submitreq.ArrayOfRoleData;
import oracle.iam.connectors.sap.cup.ws.submitreq.CustomFieldsDTO;
import oracle.iam.connectors.sap.cup.ws.submitreq.ObjectFactory;
import oracle.iam.connectors.sap.cup.ws.submitreq.RequestDetailsData;
import oracle.iam.connectors.sap.cup.ws.submitreq.RequestSubmissionResult;
import oracle.iam.connectors.sap.cup.ws.submitreq.RoleData;
import oracle.iam.connectors.sap.cup.ws.submitreq.SAPGRCACIDMSUBMITREQUEST;
import oracle.iam.connectors.sap.cup.ws.submitreq.SAPGRCACIDMSUBMITREQUESTViDocument;
import oracle.iam.connectors.sap.cup.ws.submitreq.ServiceStatusDTO;
import oracle.iam.connectors.sap.usermgmt.util.SAPUMAttributeMapBean;
import oracle.iam.connectors.sap.usermgmt.util.UMConstants;
import oracle.iam.connectors.sap.usermgmt.util.UMUtility;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

/**
 * Description: Contains methods to communicate with the target system
 */

public class SAPUMProxyUserProvisionManager implements UMConstants {

	private static ConnectorLogger logger = new ConnectorLogger(UM_LOGGER);
	private StringUtil StrUtil = new StringUtil();
	private JCoDestination jcoConnection;
	private String className = this.getClass().getName();
	private DateUtil dtUtil = new DateUtil(logger);
	private HashMap<String, String> hmConstants;

	/**
	 * Description:Used to initialize global variables
	 * 
	 * @param jcoConnection
	 *          holds connection details to SAP
	 * @param hmConstants
	 *          HashMap containing constants used in this class
	 * 
	 */
	public SAPUMProxyUserProvisionManager(JCoDestination jcoConnection,
			HashMap<String, String> hmConstants) {
		this.jcoConnection = jcoConnection;
		this.hmConstants = hmConstants;
	}

	/**
	 * Description:Used to provision a user on the target system by running
	 * BAPI_USER_CREATE
	 * 
	 * @param userDataMap
	 *          Contains attribute-mapped fields and their values in the format
	 *          key: Structure Name, value: ArrayList containing beans . For
	 *          example: [ADDRESS,[BEAN1,BEAN2]]
	 * @see oracle.iam.connectors.sap.usermgmt.util.SAPUMAttributeMapBean
	 * @param sUserID
	 *          User ID of the user to be created. For example: John.Doe
	 * @param sNewPassword
	 *          Password entered on the process form while creating a user
	 * @param sDummyPwd
	 *          Used to reset the password. The value is taken from the IT
	 *          resource
	 * @param isCUA
	 *          Flag to specify whether the target system is CUA or UM. For
	 *          example: yes
	 * @param sSystemName
	 *          System name of the target system. For example: E60
	 * @param sSyncPass
	 *          Flag to specify if the password must be synchronized by running
	 *          the Change Password task. For example: 'yes'
	 * 
	 * @return String Returns the response code that is mapped in the adapter
	 * 
	 * @throws ConnectorException
	 */
	public String createUser(
			HashMap<String, ArrayList<SAPUMAttributeMapBean>> userDataMap,
			String sUserID, String sNewPassword, String sDummyPwd, String isCUA,
			String sSystemName, String sSyncPass) throws ConnectorException {
		String sStructureName = null;
		String sStatusMsg = null;
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		HashMap<String, String> hashStatusMessage = new HashMap<String, String>();
		SAPUtil oSAPUtil = new SAPUtil(logger);
		String sMethodName = "createUser()";
		try {
			logger.setMethodStartLog(className, sMethodName);
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					hmConstants.get(BAPI_USER_CREATE));
			JCoParameterList jcoParameterList = jcoFunction.getImportParameterList();
			JCoStructure jcoStructure = null;
			Set<?> dataKeys = userDataMap.keySet();

			if (dataKeys != null) {
				Iterator<?> dataKeyIterator = dataKeys.iterator();

				while (dataKeyIterator.hasNext()) {
					sStructureName = (String) dataKeyIterator.next();

					if (!StrUtil.isEmpty(sStructureName)) {
						if (!sStructureName.equalsIgnoreCase(hmConstants.get(NONE))) {
							jcoStructure = jcoParameterList.getStructure(sStructureName);
						}
					}

					ArrayList<SAPUMAttributeMapBean> userDataList = (ArrayList<SAPUMAttributeMapBean>) userDataMap
							.get(sStructureName);

					if ((userDataList != null) && !userDataList.isEmpty()) {
						int iuserDataList = userDataList.size();

						for (int index = 0; index < iuserDataList; index++) {
							SAPUMAttributeMapBean sAPAttributeMapBean = (SAPUMAttributeMapBean) userDataList
									.get(index);

							if (sStructureName.equalsIgnoreCase(hmConstants.get(NONE))) {
				//				logger.debug(className, sMethodName, "Setting " + sAPAttributeMapBean.getBapiFieldName() +" to "+sAPAttributeMapBean.getFieldValue());
								jcoParameterList.setValue(sAPAttributeMapBean
										.getBapiFieldName(), sAPAttributeMapBean.getFieldValue());
							} else {
								if (sStructureName.equalsIgnoreCase(hmConstants.get(PASSWORD))
										&& sSyncPass.equalsIgnoreCase(hmConstants.get(YES))) {
									logger.debug(className, sMethodName, "Setting Password in " + sStructureName + " Structure");
									jcoStructure.setValue(sAPAttributeMapBean.getBapiFieldName(),
											sDummyPwd);
								} else {
				//					logger.debug(className, sMethodName, "Setting " + sStructureName + "_" + sAPAttributeMapBean.getBapiFieldName() +" to "+sAPAttributeMapBean.getFieldValue());
									jcoStructure.setValue(sAPAttributeMapBean.getBapiFieldName(),
											sAPAttributeMapBean.getFieldValue());
								}
							}
						}
					}
				}
			}

			logger.info(className, sMethodName, "Execute Create user BAPI");
			jcoFunction.execute(jcoConnection);
			hashStatusMessage = oUMUtil.getBAPIStatus(jcoFunction, "");

			if (hashStatusMessage.get(hmConstants.get(TYPE)).equals(
					hmConstants.get(S))) {
				if (isCUA.equalsIgnoreCase(hmConstants.get(YES))) {
					logger
							.info(className, sMethodName, sUserID + " created successfully");
					logger.info(className, sMethodName, "CUA environment:Adding system "
							+ sSystemName + " to user");
					try {
						sStatusMsg = addMasterSystemToUser(sUserID, sSystemName);
					} catch (ConnectorException e) {
						logger.error(className, sMethodName,
								"Check if master system name entry in IT Resource is correct");
						sSyncPass = hmConstants.get(NO);
						logger
								.info(className, sMethodName,
										"Password sync will not be done since system could not be assigned to user");
					}
					if (!StrUtil.isEmpty(sStatusMsg)
							&& !sStatusMsg.equalsIgnoreCase(ADD_SYSTEM_SUCCESSFUL)) {
						logger.error(className, sMethodName, "Add system to user");
						sStatusMsg = deleteUser(sUserID);
						if (!sStatusMsg.equalsIgnoreCase(USER_DELETION_SUCCESSFUL))
							logger.error(className, sMethodName, "Account deletion error");
						throw new ConnectorException("createUser():Create User Exception");
					} else {
						logger.info(className, sMethodName, "System added to CUA user");
						sStatusMsg = USER_CREATION_SUCCESSFUL;
					}
				} else {
					sStatusMsg = USER_CREATION_SUCCESSFUL;
					logger
							.info(className, sMethodName, sUserID + " created successfully");
				}
				if (sSyncPass.equalsIgnoreCase(hmConstants.get(YES))) {
					sStatusMsg = changePassword(sUserID, sDummyPwd, sNewPassword);
					if (!StrUtil.isEmpty(sStatusMsg)
							&& sStatusMsg.equalsIgnoreCase(PASSWORD_CHANGE_SUCCESSFUL)) {
						logger.info(className, sMethodName,
								"Password sychronized successfully");
						sStatusMsg = USER_CREATION_SUCCESSFUL;
					} else {
						logger.error(className, sMethodName, "Password sychronize failed");
						sStatusMsg = deleteUser(sUserID);
						if (!sStatusMsg.equalsIgnoreCase(USER_DELETION_SUCCESSFUL))
							logger.error(className, sMethodName, "Account deletion error");
						throw new ConnectorException("createUser():Create User Exception");
					}
				}// end sync password

			} else {
				logger.error(className, sMethodName, "Create user execution failed");
				logger.error(className, sMethodName, hashStatusMessage.get(hmConstants
						.get(MESSAGE)));
				sStatusMsg = USER_CREATION_FAILED;
			}
		} catch (JCoException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.error(className, sMethodName, e.getMessageText());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			sStatusMsg = USER_CREATION_FAILED;
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sStatusMsg = USER_CREATION_FAILED;
			throw new ConnectorException(exception);
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sStatusMsg;
	}

	/**
	 * Description:Used to modify standard attributes of a user by running
	 * BAPI_USER_CHANGE
	 * 
	 * @param sUserID
	 *          User ID of the user to be modified For example: John.Doe
	 * @param sBAPIFieldName
	 *          Field name of the value to be modified in the BAPI. For example:
	 *          TEL1_NUMBR
	 * @param sBAPIStructure
	 *          Structure name in the BAPI. The BAPI field is part of the
	 *          structure. For example: ADDRESS
	 * @param sBAPIFieldNameX
	 *          Name of the field that is used to indicate whether or not the
	 *          value in sBAPIFieldName must be applied. The value 'X' is placed
	 *          in this field to indicate that sBAPIFieldName must be changed. For
	 *          example: TEL1_NUMBR
	 * @param sBAPIStructureX
	 *          Name of the structure that holds sBAPIFieldNameX. For example:
	 *          ADDRESSX
	 * @param sBAPIFieldValue
	 *          Field value entered on process form For example: 9812373445
	 * @return String Returns the response code that is mapped in the adapter
	 * 
	 * @throws ConnectorException
	 */
	public String modifyUser(String sUserID, String sBAPIFieldName,
			String sBAPIStructure, String sBAPIFieldNameX, String sBAPIStructureX,
			String sBAPIFieldValue) throws ConnectorException {
		String sStatus = null;
		HashMap<String, String> hashStatusMessage = new HashMap<String, String>();
		String sMethodName = "modifyUser()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					hmConstants.get(BAPI_USER_CHANGE));

			JCoParameterList jcoParameterList = jcoFunction.getImportParameterList();
			logger.debug(className, sMethodName, "Setting " + hmConstants.get(USERNAME) +" - "+sUserID);
			jcoParameterList.setValue(hmConstants.get(USERNAME), sUserID);

			JCoStructure jcoStructure = jcoFunction.getImportParameterList()
					.getStructure(sBAPIStructure);
			logger.debug(className, sMethodName, "Setting " +sBAPIStructure+ "_"+ sBAPIFieldName +" to "+sBAPIFieldValue);
			jcoStructure.setValue(sBAPIFieldName, sBAPIFieldValue);

			JCoStructure jcoStructureX = jcoFunction.getImportParameterList()
					.getStructure(sBAPIStructureX);
			logger.debug(className, sMethodName, "Setting " +sBAPIStructureX+ "_"+sBAPIFieldNameX +" - "+hmConstants.get(X));
			jcoStructureX.setValue(sBAPIFieldNameX, hmConstants.get(X));

			logger.info(className, sMethodName, "Execute Update User BAPI");
			jcoFunction.execute(jcoConnection);
			hashStatusMessage = oUMUtil.getBAPIStatus(jcoFunction, "");

			if (hashStatusMessage.get(hmConstants.get(TYPE)).equals(
					hmConstants.get(S))) {
				logger.info(className, sMethodName, sUserID + " Modified Successfully");
				sStatus = USER_MODIFIED_SUCCESSFUL;
			} else {
				logger.error(className, sMethodName, "User update Failed");
				logger.error(className, sMethodName, hashStatusMessage.get(hmConstants
						.get(MESSAGE)));
				sStatus = USER_MODIFICATION_FAILED;
			}
		} catch (JCoException jcoException) {
			logger.error(className, sMethodName, jcoException.getMessage());
			logger.setStackTrace(jcoException, className, sMethodName, jcoException
					.getMessage());
			sStatus = USER_MODIFICATION_FAILED;
			throw new ConnectorException(jcoException);
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sStatus = USER_MODIFICATION_FAILED;
			throw new ConnectorException(exception);
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sStatus = USER_MODIFICATION_FAILED;
			throw new ConnectorException(exception);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sStatus;
	}

	/**
	 * Description:Used to determine if a user already exists on the target system
	 * by running BAPI_USER_EXISTENCE_CHECK
	 * 
	 * @param sUserID
	 *          User ID of the user to be checked on the target system. For
	 *          example: John.Doe
	 * 
	 * @return boolean
	 * @throws ConnectorException
	 */
	public boolean findUser(String sUserID) throws ConnectorException {
		HashMap<String, String> hashStatus = new HashMap<String, String>();
		String sMethodName = "findUser()";
		boolean isReturn;
		logger.setMethodStartLog(className, sMethodName);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		try {
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					hmConstants.get(BAPI_USER_EXISTENCE_CHECK));

			JCoParameterList jcoParameterList = jcoFunction.getImportParameterList();
			logger.debug(className, sMethodName, "Setting " + hmConstants.get(USERNAME) +" - "+sUserID);
			jcoParameterList.setValue(hmConstants.get(USERNAME), sUserID);
			jcoFunction.execute(jcoConnection);
			hashStatus = oUMUtil.getBAPIStatus(jcoFunction, hmConstants.get(Export));

			if (hashStatus.get(hmConstants.get(NUMBER))
					.equals(hmConstants.get(No088))) {
				logger.info(className, sMethodName, "SAP User Existence Check : "
						+ (String) hashStatus.get(hmConstants.get(MESSAGE)));
				isReturn = true;
			} else if (hashStatus.get(hmConstants.get(NUMBER)).equals(
					hmConstants.get(No124))) {
				logger.info(className, sMethodName, "SAP User Existence Check : "
						+ (String) hashStatus.get(hmConstants.get(MESSAGE)));
				isReturn = false;
			} else {
				logger.error(className, sMethodName, "Error:"
						+ (String) hashStatus.get(hmConstants.get(MESSAGE)));
				throw new ConnectorException("Find User Error");
			}
		} catch (JCoException jcoException) {
			logger.error(className, sMethodName, jcoException.getMessage());
			logger.setStackTrace(jcoException, className, sMethodName, jcoException
					.getMessage());
			throw new ConnectorException(jcoException);
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			if (exception.getMessage().startsWith("Connection")) {
				throw new ConnectorException("Connection error occured", exception);
			}
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		}
		return isReturn;
	}
	
	
	/**
	 * Description : Links the employee to a user
	 * 
	 * @param sUserId
	 *          SAP User ID that is to be linked to the employee record. For
	 *          example: John.Doe
	 * @param sEmplId
	 *          The Employee Number that links with the R/3 User. For example:
	 *          1000
	 * @return String Returns either '0' or '1' depending on the link status.
	 * @throws ConnectorException
	 */
	public boolean createLink(String sUserId, String sEmplId)
			throws ConnectorException {
		String sMethodName = "createLink()";
		logger.setMethodStartLog(className, sMethodName);
		JCoStructure returnStructure2 = null;
		boolean isLock = true;
		boolean isLink = false;
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {
			// get a client connection
			// create a repository
			logger.info(className, sMethodName, " sUserId: " + sUserId + " sEmplId: "
					+ sEmplId);
			// we execute the 1st BAPI to lock a HRMS user to modify it
			JCoFunction findFunction = oSAPUtil.getJCOFunction(jcoConnection,
					BAPI_EMPLOYEET_ENQUEUE);
			JCoParameterList findParameterList = findFunction
					.getImportParameterList();
			findParameterList.setValue(hmConstants.get(NUMBER), sEmplId);
			findFunction.execute(jcoConnection);
			JCoStructure returnStructure1 = findFunction.getExportParameterList()
					.getStructure(hmConstants.get(RETURN));
			if (!returnStructure1.getString(hmConstants.get(NUMBER))
					.equalsIgnoreCase(hmConstants.get(No000))) {
				logger.error(className, sMethodName, "createLink(): "
						+ returnStructure1.getString(hmConstants.get(MESSAGE)));
				isLock = false;
			}
			if (isLock) {
				// we execute a second BAPI to link a HR user to SAP user
				logger.debug(className, sMethodName, "User locked successfully");
				logger.debug(className, sMethodName, "Running link user BAPI");
				JCoFunction linkFunction = oSAPUtil.getJCOFunction(jcoConnection,
						BAPI_EMPLCOMM_CREATE);
				JCoParameterList linkParameterList = linkFunction
						.getImportParameterList();
				Date dtCurrent = new Date();
				Date toDate = dtUtil.returnDate(hmConstants.get(LinkEndDate),
						hmConstants.get(LinkDateFormat));
				logger.debug(className, sMethodName, "Personnel Number: " + sEmplId
						+ " subtype: " + hmConstants.get(LinkSubType) + " Current date: "
						+ dtCurrent + " ToDate: " + toDate + " UserID: " + sUserId);
				linkParameterList.setValue(hmConstants.get(EMPLOYEENUMBER), sEmplId);
				linkParameterList.setValue(hmConstants.get(SUBTYPE), hmConstants
						.get(LinkSubType));
				linkParameterList.setValue(hmConstants.get(VALIDITYBEGIN), dtCurrent);
				linkParameterList.setValue(hmConstants.get(VALIDITYEND), toDate);
				linkParameterList.setValue(hmConstants.get(COMMUNICATIONID), sUserId);

				linkFunction.execute(jcoConnection);
				isLink = true;
				// if the employee is linked to a user, set the flag to true
				returnStructure2 = linkFunction.getExportParameterList().getStructure(
						hmConstants.get(RETURN));
				if (!returnStructure2.getString(hmConstants.get(NUMBER))
						.equalsIgnoreCase(hmConstants.get(No000))) {
					logger.error(className, sMethodName, returnStructure2
							.getString(hmConstants.get(MESSAGE)));
					isLink = false;
				}

			} else {
				logger.debug(className, sMethodName,
						"Lock user failed hence not linking user");
			}
			if (isLock) {
				logger.info(className, sMethodName, "User linked successfully");
				logger.debug(className, sMethodName, "Running Unlock user BAPI");

				JCoFunction closeFunction = oSAPUtil.getJCOFunction(jcoConnection,
						BAPI_EMPLOYEET_DEQUEUE);
				JCoParameterList closeParameterList = closeFunction
						.getImportParameterList();
				closeParameterList.setValue(hmConstants.get(NUMBER), sEmplId);

				closeFunction.execute(jcoConnection);
				JCoStructure returnStructure3 = findFunction.getExportParameterList()
						.getStructure(hmConstants.get(RETURN));
				if (!returnStructure3.getString(hmConstants.get(NUMBER))
						.equalsIgnoreCase(hmConstants.get(No000))) {
					logger.error(className, sMethodName, returnStructure3
							.getString(hmConstants.get(MESSAGE)));
				}
			} else {
				logger.info(className, sMethodName,
						"Not Un-Locking user due to failure in prior linking tasks");
			}
		} catch (JCoException jcoException) {
			logger.error(className, sMethodName, jcoException.getMessage());
			logger.setStackTrace(jcoException, className, sMethodName, jcoException
					.getMessage());
			throw new ConnectorException(jcoException);
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			if (exception.getMessage().startsWith("Connection")) {
				throw new ConnectorException("Connection error occured", exception);
			}
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return isLink;
	}

	/**
	 * Description:This method is used to sync password of a user
	 * 
	 * @param sUserID
	 *          User ID of the user to be modified. For example: John.Doe
	 * @param sOldPassword
	 *          Old password of the user
	 * @param sNewPassword
	 *          New password of the user
	 * 
	 * @return String
	 * 
	 * @throws ConnectorException
	 */
	private String changePassword(String sUserID, String sOldPassword,
			String sNewPassword) throws ConnectorException {
		String sResponse = null;
		String sMethodName = "changePassword()";
		logger.setMethodStartLog(className, sMethodName);
		logger.info(className, sMethodName, "Change Password Start");
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {
			//Start :: Bug 27115713 - productive password cannot be set for locked user in target(sap)
			logger.info(className, sMethodName, "Checking if user is locked due to Multiple login failed Attempts before changing productive password");
			String response = checkUserLocked(sUserID);
			logger.info(className, sMethodName, response);
			//End :: Bug 27115713 - productive password cannot be set for locked user in target(sap)
			
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					hmConstants.get(SUSR_USER_CHANGE_PASSWORD_RFC));

			JCoParameterList jcoParameterList = jcoFunction.getImportParameterList();
			jcoParameterList.setValue(hmConstants.get(BNAME), sUserID);
			jcoParameterList.setValue(hmConstants.get(PASSWORD), sOldPassword);
			jcoParameterList.setValue(hmConstants.get(NEW_PASSWORD), sNewPassword);

			jcoFunction.execute(jcoConnection);
			sResponse = PASSWORD_CHANGE_SUCCESSFUL;
		} catch (JCoException e) {
			sResponse = PASSWORD_CHANGE_FAILED;
			logger.error(className, sMethodName, "Password Change Exception "
					+ e.getMessage());
			if (e.getMessageNumber().equalsIgnoreCase(No139)) {
				sResponse =  PASSWORD_DISABLED;
			} else if (e.getMessageNumber().equalsIgnoreCase(No148)) {
				sResponse =  USER_DISABLED;
			}else if (e.getMessageNumber().equalsIgnoreCase(No152)) {
				sResponse =  NAME_OR_PASSWORD_INCORRECT;
			}else if (e.getMessageNumber().equalsIgnoreCase(No156)) {
				sResponse =  LOG_ON_WITH_DIALOG_USER;
			}else if (e.getMessageNumber().equalsIgnoreCase(No158)) {
				sResponse =  USER_IS_LOCKED;
			}else if (e.getMessageNumber().equalsIgnoreCase(No180)) {
				sResponse =  CHANGE_PASSWORD_ONCE_A_DAY;
			}else if (e.getMessageNumber().equalsIgnoreCase(No182)) {
				sResponse =  PASSWORD_EXPIRED;
			}else if (e.getMessageNumber().equalsIgnoreCase(No190)) {
				sResponse =  PASSWORD_CHANGE_DISABLED;
			}else if (e.getMessageNumber().equalsIgnoreCase(No191)) {
				sResponse =  PROGRAM_ERROR_OCCURED;
			//	Bug 27115713 - productive password cannot be set for locked user in target(sap)
			}else if (e.getMessageNumber().equalsIgnoreCase(No193)) {
				sResponse =  PROVIDE_PASSWORD_DIFFERENT_FROM_PREVIOUS;
			}
			else if (e.getMessageNumber().equalsIgnoreCase(No197)) {
				sResponse =  PASSWORD_DOES_NOT_EXIST;
			}else if (e.getMessageNumber().equalsIgnoreCase(No198)) {
				sResponse =  USER_PASSWORD_DOES_NOT_EXIST;
			}else if (e.getMessageNumber().equalsIgnoreCase(No199)) {
				sResponse =  NO_REMOTE_PASSWORD_CHANGE;
			}else if (e.getMessageNumber().equalsIgnoreCase(No200)) {
				sResponse =  TOO_MANY_FAILED_ATTEMPTS;
			}else if (e.getMessageNumber().equalsIgnoreCase(No292)) {
				sResponse =  PASSWORD_RULES_TIGHTENED;
			}else if (e.getMessageNumber().equalsIgnoreCase(No790)) {
				sResponse = PASSWORD_DEACTIVATED;
			}
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
		} catch (ConnectorException ConnectorException) {
			logger.error(className, sMethodName, "Password Change Exception "
					+ ConnectorException.getMessage());
			logger.setStackTrace(ConnectorException, className, sMethodName,
					ConnectorException.getMessage());
			sResponse = PASSWORD_CHANGE_FAILED;
		} catch (Exception e) {
			logger.error(className, sMethodName, "Password Change Exception "
					+ e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			sResponse = PASSWORD_CHANGE_FAILED;
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sResponse;
	}

	private String checkUserLocked(String sUserID) {
		

		String sResponse = null;
		SAPUtil oSAPUtil = new SAPUtil(logger);
		String sMethodName = "checkUserLocked()";
	    boolean lockStatus;
		try {
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection, hmConstants.get(BAPI_USER_GET_DETAIL));

			JCoParameterList jcoParameterList = jcoFunction.getImportParameterList();
			logger.debug(className, sMethodName, "Setting " + hmConstants.get(USERNAME) + " - " + sUserID);
			jcoParameterList.setValue(hmConstants.get(USERNAME), sUserID);

			logger.info(className, sMethodName, "Execute Get User Detail BAPI");

			jcoFunction.execute(jcoConnection);
			JCoStructure lockingData = jcoFunction.getExportParameterList().getStructure("ISLOCKED");
			logger.info(className, sMethodName, lockingData.toString());
			String noUserPwdLock = lockingData.getString("NO_USER_PW");
			String wrongLogonLock = lockingData.getString("WRNG_LOGON");

			lockStatus = ("L".equals(wrongLogonLock) | ("L".equals(noUserPwdLock)));
			if (lockStatus) {
				logger.info(className, sMethodName, "User locked due to wrong logon or incurrect/No passord, so unlocking the user");
				sResponse = modifyLockUnlockUser(sUserID, "0");
			}else{
			sResponse = "User is not locked";
			}
			logger.info(className, sMethodName, sResponse);
		} catch (JCoException e) {
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException(e.getMessage());
		}
		return sResponse;
	}

	/**
	 * Description: Used to delete the user ID by running BAPI_USER_DELETE'.
	 * 
	 * @param sUserID
	 *          User ID of the user to be deleted. For example: John.Doe
	 * 
	 * @return String Returns the response code that is mapped in the adapter
	 * @throws ConnectorException
	 */
	public String deleteUser(String sUserID) throws ConnectorException {
		HashMap<String, String> hashStatusMessage = new HashMap<String, String>();
		String sStatus = null;
		String sMethodName = "deleteUser()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					hmConstants.get(BAPI_USER_DELETE));
			JCoParameterList jcoParameterList = jcoFunction.getImportParameterList();
			jcoParameterList.setValue(hmConstants.get(USERNAME), sUserID);
			jcoFunction.execute(jcoConnection);
			hashStatusMessage = oUMUtil.getBAPIStatus(jcoFunction, "");

			if (hashStatusMessage.get(hmConstants.get(TYPE)).equals(
					hmConstants.get(S))) {
				logger.info(className, sMethodName, sUserID + " deleted successfully");
				sStatus = USER_DELETION_SUCCESSFUL;
			} else {
				logger.error(className, sMethodName, hashStatusMessage.get(hmConstants
						.get(MESSAGE)));
				logger.error(className, sMethodName, "User deletion failed");
				sStatus = USER_DELETION_FAILED;
			}
		} catch (JCoException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.error(className, sMethodName, exception.getMessageText());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sStatus = USER_DELETION_FAILED;
			throw new ConnectorException(exception);
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sStatus = USER_DELETION_FAILED;
			throw new ConnectorException(exception);
		} catch (Exception e) {
			logger.error(className, sMethodName, "Password Change Exception "
					+ e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			sStatus = USER_DELETION_FAILED;
			throw new ConnectorException(e);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sStatus;
	}

	/**
	 * Description : Locks or unlocks the user by running either BAPI_USER_LOCK or
	 * BAPI_USER_UNLOCK
	 * 
	 * @param sUserId
	 *          SAP User ID to be locked. For example: John.Doe
	 * @param sAttributeValue
	 *          Attribute value specifying whether the user is to be locked or
	 *          unlocked For example: 0
	 * @return String Returns the response which will be from the adapter
	 * @throws ConnectorException
	 * 
	 */
	public String modifyLockUnlockUser(String sUserId, String sAttributeValue)
			throws ConnectorException {
		HashMap<String, String> hmHashStatusMessage = new HashMap<String, String>();
		String sReturn = null;
		String sMethodName = "modifyLockUnlockUser()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {
			JCoFunction jcoFunction = null;
			if (sAttributeValue.equalsIgnoreCase(hmConstants.get(ONE))) {
				logger.info(className, sMethodName, "Start Lock on " + sUserId);
				jcoFunction = oSAPUtil.getJCOFunction(jcoConnection, hmConstants
						.get(BAPI_USER_LOCK));
			} else if (sAttributeValue.equalsIgnoreCase(hmConstants.get(ZERO))) {
				logger.info(className, sMethodName, "Start UnLock on " + sUserId);
				jcoFunction = oSAPUtil.getJCOFunction(jcoConnection, hmConstants
						.get(BAPI_USER_UNLOCK));
			} else {
				throw new ConnectorException(INVALID_LOCK_STATE);
			}
			JCoParameterList lockParameterList = jcoFunction.getImportParameterList();
			lockParameterList.setValue(hmConstants.get(USERNAME), sUserId);
			jcoFunction.execute(jcoConnection);

			hmHashStatusMessage = oUMUtil.getBAPIStatus(jcoFunction, "");

			if (hmHashStatusMessage != null
					&& hmHashStatusMessage.get(hmConstants.get(TYPE)) != null
					&& hmHashStatusMessage.get(hmConstants.get(TYPE)).equalsIgnoreCase(
							hmConstants.get(S))) {
				if (!StrUtil.isEmpty(sAttributeValue)
						&& sAttributeValue.equalsIgnoreCase(hmConstants.get(ONE))) {
					logger.info(className, sMethodName, "Successful Lock on " + sUserId);
					sReturn = USER_LOCKED_SUCCESSFUL;
				} else {
					logger
							.info(className, sMethodName, "Successful UnLock on " + sUserId);
					sReturn = USER_UNLOCKED_SUCCESSFUL;
				}
			} else {
				throw new ConnectorException("LOCK/UNLOCK Error:"
						+ (String) hmHashStatusMessage.get(hmConstants.get(MESSAGE)));
			}
		} catch (JCoException jcoException) {
			if (sAttributeValue.equals(hmConstants.get(ONE))) {
				logger.error(className, sMethodName, jcoException.getMessage());
				sReturn = USER_LOCK_FAILED;
			} else {
				logger.error(className, sMethodName, jcoException.getMessage());;
				sReturn = USER_UNLOCK_FAILED;
			}
			logger.error(className, sMethodName, jcoException.getMessageText());
			logger.setStackTrace(jcoException, className, sMethodName, jcoException
					.getMessage());
			throw new ConnectorException(jcoException);
		} catch (ConnectorException bException) {
			if (sAttributeValue.equals(hmConstants.get(ONE))) {
				logger.error(className, sMethodName,
						"Lock request Base Exception for user id :" + sUserId);
				sReturn = USER_LOCK_FAILED;
			} else {
				logger.error(className, sMethodName,
						"UnLock request Base Exception for user id :" + sUserId);
				sReturn = USER_UNLOCK_FAILED;
			}

			logger.setStackTrace(bException, className, sMethodName, bException
					.getMessage());
			throw new ConnectorException(bException);
		} catch (Exception exception) {
			if (sAttributeValue.equals(hmConstants.get(ONE))) {
				logger.error(className, sMethodName,
						"Lock request Exception for user id :" + sUserId);
				sReturn = USER_LOCK_FAILED;
			} else {
				logger.error(className, sMethodName,
						"UnLock request Exception for user id :" + sUserId);
				sReturn = USER_UNLOCK_FAILED;
			}
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sReturn;
	}

	/**
	 * Description:Used to modify the password of a user
	 * 
	 * @param userID
	 *          User ID of the user to be modified. For example: John.Doe
	 * @param fieldName
	 *          Field name of the value to be modified in the BAPI. For example:
	 *          BAPIPWD
	 * @param structure
	 *          Structure name in the BAPI. Within this the BAPI field name
	 *          exists. For example: PASSWORD
	 * @param fieldNameX
	 *          Name of the field that is used to indicate whether or not the
	 *          value in fieldName must be applied. The value 'X' is placed in
	 *          this field to indicate that the value in fieldname must be
	 *          changed. For example: BAPIPWD
	 * @param structureX
	 *          Name of the structure that holds fieldNameX. For example: PASSWORD
	 * @param dummyPassword
	 *          Dummy password required to reset the password.
	 * @param sNewPassword
	 *          Password entered by the user on the process form
	 * @param sSyncPswrd ?
	 *          Specifies whether or not the password must be synchronized with
	 *          Oracle Identity Manager. For example: 'NO'
	 * @param sPasswordPropagateDelay
	 *          Specifies the password propagation delay time in milliseconds. 
	 * @return String Returns the response code that is mapped in the adapter
	 * 
	 * @throws ConnectorException
	 */
	public String modifyPassword(String userID, String fieldName,
			String structure, String fieldNameX, String structureX,
			String dummyPassword, String sNewPassword, String sSyncPswrd, String isPasswordPropagate, String sPasswordPropagateDelay)
			throws ConnectorException {
		String sResult = null;
		String sMethodName = "modifyPassword()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			if (sSyncPswrd.equalsIgnoreCase(hmConstants.get(NO))) {
				dummyPassword = sNewPassword;
			}
			// Start Bug: BUG 12334992 - SAP UM CONNECTOR TO PROPAGATE PASSWORD CHANGES TO CUA CHILD SYSTEMS 
			if(isPasswordPropagate.equalsIgnoreCase(hmConstants.get(NO))) {
				sResult = modifyUser(userID, fieldName, structure, fieldNameX,
					structureX, dummyPassword);
			} else {
				sResult = customModifyUser(userID, fieldName, structure, fieldNameX,
						structureX, dummyPassword);
			}
			if (!sResult.equalsIgnoreCase(USER_MODIFIED_SUCCESSFUL)) {
				logger.error(className, sMethodName, "Password reset failed");
				throw new ConnectorException(PASSWORD_CHANGE_FAILED);
			} else if (sResult.equalsIgnoreCase(USER_MODIFIED_SUCCESSFUL)) {
				logger.info(className, sMethodName, "Password reset successfully");
				sResult = PASSWORD_CHANGE_SUCCESSFUL;
			}
			if (sSyncPswrd.equalsIgnoreCase(hmConstants.get(YES)))
				if(isPasswordPropagate.equalsIgnoreCase(hmConstants.get(NO))) {
					sResult = changePassword(userID, dummyPassword, sNewPassword);
				} else {//Start:: Added for Password propagate Delay - BUG 17159673
					long delay = 0;
					if(!sPasswordPropagateDelay.equals("0")) {
						try {
							delay = Long.valueOf(sPasswordPropagateDelay);
							Thread.sleep(delay);
  					        } catch (NumberFormatException e) {
							logger.error(className, sMethodName, "In the SAP UM Main Configuration Lookup, the password propagation delay time does not contain numeric values: "+ e);
						} catch (InterruptedException ie) {
							logger.error(className, sMethodName, "Thread Interrupted Exception :" + ie);
						}
					}// End :: Added for Password propagate Delay - BUG 17159673
					sResult = customChangePassword(userID, dummyPassword, sNewPassword);
				}
			// End Bug: BUG 12334992 - SAP UM CONNECTOR TO PROPAGATE PASSWORD CHANGES TO CUA CHILD SYSTEMS 
		} catch (ConnectorException e) {
			throw new ConnectorException(e);
		} catch (Exception e) {
			throw new ConnectorException(e);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sResult;
	}

	/**
	 * Description: Used to add a multivalued attribute to a user, such as a role,
	 * profile, parameter or User Groups.
	 * 
	 * @param dataList
	 *          Contains attribute-mapped fields and values in the format
	 *          ArrayList which holds SAPUMAttributeMapBean. For example:
	 *          [BEAN1,BEAN2]
	 * @see oracle.iam.connectors.sap.usermgmt.util.SAPUMAttributeMapBean
	 * @param sUserID
	 *          User ID of the user to be modified. For example: John.Doe
	 * @param sPrimaryFieldName
	 *          Field name of the value to be modified in the BAPI. For example:
	 *          BAPIPROF
	 * @param sStructureName
	 *          Structure name in the BAPI. Within this the BAPI field name
	 *          exists. For example: PROFILE
	 * @param isCUA
	 *          Is CUA enabled or not. For example: NO
	 * @return String Returns the response code which is mapped in the Adapter.
	 * @throws ConnectorException
	 */
	public String addMultiValueData(ArrayList<SAPUMAttributeMapBean> dataList,
			String sUserID, String sStructureName, String sPrimaryFieldName,
			String isCUA) throws ConnectorException {
		String sPrimaryFieldValue = null;
		String sSubsystem = null;
		String sStatus = null;
		String sTableFieldValue = null;
		String sTableSubsystem = null;
		String sBAPIName = null;
		String sUserName = null;
		HashMap<String, String> hashStatusMessage = new HashMap<String, String>();
		String sMethodName = "addMultiValueData()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUtil = new UMUtility(logger, hmConstants);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		StringUtil StrUtil = new StringUtil();
		try {
			if ((dataList != null) && !dataList.isEmpty()) {
				int iDataListSize = dataList.size();

				for (int iIndex = 0; iIndex < iDataListSize; iIndex++) {
					SAPUMAttributeMapBean sAPAttributeMapBean = (SAPUMAttributeMapBean) dataList
							.get(iIndex);
					if (isCUA.equalsIgnoreCase(hmConstants.get(YES))){
						if ((sAPAttributeMapBean != null)
								&& sAPAttributeMapBean.getBapiFieldName().equalsIgnoreCase(
										sPrimaryFieldName)) {
							sPrimaryFieldValue = sAPAttributeMapBean.getFieldValue();
							//break;
						}
						if ((sAPAttributeMapBean != null)
									&& sAPAttributeMapBean.getBapiFieldName().equalsIgnoreCase(
											SUBSYSTEM)) {
								sSubsystem = sAPAttributeMapBean.getFieldValue();
								break;
						}
					} else {
						if ((sAPAttributeMapBean != null)
								&& sAPAttributeMapBean.getBapiFieldName().equalsIgnoreCase(
										sPrimaryFieldName)) {
							sPrimaryFieldValue = sAPAttributeMapBean.getFieldValue();
							break;
						}
					}
				}
			}
			JCoTable jcoTable = null;
			// Get the list of existing Roles/Profiles Assigned to that user.
			if (isCUA.equalsIgnoreCase(hmConstants.get(YES))
					&& (sStructureName.equalsIgnoreCase(hmConstants.get(ACTIVITYGROUPS)) || sStructureName
							.equalsIgnoreCase(hmConstants.get(PROFILES)))) {
				jcoTable = oUtil.getRoleorProfile(sUserID, sStructureName,
						jcoConnection, hmConstants);
			} else {
				jcoTable = getChildData(sUserID, sStructureName,hmConstants.get(BAPI_USER_GET_DETAIL),hmConstants.get(USERNAME));
			}
			// Check whether Role/Profile entered in process form is already assigned
			int iTableFieldRowCount = jcoTable.getNumRows();
			logger.debug(className, sMethodName, "# of existing role / profile " + iTableFieldRowCount);
			
			if (isCUA.equalsIgnoreCase(hmConstants.get(YES))){
				for (int iCount = 0; iCount < iTableFieldRowCount; iCount++) {
					jcoTable.setRow(iCount);
					sTableFieldValue = (String) jcoTable.getValue(sPrimaryFieldName);
					sTableSubsystem  = (String) jcoTable.getValue(SUBSYSTEM);
					if (sTableFieldValue.equalsIgnoreCase(sPrimaryFieldValue) && 
							sTableSubsystem.equalsIgnoreCase(sSubsystem)) {
						logger.info("Multivalue Data Already assigned to the User");
						sStatus = MULTIVALUE_DATA_ALREADY_EXISTS;
					}
				}
			} else {
				for (int iCount = 0; iCount < iTableFieldRowCount; iCount++) {
					jcoTable.setRow(iCount);
					sTableFieldValue = (String) jcoTable.getValue(sPrimaryFieldName);
					if (sTableFieldValue.equalsIgnoreCase(sPrimaryFieldValue)) {
						logger.info("Multivalue Data Already assigned to the User");
						sStatus = MULTIVALUE_DATA_ALREADY_EXISTS;
					}
				}
			}
			/**
			 * if multivalued Attr already exists then go to end of method
			 */
			if (StrUtil.isEmpty(sStatus)) {
				sBAPIName = oUtil.getAddMultiValueDataBAPIName(sStructureName, isCUA);

				JCoFunction jcoFunction;

				jcoFunction = oSAPUtil.getJCOFunction(jcoConnection, sBAPIName);
				JCoParameterList jcoParameterList = jcoFunction
						.getImportParameterList();

				sUserName = sUserID.toUpperCase();

				jcoParameterList.setValue(hmConstants.get(USERNAME), sUserName);

				JCoTable jcoTable1 = jcoFunction.getTableParameterList().getTable(
						sStructureName);

				// Add the already assigned roles/profiles/parameters in tableActGroup
				for (int iIndex = 0; iIndex < iTableFieldRowCount; iIndex++) {
					jcoTable.setRow(iIndex);
					jcoTable1.appendRow();

					if ((dataList != null) && !dataList.isEmpty()) {
						int iDataListSize = dataList.size();
						for (int iIndex1 = 0; iIndex1 < iDataListSize; iIndex1++) {
							SAPUMAttributeMapBean sAPAttributeMapBean = (SAPUMAttributeMapBean) dataList
									.get(iIndex1);
							if (isCUA.equalsIgnoreCase(hmConstants.get(NO))) {
								// if is UM disregard system name attribute
								if (sAPAttributeMapBean.getBapiFieldName().equalsIgnoreCase(
										hmConstants.get(SUBSYSTEM)))
									continue;
							}
							if (sAPAttributeMapBean != null
									&& sAPAttributeMapBean.getFieldValue() != null) {
								logger.debug(className, sMethodName,
										"Add Already Assigned Multivalued Attributes "
												+ jcoTable.getValue(sAPAttributeMapBean.getBapiFieldName()));
								jcoTable1.setValue(sAPAttributeMapBean.getBapiFieldName(),
										jcoTable.getValue(sAPAttributeMapBean.getBapiFieldName()));
							}
						}
					}
				}

				// Add the roles/profiles/parameters entered in process form in
				// tableActGroup
				jcoTable1.appendRows(1);

				if ((dataList != null) && !dataList.isEmpty()) {
					int iDataListSize = dataList.size();

					for (int iIndex1 = 0; iIndex1 < iDataListSize; iIndex1++) {
						SAPUMAttributeMapBean sAPAttributeMapBean = (SAPUMAttributeMapBean) dataList
								.get(iIndex1);
						if (isCUA.equalsIgnoreCase(hmConstants.get(NO))) {
							// if is UM disregard system name attribute
							if (sAPAttributeMapBean.getBapiFieldName().equalsIgnoreCase(
									hmConstants.get(SUBSYSTEM)))
								continue;
						}
						if ((sAPAttributeMapBean != null)
								&& (sAPAttributeMapBean.getBapiFieldName() != null)) {
							logger.debug(className, sMethodName,
									"Add new Multivalued Attribute "
											+ sAPAttributeMapBean.getFieldValue());
							jcoTable1.setValue(sAPAttributeMapBean.getBapiFieldName(),
									sAPAttributeMapBean.getFieldValue());
						}
					}
				}
				if ((dataList != null) && !dataList.isEmpty()) {
					int iDataListSize = dataList.size();
					JCoStructure jcoStructure = null;
					for (int iIndex1 = 0; iIndex1 < iDataListSize; iIndex1++) {
						SAPUMAttributeMapBean sAPAttributeMapBean = (SAPUMAttributeMapBean) dataList
								.get(iIndex1);
						String sStructurnameX = sAPAttributeMapBean.getBapiStructureX();
						String sFieldNameX = sAPAttributeMapBean.getBapiFieldNameX();
						if (!StrUtil.isEmpty(sStructurnameX)
								&& !StrUtil.isEmpty(sFieldNameX)) {
							if (iIndex1 == 0){
								jcoStructure = jcoFunction.getImportParameterList()
								.getStructure(sStructurnameX);
								logger.debug(className, sMethodName,
										"Setting " + sStructurnameX + "_" + sFieldNameX + " to " + hmConstants.get(X));
								jcoStructure.setValue(sFieldNameX, hmConstants.get(X));
							}
						}
					}
				}

				logger.info(className, sMethodName,
						"Executing BAPI for MultiValue Attributes");
				jcoFunction.execute(jcoConnection);
				hashStatusMessage = oUtil.getBAPIStatus(jcoFunction, "");

				if (hashStatusMessage.get(hmConstants.get(TYPE)).equals(
						hmConstants.get(S))) {
					logger.info(className, sMethodName,
							"MultiValued Attribute added successfully");
					sStatus = MULTI_DATA_ADDED_SUCCESSFUL;
				} else if (hashStatusMessage.get(hmConstants.get(TYPE)).equals(
						hmConstants.get(W))) {
					logger.error(className, sMethodName, hashStatusMessage
							.get(hmConstants.get(MESSAGE)));
					if (hashStatusMessage.get(hmConstants.get(NUMBER)).equals(
							hmConstants.get(No591))) {
						logger.error(className, sMethodName,
								"TimeOut while adding MultiValueAttribute");
						return RETRY;
					} else if (StrUtil.isEmpty(sStatus)
							&& hashStatusMessage.get(hmConstants.get(MESSAGE)).indexOf(
									hmConstants.get(DOES_NOT_EXIST)) != -1) {
						logger.error(className, sMethodName,
								"MultiValueAttribute does not exist in target");
						sStatus = MULTIVALUE_DATA_DOES_NOT_EXIST;
					}
				} else {
					logger.error(className, sMethodName,
							"Error while adding MultiValueAttribute");
					logger.error(className, sMethodName, hashStatusMessage
							.get(hmConstants.get(MESSAGE)));
					sStatus = ADD_MULTIVALUE_DATA_FAILED;
				}
			}// end : if multivalued Attr already exists
		} catch (JCoException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.error(className, sMethodName, e.getMessageText());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			sStatus = ADD_MULTIVALUE_DATA_FAILED;
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			if (StrUtil.isEmpty(sStatus))
				sStatus = ADD_MULTIVALUE_DATA_FAILED;
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sStatus = ADD_MULTIVALUE_DATA_FAILED;
		}
		return sStatus;
	}

	/**
	 * Description:This method is used to get child table data entered
	 * 
	 * @param sUserID
	 *          User ID of the user
	 * @param sStructureName
	 *          SAP Structure name
	 * 
	 * @return String
	 * @throws ConnectorException
	 */
	private JCoTable getChildData(String sUserID, String sStructureName, String sBAPIName, String sBAPIUserName)
			throws ConnectorException {
		JCoTable jcoTable = null;
		String sMethodName = "getChildData()";
		logger.setMethodStartLog(className, sMethodName);
		logger.info(className, sMethodName, "Get Child Data Start");
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,sBAPIName);
			JCoParameterList jcoParameterList = jcoFunction.getImportParameterList();
			
			logger.debug(className, sMethodName, "Setting " +sBAPIUserName + " to " + sUserID);
			jcoParameterList.setValue(sBAPIUserName, sUserID);
			jcoFunction.execute(jcoConnection);
			jcoTable = jcoFunction.getTableParameterList().getTable(sStructureName);
		} catch (JCoException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.error(className, sMethodName, exception.getMessageText());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return jcoTable;
	}

	/**
	 * Description:to remove a multivalued attribute from a user, such as a role,
	 * profile, parameter or User Groups.
	 * 
	 * @param sUserID
	 *          User ID of the user to be modified. For example: John.Doe
	 * @param sMultiValuedAttribute
	 *          MultiValued Attribute value in the child form that is to be
	 *          deleted. For example: 1~E60~V_SYS_ADMIN
	 * @param sChildPrimaryKey
	 *          SAP field name of the Key multivalued attribute on the child form.
	 *          For example: BAPIPROF
	 * @param sStructureName
	 *          Structure name in the BAPI. The BAPI field is part of this
	 *          structure. For example: PROFILE
	 * @param isCUA
	 *          Specifies whether or not CUA is enabled. For example: YES
	 * @return String Returns the response code which is mapped in the Adapter.
	 * @throws ConnectorException
	 */
	public String removeMultiValueData(String sUserID, String sStructureName,
			String sMultiValuedAttribute, String sChildPrimaryKey, String isCUA)
			throws ConnectorException {
		HashMap<String, String> hashStatusMessage = new HashMap<String, String>();
		String sResponse = null;
		String sBAPIName = null;
		String sUserName = null;
		String sMethodName = "removeMultiValueData()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {

			/*
			 * Call getChildData() method to get the list of existing Attributes
			 * Assigned to that user. And then remove the deleted Attributes from that
			 * list
			 */
			JCoTable jcoTable;
			// Get the list of existing Roles/Profiles Assigned to that user.
			if (isCUA.equalsIgnoreCase(hmConstants.get(YES))
					&& (sStructureName.equalsIgnoreCase(hmConstants.get(ACTIVITYGROUPS)) || sStructureName
							.equalsIgnoreCase(hmConstants.get(PROFILES)))) {
				jcoTable = oUMUtil.getRoleorProfile(sUserID, sStructureName,
						jcoConnection, hmConstants);
			} else {
				jcoTable = getChildData(sUserID, sStructureName,hmConstants.get(BAPI_USER_GET_DETAIL),hmConstants.get(USERNAME));
			}
			sBAPIName = oUMUtil.getAddMultiValueDataBAPIName(sStructureName, isCUA);

			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					sBAPIName);
			JCoParameterList jcoParameterList = jcoFunction.getImportParameterList();

			sUserName = sUserID.toUpperCase();

			jcoParameterList.setValue(hmConstants.get(USERNAME), sUserName);

			JCoTable tableActGroup = jcoFunction.getTableParameterList().getTable(
					sStructureName);
			int iTableFieldRowCount = jcoTable.getNumRows();
			logger.debug(className, sMethodName, "# of existing role or profile " + iTableFieldRowCount);
			StringTokenizer st = new StringTokenizer(sMultiValuedAttribute, "~");
			int iTokenCount = st.countTokens();
			int i = 0;
			String[] formattedAttr = new String[iTokenCount];
			while (st.hasMoreTokens()) {
				formattedAttr[i++] = st.nextToken();
			}
			int length = formattedAttr.length;
			for (int iCount = 0; iCount < iTableFieldRowCount; iCount++) {
				jcoTable.setRow(iCount);
				if (jcoTable.getString(sChildPrimaryKey).equalsIgnoreCase(
						formattedAttr[length - 1])) {
					if (isCUA.equalsIgnoreCase(hmConstants.get(NO)) || iTokenCount > 2) {
						if (isCUA.equalsIgnoreCase(hmConstants.get(NO))
								|| jcoTable.getString(hmConstants.get(SUBSYSTEM))
										.equalsIgnoreCase(formattedAttr[1])) {
							jcoTable.deleteRow();
							logger.info(className, sMethodName, formattedAttr[length - 1]
									+ " Deleted Successfully");
						}
					}else if (isCUA.equalsIgnoreCase(hmConstants.get(YES)) && iTokenCount == 2) {
						if (jcoTable.getString(sChildPrimaryKey)
										.equalsIgnoreCase(formattedAttr[1])) {
							jcoTable.deleteRow();
							logger.info(className, sMethodName, formattedAttr[length - 1]
									+ " Deleted Successfully");
						}
					}
				}
			}
			iTableFieldRowCount = jcoTable.getNumRows();
			int iTableColumnCount = jcoTable.getNumColumns();
			for (int iCount = 0; iCount < iTableFieldRowCount; iCount++) {
				tableActGroup.appendRow();
				jcoTable.setRow(iCount);
				for (int iCount2 = 0; iCount2 < iTableColumnCount; iCount2++){
					logger.info(className, sMethodName, "Adding already assigned value " + jcoTable.getString(iCount2));
					tableActGroup.setValue(iCount2, jcoTable.getString(iCount2));
				}
			}
			if (sBAPIName.equalsIgnoreCase(hmConstants.get(BAPI_USER_CHANGE))) {
				String sStructurnameX = sStructureName + hmConstants.get(X);
				JCoStructure jcoStructure = jcoFunction.getImportParameterList()
						.getStructure(sStructurnameX);
				jcoStructure.setValue(sChildPrimaryKey, hmConstants.get(X));
			}

			logger.info(className, sMethodName,
					"Executing BAPI for MultiValue Attributes");
			jcoFunction.execute(jcoConnection);

			hashStatusMessage = oUMUtil.getBAPIStatus(jcoFunction, "");

			if (hashStatusMessage.get(hmConstants.get(TYPE)).equals(
					hmConstants.get(S))) {
				logger.info(className, sMethodName,
						"MultiValued Attribute removed successfully");
				sResponse = MULTI_DATA_REMOVE_SUCCESSFUL;
			} else if (hashStatusMessage.get(hmConstants.get(TYPE)).equals(
					hmConstants.get(W))
					&& hashStatusMessage.get(hmConstants.get(NUMBER)).equals(
							hmConstants.get(No591))) {
				logger.error(className, sMethodName,
						"Error while removing MultiValueAttribute");
				return RETRY;
			} else {
				logger.error(className, sMethodName, hashStatusMessage.get(hmConstants
						.get(MESSAGE)));
				sResponse = MULTI_DATA_REMOVE_FAILED;
			}

		} catch (JCoException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.error(className, sMethodName, exception.getMessageText());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sResponse = MULTI_DATA_REMOVE_FAILED;
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sResponse = MULTI_DATA_REMOVE_FAILED;
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sResponse = MULTI_DATA_REMOVE_FAILED;
		}

		return sResponse;
	}

	/**
	 * Description:This method is used to assign system to a user
	 * 
	 * @param sUserID
	 *          User ID of the user to be modified
	 * @param sSystemName
	 *          System name of target
	 * @return String
	 * @throws ConnectorException
	 */
	private String addMasterSystemToUser(String sUserId, String sSystemName)
			throws ConnectorException {
		String sMethodName = "addMasterSystemToUser()";
		String sResponse = null;
		logger.setMethodStartLog(className, sMethodName);
		HashMap<String, String> hmTempMap = new HashMap<String, String>();
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {
			logger.debug(className, sMethodName,
					"Running BAPI to add system for user");
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					hmConstants.get(BAPI_USER_LOCPROFILES_ASSIGN));
			JCoParameterList userParameterList = jcoFunction.getImportParameterList();
			userParameterList.setValue(hmConstants.get(USERNAME), sUserId);

			JCoTable tableActGroup = jcoFunction.getTableParameterList().getTable(
					hmConstants.get(PROFILES));
			tableActGroup.appendRows(1);
			tableActGroup.setValue(hmConstants.get(SUBSYSTEM), sSystemName);
			jcoFunction.execute(jcoConnection);
			hmTempMap = oUMUtil.getBAPIStatus(jcoFunction, "");

			if (hmTempMap.get(hmConstants.get(TYPE)).equals(hmConstants.get(S))) {
				sResponse = ADD_SYSTEM_SUCCESSFUL;
			} else {
				logger.error(className, sMethodName, "Error while adding system :"
						+ sSystemName);
				logger.error(className, sMethodName, (String) hmTempMap.get(hmConstants
						.get(MESSAGE)));
			}
		} catch (JCoException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.error(className, sMethodName, exception.getMessageText());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sResponse = ADD_SYSTEM_FAILED;
			throw new ConnectorException(exception);
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sResponse = ADD_SYSTEM_FAILED;
			throw new ConnectorException(exception);
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sResponse = ADD_SYSTEM_FAILED;
			throw new ConnectorException(exception);
		}
		return sResponse;
	}

	/**
	 * Description:Used to check if the personnel number exists on the target
	 * system by running BAPI_EMPLOYEE_CHECKEXISTENCE
	 * 
	 * @param empID
	 *          The Employee Number that links with the R/3 User. For example:
	 *          1000
	 * 
	 * @return boolean
	 * @throws ConnectorException
	 */
	public boolean checkExistance(String empID) throws ConnectorException {
		String sMethodName = "checkExistance()";
		logger.setMethodStartLog(className, sMethodName);
		boolean isCheck = false;
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					BAPI_EMPLOYEE_CHECKEXISTENCE);
			JCoParameterList findParameterList = jcoFunction.getImportParameterList();
			logger.debug(className, sMethodName, "Setting " + hmConstants.get(NUMBER) +" to "+empID);
			findParameterList.setValue(hmConstants.get(NUMBER), empID);
			jcoFunction.execute(jcoConnection);
			// get the output from the SAP
			JCoStructure str = jcoFunction.getExportParameterList().getStructure(
					hmConstants.get(RETURN));
			String sTemp = str.getString(hmConstants.get(CODE));
			if (StrUtil.isEmpty(sTemp)) {
				logger.info(className, sMethodName, "Personnel number exists");
				isCheck = true;
			} else if (sTemp.equalsIgnoreCase(hmConstants.get(PG001))) {
				logger.error(className, sMethodName,
						"Personnel number not yet assigned");
				isCheck = false;
			}
		} catch (JCoException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			if (exception.getKey().equalsIgnoreCase(RFC_ERROR_LOGON_FAILURE)
					|| exception.getKey().equalsIgnoreCase(RFC_ERROR_COMMUNICATION)) {
				throw new ConnectorException("Connection error occured", exception);
			}
			logger.error(className, sMethodName, exception.getMessageText());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			isCheck = false;
			throw new ConnectorException(exception);
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			if (exception.getMessage().startsWith("Connection")) {
				throw new ConnectorException("Connection error occured", exception);
			}
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			isCheck = false;
			throw new ConnectorException(exception);
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			isCheck = false;
			throw new ConnectorException(exception);
		}
		return isCheck;
	}

	/**
	 * Description:Used to validate if the personnel number is already linked
	 * before on the target system by running BAPI_EMPLCOMM_GETDETAILEDLIST
	 * 
	 * @param sEmpID
	 *          The Employee Number that links with the R/3 User. For example:
	 *          1000
	 * 
	 * @return boolean
	 * @throws ConnectorException
	 */
	public boolean linkedBefore(String sEmpID) throws ConnectorException {
		int iTemp;
		// isCheck will return false if linked as linking should not happen
		boolean isCheck = true;
		String sMethodName = "linkedBefore()";
		logger.setMethodStartLog(className, sMethodName);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {
			logger.debug(className, sMethodName,
					"Running BAPI to check if user is Linked");
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					hmConstants.get(BAPI_EMPLCOMM_GETDETAILEDLIST));
			JCoParameterList findParameterList = jcoFunction.getImportParameterList();
			logger.debug(className, sMethodName,"Setting " +  hmConstants.get(EMPLOYEENUMBER) +" to "+sEmpID);
			findParameterList.setValue(hmConstants.get(EMPLOYEENUMBER), sEmpID);
			logger.debug(className, sMethodName, "Setting " + hmConstants.get(SUBTYPE) +" - "+hmConstants
					.get(LinkSubType));
			findParameterList.setValue(hmConstants.get(SUBTYPE), hmConstants
					.get(LinkSubType));
			jcoFunction.execute(jcoConnection);
			// get the output from the SAP
			JCoTable outTable = jcoFunction.getTableParameterList().getTable(
					hmConstants.get(COMMUNICATION));
			iTemp = outTable.getNumRows();
			Date currDate = new Date();
			/*
			 * we check, incase the personnel number is already linked, the period in
			 * which the user is linked.
			 */
			if (iTemp > 0) {
				for (int i = 0; i < iTemp; i++) {
					outTable.setRow(i);
					Date validTo = outTable.getDate(VALIDEND);
					if (validTo.after(currDate)) {
						logger.info(className, sMethodName, sEmpID + " already linked to "
								+ outTable.getString(ID) + " valid till " + validTo);
						isCheck = false;
						break;
					}
				}
			}
			if (isCheck) {
				logger.info(className, sMethodName, "User not linked");
			}
		} catch (JCoException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.error(className, sMethodName, exception.getMessageText());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			isCheck = false;
			throw new ConnectorException(exception);
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			if (exception.getMessage().startsWith("Connection")) {
				throw new ConnectorException("Connection error occured", exception);
			}
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			isCheck = false;
			throw new ConnectorException(exception);
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			isCheck = false;
			throw new ConnectorException(exception);
		}
		return isCheck;
	}

	/**
	 * Description:Used to update a multivalued attribute of a user, such as a
	 * role or parameter.
	 * 
	 * @param dataList
	 *          Contains attribute-mapped fields and values in the format
	 *          ArrayList which holds SAPUMAttributeMapBean. For example:
	 *          [BEAN1,BEAN2]
	 * @see oracle.iam.connectors.sap.usermgmt.util.SAPUMAttributeMapBean
	 * @param sMultiValuedAttribute
	 *          MultiValued Attribute value in the child form which is to be
	 *          updated. For example: 1~E60~SAP_CO_PC_PLAN_REFERENCE_SIMUL
	 * @param sUserID
	 *          User ID of the user to be modified. For example: John.Doe
	 * @param sPrimaryFieldName
	 *          Field name of the key value of the multivalues attribute in the
	 *          BAPI. For example: PARAVAL
	 * @param bapiStructureName
	 *          Structure name in the BAPI. The BAPI field is part of this
	 *          structure. For example: PARAMETER
	 * @param isCUA
	 *          Specifies whether or not CUA is enabled. For example: NO
	 * @return String Returns the response code that is mapped in the Adapter.
	 * @throws ConnectorException
	 */
	public String updateMultiValueData(ArrayList<SAPUMAttributeMapBean> dataList,
			String sMultiValuedAttribute, String sUserID, String bapiStructureName,
			String sPrimaryFieldName, String isCUA) {
		String sPrimaryFieldValue = null;
		String sStatus = null;
		String sTableFieldValue = null;
		String sBAPIName = null;
		String sUserName = null;
		HashMap<String, String> hashStatusMessage = new HashMap<String, String>();
		String sMethodName = "updateMultiValueData()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {
			if ((dataList != null) && !dataList.isEmpty()) {
				int iDataListSize = dataList.size();

				for (int iIndex = 0; iIndex < iDataListSize; iIndex++) {
					SAPUMAttributeMapBean sAPAttributeMapBean = (SAPUMAttributeMapBean) dataList
							.get(iIndex);

					if ((sAPAttributeMapBean != null)
							&& sAPAttributeMapBean.getBapiFieldName().equalsIgnoreCase(
									sPrimaryFieldName)) {
						sPrimaryFieldValue = sAPAttributeMapBean.getFieldValue();
						break;
					}
				}
			}
			JCoTable jcoTable = null;
			// Get the list of existing Roles/Profiles Assigned to that user.
			if (isCUA.equalsIgnoreCase(hmConstants.get(YES))
					&& (bapiStructureName.equalsIgnoreCase(hmConstants
							.get(ACTIVITYGROUPS)) || bapiStructureName
							.equalsIgnoreCase(hmConstants.get(PROFILES)))) {
				jcoTable = oUMUtil.getRoleorProfile(sUserID, bapiStructureName,
						jcoConnection, hmConstants);
			} else {
				jcoTable = getChildData(sUserID, bapiStructureName,hmConstants.get(BAPI_USER_GET_DETAIL),hmConstants.get(USERNAME));
			}

			// Check whether Role/Profile entered in process form is already assigned
			int iTableFieldRowCount = jcoTable.getNumRows();
			logger.debug(className, sMethodName, "# of existing multi value data: " + iTableFieldRowCount);
			StringTokenizer st = new StringTokenizer(sMultiValuedAttribute, "~");
			int iTokenCount = st.countTokens();
			int i = 0;
			String[] formattedAttr = new String[iTokenCount];
			while (st.hasMoreTokens()) {
				formattedAttr[i++] = st.nextToken();
			}
			int length = formattedAttr.length;
			for (int iCount = 0; iCount < iTableFieldRowCount; iCount++) {
				jcoTable.setRow(iCount);
				sTableFieldValue = (String) jcoTable.getValue(sPrimaryFieldName);
				if (sTableFieldValue.equalsIgnoreCase(sPrimaryFieldValue)) {
					if (isCUA.equalsIgnoreCase(hmConstants.get(NO)) || iTokenCount > 2) {
						if (isCUA.equalsIgnoreCase(hmConstants.get(NO))
								|| jcoTable.getString(hmConstants.get(SUBSYSTEM))
										.equalsIgnoreCase(formattedAttr[1])) {
							jcoTable.deleteRow();
							logger.info(className, sMethodName, formattedAttr[length - 1]
									+ " to be updated");
						}
					}else if (isCUA.equalsIgnoreCase(hmConstants.get(YES)) && iTokenCount == 2) {
						if (jcoTable.getString(sPrimaryFieldName)
								.equalsIgnoreCase(formattedAttr[1])) {
					jcoTable.deleteRow();
					logger.info(className, sMethodName, formattedAttr[length - 1]
							+ " to be updated");
				}
			}
				}
			}

			sBAPIName = oUMUtil
					.getAddMultiValueDataBAPIName(bapiStructureName, isCUA);

			JCoFunction jcoFunction;

			jcoFunction = oSAPUtil.getJCOFunction(jcoConnection, sBAPIName);
			JCoParameterList jcoParameterList = jcoFunction.getImportParameterList();

			sUserName = sUserID.toUpperCase();

			jcoParameterList.setValue(hmConstants.get(USERNAME), sUserName);

			JCoTable jcoTable1 = jcoFunction.getTableParameterList().getTable(
					bapiStructureName);
			iTableFieldRowCount = jcoTable.getNumRows();
			// Add the already assigned roles/profiles/parameters in tableActGroup
			for (int iIndex = 0; iIndex < iTableFieldRowCount; iIndex++) {
				jcoTable.setRow(iIndex);
				jcoTable1.appendRow();

				if ((dataList != null) && !dataList.isEmpty()) {
					int iDataListSize = dataList.size();
					for (int iIndex1 = 0; iIndex1 < iDataListSize; iIndex1++) {
						SAPUMAttributeMapBean sAPAttributeMapBean = (SAPUMAttributeMapBean) dataList
								.get(iIndex1);
						if (isCUA.equalsIgnoreCase(hmConstants.get(NO))) {
							// if is UM disregard system name attribute
							if (sAPAttributeMapBean.getBapiFieldName().equalsIgnoreCase(
									hmConstants.get(SUBSYSTEM)))
								continue;
						}
						if (sAPAttributeMapBean != null
								&& sAPAttributeMapBean.getFieldValue() != null) {
							logger.debug(className, sMethodName, "Setting existing multi value data " + jcoTable.getValue(sAPAttributeMapBean.getBapiFieldName()));
							jcoTable1.setValue(sAPAttributeMapBean.getBapiFieldName(),
									jcoTable.getValue(sAPAttributeMapBean.getBapiFieldName()));
						}
					}
				}
			}

			// Add the roles/profiles/parameters entered in process form in
			// tableActGroup
			jcoTable1.appendRows(1);

			if ((dataList != null) && !dataList.isEmpty()) {
				int iDataListSize = dataList.size();

				for (int iIndex1 = 0; iIndex1 < iDataListSize; iIndex1++) {
					SAPUMAttributeMapBean sAPAttributeMapBean = (SAPUMAttributeMapBean) dataList
							.get(iIndex1);
					if (isCUA.equalsIgnoreCase(hmConstants.get(NO))) {
						// if is UM disregard system name attribute
						if (sAPAttributeMapBean.getBapiFieldName().equalsIgnoreCase(
								hmConstants.get(SUBSYSTEM)))
							continue;
					}
					if ((sAPAttributeMapBean != null)
							&& (sAPAttributeMapBean.getBapiFieldName() != null)) {
						logger.debug(className, sMethodName, "Setting new multi value data " + sAPAttributeMapBean.getFieldValue());
						jcoTable1.setValue(sAPAttributeMapBean.getBapiFieldName(),
								sAPAttributeMapBean.getFieldValue());
					}
				}
			}

			if ((dataList != null) && !dataList.isEmpty()) {
				int iDataListSize = dataList.size();
				JCoStructure jcoStructure = null;
				for (int iIndex1 = 0; iIndex1 < iDataListSize; iIndex1++) {
					SAPUMAttributeMapBean sAPAttributeMapBean = (SAPUMAttributeMapBean) dataList
							.get(iIndex1);
					String sStructurnameX = sAPAttributeMapBean.getBapiStructureX();
					String sFieldNameX = sAPAttributeMapBean.getBapiFieldNameX();
					if (!StrUtil.isEmpty(sStructurnameX) && !StrUtil.isEmpty(sFieldNameX)) {
						if (iIndex1 == 0){
							jcoStructure = jcoFunction.getImportParameterList().getStructure(
									sStructurnameX);
							logger.debug(className, sMethodName, "Setting " + sStructurnameX + "_" + sFieldNameX + " to " +hmConstants.get(X) ); 
							jcoStructure.setValue(sFieldNameX, hmConstants.get(X));
						}
					}
				}
			}

			logger.info(className, sMethodName,
					"Executing BAPI for MultiValue Attributes");
			jcoFunction.execute(jcoConnection);
			hashStatusMessage = oUMUtil.getBAPIStatus(jcoFunction, "");

			if (hashStatusMessage.get(hmConstants.get(TYPE)).equals(
					hmConstants.get(S))) {
				logger.info(className, sMethodName,
						"MultiValued Attribute updated successfully");
				sStatus = MULTI_DATA_UPDATED_SUCCESSFUL;
			} else {
				logger.error(className, sMethodName, hashStatusMessage.get(hmConstants
						.get(MESSAGE)));
				sStatus = UPDATE_MULTIVALUE_DATA_FAILED;
			}
		} catch (JCoException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.error(className, sMethodName, e.getMessageText());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			sStatus = UPDATE_MULTIVALUE_DATA_FAILED;
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sStatus = UPDATE_MULTIVALUE_DATA_FAILED;
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sStatus = UPDATE_MULTIVALUE_DATA_FAILED;
		}
		return sStatus;
	}

	/**
	 * Description : Provisions custom attributes for users by running a custom
	 * BAPI
	 * 
	 * @param sUserId
	 *          The User ID to be modified. For example: John.Doe
	 * @param sUserIDBAPIName
	 *          Attribute in the custom table that holds user ID values. For
	 *          example: BNAME
	 * @param BAPIName
	 *          Name of the custom BAPI that you created for fetching values from
	 *          the custom attribute. For example: ZXLCBAPI_ZXLCUSR_USERCHANGE
	 * @param BAPIfieldname
	 *          Name of the attribute in the custom table. For example: JOB_DESC
	 * @param BAPIStructurename
	 *          The BAPI Structure name wherein the BAPIfieldname may exist For
	 *          example: ADDRESS
	 * @param FieldValue
	 *          Value to be set for the custom field. For example: Developer
	 * @return String Returns the response code that is mapped in the adapter
	 * 
	 * @throws ConnectorException
	 */
	public String modifyCustomAttr(String sUserId, String sUserIDBAPIName,
			String BAPIName, String BAPIfieldname, String BAPIStructurename,
			String FieldValue) throws ConnectorException {
		String sStatus = null;
		HashMap<String, String> hashStatusMessage = new HashMap<String, String>();
		String sMethodName = "modifyCustomAttr()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {
			if (StrUtil.isEmpty(sUserId) || StrUtil.isEmpty(sUserIDBAPIName)
					|| StrUtil.isEmpty(BAPIName) || StrUtil.isEmpty(BAPIfieldname)
					|| StrUtil.isEmpty(BAPIStructurename)) {
				logger.error(className, sMethodName,
						"Mandatory values not sent to method");
				sStatus = INSUFFICIENT_INFORMATION;
				throw new ConnectorException(INSUFFICIENT_INFORMATION);
			}
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					hmConstants.get(BAPIName));
			JCoParameterList jcoParameterList = jcoFunction.getImportParameterList();
			logger.debug(className, sMethodName, "Setting " + sUserIDBAPIName + " to " + sUserId);
			jcoParameterList.setValue(sUserIDBAPIName, sUserId);
			JCoStructure jcoStructure = jcoFunction.getImportParameterList()
					.getStructure(BAPIStructurename);
			logger.debug(className, sMethodName, "Setting " + BAPIStructurename + "_"+BAPIfieldname + " to " + FieldValue);
			jcoStructure.setValue(BAPIfieldname, FieldValue);
			jcoFunction.execute(jcoConnection);
			hashStatusMessage = oUMUtil.getBAPIStatus(jcoFunction, "");
			/**
			 * we expect the custom BAPI to give us a response on how the custom BAPI
			 * executed. we look for a 'RETURN' table having field 'MESSAGE', 'TYPE'&
			 * 'NUMBER'
			 */
			logger.debug(className, sMethodName, hashStatusMessage.get(hmConstants
					.get(MESSAGE)));
			if (hashStatusMessage.get(hmConstants.get(TYPE)).equals(
					hmConstants.get(S))) {
				logger.info(className, sMethodName, sUserId + " Modified Successfully");
				sStatus = USER_MODIFIED_SUCCESSFUL;
			} else {
				logger.error(className, sMethodName, "User update Failed");
				logger.error(className, sMethodName, hashStatusMessage.get(hmConstants
						.get(MESSAGE)));
				sStatus = USER_MODIFICATION_FAILED;
			}
		} catch (JCoException jcoException) {
			logger.error(className, sMethodName, "jcoException:"
					+ jcoException.getMessage());
			logger.setStackTrace(jcoException, className, sMethodName, jcoException
					.getMessage());
			sStatus = USER_MODIFICATION_FAILED;
			throw new ConnectorException(jcoException);
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, "ConnectorException:"
					+ exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			if (StrUtil.isEmpty(sUserId))
				sStatus = USER_MODIFICATION_FAILED;
			throw new ConnectorException(exception);
		} catch (Exception exception) {
			logger.error(className, sMethodName, "exception:"
					+ exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sStatus = USER_MODIFICATION_FAILED;
			throw new ConnectorException(exception);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sStatus;
	}

	/**
	 * Description : Creates a request taking values from the HashMap and
	 * submits it to the Target SAP CUP machine.
	 * 
	 * @param attrMap
	 *            Attribute mapped parent form values
	 * @param htCUPITRattributes
	 *            SoD ITResources values
	 * @param hmCUPConstants
	 * 			  CUP Constant values from lookup - Lookup.SAP.CUP.Constants
	 * @param childAttrMap
	 * 			Attribute mapped child form values
	 * @param RequestType
	 * 			Request Type of the request being submitted.
	 * 			e.g. CREATE_USER
	 * @param Application
	 * 			Back end target system name. e.g. E60
	 * @param Priority
	 * 			Priority of the request. e.g. HIGH
	 * @return
	 * @throws ConnectorException
	 */
	public String createCUPRequest(HashMap<String, String> attrMap,
			Hashtable<String, String> htCUPITRattributes,
			HashMap<String, String> hmCUPConstants,
			HashMap<String, String> childAttrMap, String RequestType,
			String Application, String Priority) throws ConnectorException {
		String sReqNo = null;
		Object requestDetailsInst = null;
		Object customDataInst = null;
		Object roleDataInst = null;
		String sKey = null;
		String sLoggerMethodName = "createCUPRequest()";
		String sMethodName = "createCUPRequest()";
		ObjectFactory objFactory = new ObjectFactory();
		ArrayOfCustomFieldsDTO cdto = objFactory.createArrayOfCustomFieldsDTO();
		RoleData[] roles = new RoleData[1];
		boolean customFlag = false;
		try {
			logger.setMethodStartLog(className, sLoggerMethodName);
			UMUtility oUMUtil = new UMUtility(logger, hmConstants);
			
			RequestDetailsData oReqData = objFactory.createRequestDetailsData();

			logger.debug(className, sLoggerMethodName, "Innitialize request details class");
			Class<?> requestDetailsClass = Class
			.forName(hmCUPConstants.get(REQUEST_DETAILS_CLASS));
			logger.debug(className, sLoggerMethodName, "Innitialize custom fields class");
			Class<?> customDataClass = Class
			.forName(hmCUPConstants.get(CUSTOM_FIELDS_CLASS));
			logger.debug(className, sLoggerMethodName, "Innitialize request details instance");
			requestDetailsInst = requestDetailsClass.newInstance();

			logger.debug(className, sLoggerMethodName, "Setting request type to " + RequestType);
			sMethodName = oUMUtil.getMethodName(requestDetailsClass,hmCUPConstants.get(requestType),hmCUPConstants.get(set));
			Method method = requestDetailsClass.getDeclaredMethod(sMethodName,JAXBElement.class);
			method.invoke(requestDetailsInst, objFactory.createRequestDetailsDataRequestType(RequestType));

			logger.debug(className, sLoggerMethodName, "Setting application to " + Application);
			sMethodName = oUMUtil.getMethodName(requestDetailsClass, hmCUPConstants.get(application), hmCUPConstants.get(set));
			method = requestDetailsClass.getDeclaredMethod(sMethodName, JAXBElement.class);
			method.invoke(requestDetailsInst, objFactory.createRequestDetailsDataApplication(Application));

			logger.debug(className, sLoggerMethodName, "Setting priority to " + Priority);
			sMethodName = oUMUtil.getMethodName(requestDetailsClass,hmCUPConstants.get(priority), hmCUPConstants.get(set));
			method = requestDetailsClass.getDeclaredMethod(sMethodName, JAXBElement.class);
			method.invoke(requestDetailsInst,objFactory.createRequestDetailsDataPriority(Priority));
			
			Iterator<String> itr = attrMap.keySet().iterator();
			while (itr.hasNext()) {
				sKey = (String) itr.next();
				String[] sKeyVal = sKey.split(";");
				
				if(sKeyVal[0].equalsIgnoreCase(hmCUPConstants.get(CUSTOM))) {
					customDataInst = customDataClass.newInstance();
					logger.debug(className, sLoggerMethodName,"Setting custom method "+ attrMap.get(sKey));
					sMethodName = oUMUtil.getMethodName(customDataClass, sKeyVal[1], hmCUPConstants.get(set));
					method = customDataClass.getDeclaredMethod(sMethodName, JAXBElement.class);
					method.invoke(customDataInst,objFactory.createCustomFieldsDTOName(sKeyVal[1]));
					cdto.getCustomFieldsDTO().add((CustomFieldsDTO)customDataInst);
					customFlag = true;
					continue;
				}
				sMethodName = oUMUtil.getMethodName(requestDetailsClass, sKeyVal[1], hmCUPConstants.get(set));
				logger.debug(className, sLoggerMethodName, "Setting " + sKey + " to "+ attrMap.get(sKey));
				method = requestDetailsClass.getDeclaredMethod(sMethodName,	JAXBElement.class);
				String prefix = "createRequestDetailsData";
				method.invoke(requestDetailsInst, getJAXBElementValue(oUMUtil, objFactory, sKeyVal[0],sKeyVal[1], attrMap.get(sKey), prefix));
			}
			if(customFlag) {
				logger.info(className, sLoggerMethodName, "Setting custom attributes in request details");
				method = requestDetailsClass.getDeclaredMethod(hmCUPConstants.get(setCustomField), JAXBElement.class);
				method.invoke(requestDetailsInst, objFactory.createRequestDetailsDataCustomField(cdto));
			}

			if(childAttrMap != null) {
				logger.debug(className, sLoggerMethodName, "Initialize role data class");
				Class<?> roleDataClass = Class.forName(hmCUPConstants.get(ROLE_DATA_CLASS));
				Iterator<String> itrChild = childAttrMap.keySet().iterator();
				roleDataInst = roleDataClass.newInstance();
				ArrayOfRoleData arRoleData = objFactory.createArrayOfRoleData();
				while(itrChild.hasNext()) {
					sKey = (String) itrChild.next();
					String[] sKeyVal = sKey.split(";");
					sMethodName = oUMUtil.getMethodName(roleDataClass, sKeyVal[0], hmCUPConstants.get(set));
					method = roleDataClass.getDeclaredMethod(sMethodName, JAXBElement.class);
					logger.debug(className, sLoggerMethodName, "Child Attribute : Key -   " + sKey + " Value - " + childAttrMap.get(sKey));
					String prefix = "createRoleData";
					method.invoke(roleDataInst, getJAXBElementValue(oUMUtil, objFactory, sKeyVal[1], sKeyVal[0], childAttrMap.get(sKey), prefix));
					arRoleData.getRoleData().add((RoleData) roleDataInst);
				}
				logger.info(className, sLoggerMethodName, "Setting role attributes in request details");
				method = requestDetailsClass.getDeclaredMethod(hmCUPConstants.get(setRoles), JAXBElement.class);
				logger.info(className, sLoggerMethodName, method.getName());
				method.invoke(requestDetailsInst, objFactory.createRequestDetailsDataRoles(arRoleData));
			}
			sMethodName = "createCUPRequest()";
			logger.debug(className, sLoggerMethodName, "Creating connection Url");	
			String connectionUrl = "http://" + htCUPITRattributes.get(SERVER) + ":"
					+ htCUPITRattributes.get(PORT)
					+ hmCUPConstants.get(SUBMIT_REQ_WSDL_PATH);
			logger.info(className, sLoggerMethodName, "Connection URL : " + connectionUrl);
			RequestSubmissionResult result = null;

			URL url = new URL(connectionUrl);
			QName qName = new QName(hmCUPConstants.get(SUBMIT_REQ_NAMESPACE_URI),
					hmCUPConstants.get(SUBMIT_REQ_LOCAL_PART));
			logger.debug(className, sLoggerMethodName, "Got the QName instance");
			SAPGRCACIDMSUBMITREQUEST wsSubmitRequest = new SAPGRCACIDMSUBMITREQUEST(url, qName);
			logger.debug(className, sLoggerMethodName, "Got the Submit Request instance ");
			
			SAPGRCACIDMSUBMITREQUESTViDocument port = wsSubmitRequest.getConfig1PortDocument();
			logger.debug(className, sLoggerMethodName, "Got the Port instance ");
			((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, 
					(String) htCUPITRattributes.get(username));
			((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, 
					(String) htCUPITRattributes.get(password));
			
			logger.info(className, sLoggerMethodName, "Submitting Request >......");
			result = port.getSubmitRequest((RequestDetailsData) requestDetailsInst);
			
			logger.info(className, sLoggerMethodName, "Result >....." + result);
			if (result != null) {
				JAXBElement<ServiceStatusDTO> status = result.getStatus();
				JAXBElement<String> reqno  = result.getRequestNo();
				if(reqno == null) {		
					logger.error(className, sLoggerMethodName, "Message Code " + status.getValue().getMsgCode().getValue());
					logger.error(className, sLoggerMethodName, "Message Description " + status.getValue().getMsgDesc().getValue());
					logger.error(className, sLoggerMethodName, "Message Type " + status.getValue().getMsgType().getValue());
					throw new ConnectorException(status.getValue().getMsgDesc().getValue());
				}
				sReqNo = result.getRequestNo().getValue();
                logger.info(className, sLoggerMethodName, "Request No " + sReqNo);
				logger.info(className, sLoggerMethodName, "Message Code " + status.getValue().getMsgCode().getValue());
				logger.info(className, sLoggerMethodName, "Message Description " + status.getValue().getMsgDesc().getValue());
				logger.info(className, sLoggerMethodName, "Message Type " + status.getValue().getMsgType().getValue());
			} else {
				logger.error(className, sLoggerMethodName, "Result is null");
				throw new ConnectorException();
			}
		} catch (ConnectorException exception) {
			logger.error(className, sLoggerMethodName, "ConnectorException:"
					+ exception.getMessage());
			logger.setStackTrace(exception, className, sLoggerMethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		} catch (Exception e) {
			logger.error(className, sLoggerMethodName, "ConnectorException:"
					+ e.getMessage());
			logger.setStackTrace(e, className, sLoggerMethodName, e.getMessage());
			throw new ConnectorException(e);
		}
		logger.setMethodFinishLog(className, sLoggerMethodName);
		return sReqNo;
	}

	public JAXBElement<String> getJAXBElementValue(UMUtility oUMUtil, ObjectFactory objFactory, String attrType, String attrName, String attrValue, String prefix) throws Exception{
		String methodName = oUMUtil.getMethodName(objFactory.getClass(), attrName, prefix);
		logger.debug(className, "getJAXBElementValue ", "Method Name " + methodName);
		Method method = null;
		XMLGregorianCalendar xgcal = null;
		GregorianCalendar cal = new GregorianCalendar();
		Object val = attrValue;
		if (attrType.equalsIgnoreCase("DATE")){
			if (attrValue.indexOf("T") == -1 && attrValue.indexOf("Z") == -1) {
			    DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
			    Date date = df.parse(attrValue);
			    cal.setTime(date);	
			} else {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.ss'Z'");
			    Date date = df.parse(attrValue);
			    cal.setTime(date);	
			}
			xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			val = xgcal;
			method = objFactory.getClass().getDeclaredMethod(methodName, XMLGregorianCalendar.class);

		} else {
			method = objFactory.getClass().getDeclaredMethod(methodName, String.class);
		}
		Object obj = method.invoke(objFactory, val);
		logger.debug(className, "getJAXBElementValue ", obj.toString());
		return (JAXBElement<String>)obj;
	}

	/**
	 * Description : Gets the status of the request from the Target CUP system
	 * @param responseID
	 * 			Response ID of the previously submitted request.
	 * 			e.g. 651
	 * @param htCUPITRattributes
	 * 			SoD IT Resource values.
	 * @param hmCUPConstants
	 * 			CUP Constants
	 * @return
	 */
	public String getStatus(String responseID,
			Hashtable<String, String> htCUPITRattributes,
			HashMap<String, String> hmCUPConstants) throws ConnectorException {
		String sStatus = null;
		String MethodName = "getStatus()";
		try{		
			logger.setMethodStartLog(className, MethodName);
			String connectionUrl = "http://" + htCUPITRattributes.get(SERVER) + ":"
			+ htCUPITRattributes.get(PORT)
			+ hmCUPConstants.get(REQ_STATUS_WSDL_PATH);
			SAPGRCACIDMREQUESTSTATUSViDocument port = null;
			RequestStatusDTO result=null;
			try{
				URL url = new URL(connectionUrl);
				QName qName = new QName(hmCUPConstants.get(REQ_STATUS_NAMESPACE_URI),
						hmCUPConstants.get(REQ_STATUS_LOCAL_PART));
				logger.debug(className, MethodName, "Got the QName instance");	
				SAPGRCACIDMREQUESTSTATUS wsRequestStatus = new SAPGRCACIDMREQUESTSTATUS(url, qName);
				port = wsRequestStatus.getConfig1PortDocument();
	 			
				((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, 
						(String) htCUPITRattributes.get(username));
				((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, 
						(String) htCUPITRattributes.get(password));

				logger.info(className, MethodName, "Getting Request Status >..............");
				result =  port.getStatus(responseID, "EN");
			}catch(Exception e){
				logger.error(className, MethodName, "ConnectorException:"
						+ e.getMessage());
				logger.setStackTrace(e, className, MethodName, e.getMessage());
				throw new ConnectorException(e);
			}
			if(result != null){
				logger.debug(className, MethodName, "Request No " + result.getRequestNumber().getValue());
				logger.info(className, MethodName, "Status: " + result.getStatus().getValue());
				logger.info(className, MethodName, "UserName: " + result.getUserName().getValue());
				logger.debug(className, MethodName, "DueDate: " + result.getDueDate().getValue());
				logger.debug(className, MethodName, "Stage: " + result.getStage().getValue());
				sStatus = result.getStatus().getValue();
			}else{
				logger.error(className, MethodName,"Result is null.");
			}
		}catch (ConnectorException exception) {
			logger.error(className, MethodName, "ConnectorException:"
					+ exception.getMessage());
			logger.setStackTrace(exception, className, MethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		}catch(Exception e){
			logger.error(className, MethodName, "ConnectorException:"
					+ e.getMessage());
			logger.setStackTrace(e, className, MethodName, e
					.getMessage());
			throw new ConnectorException(e);
		}
		logger.setMethodFinishLog(className, MethodName);
		return sStatus;
	}

	/**
	 * Description : Gets the Audit Trail for a request from the Target
	 * @param responseID
	 * 			Response ID of the previously submitted request.
	 * 			e.g. 651
	 * @param htCUPITRattributes
	 * 			SoD IT Resource values.
	 * @param hmCUPConstants
	 * 			CUP Constants
	 * @throws ConnectorException
	 */
	public void auditTrail(String responseID,
			Hashtable<String, String> htCUPITRattributes,
			HashMap<String, String> hmCUPConstants) throws ConnectorException {
			String MethodName = "auditTrail()";
			boolean isErrorMsg = false;
		try{		
			logger.setMethodStartLog(className, MethodName);
			String connectionUrl = "http://" + htCUPITRattributes.get(SERVER)
					+ ":" + htCUPITRattributes.get(PORT)
					+ hmCUPConstants.get(AUDIT_TRAIL_WSDL_PATH);
			SAPGRCACIDMAUDITTRAILViDocument getAuditTrailDocInstance=null;
			AuditLogResult result=null;
			JAXBElement<ArrayOfAuditLogDTO1> resultDTO = null;
			AuditLogDTO auditLogDTO=null;
			RequestHistoryDTO historyDTO = null;
			JAXBElement<ArrayOfRequestHistoryDTO> childDTOs = null;
			try{
				URL url = new URL(connectionUrl);
				QName qName = new QName(hmCUPConstants.get(AUDIT_TRAIL_NAMESPACE_URI),
						hmCUPConstants.get(AUDIT_TRAIL_LOCAL_PART));
				logger.debug(className, MethodName, "Got the QName instance");
				
				SAPGRCACIDMAUDITTRAIL wsAuditTrail = new SAPGRCACIDMAUDITTRAIL(url, qName);
				SAPGRCACIDMAUDITTRAILViDocument port = wsAuditTrail.getConfig1PortDocument();
				
				((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, 
						(String) htCUPITRattributes.get(username));
				((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, 
						(String) htCUPITRattributes.get(password));

                logger.debug(className, MethodName, "Got the Audit Trail Document instance");
				result = port.getAuditLogs(responseID, "", "", "", "", "", "");
                
				logger.info(className, MethodName, "Getting Audit Trail >..............");
				resultDTO = result.getAuditLogDTO();
			}catch(Exception e){
				logger.error(className, MethodName, "ConnectorException:"
						+ e.getMessage());
				logger.setStackTrace(e, className, MethodName, e
						.getMessage());
				throw new ConnectorException(e);
			}
			int i = 0;
			if(result != null){
				while(resultDTO != null && i<resultDTO.getValue().getAuditLogDTO().getValue().getAuditLogDTO().size()) {
					auditLogDTO = (AuditLogDTO) resultDTO.getValue().getAuditLogDTO().getValue().getAuditLogDTO().get(i);
					logger.debug(className, MethodName, "******** Parsing audit Log Element: " +(i+1)+" ********");
					logger.debug(className, MethodName, "CreateDate: " + auditLogDTO.getCreateDate().getValue());
					logger.debug(className, MethodName, "RequestedId: " + auditLogDTO.getRequestId().getValue());
					logger.debug(className, MethodName, "RequestedBy: " + auditLogDTO.getRequestedBy().getValue());
					logger.debug(className, MethodName, "Status: " + auditLogDTO.getStatus().getValue());
					logger.debug(className, MethodName, "SubmittedBy: " + auditLogDTO.getSubmittedBy().getValue());
					
					int iRequestHst = auditLogDTO.getRequestHst().getValue().getRequestHistoryDTO().size();
					int k = 0;
					while(k<iRequestHst) {
						logger.debug(className, MethodName, "Parsing Request history Element: " +(k+1));
						historyDTO = auditLogDTO.getRequestHst().getValue().getRequestHistoryDTO().get(k);
						childDTOs = historyDTO.getChildDTOs();
						logger.debug(className, MethodName, "Display String: " + historyDTO.getDisplayString().getValue());
						logger.debug(className, MethodName, "Action Date: " + historyDTO.getActionDate().getValue().toString());

						int iChildDTO = childDTOs.getValue().getRequestHistoryDTO().size();
						int j = 0;
						while (j < iChildDTO) {
						historyDTO= childDTOs.getValue().getRequestHistoryDTO().get(j);
						logger.debug(className, MethodName, "Parsing child Objects Element: " +(j+1));
						logger.debug(className, MethodName, "Display String: " + historyDTO.getDisplayString().getValue());
						logger.debug(className, MethodName, "Action Date: " + historyDTO.getActionDate().getValue());
						if(historyDTO.getDisplayString().getValue().indexOf("Error Message") != -1) {
							logger.error(className, MethodName, "Description : " + historyDTO.getDescription());
							logger.error(className, MethodName, "Display String: " + historyDTO.getDisplayString());
							isErrorMsg = true;
						}
						else 
							logger.debug(className, MethodName, "Description : " + historyDTO.getDescription());
						childDTOs = historyDTO.getChildDTOs();
						if(childDTOs == null)
							break;
						j++;
						}
						k++;
					}
					i++;
				}
			}else{
				logger.error(className, MethodName,"Result is null");
			}
			
			if(!isErrorMsg) {
				logger.info(className, MethodName,
								"No error's in audit trail. In debug mode the entire audit trail can be viewed");
			}
		}catch (ConnectorException exception) {
			logger.error(className, MethodName, "ConnectorException:"
					+ exception.getMessage());
			logger.setStackTrace(exception, className, MethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		}catch(Exception e){
			logger.error(className, MethodName, "ConnectorException:"
					+ e.getMessage());
			logger.setStackTrace(e, className, MethodName, e
					.getMessage());
			throw new ConnectorException(e);
		}
		logger.setMethodFinishLog(className, MethodName);
	}

	/**
	 * Description:This method is used to add custom multi-value attribute to a user,
	 * like structural profiles
	 * 
	 * @param sUserID
	 *            User ID of the user to be modified. For example: John.Doe
	 * @param userIDFieldName
	 * 			  User ID field in BAPI . For example: USERNAME
	 * @param AttributeName
	 *            Key multi-value Attribute value in the child form. For example:
	 *            1~E60~V_SYS_ADMIN
	 * @param bapiFieldName
	 *            SAP field name of Key multivalued attribute in the child form.
	 *            For example: PROFILE
	 * @param bapiStructureName
	 *            Structure name in the BAPI. Within this the BAPI field name
	 *            exists. For example: PROFILES
	 * @param getAttrBAPIName
	 * 			  Custom BAPI which will retrieve all the child multivalued attributes
	 * 			  from the target. For example: ZBAPI_USER_STRUCTPROFILES_READ
	 * @param bapi
	 * 			  Name of the custom BAPI which will add the child multivalued attribute
	 * @param isCUA
	 * 			  Target is CUA enabled or not.e.g. NO
	 * @return String Returns the response code which is mapped in the Adapter.
	 * @throws ConnectorException
	 */
	public String addCustomMultiValueData(
			ArrayList<SAPUMAttributeMapBean> dataList, String sUserID,
			String userIDFieldName, String getAttrBAPIName,
			String bapiStructureName, String bapiFieldName, String bapi, String isCUA) {
		String sPrimaryFieldValue = null;
		String sStatus = null;
		String sTableFieldValue = null;
		String sUserName = null;
		HashMap<String, String> hashStatusMessage = new HashMap<String, String>();
		String sMethodName = "addCustomMultiValueData()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUtil = new UMUtility(logger, hmConstants);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		StringUtil StrUtil = new StringUtil();
		try {
			if ((dataList != null) && !dataList.isEmpty()) {
				int iDataListSize = dataList.size();

				for (int iIndex = 0; iIndex < iDataListSize; iIndex++) {
					SAPUMAttributeMapBean sAPAttributeMapBean = (SAPUMAttributeMapBean) dataList
							.get(iIndex);

					if ((sAPAttributeMapBean != null)
							&& sAPAttributeMapBean.getBapiFieldName().equalsIgnoreCase(
									bapiFieldName)) {
						sPrimaryFieldValue = sAPAttributeMapBean.getFieldValue();
						break;
					}
				}
			}
			JCoTable jcoTable = null;
			/** Get the list of existing Roles/Profiles Assigned to that user.
			 *  If the BAPI name is not specified no comparison is done
			 */
			if (!StrUtil.isEmpty(getAttrBAPIName) && !StrUtil.isEmpty(userIDFieldName)) {
				jcoTable = getChildData(sUserID, bapiStructureName,
						getAttrBAPIName, userIDFieldName);
			} 

			// Check whether Role/Profile entered in process form is already assigned
			int iTableFieldRowCount = 0;
			if(jcoTable!= null)
				iTableFieldRowCount = jcoTable.getNumRows();
			logger.debug(className, sMethodName, "# of existing rows : " + iTableFieldRowCount);

			for (int iCount = 0; iCount < iTableFieldRowCount; iCount++) {
				jcoTable.setRow(iCount);
				sTableFieldValue = (String) jcoTable.getValue(bapiFieldName);

				if (sTableFieldValue.equalsIgnoreCase(sPrimaryFieldValue)) {
					logger.info("Multivalue Data Already assigned to the User");
					sStatus = MULTIVALUE_DATA_ALREADY_EXISTS;
				}
			}
			/**
			 * if multivalued Attr already exists then go to end of method
			 */
			if (StrUtil.isEmpty(sStatus)) {
				
				JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection, bapi);
				JCoParameterList jcoParameterList = jcoFunction
						.getImportParameterList();

				sUserName = sUserID.toUpperCase();

				jcoParameterList.setValue(userIDFieldName, sUserName);

				JCoTable jcoTable1 = jcoFunction.getTableParameterList().getTable(
						bapiStructureName);

				// We assume the custom BAPI is designed to append new attribute
				// and not replace

				// Add the roles/profiles/parameters entered in process form in
				// tableActGroup
				jcoTable1.appendRows(1);

				if ((dataList != null) && !dataList.isEmpty()) {
					int iDataListSize = dataList.size();

					for (int iIndex1 = 0; iIndex1 < iDataListSize; iIndex1++) {
						SAPUMAttributeMapBean sAPAttributeMapBean = (SAPUMAttributeMapBean) dataList
								.get(iIndex1);
						if (isCUA.equalsIgnoreCase(hmConstants.get(NO))) {
							// if is UM disregard system name attribute
							if (sAPAttributeMapBean.getBapiFieldName().equalsIgnoreCase(
									hmConstants.get(SUBSYSTEM)))
								continue;
						}
						if ((sAPAttributeMapBean != null)
								&& (sAPAttributeMapBean.getBapiFieldName() != null)) {
							logger.debug(className, sMethodName,
									"Setting newly assigned value"
											+ sAPAttributeMapBean.getFieldValue());
							jcoTable1.setValue(sAPAttributeMapBean.getBapiFieldName(),
									sAPAttributeMapBean.getFieldValue());
						}
					}
				}

				logger.info(className, sMethodName,
						"Executing BAPI for MultiValue Attributes");
				jcoFunction.execute(jcoConnection);
				hashStatusMessage = oUtil.getBAPIStatus(jcoFunction, "");

				if (hashStatusMessage.get(hmConstants.get(TYPE)).equals(
						hmConstants.get(S))) {
					logger.info(className, sMethodName,
							"MultiValued Attribute added successfully");
					sStatus = MULTI_DATA_ADDED_SUCCESSFUL;
				}  else {
					logger.error(className, sMethodName,
							"Error while adding MultiValueAttribute");
					logger.error(className, sMethodName, hashStatusMessage
							.get(hmConstants.get(MESSAGE)));
					sStatus = ADD_MULTIVALUE_DATA_FAILED;
				}
			}// end : if multivalued Attr already exists
		} catch (JCoException e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.error(className, sMethodName, e.getMessageText());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			sStatus = ADD_MULTIVALUE_DATA_FAILED;
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			if (StrUtil.isEmpty(sStatus))
				sStatus = ADD_MULTIVALUE_DATA_FAILED;
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sStatus = ADD_MULTIVALUE_DATA_FAILED;
		}
		return sStatus;
	}

	/**
	 * Description:Used to remove a custom multivalued attribute from a user
	 * 
	 * @param sUserID
	 *            User ID of the user to be modified. For example: John.Doe
	 * @param userIdBAPIName
	 * 			  The corresponding field label for the user name in the BAPI.
	 * 			  e.g. USERNAME          
	 * @param attributeName
	 *            MultiValued Attribute value in the child form which is to be
	 *            deleted. For example: 1~E60~V_SYS_ADMIN
	 * @param sBAPIFieldName
	 *            Field name in the BAPI. The BAPI field name is part of
	 *            this structure. For example: PROFILE
	 * @param sBAPIStructureName
	 * 			  Structure name in the BAPI which is to be populated with the multi-
	 * 			  valued attribute to be deleted  e.g. PROFILES
	 * @param sBAPIName
	 * 			  Name of the custom BAPI created to delete the attribute.
	 * 			  e.g. ZBAPI_REMOVE_ATTR
	 * @param isCUA
	 * 			  Target is CUA enabled or not.e.g. NO
	 * @return String Returns the response code that is mapped in the adapter
	 * @throws ConnectorException
	 */
	public String removeCustomMultiValueData(String sUserID, String userIdBAPIName,
			 String sBAPIStructureName,String sBAPIName, String sBAPIFieldName, String attributeName,
			String isCUA) {
		HashMap<String, String> hashStatusMessage = new HashMap<String, String>();
		String sResponse = null;
		String sMethodName = "removeMultiValueData()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {

			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					sBAPIName);
			JCoParameterList jcoParameterList = jcoFunction.getImportParameterList();

			String sUserName = sUserID.toUpperCase();

			jcoParameterList.setValue(userIdBAPIName, sUserName);

			JCoTable tableActGroup = jcoFunction.getTableParameterList().getTable(
					sBAPIStructureName);
			StringTokenizer st = new StringTokenizer(attributeName, "~");
			int iTokenCount = st.countTokens();
			int i = 0;
			String[] formattedAttr = new String[iTokenCount];
			while (st.hasMoreTokens()) {
				formattedAttr[i++] = st.nextToken();
			}
			int length = formattedAttr.length;

			tableActGroup.appendRow();
			tableActGroup.setValue(sBAPIFieldName, formattedAttr[length -1]);
			if(isCUA.equalsIgnoreCase(hmConstants.get(YES)))
				tableActGroup.setValue(hmConstants.get(SUBSYSTEM), formattedAttr[length -2]);
			
			logger.info(className, sMethodName,
					"Executing BAPI for MultiValue Attributes");
			jcoFunction.execute(jcoConnection);

			hashStatusMessage = oUMUtil.getBAPIStatus(jcoFunction, "");

			if (hashStatusMessage.get(hmConstants.get(TYPE)).equals(
					hmConstants.get(S))) {
				logger.info(className, sMethodName,
						"MultiValued Attribute removed successfully");
				sResponse = MULTI_DATA_REMOVE_SUCCESSFUL;
			}else {
				logger.error(className, sMethodName, hashStatusMessage.get(hmConstants
						.get(MESSAGE)));
				sResponse = MULTI_DATA_REMOVE_FAILED;
			}

		} catch (JCoException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.error(className, sMethodName, exception.getMessageText());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sResponse = MULTI_DATA_REMOVE_FAILED;
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sResponse = MULTI_DATA_REMOVE_FAILED;
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sResponse = MULTI_DATA_REMOVE_FAILED;
		}

		return sResponse;
	}
	
	/**
	 * Description:Used to modify standard attributes of a user by running
	 * BAPI_USER_CHANGE
	 * 
	 * @param sUserID
	 *          User ID of the user to be modified For example: John.Doe
	 * @param sBAPIFieldName
	 *          Field name of the value to be modified in the BAPI. For example:
	 *          TEL1_NUMBR
	 * @param sBAPIStructure
	 *          Structure name in the BAPI. The BAPI field is part of the
	 *          structure. For example: ADDRESS
	 * @param sBAPIFieldNameX
	 *          Name of the field that is used to indicate whether or not the
	 *          value in sBAPIFieldName must be applied. The value 'X' is placed
	 *          in this field to indicate that sBAPIFieldName must be changed. For
	 *          example: TEL1_NUMBR
	 * @param sBAPIStructureX
	 *          Name of the structure that holds sBAPIFieldNameX. For example:
	 *          ADDRESSX
	 * @param sBAPIFieldValue
	 *          Field value entered on process form For example: 9812373445
	 * @return String Returns the response code that is mapped in the adapter
	 * 
	 * @throws ConnectorException
	 */
	// Start Bug: BUG 12334992 - SAP UM CONNECTOR TO PROPAGATE PASSWORD CHANGES TO CUA CHILD SYSTEMS 
	public String customModifyUser(String sUserID, String sBAPIFieldName,
			String sBAPIStructure, String sBAPIFieldNameX, String sBAPIStructureX,
			String sBAPIFieldValue) throws ConnectorException {
		String sStatus = null;
		HashMap<String, String> hashStatusMessage = new HashMap<String, String>();
		String sMethodName = "customModifyUser()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					hmConstants.get(ZXLCBAPI_ZXLCUSR_PW_CHANGE));

			JCoParameterList jcoParameterList = jcoFunction.getImportParameterList();
			logger.debug(className, sMethodName, "Setting " + hmConstants.get(USERNAME) +" - "+sUserID);
			jcoParameterList.setValue(hmConstants.get(USERNAME), sUserID);
//			jcoParameterList.setValue(hmConstants.get(CHANGE_FLAG), "");
			
			JCoStructure jcoStructure = jcoFunction.getImportParameterList()
					.getStructure(sBAPIStructure);
//			logger.debug(className, sMethodName, "Setting " +sBAPIStructure+ "_"+ sBAPIFieldName +" to "+sBAPIFieldValue);
			jcoStructure.setValue(sBAPIFieldName, sBAPIFieldValue);

			/*JCoStructure jcoStructureX = jcoFunction.getImportParameterList()
					.getStructure(sBAPIStructureX);
			logger.debug(className, sMethodName, "Setting " +sBAPIStructureX+ "_"+sBAPIFieldNameX +" - "+hmConstants.get(X));
			jcoStructureX.setValue(sBAPIFieldNameX, hmConstants.get(X));*/
			
			JCoStructure aStructureX = jcoFunction.getImportParameterList().getStructure("PASSWORDX");
			aStructureX.setValue("BAPIPWD", sBAPIFieldValue);
			
			logger.info(className, sMethodName, "Execute Custom Modify User BAPI");
			jcoFunction.execute(jcoConnection);
			hashStatusMessage = oUMUtil.getBAPIStatus(jcoFunction, "");

			if (hashStatusMessage.get(hmConstants.get(TYPE)).equals(
					hmConstants.get(S))) {
				logger.info(className, sMethodName, sUserID + " Modified Successfully");
				sStatus = USER_MODIFIED_SUCCESSFUL;
			} else {
				logger.error(className, sMethodName, "User update Failed");
				logger.error(className, sMethodName, hashStatusMessage.get(hmConstants
						.get(MESSAGE)));
				sStatus = USER_MODIFICATION_FAILED;
			}
		} catch (JCoException jcoException) {
			logger.error(className, sMethodName, jcoException.getMessage());
			logger.setStackTrace(jcoException, className, sMethodName, jcoException
					.getMessage());
			sStatus = USER_MODIFICATION_FAILED;
			throw new ConnectorException(jcoException);
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sStatus = USER_MODIFICATION_FAILED;
			throw new ConnectorException(exception);
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			sStatus = USER_MODIFICATION_FAILED;
			throw new ConnectorException(exception);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sStatus;
	}

	/**
	 * Description:This method is used to sync password of a user
	 * 
	 * @param sUserID
	 *          User ID of the user to be modified. For example: John.Doe
	 * @param sOldPassword
	 *          Old password of the user
	 * @param sNewPassword
	 *          New password of the user
	 * 
	 * @return String
	 * 
	 * @throws ConnectorException
	 */
	private String customChangePassword(String sUserID, String sOldPassword,
			String sNewPassword) throws ConnectorException {
		String sResponse = null;
		String sMethodName = "customChangePassword()";
		logger.setMethodStartLog(className, sMethodName);
		logger.info(className, sMethodName, "Custom Change Password Start");
		SAPUtil oSAPUtil = new SAPUtil(logger);
		try {
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					hmConstants.get(ZXLCBAPI_ZXLCUSR_PASSWORDCHNGE));

			JCoParameterList jcoParameterList = jcoFunction.getImportParameterList();
			jcoParameterList.setValue(hmConstants.get(ZXLUSERNAME), sUserID);
			jcoParameterList.setValue(hmConstants.get(ZXLOLD_PASSWORD), sOldPassword);
			jcoParameterList.setValue(hmConstants.get(ZXLNEW_PASSWORD), sNewPassword);
//			jcoParameterList.setValue(hmConstants.get(CHANGE_FLAG), "");

			jcoFunction.execute(jcoConnection);
			sResponse = PASSWORD_CHANGE_SUCCESSFUL;
		} catch (JCoException e) {
			sResponse = PASSWORD_CHANGE_FAILED;
			logger.error(className, sMethodName, "Password Change Exception "
					+ e.getMessage());
			if (e.getMessageNumber().equalsIgnoreCase(No139)) {
				sResponse =  PASSWORD_DISABLED;
			} else if (e.getMessageNumber().equalsIgnoreCase(No148)) {
				sResponse =  USER_DISABLED;
			}else if (e.getMessageNumber().equalsIgnoreCase(No152)) {
				sResponse =  NAME_OR_PASSWORD_INCORRECT;
			}else if (e.getMessageNumber().equalsIgnoreCase(No156)) {
				sResponse =  LOG_ON_WITH_DIALOG_USER;
			}else if (e.getMessageNumber().equalsIgnoreCase(No158)) {
				sResponse =  USER_IS_LOCKED;
			}else if (e.getMessageNumber().equalsIgnoreCase(No180)) {
				sResponse =  CHANGE_PASSWORD_ONCE_A_DAY;
			}else if (e.getMessageNumber().equalsIgnoreCase(No182)) {
				sResponse =  PASSWORD_EXPIRED;
			}else if (e.getMessageNumber().equalsIgnoreCase(No190)) {
				sResponse =  PASSWORD_CHANGE_DISABLED;
			}else if (e.getMessageNumber().equalsIgnoreCase(No191)) {
				sResponse =  PROGRAM_ERROR_OCCURED;
			}else if (e.getMessageNumber().equalsIgnoreCase(No197)) {
				sResponse =  PASSWORD_DOES_NOT_EXIST;
			}else if (e.getMessageNumber().equalsIgnoreCase(No198)) {
				sResponse =  USER_PASSWORD_DOES_NOT_EXIST;
			}else if (e.getMessageNumber().equalsIgnoreCase(No199)) {
				sResponse =  NO_REMOTE_PASSWORD_CHANGE;
			}else if (e.getMessageNumber().equalsIgnoreCase(No200)) {
				sResponse =  TOO_MANY_FAILED_ATTEMPTS;
			}else if (e.getMessageNumber().equalsIgnoreCase(No292)) {
				sResponse =  PASSWORD_RULES_TIGHTENED;
			}else if (e.getMessageNumber().equalsIgnoreCase(No790)) {
				sResponse = PASSWORD_DEACTIVATED;
			}
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
		} catch (ConnectorException ConnectorException) {
			logger.error(className, sMethodName, "Password Change Exception "
					+ ConnectorException.getMessage());
			logger.setStackTrace(ConnectorException, className, sMethodName,
					ConnectorException.getMessage());
			sResponse = PASSWORD_CHANGE_FAILED;
		} catch (Exception e) {
			logger.error(className, sMethodName, "Password Change Exception "
					+ e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			sResponse = PASSWORD_CHANGE_FAILED;
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sResponse;
	}
//End Bug: BUG 12334992 - SAP UM CONNECTOR TO PROPAGATE PASSWORD CHANGES TO CUA CHILD SYSTEMS 

// START:Code Modification for BUG 13475350
	/**
	 * Description: Triggered to remove the System from User from SAP Target.
	 * Checks whether any role/profile of that System is assigned to user or
	 * not.If yes then System is not removed This is triggered only on SUCCESS
	 * of Remove role/Remove profile task
	 * 
	 * @param sUserID
	 *            User ID of the user to be modified. For example: John.Doe
	 * @param sStructureName
	 *            BAPI Structure name of Role/Profile for Remove Role/Remove
	 *            Profile respectively
	 * 
	 * @param sMultiValuedAttribute
	 *            Role/Profile removed
	 * @param sChildPrimaryKey
	 *            Structure Field
	 * @param isCUA
	 *            CUA enabled
	 * @throws Exception
	 */
	public void removeSystem(String sUserID, String SystemName) {

		HashMap<String, String> hashStatusMessage = new HashMap<String, String>();
		String sBAPIName = null;
		String sUserName = null;
		String sMethodName = "removeSystem()";
		logger.setMethodStartLog(className, sMethodName);
		UMUtility oUMUtil = new UMUtility(logger, hmConstants);
		SAPUtil oSAPUtil = new SAPUtil(logger);
		Set sysAssigned = new HashSet();
		boolean isSystemUsed = false;
		sUserName = sUserID.toUpperCase();

		try {

			// Check whether any Role/Profile from same System is
			// assigned to User
			String[] strucureName = { PROFILES, ACTIVITYGROUPS };
			for (int i = 0; i < strucureName.length; i++) {
				JCoTable jcoTableprofile = oUMUtil.getRoleorProfile(sUserID,
						strucureName[i], jcoConnection, hmConstants);
				int iProfTableRowCount = jcoTableprofile.getNumRows();
				logger.debug(className, sMethodName, "# of existing profile "
						+ iProfTableRowCount);

				for (int iCount = 0; iCount < iProfTableRowCount; iCount++) {
					jcoTableprofile.setRow(iCount);
					// If the role/profile of this System is not assigned to the
					// user
					// then set 'isSystemUsed=true'
					if (jcoTableprofile.getString(SUBSYSTEM).equalsIgnoreCase(
							SystemName)) {
						isSystemUsed = true;
						logger
								.info(
										className,
										sMethodName,
										"Role/Profile of this System is already assigned to User:Cannot remove the system");
					}

					else
						sysAssigned.add(jcoTableprofile.getString(SUBSYSTEM));

				}
			}
			// If the system is not used by any of the role or profile assigned
			// then delete the system from System Tab of the User
			if (!isSystemUsed) {

				sBAPIName = hmConstants.get(BAPI_USER_SYSTEM_ASSIGN);
				JCoFunction jcoFunction = oSAPUtil.getJCOFunction(
						jcoConnection, sBAPIName);
				JCoParameterList jcoParameterList = jcoFunction
						.getImportParameterList();

				jcoParameterList.setValue(hmConstants.get(USERNAME), sUserName);

				JCoTable tableActGroup = jcoFunction.getTableParameterList()
						.getTable(SYSTEMS);
				Iterator sysIter = sysAssigned.iterator();
				int tableRowCount = 0;
				tableActGroup.appendRow();
				while (sysIter.hasNext()) {
					logger.info(className, sMethodName,
							"Adding already assigned value ");
					tableActGroup.setValue(tableRowCount, sysIter.next()
							.toString());
					tableRowCount++;

				}

				logger.info(className, sMethodName,
						"Executing BAPI for System Attributes");
				jcoFunction.execute(jcoConnection);

				hashStatusMessage = oUMUtil.getBAPIStatus(jcoFunction, "");

				if (hashStatusMessage.get(hmConstants.get(TYPE)).equals(
						hmConstants.get(S))) {
					logger.info(className, sMethodName,
							"System Attribute removed successfully");
				} else {
					logger.error(className, sMethodName, hashStatusMessage
							.get(hmConstants.get(MESSAGE)));
				}

			}

		} catch (JCoException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.error(className, sMethodName, exception.getMessageText());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
		}

	}
	// END:Code Modification for BUG 13475350

}

