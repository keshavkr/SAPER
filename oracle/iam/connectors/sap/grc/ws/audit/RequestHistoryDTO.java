/**
 * RequestHistoryDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package oracle.iam.connectors.sap.grc.ws.audit;

public class RequestHistoryDTO  extends oracle.iam.connectors.sap.grc.ws.audit.BaseDTO  implements java.io.Serializable {
    private java.util.Calendar actionDate;

    private java.lang.String actionValue;

    private oracle.iam.connectors.sap.grc.ws.audit.RequestHistoryDTO[] childDTOs;

    private java.lang.String dependentId;

    private java.lang.String description;

    private java.lang.String displayString;

    private java.lang.String id;

    private java.lang.String path;

    private java.lang.String reqNo;

    private java.lang.String stage;

    private java.lang.String userId;

    public RequestHistoryDTO() {
    }

    public RequestHistoryDTO(
           java.util.Calendar actionDate,
           java.lang.String actionValue,
           oracle.iam.connectors.sap.grc.ws.audit.RequestHistoryDTO[] childDTOs,
           java.lang.String dependentId,
           java.lang.String description,
           java.lang.String displayString,
           java.lang.String id,
           java.lang.String path,
           java.lang.String reqNo,
           java.lang.String stage,
           java.lang.String userId) {
        this.actionDate = actionDate;
        this.actionValue = actionValue;
        this.childDTOs = childDTOs;
        this.dependentId = dependentId;
        this.description = description;
        this.displayString = displayString;
        this.id = id;
        this.path = path;
        this.reqNo = reqNo;
        this.stage = stage;
        this.userId = userId;
    }


    /**
     * Gets the actionDate value for this RequestHistoryDTO.
     * 
     * @return actionDate
     */
    public java.util.Calendar getActionDate() {
        return actionDate;
    }


    /**
     * Sets the actionDate value for this RequestHistoryDTO.
     * 
     * @param actionDate
     */
    public void setActionDate(java.util.Calendar actionDate) {
        this.actionDate = actionDate;
    }


    /**
     * Gets the actionValue value for this RequestHistoryDTO.
     * 
     * @return actionValue
     */
    public java.lang.String getActionValue() {
        return actionValue;
    }


    /**
     * Sets the actionValue value for this RequestHistoryDTO.
     * 
     * @param actionValue
     */
    public void setActionValue(java.lang.String actionValue) {
        this.actionValue = actionValue;
    }


    /**
     * Gets the childDTOs value for this RequestHistoryDTO.
     * 
     * @return childDTOs
     */
    public oracle.iam.connectors.sap.grc.ws.audit.RequestHistoryDTO[] getChildDTOs() {
        return childDTOs;
    }


    /**
     * Sets the childDTOs value for this RequestHistoryDTO.
     * 
     * @param childDTOs
     */
    public void setChildDTOs(oracle.iam.connectors.sap.grc.ws.audit.RequestHistoryDTO[] childDTOs) {
        this.childDTOs = childDTOs;
    }


    /**
     * Gets the dependentId value for this RequestHistoryDTO.
     * 
     * @return dependentId
     */
    public java.lang.String getDependentId() {
        return dependentId;
    }


    /**
     * Sets the dependentId value for this RequestHistoryDTO.
     * 
     * @param dependentId
     */
    public void setDependentId(java.lang.String dependentId) {
        this.dependentId = dependentId;
    }


    /**
     * Gets the description value for this RequestHistoryDTO.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this RequestHistoryDTO.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the displayString value for this RequestHistoryDTO.
     * 
     * @return displayString
     */
    public java.lang.String getDisplayString() {
        return displayString;
    }


    /**
     * Sets the displayString value for this RequestHistoryDTO.
     * 
     * @param displayString
     */
    public void setDisplayString(java.lang.String displayString) {
        this.displayString = displayString;
    }


    /**
     * Gets the id value for this RequestHistoryDTO.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this RequestHistoryDTO.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the path value for this RequestHistoryDTO.
     * 
     * @return path
     */
    public java.lang.String getPath() {
        return path;
    }


    /**
     * Sets the path value for this RequestHistoryDTO.
     * 
     * @param path
     */
    public void setPath(java.lang.String path) {
        this.path = path;
    }


    /**
     * Gets the reqNo value for this RequestHistoryDTO.
     * 
     * @return reqNo
     */
    public java.lang.String getReqNo() {
        return reqNo;
    }


    /**
     * Sets the reqNo value for this RequestHistoryDTO.
     * 
     * @param reqNo
     */
    public void setReqNo(java.lang.String reqNo) {
        this.reqNo = reqNo;
    }


    /**
     * Gets the stage value for this RequestHistoryDTO.
     * 
     * @return stage
     */
    public java.lang.String getStage() {
        return stage;
    }


    /**
     * Sets the stage value for this RequestHistoryDTO.
     * 
     * @param stage
     */
    public void setStage(java.lang.String stage) {
        this.stage = stage;
    }


    /**
     * Gets the userId value for this RequestHistoryDTO.
     * 
     * @return userId
     */
    public java.lang.String getUserId() {
        return userId;
    }


    /**
     * Sets the userId value for this RequestHistoryDTO.
     * 
     * @param userId
     */
    public void setUserId(java.lang.String userId) {
        this.userId = userId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RequestHistoryDTO)) return false;
        RequestHistoryDTO other = (RequestHistoryDTO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.actionDate==null && other.getActionDate()==null) || 
             (this.actionDate!=null &&
              this.actionDate.equals(other.getActionDate()))) &&
            ((this.actionValue==null && other.getActionValue()==null) || 
             (this.actionValue!=null &&
              this.actionValue.equals(other.getActionValue()))) &&
            ((this.childDTOs==null && other.getChildDTOs()==null) || 
             (this.childDTOs!=null &&
              java.util.Arrays.equals(this.childDTOs, other.getChildDTOs()))) &&
            ((this.dependentId==null && other.getDependentId()==null) || 
             (this.dependentId!=null &&
              this.dependentId.equals(other.getDependentId()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.displayString==null && other.getDisplayString()==null) || 
             (this.displayString!=null &&
              this.displayString.equals(other.getDisplayString()))) &&
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.path==null && other.getPath()==null) || 
             (this.path!=null &&
              this.path.equals(other.getPath()))) &&
            ((this.reqNo==null && other.getReqNo()==null) || 
             (this.reqNo!=null &&
              this.reqNo.equals(other.getReqNo()))) &&
            ((this.stage==null && other.getStage()==null) || 
             (this.stage!=null &&
              this.stage.equals(other.getStage()))) &&
            ((this.userId==null && other.getUserId()==null) || 
             (this.userId!=null &&
              this.userId.equals(other.getUserId())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getActionDate() != null) {
            _hashCode += getActionDate().hashCode();
        }
        if (getActionValue() != null) {
            _hashCode += getActionValue().hashCode();
        }
        if (getChildDTOs() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getChildDTOs());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getChildDTOs(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDependentId() != null) {
            _hashCode += getDependentId().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getDisplayString() != null) {
            _hashCode += getDisplayString().hashCode();
        }
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getPath() != null) {
            _hashCode += getPath().hashCode();
        }
        if (getReqNo() != null) {
            _hashCode += getReqNo().hashCode();
        }
        if (getStage() != null) {
            _hashCode += getStage().hashCode();
        }
        if (getUserId() != null) {
            _hashCode += getUserId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestHistoryDTO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "RequestHistoryDTO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actionDate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "actionDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actionValue");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "actionValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("childDTOs");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "childDTOs"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "RequestHistoryDTO"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "RequestHistoryDTO"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dependentId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "dependentId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("displayString");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "displayString"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("path");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "path"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reqNo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "reqNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stage");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "stage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil.auditlogs", "userId"));
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
