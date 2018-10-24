/**
 * SAPGRC_AC_IDM_REQUESTSTATUSVi_Document.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package oracle.iam.connectors.sap.grc.ws.getstatus;

public interface SAPGRC_AC_IDM_REQUESTSTATUSVi_Document extends java.rmi.Remote {
	public oracle.iam.connectors.sap.grc.ws.getstatus.RequestStatusDTO getStatus(
			java.lang.String requestId, java.lang.String language,java.lang.String userName, java.lang.String password)
			throws java.rmi.RemoteException,
			oracle.iam.connectors.sap.grc.ws.getstatus.BOException;
}
