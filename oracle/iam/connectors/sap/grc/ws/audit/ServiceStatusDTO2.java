/**
 * ServiceStatusDTO2.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package oracle.iam.connectors.sap.grc.ws.audit;

public class ServiceStatusDTO2  implements java.io.Serializable {
    private java.lang.String msgCode;

    private java.lang.String msgDesc;

    private java.lang.String msgType;

    public ServiceStatusDTO2() {
    }

    public ServiceStatusDTO2(
           java.lang.String msgCode,
           java.lang.String msgDesc,
           java.lang.String msgType) {
           this.msgCode = msgCode;
           this.msgDesc = msgDesc;
           this.msgType = msgType;
    }


    /**
     * Gets the msgCode value for this ServiceStatusDTO2.
     * 
     * @return msgCode
     */
    public java.lang.String getMsgCode() {
        return msgCode;
    }


    /**
     * Sets the msgCode value for this ServiceStatusDTO2.
     * 
     * @param msgCode
     */
    public void setMsgCode(java.lang.String msgCode) {
        this.msgCode = msgCode;
    }


    /**
     * Gets the msgDesc value for this ServiceStatusDTO2.
     * 
     * @return msgDesc
     */
    public java.lang.String getMsgDesc() {
        return msgDesc;
    }


    /**
     * Sets the msgDesc value for this ServiceStatusDTO2.
     * 
     * @param msgDesc
     */
    public void setMsgDesc(java.lang.String msgDesc) {
        this.msgDesc = msgDesc;
    }


    /**
     * Gets the msgType value for this ServiceStatusDTO2.
     * 
     * @return msgType
     */
    public java.lang.String getMsgType() {
        return msgType;
    }


    /**
     * Sets the msgType value for this ServiceStatusDTO2.
     * 
     * @param msgType
     */
    public void setMsgType(java.lang.String msgType) {
        this.msgType = msgType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ServiceStatusDTO2)) return false;
        ServiceStatusDTO2 other = (ServiceStatusDTO2) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.msgCode==null && other.getMsgCode()==null) || 
             (this.msgCode!=null &&
              this.msgCode.equals(other.getMsgCode()))) &&
            ((this.msgDesc==null && other.getMsgDesc()==null) || 
             (this.msgDesc!=null &&
              this.msgDesc.equals(other.getMsgDesc()))) &&
            ((this.msgType==null && other.getMsgType()==null) || 
             (this.msgType!=null &&
              this.msgType.equals(other.getMsgType())));
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
        if (getMsgCode() != null) {
            _hashCode += getMsgCode().hashCode();
        }
        if (getMsgDesc() != null) {
            _hashCode += getMsgDesc().hashCode();
        }
        if (getMsgType() != null) {
            _hashCode += getMsgType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ServiceStatusDTO2.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "ServiceStatusDTO2"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msgCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "msgCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msgDesc");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "msgDesc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msgType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "msgType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
