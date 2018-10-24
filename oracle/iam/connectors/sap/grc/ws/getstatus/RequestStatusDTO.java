/**
 * RequestStatusDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package oracle.iam.connectors.sap.grc.ws.getstatus;

public class RequestStatusDTO  implements java.io.Serializable {
    private java.lang.String dueDate;

    private java.lang.String msgCode;

    private java.lang.String msgDesc;

    private java.lang.String msgType;

    private java.lang.String requestNumber;

    private java.lang.String stage;

    private java.lang.String status;

    private java.lang.String userName;

    public RequestStatusDTO() {
    }

    public RequestStatusDTO(
           java.lang.String dueDate,
           java.lang.String msgCode,
           java.lang.String msgDesc,
           java.lang.String msgType,
           java.lang.String requestNumber,
           java.lang.String stage,
           java.lang.String status,
           java.lang.String userName) {
           this.dueDate = dueDate;
           this.msgCode = msgCode;
           this.msgDesc = msgDesc;
           this.msgType = msgType;
           this.requestNumber = requestNumber;
           this.stage = stage;
           this.status = status;
           this.userName = userName;
    }


    /**
     * Gets the dueDate value for this RequestStatusDTO.
     * 
     * @return dueDate
     */
    public java.lang.String getDueDate() {
        return dueDate;
    }


    /**
     * Sets the dueDate value for this RequestStatusDTO.
     * 
     * @param dueDate
     */
    public void setDueDate(java.lang.String dueDate) {
        this.dueDate = dueDate;
    }


    /**
     * Gets the msgCode value for this RequestStatusDTO.
     * 
     * @return msgCode
     */
    public java.lang.String getMsgCode() {
        return msgCode;
    }


    /**
     * Sets the msgCode value for this RequestStatusDTO.
     * 
     * @param msgCode
     */
    public void setMsgCode(java.lang.String msgCode) {
        this.msgCode = msgCode;
    }


    /**
     * Gets the msgDesc value for this RequestStatusDTO.
     * 
     * @return msgDesc
     */
    public java.lang.String getMsgDesc() {
        return msgDesc;
    }


    /**
     * Sets the msgDesc value for this RequestStatusDTO.
     * 
     * @param msgDesc
     */
    public void setMsgDesc(java.lang.String msgDesc) {
        this.msgDesc = msgDesc;
    }


    /**
     * Gets the msgType value for this RequestStatusDTO.
     * 
     * @return msgType
     */
    public java.lang.String getMsgType() {
        return msgType;
    }


    /**
     * Sets the msgType value for this RequestStatusDTO.
     * 
     * @param msgType
     */
    public void setMsgType(java.lang.String msgType) {
        this.msgType = msgType;
    }


    /**
     * Gets the requestNumber value for this RequestStatusDTO.
     * 
     * @return requestNumber
     */
    public java.lang.String getRequestNumber() {
        return requestNumber;
    }


    /**
     * Sets the requestNumber value for this RequestStatusDTO.
     * 
     * @param requestNumber
     */
    public void setRequestNumber(java.lang.String requestNumber) {
        this.requestNumber = requestNumber;
    }


    /**
     * Gets the stage value for this RequestStatusDTO.
     * 
     * @return stage
     */
    public java.lang.String getStage() {
        return stage;
    }


    /**
     * Sets the stage value for this RequestStatusDTO.
     * 
     * @param stage
     */
    public void setStage(java.lang.String stage) {
        this.stage = stage;
    }


    /**
     * Gets the status value for this RequestStatusDTO.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this RequestStatusDTO.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the userName value for this RequestStatusDTO.
     * 
     * @return userName
     */
    public java.lang.String getUserName() {
        return userName;
    }


    /**
     * Sets the userName value for this RequestStatusDTO.
     * 
     * @param userName
     */
    public void setUserName(java.lang.String userName) {
        this.userName = userName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RequestStatusDTO)) return false;
        RequestStatusDTO other = (RequestStatusDTO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dueDate==null && other.getDueDate()==null) || 
             (this.dueDate!=null &&
              this.dueDate.equals(other.getDueDate()))) &&
            ((this.msgCode==null && other.getMsgCode()==null) || 
             (this.msgCode!=null &&
              this.msgCode.equals(other.getMsgCode()))) &&
            ((this.msgDesc==null && other.getMsgDesc()==null) || 
             (this.msgDesc!=null &&
              this.msgDesc.equals(other.getMsgDesc()))) &&
            ((this.msgType==null && other.getMsgType()==null) || 
             (this.msgType!=null &&
              this.msgType.equals(other.getMsgType()))) &&
            ((this.requestNumber==null && other.getRequestNumber()==null) || 
             (this.requestNumber!=null &&
              this.requestNumber.equals(other.getRequestNumber()))) &&
            ((this.stage==null && other.getStage()==null) || 
             (this.stage!=null &&
              this.stage.equals(other.getStage()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.userName==null && other.getUserName()==null) || 
             (this.userName!=null &&
              this.userName.equals(other.getUserName())));
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
        if (getDueDate() != null) {
            _hashCode += getDueDate().hashCode();
        }
        if (getMsgCode() != null) {
            _hashCode += getMsgCode().hashCode();
        }
        if (getMsgDesc() != null) {
            _hashCode += getMsgDesc().hashCode();
        }
        if (getMsgType() != null) {
            _hashCode += getMsgType().hashCode();
        }
        if (getRequestNumber() != null) {
            _hashCode += getRequestNumber().hashCode();
        }
        if (getStage() != null) {
            _hashCode += getStage().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getUserName() != null) {
            _hashCode += getUserName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestStatusDTO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.requeststatus", "RequestStatusDTO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dueDate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.requeststatus", "dueDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msgCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.requeststatus", "msgCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msgDesc");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.requeststatus", "msgDesc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msgType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.requeststatus", "msgType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.requeststatus", "requestNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stage");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.requeststatus", "stage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.requeststatus", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.requeststatus", "userName"));
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
