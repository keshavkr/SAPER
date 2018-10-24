package oracle.iam.connectors.sap.ume.util;

import oracle.iam.connectors.sap.common.connection.SAPConstants;

public interface UMEConstants extends SAPConstants {
	String UM_LOGGER = "OIMCP.SAPU";

	String[] mandatoryLookupSchedulerAttrs = {"Lookup Name", "IT Resource"};

	String[] mandatoryUserReconSchedulerAttrs = {"IT Resource",
			"Resource Object", "Attribute Mapping Lookup",
			"Child Attribute Mapping Lookup", "SAP System Time Zone"};

	String[] mandatoryUserDeleteReconSchedulerAttrs = {"IT Resource",
			"Resource Object", "Last Execution Timestamp", "Disable User",
			"Batch Size", "SAP System Time Zone"};
	
	String[] mandatoryUMITRes = { "UME URL", "Admin User ID", "Admin Password"};
	String[][] sFixedTaskValues = {{"Disable User", "yes,no"}};
	
	
	String[][] sFixedUMITResFileValues = { { "SNC qop", "1,2,3,8,9" },
			{ "SNC mode", "yes,no" }};
	String[] mandatoryCUPUserDeleteReconSchedulerAttrs = {"IT Resource",
			"Resource Object"};

	//Task Scheduler Parameters
	String CHECK_BOX_LOOKUP = "Check Box Lookup for Recon";
	String BATCH_SIZE = "Batch Size";
	String EXECUTION_TIMESTAMP = "Last Execution Timestamp";
	String CHILD_ATTRIBUTE_MAPPING_LOOKUP = "Child Attribute Mapping Lookup";
	String CUSTOM_ATTRIBUTE_MAPPING_LOOKUP = "Custom Attribute Mapping Lookup";
	String CUSTOM_CHILD_ATTRIBUTE_MAPPING_LOOKUP = "Custom Child Attribute Mapping Lookup";
	String EXCLUSION_LIST = "Exclusion List Lookup";
	String TIMEZONEFORMAT="SAP System Time Zone";
	String DUMMY_PASSWORD = "Dummy password";
	//Config lookup
	String USE_CONNECTION_POOLING = "Connection pooling supported";
	String CUA_ENABLED = "Is CUA Enabled";
	String SYSTEM_NAME = "Master system name";
	String TRANSFORM_LOOKUP = "Transform Lookup For Recon";
	String VALIDATE_LOOKUP = "Validation Lookup For Recon";
	String TRANSFORM_LOOKUP_PROV = "Transform Lookup For Prov";
	String VALIDATE_LOOKUP_PROV = "Validation Lookup For Prov";
	String USE_RECON_VALIDATION = "Use Validation For Recon";
	String USE_TRANSFORM_MAPPING = "Use Transformation  For Recon";
	String USE_PROV_VALIDATION = "Use Validation For Prov";
	String USE_PROV_TRANSFORM_MAPPING = "Use Transformation  For Prov";
	String GLTGB = "GLTGB";
	String ACTIVITYGROUPS = "ACTIVITYGROUPS";
	String PROFILES = "PROFILES";
	String SUPPORT_LINKING = "Support HRMS 0105 Infotype Linking";
	String VALIDATE_PERNR = "Validate Personnel Number before Linking";
	String OVERWRITE_LINKING = "Overwrite Link";
	String PROV_ATTR_MAP = "Provisioning Attribute Map Lookup";
	String CHANGE_PASSWRD = "Change Password";
	String PASSWRD_DISABLED = "Password Disabled";
	String PROV_CHILD_ATTR_MAP = "Provisioning Child Attribute Map Lookup";
	String USE_TRANSFORM_MAPPING_LOOKUP = "Use Transformation  For Lookup Recon";
	String TRANSFORM_LOOKUP_RECON = "Transform Lookup For Lookup Recon";

	//General Constants
	String DATE_FORMAT = "RECON_DATE_FORMAT";
	String USER_ID = "RECON_USER_ID_FIELD";
	String IT_RESOURCE = "RECON_ITRESOURCE_FIELD";
	String STATUS = "RECON_STATUS_FIELD";
	String LOCK = "RECON_LOCK_FIELD";
	String DISABLED_STATUS = "DISABLED_STATUS";
	String ENABLED_STATUS = "ENABLED_STATUS";
	String BAPI_USER_LOCACTGROUPS_READ = "BAPI_NAME_ROLE_CUA";
	String BAPI_USER_LOCPROFILES_READ = "BAPI_NAME_PROFILE_CUA";
	String BAPI_USER_GET_DETAIL = "BAPI_NAME_ACCOUNT";
	String USERNAME = "USERNAME";
	String USH04_TABLE = "USH04";
	String USR04_TABLE = "USR04";
	String USH02_TABLE = "USH02";
	String USR02_TABLE = "USR02";
	String USZBVSYS_TABLE = "USZBVSYS";
	String RFC_READ_TABLE = "RFC_READ_TABLE";
	String FIELDTYPE_CHECKBOX = "CHECKBOX_FIELD";
	String FIELDTYPE_DATE = "DATE_FIELD";
	String FIELDTYPE_LOOKUP = "LOOKUP_FIELD";
	String UCLASSSYS="UCLASSSYS";
	String BAPI_USER_GET_LIST = "BAPI_USER_GETLIST";

	//lookup recon
	String QUERY_TABLE = "QUERY_TABLE";
	String ROWSKIPS = "ROWSKIPS";
	String ROWCOUNT = "ROWCOUNT";
	String OPTIONS = "OPTIONS";
	String TEXT = "TEXT";
	String FIELDS = "FIELDS";
	String DATA = "DATA";
	String FIELDNAME = "FIELDNAME";
	String OFFSET = "OFFSET";
	String LENGTH = "LENGTH";
	String LENG = "LENG";

	String OBJTYPE = "OBJTYPE";
	String METHOD = "METHOD";
	String PARAMETER = "PARAMETER";
	String FIELD = "FIELD";

	String TITLE_P = "TITLE_P";
	String COMPANY = "COMPANY";
	String AGR_NAME = "AGR_NAME";
	String PROFILE = "PROFILE";
	String EXPLICIT_SHLP = "EXPLICIT_SHLP";

	String SHLPNAME = "SHLPNAME";
	String SHLPTYPE = "SHLPTYPE";
	String LIC_TYPE = "LIC_TYPE";
	String SELECTION_FOR_HELPVALUES = "SELECTION_FOR_HELPVALUES";

	String SELECT_FLD = "SELECT_FLD";
	String SIGN = "SIGN";
	String OPTION = "OPTION";
	String LOW = "LOW";

	String DESCRIPTION_FOR_HELPVALUES = "DESCRIPTION_FOR_HELPVALUES";
	String HELPVALUES = "HELPVALUES";

	String BAPIPROF = "BAPIPROF";
	String SUBSYSTEM = "SUBSYSTEM";
	String SYSTEMNAME = "SYSTEMNAME";
	String WA = "WA";
	String UFLAG = "UFLAG";
	
	//IT Resource values
	String TIMEOUT_COUNT = "Timeout count";
	String TIMEOUT_RETRY_COUNT = "Timeout retry count";
	String CONFIG_LOOKUP = "Configuration lookup";
	String UME_RESOURCE_OBJECT = "Resource Object";
	
	String CONSTANTS_LOOKUP = "Constants Lookup";
	String RECON_ATTR_MAPPING_LOOKUP = "Recon Attribute Mapping Lookup";
	String RECON_CHILD_ATTR_MAPPING_LOOKUP = "Child Attribute Mapping Lookup";
	String UME_FULL_RECON_ATTR = "Full Recon Filter";
	String UME_CUSTOM_QUERY = "Custom Query";
	//Prov constants
	String RETRY= "RETRY";
	String PASSWORD = "PASSWORD";
	String TYPE ="TYPE";
	String S ="S";
	String X ="X";
	String Export = "Export";
	String NUMBER = "NUMBER";
	String No088 = "088";
	String No124 = "124";
	String No000 = "000";
	String RETURN = "RETURN";
	String MESSAGE = "MESSAGE";
	String LinkDateFormat = "Link Date Format";
	String LinkEndDate = "Link End Date";
	String EMPLOYEENUMBER = "EMPLOYEENUMBER";
	String SUBTYPE = "SUBTYPE";
	String VALIDITYBEGIN = "VALIDITYBEGIN";
	String VALIDITYEND = "VALIDITYEND";
	String COMMUNICATIONID = "COMMUNICATIONID";
	String BNAME = "BNAME";
	String NEW_PASSWORD = "NEW_PASSWORD";
	String W = "W";
	String No591 = "591";
	String No225 = "225";
	String CODE = "CODE";
	String PG001 ="PG001";
	String COMMUNICATION = "COMMUNICATION";
	String LinkSubType = "0001"; 
	String EnableDate = "Enable Account Date";
	String EnableDisableDF = "Enable disable date format";
	String RFC_ERROR_LOGON_FAILURE = "RFC_ERROR_LOGON_FAILURE";
	String RFC_ERROR_COMMUNICATION = "RFC_ERROR_COMMUNICATION";
	String VALIDEND = "VALIDEND";
	String ID = "ID";
	String PROV_CHECKBOX = "Check Box Lookup for Prov";
	String DOES_NOT_EXIST = "does not exist";
	
	//Provisioning responses
	String INSUFFICIENT_INFORMATION = "SAP.INSUFFICIENT_INFORMATION";
	String CONNECTION_ERROR = "SAP.CONNECTION_ERROR";
	String USER_ALREADY_EXISTS = "SAP.USER_ALREADY_EXISTS";
	String USER_IN_EXCLUSION_LIST = "SAP.USER_IN_EXCLUSION_LIST";
	String VALIDATION_FAILED = "SAP.VALIDATION_FAILED";
	String USER_DELETION_SUCCESSFUL = "SAP.USER_DELETION_SUCCESSFUL";
	String USER_DELETION_FAILED = "SAP.USER_DELETION_FAILED";
	String USER_CREATION_SUCCESSFUL = "SAP.USER_CREATION_SUCCESSFUL";
	String PASSWORD_CHANGE_SUCCESSFUL = "SAP.PASSWORD_CHANGE_SUCCESSFUL";
	String ADD_SYSTEM_SUCCESSFUL = "SAP.ADD_SYSTEM_SUCCESSFUL";
	String USER_CREATION_FAILED = "SAP.USER_CREATION_FAILED";
	String ATTRIBUTE_MAPPING_FAILED = "SAP.ATTRIBUTE_MAPPING_FAILED";
	String PASSWORD_CHANGE_FAILED = "SAP.PASSWORD_CHANGE_FAILED";
	String USER_MODIFIED_SUCCESSFUL = "SAP.USER_MODIFIED_SUCCESSFUL";
	String USER_MODIFICATION_FAILED = "SAP.USER_MODIFICATION_FAILED";
	String PASSWORD_NOT_ALLOWED = "SAP.PASSWORD_NOT_ALLOWED";
	String USER_LOCKED_SUCCESSFUL = "SAP.USER_LOCKED_SUCCESSFUL";
	String USER_UNLOCKED_SUCCESSFUL = "SAP.USER_UNLOCKED_SUCCESSFUL";
	String INVALID_LOCK_STATE = "SAP.INVALID_LOCK_STATE";
	String USER_LOCK_FAILED = "SAP.USER_LOCK_FAILED";
	String USER_UNLOCK_FAILED = "SAP.USER_UNLOCK_FAILED";
	String MULTIVALUE_DATA_ALREADY_EXISTS = "SAP.MULTIVALUE_DATA_ALREADY_EXISTS";
	String MULTI_DATA_ADDED_SUCCESSFUL = "SAP.MULTI_DATA_ADDED_SUCCESSFUL";
	String ADD_MULTIVALUE_DATA_FAILED = "SAP.ADD_MULTIVALUE_DATA_FAILED";
	String MULTI_DATA_REMOVE_SUCCESSFUL = "SAP.MULTI_DATA_REMOVE_SUCCESSFUL";
	String MULTI_DATA_REMOVE_FAILED = "SAP.MULTI_DATA_REMOVE_FAILED";
	String SYSTEM_NOT_MEMBER_OF_TARGET_SYSTEM = "SAP.SYSTEM_NOT_MEMBER_OF_TARGET_SYSTEM";
	String ADD_SYSTEM_FAILED = "SAP.ADD_SYSTEM_FAILED";
	String MULTI_DATA_UPDATED_SUCCESSFUL = "SAP.MULTI_DATA_UPDATED_SUCCESSFUL";
	String UPDATE_MULTIVALUE_DATA_FAILED = "SAP.UPDATE_MULTIVALUE_DATA_FAILED";
	String USER_DOES_NOT_EXIST = "SAP.USER_DOES_NOT_EXIST";
	String DISABLE_USER_SUCCESSFUL = "SAP.DISABLE_USER_SUCCESSFUL";
	String DISABLE_USER_FAILED = "SAP.DISABLE_USER_FAILED";
	String ENABLE_USER_SUCCESSFUL = "SAP.ENABLE_USER_SUCCESSFUL";
	String ENABLE_USER_FAILED = "SAP.ENABLE_USER_FAILED";
	String USER_LINK_SUCCESSFUL = "SAP.USER_LINK_SUCCESSFUL";
	String USER_LINK_FAILED = "SAP.USER_LINK_FAILED";
	String USER_ALREADY_LINKED = "SAP.USER_ALREADY_LINKED";
	String LINKING_NOT_ENABLED = "SAP.LINKING_NOT_ENABLED";
	String MULTIVALUE_DATA_DOES_NOT_EXIST = "SAP.MULTIVALUE_DATA_DOES_NOT_EXIST";
	
	//Password specific error responses
	String PASSWORD_DISABLED = "SAP.PASSWORD_DISABLED";
	String USER_DISABLED = "SAP.USER_DISABLED";
	String NAME_OR_PASSWORD_INCORRECT = "SAP.NAME_OR_PASSWORD_INCORRECT";
	String LOG_ON_WITH_DIALOG_USER = "SAP.LOG_ON_WITH_DIALOG_USER";
	String USER_IS_LOCKED = "SAP.USER_IS_LOCKED";
	String CHANGE_PASSWORD_ONCE_A_DAY = "SAP.CHANGE_PASSWORD_ONCE_A_DAY";
	String PASSWORD_EXPIRED = "SAP.PASSWORD_EXPIRED";
	String PASSWORD_CHANGE_DISABLED = "SAP.PASSWORD_CHANGE_DISABLED";
	String PROGRAM_ERROR_OCCURED = "SAP.PROGRAM_ERROR_OCCURED";
	String PASSWORD_DOES_NOT_EXIST = "SAP.PASSWORD_DOES_NOT_EXIST";
	String USER_PASSWORD_DOES_NOT_EXIST = "SAP.USER_PASSWORD_DOES_NOT_EXIST";
	String NO_REMOTE_PASSWORD_CHANGE = "SAP.NO_REMOTE_PASSWORD_CHANGE";
	String TOO_MANY_FAILED_ATTEMPTS = "SAP.TOO_MANY_FAILED_ATTEMPTS";
	String PASSWORD_RULES_TIGHTENED = "SAP.PASSWORD_RULES_TIGHTENED";
	String PASSWORD_DEACTIVATED = "SAP.PASSWORD_DEACTIVATED";
	
	//response numbers
	String No139 = "139";
	String No148= "148";
	String No152= "152";
	String No156= "156";
	String No158= "158";
	String No180= "180";
	String No182= "182";
	String No190= "190";
	String No191= "191";
	String No197= "197";
	String No198= "198";
	String No199= "199";
	String No200= "200";
	String No292= "292";
	String No790 = "790";
	
	//SAP Response
	String SAP_PASSWORD_NOT_ALLOWED = "PASSWORD_NOT_ALLOWED";
	
	//Prov BAPI names
	String BAPI_USER_CREATE = "BAPI_USER_CREATE";
	String BAPI_USER_CHANGE = "BAPI_USER_CHANGE";
	String BAPI_USER_EXISTENCE_CHECK = "BAPI_USER_EXISTENCE_CHECK";
	String BAPI_EMPLOYEET_ENQUEUE = "BAPI_EMPLOYEET_ENQUEUE";
	String BAPI_EMPLCOMM_CREATE = "BAPI_EMPLCOMM_CREATE";
	String BAPI_EMPLOYEET_DEQUEUE = "BAPI_EMPLOYEET_DEQUEUE";
	String SUSR_USER_CHANGE_PASSWORD_RFC = "SUSR_USER_CHANGE_PASSWORD_RFC";
	String BAPI_USER_DELETE = "BAPI_USER_DELETE";
	String BAPI_USER_LOCK = "BAPI_USER_LOCK";
	String BAPI_USER_UNLOCK = "BAPI_USER_UNLOCK";
	String BAPI_USER_LOCPROFILES_ASSIGN ="BAPI_USER_LOCPROFILES_ASSIGN";
	String BAPI_EMPLCOMM_GETDETAILEDLIST = "BAPI_EMPLCOMM_GETDETAILEDLIST";
	String BAPI_USER_LOCACTGROUPS_ASSIGN = "BAPI_USER_LOCACTGROUPS_ASSIGN";
	String BAPI_USER_ACTGROUPS_ASSIGN = "BAPI_USER_ACTGROUPS_ASSIGN";
	String BAPI_USER_PROFILES_ASSIGN = "BAPI_USER_PROFILES_ASSIGN";
	String BAPI_EMPLOYEE_CHECKEXISTENCE = "BAPI_EMPLOYEE_CHECKEXISTENCE";
	
	//CUP Constants
	String CUP_MODE_ENABLED = "CUP Mode Enabled";
	String ITR_FIELD = "IT Resource";
	String PARENT_ATTR_LOOKUP = "Parent Attribute Lookup";
	String CHILD_ATTR_LOOKUP = "Child Attribute Lookup";
	String APPLICATION = "Application";
	String PRIORITY = "Priority";
	String CREATE_USER = "Create User";
	String UD_REQUEST_ID = "RequestID UDF";
	String UD_REQUEST_STATUS = "RequestStatus UDF";
	String REQUEST_REJECTED = "REQUEST_REJECTED";
	String REQUEST_CREATION_SUCCESSFUL = "REQUEST_CREATION_SUCCESSFUL";
	String REQUEST_CREATION_FAILED = "REQUEST_CREATION_FAILED";
	String REQUEST_CLOSED_SUCCESSFUL = "REQUEST_CLOSED_SUCCESSFUL";
	String REJECT = "Reject status";
	String OPEN = "Open status";
	String CLOSED = "Closed status";
	String HOLD = "Hold status";
	String MODIFY_USER = "Modify User";
	String ASSIGN_ROLE = "Assign Role";
	String SYSID = "sysId";
	String ROLEID = "roleId";
	String REMOVE = "Remove Role Action";
	String ACTION = "action";
	String REQUEST_DETAILS_CLASS = "Request Details Class";
	String CUSTOM_FIELDS_CLASS = "Custom Fields Class";
	String requestType = "requestType";
	String set = "set";
	String application = "application";
	String priority = "priority";
	String CUSTOM = "CUSTOM";
	String setName = "setName";
	String setValue = "setValue";
	String setCustomField = "setCustomField";
	String setRoles = "setRoles";
	String ROLE_DATA_CLASS = "Role Data Class";
	String SERVER = "server";
	String PORT = "port";
	String SUBMIT_REQ_WSDL_PATH = "Submit Request WSDL Path";
	String SUBMIT_REQ_NAMESPACE_URI = "Submit Request Namespace URI";
	String SUBMIT_REQ_LOCAL_PART = "Submit Req Local Part";
	String username = "username";
	String password = "password";
	String REQ_STATUS_WSDL_PATH = "Request Status WSDL Path";
	String REQ_STATUS_NAMESPACE_URI = "Request Status Namespace URI";
	String REQ_STATUS_LOCAL_PART = "Req Status Local Part";
	String AUDIT_TRAIL_WSDL_PATH = "Audit Trail WSDL Path";
	String AUDIT_TRAIL_NAMESPACE_URI = "Audit Trail Namespace URI";
	String AUDIT_TRAIL_LOCAL_PART = "Audit Trail Local Part";
	String CUP_DATE_FORMAT = "CUP Dateformat";
	String DATE = "DATE";
	String STANDARD = "STANDARD";
	String MANDATORY = "MANDATORY";
	String LOCK_USER = "Lock User";
	String UNLOCK_USER = "Unlock User";
	String DELETE_USER = "Delete User";
	String IGNORE_CHECK = "Ignore User Created Check For Add Role";
	//CUP Config
	String CUP_CONFIG_LOOKUP = "CUP Configuration Lookup";
	String CUP_IGNORE_STATUS = "Ignore OPEN status";
	String USERLIST = "USERLIST";
	String CUP_USER_ID = "User ID";
	String ONE = "One";
	String ZERO = "Zero";
	
	String STATUS_REVOKED = "Revoked";
	String STATUS_PROVISIONING = "Provisioning";
	String STATUS_WAITING = "Waiting";
	
	String USER_FORMFIELD_USERID = "USRFORMCOL UserID";
	String USER_FORMFIELD_ITRES = "USRFORMCOL ITResource";
	String USER_ROFIELD_USERID = "USRROField UserID";
	
	String APPLICATION_NAME = "UD_UME_APPLICATION";
}
