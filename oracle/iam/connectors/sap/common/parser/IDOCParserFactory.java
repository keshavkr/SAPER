/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/common/parser/IDOCParserFactory.java /main/11 2009/08/14 02:21:03 ddkumar Exp $ */
/* Copyright (c) 2009, Oracle and/or its affiliates. All rights reserved. */
/*
 DESCRIPTION
 <short description of component this file declares/defines>

 PRIVATE CLASSES
 <list of private classes defined - with one-line descriptions>

 NOTES
 <other useful comments, qualifications, etc.>

 MODIFIED    (MM/DD/YY)
 ddkumar     01/14/09 - This factory class will return the instance of the
 class defined in configuration lookup based on the message type defined in
 configuration lookup
 ddkumar     01/14/09 - Creation
 */
/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/common/parser/IDOCParserFactory.java /main/11 2009/08/14 02:21:03 ddkumar Exp $
 *  @author  ddkumar
 *  @since   release specific (what release of product did this appear in)
 */
package oracle.iam.connectors.sap.common.parser;

import java.util.HashMap;

import oracle.iam.connectors.common.ConnectorException;
import oracle.iam.connectors.common.ConnectorLogger;
import oracle.iam.connectors.sap.hrms.util.HRMSConstants;

/**
 * Description:Returns the instance of the class defined in the configuration
 * lookup definition based on the message type
 */
public class IDOCParserFactory implements HRMSConstants {
    private String className = this.getClass().getName();

    /**
	 * Description : Calls the parser based on IDocs parameters
	 * 
	 * @param htHRMSConfig
	 *            Hashtable containing configuration details from the
	 *            Lookup.SAP.HRMS.Configuration lookup definition
	 */
    public IDOCParser getIDOCParser(HashMap htHRMSConfig, ConnectorLogger logger)
        throws ConnectorException {
        IDOCParser parser = null;       
        String sMethodName = "getIDOCParser()";
        logger.setMethodStartLog(className, sMethodName);
        try {
            //Get the name of the class from the Configuration Lookup
        	  //and return that class instance
        	  String sClassName = (String) htHRMSConfig.get(CLASS_NAME);
            parser = (IDOCParser) Class.forName(sClassName).newInstance();
        } catch (InstantiationException e) {
            logger.setStackTrace(e, className, sMethodName, e.getMessage());
            throw new ConnectorException(e);
        } catch (IllegalAccessException e) {
            logger.setStackTrace(e, className, sMethodName, e.getMessage());
            throw new ConnectorException(e);
        } catch (ClassNotFoundException e) {
            logger.setStackTrace(e, className, sMethodName, e.getMessage());
            throw new ConnectorException(e);
        } catch (Exception e) {
            logger.setStackTrace(e, className, sMethodName, e.getMessage());
            throw new ConnectorException(e);
        }

        logger.debug(className, sMethodName, "Class Name Returned::" + parser);
        logger.setMethodFinishLog(className, sMethodName);

        return parser;
    }
}
