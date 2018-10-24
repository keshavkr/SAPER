/**
 * SAPGRC_AC_IDM_AUDITTRAILVi_Document.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package oracle.iam.connectors.sap.grc.ws.audit;

public interface SAPGRC_AC_IDM_AUDITTRAILVi_Document extends java.rmi.Remote {
    public oracle.iam.connectors.sap.grc.ws.audit.AuditLogResult getAuditLogs(java.lang.String requestId, java.lang.String userFirstName, java.lang.String userLastName, java.lang.String fromDate, java.lang.String toDate, java.lang.String action, java.lang.String locale,java.lang.String userName, java.lang.String password) throws java.rmi.RemoteException, oracle.iam.connectors.sap.grc.ws.audit.BOException;
}
