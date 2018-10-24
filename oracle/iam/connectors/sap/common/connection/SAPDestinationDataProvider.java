/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/common/connection/SAPDestinationDataProvider.java /main/5 2009/08/14 02:21:03 ddkumar Exp $ */

/* Copyright (c) 2009, Oracle and/or its affiliates. All rights reserved. */

/*
 DESCRIPTION
 <short description of component this file declares/defines>

 PRIVATE CLASSES
 <list of private classes defined - with one-line descriptions>

 NOTES
 <other useful comments, qualifications, etc.>

 MODIFIED    (MM/DD/YY)
 ddkumar     01/30/09 - This class is used for connection purpose
 ddkumar     01/30/09 - Creation
 */

/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/common/connection/SAPDestinationDataProvider.java /main/5 2009/08/14 02:21:03 ddkumar Exp $
 *  @author  ddkumar 
 *  @since   release specific (what release of product did this appear in)
 */
package oracle.iam.connectors.sap.common.connection;

import java.util.HashMap;
import java.util.Properties;

import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

/**
 * Description: Used for connection purposes 
 */
public class SAPDestinationDataProvider implements DestinationDataProvider {
	HashMap<String, Properties> hmDestinationProps = new HashMap<String, Properties>();
	public static boolean isRegister = false;
	private static SAPDestinationDataProvider jcoprovider = null;

	public static SAPDestinationDataProvider getInstance() {
		if (jcoprovider == null) {
			jcoprovider = new SAPDestinationDataProvider();
		}
		return jcoprovider;
	}

	public void addDestination(String sDestinationName, Properties properties) {
		synchronized (hmDestinationProps) {
			hmDestinationProps.put(sDestinationName, properties);
		}
	}

	public Properties getDestinationProperties(String sDestinationName) {
		if (hmDestinationProps.containsKey(sDestinationName)) {
			return (Properties) hmDestinationProps.get(sDestinationName);
		} else {
			throw new RuntimeException("JCo destination not found: "
					+ sDestinationName);
		}
	}

	public boolean supportsEvents() {
		return false;
	}

	public void setDestinationDataEventListener(
			DestinationDataEventListener eventListener) {
	}
}
