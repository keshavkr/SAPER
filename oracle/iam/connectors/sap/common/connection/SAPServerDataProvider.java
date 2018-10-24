/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/common/connection/SAPServerDataProvider.java /main/5 2009/08/14 02:21:03 ddkumar Exp $ */

/* Copyright (c) 2009, Oracle and/or its affiliates. All rights reserved. */

/*
   DESCRIPTION
    <short description of component this file declares/defines>

   PRIVATE CLASSES
    <list of private classes defined - with one-line descriptions>

   NOTES
    <other useful comments, qualifications, etc.>

   MODIFIED    (MM/DD/YY)
    ddkumar     01/30/09 - Creation
 */

/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/common/connection/SAPServerDataProvider.java /main/5 2009/08/14 02:21:03 ddkumar Exp $
 *  @author  ddkumar 
 *  @since   release specific (what release of product did this appear in)
 */
package oracle.iam.connectors.sap.common.connection;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.sap.conn.jco.ext.ServerDataEventListener;
import com.sap.conn.jco.ext.ServerDataProvider;

/**
 * Description: Used for connection purposes 
 */
public class SAPServerDataProvider implements ServerDataProvider {

	public static boolean isRegister = false;
	private static SAPServerDataProvider jcoserverprovider = null;
	Map<String, Properties> propertiesForServerName = new HashMap<String, Properties>();

	public static SAPServerDataProvider getInstance() {
		if (jcoserverprovider == null) {
			jcoserverprovider = new SAPServerDataProvider();
		}

		return jcoserverprovider;
	}

	public void addServerProperties(String sServerName, Properties properties) {
		synchronized (propertiesForServerName) {
			propertiesForServerName.put(sServerName, properties);
		}
	}

	public Properties getServerProperties(String sServerName) {
		if (propertiesForServerName.containsKey(sServerName)) {
			return (Properties) propertiesForServerName.get(sServerName);
		} else {
			throw new RuntimeException("JCo Server not found: " + sServerName);
		}
	}

	public boolean supportsEvents() {

		return false;
	}

	public void setServerDataEventListener(ServerDataEventListener arg0) {

	}

}
