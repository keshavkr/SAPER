/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/common/parser/IDOCParser.java /main/197 2016/06/27 23:39:42 vsantosh Exp $ */

/* Copyright (c) 2009, 2016, Oracle and/or its affiliates. 
All rights reserved.*/
/* All rights reserved. */



/*

 DESCRIPTION

 <short description of component this file declares/defines>



 PRIVATE CLASSES

 <list of private classes defined - with one-line descriptions>



 NOTES

 <other useful comments, qualifications, etc.>



 MODIFIED    (MM/DD/YY)

 ddkumar     01/30/09 - This abstract class will parse the IDOC as

 IDocDocument object received from listener and also as a flat file

 ddkumar     01/30/09 - Creation

 Santosh    02/07/12 - BUG 13682760 - MISSING RECON EVENTS DURING SAP ER RECONCILIATION

 */



/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/common/parser/IDOCParser.java /main/197 2016/06/27 23:39:42 vsantosh Exp $
 *  @author  ddkumar

 *  @since   release specific (what release of product did this appear in)

 */



package oracle.iam.connectors.sap.common.parser;



import java.io.File;

import java.text.DateFormat;

import java.util.ArrayList;

import java.util.Date;

import java.util.HashMap;

import java.util.Hashtable;



import oracle.iam.connectors.common.ConnectorException;

import oracle.iam.connectors.common.ConnectorLogger;

import oracle.iam.connectors.common.dao.OIMUtil;

import oracle.iam.connectors.common.util.DateUtil;

import oracle.iam.connectors.sap.hrms.util.HRMSConstants;

import oracle.iam.connectors.sap.hrms.util.SAPAttributeMapBean;

import Thor.API.tcResultSet;

import Thor.API.Exceptions.tcColumnNotFoundException;



import com.sap.conn.idoc.IDocConversionException;

import com.sap.conn.idoc.IDocDocument;

import com.sap.conn.idoc.IDocFieldNotFoundException;

import com.sap.conn.idoc.IDocSegment;



/**

 * Description: Parses the IDOC as IDocDocument object received from the

 * listener and also as a flat file

 */

public abstract class IDOCParser implements HRMSConstants {

	protected ConnectorLogger logger = null;

	private String className = this.getClass().getName();

	protected String sSubType;

	protected HashMap hmCustomQueryLookup;

	protected String sCustomQuery;

	protected String sEmployeeTypeQuery;



	/**

	 * Description : Constructor

	 *

	 */



	public IDOCParser() {



	}



	/**

	 * Description : Parses the IDoc received from SAP directly and creates

	 * reconciliation events for user records having attributes as mentioned in

	 * the attribute mappings lookup definition

	 * 

	 * @param htConfig

	 *            Hashtable containing configuration details from the

	 *            Lookup.SAP.HRMS.Configuration lookup definition

	 * @param hmConstants

	 *            HashMap containing configuration details from the

	 *            Lookup.SAP.HRMS.Constants lookup definition

	 * @param alList

	 *            Bean ArrayList containing the start and end position of BEGDA

	 *            and SUBTY for all the segment definitions as mentioned in the

	 *            attribute mappings lookup definition

	 * @param oUtil

	 *            Util that initializes all reconciliation interfaces

	 * @param filePath

	 *            Path of the file location that must be parsed

	 * 

	 * @throws ConnectorException
	 */	   
        //BUG 23344445 - SAP-ER: Support for IDOC XML - Added htTaskAttributes in the method signature
	public abstract void parse(HashMap htConfig, HashMap hmConstants,
			ArrayList alList, OIMUtil oUtil, File filePath, Hashtable htTaskAttributes)
			throws ConnectorException;



	/**

	 * Description : Parses the IDoc received as a flat file in IDoc format and

	 * creates reconciliation events for user records having attributes as

	 * mentioned in the attribute mappings lookup definition

	 * 

	 * @param htConfig -

	 *            Hashtable containing configuration details from the

	 *            Lookup.SAP.HRMS.Configuration lookup definition

	 * @param hmConstants

	 *            HashMap containing configuration details from the

	 *            Lookup.SAP.HRMS.Constants lookup definition

	 * @param htTaskAttributes

	 *            Hashtable containing the scheduled task attributes

	 * @param oUtil

	 *            Util that initializes all reconciliation interfaces

	 * @param iDoc

	 *            IDoc that must be parsed to get SAP user details

	 * 

	 * @throws ConnectorException

	 */

	public abstract void parse(HashMap htConfig, HashMap hmConstants,

			Hashtable htTaskAttributes, OIMUtil oUtil, IDocDocument iDoc)

			throws ConnectorException;



	/**

	 * Description :Accesses BAPI IDOCTYPE_READ_COMPLETE to get the location of

	 * subtype and start date fields for all segments mentioned in the attribute

	 * mappings lookup definition

	 * 

	 * @param htConfig

	 *            Hashtable containing configuration details from the

	 *            Lookup.SAP.HRMS.Configuration lookup definition

	 * @param hmConstants

	 *            HashMap containing configuration details from the

	 *            Lookup.SAP.HRMS.Constants lookup definition

	 * @param htITRattributes

	 *            IT Resource values required for setting up a connection with

	 *            the target system

	 * @param htTaskAttributes

	 *            Hashtable containing the scheduled task attributes

	 * @param oUtil

	 *            Reference of OIMUtil class for getting API interfaces

	 * 

	 * @throws ConnectorException

	 */

	public abstract ArrayList getSchema(HashMap htConfig, HashMap hmConstants,

			Hashtable htITRattributes, Hashtable htTaskAttributes, OIMUtil oUtil)

			throws ConnectorException;



	/**

	 * Description : Checks the root segment for the deleted flag and verifies

	 * if the user has been deleted in SAP

	 * 

	 * @param strLine

	 *            String containing Root segment data

	 * @param sDecodeValue

	 *            Segment details of the indicator that identifies whether that

	 *            employee is deleted

	 * @throws ConnectorException

	 */

	public boolean isDeletedUser(String strLine, String sDecodeValue)

			throws ConnectorException {

		/*

		 * Sets the isDeleteUser flag to true if the string between start and end

		 * position of the file content passed is same as the 5th element of the

		 * "Delete Indicator " field of lookup table "Lookup.SAP.HRMS.Configuration"

		 */

		String sMethod = "isDeletedUser()";

		try {

			logger.setMethodStartLog(className, sMethod);

			String[] sSplitDecodeValue = sDecodeValue.split(";");

			boolean isDeleteUser = false;

			int iStartpos = Integer.parseInt(sSplitDecodeValue[2]);

			int iEndpos = Integer.parseInt(sSplitDecodeValue[3]);

			String sValue = strLine.substring(iStartpos - 1, iEndpos).trim();

			logger.debug(className, sMethod, "sValue is ::" + sValue);

			if (sValue.equalsIgnoreCase(sSplitDecodeValue[4])) {

				isDeleteUser = true;

			}

			logger.setMethodFinishLog(className, sMethod);

			return isDeleteUser;

		} catch (NumberFormatException e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		} catch (Exception e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		}

	}



	/**

	 * Description : Filters the record by the given condition. For example,
	 * checks if the IDoc is of the "person" type.
	 * 

	 * @param strLine

	 *            String containing Root segment data

	 * @param sDecodeValue

	 *            Segment details of the indicator that  identifies whether

	 *            the employee is valid

	 * @throws ConnectorException

	 */

	public boolean isValidRecord(String strLine, String sDecodeValue)

			throws ConnectorException {

		/*

		 * Sets the isValid flag to true if the string between start and end

		 * position of the file content passed is same as the 5th element of the

		 * "Object Type " field of lookup table "Lookup.SAP.HRMS.Configuration"

		 */

		String sMethod = "isValidRecord()";

		try {

			logger.setMethodStartLog(className, sMethod);

			String[] sSplitDecodeValue = sDecodeValue.split(";");

			boolean isValid = false;

			int iStartpos = Integer.parseInt(sSplitDecodeValue[2]);

			int iEndpos = Integer.parseInt(sSplitDecodeValue[3]);

			String sValue = strLine.substring(iStartpos - 1, iEndpos).trim();

			logger.debug(className, sMethod, "sValue is ::" + sValue);

			if (sValue.equalsIgnoreCase(sSplitDecodeValue[4])) {

				isValid = true;

			}

			logger.setMethodFinishLog(className, sMethod);

			return isValid;

		} catch (NumberFormatException e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		} catch (Exception e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		}

	}



	/**

	 * Description : Gets the required field from the segment line

	 *

	 * @param strLine

	 *          String containing segment data

	 * @param sDecodeValue

	 *          Segment details field and its location in the segment string

	 * @throws ConnectorException

	 */

	public String getFieldValue(String strLine, String sDecodeValue)

			throws ConnectorException {

		/*

		 * Returns the sting value contents b/w the start and end position of the

		 * file contents passed

		 */

		String sMethod = "getFieldValue()";

		try {

			logger.setMethodStartLog(className, sMethod);

			String[] sSplitDecodeValue = sDecodeValue.split(";");

			logger.setMethodFinishLog(className, sMethod);

			logger.debug(className, sMethod, "Field: " + sDecodeValue);

			int iStartpos = Integer.parseInt(sSplitDecodeValue[2]);

			int iEndpos = Integer.parseInt(sSplitDecodeValue[3]);

			String sValue = strLine.substring(iStartpos - 1, iEndpos).trim();

			logger.debug(className, sMethod, "Value:" + sValue);

			logger.setMethodFinishLog(className, sMethod);

			return sValue.trim();

		} catch (NumberFormatException e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		} catch (Exception e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		}

	}



	/**

	 * Description : Gets the required field from the IDoc segment

	 *

	 * @param segment

	 *          Segment details field that is to be extracted

	 * @param sConfig

	 *          String containing segment data

	 * @throws ConnectorException

	 */

	public String getFieldValue(IDocSegment segment, String sConfig)

			throws ConnectorException {

		String sMethod = "getFieldValue()";

		String sValue = null;

		try {

			logger.setMethodStartLog(className, sMethod);

			String[] sSplitDecodeValue = sConfig.split(";");

			logger.debug(className, sMethod, "Field: "+ sConfig);

			sValue = segment.getValue(sSplitDecodeValue[1]).toString();

			logger.debug(className, sMethod, segment.getDefinition() + ":"+sSplitDecodeValue[1] + " value : " + sValue);

			logger.setMethodFinishLog(className, sMethod);

		} catch (IDocConversionException e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		} catch (IDocFieldNotFoundException e) {

			logger.info(className, sMethod, e.getMessage());

		} catch (Exception e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		}

		return sValue;

	}



	/**

	 * Description : the record by the given condition For example, it checks if
	 * the IDoc is of the person type.
	 * 

	 * @param segment

	 *            Contains Root segment data

	 * @param sConfig

	 *            Segment details of the indicator that identifies whether

	 *            that employee is valid

	 * @throws ConnectorException

	 */

	public boolean isValidRecord(IDocSegment segment, String sConfig)

			throws ConnectorException {

		String sMethod = "isValidRecord()";

		try {

			logger.setMethodStartLog(className, sMethod);

			String[] sSplitDecodeValue = sConfig.split(";");

			if (!segment.getDefinition().equalsIgnoreCase(sSplitDecodeValue[0])) {

				logger.error(className, sMethod,

						"Invalid segment to verify the valid record");

				logger.debug(className, sMethod, "return value is TRUE");

				return true;

			}

			int iFields = segment.getNumFields();

			for (int i = 0; i < iFields; i++) {

				if (segment.getName(i).equalsIgnoreCase(sSplitDecodeValue[1])

						&& segment.getValue(i).toString().equalsIgnoreCase(

								sSplitDecodeValue[4])) {

					logger.setMethodFinishLog(className, sMethod);

					logger.debug(className, sMethod, "return value is TRUE");

					return true;

				}

			}

		} catch (IDocConversionException e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		} catch (Exception e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		}

		logger.debug(className, sMethod, "return value is FALSE");

		logger.setMethodFinishLog(className, sMethod);

		return false;

	}



	/**

	 * Description : Checks the root segment for the deleted flag and

	 * verifies if the user has been deleted in SAP

	 *

	 * @param segment

	 *          String containing Root segment data

	 * @param sConfig

	 *          Segment details of the indicator which identifies whether employee

	 *          is deleted

	 * @throws ConnectorException

	 */

	public boolean isDeletedUser(IDocSegment segment, String sConfig)

			throws ConnectorException {

		String sMethod = "isDeletedUser()";

		try {

			logger.setMethodStartLog(className, sMethod);

			String[] sSplitDecodeValue = sConfig.split(";");

			if (!segment.getDefinition().equalsIgnoreCase(sSplitDecodeValue[0])) {

				logger.error(className, sMethod,

						"Invalid segment to verify the valid record");

				logger.debug(className, sMethod, "return value is FALSE");

				return false;

			}

			int iFields = segment.getNumFields();

			for (int i = 0; i < iFields; i++) {

				if (segment.getName(i).equalsIgnoreCase(sSplitDecodeValue[1])

						&& segment.getValue(i).toString().equalsIgnoreCase(

								sSplitDecodeValue[4])) {

					logger.debug(className, sMethod, "return value is TRUE");

					logger.setMethodFinishLog(className, sMethod);

					return true;

				}



			}

		} catch (IDocConversionException e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		} catch (Exception e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		}

		logger.setMethodFinishLog(className, sMethod);

		return false;

	}



	/**

	 * Description : Receives the segment and loops through values in the

	 * attribute map to check if it is present in this segment. If the attribute

	 * is found, then it is added to a hashMap

	 * 

	 * @param segment

	 *            IDoc segment that is checked for the required attributes

	 * @param attrMapList

	 *            Contains array lists of attributes arranged according to

	 *            segment number

	 * @param hmConstants

	 *            HashMap containing constants declared in the lookup definition

	 * @param dfOIM

	 *            Contains Oracle Identity Manager date format

	 * @throws ConnectorException

	 */

	public HashMap processRecord(IDocSegment segment, Hashtable attrMapList,

			HashMap hmConstants, DateFormat dfOIM) throws ConnectorException {

		HashMap<String, String> hmRecord = new HashMap<String, String>();

		String sMethod = "processRecord()";

		try {

			logger.setMethodStartLog(className, sMethod);

			DateUtil dateUtil = new DateUtil(logger);

			/*

			 * attrMapList has the attributes arranged according to segment

			 * number in arraylists. Hence looping will be done for as many

			 * values in the arraylist. The values got from the segment are

			 * populated in the hashmap ~> hmRecord. Even the sub-type of the

			 * field is matched

			 */

			ArrayList alAttrMapList = (ArrayList) attrMapList.get(segment

					.getDefinition());

			sSubType = (String) hmConstants.get(SUB_TYPE_FIELD);

			if (alAttrMapList != null && alAttrMapList.size() > 0) {

				int iSize = alAttrMapList.size();

				for (int index = 0; index < iSize; index++) {

					SAPAttributeMapBean oAttrMapBean = (SAPAttributeMapBean) alAttrMapList

							.get(index);

					if (segment.getDefinition().equalsIgnoreCase(

							oAttrMapBean.getSSegmentName())

							&& (oAttrMapBean.getSSubType().equalsIgnoreCase(

									(String) hmConstants.get(NONE)) || oAttrMapBean

									.getSSubType().equalsIgnoreCase(

											segment.getValue(sSubType)

													.toString()))) {

						if(segment.getValue(oAttrMapBean.getSSAPAttributeName())!= null){

							String sValue = segment.getValue(

								oAttrMapBean.getSSAPAttributeName()).toString();



							/*

							 * Check if the field type mentioned in Attribute

							 * Mapping is of type Date. If yes then we need to

							 * format the date in OIM default format

							 */

							if (oAttrMapBean.getSFieldType().equalsIgnoreCase(

									(String) hmConstants.get(DATE_FIELD_TYPE))) {

								Date dtValue = dateUtil.returnDate(sValue,

									(String) hmConstants

											.get(LISTENER_DATE_FORMAT));

								sValue = dfOIM.format(dtValue);

							}

						if (sValue == null) {

							sValue = "";

						}

						hmRecord.put((String) oAttrMapBean.getSOIMFieldName(),

								sValue);

							logger.debug(className, sMethod, segment.getDefinition() + ":" + 

									oAttrMapBean.getSOIMFieldName() + " set to " + sValue);

						} else {

							// Start BUG 13682760 - MISSING RECON EVENTS DURING SAP ER RECONCILIATION 

							if (oAttrMapBean.getSFieldType().equalsIgnoreCase(

									(String) hmConstants.get(DATE_FIELD_TYPE))) {

								String sValue = null;

								if(segment.getValue(oAttrMapBean.getSSAPAttributeName()) == null){

									sValue = "";

									logger.debug(className, sMethod, "Date Field is either blank or 00000000 so setting blank");

								}

								hmRecord.put((String) oAttrMapBean.getSOIMFieldName(),

										sValue);

								logger.debug(className, sMethod, segment.getDefinition() + ":" + 

											oAttrMapBean.getSOIMFieldName() + " set to " + sValue);

							}

							// End BUG 13682760 - MISSING RECON EVENTS DURING SAP ER RECONCILIATION 

						}



					}

				}

			}

		} catch (IDocConversionException e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		} catch (IDocFieldNotFoundException e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		} catch (Exception e) {

			logger.error(className, sMethod, e.getMessage());

			logger.setStackTrace(e, className, sMethod, e.getMessage());

			throw new ConnectorException(e);

		}

		logger.setMethodFinishLog(className, sMethod);

		return hmRecord;

	}



	/**

	 * Description : Executes the custom query specified for each user record

	 *

	 * @param hmReconDetails

	 *          HashMap containing details of user record to be reconciled to Oracle Identity Manager 

	 * @param userResultSet

	 *          Result Set of the user for getting values from Oracle Identity Manager 

	 * @throws ConnectorException

	 *

	 */

	public boolean executeCustomQuery(HashMap<String, String> hmReconDetails,

			tcResultSet userResultSet) throws ConnectorException {

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

			String sArrORExp[] = sCustomQuery.split(OR_SPLIT_REGEX);

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

					 * Since all the attributes were not got from SAP,we got to

					 * OIM to get its value

					 */

					if (!hmReconDetails.containsKey(sKey)) {

						logger

								.debug(

										className,

										sMethod,

										"Getting the attribute value"

												+ " from OIM as the key was not present in HashMap");



						if (hmCustomQueryLookup.size() > 0

								&& hmCustomQueryLookup.containsKey(sKey)) {

							String sUDFFieldName = (String) hmCustomQueryLookup

									.get(sKey);

							String sValueFromOIM;

							if (userResultSet.getRowCount() > 0) {

								userResultSet.goToRow(0);

								try {

									sValueFromOIM = userResultSet

											.getStringValue(sUDFFieldName);

									hmReconDetails.put(sKey, sValueFromOIM);

								} catch (tcColumnNotFoundException e) {

									logger

											.error(

													className,

													sMethod,

													sUDFFieldName

															+ " Column not found in OIM");

									hmReconDetails.put(sKey, "");

								}

							} else {

								logger

										.debug(className, sMethod,

												"Could not get the attribute value from OIM as user is not present");

								hmReconDetails.put(sKey, "");

							}

						} else {

							logger

									.error(

											className,

											sMethod,

											"Please populate the Custom Query"

													+ " Mapping Lookup with proper values");

							return false;

						}



					}

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

						break;

					}

				}

				if (isValid) {

					break;

				}

			}

			if (isValid) {

				logger.info(className, sMethod, "Custom Ouery is Valid");

			} else {

				logger.info(className, sMethod, "Custom Ouery is not Valid");

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



}

