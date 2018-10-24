/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/hrms/util/HRMSConstants.java /main/14 2016/06/27 23:39:42 vsantosh Exp $ */
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
 ddkumar    01/30/09 - Creation
 radhika		03/29/10 - BUG 9523713 - DESIRE ABILITY TO CONFIGURE WHETHER FUTURE DATED PROCESSING
 jagadeesh	11/12/10 - Added constants for BUG 10229768
 Akshata    05/09/11 - Added constants for the BUG 12409614- SUPPORT SAP ER911 MANAGER BEHAVIOUR IN SAP ER912
 Chanthosh	07/23/13 - Bug 17211711 - SCHEDULED TASK: SAP HRMS UPDATE MANAGER NOT WORKING PROPERLY
 */
/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/hrms/util/HRMSConstants.java /main/14 2016/06/27 23:39:42 vsantosh Exp $
 *  @author  ddkumar
 *  @since   release specific (what release of product did this appear in)
 */

package oracle.iam.connectors.sap.hrms.util;

import oracle.iam.connectors.sap.common.connection.SAPConstants;

/**
 * Description:This interface has all the SAP HRMS Constants used in the
 							 connector
 */
public interface HRMSConstants extends SAPConstants{      
    
    //Constants used for validating IT Resources and task scheduler attributes 
       
    
     String[][] sFixedFileArchivalTaskAttrs = {     
      { "File Archival", "yes,no" }      
     };   
    
    
     String[] mandatoryUserReconSchedulerAttrs = {
            "IDOC Folder Path", "Configuration Lookup", "IT Resource",
             "Resource Object", "Attribute Mapping Lookup",
            "File Archival"
        };
     String[] mandatoryListenerSchedulerAttrs = {
            "Configuration Lookup", "IT Resource", 
            "Resource Object", "Attribute Mapping Lookup",
        };
     

 	String[] mandatoryITResListener = { "Gateway host", "Program ID",
 			"Gateway service", "Server name", "Repository destination" };
    
     String[] mandatoryFileArchival = {
      "File Archival Folder"
    };
     String[] mandatoryLookupSchedulerAttrs = {
            "Lookup Name", "Configuration Lookup", "IT Resource"
        };
     String[] mandatoryManagerLookupSchedulerAttrs = {
      "Top Most Organization Lookup", "Configuration Lookup", "IT Resource"
    };
     String[] mandatoryCustomReconAttr = {
            "Custom Query Lookup"
        };
     String[] mandatoryManagerAttrs = {
      "Configuration Lookup", "Resource Object", "Update users with empty manager id only"
    };

    //Constants defined in Lookup.SAP.HRMS.Configuration
     String HIRE_EVENTS_LOOKUP = "Hire Events Lookup";
     String REHIRE_EVENTS_LOOKUP = "Rehire Events Lookup";
     String CLASS_NAME = "Class Name";
     String TERMINATE_EVENTS_LOOKUP = "Terminate Events Lookup";
     String EMPLOYEE_TYPE_LOOKUP = "Employee Type Lookup";     
     String IT_RESOURCE_MAPPING = "IT Resource Mapping";
     String SEGMENT_LENGTH = "Segment Name Length";
     String ROOT_SEGMENT = "Root Segment";
     String GROUP_SEGMENT = "Group Segment";
     String ACTIONS_EVENT = "Actions Event";
     String DELETE_INDICATOR = "Delete Indicator";    
     String EVENT_BEGIN_DATE = "Event Begin Date";
     // Start ::BUG 23344445 - SAP-ER: Support for IDOC XML
     String IS_IDOC_FORMAT_XML="Is IDOC File Format in XML";
     // End :: BUG 23344445 - SAP-ER: Support for IDOC XML  
     
     //Start: BUG 10229768 - STARTING AND TERMINATION DATE ARE NOT COMING
     String EVENT_END_DATE = "Event End Date";
     /*String HIRE_EVENT_START_DATE_FIELD = "Hire event start date field";
     String HIRE_EVENT_END_DATE_FIELD = "Hire event end date field";*/
     String OIM_START_DATE = "OIM start date field";
     String OIM_END_DATE   = "OIM end date field";
/*     String TERMINATE_EVENT_START_DATE_FIELD = "Terminate event start date field";
     String TERMINATE_EVENT_END_DATE_FIELD = "Terminate event end date field";*/
     String CREATE_DEFERRED_EVENT_FOR_TERMINATE_EVENT = "Create deferred event for terminate event";
     String CREATE_DEFERRED_EVENT_FOR_FUTURE_DATED_HIRE = "Create deferred event for future dated hire";
		//End: BUG 10229768 - STARTING AND TERMINATION DATE ARE NOT COMING
    //Start: BUG 10434626-TRANSFORAMTION AND VALIDATION NOT SUPPORTED
 	String TRANSFORM_LOOKUP_PROV = "Transform Lookup For Prov";
	String VALIDATE_LOOKUP_PROV = "Validation Lookup For Prov";
	String USE_RECON_VALIDATION = "Use Validation For Recon";
	String USE_TRANSFORM_MAPPING = "Use Transformation For Recon";
	String TRANSFORM_LOOKUP = "Transform Lookup For Recon";
	String VALIDATE_LOOKUP = "Validation Lookup For Recon";
	//End: BUG 10434626-TRANSFORAMTION AND VALIDATION NOT SUPPORTED
     String EVENT = "Event";
     String OBJECT_TYPE = "Object Type";
     String BATCH_SIZE = "Batch Size";
     String REMOVE_ZERO = "Remove Leading Zero From Personnel Number";
     String RECONCILE_FIRST_TIME_DISABLED_USERS = "Reconcile First Time Disabled Users";
     String IDOC_TYPE_EXTENSION = "IDOC Type Extension";
     //Start: BUG 9523713-DESIRE ABILITY TO CONFIGURE FUTURE DATED PROCESSING
     String FUTURE_DATED_EVENT_HANDLING = "Is Future Dated Event Handling Enabled";
     //End: BUG 9523713-DESIRE ABILITY TO CONFIGURE FUTURE DATED PROCESSING
     
     // String ORG_LOOKUP_NAME = "Organization Hierarchy Lookup Name";
     //String MANAGER_LOOKUP_NAME = "Manager Lookup Name";

    //Constants defined for Scheduled Task Attributes
     String IDOC_FOLDER_PATH = "IDOC Folder Path";
     String FILE_ARCHIVAL_FOLDER = "File Archival Folder";
     String FILE_ARCHIVAL = "File Archival";     
    
     String EMPLOYEE_TYPE_QUERY = "Employee Type Query";   
     String CUSTOM_QUERY_LOOKUP = "Custom Query Lookup";   
     String TOP_MOST_ORG_LOOKUP_NAME = "Top Most Organization Lookup";
     
     String UPDATE_EMPTY_MANAGER_ONLY = "Update users with empty manager id only";
    
    //Constants defined for IT Resource	
     String SERVER_NAME = "Server name";
     String REPOSITORY_DEST = "Repository destination";

    //These are constants defined in Lookup.SAP.HRMS.Constants
     String IDOC_TYPE = "IDOC Type";
     String SEGMENT = "Segment";
     String FIELD = "Field";
     String SEGMENT_DEFINITION = "Segment Definition";
     String SEGMENT_TYPE = "Segment Type";
     String FIELD_NAME = "Field Name";
     String START_POSITION = "Start Position";
     String END_POSITION = "End Position";
     String BEGIN_DATE_FIELD = "Begin Date Field";
     String SUB_TYPE_FIELD = "Sub Type Field";
     String DISABLED = "Disabled";
     String STATUS = "Status";
     String ACTIVE = "Active";
     String MANAGER_ID = "Manager ID";
         String MANAGER = "Manager";
   ///Bug 12409614
     String ORG_UNIT = "Org Unit";
     String Get_MANAGER_ID="Get Manager ID During Recon";
   ///Bug 12409614
     String USER_PROVISIONING_DATE = "User Provisioning Date";
     String ORGANIZATION = "Organization";
     String EMPLOYEE_TYPE = "Employee Type";
     String BAPI_NAME = "BAPI Name for Reading IDOC";
     String USER_LOGIN_FIELD = "User Login Field";
     String USER_TYPE = "User Type";
     //String PERSONNEL_NOS_UDF = "Personnel Number User Defined Field Column";
     String USER_CREATED_FROM_HRMS_UDF="User Created From HRMS User Defined Field Column";
     String MANAGER_UDF_COL = "Manager User Defined Field Column";
     String ORGANIZATION_UDF_COL = "Organization User Defined Field Column";

     //Start: Bug 17211711
	 String MANAGER_KEY_COL = "Manager Key Column";
	 //End: Bug 17211711
     String MANAGER_LOGIN_COL = "Manager Login Column";
     String USER_CREATED_FROM_HRMS = "User Created From HRMS";
     String PERSONNEL_NUMBER = "Personnel Number";
     String LISTENER_DATE_FORMAT = "Listener Date Format";
     String FILE_DATE_FORMAT = "File Date Format";
     String FILE_SEPERATOR = "File Seperator";

     //String USER_ID_OIM = "OIM User ID";    
     String EMPLOYEE_GROUP ="Employee Group";
     String EMPLOYEE_SUB_GROUP ="Employee Sub Group";    
     String DATE_FIELD_TYPE ="Field Type Date";
     String FILE_ENCODING_TYPE ="File Encoding Type";
     String ARCHIVAL_DATE_FORMAT ="File Archival Date Format";
     String GROUP_UDF = "Employee Group User Defined Field Column";
     String SUB_GROUP_UDF = "Employee Sub Group User Defined Field Column";
    //Constants defined for Lookup Recon
     String LOOKUP_BAPI_STRUCTURE = "Lookup Recon BAPI Structure";
     String LOOKUP_BAPI_STRUC_FIELD_NAME = "Lookup Recon Structure Field Name";   
     String LOOKUP_BAPI_STRUC_FIELD_VALUE = "Lookup Recon Structure Field Value";      
     String LOOKUP_BAPI_TABLE_NAME = "Lookup Recon BAPI Table Name";  
     String LOOKUP_BAPI_NAME = "Lookup Recon BAPI Name";
     String LOOKUP_BAPI_PARAM_FIELD = "Lookup Recon BAPI Field Parameter Name";
     String LOOKUP_BAPI_PARAM_VALUE = "Lookup Recon BAPI Field Parameter Values";      
     String LOOKUP_BAPI_FIELDNAME = "Lookup Recon BAPI Field Name";
     String LOOKUP_BAPI_FIELDVALUE = "Lookup Recon BAPI Field Values";
     String OFFSET = "Lookup Recon Offset";
     String LENG = "Lookup Recon Length";
     String LOOKUP_BAPI_TABLE_HELPVALUES = "Lookup Recon BAPI Table HelpValues";
     String LOOKUP_MANAGER_PARAMETER_NAMES = "Lookup Recon Parameter Field Name for Manager";   
     String LOOKUP_ORG_PARAMETER_VALUES = "Lookup Recon Parameter Field Values for Organization";
     String LOOKUP_MANAGER_PARAMETER_VALUES = "Lookup Recon Parameter Field Values for Manager";
     String LOOKUP_MANAGER_BAPI_NAME = "Lookup Recon BAPI Name for Manager";    
     String MANAGER_LOOKUP_RECON_TABLE_NAME = "Manager Lookup Recon Table Name";
     String MANAGER_LOOKUP_RECON_SEQNR = "Manager Lookup Recon SEQNR Field";
     String MANAGER_LOOKUP_RECON_OBJID = "Manager Lookup Recon OBJID Field";
     String MANAGER_LOOKUP_RECON_PUP = "Manager Lookup Recon PUP Field";
     String MANAGER_LOOKUP_RECON_OTYPE = "Manager Lookup Recon OTYPE  Field";
     String MANAGER_LOOKUP_RECON_OTYPE_VALUE = "Manager Lookup Recon Field  OTYPE Value";
    
    //General constants used    
     String HRMS_LOGGER = "OIMCP.SAPH";
     String CURRENT = "current";
     String FUTURE = "future";

    
}
