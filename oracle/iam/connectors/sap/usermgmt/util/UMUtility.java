/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/usermgmt/util/UMUtility.java /main/12 2016/06/27 23:39:42 vsantosh Exp $ */
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
 ddkumar     01/14/09 - This class contains all the utility methods used in
 SAP User Management Connector
 ddkumar     	01/14/09 - Creation
 K S Santosh    01/19/11 - Bug 11070597 - Added logger to print BAPI Name & its parameter
 */
/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/usermgmt/util/UMUtility.java /main/12 2016/06/27 23:39:42 vsantosh Exp $
 *  @author  ddkumar
 *  @since   release specific (what release of product did this appear in)
 */
package oracle.iam.connectors.sap.usermgmt.util;

/**
 * Description:This class contains all the utility methods used in provisioning
 * and reconciliation for SAP User Management Connector
 */
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.sap.common.util.SAPUtil;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class UMUtility implements UMConstants {
	private String className = this.getClass().getName();
	ConnectorLogger logger = null;
	private HashMap<String, String> hmConstants;

	public UMUtility(ConnectorLogger logger, HashMap<String, String> hmConstants) {
		this.logger = logger;
		this.hmConstants = hmConstants;
	}

	/**
	 * Description :Gets the user field value from bean
	 * 
	 * @param attrMap
	 *            Contains attribute-mapped fields and their values in the
	 *            format: key=Structure Name, value=ArrayList, which
	 *            holds:SAPUMAttributeMapBean. For example:
	 *            [ADDRESS,[BEAN1,BEAN2]]
	 * @see oracle.iam.connectors.sap.usermgmt.util.SAPUMAttributeMapBean
	 * @param sStructureKey
	 *            Structure name in the BAPI. The BAPI field is part of this
	 *            structure. For example: ADDRESS
	 * @param sBapiKey
	 *            Field name of the value to be modified in the BAPI. For
	 *            example: TEL1_NUMBR
	 * @return String Returns the target system field value for the specified
	 *         BAPI key and BAPI Structure
	 * 
	 */
	public String getUserFieldValue(
			HashMap<String, ArrayList<SAPUMAttributeMapBean>> attrMap,
			String sStructureKey, String sBapiKey) {
		String sMethodName = "getUserFieldValue()";
		logger.setMethodStartLog(className, sMethodName);
		String sFieldValue = null;
		SAPUMAttributeMapBean sAPAttributeMapBean = getUserBean(attrMap,
				sBapiKey, sStructureKey);
		sFieldValue = sAPAttributeMapBean.getFieldValue();
		logger.setMethodFinishLog(className, sMethodName);
		return sFieldValue;
	}

	/**
	 * Description: Gets the bean containing the user field value from HashMap
	 * 
	 * @param attrMap
	 *            Contains attribute-mapped fields and their values in the
	 *            format key: Structure Name, value: ArrayList which
	 *            holds:SAPUMAttributeMapBean . e.g. [ADDRESS,[BEAN1,BEAN2]]
	 * @see oracle.iam.connectors.sap.usermgmt.util.SAPUMAttributeMapBean
	 * @param sBAPIStructure
	 *            Structure name in the BAPI. The BAPI field is part of the
	 *            structure. For example: ADDRESS
	 * @param sBAPIFieldName
	 *            Field name of the value to be modified in the BAPI. For
	 *            example: TEL1_NUMBR
	 * @return SAPUMAttributeMapBean Returns the target system field value for
	 *         the specified BAPI key and BAPI Structure
	 * 
	 */
	public SAPUMAttributeMapBean getUserBean(
			HashMap<String, ArrayList<SAPUMAttributeMapBean>> attrMap,
			String sBAPIFieldName, String sBAPIStructure) {
		String sMethodName = "getUserBean()";
		logger.setMethodStartLog(className, sMethodName);
		if (attrMap != null) {
			if (attrMap.containsKey(sBAPIStructure)) {
				ArrayList<SAPUMAttributeMapBean> dataList = attrMap
						.get(sBAPIStructure);
				int iSize = dataList.size();
				for (int index = 0; index < iSize; index++) {
					SAPUMAttributeMapBean sAPAttributeMapBean = dataList
							.get(index);
					if (sAPAttributeMapBean != null) {
						if ((sAPAttributeMapBean.getBapiFieldName() != null)
								&& sAPAttributeMapBean.getBapiFieldName()
										.equalsIgnoreCase(sBAPIFieldName)) {
							return sAPAttributeMapBean;
						}
					}
				}
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
		return null;
	}

	/**
	 * Description: Gets the status of the activity performed on the target
	 * system
	 * 
	 * @param function
	 *            Function that is used to get the structure for the type
	 * @param sType
	 *            The type i.e, "Export"
	 * @return HashMap Returns MESSAGE, TYPE, and Number from the return
	 *         structure
	 */

	public HashMap<String, String> getBAPIStatus(JCoFunction function,
			String sType) {
		HashMap<String, String> statusMap = new HashMap<String, String>();
		String sMethodName = "getBAPIStatus()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			if (function != null) {
				if (sType.equalsIgnoreCase(hmConstants.get(Export))) {
					JCoStructure returnStructure = function
							.getExportParameterList().getStructure(
									hmConstants.get(RETURN));
					statusMap.put(hmConstants.get(MESSAGE), returnStructure
							.getString(hmConstants.get(MESSAGE)));
					statusMap.put(hmConstants.get(TYPE), returnStructure
							.getString(hmConstants.get(TYPE)));
					statusMap.put(hmConstants.get(NUMBER), returnStructure
							.getString(hmConstants.get(NUMBER)));
				} else {
					JCoTable returnTable = function.getTableParameterList()
							.getTable(hmConstants.get(RETURN));
					if (returnTable.getNumRows() != 0) {
						for(int i=0;i < returnTable.getNumRows();i++) {
							returnTable.setRow(i);
							if(returnTable.getString(hmConstants.get(TYPE)).equals(hmConstants.get(S))) {
								statusMap.put(hmConstants.get(MESSAGE), returnTable
										.getString(hmConstants.get(MESSAGE)));
								statusMap.put(hmConstants.get(TYPE), returnTable
										.getString(hmConstants.get(TYPE)));
								statusMap.put(hmConstants.get(NUMBER), returnTable
										.getString(hmConstants.get(NUMBER)));
							} else {
								logger.info("BAPI return type: "+returnTable
										.getString(hmConstants.get(TYPE))+" number: "+returnTable
										.getString(hmConstants.get(NUMBER))+" message: "+returnTable
										.getString(hmConstants.get(MESSAGE)));
							}
						}
					}
				}
			} else {
				logger.error(className, sMethodName,
						"JCOFunction not initialised");
				throw new ConnectorException("JCOFunction not initialised");
			}
		} catch (ConnectorException ConnectorException) {
			logger.error(className, sMethodName, ConnectorException
					.getMessage());
			logger.setStackTrace(ConnectorException, className, sMethodName,
					ConnectorException.getMessage());
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
		}
		logger.setMethodFinishLog(className, sMethodName);
		return statusMap;
	}

	/**
	 * Description: Gets the Decode value from the specified lookup definition,
	 * which corresponds to check box values on the target system
	 * 
	 * @param sFieldName
	 *            Lookup definition containing the target attribute mapping. For
	 *            example: Check Indicator
	 * @param sFieldValue
	 *            Check box value. For example: 1
	 * @param hmCheckBoxMapping
	 *            HashMap containing lookup values with Code Key as Field Name
	 *            and Decode as target values for that field
	 * 
	 * @return String Returns value for check box field
	 * 
	 */
	public String getCheckBoxValueForSAP(String sFieldName, String sFieldValue,
			HashMap<String, String> hmCheckBoxMapping) {
		String sMethodName = "getCheckBoxValueForSAP";
		logger.setMethodStartLog(className, sMethodName);
		String sValue = null;
		String sLookupValue = null;
		int iStartIdx = 0;
		int iEndIdx = 0;
		if (hmCheckBoxMapping.size() > 0) {
			sLookupValue = (String) hmCheckBoxMapping.get(sFieldName);
			if (sFieldValue.equals(hmConstants.get(ONE))) {
				iStartIdx = 0;
				iEndIdx = 1;
			} else if (sFieldValue.equals(hmConstants.get(ZERO))){
				iStartIdx = 1;
				iEndIdx = 2;
			}else {
				logger.error(className, sMethodName, "Invalid checkbox input");
			}
			sValue = sLookupValue.substring(iStartIdx, iEndIdx);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sValue;
	}

	/**
	 * Description: Gets the BAPI name for multi-valued attributes based on its
	 * the structure name
	 * 
	 * @param sStructureName
	 *            Structure name in the BAPI. The BAPI field is part of the
	 *            structure. For example: ADDRESS
	 * @param isCUA
	 *            Specifies whether the target is SAP R/3 or SAP CUA
	 * @return String String containing the BAPI name
	 * 
	 */
	public String getAddMultiValueDataBAPIName(String sStructureName,
			String isCUA) {
		String sMethodName = "getAddMultiValueDataBAPIName";
		logger.setMethodStartLog(className, sMethodName);
		String sReturnBAPIName = hmConstants.get(BAPI_USER_CHANGE);
		if (isCUA.equalsIgnoreCase(hmConstants.get(YES))) {
			if (sStructureName
					.equalsIgnoreCase(hmConstants.get(ACTIVITYGROUPS))) {
				sReturnBAPIName = hmConstants
						.get(BAPI_USER_LOCACTGROUPS_ASSIGN);
			} else if (sStructureName.equalsIgnoreCase(hmConstants
					.get(PROFILES))) {
				sReturnBAPIName = hmConstants.get(BAPI_USER_LOCPROFILES_ASSIGN);
			}
		} else {
			if (sStructureName
					.equalsIgnoreCase(hmConstants.get(ACTIVITYGROUPS))) {
				sReturnBAPIName = hmConstants.get(BAPI_USER_ACTGROUPS_ASSIGN);
			} else if (sStructureName.equalsIgnoreCase(hmConstants
					.get(PROFILES))) {
				sReturnBAPIName = hmConstants.get(BAPI_USER_PROFILES_ASSIGN);
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sReturnBAPIName;
	}

	/**
	 * Description: Gets the list of user accounts to be reconciled to Oracle
	 * Identity Manager
	 * 
	 * @param sTableName
	 *            Table Name used for querying accounts. For example: USR02
	 * @param hmExclusionList
	 *            HashMap containing a list of excluded accounts for which
	 *            reconciliation event must not be created. This lookup
	 *            definition is populated using getLookupMap(LookupName).
	 * @param isFirstTimeRecon
	 *            Boolean value to determine if its full or incremental
	 *            reconciliation. The value can be true or false.
	 * @param iStartRecord
	 *            Start Record from which the table must be queried. For
	 *            example: 0
	 * @param iBatchSize
	 *            Batch Size for reconciling accounts. For example:0
	 * @param sExecutionTime
	 *            Time from which accounts modified must be reconciled. For
	 *            example: 20090803080305 (the format is yyyymmddHHmmss)
	 * @param jcoConnection
	 *            Holds the connection object reference
	 * @param isMODDA
	 *            Boolean value to determine if the MODDA or MODDATE field is
	 *            used to query incremental users. The value can be true or
	 *            false.
	 * @return HashMap 
	 * 			Returns HashMap having the account ID's to be reconciled
	 *          with key as the User ID name and decode as the account lock
	 *          status for the user
	 */
	public HashMap<String, String> getAccounts(String sTableName,
			HashMap<String, String> hmExclusionList, boolean isFirstTimeRecon,
			int iStartRecord, int iBatchSize, String sExecutionTime,
			JCoDestination jcoConnection, boolean isMODDA) {
		HashMap<String, String> hmAccounts = new HashMap<String, String>();
		String sMethodName = "getAccountsModified()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			if (jcoConnection != null) {
				SAPUtil oSAPUtil = new SAPUtil(logger);
				JCoFunction jcoFunction = oSAPUtil
						.getJCOFunction(jcoConnection, (String) hmConstants
								.get(RFC_READ_TABLE));
				JCoParameterList parameterList = jcoFunction
						.getImportParameterList();
				logger.debug(className, sMethodName, "Setting " + hmConstants.get(QUERY_TABLE) +" to "+sTableName);
				parameterList.setValue((String) hmConstants.get(QUERY_TABLE),
						sTableName);
				logger.debug(className, sMethodName, "Setting " + hmConstants.get(ROWSKIPS) +" to "+iStartRecord);
				parameterList.setValue((String) hmConstants.get(ROWSKIPS),
						iStartRecord);
				logger.debug(className, sMethodName, "Setting " + hmConstants.get(ROWCOUNT) +" to "+iBatchSize);
				parameterList.setValue((String) hmConstants.get(ROWCOUNT),
						iBatchSize);

				if (!isFirstTimeRecon) {
					String sDate = sExecutionTime.substring(0, 8);
					String sTime = sExecutionTime.substring(8);
					JCoTable returnOption1 = jcoFunction
							.getTableParameterList().getTable(
									(String) hmConstants.get(OPTIONS));
					returnOption1.appendRow();
					// Start :: Bug 18128991- SAP UM RECONCILIATION FINDS MODIFIED USERS ONCE IN THREE TIMES
					// New Sample Query Format: MODDA GT 'YYYYMMDD' OR MODDA EQ 'YYYYMMDD' AND MODTI GT 'HHMMSS'
					// Sample Query: MODDA GT '20140117' OR MODDA EQ '20140117' AND MODTI GT '130000'
					if (isMODDA) {
						returnOption1.setValue(hmConstants.get(TEXT),
								"MODDA GT '" + sDate + "' OR MODDA EQ '"
										+ sDate + "' AND MODTI GT '" + sTime
										+ "'");
						logger.debug(className, sMethodName, "QUERY_TABLE-"
								+ sTableName + "  TEXT-" + "MODDA GT '" + sDate
								+ "' OR MODDA EQ '" + sDate
								+ "' AND MODTI GT '" + sTime + "'");

					} else {
						returnOption1.setValue(hmConstants.get(TEXT),
								"MODDATE GT '" + sDate + "' OR MODDATE EQ '"
										+ sDate + "'AND MODTIME GT '" + sTime
										+ "'");
						logger.debug(className, sMethodName, "QUERY_TABLE-"
								+ sTableName + "  TEXT-" + "MODDATE GT '"
								+ sDate + "' OR MODDATE EQ '" + sDate
								+ "'AND MODTIME GT '" + sTime + "'");
					}// End :: Bug 18128991- SAP UM RECONCILIATION FINDS MODIFIED USERS ONCE IN THREE TIMES


				}
				JCoTable returnOption = jcoFunction.getTableParameterList()
						.getTable((String) hmConstants.get(FIELDS));
				returnOption.appendRow();
				logger.debug(className, sMethodName, "Setting " + hmConstants.get(FIELDNAME) +" to "+hmConstants.get(BNAME));
				returnOption.setValue((String) hmConstants.get(FIELDNAME),
						(String) hmConstants.get(BNAME));
				if ((sTableName.equalsIgnoreCase((String) hmConstants
						.get(USH02_TABLE)))
						|| (sTableName.equalsIgnoreCase((String) hmConstants
								.get(USR02_TABLE)))) {
					returnOption.appendRow();
					logger.debug(className, sMethodName, "Setting JCO Table Parameter if Table Name is USR02_TABLE - "+hmConstants.get(FIELDNAME) +" to "+hmConstants.get(UFLAG));
					returnOption.setValue((String) hmConstants.get(FIELDNAME),
							(String) hmConstants.get(UFLAG));
				}
				jcoFunction.execute(jcoConnection);
				JCoTable jcoTable = jcoFunction.getTableParameterList()
						.getTable((String) hmConstants.get(FIELDS));
				JCoTable jcoTable1 = jcoFunction.getTableParameterList()
						.getTable((String) hmConstants.get(DATA));
				int icodeOffSet = 0;
				int icodeLength = 0;
				int idescOffSet = 0;
				int idescLength = 0;
				int iReturnValueTable = jcoTable.getNumRows();
				int iReturnValuesDescTable = jcoTable1.getNumRows();

				if (iReturnValueTable != 0) {
					for (int i = 0; i < iReturnValueTable; i++) {
						jcoTable.setRow(i);
						String sField = jcoTable.getString((String) hmConstants
								.get(FIELDNAME));
						if (sField.equals((String) hmConstants.get(BNAME))) {
							icodeOffSet = Integer
									.parseInt(jcoTable
											.getString((String) hmConstants
													.get(OFFSET)));
							icodeLength = Integer
									.parseInt(jcoTable
											.getString((String) hmConstants
													.get(LENGTH)));
							icodeLength = icodeLength + icodeOffSet;
						}

						if ((sTableName.equalsIgnoreCase((String) hmConstants
								.get(USH02_TABLE)))
								|| (sTableName
										.equalsIgnoreCase((String) hmConstants
												.get(USR02_TABLE)))) {
							idescOffSet = Integer
									.parseInt(jcoTable
											.getString((String) hmConstants
													.get(OFFSET)));
							idescLength = Integer
									.parseInt(jcoTable
											.getString((String) hmConstants
													.get(LENGTH)));
							idescLength = idescLength + idescOffSet;
						}

					}
				}
				logger.debug(className, sMethodName, "JCO Table Field Name  - "+jcoTable.getString((String) hmConstants
						.get(FIELDNAME)));
				logger.debug(className, sMethodName, "icodeOffSet  - "+icodeOffSet);
				logger.debug(className, sMethodName, "icodeLength  - "+icodeLength);
				logger.debug(className, sMethodName, "idescOffSet  - "+idescOffSet);
				logger.debug(className, sMethodName, "idescLength  - "+idescLength);
				
				String sLockStatus;
				String sUserID;
				if (iReturnValuesDescTable != 0) {
					for (int i = 0; i < iReturnValuesDescTable; i++) {
						jcoTable1.setRow(i);
						String sMessage = jcoTable1.getString(hmConstants
								.get(WA));
						if ((sTableName.equalsIgnoreCase((String) hmConstants
								.get(USH02_TABLE)))
								|| (sTableName
										.equalsIgnoreCase((String) hmConstants
												.get(USR02_TABLE)))) {
							sUserID = sMessage.substring(icodeOffSet,
									icodeLength).trim();
							sLockStatus = sMessage.substring(idescOffSet)
									.trim();
						} else {
							sUserID = sMessage.substring(icodeOffSet).trim();
							sLockStatus = (String) hmConstants.get(NONE);
						}
						if (!hmExclusionList.containsKey(sUserID)) {
							hmAccounts.put(sUserID, sLockStatus);
						} else {
							logger.debug(className, sMethodName, sUserID
									+ " is present in exclusion list");
						}
					}
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
		logger.debug(className, sMethodName, "No Of Account Id to be reconcile -" +hmAccounts.size());
		logger.setMethodFinishLog(className, sMethodName);
		return hmAccounts;
	}

	/**
	 * Description: Runs the custom query specified for each user record.
	 * Returns true if the query is valid, otherwise, it returns false..
	 * 
	 * @param hmReconDetails
	 *            Hashmap containing details of user records to be reconciled to
	 *            Oracle Identity Manager. This contains results of parent data
	 *            fields reconciled from the target system.
	 * 
	 * @param sQuery
	 *            Result Set of the user for getting values from Oracle Identity
	 *            Manager. For example: First Name=John & Last Name=Doe
	 * @return boolean
	 * 			  Returns true if the query condition is met, otherwise, returns
	 *            false
	 * @throws ConnectorException
	 * 
	 */
	public boolean executeCustomQuery(HashMap<String, String> hmReconDetails,
			String sQuery) throws ConnectorException {
		boolean isValid = false;
		String sMethod = "executeCustomQuery()";
		try {
			logger.setMethodStartLog(className, sMethod);
			int iNoOfOR;
			int iNoOfAnd;
			String sKey;
			String sValue;
			// exp : regular expression variable for spliting the query
			// according to "|"
			String sArrORExp[] = sQuery.split(OR_SPLIT_REGEX);
			iNoOfOR = sArrORExp.length - 1;
			logger.debug(className, sMethod, "Number Of 'OR' Operators "
					+ iNoOfOR);
			for (int i = 0; i <= iNoOfOR; i++) {
				// exp : regular expression variable for splitting the query
				// according
				// to "&"
				String sArrANDExp[] = sArrORExp[i].split(AND_SPLIT_REGEX);
				iNoOfAnd = sArrANDExp.length - 1;
				for (int j = 0; j <= iNoOfAnd; j++) {
					logger.debug(className, sMethod, "Expression is  "
							+ sArrANDExp[j]);
					int iEquals = 0;
					// Get the key and value by checking first index of '='
					// Throw exception if query does not have '=' operator b/w
					// key and
					// value
					iEquals = sArrANDExp[j].indexOf('=');

					sKey = sArrANDExp[j].substring(0, iEquals).trim();
					sValue = sArrANDExp[j].substring(iEquals + 1).trim();
					/*
					 * Check if the query condition gets satisfied.If
					 * satisfied,then continue and check if any more and
					 * condition need to be validated. If all AND conditions are
					 * satisfied,then return true If query condition fails for
					 * AND,then check if any more OR conditions are to be
					 * validated. If all query condition for OR fails,return
					 * false
					 */
					if (((String) hmReconDetails.get(sKey))
							.equalsIgnoreCase(sValue)) {
						isValid = true;
					} else {
						isValid = false;
						iNoOfAnd = -1;
					}
				}
				if (isValid) {
					iNoOfOR = -1;
				}
			}
			if (isValid) {
				logger
						.info(className, sMethod,
								"Custom Ouery Condition is met");
			} else {
				logger
						.info(className, sMethod,
								"Custom Ouery Condition not met, hence not creating recon event");
			}
		} catch (Exception e) {
			isValid = false;
			logger.error(className, sMethod,
					"Exception occured during executeCustomQuery."
							+ "Please check if the query is entered properly");
		}
		logger.setMethodFinishLog(className, sMethod);
		return isValid;
	}

	/**
	 * Description: Gets the role and profile information for the SAP CUA target
	 * system
	 * 
	 * @param sUserID
	 *            Account ID for which Role and Profile information is required.
	 *            For example: John.Doe
	 * @param sStructure
	 *            Structure name of role or profile. For example:ACTIVITYGROUPS
	 * @param jcoConnection
	 *            Connection reference
	 * @param hmConstants
	 *            HashMap containing constant lookup fields
	 * @return JCoTable
	 * 			   Returns JCOTable having the Role or Profile information	
	 */
	public JCoTable getRoleorProfile(String sUserID, String sStructure,
			JCoDestination jcoConnection, HashMap hmConstants) {
		String sMethodName = "getRoleorProfile";
		logger.setMethodStartLog(className, sMethodName);
		JCoTable multiValuesTable = null;
		try {
			if (jcoConnection != null) {
				SAPUtil oSAPUtil = new SAPUtil(logger);
				String sBAPIName;
				if (sStructure.equalsIgnoreCase(ACTIVITYGROUPS)) {
					sBAPIName = (String) hmConstants
							.get(BAPI_USER_LOCACTGROUPS_READ);
				} else {
					sBAPIName = (String) hmConstants
							.get(BAPI_USER_LOCPROFILES_READ);
				}
				JCoFunction jcoRoleFunction = oSAPUtil.getJCOFunction(
						jcoConnection, sBAPIName);
				JCoParameterList input = jcoRoleFunction
						.getImportParameterList();
				input.setValue((String) hmConstants.get(USERNAME), sUserID);
				jcoRoleFunction.execute(jcoConnection);
				multiValuesTable = jcoRoleFunction.getTableParameterList()
						.getTable(sStructure);
			} else {
				logger.error(className, sMethodName,
						"jcoConnection not initialised");
				throw new ConnectorException("jcoConnection not initialised");
			}
		} catch (Exception e) {
			throw new ConnectorException(e.getMessage());
		}
		logger.setMethodFinishLog(className, sMethodName);
		return multiValuesTable;
	}

	/**
	 * Description: Gets the formatted value for the lookup fields after
	 * removing the '~' delimeter
	 * 
	 * @param fieldValue
	 *            Contains the field value to be formatted. For example: 1~XYZ
	 * @return String
	 * 			 Returns string containing the formatted value. For example: XYZ
	 */
	public String getFormattedAttributeValue(String fieldValue) {
		String sMethodName = "getFormattedAttributeValue()";
		logger.setMethodStartLog(className, sMethodName);
		String sFormattedField = null;
		try {
			int ch = '~';
			sFormattedField = fieldValue
					.substring(fieldValue.lastIndexOf(ch) + 1);
		} catch (Exception exception) {
			logger.error(className, sMethodName, exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sFormattedField;
	}

	/**
	 * Description: Implements attribute mapping for check box fields
	 * 
	 * @param sFieldName
	 *            Name of the field name defined in the resource object. For
	 *            example: FieldX
	 * @param sFieldValue
	 *            Field Value fetched during reconciliation for the specified
	 *            field. For example: A
	 * @param hmCheckBoxMapping
	 *            HashMap containing the field name and check box field values.
	 *            For example: FieldX=AB
	 * 
	 * @return String 
	 * 			Returns 1 if the first value in the Decode column of the
	 *         lookup definition matches the field value fetched during
	 *         reconciliation, otherwise, returns 0
	 */
	public String getCheckBoxValueForOIM(String sFieldName, String sFieldValue,
			HashMap hmCheckBoxMapping) {
		String sValue = null;

		if (hmCheckBoxMapping.containsKey(sFieldName)) {
			String sLookupValue = (String) hmCheckBoxMapping.get(sFieldName);
			if (sFieldValue.equals(sLookupValue.substring(0, 1))) {
				sValue = hmConstants.get(ONE);
			} else {
				sValue = hmConstants.get(ZERO);
			}
		}
		return sValue;
	}

	/**
	 * Description: Check if the user is present on the target system
	 * 
	 * @param function
	 *            Holds the JCoFunction reference
	 * @return boolean 
	 * 			Returns true if the user exists, otherwise, returns false
	 */
	public boolean findUser(JCoFunction function) throws ConnectorException {
		boolean isUserExists = false;
		String sMessage = null;
		String methodName = "findUser";
		JCoTable table = function.getTableParameterList().getTable(
				hmConstants.get(RETURN));
		try{
		 sMessage = table.getString(hmConstants.get(MESSAGE));
			if (!sMessage.contains(hmConstants.get(DOES_NOT_EXIST))) {
				isUserExists = true;
			}
		}catch(IllegalStateException e){
			isUserExists = true;
			logger.debug(className, methodName, "Older system is being used where table returns no value");
		}catch (Exception e) {
			logger.error(className, methodName, e.getMessage());
		}
		return isUserExists;
	}
	
	/**
	 * Description : Creates a method name to be invoked as a String and 
	 * validates if the method exists in the class.
	 * @param inputClass
	 * 			Class containing the required method.
	 * 			e.g. oracle.iam.connectors.sap.grc.ws.submitreq.RoleData
	 * @param input
	 * 			Variable whose setter method needs to be created
	 * 			e.g. roleId
	 * @param prefix
	 * 			Prefix 'set' used to create the method
	 * @return
	 */
	public String getMethodName(Class<?> inputClass, String input, String prefix) {
		String sMethodName = "getMethodName";
		logger.setMethodStartLog(className, sMethodName);
		String retval = "";
		input = prefix + input;
		Method[] methods = inputClass.getDeclaredMethods();
		Method method;
		for (int i = 0; i < methods.length; i++) {
			method = methods[i];
			if (method.getName().compareToIgnoreCase(input) == 0) {
				retval = method.getName();
				break;
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
		return retval;
	}

}
