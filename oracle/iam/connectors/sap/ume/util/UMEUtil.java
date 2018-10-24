package oracle.iam.connectors.sap.ume.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.sap.ume.util.UMEConstants;

import org.openspml.v2.transport.RPCRouterMonitor;

/**
 * Description: 
 * This class used to process the SOAP messages and does the following functions
 * 		1. Construction of SOAP request 
 * 		2. Gets the SAP UME system connection
 * 		3. Process the SOAP request
 * 		4. Get the SOAP response
 * 		5. Password Reset
 * 
 * @author MPHASIS - Oracle Identity Manager Team
 */
public class UMEUtil implements UMEConstants {

	private static ConnectorLogger logger = new ConnectorLogger(UM_LOGGER);
	Hashtable<String, String> htITRattributes;
	private String className = this.getClass().getName();

	public UMEUtil(Hashtable <String, String> htITAttributes) {
		this.htITRattributes = htITAttributes;
	}

	/**
	 * This method used to construct starting part of SOAP Request
	 *  
	 * @return starting part of request as String
	 * @throws ConnectorException
	 */
	public String getSOAPEnvStartTag() throws ConnectorException {
		String sMethodName = "getSOAPEnvStartTag()";
		StringBuffer sbEvnStart = new StringBuffer();

		try {
			sbEvnStart.append("<?xml version='1.0' encoding='UTF-8'?>")
			.append("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">")
			.append("<SOAP-ENV:Header/>")
			.append("<SOAP-ENV:Body>");
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
		return sbEvnStart.toString();
	}
	
	/**
	 * Description: Constructs the end part of SOAP Request
	 * 
	 * @return end part of request as String
	 */
	public String getSOAPEnvEndTag(){
		String sMethodName = "getSOAPEnvEndTag()";
		StringBuffer sbEvnEnd = new StringBuffer();
		try {
			sbEvnEnd.append("</SOAP-ENV:Body>")
			.append("</SOAP-ENV:Envelope>");
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
		return sbEvnEnd.toString();
	}

	/**
	 * Description: This method used to form the search request.
	 * 
	 * @param sObjClass - Object class can be either sapuser, 
	 * saprole, and sapgroup
	 * @param hmAttrs - HashMap contains lists of attributes 
	 * that are queried from SAP UME system. 
	 * @param hmChildAttrs - HashMap contains lists of child attributes 
	 * that is Group Name and Role Name
	 * @param alFilter - Contains filter criteria  
	 * 
	 * @return SPML Response in XML
	 * 
	 * @throws ConnectorException
	 */
	public String searchRequestTag(String sObjClass, 
			HashMap<String, String> hmAttrs, 
			HashMap<String, String> hmChildAttrs,
			ArrayList<String> alFilter) throws ConnectorException {
		StringBuffer sbSearchReq = new StringBuffer();
		String sMethodName = "searchRequestTag";
		try {
			sbSearchReq.append("<spml:searchRequest xmlns:spml='urn:oasis:names:tc:SPML:1:0' xmlns:dsml='urn:oasis:names:tc:DSML:2:0:core'>" )
			.append("<spml:searchBase type='urn:oasis:names:tc:SPML:1:0#GenericString'>")
			.append("<spml:id>" )
			.append(sObjClass.trim())
			.append("</spml:id>" )
			.append("</spml:searchBase>")
			.append(searchFilter(alFilter));

			if((hmAttrs != null && hmAttrs.size() > 0) || 
					(hmChildAttrs != null && hmChildAttrs.size() > 0)){
				sbSearchReq.append("<attributes>");
			}
			if (hmAttrs != null) {
				Iterator<String> itrAttr = hmAttrs.keySet().iterator();

				while (itrAttr.hasNext()) {
					sbSearchReq.append("<attribute name=\"" + itrAttr.next().trim() +"\"/>");
				}
			}
			if (hmChildAttrs != null) {
				Iterator<String> itrAttr = hmChildAttrs.keySet().iterator();
				while (itrAttr.hasNext()) {
					sbSearchReq.append("<attribute name=\"" + itrAttr.next().trim() +"\"/>");
				}
			}
			if((hmAttrs != null && hmAttrs.size() > 0)|| 
					(hmChildAttrs != null && hmChildAttrs.size() > 0)){
				sbSearchReq.append("</attributes>");
			}
			sbSearchReq.append("</spml:searchRequest>"); 
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
		return sbSearchReq.toString();
	}

	/**
	 * Description: Forms the filter crietria for SPML Request
	 * 
	 * @param alFilter - Filter criteria
	 * @return xml
	 * @throws ConnectorException
	 */
	public String searchFilter(ArrayList<String> alFilter) throws ConnectorException
	{
		String sMethodName = "searchFilter()";
		StringBuffer sbSearchFilter = new StringBuffer();
		try {
			int iFilterSize = alFilter.size();

			if (iFilterSize > 0 ) {
				sbSearchFilter.append("<filter>" )
				.append(iFilterSize > 1?"<and>":"");

				for (int i=0; i< iFilterSize; i++){
					sbSearchFilter.append(getFilterElement(alFilter.get(i)));
				}
				sbSearchFilter.append(iFilterSize > 1?"</and>":"")
				.append("</filter>");
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

		return sbSearchFilter.toString();
	}
	
	/**
	 * Description: It used to construct the filter criteria
	 * 
	 * @param sCondition
	 * @return
	 * @throws ConnectorException
	 */
	public String getFilterElement(String sCondition) throws ConnectorException {
		String sMethodName = "getFilterElement()";
		StringBuffer sFilterElement = new StringBuffer();
		String[] sAttrSplit = new String[2];
		try {
			if (sCondition.indexOf("equals") != -1) {
				sAttrSplit = sCondition.split("equals");
				sFilterElement.append("<equalityMatch name=\"")
				.append(sAttrSplit[0].trim())
				.append("\"><value>")
				.append(sAttrSplit[1].trim()) 
				.append("</value></equalityMatch>");
			}else if(sCondition.indexOf("startsWith") != -1){
				sAttrSplit = sCondition.split("startsWith");
				sFilterElement.append("<substrings name=\"")
				.append(sAttrSplit[0].trim())
				.append("\"><initial>")
				.append(sAttrSplit[1].trim()) 
				.append("</initial></substrings>");			
			} else if(sCondition.indexOf("endsWith") != -1){
				sAttrSplit = sCondition.split("endsWith");
				sFilterElement.append("<substrings name=\"")
				.append(sAttrSplit[0].trim())
				.append("\"><final>")
				.append(sAttrSplit[1].trim()) 
				.append("</final></substrings>");			
			} else if(sCondition.indexOf("like") != -1){
				sAttrSplit = sCondition.split("like");
				sFilterElement.append("<substrings name=\"")
				.append(sAttrSplit[0].trim())
				.append("\"><any>")
				.append(sAttrSplit[1].trim()) 
				.append("</any></substrings>");			
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
		return sFilterElement.toString();

	}

	/**
	 * Description: Get the SAP UME connection 
	 * 
	 * @param sUrl - SAP UME URL
	 * @param sUsername - User name
	 * @param sPassword - Password
	 * @return connection object
	 * @throws ConnectorException
	 */
	public HttpURLConnection connect(String sUrl, final String sUsername, final String sPassword) throws ConnectorException {
		String sMethodName = "connect()";
		URLConnection httpConn;
		try {
			URL url = new URL(sUrl);
			URLConnection connection = url.openConnection();
			httpConn = (HttpURLConnection) connection;

			if (sUsername != null && sPassword != null) {
				Authenticator.setDefault(new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(sUsername,
								sPassword.toCharArray());
					}
				});
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

		return (HttpURLConnection) httpConn;
	}

	/**
	 * Description: Returns the SOAP response
	 * 
	 * @param sSOAPRequest 
	 * @return returns SOAP response
	 * @throws ConnectorException
	 */
	
	public String getSOAPResponse(String sSOAPRequest) throws ConnectorException {
		String sMethodName = "getSOAPResponse()";
		RPCRouterMonitor monitor = null;
		String toReturn;
		try {
			String sUrl = htITRattributes.get("UME URL");
			String sUserName = htITRattributes.get("Admin User ID");
			String sPassword = htITRattributes.get("Admin Password");

			HttpURLConnection httpConn =  connect(sUrl, sUserName, sPassword);

			byte[] xmlBytes = sSOAPRequest.getBytes();
			httpConn.setRequestProperty("Content-Length",String.valueOf(xmlBytes.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
			httpConn.setRequestProperty("SOAPAction", "POST");
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);	

			if (monitor != null)
				monitor.send(sSOAPRequest);

			OutputStream out = httpConn.getOutputStream();
			out.write(xmlBytes);
			out.close();

			InputStreamReader isr =
				new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);

			String inputLine;
			StringBuffer result = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				result.append(inputLine);

			in.close();

			toReturn =  result.length() == 0 ? null : result.toString();

			if (monitor != null)
				monitor.receive(toReturn);
		}
		catch (ConnectorException exception) {
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

		return toReturn;
	}

	/**
	 * Description: Reset the users password and it take only new password
	 * 
	 * @param sUserId 
	 * @param sDummyPassword
	 * @return
	 * @throws ConnectorException
	 */
	public String resetPasswordTag(String sUserId, String sDummyPassword) throws ConnectorException {
		String sMethodName = "resetPasswordTag()";
		StringBuffer sbModifyReq = new StringBuffer();
		try {
			sbModifyReq.append("<modifyRequest requestID=\"OIMREQ_1\">" )
			.append("<identifier type=\"GenericString\">")
			.append("<id>SPML.SAPUSER." )
			.append(sUserId)
			.append("</id>" )
			.append("</identifier>" )
			.append("<modifications>")
			.append("<modification name=\"password\"><value>")
			.append(sDummyPassword)
			.append("</value></modification>")
			.append("</modifications>")
			.append("</modifyRequest>");
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
		return sbModifyReq.toString();
	}

	/**
	 * Description: Modify the users password and it takes new & old password
	 * 
	 * @param sUserId
	 * @param sDummyPassword
	 * @param sPassword
	 * @return
	 * @throws ConnectorException
	 */
	public String modifyPasswordTag(String sUserId, String sDummyPassword, String sPassword) throws ConnectorException {
		String sMethodName = "modifyPasswordTag()";
		StringBuffer sbModifyReq = new StringBuffer();
		try {
			sbModifyReq.append("<modifyRequest requestID=\"OIMREQ_1\">" )
			.append("<identifier type=\"GenericString\">")
			.append("<id>SPML.SAPUSER." )
			.append(sUserId)
			.append("</id>" )
			.append("</identifier>" )
			.append("<modifications>")
			.append("<modification name=\"oldpassword\"><value>")
			.append(sDummyPassword)
			.append("</value></modification>")
			.append("<modification name=\"password\"><value>")
			.append(sPassword)
			.append("</value></modification>")
			.append("</modifications>")
			.append("</modifyRequest>");
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
		return sbModifyReq.toString();
	}
}
