/**
 * AuditLogResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package oracle.iam.connectors.sap.grc.ws.audit;

public class AuditLogResult  implements java.io.Serializable {
    private oracle.iam.connectors.sap.grc.ws.audit.ArrayOfAuditLogDTO1 auditLogDTO;

    private oracle.iam.connectors.sap.grc.ws.audit.ServiceStatusDTO2 status;

    public AuditLogResult() {
    }

    public AuditLogResult(
           oracle.iam.connectors.sap.grc.ws.audit.ArrayOfAuditLogDTO1 auditLogDTO,
           oracle.iam.connectors.sap.grc.ws.audit.ServiceStatusDTO2 status) {
           this.auditLogDTO = auditLogDTO;
           this.status = status;
    }


    /**
     * Gets the auditLogDTO value for this AuditLogResult.
     * 
     * @return auditLogDTO
     */
    public oracle.iam.connectors.sap.grc.ws.audit.ArrayOfAuditLogDTO1 getAuditLogDTO() {
        return auditLogDTO;
    }


    /**
     * Sets the auditLogDTO value for this AuditLogResult.
     * 
     * @param auditLogDTO
     */
    public void setAuditLogDTO(oracle.iam.connectors.sap.grc.ws.audit.ArrayOfAuditLogDTO1 auditLogDTO) {
        this.auditLogDTO = auditLogDTO;
    }


    /**
     * Gets the status value for this AuditLogResult.
     * 
     * @return status
     */
    public oracle.iam.connectors.sap.grc.ws.audit.ServiceStatusDTO2 getStatus() {
        return status;
    }


    /**
     * Sets the status value for this AuditLogResult.
     * 
     * @param status
     */
    public void setStatus(oracle.iam.connectors.sap.grc.ws.audit.ServiceStatusDTO2 status) {
        this.status = status;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AuditLogResult)) return false;
        AuditLogResult other = (AuditLogResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.auditLogDTO==null && other.getAuditLogDTO()==null) || 
             (this.auditLogDTO!=null &&
              this.auditLogDTO.equals(other.getAuditLogDTO()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAuditLogDTO() != null) {
            _hashCode += getAuditLogDTO().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AuditLogResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "AuditLogResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("auditLogDTO");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "auditLogDTO"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "ArrayOfAuditLogDTO1"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "ServiceStatusDTO2"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
