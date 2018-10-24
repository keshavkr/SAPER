/**
 * RequestSubmissionResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package oracle.iam.connectors.sap.grc.ws.submitreq;

public class RequestSubmissionResult  implements java.io.Serializable {
    private java.lang.String requestNo;

    private oracle.iam.connectors.sap.grc.ws.submitreq.ServiceStatusDTO status;

    public RequestSubmissionResult() {
    }

    public RequestSubmissionResult(
           java.lang.String requestNo,
           oracle.iam.connectors.sap.grc.ws.submitreq.ServiceStatusDTO status) {
           this.requestNo = requestNo;
           this.status = status;
    }


    /**
     * Gets the requestNo value for this RequestSubmissionResult.
     * 
     * @return requestNo
     */
    public java.lang.String getRequestNo() {
        return requestNo;
    }


    /**
     * Sets the requestNo value for this RequestSubmissionResult.
     * 
     * @param requestNo
     */
    public void setRequestNo(java.lang.String requestNo) {
        this.requestNo = requestNo;
    }


    /**
     * Gets the status value for this RequestSubmissionResult.
     * 
     * @return status
     */
    public oracle.iam.connectors.sap.grc.ws.submitreq.ServiceStatusDTO getStatus() {
        return status;
    }


    /**
     * Sets the status value for this RequestSubmissionResult.
     * 
     * @param status
     */
    public void setStatus(oracle.iam.connectors.sap.grc.ws.submitreq.ServiceStatusDTO status) {
        this.status = status;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RequestSubmissionResult)) return false;
        RequestSubmissionResult other = (RequestSubmissionResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.requestNo==null && other.getRequestNo()==null) || 
             (this.requestNo!=null &&
              this.requestNo.equals(other.getRequestNo()))) &&
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
        if (getRequestNo() != null) {
            _hashCode += getRequestNo().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestSubmissionResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.submitrequest", "RequestSubmissionResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestNo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.submitrequest", "requestNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.submitrequest", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "ServiceStatusDTO"));
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
