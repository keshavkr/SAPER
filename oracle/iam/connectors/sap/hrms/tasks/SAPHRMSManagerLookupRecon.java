/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/hrms/tasks/SAPHRMSManagerLookupRecon.java /main/13 2016/07/11 22:08:47 srkale Exp $ */

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
 ddkumar     01/30/09 - This class will be mapped to task scheduler that is
 configured for populating manager values in lookup reconciliation
 ddkumar     01/30/09 - Creation
 */

/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/hrms/tasks/SAPHRMSManagerLookupRecon.java /main/13 2016/07/11 22:08:47 srkale Exp $
 *  @author  ddkumar 
 *  @since   release specific (what release of product did this appear in)
 */

package oracle.iam.connectors.sap.hrms.tasks;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.dao.OIMUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.common.vo.ITResource;
import oracle.iam.connectors.common.vo.ScheduledTask;
import oracle.iam.connectors.sap.common.connection.SAPConnection;
import oracle.iam.connectors.sap.common.util.SAPUtil;
import oracle.iam.connectors.sap.hrms.util.HRMSConstants;
import Thor.API.Exceptions.IllegalInputException;
import Thor.API.Exceptions.tcAPIException;
import Thor.API.Exceptions.tcDuplicateLookupCodeException;
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

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.thortech.xl.scheduler.tasks.SchedulerBaseTask;

/**
 * Description: Mapped to the scheduled task that is configured for populating manager values in lookup definitions
 *
 */
public class SAPHRMSManagerLookupRecon extends SchedulerBaseTask implements
		HRMSConstants {
	//private boolean isStopRecon = false;
	private boolean isValid = false;
	private OIMUtil oUtil = null;
	private String sTopOrgLookupName;
	private HashMap htHRMSConfig;
	private HashMap hmConstants;
	private HashMap htITRMapping;
	private Hashtable htITRattributes;
	private tcLookupOperationsIntf lookIntf;
	private StringUtil stringUtil = new StringUtil();
	private ConnectorLogger logger = new ConnectorLogger(HRMS_LOGGER);
	private String className = this.getClass().getName();

	/**
	 * Description: Called each time the scheduled task runs and initializes the scheduled task attributes and lookup definitions. It also validates whether all the required parameters are correctly set in the scheduled task.
	 */

	public void init() {
		String sMethodName = "init()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			logger.info(className, sMethodName,
					"Start of Manager Lookup Reconciliation process");
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
			lookIntf = (tcLookupOperationsIntf) super.getUtility(LOOKUP_API);
			tcReconciliationOperationsIntf reconOperAPI = (tcReconciliationOperationsIntf) super
					.getUtility(RECON_API);
			// oUtil contains all the API references
			 oUtil = new OIMUtil(userAPI, objAPI, formAPI, formDefAPI, resAPI,
					schedulerAPI, lookIntf, reconOperAPI, logger);			
			if (schedulerAPI == null) {
				throw new ConnectorException("Scheduler API is not getting initialised");
			}		
			if (lookIntf == null) {
				throw new ConnectorException(
						"Lookup Interface is not getting initialised");
			}

			// Get the task scheduler name and validate it
			String sScheduleTaskName = super.getAttribute(SCHEDULE_TASK_NAME);
			if (stringUtil.isEmpty(sScheduleTaskName)) {
				throw new ConnectorException(
						"Task Scheduler Name value is not set in task scheduler");
			}

			// Get the task scheduler attributes in Hashtable
			// Validate if all required task attributes are set properly
			ScheduledTask oTaskAttributes = new ScheduledTask(sScheduleTaskName,
					logger);
			Hashtable htTaskAttributes = oTaskAttributes
					.getScheduledTaskDetails(schedulerAPI);
			boolean isMandatoryTaskAttrSet = oTaskAttributes
					.validateMandatoryTaskAttrs(mandatoryManagerLookupSchedulerAttrs);
			if (!isMandatoryTaskAttrSet) {
				throw new ConnectorException("Mandatory Task Scheduler values not set");
			}

			// Initialise HashMap for Configuration Lookup and Constant Lookup
			sTopOrgLookupName = (String) htTaskAttributes
					.get(TOP_MOST_ORG_LOOKUP_NAME);
			htHRMSConfig = oUtil.getLookUpMap((String) htTaskAttributes
					.get(CONFIGURATION_LOOKUP));
			hmConstants = oUtil.getLookUpMap((String) htHRMSConfig
					.get(CONSTANTS_LOOKUP));

			// Get the IT Resource attributes in Hashtable
			ITResource oITResource = new ITResource((String) htTaskAttributes
					.get(IT_RESOURCE_NAME), resAPI, logger);
			htITRattributes = oITResource.getITResourceDetails();
			htITRMapping = oUtil.getLookUpMap((String) htHRMSConfig
					.get(IT_RESOURCE_MAPPING));
			boolean isMandatoryITRSet = oITResource
					.validateMandatoryITResource(mandatoryITRes);
			if (!isMandatoryITRSet) {
				throw new ConnectorException("Mandatory IT Resource values not set");
			}
			// Validate if all required IT Resource attributes are set properly
			boolean isMandatorySNCITRSet = oITResource.validateConditionalMandatory(
					mandatoryITResSnc, "SNC mode;yes", ";");
			if (!isMandatorySNCITRSet) {
				throw new ConnectorException(
						"SNC Related Mandatory IT Resource values not set");
			}
			logger.info("IT Resource values initialised");
			// Making isValid to true since IT Resource and Task Attributes are
			// initialised properly
			isValid = true;
			logger.setMethodFinishLog(className, sMethodName);
		} catch (ConnectorException e) {
			isValid = false;
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException(className +" : "+ sMethodName+ " :"  + e.getMessage());
			
		}catch (Exception e) {
			isValid = false;
			logger.error(className, sMethodName, e.getMessage());
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(className +" : "+ sMethodName+ " :"  + e.getMessage());
			
		}
	}

	/**
	 * Description : Called each time the scheduled task runs and executes the manager lookup reconciliation process
	 * 
	 */
	public void execute() {
		JCoDestination jcoConnection = null;
		SAPConnection sAPConnection = new SAPConnection(logger);
		String sMethodName = "execute()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			if (isValid) {
				jcoConnection = sAPConnection.addDestination(htITRMapping,
						htITRattributes);
				//Get list of all Top Most organizations in HashMap
				HashMap hmTopOrgs = oUtil.getLookUpMap(sTopOrgLookupName);
				//Populate hashMap with organization name as Code and parent organization
				//as decode for all top most organization specified
				HashMap hmOrgHierarchyLookup = populateOrgHierarchyLookup(
						jcoConnection, hmTopOrgs);
				if (hmOrgHierarchyLookup.size() > 0 && !isStopped()) {
					//Add hmOrgHierarchyLookup to the Lookup Table
					addLookupvalues(lookIntf, (String) htHRMSConfig.get(ORG_LOOKUP_NAME),
							hmOrgHierarchyLookup);
					//Populate Manager Name for all Organizations present in HashMap hmOrgHierarchyLookup
					HashMap hmManagerLookup = populateManagerLookup(jcoConnection,
							hmOrgHierarchyLookup);
					//Add hmManagerLookup to the Lookup Table
					if (hmManagerLookup.size() > 0 && !isStopped()) {
						addLookupvalues(lookIntf, (String) htHRMSConfig
								.get(MANAGER_LOOKUP_NAME), hmManagerLookup);
					}
				}
			} else {
				throw new ConnectorException(className+","+sMethodName+"Please set all the required fields values properly in "
						+ " task scheduler/ IT Resource run reconciliation again");
			}
		} catch (tcInvalidLookupException e) {
			logger.error(className, sMethodName, "Invalid Lookup");
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException("Invalid Lookup : "+className +" : "+ sMethodName+ " :"  + e.getMessage());
			
		} catch (IllegalInputException e) {
			logger.error(className, sMethodName, "IllegalInputException");
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException("IllegalInputException : "+className +" : "+ sMethodName+ " :"  + e.getMessage());
			
		} catch (tcInvalidValueException e) {
			logger.error(className, sMethodName, "InvalidValueException");
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException("InvalidValueException : "+className +" : "+ sMethodName+ " :"  + e.getMessage());
			
		} catch (ConnectorException e) {
			logger.error(className, sMethodName, "ConnectorException");
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
				throw new ConnectorException(className +" : "+ sMethodName+ " :"  + e.getMessage());
			
		} catch (Exception e) {
			logger.error(className, sMethodName, "Exception");
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			
			throw new ConnectorException(e.getMessage());
			
		} finally {
			try {
				if (jcoConnection != null) {
					sAPConnection.closeSAPConnection(jcoConnection);
				}
				logger.setMethodFinishLog(className, sMethodName);
				logger.info("End of SAP Manager Lookup Reconciliation process");
			} catch (ConnectorException e) {
				logger.error(className, sMethodName, "ConnectorException");
				logger.setStackTrace(e, className, sMethodName, e.getMessage());
			} catch (Exception e) {
				logger.error(className, sMethodName, "Exception");
				logger.setStackTrace(e, className, sMethodName, e.getMessage());
			}
		}
	}

	/**
	 * Description :This method returns HashMap with Organization Name as Code Key
	 *              and Parent Organization Name as Decode Key by executing BAPI 
	 *              for all Top Most Organizations specified in the Lookup table
	 *              
	 * @param jcoConnection:
	 * 							Connection Reference for executing BAPI
	 * @param sTopMostOrgName
	 *                Name of the Top Most Organization
	 * @return HashMap
	 * 							Returns HashMap with Organization Name as Code Key
	 *              and Parent Organization Name as Decode Key
	 * @throws Exception 
	 * 
	 */
	private HashMap getOrganizations(JCoDestination jcoConnection,
			String sTopMostOrgName) throws Exception {
		HashMap<String, String> hmOrganizations = new HashMap<String, String>();
		String sMethodName = "getOrganizations()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			HashMap<Integer, String> hashOrg = new HashMap<Integer, String>();
			String[] sFieldValues = ((String) hmConstants
					.get(LOOKUP_ORG_PARAMETER_VALUES)).split(";");
			//Get the JCO Function Name by connecting to repository
			JCoFunction jcoFunction = getdropDownListFunction(jcoConnection,
					sTopMostOrgName, sFieldValues);
			jcoFunction.execute(jcoConnection);
			//Get a list of all organizations from 'STRUCTURALDATA' table 
			JCoTable jcoTable = jcoFunction.getTableParameterList().getTable(
					(String) hmConstants.get(MANAGER_LOOKUP_RECON_TABLE_NAME));
			int iNoOfRows = jcoTable.getNumRows();
			for (int i = 0; i < iNoOfRows; i++, jcoTable.nextRow()) {
				hashOrg.put((Integer) jcoTable.getValue((String) hmConstants
						.get(MANAGER_LOOKUP_RECON_SEQNR)), (String) jcoTable
						.getValue((String) hmConstants.get(MANAGER_LOOKUP_RECON_OBJID)));
			}
			jcoTable.firstRow();
			logger.debug(className, sMethodName, iNoOfRows
					+ " Organizations found in SAP with Top Organization Name  "
					+ sTopMostOrgName);
			// Iterate through 'STRUCTURALDATA' table, and put org name 
			//and parent org name got from hashOrg in HashMap.
			String sOrgName;
			String sParentName;
			for (int i = 0; i < iNoOfRows; i++, jcoTable.nextRow()) {
				sOrgName = (String) jcoTable.getValue((String) hmConstants
						.get(MANAGER_LOOKUP_RECON_OBJID));
				sParentName = (String) hashOrg.get((Integer) jcoTable
						.getValue((String) hmConstants.get(MANAGER_LOOKUP_RECON_PUP)));
				//START: BUG 16989933
				if (stringUtil.isEmpty(sParentName)) {
					logger.debug(className, sMethodName, "sParentName : "+ sParentName);
					sParentName = NONE;
					logger.debug(className, sMethodName, "sParentName(TopMostOrg) setting to NONE : "+ sParentName);
				}
				//END: BUG 16989933
				//Put the parent Organization Name as value in HashMap for code key Organization
				hmOrganizations.put(sOrgName, sParentName);
			}
		} catch (Exception e) {
			logger.error(className, sMethodName, "Exception");
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw e;

		}
		logger.setMethodFinishLog(className, sMethodName);
		return hmOrganizations;
	}

	/**
	 * Description :This method returns HashMap with Organization Name as Code Key
	 *              and Manager Name as Decode Key by executing BAPI 
	 *              for Organizations specified in the Lookup table
	 *              
	 * @param jcoConnection:
	 * 							Connection Reference for executing BAPI
	 * @param sParentOrg
	 *                Name of the Organization for which we need the manager
	 * @return HashMap
	 * 							Returns HashMap with Organization Name as Code Key
	 *              and Manager Name as Decode Key
	 * @throws Exception 
	 * 
	 */
	private HashMap getManagers(JCoDestination jcoConnection, String sParentOrg) throws Exception {
		HashMap<String, String> hmManager = new HashMap<String, String>();
		String sMethodName = "getManagers()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			String[] sFieldValues = ((String) hmConstants
					.get(LOOKUP_MANAGER_PARAMETER_VALUES)).split(";");
			//Get the JCO Function Name by connecting to repository
			JCoFunction jcoFunction = getdropDownListFunction(jcoConnection,
					sParentOrg, sFieldValues);
			jcoFunction.execute(jcoConnection);
			//et a list of all organizations and its Manager fromJCO Table 
			JCoTable jcoTable = jcoFunction.getTableParameterList().getTable(
					(String) hmConstants.get(MANAGER_LOOKUP_RECON_TABLE_NAME));
			int iNoOfRows = jcoTable.getNumRows();
			// Iterate through STRUCTURALDATA, get manager from OTYPE='P' record
			// in  'OBJID' column.
			String sManager;
			String sField;
			for (int i = 0; i < iNoOfRows; i++, jcoTable.nextRow()) {
				sField = jcoTable.getString((String) hmConstants
						.get(MANAGER_LOOKUP_RECON_OTYPE));
				if (sField.equals((String) hmConstants
						.get(MANAGER_LOOKUP_RECON_OTYPE_VALUE))) {
					sManager = (String) jcoTable.getValue((String) hmConstants
							.get(MANAGER_LOOKUP_RECON_OBJID));
					hmManager.put(sParentOrg, sManager);
					break;
				}
			}
			if (hmManager.size() < 1) {
				logger.debug(className, sMethodName, "No Manager present for Org "
						+ sParentOrg + " Hence putting NONE");
				hmManager.put(sParentOrg, (String) hmConstants.get(NONE));
			}
		} catch (Exception e) {
			logger.error(className, sMethodName, "Exception");
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw e;

		}
		logger.setMethodFinishLog(className, sMethodName);
		return hmManager;
	}

	/**
	 * Description :This method returns HashMap with Organization Name as Code Key
	 *              and Parent Organization Name as Decode Key by executing BAPI 
	 *              for all Top Most Organizations specified in the Lookup table
	 *              
	 * @param jcoConnection:
	 * 							Connection Reference for executing BAPI
	 * @param hmTopOrgs
	 *                HashMap containing list of all Top Most Organization
	 * @return HashMap
	 * 							Returns HashMap with Organization Name as Code Key
	 *              and Parent Organization Name as Decode Key
	 * @throws Exception 
	 * 
	 */
	private HashMap populateOrgHierarchyLookup(JCoDestination jcoConnection,
			HashMap hmTopOrgs) throws Exception {
		HashMap<String, String> hmOrgHierarchyLookup = new HashMap<String, String>();
		String sMethodName = "populateOrgHierarchyLookup()";
		logger.setMethodStartLog(className, sMethodName);
		//Iterate through HashMap to get each Top most organization name
		Set keySet = hmTopOrgs.keySet();
		Iterator keySetIterator = keySet.iterator();
		logger.debug(className, sMethodName,"BAPI name: " + hmConstants.get(LOOKUP_MANAGER_BAPI_NAME));
		//For logging only
		String sFieldNames[] = ((String) hmConstants
				.get(LOOKUP_MANAGER_PARAMETER_NAMES)).split(";");
		String[] sFieldValues = ((String) hmConstants
				.get(LOOKUP_ORG_PARAMETER_VALUES)).split(";");
		logger.debug(className, sMethodName,"Parameter: " + sFieldNames[0] + " set to " + sFieldValues[0]); 
		logger.debug(className, sMethodName,"Parameter: " + sFieldNames[2] + " set to " + new Date()); 
		logger.debug(className, sMethodName,"Parameter: " + sFieldNames[3] + " set to " + sFieldValues[1]); 
		logger.debug(className, sMethodName,"Parameter: " + sFieldNames[4] + " set to " + sFieldValues[2]); 
		logger.debug(className, sMethodName,"Parameter: " + sFieldNames[5] + " set to " + sFieldValues[3]); 

		while (keySetIterator.hasNext()) {
			String sOrgName = (String) keySetIterator.next();
			logger.debug(className, sMethodName,"Parameter: " + sFieldNames[1] + "set to " + sOrgName); 
			HashMap hmOrgs = null;
			try {
				hmOrgs = getOrganizations(jcoConnection, sOrgName);
			} catch (Exception e) {
				
				throw e;
				
				
			}
			hmOrgHierarchyLookup.putAll(hmOrgs);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return hmOrgHierarchyLookup;
	}

	/**
	 * Description :This method returns HashMap with Organization Name as Code Key
	 *              and Manager Name as Decode Key by executing BAPI 
	 *              for Organizations specified in the Lookup table
	 *              
	 * @param jcoConnection:
	 * 							Connection Reference for executing BAPI
	 * @param hmOrgs
	 *              Hashmap containing list of all orgs for which Manager name is 
	 *              required
	 * @return HashMap
	 * 							Returns HashMap with Organization Name as Code Key
	 *              and Manager Name as Decode Key
	 * @throws Exception 
	 * 
	 */
	private HashMap populateManagerLookup(JCoDestination jcoConnection,
			HashMap hmOrgs) throws Exception {
		HashMap<String, String> hmManagerLookup = new HashMap<String, String>();
		String sMethodName = "populateManagerLookup()";
		logger.setMethodStartLog(className, sMethodName);
		//For logging only
		String sFieldNames[] = ((String) hmConstants
				.get(LOOKUP_MANAGER_PARAMETER_NAMES)).split(";");
		String[] sFieldValues = ((String) hmConstants
				.get(LOOKUP_MANAGER_PARAMETER_VALUES)).split(";");
		logger.debug(className, sMethodName,"Parameter: " + sFieldNames[0] + "set to " + sFieldValues[0]); 
		logger.debug(className, sMethodName,"Parameter: " + sFieldNames[2] + "set to " + new Date()); 
		logger.debug(className, sMethodName,"Parameter: " + sFieldNames[3] + "set to " + sFieldValues[1]); 
		logger.debug(className, sMethodName,"Parameter: " + sFieldNames[4] + "set to " + sFieldValues[2]); 
		logger.debug(className, sMethodName,"Parameter: " + sFieldNames[5] + "set to " + sFieldValues[3]); 
		Set keySet = hmOrgs.keySet();
		Iterator keySetIterator = keySet.iterator();
		while (keySetIterator.hasNext()) {
			String sOrgName = (String) keySetIterator.next();
			logger.debug(className, sMethodName,"Parameter: " + sFieldNames[1] + "set to " + sOrgName); 
			HashMap hmManagers = getManagers(jcoConnection, sOrgName);
			hmManagerLookup.putAll(hmManagers);
		}
		logger.setMethodFinishLog(className, sMethodName);
		return hmManagerLookup;
	}

	/**
	 * Description : This method gets the JCoFunction after executing BAPI
	 * BAPI_ORGUNITEXT_DATA_GET
	 * 
	 * @param jcoConnection
	 *          JCoDestination required for SAP connection
	 * @param  sParentOrg
	 * 				 Name of the Top Most Organization
	 * @param  sFieldValues
	 *         String Array containing the input for executing the BAPI
	 * @throws ConnectorException
	 */
	private JCoFunction getdropDownListFunction(JCoDestination jcoConnection,
			String sParentOrg, String sFieldValues[]) throws ConnectorException {
		JCoFunction dropDownListFunction = null;
		String sMethodName = "getdropDownListFunction()";
		logger.setMethodStartLog(className, sMethodName);
		try {
			SAPUtil oSAPUtil = new SAPUtil(logger);
			/*
			 * Sets inner table values of the BAPI
			 */
			String sFieldNames[] = ((String) hmConstants
					.get(LOOKUP_MANAGER_PARAMETER_NAMES)).split(";");
			dropDownListFunction = oSAPUtil.getJCOFunction(jcoConnection,
					(String) hmConstants.get(LOOKUP_MANAGER_BAPI_NAME));
			JCoParameterList parameterList = dropDownListFunction
					.getImportParameterList();
			//Set the parameter list values
			parameterList.setValue(sFieldNames[0], sFieldValues[0]);
			parameterList.setValue(sFieldNames[1], sParentOrg);
			parameterList.setValue(sFieldNames[2], new Date());
			parameterList.setValue(sFieldNames[3], sFieldValues[1]);
			parameterList.setValue(sFieldNames[4], sFieldValues[2]);
			parameterList.setValue(sFieldNames[5], sFieldValues[3]);
		} catch (RuntimeException e) {
			throw new ConnectorException(e.getMessage());
		} catch (Exception e) {
			throw new ConnectorException(e.getMessage());
		}
		logger.setMethodFinishLog(className, sMethodName);
		return dropDownListFunction;
	}

	/**
	 * Description : This method adds the lookup values in the given lookup field
	 * 
	 * @param tclookupIntf
	 *          Lookup Operations API
	 * @param sLookupCode
	 *          Name of the lookup
	 * @param hmLookupUpValues
	 *          Hashmap having key and value pair that needs to be added to the
	 *          lookup up table mentioned
	 * 
	 * @throws ConnectorException
	 * 
	 */
	private void addLookupvalues(
			Thor.API.Operations.tcLookupOperationsIntf tclookupIntf,
			String sLookupCode, HashMap hmLookupUpValues)
			throws tcInvalidLookupException, IllegalInputException,
			tcInvalidValueException, ConnectorException {
		String sMethodName = "addLookupvalues()";
		try {
			logger.setMethodStartLog(className, sMethodName);
			String sValue = "";
			String sDecode = "";
			addLookupCode(sLookupCode, tclookupIntf);
			if (hmLookupUpValues.size() > 0) {
				Iterator it = hmLookupUpValues.keySet().iterator();
				while (it.hasNext()) {
					sValue = it.next().toString().trim();
					sDecode = (String) hmLookupUpValues.get(sValue);
					try {
						// Adds values to the lookup code
						if (!isStopped())
							tclookupIntf.addLookupValue(sLookupCode, sValue, sDecode, "", "");
					} catch (tcInvalidValueException dupValEx) {
						logger.error(className, sMethodName, "InvalidValueException");
						logger.setStackTrace(dupValEx, className, sMethodName, dupValEx
								.getMessage());
						throw new ConnectorException(className +" : "+ sMethodName+ " :"  + dupValEx.getMessage());
						
					} catch (tcAPIException apiEx) {
						logger.error(className, sMethodName, "APIException");
						logger.setStackTrace(apiEx, className, sMethodName, apiEx
								.getMessage());
						throw new ConnectorException(className +" : "+ sMethodName+ " :"  + apiEx.getMessage());
						
					} catch (IllegalInputException apiEx) {
						logger.error(className, sMethodName, "IllegalInputException");
						logger.setStackTrace(apiEx, className, sMethodName, apiEx
								.getMessage());
						throw new ConnectorException(className +" : "+ sMethodName+ " :"  + apiEx.getMessage());
						
					} catch (tcInvalidLookupException e) {
						logger.error(className, sMethodName, "InvalidLookupException");
						logger.setStackTrace(e, className, sMethodName, e.getMessage());
						throw new ConnectorException(className +" : "+ sMethodName+ " :"  + e.getMessage());
						
					} catch (Exception e) {
						logger.error(className, sMethodName, "Exception");
						logger.setStackTrace(e, className, sMethodName, e.getMessage());
						throw new ConnectorException(className +" : "+ sMethodName+ " :"  + e.getMessage());
						
					}
				}
			}
			logger.info(className, sMethodName, "Lookup Populated");
		} catch (Exception e) {
			logger.error(className, sMethodName, "Exception");
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e.getMessage());
			
		}
		logger.setMethodFinishLog(className, sMethodName);
	}

	/**
	 * Description : This method clears any lookup that previously existed and
	 * adds the lookup code
	 * 
	 * @param sLookupCode
	 *          Name of the lookup
	 * @param tclookupIntf
	 *          Lookup Operations API
	 * 
	 * @throws ConnectorException
	 * 
	 */
	private void addLookupCode(String sLookupCode,
			Thor.API.Operations.tcLookupOperationsIntf tclookupIntf)
			throws ConnectorException {
		String sMethodName = "addLookupCode()";
		try {
			// If lookup is already there we just clear it and add again
			logger.setMethodStartLog(className, sMethodName);
			tclookupIntf.removeLookupCode(sLookupCode);
			tclookupIntf.addLookupCode(sLookupCode);
			logger.debug(className, sMethodName, "addLookupCode completed");
		} catch (tcAPIException e) {
			logger.error(className, sMethodName, "APIException");
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(className +" : "+ sMethodName+ " :"  + e.getMessage());
			
		} catch (tcInvalidLookupException e) {
			try {
				// If lookup is not there we create a lookup.
				tclookupIntf.addLookupCode(sLookupCode);
				logger.debug(className, sMethodName, "addLookupCode completed");
			} catch (tcAPIException e1) {
				logger.error(className, sMethodName, "APIException");
				logger.setStackTrace(e1, className, sMethodName, e1.getMessage());
				throw new ConnectorException(className +" : "+ sMethodName+ " :"  + e.getMessage());
				
			} catch (tcDuplicateLookupCodeException e1) {
				logger.error(className, sMethodName, "DuplicateLookupCodeException");
				logger.setStackTrace(e1, className, sMethodName, e1.getMessage());
				throw new ConnectorException(className +" : "+ sMethodName+ " :"  + e1.getMessage());
			}
		} catch (tcDuplicateLookupCodeException e) {
			logger.error(className, sMethodName, "DuplicateLookupCodeException");
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(className +" : "+ sMethodName+ " :"  + e.getMessage());

		} catch (Exception e) {
			logger.error(className, sMethodName, "Exception");
			logger.setStackTrace(e, className, sMethodName, e.getMessage());
			throw new ConnectorException(e.getMessage());
		}
		logger.setMethodFinishLog(className, sMethodName);
	}
}
