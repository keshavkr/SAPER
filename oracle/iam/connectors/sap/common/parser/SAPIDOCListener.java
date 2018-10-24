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
 ddkumar     01/30/09 - This class will be mapped to task scheduler that is
 configured for incremental recon for processing the IDOC contents
 ddkumar     01/30/09 - Creation
 K S Santosh 05/25/12 - BUG 14075167 - SAME PROFILE SET WHEN RECON FROM TWO SAP ER LISTENERS
 */
/**
 *  @author  ddkumar
 *  @since   release specific (what release of product did this appear in)
 */
package oracle.iam.connectors.sap.common.parser;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import javax.security.auth.login.LoginContext;

import javax.security.auth.login.LoginException;



import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.dao.OIMUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.common.vo.ITResource;
import oracle.iam.connectors.common.vo.ScheduledTask;
import oracle.iam.connectors.sap.common.connection.SAPDestinationDataProvider;
import oracle.iam.connectors.sap.common.connection.SAPServerDataProvider;
import oracle.iam.connectors.sap.common.util.SAPUtil;
import oracle.iam.connectors.sap.hrms.util.HRMSConstants;
import Thor.API.tcUtilityFactory;

import Thor.API.Exceptions.tcAPIException;

import Thor.API.Operations.tcFormDefinitionOperationsIntf;
import Thor.API.Operations.tcFormInstanceOperationsIntf;
import Thor.API.Operations.tcITResourceInstanceOperationsIntf;
import Thor.API.Operations.tcLookupOperationsIntf;
import Thor.API.Operations.tcObjectOperationsIntf;
import Thor.API.Operations.tcReconciliationOperationsIntf;
import Thor.API.Operations.tcSchedulerOperationsIntf;
import Thor.API.Operations.tcUserOperationsIntf;
import Thor.API.Security.LoginHandler.websphereLoginSession;




import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocDocumentIterator;
import com.sap.conn.idoc.IDocDocumentList;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.idoc.jco.JCoIDocHandler;
import com.sap.conn.idoc.jco.JCoIDocHandlerFactory;
import com.sap.conn.idoc.jco.JCoIDocServer;
import com.sap.conn.idoc.jco.JCoIDocServerContext;
import com.sap.conn.jco.monitor.JCoServerMonitor;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerContextInfo;
import com.sap.conn.jco.server.JCoServerErrorListener;
import com.sap.conn.jco.server.JCoServerExceptionListener;
import com.sap.conn.jco.server.JCoServerState;
import com.sap.conn.jco.server.JCoServerStateChangedListener;
import com.sap.conn.jco.server.JCoServerTIDHandler;
import com.thortech.xl.crypto.tcCryptoUtil;

import com.thortech.xl.crypto.tcSignatureMessage;

import com.thortech.xl.scheduler.tasks.SchedulerBaseTask;
import com.thortech.xl.util.config.ConfigurationClient;


/**
 * Description: Mapped to scheduled task that is configured for incremental
 * recon for processing the IDOC contents
 */

public class SAPIDOCListener extends SchedulerBaseTask implements HRMSConstants {
	private String className = this.getClass().getName();
	//private boolean isStopRecon = false;
	private boolean isValid = false;
	private Hashtable htITRattributes;
	private HashMap htITRMapping;
//Modified below line for bug 14075167
	private HashMap htHRMSConfig;
	//private static HashMap htHRMSConfig;
	private static HashMap hmConstants;
	private static ConnectorLogger logger = new ConnectorLogger(HRMS_LOGGER);
	private static OIMUtil oUtil = null;
//Modified below line for bug 14075167
	private Hashtable htScheduleAttributes;
	//private static Hashtable htScheduleAttributes;
	private static int iAppend = 0;
	private StringUtil stringUtil = new StringUtil();
	public static SAPDestinationDataProvider destProvider;
	public static SAPServerDataProvider serverProvider;
	JCoIDocServer server;
	public static tcUtilityFactory m_utilFactory = null;

	


	/**
	 * Description: Called each time the scheduled task runs and initializes the
	 * scheduled task attributes and lookup definitions. It also validates
	 * whether all the required parameters are correctly set in the scheduled
	 * task.
	 */
	public void init() {
		String sMethodName = "init()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			destProvider = SAPDestinationDataProvider.getInstance();
			serverProvider = SAPServerDataProvider.getInstance();
			iAppend++;
			// Initialise all OIM API's
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
			tcLookupOperationsIntf lookIntf = (tcLookupOperationsIntf) super
					.getUtility(LOOKUP_API);
			tcReconciliationOperationsIntf reconOperAPI = (tcReconciliationOperationsIntf) super
					.getUtility(RECON_API);
			// oUtil contains all the API references
			oUtil = new OIMUtil(userAPI, objAPI, formAPI, formDefAPI, resAPI,
					schedulerAPI, lookIntf, reconOperAPI, logger);

			if (schedulerAPI == null) {
				throw new ConnectorException(
						"Scheduler API is not getting initialised");
			}
			/*
			 * The providers have to be registered. The providers contain
			 * connection parameters
			 */
			if (!SAPDestinationDataProvider.isRegister) {
				com.sap.conn.jco.ext.Environment
						.registerDestinationDataProvider(destProvider);
				SAPDestinationDataProvider.isRegister = true;
			}

			if (!SAPServerDataProvider.isRegister) {
				com.sap.conn.jco.ext.Environment
						.registerServerDataProvider(serverProvider);
				SAPServerDataProvider.isRegister = true;
			}

			String sScheduleTaskName = super.getAttribute(SCHEDULE_TASK_NAME);

			if (stringUtil.isEmpty(sScheduleTaskName)) {
				throw new ConnectorException(
						"Task Scheduler Name value is not set in task scheduler");
			}

			ScheduledTask reconAttributes = new ScheduledTask(
					sScheduleTaskName, logger);
			htScheduleAttributes = reconAttributes
					.getScheduledTaskDetails(schedulerAPI);
			boolean isMandatoryTaskAttrSet = reconAttributes
					.validateMandatoryTaskAttrs(mandatoryListenerSchedulerAttrs);

			if (!isMandatoryTaskAttrSet) {
				throw new ConnectorException(
						"Mandatory Task Scheduler values not set");
			}
			// Initialise HashMap for Configuration Lookup and Constant Lookup
			String sConfigLookup = (String) htScheduleAttributes
					.get(CONFIGURATION_LOOKUP);
			htHRMSConfig = oUtil.getLookUpMap(sConfigLookup);
			hmConstants = oUtil.getLookUpMap((String) htHRMSConfig
					.get(CONSTANTS_LOOKUP));
			// Verify and validate IT Resource parameters for Listener
			// configuration
			// and task sceduler parameters also
			String sCustomQuery = (String) htScheduleAttributes
					.get(CUSTOM_QUERY);
			if (!stringUtil.isEmpty(sCustomQuery)) {
				boolean isMandatoryCustomAttrSet = reconAttributes
						.validateMandatoryTaskAttrs(mandatoryCustomReconAttr);
				if (!isMandatoryCustomAttrSet) {
					throw new ConnectorException(
							"Task Scheduler Attribute Custom Query Lookup values not set");
				}
				SAPUtil oSAPUtil = new SAPUtil(logger);
				String sAttributeMapingLookup = (String) htScheduleAttributes
						.get(ATTRIBUTE_MAPPING_LOOKUP);
				HashMap hmAttributeMapforCustomRecon = oUtil
						.getLookUpMap(sAttributeMapingLookup);
				oSAPUtil.validateQuery(sCustomQuery,
						hmAttributeMapforCustomRecon);
			}
			String sEmpTypeQuery = (String) htScheduleAttributes
					.get(EMPLOYEE_TYPE_QUERY);
			if (!stringUtil.isEmpty(sEmpTypeQuery)) {
				SAPUtil oSAPUtil = new SAPUtil(logger);
				HashMap<String, String> hmMapforUserType = new HashMap<String, String>();
				hmMapforUserType.put((String) hmConstants.get(EMPLOYEE_GROUP),
						"");
				hmMapforUserType.put((String) hmConstants
						.get(EMPLOYEE_SUB_GROUP), "");
				oSAPUtil.validateQuery(sEmpTypeQuery, hmMapforUserType);
			}
			String sITResName = (String) htScheduleAttributes
					.get(IT_RESOURCE_NAME);

			ITResource oITResource = new ITResource(sITResName, resAPI, logger);
			htITRattributes = oITResource.getITResourceDetails();
			htHRMSConfig = oUtil.getLookUpMap(sConfigLookup);
			htITRMapping = oUtil.getLookUpMap((String) htHRMSConfig
					.get(IT_RESOURCE_MAPPING));
			boolean isMandatoryITRSet = oITResource
					.validateMandatoryITResource(mandatoryITRes);
			if (!isMandatoryITRSet) {
				throw new ConnectorException(
						"Mandatory IT Resource values not set");
			}
			boolean isMandatorySNCITRSet = oITResource
					.validateConditionalMandatory(mandatoryITResSnc,
							"SNC mode;yes", ";");
			if (!isMandatorySNCITRSet) {
				throw new ConnectorException(
						"SNC Related Mandatory IT Resource values not set");
			}
			boolean isMandatoryListenerITRSet = oITResource
					.validateMandatoryITResource(mandatoryITResListener);
			if (!isMandatoryListenerITRSet) {
				throw new ConnectorException(
						"Mandatory  Listener related IT Resource values not set");
			}
			logger.info("IT Resource values initialised");
			// Making isValid to true since IT Resource and Task Attributes are
			// initialised properly
			isValid = true;
			logger.setMethodFinishLog(className, sMethodName);
		} catch (ConnectorException e) {
			isValid = false;
			logger.error(className, sMethodName, e.getMessage());
		} catch (Exception e) {
			isValid = false;
			logger.error(className, sMethodName, e.getMessage());
		}
	}

	/**
	 * Description: Called each time the scheduled task starts the SAP listener
	 * 
	 */
	public void execute() {
		String sMethodName = "execute()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			if (isValid) {
				createProperties();
				server = JCoIDoc.getServer(htITRattributes.get(SERVER_NAME)
						.toString()
						+ iAppend);
				server.setIDocHandlerFactory(new IDocHandlerFactory(this));

				server.setTIDHandler(new TidHandler());

				ThrowableListener listener = new ThrowableListener();
				server.addServerErrorListener(listener);
				server.addServerExceptionListener(listener);
				MyServerStateChangedListener listener1 = new MyServerStateChangedListener();
				server.addServerStateChangedListener(listener1);
				server.start();
				logger.info("Started MyIDocServer.");
				logger.info("MyIDocServer is listening now...");
				/*
				 * The server is synchronized so as to prevent the task
				 * scheduler to fall through and make it remain in running state
				 */
				//Modified the blow code(bug 15934207) to stop the listener(isStopped()) for 11gR1
				synchronized (server) {
					while (!isStopped()) {
						server.wait(5000);
					}
					if(isStopped()){
						serverStop();
					}
				}
			} else {
				logger
						.error(
								className,
								sMethodName,
								"Please set all the required fields values properly in "
										+ " task scheduler/ IT Resource run reconciliation again");
			}
			logger.setMethodFinishLog(className, sMethodName);
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
		}
	}
	// Start Bug 12320106 -INVALID USER FOUND: UNAUTHENTICATED DURING TIMEOUT 

	public OIMUtil get_oUtil() {

		String sMethodName = "getoUtil()";

		try {

			logger.setMethodStartLog("SAPIDOCListener", sMethodName);

			ConfigurationClient.ComplexSetting config = ConfigurationClient

				.getComplexSettingByPath("Discovery.CoreServer");

	

			final Hashtable env = config.getAllSettings(); 

			tcSignatureMessage moSignature = tcCryptoUtil.sign("xelsysadm","PrivateKey");  

			 m_utilFactory = new tcUtilityFactory(env, moSignature);

			

			tcUserOperationsIntf userAPI = ((Thor.API.Operations.tcUserOperationsIntf) m_utilFactory

					.getUtility(USER_API));

			

			tcObjectOperationsIntf objAPI = ((Thor.API.Operations.tcObjectOperationsIntf) m_utilFactory

					.getUtility(OBJECT_API));

			

			tcFormInstanceOperationsIntf formAPI = ((Thor.API.Operations.tcFormInstanceOperationsIntf) m_utilFactory

					.getUtility(FORM_API));

			

			tcFormDefinitionOperationsIntf formDefAPI = ((Thor.API.Operations.tcFormDefinitionOperationsIntf) m_utilFactory

					.getUtility(FORM_DEF_API));

			

			tcITResourceInstanceOperationsIntf resAPI = ((Thor.API.Operations.tcITResourceInstanceOperationsIntf) m_utilFactory

					.getUtility(IT_RESOURCE_API));

			

			tcSchedulerOperationsIntf schedulerAPI = ((Thor.API.Operations.tcSchedulerOperationsIntf) m_utilFactory

					.getUtility(SCHED_TASK_API));

			

			tcLookupOperationsIntf lookIntf = ((Thor.API.Operations.tcLookupOperationsIntf) m_utilFactory

					.getUtility(LOOKUP_API));

			

			tcReconciliationOperationsIntf reconOperAPI = ((Thor.API.Operations.tcReconciliationOperationsIntf) m_utilFactory

					.getUtility(RECON_API));

		

		// oUtil contains all the API references

		oUtil = new OIMUtil(userAPI, objAPI, formAPI, formDefAPI, resAPI,

		schedulerAPI, lookIntf, reconOperAPI, logger);

		

		return oUtil;

		

		} catch (Exception e){

			

			throw new ConnectorException(e.getMessage());

		}

	}

	// End Bug 12320106 -INVALID USER FOUND: UNAUTHENTICATED DURING TIMEOUT 

	

	/**
	 * Description: Stops the execution of the lstener by setting the Boolean
	 * flag to true
	 * 
	 * @return Returns true if the scheduled task is manually stopped during the
	 *         reconciliation run
	 */
	/*public boolean stop() {
		String sMethodName = "stop()";
		logger.info(className, sMethodName, "SAP Listener STOP");
		serverStop();
		isStopRecon = true;
		return isStopRecon;
	}*/

	/**
	 * Description : This method do all the steps to ensure a clean stop of the
	 * IDoc Listener
	 */
	private void serverStop() {
		String sMethodName = "serverStop()";
		logger.setMethodStartLog(className, sMethodName);
		/*
		 * unregister the providers
		 */
		if (SAPDestinationDataProvider.isRegister) {
			com.sap.conn.jco.ext.Environment
					.unregisterDestinationDataProvider(destProvider);
			SAPDestinationDataProvider.isRegister = false;
		}
		if (SAPServerDataProvider.isRegister) {
			com.sap.conn.jco.ext.Environment
					.unregisterServerDataProvider(serverProvider);
			SAPServerDataProvider.isRegister = false;
		}
		logger.debug(className, sMethodName,
				"Before stopping ....................");
		logServerMonitor(server);
		if (server.getState().equals(JCoServerState.ALIVE)
				|| server.getState().equals(JCoServerState.STARTED)) {
			server.stop();
		}
		server.release();
		logger.debug(className, sMethodName,
				"After stopping ....................");
		logServerMonitor(server);
		logger.setMethodFinishLog(className, sMethodName);
	}

	/**
	 * Description : This method will create properties files out of the values
	 * supplied in the IT Resources and set the values to server and destination
	 * providers which is used by the Listener to interact with the SAP system
	 */
	private void createProperties() throws ConnectorException {
		Properties clientProps = new Properties();
		Properties serverProps = new Properties();
		String sITRAttrName = "";
		String sMethodName = "createProperties()";

		try {
			logger.setMethodStartLog(className, sMethodName);

			int iServerPropLength = HRMSConstants.serverProperties.length;
			int iClientPropLength = HRMSConstants.clientProperties.length;

			for (int i = 0; i < iServerPropLength; i++) {
				sITRAttrName = (String) htITRMapping
						.get(HRMSConstants.serverProperties[i]);
				if (stringUtil.isEmpty(sITRAttrName)) {
					continue;
				}
				String sItAttr = htITRattributes.get(sITRAttrName).toString();
				if (stringUtil.isEmpty(sItAttr)) {
					continue;
				}
				serverProps.setProperty(HRMSConstants.serverProperties[i],
						sItAttr);
			}
			for (int j = 0; j < iClientPropLength; j++) {
				sITRAttrName = (String) htITRMapping
						.get(HRMSConstants.clientProperties[j]);
				if ((sITRAttrName == null) || sITRAttrName.equalsIgnoreCase("")) {
					continue;
				}
				String sItAttr = htITRattributes.get(sITRAttrName).toString();
				if (stringUtil.isEmpty(sItAttr)) {
					continue;
				}
				clientProps.setProperty(HRMSConstants.clientProperties[j],
						sItAttr);
			}
			destProvider.addDestination(htITRattributes.get(REPOSITORY_DEST)
					.toString(), clientProps);
			serverProvider.addServerProperties(htITRattributes.get(SERVER_NAME)
					.toString()
					+ iAppend, serverProps);
			logger.setMethodFinishLog(className, sMethodName);
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e.getMessage());
		}
	}

	/**
	 * Description: Receives the IDoc list
	 * Removed the static from class definition for BUG 14075167 - SAME PROFILE SET WHEN RECON FROM TWO SAP ER LISTENERS
	 */
	public class IDocHandler implements JCoIDocHandler {
		private String sClassName = this.getClass().getName();
		private SchedulerBaseTask schBaseTask = null;

		

		public IDocHandler(SchedulerBaseTask sbt){

			schBaseTask = sbt;

			

		}

		/**
		 * Description: Receives IDocs sent from SAP as an IDocList and forwards the list to a parser
		 * 
		 * @param serverCtx
		 *         JCoServerContext
		 * @param idocList
		 *         List of IDocs received from SAP
		 */
		public void handleRequest(JCoServerContext serverCtx,
				IDocDocumentList idocList) {
			IDocDocumentIterator iterator = idocList.iterator();
			IDocDocument doc = null;
			String sMethodName = "handleRequest";

			try {
				IDOCParserFactory parserFactory = new IDOCParserFactory();
				IDOCParser parser = parserFactory.getIDOCParser(htHRMSConfig,
						logger);

				while (iterator.hasNext()) {
					doc = iterator.next();
					// The recieved IDocs are sent to parse method
					//Start Bug 12320106 -INVALID USER FOUND: UNAUTHENTICATED DURING TIMEOUT 

					

					OIMUtil oUtil1 = ((SAPIDOCListener)schBaseTask).get_oUtil();

					logger.info(sClassName,sMethodName,"This is message is to display the value of oUtil::" + oUtil);

					logger.info(sClassName,sMethodName,"This is message is to display the value of Lookup API::" 

							+oUtil.getLookIntf());

					parser.parse(htHRMSConfig, hmConstants,
								htScheduleAttributes, oUtil1, doc);

					//End Bug 12320106 -INVALID USER FOUND: UNAUTHENTICATED DURING TIMEOUT 

				}
			} catch (Exception e) {
				logger.error(sClassName, sMethodName, e.getMessage());
				logger
						.setStackTrace(e, sClassName, sMethodName, e
								.getMessage());
			}
			finally {

				m_utilFactory.close();

			}

		}
	}

	/**
	 * Removed the static from class definition for BUG 14075167 - SAME PROFILE SET WHEN RECON FROM TWO SAP ER LISTENERS
	 */
	class IDocHandlerFactory implements JCoIDocHandlerFactory {
		private JCoIDocHandler jcohandler = null;

		

		public IDocHandlerFactory(SchedulerBaseTask sBTask) {

			// TODO Auto-generated constructor stub

			jcohandler = new IDocHandler(sBTask);

		}

		/**
		 * Description : The method should return the instance of IDocHandler
		 * 
		 * @param serverCtx
		 *            JCoServerContext
		 */
		public JCoIDocHandler getIDocHandler(JCoIDocServerContext serverCtx) {
			return jcohandler;
		}
	}

	/**
	 * Description : This class notifies when the listener changes its state
	 * 
	 */
	class MyServerStateChangedListener implements JCoServerStateChangedListener {
		private String sClassName = this.getClass().getName();

		public void serverStateChangeOccurred(JCoServer server,
				JCoServerState oldState, JCoServerState newState) {
			String sMethodName = "JCoServerStateChangedListener";
			logger.info(sClassName, sMethodName, server.getProgramID()
					+ ": Listener status changed from " + oldState + " to "
					+ newState);
		}

		public void serverExceptionOccurred(JCoServer server,
				String connectionId, JCoServerContextInfo ctx, Exception error) {
			String sMethodName = "serverExceptionOccurred";
			logger.error(sClassName, sMethodName, ">>> Error occured on "
					+ server.getProgramID() + " connection " + connectionId);
			logger.setStackTrace(error, sClassName, sMethodName, error
					.getMessage());
		}
	}

	/**
	 * Description : This class implements JCoServerErrorListener
	 * Removed the static from class definition for BUG 14075167 - SAME PROFILE SET WHEN RECON FROM TWO SAP ER LISTENERS
	 */
	class ThrowableListener implements JCoServerErrorListener,
			JCoServerExceptionListener {
		private String sClassName = this.getClass().getName();

		/*
		 * Throws errors at a listener level. Like if Metadata not recieved.
		 */
		public void serverErrorOccurred(JCoServer server, String connectionId,
				JCoServerContextInfo ctx, Error error) {
			String sMethodName = "ThrowableListener";
			logger.error(sClassName, sMethodName, ">>> Error occured on "
					+ server.getProgramID() + " connection " + connectionId);
			logger.setStackTrace(error, sClassName, sMethodName, error
					.getMessage());
		}

		public void serverExceptionOccurred(JCoServer server,
				String connectionId, JCoServerContextInfo ctx, Exception error) {
			String sMethodName = "serverExceptionOccurred";
			logger.error(sClassName, sMethodName, ">>> Error occured on "
					+ server.getProgramID() + " connection " + connectionId);
			logger.setStackTrace(error, sClassName, sMethodName, error
					.getMessage());
		}
	}

	/**
	 * Description: Gives the thread status of the listener
	 * 
	 * @param server
	 *            Listener whose status is expected
	 * 
	 */
	public void logServerMonitor(JCoIDocServer server) {
		JCoServerMonitor monitor = server.getMonitor();
		String sMethodName = "logServerMonitor()";
		logger.setMethodStartLog(className, sMethodName);
		logger.debug(className, sMethodName, "CurrentConnectionCount "
				+ monitor.getCurrentConnectionCount());
		logger.debug(className, sMethodName, "CurrentServerThreadCount "
				+ monitor.getCurrentServerThreadCount());
		logger.debug(className, sMethodName, "MaximumUsedServerThreadCount "
				+ monitor.getMaximumUsedServerThreadCount());
		logger.debug(className, sMethodName, "ServerThreadCount "
				+ monitor.getServerThreadCount());
		logger.debug(className, sMethodName, "StatelessConnectionCount "
				+ monitor.getStatelessConnectionCount());
		logger.debug(className, sMethodName, "UsedServerThreadCount "
				+ monitor.getUsedServerThreadCount());
		logger.setMethodFinishLog(className, sMethodName);
	}

	/**
	 * Description : This class handles IDoc transaction ID
	 * Removed the static from class definition for BUG 14075167 - SAME PROFILE SET WHEN RECON FROM TWO SAP ER LISTENERS
	 */
	class TidHandler implements JCoServerTIDHandler {
		private String sClassName = this.getClass().getName();

		public boolean checkTID(JCoServerContext serverCtx, String tid) {
			logger.debug(sClassName, "TidHandler", "checkTID called for TID="
					+ tid);
			return true;
		}

		public void confirmTID(JCoServerContext serverCtx, String tid) {
			logger.debug(sClassName, "confirmTID", "confirmTID called for TID="
					+ tid);
		}

		public void commit(JCoServerContext serverCtx, String tid) {
			logger.debug(sClassName, "commit", "commit called for TID=" + tid);
		}

		public void rollback(JCoServerContext serverCtx, String tid) {
			logger.debug(sClassName, "rollback", "rollback called for TID="
					+ tid);
		}
	}
}
