/**
 * Config1BindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package oracle.iam.connectors.sap.grc.ws.audit;

public class Config1BindingStub extends org.apache.axis.client.Stub implements oracle.iam.connectors.sap.grc.ws.audit.SAPGRC_AC_IDM_AUDITTRAILVi_Document {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[1];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAuditLogs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:SAPGRC_AC_IDM_AUDITTRAILVi", "requestId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:SAPGRC_AC_IDM_AUDITTRAILVi", "userFirstName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:SAPGRC_AC_IDM_AUDITTRAILVi", "userLastName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:SAPGRC_AC_IDM_AUDITTRAILVi", "fromDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:SAPGRC_AC_IDM_AUDITTRAILVi", "toDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:SAPGRC_AC_IDM_AUDITTRAILVi", "action"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:SAPGRC_AC_IDM_AUDITTRAILVi", "locale"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "AuditLogResult"));
        oper.setReturnClass(oracle.iam.connectors.sap.grc.ws.audit.AuditLogResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:SAPGRC_AC_IDM_AUDITTRAILVi", "Response"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:SAPGRC_AC_IDM_AUDITTRAILWsd/SAPGRC_AC_IDM_AUDITTRAILVi", "getAuditLogs_com.virsa.ae.core.BOException"),
                      "oracle.iam.connectors.sap.grc.ws.audit.BOException",
                      new javax.xml.namespace.QName("urn:com.virsa.ae.core", "BOException"), 
                      true
                     ));
        _operations[0] = oper;

    }

    public Config1BindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public Config1BindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public Config1BindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("urn:com.virsa.ae.core", "AEException");
            cachedSerQNames.add(qName);
            cls = oracle.iam.connectors.sap.grc.ws.audit.AEException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.virsa.ae.core", "BaseDTO");
            cachedSerQNames.add(qName);
            cls = oracle.iam.connectors.sap.grc.ws.audit.BaseDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.virsa.ae.core", "BOException");
            cachedSerQNames.add(qName);
            cls = oracle.iam.connectors.sap.grc.ws.audit.BOException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "ArrayOfAuditLogDTO");
            cachedSerQNames.add(qName);
            cls = oracle.iam.connectors.sap.grc.ws.audit.AuditLogDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "AuditLogDTO");
            qName2 = new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "AuditLogDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "ArrayOfAuditLogDTO1");
            cachedSerQNames.add(qName);
            cls = oracle.iam.connectors.sap.grc.ws.audit.ArrayOfAuditLogDTO1.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "ArrayOfRequestHistoryDTO");
            cachedSerQNames.add(qName);
            cls = oracle.iam.connectors.sap.grc.ws.audit.RequestHistoryDTO[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "RequestHistoryDTO");
            qName2 = new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "RequestHistoryDTO");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "AuditLogDTO");
            cachedSerQNames.add(qName);
            cls = oracle.iam.connectors.sap.grc.ws.audit.AuditLogDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "AuditLogResult");
            cachedSerQNames.add(qName);
            cls = oracle.iam.connectors.sap.grc.ws.audit.AuditLogResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "RequestHistoryDTO");
            cachedSerQNames.add(qName);
            cls = oracle.iam.connectors.sap.grc.ws.audit.RequestHistoryDTO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "ServiceStatusDTO2");
            cachedSerQNames.add(qName);
            cls = oracle.iam.connectors.sap.grc.ws.audit.ServiceStatusDTO2.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public oracle.iam.connectors.sap.grc.ws.audit.AuditLogResult getAuditLogs(java.lang.String requestId, java.lang.String userFirstName, java.lang.String userLastName, java.lang.String fromDate, java.lang.String toDate, java.lang.String action, java.lang.String locale,java.lang.String userName, java.lang.String password) throws java.rmi.RemoteException, oracle.iam.connectors.sap.grc.ws.audit.BOException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setUsername(userName);
        _call.setPassword(password);
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:SAPGRC_AC_IDM_AUDITTRAILVi", "getAuditLogs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {requestId, userFirstName, userLastName, fromDate, toDate, action, locale});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (oracle.iam.connectors.sap.grc.ws.audit.AuditLogResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (oracle.iam.connectors.sap.grc.ws.audit.AuditLogResult) org.apache.axis.utils.JavaUtils.convert(_resp, oracle.iam.connectors.sap.grc.ws.audit.AuditLogResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof oracle.iam.connectors.sap.grc.ws.audit.BOException) {
              throw (oracle.iam.connectors.sap.grc.ws.audit.BOException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

}
