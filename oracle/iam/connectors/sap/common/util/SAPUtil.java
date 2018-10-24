/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/common/util/SAPUtil.java /main/28 2013/06/20 10:41:10 aarepura Exp $ */
/* Copyright (c) 2009, 2013, Oracle and/or its affiliates. 
All rights reserved. */
/*
 DESCRIPTION
 <short description of component this file declares/defines>

 PRIVATE CLASSES
 <list of private classes defined - with one-line descriptions>

 NOTES
 <other useful comments, qualifications, etc.>

 MODIFIED    (MM/DD/YY)
 ddkumar     01/14/09 - This class contains all the utility methods used in
 SAP Connector
 ddkumar     01/14/09 - Creation
 */
/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/common/util/SAPUtil.java /main/28 2013/06/20 10:41:10 aarepura Exp $
 *  @author  ddkumar
 *  @since   release specific (what release of product did this appear in)
 */
package oracle.iam.connectors.sap.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.Constants;
import oracle.iam.connectors.common.dao.OIMUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.sap.common.connection.SAPConstants;
import oracle.iam.connectors.sap.hrms.util.SAPAttributeMapBean;
import oracle.iam.connectors.sap.usermgmt.util.SAPUMAttributeMapBean;
import Thor.API.tcResultSet;
import Thor.API.Exceptions.tcAPIException;
import Thor.API.Exceptions.tcColumnNotFoundException;
import Thor.API.Operations.tcLookupOperationsIntf;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoRepository;

/**
 * Description: Contains the utility methods used by the SAP connector
 */

/*
 *	<br>Modification History:</br>
 *  S.No.                 Date                              Bug fix no.
 *  1.                  31 May 2011                     9407605  - SAP TRUSTED EMPLOYEE RECON FAILS IF USER IS MANAGER OF PARENT ORG  
 */
public class SAPUtil implements SAPConstants, Constants {

	private String className = this.getClass().getName();
	ConnectorLogger logger = null;
	private StringUtil stringUtil = new StringUtil();;

	public SAPUtil(ConnectorLogger logger) {
		this.logger = logger;
	}

	/**
	 * Description : Gets the JCoFunction after executing a BAPI
	 * 
	 * @param mConnection
	 *            Connection reference holding connection details
	 * @param sBAPIName
	 *            BAPI to be executed. For example: BAPI_USER_GETDETAILS
	 * @return JCoFunction Returns JCoFunction for the associated BAPI Name and
	 *         JCoDestination having the connection properties
	 * 
	 * @throws ConnectorException
	 * 
	 */
	public JCoFunction getJCOFunction(JCoDestination mConnection,
			String sBAPIName) throws ConnectorException {
		JCoFunction function = null;
		String sMethod = "getJCOFunction()";
		// Connect to repository and return the function according to the
		// BAPI name passed
		try {
			logger.setMethodStartLog(className, sMethod);
			JCoRepository mRepository = mConnection.getRepository();
			JCoFunctionTemplate createFunctionTemplate;
			logger.debug(className, sMethod, "Executing BAPI: " + sBAPIName);
			createFunctionTemplate = mRepository.getFunctionTemplate(sBAPIName);
			function = createFunctionTemplate.getFunction();
		} catch (JCoException e) {
			logger.error(className, sMethod, e.getMessage());
			if (e.getKey().equalsIgnoreCase("RFC_ERROR_LOGON_FAILURE")
					|| e.getKey().equalsIgnoreCase("RFC_ERROR_COMMUNICATION")) {
				throw new ConnectorException("Connection error occured", e);
			}
			throw new ConnectorException(e.getMessage());
		} catch (Exception e) {
			logger.error(className, sMethod, e.getMessage());
			throw new ConnectorException(e.getMessage());
		}
		logger.setMethodFinishLog(className, sMethod);
		return function;
	}

	/**
	 * Description: Returns a HashTable of attribute mapping values defined in
	 * the attribute map lookup containing the code key as the segment name and
	 * decode as ArrayList of AttributeMapBean with each AttributeMapBean
	 * containing OIMFieldName, SAP Segment Name, SAP Attribute Name, Start
	 * position, and end position.This is used for HRMS attribute mapping.
	 * 
	 * @param sLookupName
	 *            Name of the lookup definition containing the target attribute
	 *            mapping fields
	 * @param lookIntf
	 *            Lookup interface to get all lookup values mentioned in the
	 *            lookup definition
	 * @return HashTable Returns the HashTable with the Code Key value as
	 *         Segment Name and Decode value as ArrayList of AttributeMapBean
	 *         with each AttributeMapBean containing OIMFieldName, SAP Segment
	 *         Name, SAP Attribute Name, Start position, and end position for
	 *         attributes specified in the lookup definition
	 * 
	 * @throws ConnectorException
	 * 
	 */
	public Hashtable populateAttributeMapDetails(String sLookupName,
			tcLookupOperationsIntf lookIntf) throws ConnectorException {
		Hashtable<String, Object> htDataMap = new Hashtable<String, Object>();
		String sMethodName = "populateAttributeMapDetails()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			ArrayList attrMapList = null;
			tcResultSet rsLookupValues = lookIntf.getLookupValues(sLookupName);
			int iLookupRowCount = rsLookupValues.getRowCount();
			String sCode = null;
			String sDecode = null;
			/*
			 * Loop through the attribute map lookup and get the code and decode
			 * values. Place the decode values in the Bean after splitting it
			 * and return Hashtable containing segment name as key and all its
			 * attributes as value
			 */
			for (int i = 0; i < iLookupRowCount; i++) {
				rsLookupValues.goToRow(i);
				sCode = rsLookupValues.getStringValue(LOOKUP_CODE);
				sDecode = rsLookupValues.getStringValue(LOOKUP_DECODE);

				SAPAttributeMapBean oAttributeMapBean = new SAPAttributeMapBean();

				if (!stringUtil.isEmpty(sDecode)) {
					String[] keyArr = sDecode.split(";");
					String sSegmentName = keyArr[0];
					if ((htDataMap != null) && (sSegmentName != null)
							&& htDataMap.containsKey(sSegmentName)) {
						attrMapList = (ArrayList) htDataMap.get(sSegmentName);
					} else {
						attrMapList = new ArrayList<Object>();
					}

					oAttributeMapBean.setSOIMFieldName(sCode);
					oAttributeMapBean.setSSegmentName(sSegmentName);
					oAttributeMapBean.setSSubType(keyArr[1]);
					oAttributeMapBean.setSSAPAttributeName(keyArr[2]);
					oAttributeMapBean.setSStartPosition(keyArr[3]);
					oAttributeMapBean.setSEndPosition(keyArr[4]);
					oAttributeMapBean.setSFieldType(keyArr[5]);
					attrMapList.add(oAttributeMapBean);
					htDataMap.put(sSegmentName, attrMapList);
				}
			}
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return htDataMap;
	}

	/**
	 * Description: Populates the HashMap of the attribute map lookup
	 * definitions used for user Management reconciliation.Returns the HashMap
	 * with Code Key value as BAPI Structure Name and Decode value as ArrayList
	 * of AttributeMapBean with each AttributeMapBean containing OIMFieldName,
	 * SAP Field Name, SAP Structure, and Child Table Name for attributes
	 * specified in the lookup definition
	 * 
	 * @param sAttributeMapLookupName
	 *            Name of the lookup definition containing target attribute
	 *            mapping fields
	 * @param lookIntf
	 *            Lookup interface to get all lookup values mentioned in the
	 *            lookup definition
	 * @param isUM
	 *            Boolean field that specifies whether the target system is SAP
	 *            R/3 or SAP CUA
	 * @return HashMap
	 * 		   Returns the HashMap with Code Key value as BAPI Structure Name
	 *         and Decode value as ArrayList of AttributeMapBean with each
	 *         AttributeMapBean containing OIMFieldName, SAP Field Name, SAP
	 *         Structure, and Child Table Name for attributes specified in the
	 *         lookup definition
	 * 
	 * @throws ConnectorException
	 * 
	 */
	public HashMap initializeTargetReconAttrMap(String sAttributeMapLookupName,
			tcLookupOperationsIntf lookIntf, boolean isUM)
			throws ConnectorException {
		StringUtil oStringUtil = new StringUtil();
		String sStructure = null;
		HashMap dataMap = new HashMap();
		ArrayList userDataList = null;
		String sCode = null;
		String sDecode = null;

		try {
			tcResultSet lookupRS = lookIntf
					.getLookupValues(sAttributeMapLookupName);
			int iLookupRowCount = lookupRS.getRowCount();

			for (int i = 0; i < iLookupRowCount; i++) {
				lookupRS.goToRow(i);
				sCode = lookupRS.getStringValue(LOOKUP_CODE);
				sDecode = lookupRS.getStringValue(LOOKUP_DECODE);

				SAPUMAttributeMapBean oAttributeMapBean = new SAPUMAttributeMapBean();

				if (!oStringUtil.isEmpty(sDecode)) {
					String[] keyArr = sDecode.split(";");
					int keyArrlength = keyArr.length;

					// Get Structure from the decode value
					sStructure = keyArr[2];
					if (sStructure.equalsIgnoreCase("UCLASS|UCLASSSYS")) {
						if (isUM) {
							sStructure = sStructure.substring(0, sStructure
									.indexOf('|'));
						} else {
							sStructure = sStructure.substring(sStructure
									.indexOf('|') + 1);
						}
					}
					// If dataMap contains a key with Structure
					if ((dataMap != null) && (sStructure != null)
							&& dataMap.containsKey(sStructure)) {
						userDataList = (ArrayList) dataMap.get(sStructure);
					} else {
						userDataList = new ArrayList();
					}

					oAttributeMapBean.setOIMfieldName(sCode);
					oAttributeMapBean.setFieldType(keyArr[0]);
					if (keyArr[1].equalsIgnoreCase("PROFILE|BAPIPROF")) {
						if (isUM) {
							keyArr[1] = keyArr[1].substring(keyArr[1]
									.indexOf('|') + 1);
						} else {
							keyArr[1] = keyArr[1].substring(0, keyArr[1]
									.indexOf('|'));
						}
					}

					oAttributeMapBean.setBapiFieldName(keyArr[1]);
					oAttributeMapBean.setBapiStructure(sStructure);

					if (keyArrlength > 3) {
						oAttributeMapBean.setChildTableName(keyArr[3]);
					}

					userDataList.add(oAttributeMapBean);
					dataMap.put(sStructure, userDataList);
				}
			}
		} catch (Exception e) {
			throw new ConnectorException(
					"Error occured during initializeTargetReconFieldMap");
		}

		return dataMap;
	}

	/**
	 * Description: Validates whether or not the custom query entered in the
	 * scheduled task is valid
	 * 
	 * @param sQuery
	 *            Contains the custom query as entered in scheduled tasks. For
	 *            example: First Name=John & Last Name=Doe
	 * @param hmValidateQuery
	 *            HashMap containing fields that can be used in the custom query
	 * 
	 * @throws ConnectorException
	 * 
	 */
	public void validateQuery(String sQuery, HashMap hmValidateQuery)
			throws ConnectorException {
		String sMethod = "validateQuery()";
		try {
			logger.setMethodStartLog(className, sMethod);
			int iNoOfOR;
			int iNoOfAnd;
			String sKey;
			// exp : regular expression variable for splitting the query
			// according to
			// "|"
			String sArrORExp[] = sQuery.split(OR_SPLIT_REGEX);
			iNoOfOR = sArrORExp.length - 1;
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
					if (sArrANDExp[j].indexOf('=') != -1) {
						iEquals = sArrANDExp[j].indexOf('=');
					} else {
						logger.error(className, sMethod,
								"No '=' operator present in the "
										+ sArrANDExp[j] + " expression");
						throw new Exception(
								"Query specified in the task scheduler is not a valid one");
					}
					sKey = sArrANDExp[j].substring(0, iEquals).trim();

					if (!hmValidateQuery.containsKey(sKey)) {
						logger
								.error(
										className,
										sMethod,
										"Query specified in the task scheduler is not a valid one.Please refer doc for more details");
						throw new Exception(
								"Query specified in the task scheduler is not a valid one.Please refer doc for more details");
					}
				}
			}
		} catch (Exception e) {
			logger.error(className, sMethod, e.getMessage());
			throw new ConnectorException(e);
		}
		logger.setMethodFinishLog(className, sMethod);
	}

	/**
	 * Description: Populates the HashMap of the custom attribute map lookups
	 * used for reconciliation.Returns the HashMap with the Code Key value as
	 * the BAPI Name and Decode value as ArrayList of AttributeMapBean with each
	 * AttributeMapBean containing OIMFieldName, SAP Field Name, SAP Structure,
	 * and User ID Field for attributes specified in the lookup definition
	 * 
	 * 
	 * @param sAttributeMapLookupName
	 *            Name of the lookup definition containing the custom target
	 *            attribute mapping fields
	 * @param lookIntf
	 *            Lookup Interface to get all lookup values mentioned in the
	 *            lookup definition
	 * 
	 * @return HashMap Returns the HashMap with the Code Key value as the BAPI
	 *         Name and Decode value as ArrayList of AttributeMapBean with each
	 *         AttributeMapBean containing OIMFieldName, SAP Field Name, SAP
	 *         Structure, and User ID Field for attributes specified in the
	 *         lookup definition
	 * 
	 * @throws ConnectorException
	 * 
	 */
	public HashMap initializeCustomAttrMap(String sAttributeMapLookupName,
			tcLookupOperationsIntf lookIntf) throws ConnectorException {
		StringUtil oStringUtil = new StringUtil();
		HashMap dataMap = new HashMap();
		HashMap hmBAPI = new HashMap();
		ArrayList userDataList = null;
		String sBAPIName = null;
		String sCode = null;
		String sDecode = null;

		try {
			tcResultSet lookupRS = lookIntf
					.getLookupValues(sAttributeMapLookupName);
			int iLookupRowCount = lookupRS.getRowCount();

			for (int i = 0; i < iLookupRowCount; i++) {
				lookupRS.goToRow(i);
				sCode = lookupRS.getStringValue(LOOKUP_CODE);
				sDecode = lookupRS.getStringValue(LOOKUP_DECODE);

				SAPUMAttributeMapBean oAttributeMapBean = new SAPUMAttributeMapBean();
				if (!oStringUtil.isEmpty(sDecode)) {
					String[] keyArr = sDecode.split(";");
					sBAPIName = keyArr[0];
					// If dataMap contains a key with Structure
					if ((dataMap != null) && (sBAPIName != null)
							&& dataMap.containsKey(sBAPIName)) {
						userDataList = (ArrayList) dataMap.get(sBAPIName);
					} else {
						userDataList = new ArrayList();
					}

					oAttributeMapBean.setOIMfieldName(sCode);
					oAttributeMapBean.setFieldType(keyArr[1]);
					oAttributeMapBean.setBapiStructure(keyArr[2]);
					oAttributeMapBean.setBapiFieldName(keyArr[3]);
					oAttributeMapBean.setUserIDKeyField(keyArr[4]);
					userDataList.add(oAttributeMapBean);
					dataMap.put(sBAPIName, userDataList);
				}
			}
		} catch (Exception e) {
			throw new ConnectorException(
					"Error occured during initializeTargetReconFieldMap");
		}

		return dataMap;
	}
	
	/**
	 * Description: Populates the HashMap of the custom child attribute map lookups
	 * used for reconciliation.Returns the HashMap with the Code Key value as
	 * the Table Name and Decode value as ArrayList of AttributeMapBean with each
	 * AttributeMapBean containing OIMFieldName, SAP Field Name, SAP Structure,
	 * and User ID,Child Table Name Field for attributes specified in the lookup definition
	 * 
	 * 
	 * @param sAttributeMapLookupName
	 *            Name of the lookup definition containing the custom child target
	 *            attribute mapping fields
	 *            
	 * @param lookIntf
	 *            Lookup Interface to get all lookup values mentioned in the
	 *            lookup definition
	 * 
	 * @return HashMap Returns the HashMap with the Code Key value as the Table
	 *         Name and Decode value as ArrayList of AttributeMapBean with each
	 *         AttributeMapBean containing OIMFieldName, SAP Field Name, SAP
	 *         Structure, and User ID,Child Table Name Field for attributes specified in the
	 *         lookup definition
	 * 
	 * @throws ConnectorException
	 * 
	 */
	public HashMap initializeCustomChildAttrMap(String sAttributeMapLookupName,
			tcLookupOperationsIntf lookIntf) throws ConnectorException {
		StringUtil oStringUtil = new StringUtil();
		HashMap dataMap = new HashMap();
		HashMap hmBAPI = new HashMap();
		ArrayList userDataList = null;
		String sStructure = null;
		String sCode = null;
		String sDecode = null;

		try {
			tcResultSet lookupRS = lookIntf
					.getLookupValues(sAttributeMapLookupName);
			int iLookupRowCount = lookupRS.getRowCount();

			for (int i = 0; i < iLookupRowCount; i++) {
				lookupRS.goToRow(i);
				sCode = lookupRS.getStringValue(LOOKUP_CODE);
				sDecode = lookupRS.getStringValue(LOOKUP_DECODE);

				SAPUMAttributeMapBean oAttributeMapBean = new SAPUMAttributeMapBean();
				if (!oStringUtil.isEmpty(sDecode)) {
					String[] keyArr = sDecode.split(";");
					sStructure = keyArr[2];
					// If dataMap contains a key with Structure
					if ((dataMap != null) && (sStructure != null)
							&& dataMap.containsKey(sStructure)) {
						userDataList = (ArrayList) dataMap.get(sStructure);
					} else {
						userDataList = new ArrayList();
					}

					oAttributeMapBean.setOIMfieldName(sCode);
					oAttributeMapBean.setSBAPINAME(keyArr[0]);
					oAttributeMapBean.setFieldType(keyArr[1]);
					oAttributeMapBean.setBapiStructure(keyArr[2]);
					oAttributeMapBean.setBapiFieldName(keyArr[3]);
					oAttributeMapBean.setUserIDKeyField(keyArr[4]);
					oAttributeMapBean.setChildTableName(keyArr[5]);
					userDataList.add(oAttributeMapBean);
					dataMap.put(sStructure, userDataList);
				}
			}
		} catch (Exception e) {
			throw new ConnectorException(
					"Error occured during initializeTargetReconFieldMap");
		}

		return dataMap;
	}

	/**
	 * Description: Processes user attributes to create a reconciliation event
	 * if the custom reconciliation query and user type query are valid
	 * 
	 * @param sOrgName
	 *            Name of the organization for which Manager information must be
	 *            retrieved
	 * @param hmConstants
	 *            HashMap containing configuration details from the
	 *            Lookup.SAP.HRMS.Constants lookup definition
	 * @param sSAPUserID
	 *            Personnel Number of the reconciled SAP record
	 * 
	 * @param oUtil
	 *            Util that initializes all reconciliation interfaces
	 * 
	 * @return String Returns the Manager ID for the associated organization
	 * 
	 */
	public String getManagerIDFromOrg(String sOrgName, HashMap hmConstants,
			String sSAPUserID, OIMUtil oUtil, HashMap hmOrgHierarchy, HashMap hmManagerHierarchy) {
		String sMethodName = "getManagerIDFromOrg()";
		logger.setMethodStartLog(className, sMethodName);
		logger.debug(className, sMethodName, "sOrgName :" + sOrgName
				+ " sSAPUserID :" + sSAPUserID);
		String sUDFPersonnelNo = (String) hmConstants.get(PERSONNEL_NOS_UDF);

		String sManagerName = (String) hmManagerHierarchy.get(sOrgName);
		logger.debug(className, sMethodName, "Manager Name " + sManagerName);
		/*
		 * Get manager name from hmOrgHierarchy by passing org name got from
		 * IDoc. If the manager is NOT same as Personnel Number,then manager
		 * name returned is considered as Manger. If manager is same as
		 * employee, then from hmOrgHierarchy, parent org should be taken and
		 * manager of that parent org taken from hmOrgHierarchy is considered as
		 * manager.
		 */
						
		//Start :: BUG 9407605
		String sParentOrgName = sOrgName;
		while (stringUtil.isEmpty(sManagerName)
				|| sManagerName.equalsIgnoreCase(sSAPUserID)
				|| sManagerName
						.equalsIgnoreCase((String) hmConstants.get(NONE))) {

			if (hmOrgHierarchy.containsKey(sParentOrgName)) {
				sParentOrgName = (String) hmOrgHierarchy.get(sParentOrgName);
				logger.debug(className, sMethodName, "sParentOrgName :"
						+ sParentOrgName);
				if (hmManagerHierarchy.containsKey(sParentOrgName)) {
					sManagerName = (String) hmManagerHierarchy
							.get(sParentOrgName);
					logger.debug(className, sMethodName,
							"Manager of sParentOrgName :" + sManagerName);
					if (sManagerName.equalsIgnoreCase((String) hmConstants
							.get(NONE))) {
						continue;
					}
				} else {
					sManagerName = null;
				}
			} else {
				sManagerName = null;
			}
			if (!stringUtil.isEmpty(sManagerName) && sManagerName.equalsIgnoreCase(sSAPUserID)) {
				continue;
			} else {
				break;
			}
		}
		//End :: BUG 9407605
		if (!stringUtil.isEmpty(sManagerName)) {
			/*
			 * Check is user exists in OIM based on SAP Manager ID get the
			 * actual User ID as in OIM.This will get correct User ID in case if
			 * its modified in OIM
			 */
			logger.debug(className, sMethodName, "Manager Name is "
					+ sManagerName);
			HashMap<String, String> hmManagerID = new HashMap<String, String>();
			hmManagerID.put(sUDFPersonnelNo, sManagerName);
			tcResultSet userResultSet;
			try {
				userResultSet = oUtil.getUserAPI().findUsers(hmManagerID);
				if (userResultSet != null && userResultSet.getRowCount() > 0) {
					userResultSet.goToRow(0);
					logger.debug(className, sMethodName,
							"Getting the User ID from OIM");
					sManagerName = userResultSet
							.getStringValue((String) hmConstants
									.get(USER_ID_OIM));
				}
			} catch (tcAPIException e) {
			} catch (tcColumnNotFoundException e) {
			}
		}
		logger.debug(className, sMethodName, "Manager Name Returned is  "
				+ sManagerName);
		logger.setMethodFinishLog(className, sMethodName);
		return sManagerName;
	}
}
