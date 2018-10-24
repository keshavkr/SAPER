/**
 * ArrayOfAuditLogDTO1.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package oracle.iam.connectors.sap.grc.ws.audit;

public class ArrayOfAuditLogDTO1  implements java.io.Serializable {
    private oracle.iam.connectors.sap.grc.ws.audit.AuditLogDTO[] auditLogDTO;

    public ArrayOfAuditLogDTO1() {
    }

    public ArrayOfAuditLogDTO1(
           oracle.iam.connectors.sap.grc.ws.audit.AuditLogDTO[] auditLogDTO) {
           this.auditLogDTO = auditLogDTO;
    }


    /**
     * Gets the auditLogDTO value for this ArrayOfAuditLogDTO1.
     * 
     * @return auditLogDTO
     */
    public oracle.iam.connectors.sap.grc.ws.audit.AuditLogDTO[] getAuditLogDTO() {
        return auditLogDTO;
    }


    /**
     * Sets the auditLogDTO value for this ArrayOfAuditLogDTO1.
     * 
     * @param auditLogDTO
     */
    public void setAuditLogDTO(oracle.iam.connectors.sap.grc.ws.audit.AuditLogDTO[] auditLogDTO) {
        this.auditLogDTO = auditLogDTO;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfAuditLogDTO1)) return false;
        ArrayOfAuditLogDTO1 other = (ArrayOfAuditLogDTO1) obj;
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
              java.util.Arrays.equals(this.auditLogDTO, other.getAuditLogDTO())));
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
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAuditLogDTO());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAuditLogDTO(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ArrayOfAuditLogDTO1.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "ArrayOfAuditLogDTO1"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("auditLogDTO");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "auditLogDTO"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "AuditLogDTO"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "AuditLogDTO"));
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
