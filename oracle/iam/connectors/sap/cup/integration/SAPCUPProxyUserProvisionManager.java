/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/cup/integration/SAPCUPProxyUserProvisionManager.java /main/1 2010/05/11 03:23:16 ddkumar Exp $ */

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

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.namespace.QName;
import org.apache.axis.client.Service;
import org.apache.axis.client.ServiceFactory;
import org.openspml.v2.util.xml.XmlElement;
import org.openspml.v2.util.xml.XmlParser;
import org.w3c.dom.Document;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.util.DateUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.sap.grc.ws.audit.ArrayOfAuditLogDTO1;
import oracle.iam.connectors.sap.grc.ws.audit.AuditLogDTO;
import oracle.iam.connectors.sap.grc.ws.audit.AuditLogResult;
import oracle.iam.connectors.sap.grc.ws.audit.RequestHistoryDTO;
import oracle.iam.connectors.sap.grc.ws.audit.SAPGRC_AC_IDM_AUDITTRAILVi_Document;
import oracle.iam.connectors.sap.grc.ws.getstatus.RequestStatusDTO;
import oracle.iam.connectors.sap.grc.ws.getstatus.SAPGRC_AC_IDM_REQUESTSTATUSVi_Document;
import oracle.iam.connectors.sap.grc.ws.submitreq.CustomFieldsDTO;
import oracle.iam.connectors.sap.grc.ws.submitreq.RequestDetailsData;
import oracle.iam.connectors.sap.grc.ws.submitreq.RequestSubmissionResult;
import oracle.iam.connectors.sap.grc.ws.submitreq.RoleData;
import oracle.iam.connectors.sap.grc.ws.submitreq.SAPGRC_AC_IDM_SUBMITREQUESTVi_Document;
import oracle.iam.connectors.sap.grc.ws.submitreq.ServiceStatusDTO;
import oracle.iam.connectors.sap.ume.util.UMEUtil;
import oracle.iam.connectors.sap.usermgmt.util.UMConstants;
import oracle.iam.connectors.sap.usermgmt.util.UMUtility;

/**
 * Description: Contains methods to communicate with the target system
 */
/**
 * @author Annaya.MD
 *
 */
/**
 * @author Annaya.MD
 *
 */
public class SAPCUPProxyUserProvisionManager implements UMConstants {

	private static ConnectorLogger logger = new ConnectorLogger(UM_LOGGER);
	private StringUtil StrUtil = new StringUtil();
	private String className = this.getClass().getName();
	private DateUtil dtUtil = new DateUtil(logger);
	private HashMap<String, String> hmConstants;
	private Hashtable<String, String> htGRCITRattr;
	private HashMap<String, String> configurationMap;
	/**
	 * Description:Used to initialize global variables
	 * 
	 * @param jcoConnection
	 *          holds connection details to SAP
	 * @param hmConstants
	 *          HashMap containing constants used in this class
	 * 
	 */
	public SAPCUPProxyUserProvisionManager(HashMap<String, String> hmConstants, 
			HashMap<String, String> configurationMap, 
			Hashtable<String, String> htGRCITRattr) {
		this.hmConstants = hmConstants;
		this.htGRCITRattr = htGRCITRattr;
		this.configurationMap = configurationMap;
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
		String sMethodName = "createCUPRequest()";
		CustomFieldsDTO[] cdto = new CustomFieldsDTO[1];
		RoleData[] roles = new RoleData[1];
		boolean customFlag = false;
		try {
			logger.setMethodStartLog(className, sMethodName);
			UMUtility oUMUtil = new UMUtility(logger, hmConstants);

			logger.debug(className, sMethodName, "Innitialize request details class");
			Class<?> requestDetailsClass = Class
			.forName(hmCUPConstants.get(REQUEST_DETAILS_CLASS));
			logger.debug(className, sMethodName, "Innitialize custom fields class");
			Class<?> customDataClass = Class
			.forName(hmCUPConstants.get(CUSTOM_FIELDS_CLASS));
			logger.debug(className, sMethodName, "Innitialize request details instance");
			requestDetailsInst = requestDetailsClass.newInstance();
			logger.debug(className, sMethodName, "Innitialize method for request type and invoke it");
			sMethodName = oUMUtil.getMethodName(requestDetailsClass,hmCUPConstants.get(requestType),hmCUPConstants.get(set));
			Method method = requestDetailsClass.getDeclaredMethod(sMethodName,
					String.class);
			method.invoke(requestDetailsInst, RequestType);
			logger.debug(className, sMethodName, "Innitialize method for application and invoke it");
			sMethodName = oUMUtil.getMethodName(requestDetailsClass, hmCUPConstants.get(application), hmCUPConstants.get(set));
			method = requestDetailsClass.getDeclaredMethod(sMethodName, String.class);
			method.invoke(requestDetailsInst, Application);
			logger.debug(className, sMethodName, "Innitialize method for priority and invoke it");
			sMethodName = oUMUtil.getMethodName(requestDetailsClass,hmCUPConstants.get(priority), hmCUPConstants.get(set));
			method = requestDetailsClass.getDeclaredMethod(sMethodName, String.class);
			method.invoke(requestDetailsInst,Priority);

			Iterator<String> itr = attrMap.keySet().iterator();
			for(int i =0;itr.hasNext();) {
				sKey = (String) itr.next();
				String[] sKeyVal = sKey.split(";");
				if(sKeyVal[0].equalsIgnoreCase(hmCUPConstants.get(CUSTOM))) {
					customDataInst = customDataClass.newInstance();
					logger.debug(className, sMethodName,"Innitialize custom method for "+ attrMap.get(sKey) +" and invoke it");
					method = customDataClass.getDeclaredMethod(hmCUPConstants.get(setName), String.class);
					method.invoke(customDataInst, sKeyVal[1]);
					method = customDataClass.getDeclaredMethod(hmCUPConstants.get(setValue), String.class);
					method.invoke(customDataInst, attrMap.get(sKey));
					if(i+1 > cdto.length) {
						logger.debug(className, sMethodName, "Dynamically increasing custom array size");
						CustomFieldsDTO[] tempCdto = new CustomFieldsDTO[cdto.length+1];
						System.arraycopy(cdto, 0, tempCdto, 0, cdto.length);
						cdto = tempCdto;
					}
					cdto[i] = (CustomFieldsDTO)customDataInst;
					i++;
					customFlag = true;
					continue;
				}
				logger.debug(className, sMethodName, "Innitialize method for "+ attrMap.get(sKey) +" and invoke it");
				sMethodName = oUMUtil.getMethodName(requestDetailsClass, sKey, hmCUPConstants.get(set));
				method = requestDetailsClass.getDeclaredMethod(sMethodName,
						String.class);
				method.invoke(requestDetailsInst, attrMap.get(sKey));
			}
			if(customFlag) {
				logger.info(className, sMethodName, "Setting custom attributes in request details");
				method = requestDetailsClass.getDeclaredMethod(hmCUPConstants.get(setCustomField), CustomFieldsDTO[].class);
				method.invoke(requestDetailsInst, new Object[]{cdto});
			}

			if(childAttrMap != null) {
				logger.debug(className, sMethodName, "Innitialize role data class");
				Class<?> roleDataClass = Class
				.forName(hmCUPConstants.get(ROLE_DATA_CLASS));
				Iterator<String> itrChild = childAttrMap.keySet().iterator();
				roleDataInst = roleDataClass.newInstance();
				while(itrChild.hasNext()) {
					logger.debug(className, sMethodName, "Innitialize method for "+ childAttrMap.get(sKey) +" and invoke it");
					sKey = (String) itrChild.next();
					sMethodName = oUMUtil.getMethodName(roleDataClass, sKey, hmCUPConstants.get(set));
					method = roleDataClass.getDeclaredMethod(sMethodName,
							String.class);
					method.invoke(roleDataInst, childAttrMap.get(sKey));
				}
				logger.info(className, sMethodName, "Setting role attributes in request details");
				roles[0] = (RoleData)roleDataInst;
				method = requestDetailsClass.getDeclaredMethod(hmCUPConstants.get(setRoles), RoleData[].class);
				method.invoke(requestDetailsInst, new Object[]{roles});
			}
			logger.debug(className, sMethodName, "Creating connection Url");	
			String connectionUrl = "http://" + htCUPITRattributes.get(SERVER) + ":"
			+ htCUPITRattributes.get(PORT)
			+ hmCUPConstants.get(SUBMIT_REQ_WSDL_PATH);
			SAPGRC_AC_IDM_SUBMITREQUESTVi_Document submitRequestDocument = null;
			RequestSubmissionResult result = null;

			URL url = new URL(connectionUrl);
			QName qName = new QName(hmCUPConstants.get(SUBMIT_REQ_NAMESPACE_URI),
					hmCUPConstants.get(SUBMIT_REQ_LOCAL_PART));
			logger.debug(className, sMethodName, "Got the QName instance");
			ServiceFactory factory = new ServiceFactory();
			logger.debug(className, sMethodName, "Got the Service factory instance");
			Service remote = (Service) factory.createService(url, qName);
			logger.debug(className, sMethodName, "Got the Service instance");

			submitRequestDocument = (SAPGRC_AC_IDM_SUBMITREQUESTVi_Document) remote
			.getPort(SAPGRC_AC_IDM_SUBMITREQUESTVi_Document.class);
			logger.debug(className, sMethodName, "Got the Submit Request Document instance");

			logger.info(className, sMethodName, "Submitting Request >..............");
			result = submitRequestDocument.getSubmitRequest(
					(RequestDetailsData) requestDetailsInst, (String) htCUPITRattributes
					.get(username), (String) htCUPITRattributes.get(password));
			if (result != null) {
				logger.info(className, sMethodName, "Request No " + result.getRequestNo());
				ServiceStatusDTO status = (ServiceStatusDTO) result.getStatus();
				sReqNo = result.getRequestNo();
				if(sReqNo == null) {
					logger.error(className, sMethodName, "Message Code " + status.getMsgCode());
					logger.error(className, sMethodName, "Message Description " + status.getMsgDesc());
					logger.error(className, sMethodName, "Message Type " + status.getMsgType());
					throw new ConnectorException(status.getMsgDesc());
				}
				logger.info(className, sMethodName, "Message Code " + status.getMsgCode());
				logger.info(className, sMethodName, "Message Description " + status.getMsgDesc());
				logger.info(className, sMethodName, "Message Type " + status.getMsgType());
			} else {
				logger.error(className, sMethodName, "Result is null");
				throw new ConnectorException();
			}
		} catch (ConnectorException exception) {
			logger.error(className, sMethodName, "ConnectorException:"
					+ exception.getMessage());
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		} catch (Exception e) {
			logger.error(className, sMethodName, "ConnectorException:"
					+ e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return sReqNo;
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
			SAPGRC_AC_IDM_REQUESTSTATUSVi_Document getReqStatusDocInstance=null ;
			RequestStatusDTO result=null;
			try{
				URL url = new URL(connectionUrl);
				QName qName = new QName(hmCUPConstants.get(REQ_STATUS_NAMESPACE_URI),
						hmCUPConstants.get(REQ_STATUS_LOCAL_PART));
				logger.debug(className, MethodName, "Got the QName instance");
				ServiceFactory factory = new ServiceFactory();
				logger.debug(className, MethodName, "Got the Service factory instance");
				Service remote = (Service) factory.createService(url, qName);
				logger.debug(className, MethodName, "Got the Service instance");

				getReqStatusDocInstance = (SAPGRC_AC_IDM_REQUESTSTATUSVi_Document) remote
				.getPort(SAPGRC_AC_IDM_REQUESTSTATUSVi_Document.class);
				logger.debug(className, MethodName, "Got the Submit Request Document instance");

				logger.info(className, MethodName, "Getting Request Status >..............");
				result = getReqStatusDocInstance.getStatus(responseID, "", (String) htCUPITRattributes
						.get(username), (String) htCUPITRattributes.get(password)); 
			}catch(Exception e){
				logger.error(className, MethodName, "ConnectorException:"
						+ e.getMessage());
				logger.setStackTrace(e, className, MethodName, e.getMessage());
				throw new ConnectorException(e);
			}
			if(result != null){
				logger.debug(className, MethodName, "Request No " + result.getRequestNumber());
				logger.info(className, MethodName, "Status: " + result.getStatus());
				logger.info(className, MethodName, "UserName: " + result.getUserName());
				logger.debug(className, MethodName, "DueDate: " + result.getDueDate());
				logger.debug(className, MethodName, "Stage: " + result.getStage());
				sStatus = result.getStatus();
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
			SAPGRC_AC_IDM_AUDITTRAILVi_Document getAuditTrailDocInstance=null ;
			AuditLogResult result=null;
			ArrayOfAuditLogDTO1 resultDTO = null;
			AuditLogDTO auditLogDTO=null;
			RequestHistoryDTO historyDTO = null;
			RequestHistoryDTO[] childDTOs = null;
			try{
				URL url = new URL(connectionUrl);
				QName qName = new QName(hmCUPConstants.get(AUDIT_TRAIL_NAMESPACE_URI),
						hmCUPConstants.get(AUDIT_TRAIL_LOCAL_PART));
				logger.debug(className, MethodName, "Got the QName instance");
				ServiceFactory factory = new ServiceFactory();
				logger.debug(className, MethodName, "Got the Service factory instance");

				Service remote = (Service) factory.createService(url, qName);
				logger.debug(className, MethodName, "Got the Service instance");

				getAuditTrailDocInstance = (SAPGRC_AC_IDM_AUDITTRAILVi_Document) remote
				.getPort(SAPGRC_AC_IDM_AUDITTRAILVi_Document.class);
				logger.debug(className, MethodName, "Got the Audit Trail Document instance");

				logger.info(className, MethodName, "Getting Audit Trail >..............");
				result = getAuditTrailDocInstance.getAuditLogs(responseID, "", "", "", "", "", "", (String) htCUPITRattributes
						.get(username), (String) htCUPITRattributes.get(password)); 
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
				while(resultDTO != null && i<java.lang.reflect.Array.getLength(resultDTO.getAuditLogDTO())) {
					auditLogDTO = (AuditLogDTO) java.lang.reflect.Array.get(resultDTO.getAuditLogDTO(), i);
					logger.debug(className, MethodName, "******** Parsing audit Log Element: " +(i+1)+" ********");
					logger.debug(className, MethodName, "CreateDate: " + auditLogDTO.getCreateDate().get(5)+":"+auditLogDTO.getCreateDate().get(2)+":"+auditLogDTO.getCreateDate().get(1));
					logger.debug(className, MethodName, "RequestedBy: " + auditLogDTO.getRequestedBy());
					logger.debug(className, MethodName, "Status: " + auditLogDTO.getStatus());
					logger.debug(className, MethodName, "SubmittedBy: " + auditLogDTO.getSubmittedBy());
					int k = 0;
					while(k<java.lang.reflect.Array.getLength(auditLogDTO.getRequestHst())) {
						historyDTO = (RequestHistoryDTO) java.lang.reflect.Array.get(auditLogDTO.getRequestHst(), k);
						logger.debug(className, MethodName, "******** Parsing Request history Element: " +(k+1)+" ********");
						childDTOs = historyDTO.getChildDTOs();
						logger.debug(className, MethodName, "Display String: " + historyDTO.getDisplayString());
						logger.debug(className, MethodName, "Action Date: " + historyDTO.getActionDate().get(5)+":"+historyDTO.getActionDate().get(2)+":"+historyDTO.getActionDate().get(1));
						int j = 0;
						while(j<java.lang.reflect.Array.getLength(childDTOs)) {
							historyDTO = (RequestHistoryDTO) java.lang.reflect.Array.get(childDTOs, j);
							logger.debug(className, MethodName, "******** Parsing child Objects Element: " +(j+1)+" ********");
							logger.debug(className, MethodName, "Display String: " + historyDTO.getDisplayString());
							logger.debug(className, MethodName, "Action Date: " + historyDTO.getActionDate().get(5)+":"+historyDTO.getActionDate().get(2)+":"+historyDTO.getActionDate().get(1));
							if(historyDTO.getDisplayString().indexOf("Error Message") != -1) {
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

	public boolean findUser(String sUserId) throws ConnectorException{
		String MethodName = "findUser()";
		boolean flag = false;
		String sObjectClass = "sapuser";

		UMEUtil oUMEUtil = new UMEUtil(htGRCITRattr);
		StringBuffer sSOAPReq = new StringBuffer();

		ArrayList<String> alFilter = new ArrayList<String>();
		alFilter.add("datasource equals R3_DATASOURCE");
		alFilter.add("logonname equals " + sUserId);

		try {
			String sSOAPXml = sSOAPReq.append(oUMEUtil.getSOAPEnvStartTag())
			.append(oUMEUtil.searchRequestTag(sObjectClass, null, null, alFilter))
			.append(oUMEUtil.getSOAPEnvEndTag()).toString();

			logger.info (className, MethodName, "Request : " + sSOAPXml);
			String toReturn = oUMEUtil.getSOAPResponse(sSOAPXml);
			logger.info (className, MethodName, "Response: " + toReturn);

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
									if (sAttrValue.indexOf(sUserId) != -1) 
										flag = true;
								}//End of If
								oChildElement = oChildElement.getNextElement();
							}//End of While - Identifier & Attr
							oResultEntry = oResultEntry.getNextElement();
						} //End of While - Result Entry
					}//End of If - SearchResponse
				}
			}
		}catch (Exception exception) {
			logger.error(className, MethodName, "ConnectorException:"
					+ exception.getMessage());
			logger.setStackTrace(exception, className, MethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		}


		return flag;
	}

	public String modifyPassword(String sUserId, String sDummyPassword, String sPassword) throws ConnectorException{
		String MethodName = "modifyPassword()";
		Boolean flag = false;
		String sResponse = PASSWORD_CHANGE_SUCCESSFUL;
		UMEUtil oUMEUtil = new UMEUtil(htGRCITRattr);
		StringBuffer sSOAPReq = new StringBuffer();

		try {
			String sSOAPXml = sSOAPReq.append(oUMEUtil.getSOAPEnvStartTag())
			.append(oUMEUtil.modifyPasswordTag(sUserId, sDummyPassword,sPassword))
			.append(oUMEUtil.getSOAPEnvEndTag()).toString();

			String toReturn = oUMEUtil.getSOAPResponse(sSOAPXml);
			logger.info (className, MethodName, toReturn);

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
					if (tag.equalsIgnoreCase("modifyResponse")) {
						oResultEntry = oSOAPMSg.getChildElement();
						while (oResultEntry != null) {
							oChildElement = oResultEntry.getChildElement();
							while(oChildElement !=null){
								if(oChildElement.getTagName().equals("errorMessage")){
									oAttrElement = oChildElement.getChildElement();
									String sAttrValue = oChildElement.getContent();
									flag = true;
								}//End of If
								oChildElement = oChildElement.getNextElement();
							}//End of While - Identifier & Attr
							oResultEntry = oResultEntry.getNextElement();
						} //End of While - Result Entry
					}//End of If - SearchResponse
				}
			}
		} catch (Exception exception) {
			logger.error(className, MethodName, "ConnectorException:"
					+ exception.getMessage());
			logger.setStackTrace(exception, className, MethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		}

		return sResponse;
	}

	public void resetPassword(String sUserId, String sDummyPassword) throws ConnectorException{
		String MethodName = "resetPassword()";		
		UMEUtil oUMEUtil = new UMEUtil(htGRCITRattr);
		StringBuffer sSOAPReq = new StringBuffer();
		Boolean flag = false;
		try {
			String sSOAPXml = sSOAPReq.append(oUMEUtil.getSOAPEnvStartTag())
			.append(oUMEUtil.resetPasswordTag(sUserId, sDummyPassword))
			.append(oUMEUtil.getSOAPEnvEndTag()).toString();

			String toReturn = oUMEUtil.getSOAPResponse(sSOAPXml);
			logger.info (className, MethodName, "Response : " + toReturn);
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
					if (tag.equalsIgnoreCase("modifyResponse")) {
						oResultEntry = oSOAPMSg.getChildElement();
						while (oResultEntry != null) {
							oChildElement = oResultEntry.getChildElement();
							while(oChildElement !=null){
								if(oChildElement.getTagName().equals("errorMessage")){
									oAttrElement = oChildElement.getChildElement();
									String sAttrValue = oChildElement.getContent();
									flag = true;
								}//End of If
								oChildElement = oChildElement.getNextElement();
							}//End of While - Identifier & Attr
							oResultEntry = oResultEntry.getNextElement();
						} //End of While - Result Entry
					}//End of If - SearchResponse
				}
			}
		}catch (Exception exception) {
			logger.error(className, MethodName, "ConnectorException:"
					+ exception.getMessage());
			logger.setStackTrace(exception, className, MethodName, exception
					.getMessage());
			throw new ConnectorException(exception);
		}

	}
}

