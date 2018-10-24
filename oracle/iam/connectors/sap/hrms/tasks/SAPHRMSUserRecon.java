/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/hrms/tasks/SAPHRMSUserRecon.java /main/22 2016/07/11 22:08:48 srkale Exp $ */
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
 ddkumar     01/06/09 - This class will be mapped to task scheduler that is
 configured for first time recon for processing the file contents
 ddkumar     01/06/09 - Creation
 */
/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/hrms/tasks/SAPHRMSUserRecon.java /main/22 2016/07/11 22:08:48 srkale Exp $
 *  @author  ddkumar
 *  @since   release specific (what release of product did this appear in)
 */
package oracle.iam.connectors.sap.hrms.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.common.dao.OIMUtil;
import oracle.iam.connectors.common.util.StringUtil;
import oracle.iam.connectors.common.vo.ITResource;
import oracle.iam.connectors.common.vo.ScheduledTask;
import oracle.iam.connectors.sap.common.parser.IDOCParser;
import oracle.iam.connectors.sap.common.parser.IDOCParserFactory;
import oracle.iam.connectors.sap.common.util.SAPUtil;
import oracle.iam.connectors.sap.hrms.util.HRMSConstants;
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
 * Description: Mapped to the scheduled task that is configured for first time recon for processing the file contents 
 * 
 *
 */
public class SAPHRMSUserRecon extends SchedulerBaseTask implements
		HRMSConstants {
	public static boolean isReconStopped;
	private Hashtable htITRattributes;
	private Hashtable htTaskAttributes;
	private HashMap hmConstants;
	private HashMap htHRMSConfig;
	private boolean isValid = false;
	private OIMUtil oUtil = null;
	private StringUtil stringUtil = new StringUtil();
	private ConnectorLogger logger = new ConnectorLogger(HRMS_LOGGER);
	private String className = this.getClass().getName();

	/**
	 * Description: Called each time the scheduled task runs and initializes the scheduled task attributes and lookup definitions. It also validates whether all the required parameters are correctly set in the scheduled task.
	 */
	public void init() {
		String sMethodName = "init()";
		logger.setMethodStartLog(className, sMethodName);
		logger.info(className, sMethodName,
				"Start of SAP HRMS Reconciliation process");
		try {
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
			tcLookupOperationsIntf lookIntf = (tcLookupOperationsIntf) super.getUtility(LOOKUP_API);
			tcReconciliationOperationsIntf reconOperAPI = (tcReconciliationOperationsIntf) super
					.getUtility(RECON_API);
			// oUtil contains all the API references
			 oUtil = new OIMUtil(userAPI, objAPI, formAPI, formDefAPI, resAPI,
					schedulerAPI, lookIntf, reconOperAPI, logger);	

			if (schedulerAPI == null) {
				throw new ConnectorException("Scheduler API is not getting initialised");
			}

			// Get the task scheduler name and validate it
			String sScheduleTaskName = super.getAttribute(SCHEDULE_TASK_NAME);
			if (stringUtil.isEmpty(sScheduleTaskName)) {
				logger.error(className, sMethodName,
						"Task Scheduler Name value is not set in task scheduler");
				throw new ConnectorException(
						"Task Scheduler Name value is not set in task scheduler");
			}

			// Get the task scheduler attributes in Hashtable
			ScheduledTask oTaskAttributes = new ScheduledTask(sScheduleTaskName,
					logger);
			htTaskAttributes = oTaskAttributes
					.getScheduledTaskDetails(schedulerAPI);

			// Validate if all required task attributes are set properly
			boolean isMandatoryTaskAttrSet = oTaskAttributes
					.validateMandatoryTaskAttrs(mandatoryUserReconSchedulerAttrs);
			if (!isMandatoryTaskAttrSet) {
				throw new ConnectorException("Mandatory Task Scheduler values not set");
			}

			boolean isFixedFileAttrs = oTaskAttributes.validateFixedValues(
					sFixedFileArchivalTaskAttrs, ",");

			if (!isFixedFileAttrs) {
				throw new ConnectorException(
						"File Archival task attribute value not set properly");
			}

			// Initialise HashMap for Configuration Lookup and Constant Lookup
			String sConfigLookup = (String) htTaskAttributes
					.get(CONFIGURATION_LOOKUP);
			htHRMSConfig = oUtil.getLookUpMap(sConfigLookup);
			hmConstants = oUtil.getLookUpMap((String) htHRMSConfig
					.get(CONSTANTS_LOOKUP));

			// Validate if all required conditional task attributes are set properly
			boolean isMandatoryConditionalTaskSet = oTaskAttributes
					.validateConditionalMandatory(mandatoryFileArchival, FILE_ARCHIVAL
							+ ";" + (String) hmConstants.get(YES), ";");
			if (!isMandatoryConditionalTaskSet) {
				throw new ConnectorException(
						"Task Scheduler Attribute File Archival Folder value not set");
			}

			// Check if the custom query/user type entered is valid or not
			String sCustomQuery = (String) htTaskAttributes.get(CUSTOM_QUERY);

			if (!stringUtil.isEmpty(sCustomQuery)) {
				boolean isMandatoryCustomAttrSet = oTaskAttributes
						.validateMandatoryTaskAttrs(mandatoryCustomReconAttr);

				if (!isMandatoryCustomAttrSet) {
					throw new ConnectorException(
							"Task Scheduler Attribute Custom Query Lookup values not set");
				}

				SAPUtil oSAPUtil = new SAPUtil(logger);
				String sAttributeMapingLookup = (String) htTaskAttributes
						.get(ATTRIBUTE_MAPPING_LOOKUP);
				HashMap hmAttributeMapforCustomRecon = oUtil
						.getLookUpMap(sAttributeMapingLookup);
				oSAPUtil.validateQuery(sCustomQuery, hmAttributeMapforCustomRecon);
			}

			String sEmpTypeQuery = (String) htTaskAttributes.get(EMPLOYEE_TYPE_QUERY);

			if (!stringUtil.isEmpty(sEmpTypeQuery)) {

				SAPUtil oSAPUtil = new SAPUtil(logger);
				HashMap<String, String> hmMapforUserType = new HashMap<String, String>();
				hmMapforUserType.put((String) hmConstants.get(EMPLOYEE_GROUP), "");
				hmMapforUserType.put((String) hmConstants.get(EMPLOYEE_SUB_GROUP), "");
				oSAPUtil.validateQuery(sEmpTypeQuery, hmMapforUserType);
			}

			logger.info("Task Scheduler Attributes initialised");

			// Get the IT Resource attributes in Hashtable
			ITResource oITResource = new ITResource((String) htTaskAttributes
					.get(IT_RESOURCE_NAME), resAPI, logger);
			htITRattributes = oITResource.getITResourceDetails();

			// Validate if all required IT Resource attributes are set properly
			boolean isMandatoryITRSet = oITResource
					.validateMandatoryITResource(mandatoryITRes);

			if (!isMandatoryITRSet) {
				throw new ConnectorException("Mandatory IT Resource values not set");
			}

			// Validate if all required SNC related IT Resource attributes are set
			// properly
			boolean isMandatorySNCITRSet = oITResource.validateConditionalMandatory(
					mandatoryITResSnc, "SNC mode" + ";" + (String) hmConstants.get(YES),
					";");

			if (!isMandatorySNCITRSet) {
				throw new ConnectorException(
						"SNC Related Mandatory IT Resource values not set");
			}
			// Validate if all Fixed IT Resource attributes are set properly
			boolean isFixedITRSet = oITResource.validateFixedValues(
					sFixedITResFileValues, ",");

			if (!isFixedITRSet) {
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
			throw new ConnectorException(className +" : "+ sMethodName+ " : "  + e.getMessage());
			
		} catch (Exception e) {
			isValid = false;
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException(className +" : "+ sMethodName+ " : "  + e.getMessage());
			
		}
	}

	/**
	 * Description: Called each time the scheduled task runs and executes the reconciliation process 
	 * 
	 */
	public void execute() {
		String sMethodName = "execute()";
		logger.setMethodStartLog(className, sMethodName);

		try {
			if (isValid) {
				String sDestinationFolder = null;
				// Get the Source and destination folder as entered in task scheduler
				String sSourceFolder = (String) htTaskAttributes.get(IDOC_FOLDER_PATH);
				String sFileArchival = (String) htTaskAttributes.get(FILE_ARCHIVAL);
				if (sFileArchival.equalsIgnoreCase((String) hmConstants.get(YES))) {
					sDestinationFolder = (String) htTaskAttributes
							.get(FILE_ARCHIVAL_FOLDER);
				}
				File file = new File(sSourceFolder);
				// Get the list of files present in the folder
				File[] fileList = file.listFiles();

				if (fileList != null) {
					int iNosOfFiles = fileList.length;
					logger.debug(className, sMethodName,
							"Number of files present in folder to be reconciled "
									+ iNosOfFiles);

					IDOCParserFactory parserFactory = new IDOCParserFactory();

					// Gets the name of the IDOC Parser to be called
					IDOCParser parser = parserFactory.getIDOCParser(htHRMSConfig, logger);

					// Get the BEGDA and SUBTY fields for all the segments specified in
					// attribute map lookup table in an ArrayList by connecting to SAP
					// schema
					ArrayList alList = parser.getSchema(htHRMSConfig, hmConstants,
							htITRattributes, htTaskAttributes, oUtil);

					for (int i = 0; i < iNosOfFiles; i++) {
						if (!isStopped()) {
							File filePath = new File(fileList[i].getAbsolutePath());
							// Parse the file by calling parse method
							if (filePath.isFile() && filePath.canRead()) {
								logger.debug(className, sMethodName, "Parsing file "
										+ fileList[i].getAbsolutePath());
								// BUG 23344445 - SAP-ER: Support for IDOC XML - Added htTaskAttributes in the method signature
								parser
								                .parse(htHRMSConfig, hmConstants, alList, oUtil, filePath, htTaskAttributes);
								
								// Back up the file in Archive folder if File Archival is set to
								// Yes
								if (!isStopped()) {
									if (sFileArchival.equalsIgnoreCase((String) hmConstants
											.get(YES))) {
										try {
											copyFilesToArchive(fileList[i].getName(), fileList[i]
													.getAbsolutePath(), sDestinationFolder);
										} catch (Exception e) {
										}
									}
									// Delete the file from the source folder
									filePath.delete();
								}
							} else {
								logger.info(className, sMethodName, fileList[i]
										+ " is not a file");
							}
						}
					}
				} else {
					logger.error(className, sMethodName, "File Path specified  "
							+ sSourceFolder + " does not exist.So please set the "
							+ "IDOC File path properly and run reconciliation again");
					throw new ConnectorException(className+","+sMethodName+"File Path specified "
							+ sSourceFolder + " does not exist.So please set the "
							+ "IDOC File path properly and run reconciliation again");	
					
				}
			} else {
				logger.error(className, sMethodName,
						"Please set all the required fields values properly in "
								+ " task scheduler/ IT Resource and run reconciliation again");
				throw new ConnectorException(className+","+sMethodName+"Please set all the required fields values properly in "
						+ " task scheduler/ IT Resource run reconciliation again");
				
			}

			logger.setMethodFinishLog(className, sMethodName);
		} catch (ConnectorException e) {
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException(className +" : "+ sMethodName+ " : "  + e.getMessage());
			
		} catch (Exception e) {
			logger.error(className, sMethodName, e.getMessage());
			throw new ConnectorException(className +" : "+ sMethodName+ " : "  + e.getMessage());
			
		} finally {
			logger.info(className, sMethodName,
					"End of SAP HRMS Reconciliation process");
		}
	}

	/**
	 * Description : This method copies the file from source folder to the 
	 * 							 destination folder specified
	 * 
	 * @param  sFilename  
	 *                 Source File Name
	 * @param  sFilePath
	 *                Source File Path
	 * @param sDestinationFolder     
	 *                Destination File Path  
	 * @throws Exception 
	 * 
	 */
	private void copyFilesToArchive(String sFilename, String sFilePath,
			String sDestinationFolder) throws Exception {
		String sMethodName = "copyFilesToArchive()";
		FileChannel srcChannel = null;
		FileChannel dstChannel = null;
		logger.setMethodStartLog(className, sMethodName);
		try {
			// Create channel on the source
			srcChannel = new FileInputStream(sFilePath).getChannel();
			// Date format for appending it to the file name
			SimpleDateFormat sdFormat = new SimpleDateFormat((String) hmConstants
					.get(ARCHIVAL_DATE_FORMAT));
			// Create channel on the destination
			dstChannel = new FileOutputStream(sDestinationFolder
					+ (String) hmConstants.get(FILE_SEPERATOR) + sFilename + "_"
					+ sdFormat.format(new Date())).getChannel();

			// Copy file contents from source to destination
			dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

		} catch (FileNotFoundException e) {
			logger.error(className, sMethodName,
					"Folder specified in 'File Archival Folder'  " + sDestinationFolder
							+ " of the task scheduler"
							+ " does not exist. Hence not archiving the files");
			throw new ConnectorException(className+","+sMethodName+"Folder specified in 'File Archival Folder'  " + sDestinationFolder
					+ " of the task scheduler"
					+ " does not exist. Hence not archiving the files");
			
		} catch (IOException e) {
			logger.error(className, sMethodName, e.getMessage());
			throw e;
		} finally {
			// Close the channels
			try {
				srcChannel.close();
				dstChannel.close();
			} catch (IOException e) {
				logger.error(className, sMethodName, e.getMessage());
			}
			logger.setMethodFinishLog(className, sMethodName);
		}
	}
}
