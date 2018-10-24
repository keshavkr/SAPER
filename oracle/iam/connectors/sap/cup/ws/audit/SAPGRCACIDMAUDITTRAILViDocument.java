 
package oracle.iam.connectors.sap.cup.ws.audit;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * Oracle JAX-WS 2.1.3-06/19/2008 07:03 PM(bt)
 * Generated source version: 2.1
 * 
 */
@WebService(name = "SAPGRC_AC_IDM_AUDITTRAILVi_Document", targetNamespace = "urn:SAPGRC_AC_IDM_AUDITTRAILWsd/SAPGRC_AC_IDM_AUDITTRAILVi/document")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface SAPGRCACIDMAUDITTRAILViDocument {


    /**
     * 
     * @param fromDate
     * @param userFirstName
     * @param requestId
     * @param locale
     * @param action
     * @param toDate
     * @param userLastName
     * @return
     *     returns examples.webservices.simple_client.audittrial.AuditLogResult
     * @throws GetAuditLogsComVirsaAeCoreBOExceptionDoc
     */
    @WebMethod
    @WebResult(name = "Response", targetNamespace = "urn:SAPGRC_AC_IDM_AUDITTRAILVi")
    @RequestWrapper(localName = "getAuditLogs", targetNamespace = "urn:SAPGRC_AC_IDM_AUDITTRAILVi", className = "oracle.iam.connectors.sap.cup.ws.audit.GetAuditLogs")
    @ResponseWrapper(localName = "getAuditLogsResponse", targetNamespace = "urn:SAPGRC_AC_IDM_AUDITTRAILVi", className = "oracle.iam.connectors.sap.cup.ws.audit.GetAuditLogsResponse")
    public AuditLogResult getAuditLogs(
        @WebParam(name = "requestId", targetNamespace = "urn:SAPGRC_AC_IDM_AUDITTRAILVi")
        String requestId,
        @WebParam(name = "userFirstName", targetNamespace = "urn:SAPGRC_AC_IDM_AUDITTRAILVi")
        String userFirstName,
        @WebParam(name = "userLastName", targetNamespace = "urn:SAPGRC_AC_IDM_AUDITTRAILVi")
        String userLastName,
        @WebParam(name = "fromDate", targetNamespace = "urn:SAPGRC_AC_IDM_AUDITTRAILVi")
        String fromDate,
        @WebParam(name = "toDate", targetNamespace = "urn:SAPGRC_AC_IDM_AUDITTRAILVi")
        String toDate,
        @WebParam(name = "action", targetNamespace = "urn:SAPGRC_AC_IDM_AUDITTRAILVi")
        String action,
        @WebParam(name = "locale", targetNamespace = "urn:SAPGRC_AC_IDM_AUDITTRAILVi")
        String locale)
        throws GetAuditLogsComVirsaAeCoreBOExceptionDoc
    ;

}
