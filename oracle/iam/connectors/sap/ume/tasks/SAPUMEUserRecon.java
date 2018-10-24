/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/ume/tasks/SAPUMEUserRecon.java /main/1 2010/05/11 03:23:16 ddkumar Exp $ */

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
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/ume/tasks/SAPUMEUserRecon.java /main/1 2010/05/11 03:23:16 ddkumar Exp $
 *  @author  ddkumar 
 *  @since   release specific (what release of product did this appear in)
 */

package oracle.iam.connectors.sap.ume.tasks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.SimpleTimeZone;

import org.openspml.v2.util.xml.XmlElement;
import org.openspml.v2.util.xml.XmlParser;
import org.w3c.dom.Document;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.dao.OIMUtil;
import oracle.iam.connectors.common.util.DateUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.common.vo.ITResource;
import oracle.iam.connectors.common.vo.ScheduledTask;
import oracle.iam.connectors.sap.ume.util.UMEConstants;
import oracle.iam.connectors.sap.ume.util.UMEUtil;
import Thor.API.Exceptions.tcAPIException;
import Thor.API.Exceptions.tcColumnNotFoundException;
import Thor.API.Exceptions.tcInvalidLookupException;
import Thor.API.Exceptions.tcObjectNotFoundException;
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
public class SAPUMEUserRecon extends SchedulerBaseTask implements UMEConstants {
	private boolean isStopRecon = false;
	private boolean isValid = false;
	private long lITResourceKey;
	private String className = this.getClass().getName();
	private String sITResourceName;
	private Hashtable<String, String> htITRattributes;
	private HashMap<String, String> hmUMConfig;
	private HashMap<String, String> hmFullReconAttr;
	private HashMap<String, String> hmConstants;
	private HashMap<String, String> hmAttrMap;
	private HashMap<String, String> hmChildAttrMap;
	private tcLookupOperationsIntf lookIntf;
	private StringUtil stringUtil = new StringUtil();
	private OIMUtil oUtil;
	private ConnectorLogger logger = new ConnectorLogger(UM_LOGGER);
	private String sCustomQuery;
	private String sResourceObject;
	/**
	 * Description: Initializes the attributes of the lookup synchronization 
	 * scheduled task and validates the IT Resource parameters and 
	 * scheduled task attributes
	 */
	public void init() {
		String sMethodName = "init()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			logger.info(className, sMethodName, "Start of SAP UME User Reconciliation");

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
			oUtil = new OIMUtil(userAPI, objAPI, formAPI, formDefAPI, 
					resAPI,schedulerAPI, lookIntf, reconOperAPI, logger);

			if (schedulerAPI == null || reconOperAPI == null
					|| lookIntf == null || userAPI == null || objAPI == null
					|| formAPI == null || formDefAPI == null || resAPI == null) {
				throw new ConnectorException(
				"OIM API's are not getting initialised");
			}

			// Get the task scheduler name and validate it
			String sScheduleTaskName = super.getAttribute(SCHEDULE_TASK_NAME);
			if (stringUtil.isEmpty(sScheduleTaskName)) {
				throw new ConnectorException(
				"Task Scheduler Name value is not set in task scheduler");
			}

			// Get the task scheduler attributes in HashTable
			// Validate if all required task attributes are set properly
			ScheduledTask oTaskAttributes = new ScheduledTask
			(sScheduleTaskName, logger);
			Hashtable<String, String> htTaskAttributes = 
				oTaskAttributes.getScheduledTaskDetails(schedulerAPI);
			boolean isMandatoryTaskAttrSet = oTaskAttributes
			.validateMandatoryTaskAttrs(mandatoryUserReconSchedulerAttrs);
			if (!isMandatoryTaskAttrSet) {
				throw new ConnectorException
				("Mandatory Task Scheduler values not set");
			}

			sITResourceName = (String) htTaskAttributes.get(IT_RESOURCE_NAME);

			// Get the IT Resource attributes in HashTable and validate
			ITResource oITResource = new ITResource(sITResourceName, resAPI, logger);
			htITRattributes = oITResource.getITResourceDetails();
			lITResourceKey = oUtil.getITResourceKey(sITResourceName);

			// Initialize HashMap for Configuration Lookup and Constant Lookup
			hmUMConfig = oUtil.getLookUpMap((String) htITRattributes.get(CONFIG_LOOKUP));
			hmConstants = oUtil.getLookUpMap((String) hmUMConfig.get(CONSTANTS_LOOKUP));
			hmAttrMap = oUtil.getLookUpMap((String) hmUMConfig.get(RECON_ATTR_MAPPING_LOOKUP));
			hmChildAttrMap = oUtil.getLookUpMap((String) htTaskAttributes.get(RECON_CHILD_ATTR_MAPPING_LOOKUP));
			hmFullReconAttr = oUtil.getLookUpMap((String) htTaskAttributes.get(UME_FULL_RECON_ATTR));
			sCustomQuery =  (String) htTaskAttributes.get(UME_CUSTOM_QUERY);
			sResourceObject = (String) htTaskAttributes.get(UME_RESOURCE_OBJECT);
			logger.info("IT Resource values initialised");

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
				if (!new StringUtil().isEmpty(sCustomQuery)){
					customRecon(sCustomQuery);
				} else {
					fullRecon();
				}
			} 
			else {
				throw new ConnectorException
				("Required Values not set properly " +
				"in IT Resource or Task Scheduler");
			}
		} catch (ConnectorException e) {
			logger.error(className, sMethodName, e.getMessage());
		
		}catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
		} 
		logger.info(className, sMethodName,
				"End of SAP UME Reconciliation");
	}

	public void customRecon(String sCustomQuery) throws ConnectorException{
		String MethodName = "customRecon()";
		try {
		ArrayList<String> alFilter = new ArrayList<String>();
		alFilter.add("datasource equals " + hmUMConfig.get("User Datasource"));
		alFilter.add(sCustomQuery);
		getDetails (alFilter);
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
		logger.info("Stopping UME User Reconciliation........");
		return isStopRecon;
	}

	public HashMap<String, String> fullRecon() throws ConnectorException {
		String MethodName = "fullRecon()";
		HashMap<String, String> hmSOAPDetails = new HashMap<String, String>();
		HashMap<String, String> hmFilter = new HashMap<String, String>();

		if (hmFullReconAttr != null) {
			try {
				Set hmAttrKeyset = hmFullReconAttr.keySet();
				Iterator itrAttr = hmAttrKeyset.iterator();

				while (itrAttr.hasNext()) {
					String sAttrKey = (String)(itrAttr.next());
					String sSearchUser = hmFullReconAttr.get(sAttrKey);

					ArrayList<String> alFilter = new ArrayList<String>();
					alFilter.add("datasource equals " + hmUMConfig.get("User Datasource"));
					alFilter.add("logonname"  + " startsWith " + sSearchUser);
					getDetails (alFilter);
					alFilter.clear();
				}
			}  catch (ConnectorException bException) {
				logger.error(className, MethodName, bException.getMessage());
				logger.setStackTrace(bException, className, MethodName, bException
						.getMessage());
				throw new ConnectorException();
			} catch (Exception e) {
				logger.error(className, MethodName, e.getMessage());
				logger.setStackTrace(e, className, MethodName, e.getMessage());
				throw new ConnectorException();
			}
		}
		return hmSOAPDetails;
	}

	public void getDetails(ArrayList<String> alFilter) throws ConnectorException {
		String MethodName = "getDetails()";
		String sObjectClass = hmConstants.get("User Object Class");

		UMEUtil oUMEUtil = new UMEUtil(htITRattributes);
		StringBuffer sSOAPReq = new StringBuffer();

		try {
			String sSOAPXml = sSOAPReq.append(oUMEUtil.getSOAPEnvStartTag())
			.append(oUMEUtil.searchRequestTag(sObjectClass, null, null, alFilter))
			.append(oUMEUtil.getSOAPEnvEndTag()).toString();

			String toReturn = oUMEUtil.getSOAPResponse(sSOAPXml);

			XmlElement oSOAPMSg = null, oResultEntry = null;
			XmlElement oChildElement = null, oAttrElement =null;
			Document doc = XmlParser.parse(toReturn);
			XmlElement envelope = new XmlElement(doc.getDocumentElement());

			if (!envelope.getLocalName().equals("Envelope")) {
				oSOAPMSg = envelope;
			}
			else {
				XmlElement body = envelope.getChildElement("Body");

				if (body != null) {
					oSOAPMSg = body.getChildElement();
					String tag = oSOAPMSg.getLocalName();
					if (tag.equalsIgnoreCase("searchResponse")) {
						oResultEntry = oSOAPMSg.getChildElement();
						while (oResultEntry != null) {
							oChildElement = oResultEntry.getChildElement();
							while(oChildElement !=null){
								if(oChildElement.getTagName().equals("identifier")){
									oAttrElement = oChildElement.getChildElement();
									String sAttrValue = oChildElement.getContent();
									ArrayList<String> alUserFilter = new ArrayList<String>();
									alUserFilter.add("datasource equals " + hmUMConfig.get("User Datasource"));
									String sUserIdForm = "SPML." + hmConstants.get("User Object Class").toUpperCase() + ".";
									if (sAttrValue.indexOf(sUserIdForm) != -1) {
										String[] sSplitedUserId = sAttrValue.split(sUserIdForm);
										alUserFilter.add("logonname equals " + sSplitedUserId[1]);
									}
									getUserDetails (sObjectClass, alUserFilter);
									alUserFilter.clear();
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
	}

	public void getUserDetails(String sObjectClass, ArrayList <String> alUserFilter) throws ConnectorException {
		String MethodName = "getUserDetails()";
		HashMap hmSAPUserDetails = new HashMap();
		HashMap hmSAPGrpRoles    = new HashMap();
		ArrayList alChildTableRole = new ArrayList();
		ArrayList alChildTableGroup = new ArrayList(); 
		String sUserID = null;

		String sUMEURL = htITRattributes.get("UME URL");
		htITRattributes.remove("UME URL");

		String sModifiedUMEURL = sUMEURL.replaceAll("provisioning", "spmlservice");
		htITRattributes.put("UME URL", sModifiedUMEURL);

		UMEUtil oUMEUtil = new UMEUtil(htITRattributes);
		StringBuffer sSOAPReq = new StringBuffer();


		try {
			String sSOAPXml = sSOAPReq.append(oUMEUtil.getSOAPEnvStartTag())
			.append(oUMEUtil.searchRequestTag(sObjectClass, hmAttrMap, hmChildAttrMap, alUserFilter))
			.append(oUMEUtil.getSOAPEnvEndTag()).toString();

			String toReturn = oUMEUtil.getSOAPResponse(sSOAPXml);

			XmlElement oSOAPMSg = null, oResultEntry = null;
			XmlElement oChildElement = null, oAttrElement = null, oAttrSubElement = null;
			Document doc = XmlParser.parse(toReturn);
			XmlElement envelope = new XmlElement(doc.getDocumentElement());

			if (!envelope.getLocalName().equals("Envelope")) {
				oSOAPMSg = envelope;
			}
			else {
				XmlElement body = envelope.getChildElement("Body");
				String sAttrElementName = null, sAttrElementValue=null;

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

										if (hmAttrMap.get(sAttrElementName) != null) {
											if (hmAttrMap.get(sAttrElementName).equals("Valid Through") 
													|| hmAttrMap.get(sAttrElementName).equals("Valid From")) {
												String sFormat = oUtil.getReconOperAPI().getDefaultDateFormat();
												if (sAttrElementValue.indexOf("Z") != -1) {
													sAttrElementValue = sAttrElementValue.replaceAll("Z", "");
													sAttrElementValue = sAttrElementValue.replaceAll("z", "");

													SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
													Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
													dateFormat.setCalendar(cal);
													Date dt = dateFormat.parse(sAttrElementValue);
													DateFormat sdf = new SimpleDateFormat(oUtil
															.getReconOperAPI().getDefaultDateFormat());
													sAttrElementValue = sdf.format(dt);
												}
											}
											if (hmAttrMap.get(sAttrElementName).equalsIgnoreCase("User Lock")){
												if (sAttrElementValue.equalsIgnoreCase("true")) {
													sAttrElementValue = (String) hmConstants.get(ONE);
												} else if (sAttrElementValue.equalsIgnoreCase("false")) {
													sAttrElementValue = (String) hmConstants.get(ZERO);
												}
											}
											hmSAPUserDetails.put(hmAttrMap.get(sAttrElementName), sAttrElementValue);
											if (sAttrElementName.equalsIgnoreCase("logonname") == true)
												sUserID = sAttrElementValue;
										} else if (hmChildAttrMap.get(sAttrElementName) != null) {
											oAttrSubElement = oAttrElement.getChildElement();
											while (oAttrSubElement != null) {
												String sAttrSubElementValue = oAttrSubElement.getContent();
												if (hmChildAttrMap.get(sAttrElementName).equalsIgnoreCase("RoleName")) {
													HashMap<String, String> hmChildTableRole  = new HashMap<String, String>();
													String sRoleDatasource = hmUMConfig.get("Role Datasource");
													if (sAttrSubElementValue.indexOf(sRoleDatasource) != -1) {
														String[] sarrAttrElementValue = sAttrSubElementValue.split(":");
														sAttrSubElementValue = sarrAttrElementValue[sarrAttrElementValue.length - 1 ];
														hmChildTableRole.put("RoleName", lITResourceKey+"~"+sAttrSubElementValue);
														alChildTableRole.add(hmChildTableRole);                                   
													}
												}
												if (hmChildAttrMap.get(sAttrElementName).equalsIgnoreCase("GroupName")) {
													HashMap<String, String> hmChildTableGroup = new HashMap<String, String>();
													String sGroupDatasource = hmUMConfig.get("Group Datasource");

													if (sAttrSubElementValue.indexOf(sGroupDatasource) != -1) {
														String[] sarrAttrElementValue = sAttrSubElementValue.split("\\.");
														sAttrSubElementValue = sarrAttrElementValue[sarrAttrElementValue.length - 1 ];
														hmChildTableGroup.put("GroupName", lITResourceKey + "~" + sAttrSubElementValue);
														alChildTableGroup.add(hmChildTableGroup);
													}
												}				
												oAttrSubElement = oAttrSubElement.getNextElement();
											}
										}
										oAttrElement = oAttrElement.getNextElement();
									}//End of While
								}//End of If
								oChildElement = oChildElement.getNextElement();
							}//End of While - Identifier & Attr
							oResultEntry = oResultEntry.getNextElement();
						} //End of While - Result Entry
					}//End of If - SearchResponse
				}
			}
			if(!alChildTableRole.isEmpty() && alChildTableRole.size() > 0)
				hmSAPGrpRoles.put("Role", alChildTableRole);

			if(!alChildTableGroup.isEmpty() && alChildTableGroup.size() > 0)
				hmSAPGrpRoles.put("Group", alChildTableGroup);			

			hmSAPUserDetails.put("IT Resource", sITResourceName);
			
			/*
			 * If transform mapping is required,then call
			 * transformSingleOrMultivaluedData() to transform the data
			 */
			if (hmConstants.get(YES).equalsIgnoreCase(
					(String) hmUMConfig.get(USE_TRANSFORM_MAPPING))) {
				hmSAPUserDetails = oUtil.transformSingleOrMultivaluedData(
						hmSAPUserDetails, hmSAPGrpRoles, (String) hmUMConfig
								.get(TRANSFORM_LOOKUP));
			}
			/*
			 * If validation is required,then call
			 * validateSingleOrMultivaluedData() to transform the data
			 */
			if (hmConstants.get(YES).equalsIgnoreCase(
					(String) hmUMConfig.get(USE_RECON_VALIDATION))) {
				isValid = oUtil.validateSingleOrMultivaluedData(
						hmSAPUserDetails, hmSAPGrpRoles, (String) hmUMConfig
								.get(VALIDATE_LOOKUP));
			}
			boolean bTargetReconFlag = false;
			boolean bMultivalueFlag = false;
			
			if (isValid) {
			//oUtil.createTargetReconEvent(sITResourceObject, hmSAPUserDetails, hmSAPGrpRoles, sUserID);

				try {
					if (!oUtil.getReconOperAPI().ignoreEvent(sResourceObject,
							hmSAPUserDetails)) {
						bTargetReconFlag = true;
					}
				} catch (tcAPIException e) {
					isValid = false;
					logger.debug(className, MethodName, "Error");
				} catch (tcObjectNotFoundException e) {
					isValid = false;
					logger.error(className, MethodName, e.getMessage());
					logger.setStackTrace(e, className, MethodName, e
							.getMessage());
					throw new ConnectorException(e);
				} catch (Exception e) {
					isValid = false;
				}
				if (isValid && !bTargetReconFlag) {
					Iterator iterator = hmSAPGrpRoles.keySet().iterator();
					while (iterator.hasNext()) {
						String sChildtablename = (String) iterator.next();
						ArrayList alEntitlementList = (ArrayList) hmSAPGrpRoles
								.get(sChildtablename);
						int iSize = alEntitlementList.size();
						HashMap[] hmChildData = new HashMap[iSize];
						for (int i = 0; i < iSize; i++) {
							HashMap hmList = (HashMap) alEntitlementList.get(i);
							hmChildData[i] = hmList;
						}

						try {
							if (!oUtil.getReconOperAPI()
									.ignoreEventAttributeData(sResourceObject,
											hmSAPUserDetails, sChildtablename,
											hmChildData)) {
								bMultivalueFlag = true;
							}
						} catch (tcAPIException e) {
							isValid = false;
							logger
									.error(className, MethodName, e
											.getMessage());
						} catch (tcObjectNotFoundException e) {
							isValid = false;
							logger
									.error(className, MethodName, e
											.getMessage());
							throw new ConnectorException(e);
						}
					}
				}
				if ((bTargetReconFlag || bMultivalueFlag)) {
					if (!isStopRecon && isValid)
						oUtil.createTargetReconEvent(sResourceObject,
								hmSAPUserDetails, hmSAPGrpRoles, sUserID);
				} else {
					logger
							.info("No change in data being reconciled and that present in OIM. "
									+ " Hence not creating Recon Event for user with User Login::"
									+ sUserID);
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
	}
}
