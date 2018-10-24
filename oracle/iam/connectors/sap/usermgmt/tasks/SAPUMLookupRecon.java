/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/usermgmt/tasks/SAPUMLookupRecon.java /main/8 2011/05/26 04:09:16 hhaque Exp $ */

/* Copyright (c) 2009, 2011, Oracle and/or its affiliates. 
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
 ddkumar     	07/10/09 - Creation
 K S Santosh    01/19/11 - Bug 11070597 - Added logger to print BAPI Name & its parameter
 */

/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/usermgmt/tasks/SAPUMLookupRecon.java /main/6 2010/07/08 02:55:19 ddkumar Exp $
 *  @author  ddkumar 
 *  @since   release specific (what release of product did this appear in)
 */

package oracle.iam.connectors.sap.usermgmt.tasks;

/*
	(MM/DD/YY)    BUG Description
	05/26/2011	  BUG 12586222 - SAP UM RECON LOOKUP WITH NON UNIQUE VALUES

*/
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.dao.OIMUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.common.vo.ITResource;
import oracle.iam.connectors.common.vo.ScheduledTask;
import oracle.iam.connectors.sap.common.connection.SAPConnection;
import oracle.iam.connectors.sap.common.connection.SAPResourceImpl;
import oracle.iam.connectors.sap.common.util.SAPUtil;
import oracle.iam.connectors.sap.usermgmt.util.UMConstants;
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

import com.oracle.oim.gcp.exceptions.ConnectionServiceException;
import com.oracle.oim.gcp.pool.ConnectionService;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.thortech.xl.scheduler.tasks.SchedulerBaseTask;

/**
 * Description:Mapped to the scheduled task that is configured for user management lookup synchronization 
 */
public class SAPUMLookupRecon extends SchedulerBaseTask implements UMConstants {
	//private boolean isStopRecon = false;
	private boolean isValid = false;
	private boolean isUM = false;
	private boolean isPoolingEnabled = false;
	private long lITResourceKey;
	private String className = this.getClass().getName();
	private String sSubsystem;
	private String sLookupName;
	private String sITResourceName;
	private Hashtable<String, String> htITRattributes;
	private HashMap<String, String> hmUMConfig;
	private HashMap<String, String> hmConstants;
	private ConnectionService ser;
	private SAPResourceImpl rc = null;
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
			logger.info(className, sMethodName,
					"Start of SAP User Management Lookup Reconciliation");

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
				throw new ConnectorException(
						"Task Scheduler Name value is not set in task scheduler");
			}

			// Get the task scheduler attributes in HashTable
			// Validate if all required task attributes are set properly
			ScheduledTask oTaskAttributes = new ScheduledTask(
					sScheduleTaskName, logger);
			Hashtable<String, String> htTaskAttributes = oTaskAttributes
					.getScheduledTaskDetails(schedulerAPI);
			boolean isMandatoryTaskAttrSet = oTaskAttributes
					.validateMandatoryTaskAttrs(mandatoryLookupSchedulerAttrs);
			if (!isMandatoryTaskAttrSet) {
				throw new ConnectorException(
						"Mandatory Task Scheduler values not set");
			}
			sLookupName = (String) htTaskAttributes.get(LOOKUP_NAME);

			sITResourceName = (String) htTaskAttributes.get(IT_RESOURCE_NAME);
			// Get the IT Resource attributes in HashTable and validate
			ITResource oITResource = new ITResource(sITResourceName, resAPI,
					logger);
			htITRattributes = oITResource.getITResourceDetails();
			// Initialize HashMap for Configuration Lookup and Constant Lookup
			hmUMConfig = oUtil.getLookUpMap((String) htITRattributes
					.get(CONFIG_LOOKUP));
			hmConstants = oUtil.getLookUpMap((String) hmUMConfig
					.get(CONSTANTS_LOOKUP));
		// Start :: BUG 12586222 - SAP UM RECON LOOKUP WITH NON UNIQUE VALUES
			//lITResourceKey = oUtil.getITResourceKey(sITResourceName);
			lITResourceKey = Long.parseLong(htITRattributes.get(hmConstants.get(IT_RESOURCE_KEY)));
		// End   :: BUG 12586222 - SAP UM RECON LOOKUP WITH NON UNIQUE VALUES
			
			// Check if CUA is enabled or not
			String sUM = (String) hmUMConfig.get(CUA_ENABLED);

			if (sUM.equalsIgnoreCase(NO)) {
				isUM = true;
				sSubsystem = (String) htITRattributes.get(SYSTEM_NAME);
			}
			logger.debug(className, sMethodName, "is CUA Enabled " + isUM);
			boolean isMandatoryITRSet = oITResource
					.validateMandatoryITResource(mandatoryUMITRes);
			if (!isMandatoryITRSet) {
				throw new ConnectorException(
						"Mandatory IT Resource values not set");
			}
			// Validate if all required IT Resource attributes are set properly
			boolean isMandatorySNCITRSet = oITResource
					.validateConditionalMandatory(mandatoryITResSnc,
							"SNC mode;yes", ";");
			if (!isMandatorySNCITRSet) {
				throw new ConnectorException(
						"SNC Related Mandatory IT Resource values not set");
			}

			// Validate if all Fixed IT Resource attributes are set properly
			boolean isFixedITRSet = oITResource.validateFixedValues(
					sFixedUMITResFileValues, ",");

			if (!isFixedITRSet) {
				throw new ConnectorException(
						"SNC Related Mandatory IT Resource values not set properly");
			}
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
		JCoDestination jcoConnection = null;
		SAPConnection sAPConnection = null;
		String sMethodName = "execute()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			if (isValid) {
				String sLookupFieldName;
				String sLookupFieldMapping;
				HashMap<String, String> hmLookupValues = null;
				String[] sSplitFieldMappings = null;
				/*
				 * Check if connection pooling is enabled.If enabled get the
				 * connection from the class which implements
				 * ResourceConnection.Else create a new Connection
				 */
				String isPool = (String) htITRattributes
						.get(USE_CONNECTION_POOLING);
				logger.info(className, sMethodName, "isPool : " + isPool);

				if ((isPool != null)
						&& (isPool.equalsIgnoreCase((hmConstants).get(YES)) || isPool
								.equalsIgnoreCase((hmConstants).get(TRUE)))) {
					logger
							.info(className, sMethodName,
									"Connection Pooling has been enabled. Will use Connection Pooling Service");
					isPoolingEnabled = true;
				} else {
					logger
							.info(
									className,
									sMethodName,
									"Connection Pooling has not been enabled. Will not use Connection Pooling Service");
				}

				if (isPoolingEnabled) {
					ser = new ConnectionService();

					try {
						rc = (SAPResourceImpl) ser
								.getConnection(lITResourceKey);
						jcoConnection = rc.mConnection;
					} catch (ConnectionServiceException dbe) {
						throw new ConnectorException(
								"Unable to get a connection from connection pool",
								dbe);
					}
				} else {
					HashMap<String, String> hmITRMapping = oUtil
							.getLookUpMap((String) hmUMConfig
									.get(IT_RESOURCE_MAPPING));
					sAPConnection = new SAPConnection(logger);
					jcoConnection = sAPConnection.addDestination(hmITRMapping,
							htITRattributes);
					hmITRMapping.clear();
				}
				htITRattributes.clear();

				/*
				 * Loop through the lookup to find the fields for which lookup
				 * recon needs to be done. If BAPI used is RFC_READ_TABLE,then
				 * call getLookupValuesRFC(),else call getLookupValuesBAPI() and
				 * add all these lookup values to lookup table.
				 */
				tcResultSet lookupRS = lookIntf.getLookupValues(sLookupName);
				int iLookupRowCount = lookupRS.getRowCount();

				for (int i = 0; i < iLookupRowCount; i++) {
					lookupRS.goToRow(i);
					sLookupFieldName = lookupRS.getStringValue(LOOKUP_CODE);
					sLookupFieldMapping = lookupRS
							.getStringValue(LOOKUP_DECODE);

					if (!isStopped()
							&& !stringUtil.isEmpty(sLookupFieldMapping)) {
						hmLookupValues = new HashMap<String, String>();
						logger.info("Look Up Field Name :" + sLookupFieldName);
						sSplitFieldMappings = sLookupFieldMapping.split(";");
						if (sSplitFieldMappings[0]
								.equalsIgnoreCase((hmConstants)
										.get(RFC_READ_TABLE))) {
							if (sSplitFieldMappings[1]
									.equalsIgnoreCase("USZBVLNDRC")) {
								hmLookupValues = getLookupValuesRFC(
										jcoConnection, sSplitFieldMappings);
							} else {
								/*
								 * Get the description of values for
								 * Roles/Profiles by calling
								 * getLookupValuesRFC() and the code key by
								 * calling getLookupValuesRolesorProfiles()
								 */
								HashMap hmValuesforDescription = getLookupValuesRFC(
										jcoConnection, sSplitFieldMappings);
								HashMap hmValuesForCode = getLookupValuesRolesorProfiles(
										jcoConnection, sSplitFieldMappings);
								String sKey = "";
								String sValue = "";
								if (hmValuesForCode.size() > 0) {
									Iterator it = hmValuesForCode.keySet()
											.iterator();
									while (it.hasNext()) {
										sKey = it.next().toString();
										if (hmValuesforDescription
												.containsKey(sKey)) {
											sValue = (String) hmValuesforDescription
													.get(sKey);
										} else {
											sValue = (String) hmValuesForCode
													.get(sKey);
										}
										hmLookupValues.put(sKey, sValue);
									}
								}
							}
						} else if (isUM
								&& sSplitFieldMappings[0]
										.equalsIgnoreCase((hmConstants)
												.get(SYSTEMNAME))) {
							hmLookupValues = new HashMap<String, String>();
							String sCode = lITResourceKey + "~" + sSubsystem;
							String sDecode = sITResourceName + "~" + sSubsystem;
							hmLookupValues.put(sCode, sDecode);
						} else {
							hmLookupValues = getLookupValuesBAPI(jcoConnection,
									sSplitFieldMappings);
						}
						/*
						 * If transform mapping is required,then call
						 * transformSingleOrMultivaluedData() to transform the data
						 */
						if (hmConstants.get(YES).equalsIgnoreCase(
								(String) hmUMConfig.get(USE_TRANSFORM_MAPPING_LOOKUP))) {
							hmLookupValues = oUtil.transformSingleData(
									hmLookupValues, (String) hmUMConfig
											.get(TRANSFORM_LOOKUP_RECON));
						}
						logger.info("Starting Lookup Reconciliation for "
								+ sLookupFieldName);
						logger.debug(className, sMethodName,
								"Lookup Values are :"
										+ hmLookupValues.toString());
						if (!isStopped() && (hmLookupValues.size() > 0)) {
							try {
								oUtil.addLookupvalues(sLookupFieldName,
										hmLookupValues);
							} catch (IllegalInputException e) {
								logger.error(className, sMethodName, e
										.getMessage());

							} catch (tcInvalidValueException e) {
								logger.error(className, sMethodName, e
										.getMessage());
							}
						}
						logger.info("End of Lookup Reconciliation for "
								+ sLookupFieldName);
					}
				}

			} else {
				throw new ConnectorException(
						"Required Values not set properly in IT Resource or Task Scheduler");
			}
		} catch (tcAPIException e) {
			logger.error(className, sMethodName, e.getMessage());
		} catch (tcColumnNotFoundException e) {
			logger.error(className, sMethodName, e.getMessage());
		} catch (tcInvalidLookupException e) {
			logger.error(className, sMethodName, e.getMessage());
		} finally {
			try {
				if (isPoolingEnabled) {
					ser.releaseConnection(rc);
				} else {
					if (jcoConnection != null) {
						sAPConnection.closeSAPConnection(jcoConnection);
					}
				}

			} catch (ConnectorException e) {
				logger.error(className, sMethodName, e.getMessage());
			} catch (Exception e) {
				logger.error(className, sMethodName, e.getMessage());
			}
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
	/*public boolean stop() {
		isStopRecon = true;
		logger.info("Stopping Lookup Reconciliation........");

		return isStopRecon;
	}*/

	/**
	 * Description : This method is used to get the lookup values executed using
	 * RFC_READ_TABLE
	 * 
	 * @param jcoConnection
	 *            Connection Object
	 * @param SplitFieldMappings
	 *            String Array which contains the parameters RFC_READ_TABLE
	 *            executes
	 * @return HashMap HashMap containing lookup values to be added to the
	 *         lookup table
	 * @throws ConnectorException
	 * 
	 */
	private HashMap<String, String> getLookupValuesRFC(
			JCoDestination jcoConnection, String[] sSplitFieldMappings)
			throws ConnectorException {
		HashMap<String, String> returnValuesMap = new HashMap<String, String>();
		String sMethodName = "getLookupValuesRFC()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			SAPUtil oSAPUtil = new SAPUtil(logger);
			/*
			 * Get the jcoFunction by using the BAPI Name passed and set the
			 * fields to be executed for that BAPI
			 */
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					sSplitFieldMappings[0]);
			JCoParameterList parameterList = jcoFunction
					.getImportParameterList();
			logger.debug(className, sMethodName, "Setting "+ (hmConstants).get(QUERY_TABLE) +" to "+sSplitFieldMappings[1]);
			parameterList.setValue((hmConstants).get(QUERY_TABLE),
					sSplitFieldMappings[1]);

			if (sSplitFieldMappings.length > 4) {
				JCoTable returnOption = jcoFunction.getTableParameterList()
						.getTable((hmConstants).get(OPTIONS));
				returnOption.appendRow();
				logger.debug(className, sMethodName, "Setting " + (hmConstants).get(TEXT) +" to "+sSplitFieldMappings[6]);
				returnOption.setValue((hmConstants).get(TEXT),
						sSplitFieldMappings[6]);
			}
			jcoFunction.execute(jcoConnection);
			returnValuesMap = getDropValuesRFC(sSplitFieldMappings, jcoFunction);
			logger.setMethodFinishLog(className, sMethodName);
		} catch (Exception ex) {
			logger.error(className, sMethodName, ex.getMessage());
			throw new ConnectorException(ex.getMessage());
		}

		return returnValuesMap;
	}

	/**
	 * Description : This method is used to get the lookup values executed using
	 * RFC_READ_TABLE
	 * 
	 * @param jcoConnection
	 *            Connection Object
	 * @param SplitFieldMappings
	 *            String Array which contains the parameters RFC_READ_TABLE
	 *            executes
	 * @return HashMap HashMap containing lookup values to be added to the
	 *         lookup table
	 * @throws ConnectorException
	 * 
	 */
	private HashMap<String, String> getLookupValuesRolesorProfiles(
			JCoDestination jcoConnection, String[] sSplitFieldMappings)
			throws ConnectorException {
		HashMap<String, String> returnValuesMap = new HashMap<String, String>();
		String sMethodName = "getLookupValuesRolesorProfiles()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			SAPUtil oSAPUtil = new SAPUtil(logger);
			/*
			 * Get the jcoFunction by using the BAPI Name passed and set the
			 * fields to be executed for that BAPI
			 */
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					sSplitFieldMappings[0]);
			JCoParameterList parameterList = jcoFunction
					.getImportParameterList();
			logger.debug(className, sMethodName, "Setting " +(hmConstants).get(QUERY_TABLE) +" to "+sSplitFieldMappings[5]);
			parameterList.setValue((hmConstants).get(QUERY_TABLE),
					sSplitFieldMappings[5]);
			jcoFunction.execute(jcoConnection);
			returnValuesMap = getDropValuesRFC(sSplitFieldMappings, jcoFunction);
			logger.setMethodFinishLog(className, sMethodName);
		} catch (Exception ex) {
			logger.error(className, sMethodName, ex.getMessage());
			throw new ConnectorException(ex.getMessage());
		}

		return returnValuesMap;
	}

	/**
	 * Description : This method is used to get the lookup values executed using
	 * RFC_READ_TABLE
	 * 
	 * @param sSplitFieldMappings
	 *            Connection Object
	 * @param dropDownListFunction
	 *            String Array which contains the parameters RFC_READ_TABLE
	 *            executes
	 * @return HashMap HashMap containing lookup values to be added to the
	 *         lookup table
	 * @throws ConnectorException
	 * 
	 */
	private HashMap<String, String> getDropValuesRFC(
			String[] sSplitFieldMappings, JCoFunction dropDownListFunction)
			throws ConnectorException {
		String sMethodName = "getDropValuesRFC()";
		logger.setMethodStartLog(className, sMethodName);
		int icodeOffSet = 0;
		int icodeLength = 0;
		int idescOffSet = 0;
		int idescLength = 0;
		int iSubSystemOffset = 0;
		int iSubSystemLength = 0;
		String sField = "";
		HashMap<String, String> returnValuesMap = new HashMap<String, String>();

		try {
			/*
			 * Loop through jcoTables and get the corresponding code and decode
			 * values to be put in HashMap
			 */
			JCoTable jcoTable = dropDownListFunction.getTableParameterList()
					.getTable((hmConstants).get(FIELDS));

			JCoTable jcoTable1 = dropDownListFunction.getTableParameterList()
					.getTable((hmConstants).get(DATA));

			int iReturnValueTable = jcoTable.getNumRows();
			int iReturnValuesDescTable = jcoTable1.getNumRows();
			if (iReturnValueTable != 0) {
				for (int i = 0; i < iReturnValueTable; i++) {
					jcoTable.setRow(i);
					sField = jcoTable.getString((hmConstants).get(FIELDNAME));

					if (sField.equals(sSplitFieldMappings[2].toString())) {
						icodeOffSet = Integer.parseInt(jcoTable
								.getString((hmConstants).get(OFFSET)));
						icodeLength = Integer.parseInt(jcoTable
								.getString((hmConstants).get(LENGTH)));
						icodeLength = icodeLength + icodeOffSet;
					}

					if (sField.equals(sSplitFieldMappings[3].toString())) {
						idescOffSet = Integer.parseInt(jcoTable
								.getString((hmConstants).get(OFFSET)));
						idescLength = Integer.parseInt(jcoTable
								.getString((hmConstants).get(LENGTH)));
						idescLength = idescLength + idescOffSet;
					}

					if (!sSplitFieldMappings[1].equalsIgnoreCase("USZBVLNDRC")) {
						if (sField.equals(sSplitFieldMappings[4].toString())) {
							iSubSystemOffset = Integer.parseInt(jcoTable
									.getString((hmConstants).get(OFFSET)));
							iSubSystemLength = Integer.parseInt(jcoTable
									.getString((hmConstants).get(LENGTH)));
							iSubSystemLength = iSubSystemLength
									+ iSubSystemOffset;
						}
					}
				}
			}

			logger.debug(className, sMethodName, "JCO Table Field Name  - "+sField);
			logger.debug(className, sMethodName, "icodeOffSet  - "+icodeOffSet);
			logger.debug(className, sMethodName, "icodeLength  - "+icodeLength);
			logger.debug(className, sMethodName, "idescOffSet  - "+idescOffSet);
			logger.debug(className, sMethodName, "idescLength  - "+idescLength);
			logger.debug(className, sMethodName, "iSubSystemOffset  - "+iSubSystemOffset);
			logger.debug(className, sMethodName, "iSubSystemLength  - "+iSubSystemLength);
			
			if (iReturnValuesDescTable != 0) {
				String sCode = null;
				String sDecode = null;
				String sMessage = null;
				for (int i = 0; i < iReturnValuesDescTable; i++) {
					sCode = null;
					sDecode = null;
					jcoTable1.setRow(i);
					sMessage = jcoTable1.getString("WA");
					if ((sMessage != null) && (sMessage.length() == 0)) {
						sCode = null;
						sDecode = null;
					} else if (sMessage.length() < icodeLength) {
						sCode = sMessage.substring(icodeOffSet);
						sDecode = null;
					} else if ((sMessage.length() > icodeLength)
							&& (sMessage.length() < idescLength)) {
						sCode = sMessage.substring(icodeOffSet, icodeLength);

						if ((idescOffSet > 0)
								&& (sMessage.length() > idescOffSet)) {
							sDecode = sMessage.substring(idescOffSet);
						}
					} else if (sMessage.length() > idescLength) {
						sCode = sMessage.substring(icodeOffSet, icodeLength);
						sDecode = sMessage.substring(idescOffSet, idescLength);
					} else if (sMessage.length() == icodeLength) {
						sCode = sMessage.substring(icodeOffSet, icodeLength);
						sDecode = null;
					} else if (sMessage.length() == idescLength) {
						sCode = sMessage.substring(icodeOffSet, icodeLength);
						sDecode = sMessage.substring(idescOffSet);
					}
					if (stringUtil.isEmpty(sDecode)) {
						sDecode = sCode;
					}
					/*
					 * For Roles and Profiles,we are modifying the code value to
					 * SUBSYSTEM ~ Role/Profile Name format
					 */

					if (!stringUtil.isEmpty(sCode)) {
						if (!sSplitFieldMappings[1]
								.equalsIgnoreCase("USZBVLNDRC")) {
							String sSubSystem = sMessage.substring(
									iSubSystemOffset, iSubSystemLength).trim();
							sCode = sSubSystem + "~" + sCode;
						}
						sCode = lITResourceKey + "~" + sCode;
						sDecode = sITResourceName + "~" + sDecode;
						returnValuesMap.put(sCode.trim(), sDecode.trim());
					}
				}
			}
			logger.debug(className, sMethodName, "HashMap Containing Lookup values to be added in table  - "+returnValuesMap);
			logger.setMethodFinishLog(className, sMethodName);
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException(e.getMessage());
		}
		return returnValuesMap;
	}

	/**
	 * Description : This method is used to get the lookup values executed using
	 * BAPI_HELPVALUES_GET
	 * 
	 * @param jcoConnection
	 *            Connection Object
	 * @param SplitFieldMappings
	 *            String Array which contains the parameters RFC_READ_TABLE
	 *            executes
	 * @return HashMap HashMap containing lookup values to be added to the
	 *         lookup table
	 * @throws ConnectorException
	 * 
	 */
	private HashMap<String, String> getLookupValuesBAPI(
			JCoDestination jcoConnection, String[] sSplitFieldMappings)
			throws ConnectorException {
		HashMap<String, String> returnValuesMap = new HashMap<String, String>();
		String sMethodName = "getLookupValuesBAPI()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			SAPUtil oSAPUtil = new SAPUtil(logger);
			/*
			 * Get the jcoFunction by using the BAPI Name passed and set the
			 * fields to be executed for that BAPI
			 */
			JCoFunction jcoFunction = oSAPUtil.getJCOFunction(jcoConnection,
					sSplitFieldMappings[0]);
			JCoParameterList parameterList = jcoFunction
					.getImportParameterList();
			logger.debug(className, sMethodName, "Setting " + (hmConstants).get(OBJTYPE) +" to USER");
			parameterList.setValue((hmConstants).get(OBJTYPE), "USER");
			logger.debug(className, sMethodName, "Setting " +(hmConstants).get(METHOD) +" to "+sSplitFieldMappings[1]);
			parameterList.setValue((hmConstants).get(METHOD),
					sSplitFieldMappings[1]);
			logger.debug(className, sMethodName, "Setting " +(hmConstants).get(PARAMETER) +" to "+sSplitFieldMappings[2]);
			parameterList.setValue((hmConstants).get(PARAMETER),
					sSplitFieldMappings[2]);
			logger.debug(className, sMethodName, "Setting " +(hmConstants).get(FIELD) +" to "+sSplitFieldMappings[3]);
			parameterList.setValue((hmConstants).get(FIELD),
					sSplitFieldMappings[3]);
			if (sSplitFieldMappings[3].equalsIgnoreCase((hmConstants)
					.get(TITLE_P))
					|| sSplitFieldMappings[3].equalsIgnoreCase((hmConstants)
							.get(COMPANY))
					|| sSplitFieldMappings[3].equalsIgnoreCase((hmConstants)
							.get(AGR_NAME))) {
				JCoStructure jcoStructure = jcoFunction
						.getImportParameterList().getStructure(
								(hmConstants).get(EXPLICIT_SHLP));
				logger.debug(className, sMethodName, "Setting " + (hmConstants).get(SHLPNAME) +" to "+sSplitFieldMappings[6]);
				jcoStructure.setValue((hmConstants).get(SHLPNAME),
						sSplitFieldMappings[6]);
				logger.debug(className, sMethodName, "Setting " +(hmConstants).get(SHLPTYPE) +" to "+sSplitFieldMappings[7]);
				jcoStructure.setValue((hmConstants).get(SHLPTYPE),
						sSplitFieldMappings[7]);
				if (sSplitFieldMappings[3].equalsIgnoreCase((hmConstants)
						.get(TITLE_P))) {
					JCoTable jcotable = jcoFunction
							.getTableParameterList()
							.getTable(
									(hmConstants).get(SELECTION_FOR_HELPVALUES));
					jcotable.appendRow();
					logger.debug(className, sMethodName, "Setting " + (hmConstants).get(SELECT_FLD) +" to "+sSplitFieldMappings[8]);
					jcotable.setValue((hmConstants).get(SELECT_FLD),
							sSplitFieldMappings[8]);
					logger.debug(className, sMethodName, "Setting " + (hmConstants).get(SIGN) +" to "+sSplitFieldMappings[9]);
					jcotable.setValue((hmConstants).get(SIGN),
							sSplitFieldMappings[9]);
					logger.debug(className, sMethodName, "Setting " + (hmConstants).get(OPTION) +" to "+sSplitFieldMappings[10]);
					jcotable.setValue((hmConstants).get(OPTION),
							sSplitFieldMappings[10]);
					logger.debug(className, sMethodName, "Setting " +(hmConstants).get(LOW) +" to "+sSplitFieldMappings[11]);
					jcotable.setValue((hmConstants).get(LOW),
							sSplitFieldMappings[11]);
				}
			} else if (sSplitFieldMappings[3].equalsIgnoreCase((hmConstants)
					.get(LIC_TYPE))) {
				JCoTable jcotable = jcoFunction.getTableParameterList()
						.getTable((hmConstants).get(SELECTION_FOR_HELPVALUES));
				jcotable.appendRow();
				logger.debug(className, sMethodName, "Setting JCO Table Parameter for LIC_TYPE");
				logger.debug(className, sMethodName, "Setting " +(hmConstants).get(SELECT_FLD) +" to "+sSplitFieldMappings[6]);
				jcotable.setValue((hmConstants).get(SELECT_FLD),
						sSplitFieldMappings[6]);
				logger.debug(className, sMethodName, "Setting " +(hmConstants).get(SIGN) +" to "+sSplitFieldMappings[7]);
				jcotable.setValue((hmConstants).get(SIGN),
						sSplitFieldMappings[7]);
				logger.debug(className, sMethodName, "Setting " +(hmConstants).get(OPTION) +" to "+sSplitFieldMappings[8]);
				jcotable.setValue((hmConstants).get(OPTION),
						sSplitFieldMappings[8]);
				logger.debug(className, sMethodName, "Setting " +(hmConstants).get(LOW) +" to "+sSplitFieldMappings[9]);
				jcotable.setValue((hmConstants).get(LOW),
						sSplitFieldMappings[9]);
			}
			jcoFunction.execute(jcoConnection);
			returnValuesMap = getDropValuesBAPI(sSplitFieldMappings,
					jcoFunction);
			logger.setMethodFinishLog(className, sMethodName);
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException(e.getMessage());
		}
		return returnValuesMap;
	}

	/**
	 * Description : This method is used to get the lookup values executed using
	 * RFC_READ_TABLE
	 * 
	 * @param sSplitFieldMappings
	 *            Connection Object
	 * @param dropDownListFunction
	 *            String Array which contains the parameters RFC_READ_TABLE
	 *            executes
	 * @return HashMap HashMap containing lookup values to be added to the
	 *         lookup table
	 * @throws ConnectorException
	 * 
	 */
	private HashMap<String, String> getDropValuesBAPI(
			String[] sSplitFieldMappings, JCoFunction dropDownListFunction)
			throws ConnectorException {
		HashMap<String, String> returnValuesMap = new HashMap<String, String>();
		String sMethodName = "getDropValues()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			/*
			 * Loop through jcoTables and get the corresponding code and decode
			 * values to be put in HashMap
			 */
			JCoTable jcoTable = dropDownListFunction.getTableParameterList()
					.getTable((hmConstants).get(DESCRIPTION_FOR_HELPVALUES));
			JCoTable jcoTable1 = dropDownListFunction.getTableParameterList()
					.getTable((hmConstants).get(HELPVALUES));
			int iCodeOffSet = 0;
			int iCodeLength = 0;
			int iDescOffSet = 0;
			int iDescLength = 0;
			String sField = "";
			int iDropDownTableRows = jcoTable.getNumRows();

			if (iDropDownTableRows != 0) {
				for (int iIndex = 0; iIndex < iDropDownTableRows; iIndex++) {
					jcoTable.setRow(iIndex);
					sField = jcoTable.getString((hmConstants).get(FIELDNAME));

					if (sField.equals(sSplitFieldMappings[4].toString())) {
						iCodeOffSet = Integer.parseInt(jcoTable
								.getString((hmConstants).get(OFFSET)));
						iCodeLength = Integer.parseInt(jcoTable
								.getString((hmConstants).get(LENG)));
						iCodeLength = iCodeLength + iCodeOffSet;
					}

					if (sField.equals(sSplitFieldMappings[5].toString())) {
						iDescOffSet = Integer.parseInt(jcoTable
								.getString((hmConstants).get(OFFSET)));
						iDescLength = Integer.parseInt(jcoTable
								.getString((hmConstants).get(LENG)));
						iDescLength = iDescLength + iDescOffSet;
					}
				}
			}
			logger.debug(className, sMethodName, "JCO Table Field Name  - "+sField);
			logger.debug(className, sMethodName, "iCodeOffSet  - "+iCodeOffSet);
			logger.debug(className, sMethodName, "iCodeOffSet  - "+iCodeLength);
			logger.debug(className, sMethodName, "iDescOffSet  - "+iDescOffSet);
			logger.debug(className, sMethodName, "iDescLength  - "+iDescLength);
			
			int idropDownHelpValuesTable = jcoTable1.getNumRows();

			if (idropDownHelpValuesTable != 0) {
				String sCode = null;
				String sDecode = null;
				String sMessage = null;
				int index1 = -1;
				int index2 = -1;
				for (int i = 0; i < idropDownHelpValuesTable; i++) {
					sCode = null;
					sDecode = null;
					jcoTable1.setRow(i);
					sMessage = jcoTable1.getString((hmConstants)
							.get(HELPVALUES));

					if (sSplitFieldMappings[3].toString().equalsIgnoreCase(
							"DCPFM")) {
						index1 = sMessage.indexOf('N');
						index2 = sMessage.indexOf('1');
					}
					if ((sMessage != null) && (sMessage.length() == 0)) {
						sCode = null;
						sDecode = null;
					} else if (sMessage.length() < iCodeLength) {
						sCode = sMessage.substring(iCodeOffSet);
						sDecode = null;
					} else if ((sMessage.length() > iCodeLength)
							&& (sMessage.length() < iDescLength)) {
						sCode = sMessage.substring(iCodeOffSet, iCodeLength);

						if (index1 != -1) {
							sDecode = sMessage.substring(index1);
						} else if (index2 != -1) {
							sDecode = sMessage.substring(index2);
						} else if ((iDescOffSet > 0)
								&& (sMessage.length() > iDescOffSet)) {
							sDecode = sMessage.substring(iDescOffSet);
						}
					} else if (sMessage.length() > iDescLength) {
						sCode = sMessage.substring(iCodeOffSet, iCodeLength);

						if (index1 != -1) {
							sDecode = sMessage.substring(index1);
						} else if (index2 != -1) {
							sDecode = sMessage.substring(index2);
						} else {
							sDecode = sMessage.substring(iDescOffSet,
									iDescLength);
						}
					} else if (sMessage.length() == iCodeLength) {
						sCode = sMessage.substring(iCodeOffSet, iCodeLength);
						sDecode = null;
					} else if (sMessage.length() == iDescLength) {
						sCode = sMessage.substring(iCodeOffSet, iCodeLength);

						if (index1 != -1) {
							sDecode = sMessage.substring(index1);
						} else if (index2 != -1) {
							sDecode = sMessage.substring(index2);
						} else {
							sDecode = sMessage.substring(iDescOffSet);
						}
					}
					if (!stringUtil.isEmpty(sCode)) {
						if (stringUtil.isEmpty(sDecode)) {
							sDecode = sCode;
						}
						if (isUM
								&& ((sSplitFieldMappings[3]
										.equalsIgnoreCase((hmConstants)
												.get(AGR_NAME)) || (sSplitFieldMappings[3]
										.equalsIgnoreCase((hmConstants)
												.get(BAPIPROF)))))) {
							sCode = lITResourceKey + "~" + sSubsystem + "~"
									+ sCode;
						} else {
							sCode = lITResourceKey + "~" + sCode;
						}
						sDecode = sITResourceName + "~" + sDecode;
						returnValuesMap.put(sCode, sDecode);
					}
				}
			}
			logger.debug(className, sMethodName, "HashMap Containing Lookup values to be added in table  - "+returnValuesMap);
			logger.setMethodFinishLog(className, sMethodName);
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException(e.getMessage());
		}
		return returnValuesMap;
	}
}
