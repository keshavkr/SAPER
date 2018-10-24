


/* Copyright (c) 2009, 2017, Oracle and/or its affiliates. 
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

 ddkumar		01/30/09 - This class is the parser for HRMD_A message type

 ddkumar		01/30/09 - Creation

 radhika		03/29/10 - BUG 9523713 - DESIRE ABILITY TO CONFIGURE WHETHER FUTURE DATED PROCESSING

 niranjan		09/28/10 - BUG 10134695 - ATTRIBUTES WITH SUBTYPES ARE NOT PARSED WHEN END-OF-LIN

 jagadeesh	11/12/10 - BUG 10229768 - STARTING AND TERMINATION DATE ARE NOT COMING

 jagadeesh  12/31/10 - BUG 10434626 - TRANSFORAMTION AND VALIDATION NOT SUPPORTED

 jagadeesh  01/03/11 - BUG 10312742 - CAPABILITY TO TRACK THE IDCO NUMBER IN OIM SAP U

 jagadeesh  01/10/11 - BUG 11059390 - FUTURE DATED HIRE EVENTS RECON FAILING IF LOOKUP VALUES ARE EMPTY

 Akshata    05/09/11 - Bug 12409614- SUPPORT SAP ER911 MANAGER BEHAVIOUR IN SAP ER912

 Santosh    01/10/12 - Bug 13506280- INVALID DATE PARSING DURING SAP HRMS USER RECON

 Santosh    01/10/12 - BUG 13538856 - RECON FAILS WITH ORA-01400 IN XL_SP_RECONEVALUATEUSER 

 Santosh    02/27/12 - BUG 13716729 - FUTURE DATED HIRE, TERMINATE, REHIRE NOT FUNCTIONING CORRECTLY  

 Santosh    03/31/12 - Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

 Santosh    09/08/12 - BUG 14384664 - SAP HR RE-HIRE EVENT IS NOT ENABLING THE USER  
 Santosh    12/04/12 - BUG 15947444 - ISSUE WITH CREATION OR UPDATE OF EMPLOYEE WITH START DATE IN THE FUTURE 
 Swaroopa   11/18/13 -BUG 17796698 - SAP ER Connector Duplication of Deferred Events
 Swaroopa   12/04/13- Bug 17892489-SAP ER 9.1.2.4.0 CONNECTOR EMPLOYEE END DATE PROBLEM
 Sandeep    03/06/16 - Bug 23482998 - SAP ER CONNECTER: ERROR WHILE READING SINGLE CHARACTER FIELDS AT END OF LINE  
 */



/**

 *  @author  ddkumar

 *  @since   release specific (what release of product did this appear in)

 */



package oracle.iam.connectors.sap.common.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

import java.io.FileInputStream;

import java.io.FileNotFoundException;

import java.io.IOException;

import java.io.InputStreamReader;

import java.io.LineNumberReader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Date;

import java.util.HashMap;

import java.util.Hashtable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;



import oracle.iam.connectors.common.ConnectorException;

import oracle.iam.connectors.common.ConnectorLogger;

import oracle.iam.connectors.common.dao.OIMUtil;

import oracle.iam.connectors.common.util.DateUtil;

import oracle.iam.connectors.common.util.NumberUtil;

import oracle.iam.connectors.common.util.StringUtil;

import oracle.iam.connectors.sap.common.connection.SAPConnection;

import oracle.iam.connectors.sap.common.util.SAPUtil;

import oracle.iam.connectors.sap.hrms.tasks.SAPHRMSUserRecon;

import oracle.iam.connectors.sap.hrms.util.SAPAttributeMapBean;
import oracle.iam.platform.Platform;
import oracle.iam.reconciliation.api.BatchAttributes;
import oracle.iam.reconciliation.api.ChangeType;
import oracle.iam.reconciliation.api.InputData;
import oracle.iam.reconciliation.api.ReconOperationsService;
import oracle.iam.reconciliation.api.ReconciliationResult;
import Thor.API.tcResultSet;

import Thor.API.Exceptions.tcAPIException;

import Thor.API.Exceptions.tcColumnNotFoundException;



import com.sap.conn.idoc.IDocDocument;

import com.sap.conn.idoc.IDocDocumentIterator;
import com.sap.conn.idoc.IDocDocumentList;
import com.sap.conn.idoc.IDocFactory;
import com.sap.conn.idoc.IDocRepository; 
import com.sap.conn.idoc.IDocXMLProcessor; 
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.idoc.IDocSegment;

import com.sap.conn.jco.JCoDestination;

import com.sap.conn.jco.JCoException;

import com.sap.conn.jco.JCoFunction;

import com.sap.conn.jco.JCoParameterList;

import com.sap.conn.jco.JCoTable;



/**

 * Description: Parser for HRMD_A message type

 *

 */

public class HRMDAParser extends IDOCParser {

	private boolean isValid = false;	

	private boolean isEOF = false;

	private HashMap hmHireEvent;

	private HashMap hmRehireEvent;

	private HashMap hmTerminateEvents;

	private HashMap hmOrgHierarchy;

	private HashMap hmManagerHierarchy;

	private Hashtable htAttrMap;

	private HashMap htITRMapping;

	private HashMap hmEmployeeType;

	private String sRemoveZero;

	private String sDisabledStatus;

	private String sStatus;

	private String sActiveStatus;

	private String sUserProvisioningDate;

	private String sOrganization;

	private String sEmployeeType;

	private String sUserLoginField;

	private String sSAPPersonnelNumber;

	private String sUserType;

	private String sUDFPersonnelNo;

	private String sHRMSCreated;

	private String sReconcileFirstTimeDisabledUsers;

	private String sObjectName;

	private String isFutureDatedEventHandlingEnabled;

	LineNumberReader lnrdr = null;

	private StringUtil stringUtil = null;

	private NumberUtil numberUtil = null;

	private SAPUtil oSAPUtil = null; 

	private DateUtil dateUtil = null;

	private String className = this.getClass().getName();

	Date dtHireDate = null;

	// OIM Services
    private ReconOperationsService reconOps =null;

	/**

	 * Description: Constructor method to initialize the logger

	 */

	public HRMDAParser() {

		logger = new ConnectorLogger(HRMS_LOGGER);

		stringUtil = new StringUtil();

		dateUtil=new DateUtil(logger);

		numberUtil=new NumberUtil();
		
		reconOps = ((ReconOperationsService)Platform.getService(ReconOperationsService.class));

	}



	/**

	 * Description: Initializes all lookup values mentioned in the Configuration

	 * lookup definition, such as Hire Events,Terminate Events, User Type, and

	 * Custom Query

	 * 

	 * @param hmHRMSConfig

	 *            HashMap containing configuration details from the

	 *            Lookup.SAP.HRMS.Configuration lookup definition

	 * @param hmConstants

	 *            HashMap containing configuration details from the

	 *            Lookup.SAP.HRMS.Constants lookup definition

	 * @param htTaskAttributes

	 *            Hashtable containing the scheduled task attributes

	 * @param oUtil

	 *            Reference of OIMUtil class for getting API interfaces

	 * @throws ConnectorException

	 */

	public void initialiseLookupValues(final HashMap hmHRMSConfig,

			final HashMap hmConstants, Hashtable htTaskAttributes, OIMUtil oUtil)

			throws ConnectorException {

		String sMethodName = "initialiseValues()";

		logger.setMethodStartLog(className, sMethodName);

		try {

			oSAPUtil = new SAPUtil(logger);

			// Initialising Task Attributes

			sCustomQuery = (String) htTaskAttributes.get(CUSTOM_QUERY);

			sObjectName = (String) htTaskAttributes.get(RESOURCE_OBJECT);

			sEmployeeTypeQuery = (String) htTaskAttributes.get(EMPLOYEE_TYPE_QUERY);

			String sAttributeMapingLookup = (String) htTaskAttributes

					.get(ATTRIBUTE_MAPPING_LOOKUP);

			htAttrMap = oSAPUtil.populateAttributeMapDetails(sAttributeMapingLookup,

					oUtil.getLookIntf());



			// If Custom Query value is set,then initialise Query Lookup

			if (!stringUtil.isEmpty(sCustomQuery)) {

				String sCustomQueryLookup = (String) htTaskAttributes

						.get(CUSTOM_QUERY_LOOKUP);

				hmCustomQueryLookup = oUtil.getLookUpMap(sCustomQueryLookup);

			}

			sReconcileFirstTimeDisabledUsers = (String) hmHRMSConfig

					.get(RECONCILE_FIRST_TIME_DISABLED_USERS);

			sRemoveZero = (String) hmHRMSConfig.get(REMOVE_ZERO);

			// Initialising Lookup Values HashMap

			hmHireEvent = oUtil.getLookUpMap((String) hmHRMSConfig

					.get(HIRE_EVENTS_LOOKUP));

			hmRehireEvent = oUtil.getLookUpMap((String) hmHRMSConfig

					.get(REHIRE_EVENTS_LOOKUP));

			hmTerminateEvents = oUtil.getLookUpMap((String) hmHRMSConfig

					.get(TERMINATE_EVENTS_LOOKUP));

			

			hmEmployeeType = oUtil.getLookUpMap((String) hmHRMSConfig

					.get(EMPLOYEE_TYPE_LOOKUP));

			htITRMapping = oUtil.getLookUpMap((String) hmHRMSConfig

					.get(IT_RESOURCE_MAPPING));



			hmOrgHierarchy = oUtil.getLookUpMap((String) hmHRMSConfig

					.get(ORG_LOOKUP_NAME));

			hmManagerHierarchy = oUtil.getLookUpMap((String) hmHRMSConfig

					.get(MANAGER_LOOKUP_NAME));	

			

			//Start: BUG 9523713-DESIRE ABILITY TO CONFIGURE FUTURE DATED PROCESSING 

			isFutureDatedEventHandlingEnabled = (String)hmHRMSConfig.get(FUTURE_DATED_EVENT_HANDLING);

			//End: BUG 9523713-DESIRE ABILITY TO CONFIGURE FUTURE DATED PROCESSING

			

			// Initialise all the global constants used frequently

			initialiseConstantsfromLookup(hmConstants);

		} catch (Exception e) {

			logger.error(className, sMethodName,

					"Problem in initialising the Task Scheduler/Lookup values");

			throw new ConnectorException(e);

		}

		logger.setMethodFinishLog(className, sMethodName);



	}



	/**

	 * Description: Initialize all the lookup values mentioned the

	 * "Lookup.SAP.HRMS.Constants"

	 * 

	 * @param hmConstants

	 *          HashMap containing configuration details from the lookup

	 *          "Lookup.SAP.HRMS.Constants"

	 * 

	 * @throws ConnectorException

	 * 

	 */

	private void initialiseConstantsfromLookup(final HashMap hmConstants)

			throws ConnectorException {

		String sMethodName = "initialiseConstantsfromLookup()";

		logger.setMethodStartLog(className, sMethodName);

		try {

			// Initialise constants from "Lookup.SAP.HRMS.Constants"

			sDisabledStatus = (String) hmConstants.get(DISABLED);

			sStatus = (String) hmConstants.get(STATUS);

			sActiveStatus = (String) hmConstants.get(ACTIVE);

			sUserProvisioningDate = (String) hmConstants.get(USER_PROVISIONING_DATE);

			sOrganization = (String) hmConstants.get(ORGANIZATION);

			sEmployeeType = (String) hmConstants.get(EMPLOYEE_TYPE);

			sUserLoginField = (String) hmConstants.get(USER_LOGIN_FIELD);

			sUserType = (String) hmConstants.get(USER_TYPE);

			sUDFPersonnelNo = (String) hmConstants.get(PERSONNEL_NOS_UDF);

			sHRMSCreated = (String) hmConstants.get(USER_CREATED_FROM_HRMS);

			sSAPPersonnelNumber = (String) hmConstants.get(PERSONNEL_NUMBER);

		} catch (Exception e) {

			logger.error(className, sMethodName,

					"Problem in initialising the constant lookup values");

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

			throw new ConnectorException(e);

		}

		logger.setMethodFinishLog(className, sMethodName);

	}



	/**

	 * Description: Parses the IDoc received as a flat file in IDoc format and

	 * creates reconciliation events for user records having attributes as

	 * mentioned in the attribute mappings lookup definition

	 * 

	 * @param hmConfig

	 *            HashMap containing configuration details from the

	 *            Lookup.SAP.HRMS.Configuration lookup definition

	 * @param hmConstants

	 *            HashMap containing configuration details from the

	 *            Lookup.SAP.HRMS.Constants lookup definition

	 * @param alList

	 *            ArrayList containing the start and end position of BEGDA and

	 *            SUBTY for all the segment definitions as mentioned in the

	 *            attribute mappings lookup definition

	 * @param oUtil

	 *            Util that initializes all reconciliation interfaces

	 * @param filePath

	 *            Path of the file location that must be parsed

	 * 

	 * @throws ConnectorException

	 */
	//BUG 23344445 - SAP-ER: Support for IDOC XML - Added htTaskAttributes in the method signature
	public void parse(final HashMap hmConfig, final HashMap hmConstants,
			ArrayList alList, OIMUtil oUtil, File filePath, Hashtable htTaskAttributes) throws ConnectorException {
		boolean isFirst = true;

		isValid = false;

		isEOF = false;

		int currentlineNumber = 0;

//		HashMap hmCurrDatedEvents = new HashMap();

		HashMap hmDefaultAttrs = new HashMap();

//		HashMap<Date, HashMap> hmDeferredEvents = new HashMap<Date, HashMap>();

//		HashMap hmFutureDatedEvents = new HashMap();
		
		//*********************************New API******************************************************//
		//LinkedHashMap<String, HashMap> eventsData = new LinkedHashMap<String, HashMap>();
		// CurrentDated Event data list

    	//*********************************New API******************************************************//

		
		String sUserIDFromSAP = null;
		//Date dtHireDate = null;

		Date dtStartDate = null;

		//Date dtEndDate = null;

		String sMethodName = "parse()";
		FileReader fileReaderXml = null;
		BufferedReader br = null;

		try {

			logger.setMethodStartLog(className, sMethodName);

			/*

			 * Get the batch size,Employee Group,Employee Sub Group and other

			 * mandatory OIM attributesfrom the lookup table

			 * "Lookup.SAP.HRMS.Configuration"

			 */

			int iBatchSize;

			try {

				iBatchSize = Integer.parseInt((String) hmConfig.get(BATCH_SIZE));

			} catch (Exception e) {

				// Setting default batch size value to 10 in case error occurs in

				// parsing

				logger.warn(className, sMethodName,

						"Error in parsing the batch size value, Hence"

								+ "Setting default batch size value to 10");

				iBatchSize = 10;

			}

			String sDateFormat = (String) hmConstants.get(FILE_DATE_FORMAT);

			String sEmployeeGroup = (String) hmConfig.get((String) hmConstants

					.get(EMPLOYEE_GROUP));

			String sEmployeeSubGroup = (String) hmConfig.get((String) hmConstants

					.get(EMPLOYEE_SUB_GROUP));

			hmDefaultAttrs.put(sOrganization, (String) hmConfig.get(sOrganization));

			hmDefaultAttrs.put(sUserType, (String) hmConfig.get(sUserType));

			hmDefaultAttrs.put(sEmployeeType, (String) hmConfig

					.get(sEmployeeType));



			DateFormat sdf = new SimpleDateFormat(oUtil.getReconOperAPI()

					.getDefaultDateFormat());
			//Start ::BUG 23344445 - SAP-ER: Support for IDOC XML 
			if (hmConfig.get(IS_IDOC_FORMAT_XML)!=null && hmConfig.get(IS_IDOC_FORMAT_XML).toString().equalsIgnoreCase(YES)) {
				String iDocXML = null;
				if(filePath != null && filePath.exists()){
					fileReaderXml = new FileReader(filePath);
					br = new BufferedReader(fileReaderXml);
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = br.readLine()) != null)
					{
						sb.append(line);
					}
					iDocXML = sb.toString();

					IDocRepository iDocRepository = JCoIDoc.getIDocRepository(SAPConnection.getJcoConnection());
					String tid = SAPConnection.getJcoConnection().createTID();
					IDocFactory iDocFactory = JCoIDoc.getIDocFactory();

					IDocXMLProcessor processor=iDocFactory.getIDocXMLProcessor();
					IDocDocumentList iDocList=processor.parse(iDocRepository, iDocXML);
					JCoIDoc.send(iDocList, IDocFactory.IDOC_VERSION_DEFAULT, SAPConnection.getJcoConnection(), tid);
					IDocDocumentIterator iterator = iDocList.iterator();
					while (iterator.hasNext()) {
						IDocDocument doc = iterator.next();
						parse(hmConfig, hmConstants, htTaskAttributes, oUtil, doc);
					}
					SAPConnection.getJcoConnection().confirmTID(tid);
				}else{
					throw new ConnectorException("IDOC XML file does not exist");
				}
			}//End ::BUG 23344445 - SAP-ER: Support for IDOC XML
			else{
				
				
		    	ArrayList<HashMap<String,String>> currentDatedEventList = new ArrayList<HashMap<String,String>>();
		    	ArrayList<HashMap<Date,HashMap>> deferredEventList = new ArrayList<HashMap<Date,HashMap>>();
				
				HashMap hmCurrDatedEvents= new HashMap();
				HashMap<Date, HashMap> hmDeferredEvents = new HashMap<Date, HashMap>();
				HashMap hmFutureDatedEvents = new HashMap();
				
				boolean isBulkRecon  = isBulkReconSupported();
				// Load the file using LineNumberReader

				String sFileEncodingtype = (String) hmConstants.get(FILE_ENCODING_TYPE);

				if (sFileEncodingtype.equalsIgnoreCase((String) hmConstants.get(NONE))) {

					lnrdr = new LineNumberReader(new InputStreamReader(new FileInputStream(

							filePath)));

				} else {

					lnrdr = new LineNumberReader(new InputStreamReader(new FileInputStream(

							filePath), sFileEncodingtype));

				}

				String sResult = null;

				boolean isFutureDatedSegment = false;
				// Loop through until EOF is reached

				while (!isEOF) {

					currentlineNumber = lnrdr.getLineNumber();

					lnrdr.setLineNumber(currentlineNumber);



					// Calling getNextpage() to get the lines to read based on the batch

					// size value set

					sResult = getNextpage(iBatchSize, hmConstants);

					if (!stringUtil.isEmpty(sResult)) {

						String[] rows = sResult.split("\n");

						int noOfRows = rows.length;

						String sStrLine;

						for (int iRows = 0; iRows < noOfRows; iRows++) {

							sStrLine = rows[iRows];

							isFutureDatedSegment = false;

							String sSubtype = "";

							/*

							 * Loop through the contents of the file line by line and check if

							 * the segment name is same as that of the root segment mentioned in

							 * the Lookup Table"Lookup.SAP.HRMS.Configuration".This will

							 * detemine the occurence of new user to be reconciled.

							 */

							String sSegmentName = sStrLine.substring(0,

									Integer.parseInt((String) hmConfig.get(SEGMENT_LENGTH))).trim();

							logger.debug(className, sMethodName, "Segment Name = " + sSegmentName);

							if (sSegmentName.equalsIgnoreCase((String) hmConfig

									.get(ROOT_SEGMENT))) {

								logger.debug(className, sMethodName, "Parsing root segment");

								if (!isFirst) {

									if (isValid && !SAPHRMSUserRecon.isReconStopped) {

										// Calling reconcileUser to process the user record

										hmCurrDatedEvents.putAll(hmDefaultAttrs);

										if(hmCurrDatedEvents!= null && hmCurrDatedEvents.size()>0){

											//START:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

											/*hmCurrDatedEvents = doTrasformation(hmCurrDatedEvents, hmConstants, oUtil, hmConfig);

										if(doValidation(hmCurrDatedEvents, hmConstants, oUtil, hmConfig))*/
											
											//*********************New API *****************************************//
											LinkedHashMap<String, HashMap> eventsData = reconcileUser(hmCurrDatedEvents, hmConstants, oUtil, hmConfig, isBulkRecon);
											if(eventsData.size()>0){
												if(eventsData.keySet().contains(FUTURE)){
													deferredEventList.add(new HashMap<Date, HashMap>(eventsData.get(FUTURE)));
												}else{
												logger.debug(className, sMethodName, "Personnel Number :: "+hmCurrDatedEvents.get("Personnel Number"));
												logger.debug(className, sMethodName, "Current events " + hmCurrDatedEvents);
												currentDatedEventList.add(new HashMap<String, String>(eventsData.get(CURRENT))); 
												hmCurrDatedEvents.clear();
											}
											}

											//*********************New API ********************************************//

										}//END:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES

										if(hmDeferredEvents!= null && hmDeferredEvents.size()>0){
											//*********************New API ********************************************//

											LinkedHashMap<String, HashMap> eventsData = createDeferredEvents(hmDeferredEvents, sUserIDFromSAP, hmConstants, oUtil, hmConfig, isBulkRecon);
											if(eventsData.size()>0){								
												HashMap hmDeferredEventsNew = eventsData.get(FUTURE);
												logger.debug(className, sMethodName, "Deferred events " + hmDeferredEvents.get("End date"));
												Iterator<HashMap<Date, HashMap<String, String>>> it = hmDeferredEventsNew.entrySet().iterator();
												while (it.hasNext()) {
													Map.Entry pair = (Map.Entry)it.next();
													HashMap<Date,HashMap> deferredMap = new HashMap<Date,HashMap>();
													deferredMap.put((Date)pair.getKey(), (HashMap)pair.getValue());
													deferredEventList.add(deferredMap);	
												}
											}
											//*********************New API ********************************************//
											//Start: Bug 17796698- SAP-ER connector: duplication of deferred events
											hmDeferredEvents.clear();
											//End: Bug 17796698- SAP-ER connector: duplication of deferred events 
										}

									}

								}

								isFirst = false;

								// Getting the User ID from SAP

								sUserIDFromSAP = getFieldValue(sStrLine, (String) hmConfig

										.get(sUserLoginField));

								/*

								 * Checks if the user record is valid or not and sets the isValid

								 * flag to true if record is to be reconciled

								 */

								isValid = isValidRecord(sStrLine, (String) hmConfig

										.get(OBJECT_TYPE));

								logger.debug(className, sMethodName, "Valid object type?" + isValid);

								/*

								 * The isDeletedUser flag is set to true and

								 * createDeleteReconEvent is called if user is deleted from target

								 * system

								 */

								if (isValid

										&& isDeletedUser(sStrLine, (String) hmConfig

												.get(DELETE_INDICATOR))) {

									hmCurrDatedEvents.put(sSAPPersonnelNumber, sUserIDFromSAP);

									try {

										reconcileDeletedUser(hmCurrDatedEvents, hmConstants, oUtil);

									} catch (Exception e) {

										logger.error(className, sMethodName, "Error while creating delete recon event");

									}

								}

							}



							/*

							 * If lookup recon for employee type is done,then get the decode

							 * value for Emp Group~Emp Sub Group combination.The obtained decode

							 * value is the value for key "Employee Type" in the hashmap. If

							 * lookup is not populated,then the default value as mentioned in

							 * lookup table "Lookup.SAP.HRMS.Configuration" is the value for key

							 * "Employee Type" in the hashmap.

							 */

							if (isValid

									&& sSegmentName.equalsIgnoreCase((String) hmConfig

											.get(GROUP_SEGMENT))) {

								String sEmpGroup = getFieldValue(sStrLine, sEmployeeGroup);

								String sEmpSubGroup = getFieldValue(sStrLine, sEmployeeSubGroup);

								String sGroup_SubGroup = sEmpGroup + '~' + sEmpSubGroup;

								hmCurrDatedEvents.put((String) hmConstants.get(EMPLOYEE_GROUP),

										sEmpGroup);

								hmCurrDatedEvents.put((String) hmConstants.get(EMPLOYEE_SUB_GROUP),

										sEmpSubGroup);

								if (hmEmployeeType.containsKey(sGroup_SubGroup)) {

									hmDefaultAttrs.put(sEmployeeType, (String) hmEmployeeType

											.get(sGroup_SubGroup));

								} 

							}



							if (isValid

									&& sSegmentName.equalsIgnoreCase((String) hmConfig

											.get(ACTIONS_EVENT))) {

								String sStartDate = getFieldValue(sStrLine, (String) hmConfig

										.get(EVENT_BEGIN_DATE));

								//Start: BUG 10229768 - STARTING AND TERMINATION DATE ARE NOT COMING

								String sEndDate = getFieldValue(sStrLine, (String) hmConfig

										.get(EVENT_END_DATE));

								//End: BUG 10229768 - STARTING AND TERMINATION DATE ARE NOT COMING

								String sEventID = getFieldValue(sStrLine, (String) hmConfig

										.get(EVENT));



								populateDates(sStartDate, sEndDate, sEventID,

										hmConstants, hmConfig, hmCurrDatedEvents,

										hmFutureDatedEvents, hmDeferredEvents, sdf,

										sUserIDFromSAP, oUtil, sDateFormat);

							}



							if (isValid && htAttrMap.containsKey(sSegmentName)) {

								int iNoOfSegments = alList.size();

								/*

								 * Loop through and get the corresponding SUBTYP and BEGDA start

								 * and end postion values

								 */

								for (int i = 0; i < iNoOfSegments; i++) {

									SAPAttributeMapBean oBean = (SAPAttributeMapBean) alList.get(i);

									if (oBean.getSSegmentName().equalsIgnoreCase(sSegmentName)

											&& !oBean.getSSegmentName().equalsIgnoreCase(

													(String) hmConfig.get(ROOT_SEGMENT))) {

										isFutureDatedSegment = false;

										if(!stringUtil.isEmpty(oBean

												.getSBeginDatePosition())&&!stringUtil.isEmpty(oBean

														.getSEndDatePosition())){

											int iStartBeginDatePos = Integer.parseInt(oBean

													.getSBeginDatePosition());

											int iEndBeginDatePos = Integer.parseInt(oBean

													.getSEndDatePosition());



											String sStartDate = sStrLine.substring(

													iStartBeginDatePos - 1, iEndBeginDatePos).trim();

											/*

											 * Determines if user record is future dated or not by

											 * comparing the start date value of Infotype segment to the

											 * current date.Sets the isFutureDatedEvent flag to true if

											 * start date is greater than the current date

											 */

											dtStartDate = dateUtil.returnDate(sStartDate, sDateFormat);

											Date dtToday = new Date();

											//Start: BUG 9523713-DESIRE ABILITY TO CONFIGURE FUTURE DATED PROCESSING 

											if (dtStartDate!= null && dtStartDate.after(dtToday)&& 

													!dtStartDate.equals(dtHireDate) &&

													isFutureDatedEventHandlingEnabled.equalsIgnoreCase((String) hmConstants.get(YES))) {

												isFutureDatedSegment = true;

												//End: BUG 9523713-DESIRE ABILITY TO CONFIGURE FUTURE DATED PROCESSING

											}

										}

										if(!stringUtil.isEmpty(oBean

												.getSSubTypeStartPosition())&&!stringUtil.isEmpty(oBean

														.getSSubTypeEndPosition())){

											int iStartSubtypPos = Integer.parseInt(oBean

													.getSSubTypeStartPosition());

											int iEndSubTypPos = Integer.parseInt(oBean

													.getSSubTypeEndPosition());

											sSubtype = sStrLine.substring(iStartSubtypPos - 1,

													iEndSubTypPos).trim();

											logger.debug(className, sMethodName, "Sub type" + sSubtype);

										}									

										break;

									}

								}

								ArrayList alAttrMapList = (ArrayList) htAttrMap.get(sSegmentName);

								int iNosOfAttributes = alAttrMapList.size();

								/*

								 * Loop through all the attributes specified in lookup table

								 * "Lookup.SAP.HRMS.AttributeMapping" Put the values in hashmap

								 * hmReconDetails if the data is current dated Put the values in

								 * hashmap hmFutureDatedEvents if the data is future dated only if

								 * Subtype value is NONE or the value specified in the Lookup

								 * matches the value got rom its respective segment definition

								 */

								for (int index = 0; index < iNosOfAttributes; index++) {

									SAPAttributeMapBean oAttrMapBean = (SAPAttributeMapBean) alAttrMapList

											.get(index);



									if (oAttrMapBean.getSSubType().equalsIgnoreCase(

											(String) hmConstants.get(NONE))

											|| oAttrMapBean.getSSubType().equalsIgnoreCase(sSubtype)) {



										String shmKey = oAttrMapBean.getSOIMFieldName();

										int iStartPos = Integer.parseInt(oAttrMapBean

												.getSStartPosition());

										int iEndPos = Integer

												.parseInt(oAttrMapBean.getSEndPosition());

										String shmValue = null;

										try {

											shmValue = sStrLine.substring(iStartPos - 1, iEndPos)

													.trim();

											/*

											 * Check if the field type mentioned in Attribute Mapping is

											 * of type Date. If yes then we need to format the date in

											 * OIM default format

											 */

											if (oAttrMapBean.getSFieldType().equalsIgnoreCase(

													(String) hmConstants.get(DATE_FIELD_TYPE))) {

												// Start Bug 13506280 INVALID DATE PARSING DURING SAP HRMS USER RECON	

												if(stringUtil.isEmpty(shmValue) || "00000000".equals(shmValue)){

													logger.debug(className, sMethodName, "Date Field is either blank or 00000000 so setting null");

													shmValue = null;

												} else {

													Date dtValue = dateUtil.returnDate(shmValue,

															(String) hmConstants.get(FILE_DATE_FORMAT));

													shmValue = sdf.format(dtValue);

												}// End Bug 13506280 INVALID DATE PARSING DURING SAP HRMS USER RECON

											}

										} catch (Exception e) {

											logger.error(className, sMethodName,

													"Error in Parsing the " + "attribute field " + shmKey);

											//Start: BUG 10134695 - ATTRIBUTES WITH SUBTYPES ARE NOT PARSED WHEN END-OF-LIN

											logger.debug(className, sMethodName, 

													"File does not contain values in position " + iStartPos + ":" + iEndPos);

											logger.debug(className, sMethodName, 

													"Getting truncated value based on length");



										// Bug 23482998 - Changed the condition from iStartPos to iStartPos-1
										if(iStartPos-1 < sStrLine.length() && iEndPos > sStrLine.length()){

											shmValue = sStrLine.substring(iStartPos - 1, sStrLine.length()).trim();
                                                                                 }

											if (oAttrMapBean.getSFieldType().equalsIgnoreCase(

													(String) hmConstants.get(DATE_FIELD_TYPE))) {

												Date dtValue = dateUtil.returnDate(shmValue, sDateFormat);

												shmValue = sdf.format(dtValue);

											}

											//End: BUG 10134695 - ATTRIBUTES WITH SUBTYPES ARE NOT PARSED WHEN END-OF-LIN



										}

										if (isFutureDatedSegment) {

											if(hmDeferredEvents.get(dtStartDate)!= null){

												hmFutureDatedEvents = hmDeferredEvents.get(dtStartDate);

											}else{

												hmFutureDatedEvents = new HashMap<String, String>();

											}

											logger.debug(className, sMethodName, "Adding into deferred event map");

											//START:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

											if (isValid

													&& sSegmentName.equalsIgnoreCase((String) hmConfig

															.get(GROUP_SEGMENT))) {

												String sEmpGroup = getFieldValue(sStrLine, sEmployeeGroup);

												String sEmpSubGroup = getFieldValue(sStrLine, sEmployeeSubGroup);

												String sGroup_SubGroup = sEmpGroup + '~' + sEmpSubGroup;

												hmFutureDatedEvents.put((String) hmConstants.get(EMPLOYEE_GROUP),

														sEmpGroup);

												hmFutureDatedEvents.put((String) hmConstants.get(EMPLOYEE_SUB_GROUP),

														sEmpSubGroup);

												if (hmEmployeeType.containsKey(sGroup_SubGroup)) {

													hmFutureDatedEvents.put(sEmployeeType, (String) hmEmployeeType

															.get(sGroup_SubGroup));

												} 

											}

											//END:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES

											hmFutureDatedEvents.put(shmKey, shmValue);

											hmDeferredEvents.put(dtStartDate, hmFutureDatedEvents);

										} else {

											logger.debug(className, sMethodName, "Adding into current event map");

											//START:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

											if (isValid

													&& sSegmentName.equalsIgnoreCase((String) hmConfig

															.get(GROUP_SEGMENT))) {

												String sEmpGroup = getFieldValue(sStrLine, sEmployeeGroup);

												String sEmpSubGroup = getFieldValue(sStrLine, sEmployeeSubGroup);

												String sGroup_SubGroup = sEmpGroup + '~' + sEmpSubGroup;

												hmCurrDatedEvents.put((String) hmConstants.get(EMPLOYEE_GROUP),

														sEmpGroup);

												hmCurrDatedEvents.put((String) hmConstants.get(EMPLOYEE_SUB_GROUP),

														sEmpSubGroup);

												if (hmEmployeeType.containsKey(sGroup_SubGroup)) {

													hmDefaultAttrs.put(sEmployeeType, (String) hmEmployeeType

															.get(sGroup_SubGroup));

												} 

											}

											//END:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES

											hmCurrDatedEvents.put(shmKey, shmValue);

										}

									}

								}

								/*

								 * Future Dated processing of recon events.Putting Personnel

								 * Number got from SAP in hmFutureDatedEvents as it is required

								 * for matching rules

								 */

								if (isFutureDatedSegment) {

									hmFutureDatedEvents.put(sSAPPersonnelNumber, sUserIDFromSAP);

									//START:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

									//hmFutureDatedEvents = doTrasformation(hmFutureDatedEvents, hmConstants, oUtil, hmConfig);

									logger.debug(className, sMethodName, sSegmentName + " attributes are future dated " + dtStartDate);

									if(doValidation(hmFutureDatedEvents, hmConstants, oUtil, hmConfig))

										hmDeferredEvents.put(dtStartDate, hmFutureDatedEvents);

									//END:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES

									/*java.sql.Date sqlDate = new java.sql.Date(dtStartDate.getTime());

								oUtil.createFutureTrustedReconEvent(sObjectName,

										hmFutureDatedEvents, sUserIDFromSAP, sqlDate);

								hmFutureDatedEvents.clear();*/

								}

							}

						}

					}

				}



				/*

				 * Creating reconciliation event for last user record present in the file

				 */

				if (isValid && !SAPHRMSUserRecon.isReconStopped) {

					hmCurrDatedEvents.putAll(hmDefaultAttrs);



					logger.debug(className, sMethodName, "hmReconDetails " + hmCurrDatedEvents);

					if(hmCurrDatedEvents!= null && hmCurrDatedEvents.size() > 0){

						//START:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

						//hmCurrDatedEvents = doTrasformation(hmCurrDatedEvents, hmConstants, oUtil, hmConfig);

						//if(doValidation(hmCurrDatedEvents, hmConstants, oUtil, hmConfig))
						
						//*********************New API Uncomment after implementation****************************************//
						LinkedHashMap<String, HashMap>  eventsData = reconcileUser(hmCurrDatedEvents, hmConstants, oUtil, hmConfig, isBulkRecon);
						if(eventsData.size()>0){
							if(eventsData.keySet().contains(FUTURE)){
								deferredEventList.add(new HashMap<Date, HashMap>(eventsData.get(FUTURE)));
							}else{
							logger.debug(className, sMethodName, "Personnel Number :: "+hmCurrDatedEvents.get("Personnel Number"));
							logger.debug(className, sMethodName, "Current events " + hmCurrDatedEvents);
							currentDatedEventList.add(new HashMap<String, String>(eventsData.get(CURRENT)));
							hmCurrDatedEvents.clear();
							}
						}
						//*********************New API Uncomment*************************************************************//

					}//END:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES

					logger.debug(className, sMethodName, "hmDeferredEvents " + hmDeferredEvents);

					if(hmDeferredEvents!= null && hmDeferredEvents.size() > 0){
						//*********************New API Uncomment*************************************************************//
						LinkedHashMap<String, HashMap> eventsData=createDeferredEvents(hmDeferredEvents, sUserIDFromSAP, hmConstants, oUtil, hmConfig, isBulkRecon);
						if(eventsData.size()>0){								
							HashMap hmDeferredEventsNew = eventsData.get(FUTURE);
							logger.debug(className, sMethodName, "Deferred events " + hmDeferredEvents.get("End date"));
							Iterator<HashMap<Date, HashMap<String, String>>> it = hmDeferredEventsNew.entrySet().iterator();
							while (it.hasNext()) {
								Map.Entry pair = (Map.Entry)it.next();
								HashMap<Date,HashMap> deferredMap = new HashMap<Date,HashMap>();
								deferredMap.put((Date)pair.getKey(), (HashMap)pair.getValue());
								deferredEventList.add(deferredMap);	
							}
						}
						//*********************New API *********************************************************************//
						//Start: Bug 17796698- SAP-ER connector: duplication of deferred events
						hmDeferredEvents.clear();
						//End: Bug 17796698- SAP-ER connector: duplication of deferred events 
					}

				}
				//*********************************New API******************************************************//
				logger.debug(className, sMethodName, "size of currentDatedEventList " + currentDatedEventList.size());
				logger.debug(className, sMethodName, "currentDatedEventList " + currentDatedEventList);
				logger.debug(className, sMethodName, "deferredDatedEventList " + deferredEventList);
				if(currentDatedEventList.size()>0){
					processEvents(CURRENT, currentDatedEventList);
				}
				if(deferredEventList.size()>0){
					processEvents(FUTURE, deferredEventList);						
				}
				//*********************************New API******************************************************//

			}
		} catch (FileNotFoundException e) {

			disconnectReaderContext();

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

			throw new ConnectorException(e);

		} catch (IOException e) {

			disconnectReaderContext();

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

			throw new ConnectorException(e);

		} catch (tcAPIException e) {

			disconnectReaderContext();

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

			throw new ConnectorException(e);

		} catch (ConnectorException e) {

			disconnectReaderContext();

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

			throw e;

		}catch (Exception e) {

			disconnectReaderContext();

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

			throw new ConnectorException(e);

		} finally {
			try {
				if(br != null){

					br.close();
				}
				if(fileReaderXml != null){
					fileReaderXml.close();
				}
			}catch (IOException e) {
				disconnectReaderContext();

				logger.error(className, sMethodName, e.getMessage());

				logger.setStackTrace(e, className, sMethodName, e.getMessage());

				throw new ConnectorException(e);
			}
			disconnectReaderContext();
		}

		logger.setMethodFinishLog(className, sMethodName);

	}



	

	/**

	 * Description: Disconnects the LineNumberReader context if found

	 * open

	 * 

	 */

	private void disconnectReaderContext() {

		String sMethodName = "disconnectReaderContext()";

		logger.setMethodStartLog(className, sMethodName);

		// Close any existing LineNumberReader context

		if (lnrdr != null) {

			try {

				lnrdr.close();

			} catch (IOException e) {

				logger.error(className, sMethodName, e.getMessage());

			}

			logger.setMethodFinishLog(className, sMethodName);

		}

	}



	/**

	 * Description: Processes the user attributes to create recon

	 * event is custom recon query and user type query is valid

	 * 

	 * @param hmReconDetails

	 *          Hashmap containing details of user record to be reconciled to OIM

	 * @param hmConstants

	 *          HashMap containing configuration details from the lookup

	 *          "Lookup.SAP.HRMS.Constants"

	 * @param hmFutureDatedEvents

	 *          Hashmap containing details of future dated attributes. These

	 *          attributes are not reconciled to OIM

	 * 

	 * @param oUtil

	 *          Util that innitializes all reconciliation interfaces

	 * 

	 * 

	 * @throws ConnectorException

	 */

	private LinkedHashMap<String,HashMap> reconcileUser(HashMap hmReconDetails,

			HashMap hmConstants, OIMUtil oUtil, HashMap hmConfig, boolean isBulkRecon) throws ConnectorException {

		String sMethodName = "reconcileUser()";

		logger.setMethodStartLog(className, sMethodName);
		LinkedHashMap<String, HashMap> eventsData = new LinkedHashMap<String, HashMap>();
		//HashMap deferredEventsData  = new HashMap();
		try {

			boolean isUserExists = false;

			hmReconDetails.put(sHRMSCreated, (String) hmConstants.get(ONE));

			String status = (String) hmReconDetails.get(sStatus);



			// Check is user exists in OIM based on SAP Personnel Number

			// and set isUserExists to true if it does exist in OIM



			String sSAPUserID = (String) hmReconDetails.get(sSAPPersonnelNumber);

			HashMap<String, String> hmUserID = new HashMap<String, String>();

			hmUserID.put(sUDFPersonnelNo, sSAPUserID);

			tcResultSet userResultSet = oUtil.getUserAPI().findUsers(hmUserID);

			if (userResultSet != null && userResultSet.getRowCount() > 0) {

				isUserExists = true;

			}



			/*

			 * Get manager name from hmManagerHierarchy by passing org name got from

			 * IDoc. If the manager is NOT same as its Personel Number, then it should

			 * be considered as Manager ID. If manager is same as Personel Number,

			 * then from hmOrgHierarchy, parent org should be taken and manager of

			 * that parent org should be taken from hmManagerHierarchy should be

			 * considered as Manager ID.

			 * 

			 */

			if(((String)hmConfig.get(Get_MANAGER_ID)).equalsIgnoreCase(YES))

			{

				String sOrgUnit = (String) hmConstants.get(ORG_UNIT);

				String sMnagerID=(String)hmConstants.get(MANAGER_ID);

				String sManager = (String) hmConstants.get(MANAGER);

				if (hmReconDetails.containsKey(sOrgUnit)) {

					String sOrgName = (String) hmReconDetails.get(sOrgUnit);

					// Populate Manager Name to Manager ID and Manager fields of

					// RO
					//Start: Bug 17018912
					if (hmManagerHierarchy.containsKey(sOrgName)) {

						String sManagerName = oSAPUtil.getManagerIDFromOrg(

								sOrgName, hmConstants, sSAPUserID,

								oUtil, hmOrgHierarchy, hmManagerHierarchy);

						hmReconDetails.put(sMnagerID, sManagerName);

						hmReconDetails.put(sManager, sManagerName);

					} else {

						hmReconDetails.put(sMnagerID, null);

						hmReconDetails.put(sManager, null);

					}//End: Bug 17018912

				}

			}

			

			/*

			 * Validates if the custom recon query is valid for the user record

			 */

			if (!stringUtil.isEmpty(sCustomQuery)) {

				isValid = executeCustomQuery(hmReconDetails, userResultSet);

				logger.debug(className, sMethodName, "Custom recon query valid? " + isValid);

			}

			/*

			 * Validates if the user type query is valid for the user record

			 */



			if (isValid && !stringUtil.isEmpty(sEmployeeTypeQuery)) {

				isValid = executeEmployeeTypeQuery(hmReconDetails, userResultSet,

						hmConstants);

				logger.debug(className, sMethodName, "Employee type query valid? " + isValid);

			}



			// Get the User ID from OIM.This is required since there might be scenario

			// where User ID is updated in OIM and put that value in HashMap

			if (isValid) {

				if (isUserExists) {

					userResultSet.goToRow(0);

					logger.debug(className, sMethodName, "Getting the User ID from OIM");

					String sNewUserID = userResultSet.getStringValue((String) hmConstants

							.get(USER_ID_OIM));

					hmReconDetails.put(sUserLoginField, sNewUserID);

				} else {

					hmReconDetails.put(sUserLoginField, sSAPUserID);

				}



				// Remove any leading zeroes existing in User ID

				if (!stringUtil.isEmpty(sRemoveZero)

						&& sRemoveZero.equalsIgnoreCase((String) hmConstants.get(YES))

						&& numberUtil.isNumeric((String)hmReconDetails.get(sUserLoginField))) {

					String sNewUserID = Integer.toString(Integer.parseInt((String)hmReconDetails

							.get(sUserLoginField)));

					hmReconDetails.put(sUserLoginField, sNewUserID);

				}



				// If Reconcile First Time Disabled user is set to no,then user is not

				// brought to OIM by making isValid to false

				if (!stringUtil.isEmpty(status)

						&& status.equalsIgnoreCase(sDisabledStatus)

						&& sReconcileFirstTimeDisabledUsers

								.equalsIgnoreCase((String) hmConstants.get(NO))) {

					if (!isUserExists) {

						isValid = false;

						logger.info(className, sMethodName, "Disabled User " + sSAPUserID

								+ " is not being reconciled since Reconcile First Time "

								+ "Disabled Users value is set to 'no' ");

					}

				}

				String sOIMStartDtField = (String)hmConfig.get(OIM_START_DATE);

				// Remove the status field if its future dated event

			

			//Start: Bug BUG 14384664 - SAP HR RE-HIRE EVENT IS NOT ENABLING THE USER - Commented Below Code	

			/*	if ((sOIMStartDtField.equalsIgnoreCase(NONE) && hmReconDetails.containsKey(sUserProvisioningDate))||

						!sOIMStartDtField.equalsIgnoreCase(NONE)){

					hmReconDetails.remove(sStatus);

				}

			*/	

			//End: Bug BUG 14384664 



				//Start: BUG 10434626-TRANSFORAMTION AND VALIDATION NOT SUPPORTED



				/*

				 * If transform mapping is required,then call

				 * transformSingleOrMultivaluedData() to transform the data

				 */

				if (((String)hmConstants.get(YES)).equalsIgnoreCase((String) hmConfig.get(USE_TRANSFORM_MAPPING))) {

					hmReconDetails = oUtil.transformSingleOrMultivaluedData(

							hmReconDetails, null, (String) hmConfig

									.get(TRANSFORM_LOOKUP));

				}

				

				/*

				 * If validation is required,then call

				 * validateSingleOrMultivaluedData() to transform the data

				 */

				if (((String)hmConstants.get(YES)).equalsIgnoreCase(

						(String) hmConfig.get(USE_RECON_VALIDATION))) {

					isValid = oUtil.validateSingleOrMultivaluedData(

							hmReconDetails, null, (String) hmConfig

									.get(VALIDATE_LOOKUP));

				}

				//End: BUG 10434626-TRANSFORAMTION AND VALIDATION NOT SUPPORTED



				if (isValid) {

					String sPersonnelNumber = (String) hmReconDetails

					.get(sUserLoginField);

					if (hmReconDetails.containsKey(sUserProvisioningDate)) {

						

						//If create deferred event is set to 'Yes' and 

						//has provisioning date

						String sStartDate=(String)hmReconDetails.get(sUserProvisioningDate);

						Date dTodayDate = new Date();

						Date dStartDate;

						if(!stringUtil.isEmpty(sStartDate)){

							DateFormat sdf = new SimpleDateFormat(oUtil.getReconOperAPI()

									.getDefaultDateFormat());

							dStartDate = sdf.parse(sStartDate);

					        

							if (((String)hmConfig.get(CREATE_DEFERRED_EVENT_FOR_FUTURE_DATED_HIRE)).equalsIgnoreCase(YES)
									&& dStartDate.after(dTodayDate)) {

								//Create deferred event if hire date is greater 

								//todays date

								HashMap hmDeferredEvents = new HashMap();

								hmDeferredEvents.put(dStartDate, hmReconDetails);
								
								eventsData =createDeferredEvents (hmDeferredEvents, sPersonnelNumber, hmConstants, oUtil, hmConfig, isBulkRecon);
								hmDeferredEvents.clear();

							} else {

								//Link event if hire date is lesser than todays date
								if(!isBulkRecon){

								oUtil.createTrustedReconEvent(sObjectName, hmReconDetails,

										sPersonnelNumber);
							}else{
								eventsData.put(CURRENT, hmReconDetails);
							}

							}

						}

					} else {

						//If create deferred event is not set
						if(!isBulkRecon){
						oUtil.createTrustedReconEvent(sObjectName, hmReconDetails,

								sPersonnelNumber);
						}else{
							eventsData.put(CURRENT, hmReconDetails);
						}
					}

				}

			}

			logger.setMethodFinishLog(className, sMethodName);

		} catch (tcAPIException e) {

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

			throw new ConnectorException(e);

		} catch (Exception e) {

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

			throw new ConnectorException(e);

		} finally {

		//	hmReconDetails.clear();

		}


		return eventsData; 
	}



	private LinkedHashMap<String, HashMap> createDeferredEvents(HashMap<Date, HashMap> hmDeferredEvents,

			String sUserID, HashMap hmConstants, OIMUtil oUtil, HashMap hmConfig, boolean isBulkRecon) 

			throws ConnectorException {

		String sMethodName = "createDeferredEvents()";

		logger.setMethodStartLog(className, sMethodName);

		boolean isUserExists = false;
		LinkedHashMap<String,HashMap> eventData = new LinkedHashMap<String, HashMap>();
		HashMap<Date,HashMap>deferredEventsData  = new HashMap();
		
		try{

			if(hmDeferredEvents!= null && hmDeferredEvents.size()>0){

				//START:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES

				HashMap<String, String> hmUserID = new HashMap<String, String>();

				hmUserID.put(sUDFPersonnelNo, sUserID);

				tcResultSet userResultSet = oUtil.getUserAPI().findUsers(hmUserID);

				if (userResultSet != null && userResultSet.getRowCount() > 0) {

					isUserExists = true;

				}

				//END:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES

				Iterator itr = hmDeferredEvents.keySet().iterator();

				while(itr.hasNext()){

					Date dtEventDt = (Date)itr.next();

					HashMap hmAttrs = (HashMap)hmDeferredEvents.get(dtEventDt); 

					if(hmAttrs!=null && hmAttrs.size()>0){

						String sManagerID = (String) hmConstants.get(MANAGER_ID);	
						//Added below condtion for bug 15947444 
						if(((String)hmConfig.get(Get_MANAGER_ID)).equalsIgnoreCase(YES)){
							if (hmAttrs.containsKey(sManagerID)) {

								String sOrgName = (String) hmAttrs.get(sManagerID);

								//Populate Manager Name to Manager ID 

								if (hmManagerHierarchy.containsKey(sOrgName)) {

									String sManagerName = oSAPUtil.getManagerIDFromOrg(sOrgName, hmConstants,

											sSAPPersonnelNumber, oUtil, hmOrgHierarchy, hmManagerHierarchy);

									hmAttrs.put(sManagerID, sManagerName);										

								} else {

									hmAttrs.put(sManagerID, null);										

								}
						  }

						}

						//START:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES

						if (isUserExists) {

							userResultSet.goToRow(0);

							logger.debug(className, sMethodName, "Getting the User ID from OIM");

							String sNewUserID = userResultSet.getStringValue((String) hmConstants

									.get(USER_ID_OIM));

							hmAttrs.put(sUserLoginField, sNewUserID);

						} else {

							hmAttrs.put(sUserLoginField, sUserID);

						}

						//END:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES

						java.sql.Date sqlDate = new java.sql.Date(dtEventDt.getTime());

						logger.debug(className, sMethodName, "Creating deferred event for " + dtEventDt);

						//START:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

						hmAttrs = doTrasformation(hmAttrs, hmConstants, oUtil, hmConfig);

						//END:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

						//************************************New API********************************
						if(!isBulkRecon){
						oUtil.createFutureTrustedReconEvent(sObjectName, hmAttrs, sUserID, sqlDate);
						continue;
						}else{
						deferredEventsData.put(dtEventDt, hmAttrs);
						eventData.put(FUTURE, deferredEventsData);
						logger.debug(className, sMethodName, "Deferred event Data " + deferredEventsData);
						}
					}

				}

			}

			

		}catch(Exception e){

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

			throw new ConnectorException(e);

		}

		logger.setMethodFinishLog(className, sMethodName);

		return eventData;

	}



	/**

	 * Description: Parses the IDoc received from SAP and creates reconciliation

	 * events for user records having attributes as mentioned in the attribute

	 * mappings lookup definition

	 * 

	 * @param hmConfig

	 *            HashMap containing configuration details from the

	 *            Lookup.SAP.HRMS.Configuration lookup definition

	 * @param hmConstants

	 *            HashMap containing configuration details from the

	 *            Lookup.SAP.HRMS.Constants lookup definition

	 * @param htTaskAttributes

	 *            Hashtable containing the scheduled task attributes

	 * @param oUtil

	 *            Util that initializes all reconciliation interfaces

	 * @param iDoc

	 *            IDoc to be parsed to get SAP user details

	 * 

	 * @throws ConnectorException

	 */

	public void parse(HashMap hmConfig, HashMap hmConstants,

			Hashtable htTaskAttributes, OIMUtil oUtil, IDocDocument iDoc)

			throws ConnectorException {

		boolean isDel = false;

		// hashmap containing current dated events

		//HashMap<String, String> hmCurrDatedEvents = new HashMap<String, String>();

		// hashmap containing Future dated events

//		HashMap<String, String> hmFutureDatedEvents = new HashMap<String, String>();

//		HashMap<Date, HashMap> hmDeferredEvents = new HashMap<Date, HashMap>();
		//*********************************New API******************************************************//
		LinkedHashMap<String, HashMap> eventsData = new LinkedHashMap<String, HashMap>();
		// CurrentDated Event data list
    	ArrayList<HashMap<String,String>> currentDatedEventList = new ArrayList<HashMap<String,String>>();
    	// FutureDated Event data list
    	ArrayList<HashMap<Date,HashMap>> deferredEventList = new ArrayList<HashMap<Date,HashMap>>();
    	
		boolean isBulkRecon  = isBulkReconSupported();
    	//*********************************New API******************************************************//

		// Added below line for BUG 13538856 - RECON FAILS WITH ORA-01400 IN XL_SP_RECONEVALUATEUSER

		HashMap hmDefaultAttrs = new HashMap();



		String sMethodName = "parse()";

		try {

			logger.setMethodStartLog(className, sMethodName);

			initialiseLookupValues(hmConfig, hmConstants, htTaskAttributes, oUtil);

			// we get the root segment of the iDoc

			IDocSegment jcoRoot = iDoc.getRootSegment();

			//Start: BUG 10312742-CAPABILITY TO TRACK THE IDCO NUMBER IN OIM SAP U

			logger.info("IDOC Number: " + iDoc.getIDocNumber());

			//End: BUG 10312742-CAPABILITY TO TRACK THE IDCO NUMBER IN OIM SAP U

			// OIM default date format is used to insert dates

			DateFormat sdf = new SimpleDateFormat(oUtil.getReconOperAPI()

					.getDefaultDateFormat());

			if (jcoRoot != null) {

				/**

				 * The java object iDoc has a hierachichal structure starting from the

				 * root segment to its child segments.We iterate through this structure

				 * to get the required info from each segment.

				 * 

				 */

				IDocSegment jcoRootChildren[] = jcoRoot.getChildren();

				logger.debug(className, sMethodName, 

						"Children length: " + jcoRootChildren.length);

				// Modified below 3 lines for BUG 13538856 - RECON FAILS WITH ORA-01400 IN XL_SP_RECONEVALUATEUSER 

				hmDefaultAttrs.put(sOrganization, (String) hmConfig.get(sOrganization));

				hmDefaultAttrs.put(sUserType, (String) hmConfig.get(sUserType));

				hmDefaultAttrs.put(sEmployeeType, (String) hmConfig.get(sEmployeeType));



				for (int jcoRootChildCnt = 0; jcoRootChildCnt < jcoRootChildren.length; jcoRootChildCnt++) {

					IDocSegment jcoRootChild = jcoRootChildren[jcoRootChildCnt];

					logger.debug(className, sMethodName, 

							"Segment definition: " + jcoRootChild.getDefinition());
					
					//*********************************New API******************************************************//
					HashMap<String, String> hmCurrDatedEvents = new HashMap<String, String>();
					HashMap<String, String> hmFutureDatedEvents = new HashMap<String, String>();
					HashMap<Date, HashMap> hmDeferredEvents = new HashMap<Date, HashMap>();
					//*********************************New API******************************************************//
					String sUserIDFromSAP = getFieldValue(jcoRootChild, (String) hmConfig

							.get(sUserLoginField));

					if (stringUtil.isEmpty(sUserIDFromSAP)) {

						logger.info(className, sMethodName, "Invalid personnel number");

						continue;

					}

					// Added below line for BUG 13538856 - RECON FAILS WITH ORA-01400 IN XL_SP_RECONEVALUATEUSER 

					hmCurrDatedEvents.putAll(hmDefaultAttrs);

					hmCurrDatedEvents.put(sSAPPersonnelNumber, sUserIDFromSAP);



					isValid = isValidRecord(jcoRootChild, (String) hmConfig

							.get(OBJECT_TYPE));

					if (isValid) {

						/*

						 * If the user is deleted in ER then even in OIM the user should be

						 * deleted

						 */

						isDel = isDeletedUser(jcoRootChild, (String) hmConfig

								.get(DELETE_INDICATOR));

						if (isDel) {

							HashMap<String, String> hmReconDetails = new HashMap<String, String>();

							hmReconDetails.put(sSAPPersonnelNumber, sUserIDFromSAP);

							reconcileDeletedUser(hmReconDetails, hmConstants, oUtil);

						}

						//HashMap hmReconData = new HashMap();

						/*

						 * Create recon event for user

						 */

						if (jcoRootChild != null && jcoRootChild.getNumChildren() > 0

								&& !isDel) {

							processChildSegment(jcoRootChild, hmConstants,

									hmConfig, hmCurrDatedEvents, hmFutureDatedEvents, 

									hmDeferredEvents, sdf, sUserIDFromSAP, oUtil);

							//hmCurrDatedEvents = (HashMap) hmReconData.get(CURRENT);

							if(hmCurrDatedEvents !=null && hmCurrDatedEvents .size()>0){

								logger.debug(className, sMethodName, "Current events " + hmCurrDatedEvents);

								//START:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

								//hmCurrDatedEvents = doTrasformation(hmCurrDatedEvents, hmConstants, oUtil, hmConfig);

								//if(doValidation(hmCurrDatedEvents, hmConstants, oUtil, hmConfig))
									
								//*********************************New API******************************************************//
									//reconcileUser(hmCurrDatedEvents, hmConstants, oUtil, hmConfig);
									eventsData = reconcileUser(hmCurrDatedEvents, hmConstants, oUtil, hmConfig, isBulkRecon);
									if(eventsData.size()>0){
										if(eventsData.keySet().contains(FUTURE)){
											deferredEventList.add(new HashMap<Date, HashMap>(eventsData.get(FUTURE)));
										}else{
										hmCurrDatedEvents=eventsData.get(CURRENT);
										logger.debug(className, sMethodName, "Personnel Number :: "+hmCurrDatedEvents.get("Personnel Number"));
										logger.debug(className, sMethodName, "Current events " + hmCurrDatedEvents);
										currentDatedEventList.add(hmCurrDatedEvents);										
									}
									}
								//*********************************New API******************************************************//
							}

							//End:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES

							

							//HashMap hmDeferredEvents = (HashMap) hmReconData.get(FUTURE);

							if(hmDeferredEvents!=null && hmDeferredEvents.size()>0){

								logger.debug(className, sMethodName, "Deferred events " + hmDeferredEvents);
								//*********************************New API******************************************************//
								eventsData = createDeferredEvents(hmDeferredEvents, sUserIDFromSAP, hmConstants, oUtil, hmConfig, isBulkRecon);
								if(eventsData.size()>0){								
									hmDeferredEvents=eventsData.get(FUTURE);
									logger.debug(className, sMethodName, "Deferred events " + hmDeferredEvents.get("End date"));
									deferredEventList.add(hmDeferredEvents);
								}
								//*********************************New API******************************************************//
								//Start: Bug 17796698- SAP-ER connector: duplication of deferred events
								//hmDeferredEvents.clear();
								//End: Bug 17796698- SAP-ER connector: duplication of deferred events 
							}

						}

					}else{

						logger.debug(className, sMethodName, "It is not a valid object type");

					}
				}
				//*********************************New API******************************************************//
				logger.debug(className, sMethodName, "size of currentDatedEventList " + currentDatedEventList.size());
				logger.debug(className, sMethodName, "currentDatedEventList " + currentDatedEventList);
				logger.debug(className, sMethodName, "deferredDatedEventList " + deferredEventList);
				if(currentDatedEventList.size()>0){
					processEvents(CURRENT, currentDatedEventList);
				}
				if(deferredEventList.size()>0){
					processEvents(FUTURE, deferredEventList);						
				}
				//*********************************New API******************************************************//
			}

		} catch (Exception e) {

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

			throw new ConnectorException(e);

		}

		logger.setMethodFinishLog(className, sMethodName);

	}
	
	/**
	 *  Create reconciliation events
	 * @param event
	 * @param eventDataList
	 */
	public void processEvents(String event, ArrayList eventDataList){
		int eventIndex =0;
	    InputData inputData =null;
		InputData[] eventsData = new InputData[eventDataList.size()];
	    DateFormat df;
	    Date actionDate = null;
	    
		try {
			df = new SimpleDateFormat(reconOps.getDefaultDateFormat());
			BatchAttributes batchAttr= new BatchAttributes(sObjectName, reconOps.getDefaultDateFormat(),true);
		    Iterator itr = eventDataList.iterator();
		    while(itr.hasNext()){
		    	if(event.equals(CURRENT)){
		    		actionDate = df.parse(df.format(new Date()));					
			    	inputData = new InputData((HashMap<String,String>) itr.next(), ChangeType.CHANGELOG, actionDate);
			    	eventsData[eventIndex]= inputData;
		    	}else if(event.equals(FUTURE)){		    		
		    		HashMap<Date, HashMap> tmp = (HashMap<Date,HashMap>) itr.next();
		    		Iterator itr1 = tmp.keySet().iterator();					
					while(itr1.hasNext()){
						actionDate = (Date)itr1.next();
						HashMap deferredReconData = (HashMap)tmp.get(actionDate);
						inputData = new InputData(deferredReconData, ChangeType.CHANGELOG, actionDate);
				    	eventsData[eventIndex]= inputData;
					}
		    	}
		    	eventIndex ++;			   
		    }
			ReconciliationResult  reconResult = reconOps.createReconciliationEvents(batchAttr,eventsData);
		} catch (tcAPIException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}



	/**

	 * Description: Parses the IDoc received from SAP. It recursively

	 * iterates through the hierarchy of the idoc and seperates the future dated

	 * and current dated users/attributes. Segments from the IDoc recieved by

	 * parse method is sent to another method to get values present in attribute

	 * map.

	 * 

	 * @param jcoRootChild

	 *          Segments of the IDoc which are iterated through

	 * @param hmConfig

	 *          Hashtable containing configuration details from the lookup

	 *          "Lookup.SAP.HRMS.Configuration"

	 * @param hmCurrDatedEvents

	 *          HashMap holding attributes which are current dated

	 * @param hmFutureDatedEvents

	 *          HashMap holding attributes which are future dated

	 * @param dfOIM

	 *          OIM date format used to format dates inserted in OIM

	 * @param sPersonnelNumber

	 *          The Personnel number of the User

	 * @param oUtil

	 *          Util that innitializes all reconciliation interfaces

	 * @throws ConnectorException

	 */

	private HashMap processChildSegment(IDocSegment jcoRootChild,

			HashMap hmConstants, HashMap hmConfig,

			HashMap<String, String> hmCurrDatedEvents,

			HashMap<String, String> hmFutureDatedEvents, 

			HashMap<Date, HashMap> hmDeferredEvents, DateFormat dfOIM,

			String sPersonnelNumber, OIMUtil oUtil) throws ConnectorException {

		HashMap<String, HashMap> hmReturnMap = new HashMap<String, HashMap>();

		String sStartDt = null;

		String sEndDt = null;

		

		Date start_date = null;

		Date end_date = null;

		String sEventID;

		boolean isFutureDatedSegment = false;

		String sMethodName = "processChildSegment()";

		try {

			String sSegmentDefinition = jcoRootChild.getDefinition();

			//START: Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

			sStartDt = getFieldValue(jcoRootChild, hmConfig.get(EVENT_BEGIN_DATE)

					.toString());

			//END: Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

			logger.setMethodStartLog(className, sMethodName);

			logger.debug(className, sMethodName, 

					"Segment definition: " + sSegmentDefinition);



			if (sSegmentDefinition.equalsIgnoreCase(

					(String) hmConfig.get(ACTIONS_EVENT))) {

				

				sEventID = getFieldValue(jcoRootChild, hmConfig.get(EVENT).toString());

				//START: Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

				/*sStartDt = getFieldValue(jcoRootChild, hmConfig.get(EVENT_BEGIN_DATE)

						.toString());*/

				//END: Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

				sEndDt = getFieldValue(jcoRootChild, hmConfig.get(EVENT_END_DATE)

						.toString());



				/*

				 * Determines if user record is future dated or not by comparing the

				 * start date value of Infotype segment to the current date.Sets the

				 * isFutureDatedEvent flag to true if start date is greater than the

				 * current date

				 */

				

				populateDates (sStartDt, sEndDt, sEventID, 

						hmConstants, hmConfig, hmCurrDatedEvents, 

						hmFutureDatedEvents, hmDeferredEvents, dfOIM,

						sPersonnelNumber, oUtil, (String) hmConstants

						.get(LISTENER_DATE_FORMAT));

			}

			/*

			 * Future dated checks should be on each infotype and not root segment and

			 * segments not in sttribute mapping

			 */

			//Start Bug : : 19319985 :: Changed Elseif to if
			if (!sSegmentDefinition.equalsIgnoreCase(

					(String) hmConfig.get(ROOT_SEGMENT))) {

			// End Bug :: 19319985
			isFutureDatedSegment = false;

				Set keySet = htAttrMap.keySet();

				if (keySet.contains(sSegmentDefinition )) {

					sStartDt = getFieldValue(jcoRootChild, hmConfig.get(EVENT_BEGIN_DATE)

							.toString());

					if(!stringUtil.isEmpty(sStartDt)){

						start_date = dateUtil.returnDate(sStartDt, (String) hmConstants

								.get(LISTENER_DATE_FORMAT));

						Date dtToday = new Date();

					//Start: BUG 9523713-DESIRE ABILITY TO CONFIGURE FUTURE DATED PROCESSING

						if (start_date != null && start_date.after(dtToday)

								&& !start_date.equals(dtHireDate) &&

								isFutureDatedEventHandlingEnabled.equalsIgnoreCase((String) hmConstants.get(YES))) {

							isFutureDatedSegment = true;

						    logger.info(className, sMethodName,

									"The segment " + sSegmentDefinition  + " is future dated ");

						}

					//End: BUG 9523713-DESIRE ABILITY TO CONFIGURE FUTURE DATED PROCESSING

					}					

				}

			}

			

			if (htAttrMap.keySet().contains(sSegmentDefinition)) {

			HashMap hmMap = processRecord(jcoRootChild, htAttrMap,

					hmConstants, dfOIM);

			logger.debug(className, sMethodName, sSegmentDefinition  + ":" + hmMap);

			if (isFutureDatedSegment) {

				/*

				 * If user has attributes that are future dated for infotype we have

				 * to create recon events for each infotype specifying the date on which

				 * these attributes are to be linked to the user

				 */

				

				if(hmMap != null && hmMap.size() >0){

					if(hmDeferredEvents.get(start_date)!= null){

						//Start: BUG 11059390 - FUTURE DATED HIRE EVENTS RECON FAILING IF LOOKUP VALUES ARE EMPTY

						hmFutureDatedEvents = hmDeferredEvents.get(start_date);

						//End: BUG 11059390 - FUTURE DATED HIRE EVENTS RECON FAILING IF LOOKUP VALUES ARE EMPTY

					}else{

						hmFutureDatedEvents = new HashMap<String, String>();

					}

					hmFutureDatedEvents.putAll(hmMap);

					//START: Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

					if (sSegmentDefinition .equalsIgnoreCase(

							(String) hmConfig.get(GROUP_SEGMENT))) {

/*						String sEmpGroup = getFieldValue(jcoRootChild, (String) hmConfig

								.get((String) hmConstants.get(EMPLOYEE_GROUP)));

						String sEmpSubGroup = getFieldValue(jcoRootChild, (String) hmConfig

								.get((String) hmConstants.get(EMPLOYEE_SUB_GROUP)));

						String sGroup_SubGroup = sEmpGroup + '~' + sEmpSubGroup;



						hmFutureDatedEvents.put((String) hmConstants.get(EMPLOYEE_GROUP),

								sEmpGroup);

						hmFutureDatedEvents.put((String) hmConstants.get(EMPLOYEE_SUB_GROUP),

								sEmpSubGroup);



						if (hmEmployeeType.containsKey(sGroup_SubGroup)) {

							hmFutureDatedEvents.remove(sEmployeeType);

							hmFutureDatedEvents.put(sEmployeeType, (String) hmEmployeeType

									.get(sGroup_SubGroup));

						} */

						hmFutureDatedEvents.putAll(getGroupSegmentFields(jcoRootChild, hmConfig, hmConstants));

						logger.debug(className, sMethodName, "Values set for attributes in group segment");

					}

					//END: Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES

					hmFutureDatedEvents.put(sSAPPersonnelNumber, sPersonnelNumber);

					logger.debug(className, sMethodName, "Adding to deferred event map");

					//START: Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

					//hmFutureDatedEvents = doTrasformation(hmFutureDatedEvents, hmConstants, oUtil, hmConfig);

					if(doValidation(hmFutureDatedEvents, hmConstants, oUtil, hmConfig))

						hmDeferredEvents.put(start_date, hmFutureDatedEvents);

				}//END: Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES

			} else {

				logger.debug(className, sMethodName, "Adding to current event map");

				hmCurrDatedEvents.putAll(hmMap);

			}

			}

			/*

			 * If lookup recon for employee type is done,then get the decode value for

			 * Emp Group~Emp Sub Group combination.The obtained decode value is the

			 * value for key "Employee Type" in the hashmap. If lookup is not

			 * populated,then the default value as mentioned in lookup table

			 * "Lookup.SAP.HRMS.Configuration" is the value for key "Employee Type" in

			 * the hashmap.

			 */



			if (sSegmentDefinition .equalsIgnoreCase(

					(String) hmConfig.get(GROUP_SEGMENT))) {

				if(hmDeferredEvents.get(start_date)!= null){

					//Start: BUG 11059390 - FUTURE DATED HIRE EVENTS RECON FAILING IF LOOKUP VALUES ARE EMPTY

					hmFutureDatedEvents = hmDeferredEvents.get(start_date);

					//End: BUG 11059390 - FUTURE DATED HIRE EVENTS RECON FAILING IF LOOKUP VALUES ARE EMPTY

				}else{

					hmFutureDatedEvents = new HashMap<String, String>();

				}

				/*String sEmpGroup = getFieldValue(jcoRootChild, (String) hmConfig

						.get((String) hmConstants.get(EMPLOYEE_GROUP)));

				String sEmpSubGroup = getFieldValue(jcoRootChild, (String) hmConfig

						.get((String) hmConstants.get(EMPLOYEE_SUB_GROUP)));

				String sGroup_SubGroup = sEmpGroup + '~' + sEmpSubGroup;



				hmCurrDatedEvents.put((String) hmConstants.get(EMPLOYEE_GROUP),

						sEmpGroup);

				hmCurrDatedEvents.put((String) hmConstants.get(EMPLOYEE_SUB_GROUP),

						sEmpSubGroup);



				if (hmEmployeeType.containsKey(sGroup_SubGroup)) {

					hmCurrDatedEvents.remove(sEmployeeType);

					hmCurrDatedEvents.put(sEmployeeType, (String) hmEmployeeType

							.get(sGroup_SubGroup));

				} */

				if(isFutureDatedSegment)

					hmFutureDatedEvents.putAll(getGroupSegmentFields(jcoRootChild, hmConfig, hmConstants));

				else

					hmCurrDatedEvents.putAll(getGroupSegmentFields(jcoRootChild, hmConfig, hmConstants));

				logger.debug(className, sMethodName, "Values set for attributes in group segment -"+ hmCurrDatedEvents);

			}

			// end of adding default attributes



			IDocSegment children[] = jcoRootChild.getChildren();

			for (int iCount = 0; iCount < children.length; iCount++) {

				IDocSegment childSegment = children[iCount];

				if (childSegment == null)

					continue;

				processChildSegment(childSegment, hmConstants, hmConfig,

						hmCurrDatedEvents, hmFutureDatedEvents, hmDeferredEvents, 

						dfOIM, sPersonnelNumber, oUtil);

			}

			//hmReturnMap.put(CURRENT, hmCurrDatedEvents);

			//hmReturnMap.put(FUTURE,hmDeferredEvents);

		} catch (Exception e) {

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

			throw new ConnectorException(e);

		}

		logger.setMethodFinishLog(className, sMethodName);

		return hmReturnMap;

	}



	/**

	 * Populate start and end date for event received as IDoc.

	 * 

	 * @param sStartDt Start date of events

	 * @param sEndDt End date of events

	 * @param sEventID 

	 * 			  Event Id and it might be hire, rehire, terminate or etc.,

	 * @param hmConstants

	 *            HashMap containing configuration details from the

	 *            Lookup.SAP.HRMS.Constants lookup definition

	 * @param hmConfig 

	 *            HashMap containing configuration details from the

	 *            Lookup.SAP.HRMS.Configuration lookup definition

	 * @param hmCurrDatedEvents  

	 * 			  Holds current dated events record details

	 * @param hmFutureDatedEvents 

	 * 			  Holds future dated events record details

	 * @param hmDeferredEvents 

	 * 			  Holds Deferred dated events record details

	 * @param dfOIM OIM Date format

	 * @param sPersonnelNumber Personnel Number

	 * @param oUtil OIMUtil object

	 * @param sDateFormat 

	 * 			  Contains the data format type either FILE port or TRFC port.

	 */

	private void populateDates(String sStartDt, String sEndDt, String sEventID, 

			HashMap hmConstants, HashMap hmConfig,

			HashMap<String, String> hmCurrDatedEvents,

			HashMap<String, String> hmFutureDatedEvents, 

			HashMap<Date, HashMap> hmDeferredEvents, DateFormat dfOIM,

			String sPersonnelNumber, OIMUtil oUtil, String sDateFormat) {



		String sMethodName = "populateDates";

		//String sStartDate="";

		//String sEndDate="";

		//String sDateFormat = "";

		Date start_date = null;

		Date end_date = null;

		boolean isFutureDatedEvent = false;

		HashMap hmFutureDatedReHireEvents = new HashMap();

		logger.setMethodStartLog(className, sMethodName);

		try {

			//Parsing start date

			if(!stringUtil.isEmpty(sStartDt)){

				start_date = dateUtil.returnDate(sStartDt, sDateFormat);

				sStartDt = dfOIM.format(start_date);

			}



			//Parsing End date

			if(!stringUtil.isEmpty(sEndDt)){

				end_date = dateUtil.returnDate(sEndDt, sDateFormat);

				sEndDt = dfOIM.format(end_date);

			}



			//Identify whether the event is future dated or not. 

			//If future dated then set isFutureDatedEvent to true otherwise, false.

			Date dtToday = new Date();

			//Start: BUG 9523713-DESIRE ABILITY TO CONFIGURE FUTURE DATED PROCESSING

			if (start_date!=null && start_date.after(dtToday)&& 

					isFutureDatedEventHandlingEnabled.equalsIgnoreCase((String) hmConstants.get(YES))) {

				isFutureDatedEvent = true;

			}

			

			//End: BUG 9523713-DESIRE ABILITY TO CONFIGURE FUTURE DATED PROCESSING

			/*

			 * If event id received from IDoc is found in

			 * "Lookup.SAP.HRMS.HireEvents" then it is hire event and if user record

			 * is future dated, then start date will be populated in User

			 * provisioning date attribute of OIM user form

			 */

			

			String sOIMStartDate = (String)hmConfig.get(OIM_START_DATE);

			String sOIMEndDate = (String)hmConfig.get(OIM_END_DATE);

	

			if (sOIMStartDate.equalsIgnoreCase(NONE) && sOIMStartDate.equalsIgnoreCase(NONE)) {

				hmCurrDatedEvents.put(sStatus, sActiveStatus);	

			}

					

			if (stringUtil.isEmpty(sStartDt)||

					stringUtil.isEmpty(sOIMStartDate) ||

					stringUtil.isEmpty(sEndDt)||

					stringUtil.isEmpty(sOIMEndDate)) {

				logger.debug(className, sMethodName, 

						"Start Dt: " + sStartDt+ " End Dt: " +  sEndDt);	

				logger.debug(className, sMethodName, 

						"OIM start date or end date field configuration / " +

						"Start date or End date value is null");

				return;

			}

			

			if (hmHireEvent.containsKey(sEventID)) {

				logger.debug(className, sMethodName, 

						"Hire event: " + sEventID + "--" +  sStartDt+ "--" +  sEndDt);	

				/*

				 * If event id received from IDoc is found in

				 * "Lookup.SAP.HRMS.HireEvents" then it is Hire event

				 */

				dtHireDate = start_date;

				//Start: BUG 10229768 - STARTING AND TERMINATION DATE ARE NOT COMING

				if (!sOIMStartDate.equalsIgnoreCase(NONE)) {

					hmCurrDatedEvents.put(sOIMStartDate, sStartDt);

				}

				if (!sOIMEndDate.equalsIgnoreCase(NONE)) {

					hmCurrDatedEvents.put(sOIMEndDate, sEndDt);

				}

				//End: BUG 10229768 - STARTING AND TERMINATION DATE ARE NOT COMING

				hmCurrDatedEvents.put(sStatus, sActiveStatus);

				if(isFutureDatedEvent){

					String dtnewStartDate = dfOIM.format(start_date);

					logger.info(className, sMethodName, "The user is future dated");

					logger.info(className, sMethodName, "Setting user provisioning date to " + dtnewStartDate);

					hmCurrDatedEvents.put(sUserProvisioningDate, dtnewStartDate);

				} 

			} else if (hmRehireEvent.containsKey(sEventID)) {

				logger.debug(className, sMethodName, 

						"Re-hire event: " + sEventID + "--" +  sStartDt+ "--" +  sEndDt);	

				/*

				 * If event id received from IDoc is found in

				 * "Lookup.SAP.HRMS.RehireEvents" then it is Rehire event

				 */

				if (!isFutureDatedEvent) {

					if (!sOIMStartDate.equalsIgnoreCase(NONE)) {

						hmCurrDatedEvents.put(sOIMStartDate, sStartDt);

					}

					if (!sOIMEndDate.equalsIgnoreCase(NONE)) {

						hmCurrDatedEvents.put(sOIMEndDate, sEndDt);

					}					

					hmCurrDatedEvents.put(sStatus, sActiveStatus);

				} else {

					//Adding Personal number, start date and end date 

					//into future dated events map for rehired user

					//START: BUG 13716729 - FUTURE DATED HIRE, TERMINATE, REHIRE NOT FUNCTIONING CORRECTLY

					if(hmDeferredEvents.get(start_date)!= null){

						hmFutureDatedEvents = hmDeferredEvents.get(start_date);

					}else{

						hmFutureDatedEvents = new HashMap<String, String>();

					}

					//END: BUG 13716729

					hmFutureDatedEvents.put(sSAPPersonnelNumber, sPersonnelNumber);

					//START: BUG 13716729 - FUTURE DATED HIRE, TERMINATE, REHIRE NOT FUNCTIONING CORRECTLY

					hmFutureDatedEvents.put(sStatus, sActiveStatus);

					//END: BUG 13716729

					if (!sOIMStartDate.equalsIgnoreCase(NONE)) {

						hmFutureDatedEvents.put(sOIMStartDate, sStartDt);

					} else {

						hmFutureDatedEvents.put(sUserProvisioningDate, sStartDt);

					}

					if (!sOIMEndDate.equalsIgnoreCase(NONE)) {

						hmFutureDatedEvents.put(sOIMEndDate, sEndDt);

					}

					//START: BUG 13716729 - FUTURE DATED HIRE, TERMINATE, REHIRE NOT FUNCTIONING CORRECTLY

					/*java.sql.Date sqlDate = new java.sql.Date(start_date.getTime());

					logger.info(className, sMethodName, "Future dated Rehire event. Creating deferred event for date " + sqlDate);

					oUtil.createFutureTrustedReconEvent(sObjectName,

							hmFutureDatedEvents, sPersonnelNumber, sqlDate);

					hmFutureDatedEvents.clear();*/

					//END: BUG 13716729

					logger.info(className, sMethodName, "Re Hire Event Map " + hmFutureDatedEvents);

					hmDeferredEvents.put(start_date, hmFutureDatedEvents);

				}

			} else if (hmTerminateEvents.containsKey(sEventID)) {

				logger.debug(className, sMethodName, 

						"Terminate event: " + sEventID + "--" +  sStartDt+ "--" +  sEndDt);	

				/*

				 * If event id received from IDoc is found in

				 * "Lookup.SAP.HRMS.TerminateEvents" then it is terminate event

				 */

				//Start: BUG 10229768 - STARTING AND TERMINATION DATE ARE NOT COMING

				

				//Set OIM event end date as  terminate event start.

				if (!sOIMEndDate.equalsIgnoreCase(NONE)) {

					hmCurrDatedEvents.put(sOIMEndDate, sStartDt);

				}

				String sCreateDeferEvent = (String)hmConfig.get(CREATE_DEFERRED_EVENT_FOR_TERMINATE_EVENT);

				sCreateDeferEvent = stringUtil.isEmpty(sCreateDeferEvent)?YES:sCreateDeferEvent;

				//End: BUG 10229768 - STARTING AND TERMINATION DATE ARE NOT COMING



				// If user record is not future dated then user

				// will be disabled in OIM else future recon event gets created

				if (isFutureDatedEvent&&sCreateDeferEvent.equalsIgnoreCase(YES)) {

					//START: BUG 13716729 - FUTURE DATED HIRE, TERMINATE, REHIRE NOT FUNCTIONING CORRECTLY

					if(hmDeferredEvents.get(start_date)!= null){

						hmFutureDatedEvents = hmDeferredEvents.get(start_date);

					}else{

						hmFutureDatedEvents = new HashMap<String, String>();

					}

					//END: BUG 13716729

					hmFutureDatedEvents.put(sSAPPersonnelNumber, sPersonnelNumber);

					hmFutureDatedEvents.put(sStatus, sDisabledStatus);

					//START: BUG 13716729 - FUTURE DATED HIRE, TERMINATE, REHIRE NOT FUNCTIONING CORRECTLY

					hmFutureDatedEvents.put(sOIMEndDate, sStartDt);

					/*java.sql.Date sqlDate = new java.sql.Date(start_date.getTime());

					logger.info(className, sMethodName, "Future dated terminate event. Creating deferred event for date " + sqlDate);

					oUtil.createFutureTrustedReconEvent(sObjectName,

							hmFutureDatedEvents, sPersonnelNumber, sqlDate);

					hmFutureDatedEvents.clear();*/

					hmDeferredEvents.put(start_date, hmFutureDatedEvents);

					//END: BUG 13716729

				}else{

					hmCurrDatedEvents.put(sStatus, sDisabledStatus);

				}

			} else {

				logger.debug(className, sMethodName, 

						"Other event: " + sEventID + "--" +  sStartDt+ "--" +  sEndDt);	

				//set OIM end date as last event end date if above conditions are not satisfied. 
		//Start: Bug 17892489-SAP ER 9.1.2.4.0 CONNECTOR EMPLOYEE END DATE PROBLEM 
				/*if (!sOIMEndDate.equalsIgnoreCase(NONE)) {

					hmCurrDatedEvents.put(sOIMEndDate, sEndDt);
				}*/
		//End: Bug 17892489-SAP ER 9.1.2.4.0 CONNECTOR EMPLOYEE END DATE PROBLEM 
				}

		} catch (Exception  e) {

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

			throw new ConnectorException(e);

		}

		logger.setMethodFinishLog(className, sMethodName);

	}

	/**

	 * Description:Accesses BAPI IDOCTYPE_READ_COMPLETE to get the location of

	 * sub type and start date fields for all segments mentioned in the

	 * attribute mappings lookup definition

	 * 

	 * @param hmConfig

	 *            HasHtable containing configuration details from the

	 *            Lookup.SAP.HRMS.Configuration lookup definition

	 * @param hmConstants

	 *            HashMap containing configuration details from the

	 *            Lookup.SAP.HRMS.Constants lookup definition

	 * @param htITRattributes

	 *            IT Resource values required for setting up a connection with

	 *            the target system

	 * @param htTaskAttributes

	 *            HashTable containing scheduled task attributes

	 * @param oUtil

	 *            Reference of OIMUtil class for getting API interfaces

	 * 

	 * @throws ConnectorException

	 */

	public ArrayList getSchema(HashMap hmConfig, final HashMap hmConstants,

			Hashtable htITRattributes, Hashtable htTaskAttributes, OIMUtil oUtil)

			throws ConnectorException {

		ArrayList alList = new ArrayList();

		String sMethodName = "getSchema()";

		logger.setMethodStartLog(className, sMethodName);

		SAPConnection sAPConnection = new SAPConnection(logger);

		JCoDestination jcoConnection = null;

		try {

			SAPUtil oSAPUtil=new SAPUtil(logger);

			logger.debug(className, sMethodName, "Executing"

					+ (String) hmConstants.get(BAPI_NAME));



			/*

			 * Calling Initialising the Lookup table values to initialise all the

			 * HashMap and other task scheduler constants once during the first time

			 * recon run

			 */

			initialiseLookupValues(hmConfig, hmConstants, htTaskAttributes, oUtil);

			

/*			hmOrgHierarchy = oUtil.getLookUpMap((String) hmConfig

					.get(ORG_LOOKUP_NAME));

			hmManagerHierarchy = oUtil.getLookUpMap((String) hmConfig

					.get(MANAGER_LOOKUP_NAME));	*/		

			JCoTable jcoTable1 = null;

			JCoTable jcoTable2 = null;

			ArrayList beanArray = new ArrayList();

			/*

			 * Connecting to SAP schema and adding the segment name in ArrayList

			 */



			jcoConnection = sAPConnection.addDestination(htITRMapping,

					htITRattributes);

			Set keySet = htAttrMap.keySet();

			Iterator keySetIterator = keySet.iterator();

			while (keySetIterator.hasNext()) {

				SAPAttributeMapBean bean = new SAPAttributeMapBean();

				String sSegmentName = (String) keySetIterator.next();

				bean.setSSegmentName(sSegmentName);

				beanArray.add(bean);

			}



			// Execute the BAPI

			JCoFunction function = oSAPUtil.getJCOFunction(jcoConnection,

					(String) hmConstants.get(BAPI_NAME));



			JCoParameterList findParameterList = function.getImportParameterList();

			logger.debug(className, sMethodName, "Setting " + 

					(String)hmConstants.get(IDOC_TYPE) + " to " + hmConfig.get(IDOC_TYPE));

			findParameterList.setValue((String) hmConstants.get(IDOC_TYPE), hmConfig

					.get(IDOC_TYPE));



			String sPI_CIMTYP = (String) hmConfig.get(IDOC_TYPE_EXTENSION);



			if (!sPI_CIMTYP.equalsIgnoreCase((String) hmConstants.get(NONE))) {

				logger.debug(className, sMethodName, "Setting " + 

						(String)hmConstants.get(IDOC_TYPE_EXTENSION) + " to " + sPI_CIMTYP);

				findParameterList.setValue((String) hmConstants

						.get(IDOC_TYPE_EXTENSION), sPI_CIMTYP);

			}

			function.execute(jcoConnection);

			logger.debug(className, sMethodName, "BAPI executed");

			jcoTable1 = function.getTableParameterList().getTable(

					(String) hmConstants.get(SEGMENT));

			jcoTable2 = function.getTableParameterList().getTable(

					(String) hmConstants.get(FIELD));



			/*

			 * Loop through the both the JCO tables and get the start and end postion

			 * for BEGDA and SUBTY fields and store it in arraylist for all segments

			 * mentioned in attribute map lookup table

			 */

			int iBeanArraySize = beanArray.size();

			int iJCOTable1Rows = jcoTable1.getNumRows();

			int iJCOTable2Rows = jcoTable2.getNumRows();

			for (int i = 0; i < iBeanArraySize; i++) {



				SAPAttributeMapBean oBean = (SAPAttributeMapBean) beanArray.get(i);

				jcoTable1.firstRow();

				for (int j = 1; j <= iJCOTable1Rows; j++) {

					if (jcoTable1.getValue((String) hmConstants.get(SEGMENT_DEFINITION))

							.toString().equalsIgnoreCase(oBean.getSSegmentName())) {

						jcoTable2.firstRow();

						for (int k = 1; k <= iJCOTable2Rows; k++) {

							if (jcoTable2.getValue((String) hmConstants.get(SEGMENT_TYPE))

									.toString().equalsIgnoreCase(

											jcoTable1

													.getValue((String) hmConstants.get(SEGMENT_TYPE))

													.toString())) {

								if (jcoTable2.getValue((String) hmConstants.get(FIELD_NAME))

										.toString().equalsIgnoreCase(

												(String) hmConstants.get(SUB_TYPE_FIELD))) {

									oBean.setSSubTypeStartPosition(jcoTable2.getValue(

											(String) hmConstants.get(START_POSITION)).toString());

									oBean.setSSubTypeEndPosition(jcoTable2.getValue(

											(String) hmConstants.get(END_POSITION)).toString());

								} else if (jcoTable2.getValue(

										(String) hmConstants.get(FIELD_NAME)).toString()

										.equalsIgnoreCase(

												(String) hmConstants.get(BEGIN_DATE_FIELD))) {

									oBean.setSBeginDatePosition(jcoTable2.getValue(

											(String) hmConstants.get(START_POSITION)).toString());

									oBean.setSEndDatePosition(jcoTable2.getValue(

											(String) hmConstants.get(END_POSITION)).toString());

								}

							}

							jcoTable2.nextRow();

						}

						alList.add(oBean);

					}

					jcoTable1.nextRow();

				}

			}

		} catch (JCoException e) {

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

			throw new ConnectorException(e);

		} catch (ConnectorException e) {

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

			throw new ConnectorException(e);

		} finally {

			try {

				sAPConnection.closeSAPConnection(jcoConnection);

			} catch (Exception e) {

				logger.error(className, sMethodName, e.getMessage());

			}

		}

		logger.setMethodFinishLog(className, sMethodName);

		return alList;

	}



	/**

	 * Description: Loops through the file for the Batch Size value

	 * set and returns string

	 * 

	 * @param iBatchSize

	 *          Batch Size for looping through the file records

	 * @param hmConstants

	 *          HashMap containing configuration details from the lookup

	 *          "Lookup.SAP.HRMS.Constants"

	 */

	public String getNextpage(int iBatchSize, HashMap hmConstants) {

		String sMethodName = "getNextpage()";

		String fileString = new String();

		StringBuffer cRowBuffer = new StringBuffer();

		String cRow = null;

		logger.setMethodStartLog(className, sMethodName);

		// Loop through the file and read the lines as specified in batch size and

		// store the result in StringBuffer

		for (int count = 0; count < iBatchSize; count++) {

			try {

				cRow = lnrdr.readLine();

				if (cRow != null && !SAPHRMSUserRecon.isReconStopped) {

					cRowBuffer.append(cRow);

					cRowBuffer.append("\n");

				} else {

					isEOF = true;

				}

			} catch (Exception e2) {

				logger.error(className, sMethodName, e2.getMessage());

				continue;

			}

		}

		try {

			fileString = new String(cRowBuffer.toString());

		} catch (Exception e) {

			logger.error(className, sMethodName, e.getMessage());

		}

		logger.setMethodFinishLog(className, sMethodName);

		return fileString;

	}



	/**

	 * Description: Used to execute the user type query specified

	 * for each user record. Returns true if valid .Else returns false.

	 * 

	 * @param hmReconDetails

	 *          Hashmap containing details of user record to be reconciled to OIM

	 * @param userResultSet

	 *          Result Set of the user for getting values from OIM

	 * @throws ConnectorException

	 * 

	 */

	private boolean executeEmployeeTypeQuery(

			HashMap<String, String> hmReconDetails, tcResultSet userResultSet,

			HashMap hmConstants) throws ConnectorException {

		boolean isValid = false;

		String sMethod = "executeEmployeeTypeQuery()";

		try {

			logger.setMethodStartLog(className, sMethod);

			String sSplitAND = AND_SPLIT_REGEX;

			String sSplitOR = OR_SPLIT_REGEX;

			int iNoOfOR;

			int iNoOfAnd;

			String sKey;

			String sValue;

			// exp : regular expression variable for spliting the query according to

			// "|"

			String sArrORExp[] = sEmployeeTypeQuery.split(sSplitOR);

			iNoOfOR = sArrORExp.length - 1;

			logger.debug(className, sMethod, "Number Of 'OR' Operators " + iNoOfOR);

			for (int i = 0; i <= iNoOfOR; i++) {

				// exp : regular expression variable for spliting the query according

				// to "&"

				String sArrANDExp[] = sArrORExp[i].split(sSplitAND);

				iNoOfAnd = sArrANDExp.length - 1;

				for (int j = 0; j <= iNoOfAnd; j++) {

					logger.debug(className, sMethod, "Expression is  " + sArrANDExp[j]);

					int iEquals = 0;

					// Get the key and value by checking first index of '='

					// Throw exception if query does not have '=' operator b/w key and

					// value

					iEquals = sArrANDExp[j].indexOf('=');



					sKey = sArrANDExp[j].substring(0, iEquals).trim();

					sValue = sArrANDExp[j].substring(iEquals + 1).trim();

					/*

					 * Since all the attributes were not got from SAP,we got to OIM to get

					 * its value

					 */

					if (!hmReconDetails.containsKey(sKey)) {

						logger.debug(className, sMethod, "Getting the attribute value"

								+ " from OIM as the key was not present in HashMap");



						if (userResultSet.getRowCount() > 0) {

							userResultSet.goToRow(0);

							try {

								String sGroupValueFromOIM = userResultSet

										.getStringValue((String) hmConstants.get(GROUP_UDF));

								String sSubGroupValueFromOIM = userResultSet

										.getStringValue((String) hmConstants.get(SUB_GROUP_UDF));

								hmReconDetails.put((String) hmConstants.get(EMPLOYEE_GROUP),

										sGroupValueFromOIM);

								hmReconDetails.put(

										(String) hmConstants.get(EMPLOYEE_SUB_GROUP),

										sSubGroupValueFromOIM);

							} catch (tcColumnNotFoundException e) {

								logger.error(className, sMethod,

										"The column was not found in OIM");

								hmReconDetails.put(sKey, "");

							}

						} else {

							logger.debug(className, sMethod,

									"Could not get the attribute value from OIM as user is not present");

							hmReconDetails.put(sKey, "");

						}



					}

					/*

					 * Check if the query condition gets satisfied.If satisfied,then

					 * continue and check if any more and condition need to be validated.

					 * If all AND conditions are satisfied,then return true If query

					 * condition fails for AND,then check if any more OR conditions are to

					 * be validated. If all query condition for OR fails,return false

					 */

					if (((String) hmReconDetails.get(sKey)).equalsIgnoreCase(sValue)) {

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

			logger.info(className, sMethod, "Employee Type Query is valid? " + isValid);

		} catch (Exception e) {

			isValid = false;

			logger.error(className, sMethod,

					"Exception occured during execute Employee Type Query."

							+ "Please check if the query is entered properly");

		}

		logger.setMethodFinishLog(className, sMethod);

		return isValid;

	}



	/**

	 * Description: Processes the user attributes to create delete

	 * recon event is custom recon query and user type query is valid

	 * 

	 * @param hmReconDetails

	 *          Hashmap containing details of user record to be reconciled to OIM

	 * 

	 * @param hmFutureDatedEvents

	 *          Hashmap containing details of future dated attributes. These

	 *          attributes are not reconciled to OIM

	 * 

	 * @param oUtil

	 *          Utility that initializes all reconciliation interfaces

	 * 

	 * 

	 * @throws ConnectorException

	 */

	private void reconcileDeletedUser(HashMap<String, String> hmReconDetails,

			HashMap hmConstants, OIMUtil oUtil) throws ConnectorException {

		String sMethodName = "reconcileDeletedUser()";

		logger.setMethodStartLog(className, sMethodName);

		try {

			boolean isUserExists = false;

			String sSAPUserID = (String) hmReconDetails.get(sSAPPersonnelNumber);

			HashMap<String, String> hmUserID = new HashMap<String, String>();

			hmUserID.put(sUDFPersonnelNo, sSAPUserID);

			logger.debug(className, sMethodName, "Searching " + sSAPUserID + " user in OIM");

			tcResultSet userResultSet = oUtil.getUserAPI().findUsers(hmUserID);

			if (userResultSet != null && userResultSet.getRowCount() > 0) {

				isUserExists = true;

			}

			/*

			 * Validates if the custom recon query is valid for the user record

			 */

			if (!stringUtil.isEmpty(sCustomQuery)) {

				isValid = executeCustomQuery(hmReconDetails, userResultSet);

			}

			/*

			 * Validates if the user type query is valid for the user record

			 */



			if (!stringUtil.isEmpty(sEmployeeTypeQuery)) {

				isValid = executeEmployeeTypeQuery(hmReconDetails, userResultSet,

						hmConstants);

			}



			// Get the User ID from OIM.This is required since there might be scenario

			// where User ID is updated in OIM and put that value in HashMap

			if (isValid) {

				if (isUserExists) {

					userResultSet.goToRow(0);

					logger.debug(className, sMethodName, "Getting the User ID from OIM");

					String sNewUserID = userResultSet.getStringValue((String) hmConstants

							.get(USER_ID_OIM));

					hmReconDetails.put(sUserLoginField, sNewUserID);

				} else {

					hmReconDetails.put(sUserLoginField, sSAPUserID);

				}



				if (isValid) {

					String sPersonnelNumber = (String) hmReconDetails

							.get(sUserLoginField);

					oUtil.createTrustedDeleteReconEvent(sObjectName, hmReconDetails,

							sPersonnelNumber);

				}

			}

			logger.setMethodFinishLog(className, sMethodName);

		} catch (tcAPIException e) {

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

		} catch (Exception e) {

			logger.error(className, sMethodName, e.getMessage());

			logger.setStackTrace(e, className, sMethodName, e.getMessage());

		} finally {

			isValid = false;

			hmReconDetails.clear();

		}



	}

	

	//START:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

	/**

	 * Description: Process the transformation logic

	 *  

	 * @param hmDeferredEventTransform

	 *          Hashmap containing details of future dated attributes. These

	 *          attributes are not reconciled to OIM

	 * @param hmConstants

	 *          HashMap containing configuration details from the lookup

	 *          "Lookup.SAP.HRMS.Constants"

	 * 

	 * @param oUtil

	 *          Util that innitializes all reconciliation interfaces

	 * 

	 * 

	 * @throws ConnectorException

	 */

	private HashMap doTrasformation(HashMap hmDeferredEventTransform,

			HashMap hmConstants, OIMUtil oUtil, HashMap hmConfig) throws ConnectorException {

		String sMethodName = "doTrasformation()";

		logger.setMethodStartLog(className, sMethodName);

		/*

		 * If transform mapping is required,then call

		 * transformSingleOrMultivaluedData() to transform the data

		 */

		if (((String)hmConstants.get(YES)).equalsIgnoreCase((String) hmConfig.get(USE_TRANSFORM_MAPPING))) {

			hmDeferredEventTransform = oUtil.transformSingleOrMultivaluedData(hmDeferredEventTransform, null, (String) hmConfig.get(TRANSFORM_LOOKUP));

		}

		logger.debug(className, sMethodName, "Transformation Map " + hmDeferredEventTransform );

		logger.setMethodFinishLog(className, sMethodName);

		return hmDeferredEventTransform;

	}

	

		

	/**

	 * Description: Process the validation logic

	 *  

	 * @param hmDeferredEventValidation

	 *          Hashmap containing details of future dated attributes. These

	 *          attributes are not reconciled to OIM

	 * @param hmConstants

	 *          HashMap containing configuration details from the lookup

	 *          "Lookup.SAP.HRMS.Constants"

	 * 

	 * @param oUtil

	 *          Util that innitializes all reconciliation interfaces

	 * 

	 * 

	 * @throws ConnectorException

	 */

	private boolean doValidation(HashMap hmDeferredEventValidation,

			HashMap hmConstants, OIMUtil oUtil, HashMap hmConfig) throws ConnectorException {

		String sMethodName = "doValidation";

		logger.setMethodStartLog(className, sMethodName);

		/*

		 * If validation is required,then call

		 * validateSingleOrMultivaluedData() to transform the data

		 */

		if (((String)hmConstants.get(YES)).equalsIgnoreCase(

				(String) hmConfig.get(USE_RECON_VALIDATION))) {

			isValid = oUtil.validateSingleOrMultivaluedData(hmDeferredEventValidation, null, (String) hmConfig

							.get(VALIDATE_LOOKUP));

		}

		logger.setMethodFinishLog(className, sMethodName);

		return isValid;

	}

	//End:Bug 13803977 - SAP ER - DEFERRED EVENTS ARE LOOSING TRANSFORMED VALUES 

	

	/**

	 * Description: Get Group Information for all the event

	 *  

	 * @param hmDeferredEventValidation

	 *          Hashmap containing details of future dated attributes. These

	 *          attributes are not reconciled to OIM

	 * @param hmConstants

	 *          HashMap containing configuration details from the lookup

	 *          "Lookup.SAP.HRMS.Constants"

	 * 

	 * @param oUtil

	 *          Util that innitializes all reconciliation interfaces

	 * 

	 * 

	 * @throws ConnectorException

	 */

	private HashMap getGroupSegmentFields(IDocSegment jcoRootChild, HashMap hmConfig, HashMap hmConstants){

		String sMethodName = "getGroupSegmentFields";

		logger.setMethodStartLog(className, sMethodName);

		

		HashMap<String, String> hmDGroupAttrs = new HashMap<String, String>();

		String sEmpGroup = getFieldValue(jcoRootChild, (String) hmConfig

				.get((String) hmConstants.get(EMPLOYEE_GROUP)));

		String sEmpSubGroup = getFieldValue(jcoRootChild, (String) hmConfig

				.get((String) hmConstants.get(EMPLOYEE_SUB_GROUP)));

		String sGroup_SubGroup = sEmpGroup + '~' + sEmpSubGroup;



		hmDGroupAttrs.put((String) hmConstants.get(EMPLOYEE_GROUP),

				sEmpGroup);

		hmDGroupAttrs.put((String) hmConstants.get(EMPLOYEE_SUB_GROUP),

				sEmpSubGroup);



		if (hmEmployeeType.containsKey(sGroup_SubGroup)) {

			hmDGroupAttrs.remove(sEmployeeType);

			hmDGroupAttrs.put(sEmployeeType, (String) hmEmployeeType

					.get(sGroup_SubGroup));

		}

		logger.debug(className, sMethodName, "Group Map " + hmDGroupAttrs );

		logger.setMethodFinishLog(className, sMethodName);

		System.out.println("hmDGroupAttrs ---"+hmDGroupAttrs);

		return hmDGroupAttrs;

		

	}
	
	
	public Boolean isBulkReconSupported(){
		String sMethodName = "isBulkReconSupported";
		try{
		BatchAttributes batchAttr= new BatchAttributes(new String(), new String(),true);
		logger.info(className, sMethodName, "Bulk reconciliation is supported");

		}catch(NoSuchMethodError nMethod){
			logger.info(className, sMethodName, "Bulk reconciliation is not supported");
			return false;
		}
		return true;
	}

}

