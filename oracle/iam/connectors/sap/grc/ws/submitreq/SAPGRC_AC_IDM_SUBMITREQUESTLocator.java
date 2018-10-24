/**
 * SAPGRC_AC_IDM_SUBMITREQUESTLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package oracle.iam.connectors.sap.grc.ws.submitreq;

public class SAPGRC_AC_IDM_SUBMITREQUESTLocator extends org.apache.axis.client.Service implements oracle.iam.connectors.sap.grc.ws.submitreq.SAPGRC_AC_IDM_SUBMITREQUEST {

    public SAPGRC_AC_IDM_SUBMITREQUESTLocator() {
    }


    public SAPGRC_AC_IDM_SUBMITREQUESTLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SAPGRC_AC_IDM_SUBMITREQUESTLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Config1Port_Document
    private java.lang.String Config1Port_Document_address = "http://macpsap01:50000/SAPGRC_AC_IDM_SUBMITREQUEST/Config1?style=document";

    public java.lang.String getConfig1Port_DocumentAddress() {
        return Config1Port_Document_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String Config1Port_DocumentWSDDServiceName = "Config1Port_Document";

    public java.lang.String getConfig1Port_DocumentWSDDServiceName() {
        return Config1Port_DocumentWSDDServiceName;
    }

    public void setConfig1Port_DocumentWSDDServiceName(java.lang.String name) {
        Config1Port_DocumentWSDDServiceName = name;
    }

    public oracle.iam.connectors.sap.grc.ws.submitreq.SAPGRC_AC_IDM_SUBMITREQUESTVi_Document getConfig1Port_Document() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Config1Port_Document_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getConfig1Port_Document(endpoint);
    }

    public oracle.iam.connectors.sap.grc.ws.submitreq.SAPGRC_AC_IDM_SUBMITREQUESTVi_Document getConfig1Port_Document(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            oracle.iam.connectors.sap.grc.ws.submitreq.Config1BindingStub _stub = new oracle.iam.connectors.sap.grc.ws.submitreq.Config1BindingStub(portAddress, this);
            _stub.setPortName(getConfig1Port_DocumentWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setConfig1Port_DocumentEndpointAddress(java.lang.String address) {
        Config1Port_Document_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (oracle.iam.connectors.sap.grc.ws.submitreq.SAPGRC_AC_IDM_SUBMITREQUESTVi_Document.class.isAssignableFrom(serviceEndpointInterface)) {
                oracle.iam.connectors.sap.grc.ws.submitreq.Config1BindingStub _stub = new oracle.iam.connectors.sap.grc.ws.submitreq.Config1BindingStub(new java.net.URL(Config1Port_Document_address), this);
                _stub.setPortName(getConfig1Port_DocumentWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("Config1Port_Document".equals(inputPortName)) {
            return getConfig1Port_Document();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:SAPGRC_AC_IDM_SUBMITREQUESTWsd", "SAPGRC_AC_IDM_SUBMITREQUEST");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:SAPGRC_AC_IDM_SUBMITREQUESTWsd", "Config1Port_Document"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("Config1Port_Document".equals(portName)) {
            setConfig1Port_DocumentEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
