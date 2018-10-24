package oracle.iam.connectors.sap.common.connection;

import oracle.iam.connectors.common.Constants;

/**
 * Description: Contains all constants used to set up a connection with the target system
 */
public interface SAPConstants extends Constants {
	//IT Resource constants
	String[] clientProperties = { "jco.destination.pool_capacity",
			"jco.client.lang", "jco.client.snc_partnername",
			"jco.client.ashost", "jco.client.user", "jco.client.snc_mode",
			"jco.client.snc_myname", "jco.destination.peak_limit",
			"jco.client.snc_lib", "jco.client.sysnr", "jco.client.passwd",
			"jco.client.client", "jco.client.snc_qop", "jco.client.mshost",
			"jco.client.snc_mode", "jco.client.snc_lib",
			"jco.client.snc_myname", "jco.client.snc_partnername",
			"jco.client.snc_qop", "jco.client.alias_user",
			"jco.client.codepage", "jco.client.cpic_trace", "jco.client.dest",
			"jco.destination.expiration_check_period",
			"jco.destination.expiration_time", "jco.client.getsso2",
			"jco.client.group", "jco.client.gwhost", "jco.client.gwserv",
			"jco.client.lcheck", "jco.destination.max_get_client_time",
			"jco.client.msserv", "jco.client.mysapsso2", "jco.client.pcs",
			"jco.client.r3name", "jco.destination.repository_destination",
			"jco.destination.repository.passwd",
			"jco.destination.repository.snc_mode",
			"jco.destination.repository.user", "jco.client.saprouter",
			"jco.client.tphost", "jco.client.tpname", "jco.client.trace",
			"jco.client.type", "jco.client.x509cert" };

	String[] serverProperties = { "jco.server.connection_count",
			"jco.server.gwhost", "jco.server.progid", "jco.server.gwserv",
			"jco.server.repository_destination", "jco.server.unicode",
			"jco.server.snc_mode", "jco.server.snc_lib",
			"jco.server.snc_myname", "jco.server.snc_qop", "jco.server.trace",
			"jco.server.max_startup_delay", "jco.server.saprouter" };

	String[] mandatoryITRes = { "Client logon", "User logon", "Password",
			"Language", "SNC mode" };
	String[] mandatoryITResSnc = { "SNC partner name", "SNC my name",
			"SNC lib", "SNC qop" };

	String[][] sFixedITResFileValues = { { "SNC qop", "1,2,3,8,9" },
			{ "SNC mode", "yes,no" }, { "Unicode mode", "yes,no" } };

	String ORG_LOOKUP_NAME = "Organization Hierarchy Lookup Name";
	String MANAGER_LOOKUP_NAME = "Manager Lookup Name";
	String PERSONNEL_NOS_UDF = "Personnel Number User Defined Field Column";
	String USER_ID_OIM = "OIM User ID";

	String IT_RESOURCE_MAPPING = "IT Resource Mapping";
	String SCHEDULE_TASK_NAME = "Schedule Task Name";
	String CONFIGURATION_LOOKUP = "Configuration Lookup";
	String IT_RESOURCE_NAME = "IT Resource";
	String LOOKUP_NAME = "Lookup Name";
	String CONSTANTS_LOOKUP = "Constants Lookup";
	String RESOURCE_OBJECT = "Resource Object";
	String DISABLE_USER = "Disable User";
	String ATTRIBUTE_MAPPING_LOOKUP = "Attribute Mapping Lookup";
	String CUSTOM_QUERY = "Custom Query";
	String AND_SPLIT_REGEX = "\\s[&]\\s";
	String OR_SPLIT_REGEX = "\\s[|]\\s";
	String TRUE = "True";

	String YES = "Yes";
	String ONE = "One";
	String NONE = "NONE";
	String NO = "No";
	String ZERO = "Zero";

	// Start :: Added for the bug 14209711
	String ENABLE_USER_USING_OIM_END_DATE = "Enable User Using OIM End Date";
	// End :: Added for the bug 14209711
	//Start: Added for Matching Rule Bug 17047491	
	String RULE_ATTR_LOOKUP="Recon Rule Attribute Lookup";
	//End: Added for Matching Rule Bug 17047491
}
