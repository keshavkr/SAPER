package oracle.iam.connectors.sap.common.connection;

import java.util.HashMap;
import java.util.Properties;

import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.util.StringUtil;

import com.oracle.oim.gcp.exceptions.ResourceConnectionCloseException;
import com.oracle.oim.gcp.exceptions.ResourceConnectionCreateException;
import com.oracle.oim.gcp.exceptions.ResourceConnectionValidationxception;
import com.oracle.oim.gcp.resourceconnection.ResourceConnection;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;

/**
 * Description: Provides connection pooling features
 */
public class SAPResourceImpl implements ResourceConnection {
	private String className = this.getClass().getName();
	private ConnectorLogger logger = new ConnectorLogger(className);
	public JCoDestination mConnection;
	SAPConnection sConnection = null;

	/**
	 * Description: Creates a SAPResourceImpl object
	 */
	public SAPResourceImpl() {
		super();
	}

	/**
	 * Description: Closes the connection with the target system
	 * 
	 * @throws ResourceConnectionCloseException
	 *             If an exception occurs while closing the resource connection
	 */
	public void closeConnection() throws ResourceConnectionCloseException {
		sConnection = new SAPConnection(logger);
		try {
			sConnection.closeSAPConnection(mConnection);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceConnectionCloseException(e);
		}

	}

	/**
	 * Description: Creates a resource connection object for the corresponding
	 * IT Resource connection parameters passed
	 * 
	 * @param hmHashConnectionParam
	 *            Contains the IT resource connection parameters.
	 * 
	 * @return Returns a resource connection object
	 * 
	 * @throws ResourceConnectionCreateException
	 *             If an error occurs while creating the resource connection
	 */
	public ResourceConnection createConnection(HashMap hmHashConnectionParam)
			throws ResourceConnectionCreateException {
		// ResourceConnection r = null;
		sConnection = new SAPConnection(logger);
		StringUtil oStringUtil = new StringUtil();
		// setting the connection properties based on the defined IT parameters
		// for this connector
		Properties connectProperties = new Properties();
		String sHostName = (String) hmHashConnectionParam
				.get("App server host");
		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST,
				(String) hmHashConnectionParam.get("App server host"));
		connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,
				(String) hmHashConnectionParam.get("System number"));
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT,
				(String) hmHashConnectionParam.get("Client logon"));
		connectProperties.setProperty(DestinationDataProvider.JCO_USER,
				(String) hmHashConnectionParam.get("Admin logon"));
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD,
				(String) hmHashConnectionParam.get("Admin password"));
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG,
				(String) hmHashConnectionParam.get("Language"));

		if (((String) hmHashConnectionParam.get("SNC mode"))
				.equalsIgnoreCase("yes")) {
			connectProperties.setProperty(DestinationDataProvider.JCO_SNC_MODE,
					"1");
			connectProperties.setProperty(
					DestinationDataProvider.JCO_SNC_PARTNERNAME,
					(String) hmHashConnectionParam.get("SNC partner name"));
			connectProperties.setProperty(DestinationDataProvider.JCO_SNC_QOP,
					(String) hmHashConnectionParam.get("SNC qop"));
			connectProperties.setProperty(
					DestinationDataProvider.JCO_SNC_MYNAME,
					(String) hmHashConnectionParam.get("SNC my name"));
			connectProperties.setProperty(
					DestinationDataProvider.JCO_SNC_LIBRARY,
					(String) hmHashConnectionParam.get("SNC lib"));
		} else {
			connectProperties.setProperty(DestinationDataProvider.JCO_SNC_MODE,
					"0");
		}
		// For Logon Groups
		if (!oStringUtil.isEmpty((String) hmHashConnectionParam.get("R3 name"))) {
			connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME,
					(String) hmHashConnectionParam.get("R3 name"));
		}
		if (!oStringUtil.isEmpty((String) hmHashConnectionParam
				.get("Message server"))) {
			connectProperties.setProperty(DestinationDataProvider.JCO_MSSERV,
					(String) hmHashConnectionParam.get("Message server"));
		}
		if (!oStringUtil.isEmpty((String) hmHashConnectionParam
				.get("Logon group name"))) {
			connectProperties.setProperty(DestinationDataProvider.JCO_GROUP,
					(String) hmHashConnectionParam.get("Logon group name"));
		}
		if ((((String) hmHashConnectionParam.get("Language"))
				.equalsIgnoreCase("ja"))
				&& (((String) hmHashConnectionParam.get("SNC mode"))
						.equalsIgnoreCase("no"))) {
			connectProperties.setProperty(DestinationDataProvider.JCO_CODEPAGE,
					"8000");
		}
		// passing the host and destination's connection properties to the SAP
		// target
		SAPConnection.provider.addDestination(sHostName, connectProperties);
		try {
			mConnection = sConnection.createSAPConnection(sHostName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Description: Check the availability of the Connection object
	 * 
	 * @throws ResourceConnectionValidationxception
	 *             If any error occurs during connection validation.
	 */
	public void heartbeat() throws ResourceConnectionValidationxception {
		String methodName = "heartbeat";
		String sHostName = this.mConnection.getApplicationServerHost();
		logger
				.info(className, methodName, "["
						+ this.getClass().getSimpleName() + "][sHostName] "
						+ sHostName);
	}

	/**
	 * Description: Validates the availability of the Connection object
	 * 
	 * @return true if the Connection object is available, otherwise, returns
	 *         false
	 */
	public boolean isValid() {
		boolean isValid = false;
		String methodName = "isValid";
		try {
			this.mConnection.ping();
			isValid = true;
		} catch (JCoException e) {

		}
		logger.info(className, methodName, "["
				+ this.getClass().getSimpleName() + "][isValid] " + isValid);
		return isValid;

	}

}
