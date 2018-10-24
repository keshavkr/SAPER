/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/ume/tasks/SAPUMELookupRecon.java /main/1 2010/05/11 03:23:16 ddkumar Exp $ */

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
 configured for lookup reconciliation
 ddkumar     07/10/09 - Creation
 */

/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/ume/tasks/SAPUMELookupRecon.java /main/1 2010/05/11 03:23:16 ddkumar Exp $
 *  @author  ddkumar 
 *  @since   release specific (what release of product did this appear in)
 */

package oracle.iam.connectors.sap.ume.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.openspml.v2.util.xml.XmlElement;
import org.openspml.v2.util.xml.XmlParser;
import org.w3c.dom.Document;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.dao.OIMUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.common.vo.ITResource;
import oracle.iam.connectors.common.vo.ScheduledTask;
import oracle.iam.connectors.sap.ume.util.UMEConstants;
import oracle.iam.connectors.sap.ume.util.UMEUtil;
import Thor.API.tcResultSet;
import Thor.API.Exceptions.IllegalInputException;
import Thor.API.Exceptions.tcAPIException;
import Thor.API.Exceptions.tcColumnNotFoundException;
import Thor.API.Exceptions.tcInvalidLookupException;
import Thor.API.Exceptions.tcInvalidValueException;
import Thor.API.Operations.tcFormDefinitionOperationsIntf;
import Thor.API.Operations.tcFormInstanceOperationsIntf;
import Thor.API.Operations.tcITResourceInstanceOperationsIntf;
import Thor.API.Operations.tcLookupOperationsIntf;
import Thor.API.Operations.tcObjectOperationsIntf;
import Thor.API.Operations.tcReconciliationOperationsIntf;
import Thor.API.Operations.tcSchedulerOperationsIntf;
import Thor.API.Operations.tcUserOperationsIntf;

import com.thortech.xl.scheduler.tasks.SchedulerBaseTask;

/**
 * Description:Mapped to the scheduled task that is configured for user management lookup synchronization 
 */
public class SAPUMELookupRecon extends SchedulerBaseTask implements UMEConstants {
	private boolean isStopRecon = false;
	private boolean isValid = false;
	private long lITResourceKey;
	private String className = this.getClass().getName();
	private String sLookupName;
	private String sITResourceName;
	private Hashtable<String, String> htITRattributes;
	private HashMap<String, String> hmUMConfig;
	private HashMap<String, String> hmConstants;
	private tcLookupOperationsIntf lookIntf;
	private StringUtil stringUtil = new StringUtil();
	private OIMUtil oUtil;
	private ConnectorLogger logger = new ConnectorLogger(UM_LOGGER);

	/**
	 * Description: Initializes the attributes of the lookup synchronization scheduled task and validates the IT Resource parameters and scheduled task attributes
	 */
	public void init() {
		String sMethodName = "init()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			logger.info(className, sMethodName, "Start of SAP User Management Lookup Reconciliation");

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
			lookIntf = (tcLookupOperationsIntf) super.getUtility(LOOKUP_API);
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
			String sScheduleTaskName = super.getAttribute(SCHEDULE_TASK_NAME);
			if (stringUtil.isEmpty(sScheduleTaskName)) {
				throw new ConnectorException
					("Task Scheduler Name value is not set in task scheduler");
			}

			// Get the task scheduler attributes in HashTable
			// Validate if all required task attributes are set properly
			ScheduledTask oTaskAttributes = 
					new ScheduledTask(sScheduleTaskName, logger);
			Hashtable<String, String> htTaskAttributes = 
					oTaskAttributes.getScheduledTaskDetails(schedulerAPI);
			boolean isMandatoryTaskAttrSet = oTaskAttributes
				.validateMandatoryTaskAttrs(mandatoryLookupSchedulerAttrs);
			
			if (!isMandatoryTaskAttrSet) {
				throw new ConnectorException
					("Mandatory Task Scheduler values not set");
			}
			sLookupName = (String) htTaskAttributes.get(LOOKUP_NAME);
			sITResourceName = (String) htTaskAttributes.get(IT_RESOURCE_NAME);
			// Get the IT Resource attributes in HashTable and validate
			ITResource oITResource = 
					new ITResource(sITResourceName, resAPI, logger);
			htITRattributes = oITResource.getITResourceDetails();
			lITResourceKey = oUtil.getITResourceKey(sITResourceName);
			
			// Initialize HashMap for Configuration Lookup and Constant Lookup
			hmUMConfig = oUtil.getLookUpMap
				((String) htITRattributes.get(CONFIG_LOOKUP));
			hmConstants = oUtil.getLookUpMap
				((String) hmUMConfig.get(CONSTANTS_LOOKUP));
			
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
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
		}
	}
	
	/**
	 *Description: Executed from scheduled task after all the values are initialized
	 * 
	 */
	public void execute() {
		String sMethodName = "execute()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			if (isValid) {
				String sLookupFieldName;
				String sLookupFieldMapping;
				HashMap<String, String> hmLookupValues = null;

				tcResultSet lookupRS = lookIntf.getLookupValues(sLookupName);
				int iLookupRowCount = lookupRS.getRowCount();
				String sDataSource = null;

				for (int i = 0; i < iLookupRowCount; i++) {
					lookupRS.goToRow(i);
					sLookupFieldName = lookupRS.getStringValue(LOOKUP_CODE);
					sLookupFieldMapping = lookupRS.getStringValue(LOOKUP_DECODE);

					if (!isStopRecon && !stringUtil.isEmpty(sLookupFieldMapping)) {
						hmLookupValues = new HashMap<String, String>();
						logger.info("Starting Lookup Reconciliation for " + sLookupFieldName);
						
						if (sLookupFieldMapping.equalsIgnoreCase("saprole"))
							sDataSource = hmUMConfig.get("Role Datasource");
						else if (sLookupFieldMapping.equalsIgnoreCase("sapgroup"))
							sDataSource = hmUMConfig.get("Group Datasource");
						
						if (sDataSource == null) {
							logger.error(className, sMethodName, "Datasournce not initialized");
						} else {
							hmLookupValues = getDetails(sLookupFieldMapping, sDataSource);
						}
						
						logger.debug(className, sMethodName,"Lookup Values are :" + hmLookupValues.toString());
						if (!isStopRecon && (hmLookupValues.size() > 0)) {
							try {
								oUtil.addLookupvalues(sLookupFieldName, hmLookupValues);
							} catch (IllegalInputException e) {
								logger.error(className, sMethodName, e.getMessage());

							} catch (tcInvalidValueException e) {
								logger.error(className, sMethodName, e.getMessage());
							}
						}
						logger.info("End of Lookup Reconciliation for " + sLookupFieldName);
					}
				}

			} else {
				throw new ConnectorException("Required Values not set properly in IT Resource or Task Scheduler");
			}
		} catch (tcAPIException e) {
			logger.error(className, sMethodName, e.getMessage());
		} catch (tcColumnNotFoundException e) {
			logger.error(className, sMethodName, e.getMessage());
		} catch (tcInvalidLookupException e) {
			logger.error(className, sMethodName, e.getMessage());
		}catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
		} finally {
			logger.info("End of SAP User Management Lookup Reconciliation");
		}
	}

	/**
	 * Description: Stops reconciliation when the Stop Reconciliation is selected in the scheduled task. 
	 * 
	 * @return boolean 
	 * 			 Returns true if the scheduled task is manually stopped during the reconciliation run
	 * 
	 */
	public boolean stop() {
		isStopRecon = true;
		logger.info("Stopping Lookup Reconciliation........");

		return isStopRecon;
	}

	public HashMap<String, String> getDetails(String sObjectClass, String sDataSource) throws ConnectorException {
		String MethodName = "getDetails()"; 
		HashMap<String, String> hmSOAPDetails = new HashMap<String, String>();
		
		ArrayList<String> alFilter = new ArrayList<String>();
		alFilter.add("datasource equals " + sDataSource);
		
		HashMap<String, String> hmAttrs = new HashMap<String, String>();
		hmAttrs.put("uniquename","uniquename"); 
		hmAttrs.put("description","description");
			
		UMEUtil oUMEUtil = new UMEUtil(htITRattributes);
		StringBuffer sSOAPReq = new StringBuffer();
		
		try {
			
			String sSOAPXml = sSOAPReq.append(oUMEUtil.getSOAPEnvStartTag())
			.append(oUMEUtil.searchRequestTag(sObjectClass, hmAttrs, null, alFilter))
			.append(oUMEUtil.getSOAPEnvEndTag()).toString();
			
			String toReturn = oUMEUtil.getSOAPResponse(sSOAPXml);
			
			//SearchResponse response = null;
			XmlElement oSOAPMSg = null, oResultEntry = null;
			XmlElement oChildElement = null, oAttrElement =null;
			Document doc = XmlParser.parse(toReturn);
			XmlElement envelope = new XmlElement(doc.getDocumentElement());

			if (!envelope.getLocalName().equals("Envelope")) {
				oSOAPMSg = envelope;
			}
			else {
				XmlElement body = envelope.getChildElement("Body");
				String sAttrElementName = null, sAttrElementValue=null;
				String sUniqueNameVal = "", sDescriptionVal = "";
				
				if (body != null) {
					oSOAPMSg = body.getChildElement();
					String tag = oSOAPMSg.getLocalName();
					if (tag.equalsIgnoreCase("searchResponse")) {
						oResultEntry = oSOAPMSg.getChildElement();
						while (oResultEntry != null) {
							oChildElement = oResultEntry.getChildElement();
							while(oChildElement !=null){
								if(oChildElement.getTagName().equals("attributes")){
									oAttrElement = oChildElement.getChildElement();
									
									while(oAttrElement!=null){
										sAttrElementName  = oAttrElement.getAttribute("name");
										sAttrElementValue = oAttrElement.getContent();
										if (sAttrElementName.equalsIgnoreCase("uniquename")){
											sUniqueNameVal = sAttrElementValue;
										}
										if (sAttrElementName.equalsIgnoreCase("description")){
											sDescriptionVal = sAttrElementValue;
										}
										oAttrElement = oAttrElement.getNextElement();
									}//End of While
									StringUtil strUtil = new StringUtil();
									sDescriptionVal = strUtil.isEmpty(sDescriptionVal)?sUniqueNameVal.trim():sDescriptionVal.trim();
									sDescriptionVal = sITResourceName + "~" + sDescriptionVal;
									if(!strUtil.isEmpty(sUniqueNameVal)){
										hmSOAPDetails.put(lITResourceKey + "~" + sUniqueNameVal.trim(), sDescriptionVal.trim());
									}
									sDescriptionVal = "";
								}//End of If
								oChildElement = oChildElement.getNextElement();
							}//End of While - Identifier & Attr
							oResultEntry = oResultEntry.getNextElement();
						} //End of While - Result Entry
					}//End of If - SearchResponse
				}
			}
		} catch (ConnectorException bException) {
			logger.error(className, MethodName, bException.getMessage());
			logger.setStackTrace(bException, className, MethodName, bException
					.getMessage());
			throw new ConnectorException();
		} catch (Exception e) {
			logger.error(className, MethodName, e.getMessage());
			logger.setStackTrace(e, className, MethodName, e.getMessage());
			throw new ConnectorException();
		}
		return hmSOAPDetails;
	}
}
