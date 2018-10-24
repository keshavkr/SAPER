/**
 * AuditLogDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package oracle.iam.connectors.sap.grc.ws.audit;

public class AuditLogDTO  implements java.io.Serializable {
    private java.util.Calendar createDate;

    private java.lang.String logDetails;

    private java.lang.String priority;

    private oracle.iam.connectors.sap.grc.ws.audit.RequestHistoryDTO[] requestHst;

    private java.lang.String requestId;

    private java.lang.String requestedBy;

    private java.lang.String status;

    private java.lang.String submittedBy;

    public AuditLogDTO() {
    }

    public AuditLogDTO(
           java.util.Calendar createDate,
           java.lang.String logDetails,
           java.lang.String priority,
           oracle.iam.connectors.sap.grc.ws.audit.RequestHistoryDTO[] requestHst,
           java.lang.String requestId,
           java.lang.String requestedBy,
           java.lang.String status,
           java.lang.String submittedBy) {
           this.createDate = createDate;
           this.logDetails = logDetails;
           this.priority = priority;
           this.requestHst = requestHst;
           this.requestId = requestId;
           this.requestedBy = requestedBy;
           this.status = status;
           this.submittedBy = submittedBy;
    }


    /**
     * Gets the createDate value for this AuditLogDTO.
     * 
     * @return createDate
     */
    public java.util.Calendar getCreateDate() {
        return createDate;
    }


    /**
     * Sets the createDate value for this AuditLogDTO.
     * 
     * @param createDate
     */
    public void setCreateDate(java.util.Calendar createDate) {
        this.createDate = createDate;
    }


    /**
     * Gets the logDetails value for this AuditLogDTO.
     * 
     * @return logDetails
     */
    public java.lang.String getLogDetails() {
        return logDetails;
    }


    /**
     * Sets the logDetails value for this AuditLogDTO.
     * 
     * @param logDetails
     */
    public void setLogDetails(java.lang.String logDetails) {
        this.logDetails = logDetails;
    }


    /**
     * Gets the priority value for this AuditLogDTO.
     * 
     * @return priority
     */
    public java.lang.String getPriority() {
        return priority;
    }


    /**
     * Sets the priority value for this AuditLogDTO.
     * 
     * @param priority
     */
    public void setPriority(java.lang.String priority) {
        this.priority = priority;
    }


    /**
     * Gets the requestHst value for this AuditLogDTO.
     * 
     * @return requestHst
     */
    public oracle.iam.connectors.sap.grc.ws.audit.RequestHistoryDTO[] getRequestHst() {
        return requestHst;
    }


    /**
     * Sets the requestHst value for this AuditLogDTO.
     * 
     * @param requestHst
     */
    public void setRequestHst(oracle.iam.connectors.sap.grc.ws.audit.RequestHistoryDTO[] requestHst) {
        this.requestHst = requestHst;
    }


    /**
     * Gets the requestId value for this AuditLogDTO.
     * 
     * @return requestId
     */
    public java.lang.String getRequestId() {
        return requestId;
    }


    /**
     * Sets the requestId value for this AuditLogDTO.
     * 
     * @param requestId
     */
    public void setRequestId(java.lang.String requestId) {
        this.requestId = requestId;
    }


    /**
     * Gets the requestedBy value for this AuditLogDTO.
     * 
     * @return requestedBy
     */
    public java.lang.String getRequestedBy() {
        return requestedBy;
    }


    /**
     * Sets the requestedBy value for this AuditLogDTO.
     * 
     * @param requestedBy
     */
    public void setRequestedBy(java.lang.String requestedBy) {
        this.requestedBy = requestedBy;
    }


    /**
     * Gets the status value for this AuditLogDTO.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this AuditLogDTO.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the submittedBy value for this AuditLogDTO.
     * 
     * @return submittedBy
     */
    public java.lang.String getSubmittedBy() {
        return submittedBy;
    }


    /**
     * Sets the submittedBy value for this AuditLogDTO.
     * 
     * @param submittedBy
     */
    public void setSubmittedBy(java.lang.String submittedBy) {
        this.submittedBy = submittedBy;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AuditLogDTO)) return false;
        AuditLogDTO other = (AuditLogDTO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.createDate==null && other.getCreateDate()==null) || 
             (this.createDate!=null &&
              this.createDate.equals(other.getCreateDate()))) &&
            ((this.logDetails==null && other.getLogDetails()==null) || 
             (this.logDetails!=null &&
              this.logDetails.equals(other.getLogDetails()))) &&
            ((this.priority==null && other.getPriority()==null) || 
             (this.priority!=null &&
              this.priority.equals(other.getPriority()))) &&
            ((this.requestHst==null && other.getRequestHst()==null) || 
             (this.requestHst!=null &&
              java.util.Arrays.equals(this.requestHst, other.getRequestHst()))) &&
            ((this.requestId==null && other.getRequestId()==null) || 
             (this.requestId!=null &&
              this.requestId.equals(other.getRequestId()))) &&
            ((this.requestedBy==null && other.getRequestedBy()==null) || 
             (this.requestedBy!=null &&
              this.requestedBy.equals(other.getRequestedBy()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.submittedBy==null && other.getSubmittedBy()==null) || 
             (this.submittedBy!=null &&
              this.submittedBy.equals(other.getSubmittedBy())));
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
        if (getCreateDate() != null) {
            _hashCode += getCreateDate().hashCode();
        }
        if (getLogDetails() != null) {
            _hashCode += getLogDetails().hashCode();
        }
        if (getPriority() != null) {
            _hashCode += getPriority().hashCode();
        }
        if (getRequestHst() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRequestHst());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRequestHst(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRequestId() != null) {
            _hashCode += getRequestId().hashCode();
        }
        if (getRequestedBy() != null) {
            _hashCode += getRequestedBy().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getSubmittedBy() != null) {
            _hashCode += getSubmittedBy().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AuditLogDTO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "AuditLogDTO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("createDate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "createDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("logDetails");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "logDetails"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priority");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "priority"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestHst");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "requestHst"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "RequestHistoryDTO"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "RequestHistoryDTO"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "requestId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestedBy");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "requestedBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("submittedBy");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "submittedBy"));
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
