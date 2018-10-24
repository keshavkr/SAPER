/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/common/connection/SAPConnection.java /main/9 2016/06/27 23:39:42 vsantosh Exp $ */

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
 ddkumar     01/30/09 - This class is used for connecting to SAP target
 system
 ddkumar     01/30/09 - Creation
 */

/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/common/connection/SAPConnection.java /main/9 2016/06/27 23:39:42 vsantosh Exp $
 *  @author  ddkumar 
 *  @since   release specific (what release of product did this appear in)
 */

package oracle.iam.connectors.sap.common.connection;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.util.StringUtil;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;

/**
 * Description: Creates and destroys connections with the target system
 */
public class SAPConnection implements SAPConstants {
	private ConnectorLogger logger = null;
	private String className = this.getClass().getName();
	private static JCoDestination jcoConnection;
	Properties connectProperties = new Properties();
	public static SAPDestinationDataProvider provider = null;

	/**
	 * Description : Class constructor method
	 * 
	 * @param logger
	 *            initialize logger from calling method
	 */
	public SAPConnection(ConnectorLogger logger) {
		this.logger = logger;
		provider = SAPDestinationDataProvider.getInstance();
	}

	/**
	 * Description : Registers the properties provided and starts the connection
	 * to the SAP system
	 * 
	 * @param DESTINATION_NAME
	 *            The host name of the SAP system to connect. For Example:
	 *            172.16.0.10
	 */
	public JCoDestination createSAPConnection(String DESTINATION_NAME)
			throws Exception {
		String sMethodName = "createSAPConnection()";
		logger.setMethodStartLog(className, sMethodName);
		logger.info(className, sMethodName, " SAP Create Connection Request.");
		JCoDestination mConnection;
		try {
			if (!SAPDestinationDataProvider.isRegister) {
				try {
					com.sap.conn.jco.ext.Environment
							.registerDestinationDataProvider(provider);
				} catch (IllegalStateException e) {
					logger.setStackTrace(e, className, sMethodName, e
							.getMessage());
					logger.info(className, sMethodName,
							"Please wait for the running task to complete");
				}
				SAPDestinationDataProvider.isRegister = true;
			}
			mConnection = JCoDestinationManager
					.getDestination(DESTINATION_NAME);
			JCoContext.begin(mConnection);
			logger.info(className, sMethodName,
					" Completed SAP Connection Creation.");
		} catch (Exception exception) {
			logger.error(className, sMethodName, " SAP Connection Exception");
			logger.setStackTrace(exception, className, sMethodName, exception
					.getMessage());
			throw new Exception(" SAP Connection Exception", exception);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return mConnection;
	}

	/**
	 * Description : Close the connection with the SAP system
	 * 
	 * @param mConnection
	 *            Connection Handler
	 * @throws Exception
	 * 
	 */
	public JCoDestination closeSAPConnection(JCoDestination mConnection)
			throws Exception {
		String sMethodName = "closeSAPConnection()";
		logger.setMethodStartLog(className, sMethodName);
		logger.info(className, sMethodName, "Start SAP Connection Close.");
		if (mConnection != null) {
			try {
				JCoContext.end(mConnection);
				logger.info(className, sMethodName,
						"SAP Connection Close Successfull.");
			} catch (JCoException jcoException) {
				logger.error(className, sMethodName,
						"SAP Connection Close Fail:"
								+ jcoException.getMessage());
				logger.setStackTrace(jcoException, className, sMethodName,
						jcoException.getMessage());
				throw new Exception("SAP closeConnection JCO Exception",
						jcoException);
			} catch (Exception exception) {
				logger.error(className, sMethodName,
						"SAP closeConnection Exception");
				logger.setStackTrace(exception, className, sMethodName,
						exception.getMessage());
				throw new Exception("SAP closeConnection Exception", exception);
			}
		}
		logger.setMethodFinishLog(className, sMethodName);
		return mConnection;
	}

	/**
	 * Description: Creates property from IT Resource attributes required to
	 * connect to the respective SAP system
	 * 
	 * @param htITResource
	 *            Contains IT Resource values mapping in HashMap with code key
	 *            as JCO parameter and decode as IT Resource attribute name
	 * @param htConnectionParam
	 *            Holds SAP connection values in HashMap with code key as JCO
	 *            parameter and decode as value entered for the IT Resource
	 *            parameter
	 * @return JCoDestination
	 * 
	 * @throws ConnectorException
	 */
	public JCoDestination addDestination(HashMap<String, String> htITResource,
			Hashtable<String, String> htConnectionParam)
			throws ConnectorException {
		String sITRAttrName = "";
		String sMethodName = "addDestination()";
		try {
			logger.setMethodStartLog(className, sMethodName);
			StringUtil oStringUtil = new StringUtil();
			int iClientPropLength = SAPConstants.clientProperties.length;
			String sHostName = htConnectionParam.get("App server host");
			for (int i = 0; i < iClientPropLength; i++) {
				sITRAttrName = htITResource
						.get(SAPConstants.clientProperties[i]);
				if (oStringUtil.isEmpty(sITRAttrName))
					continue;
				String sItAttr = htConnectionParam.get(sITRAttrName);
				if (oStringUtil.isEmpty(sItAttr))
					continue;
				connectProperties.setProperty(SAPConstants.clientProperties[i],
						sItAttr);
			}
			SAPConnection.provider.addDestination(sHostName, connectProperties);
			jcoConnection = createSAPConnection(sHostName);
		} catch (Exception bException) {
			logger.error(className, sMethodName,
					"SAP AddDestination Exception.");
			logger.setStackTrace(bException, className, sMethodName, bException
					.getMessage());
			throw new ConnectorException(bException.getMessage());
		}
		logger.setMethodFinishLog(className, sMethodName);
		return jcoConnection;
	}
	//Start ::BUG 23344445 - SAP-ER: Support for IDOC XML 
	public static JCoDestination getJcoConnection() {
		return jcoConnection;
	}
	//ENd ::BUG 23344445 - SAP-ER: Support for IDOC XML 
}
