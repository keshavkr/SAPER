/**
 * RequestDetailsData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package oracle.iam.connectors.sap.grc.ws.submitreq;

public class RequestDetailsData  implements java.io.Serializable {
    private java.lang.String application;

    private java.lang.String company;

    private oracle.iam.connectors.sap.grc.ws.submitreq.CustomFieldsDTO[] customField;

    private java.lang.String department;

    private java.lang.String emailAddress;

    private java.lang.String employeeType;

    private java.lang.String firstName;

    private java.lang.String functionalArea;

    private java.lang.String lastName;

    private java.lang.String locale;

    private java.lang.String location;

    private java.lang.String managerTelephone;

    private java.lang.String mgrEmailAddress;

    private java.lang.String mgrFirstName;

    private java.lang.String mgrId;

    private java.lang.String mgrLastName;

    private java.lang.String priority;

    private java.lang.String requestReason;

    private java.lang.String requestType;

    private java.lang.String requestorEmailAddress;

    private java.lang.String requestorFirstName;

    private java.lang.String requestorId;

    private java.lang.String requestorLastName;

    private java.lang.String requestorTelephone;

    private oracle.iam.connectors.sap.grc.ws.submitreq.RoleData[] roles;

    private java.lang.String sNCName;

    private java.lang.String telephone;

    private java.lang.Boolean unsecureLogon;

    private java.lang.String userId;

    private java.lang.String validFrom;

    private java.lang.String validTo;

    public RequestDetailsData() {
    }

    public RequestDetailsData(
           java.lang.String application,
           java.lang.String company,
           oracle.iam.connectors.sap.grc.ws.submitreq.CustomFieldsDTO[] customField,
           java.lang.String department,
           java.lang.String emailAddress,
           java.lang.String employeeType,
           java.lang.String firstName,
           java.lang.String functionalArea,
           java.lang.String lastName,
           java.lang.String locale,
           java.lang.String location,
           java.lang.String managerTelephone,
           java.lang.String mgrEmailAddress,
           java.lang.String mgrFirstName,
           java.lang.String mgrId,
           java.lang.String mgrLastName,
           java.lang.String priority,
           java.lang.String requestReason,
           java.lang.String requestType,
           java.lang.String requestorEmailAddress,
           java.lang.String requestorFirstName,
           java.lang.String requestorId,
           java.lang.String requestorLastName,
           java.lang.String requestorTelephone,
           oracle.iam.connectors.sap.grc.ws.submitreq.RoleData[] roles,
           java.lang.String sNCName,
           java.lang.String telephone,
           java.lang.Boolean unsecureLogon,
           java.lang.String userId,
           java.lang.String validFrom,
           java.lang.String validTo) {
           this.application = application;
           this.company = company;
           this.customField = customField;
           this.department = department;
           this.emailAddress = emailAddress;
           this.employeeType = employeeType;
           this.firstName = firstName;
           this.functionalArea = functionalArea;
           this.lastName = lastName;
           this.locale = locale;
           this.location = location;
           this.managerTelephone = managerTelephone;
           this.mgrEmailAddress = mgrEmailAddress;
           this.mgrFirstName = mgrFirstName;
           this.mgrId = mgrId;
           this.mgrLastName = mgrLastName;
           this.priority = priority;
           this.requestReason = requestReason;
           this.requestType = requestType;
           this.requestorEmailAddress = requestorEmailAddress;
           this.requestorFirstName = requestorFirstName;
           this.requestorId = requestorId;
           this.requestorLastName = requestorLastName;
           this.requestorTelephone = requestorTelephone;
           this.roles = roles;
           this.sNCName = sNCName;
           this.telephone = telephone;
           this.unsecureLogon = unsecureLogon;
           this.userId = userId;
           this.validFrom = validFrom;
           this.validTo = validTo;
    }


    /**
     * Gets the application value for this RequestDetailsData.
     * 
     * @return application
     */
    public java.lang.String getApplication() {
        return application;
    }


    /**
     * Sets the application value for this RequestDetailsData.
     * 
     * @param application
     */
    public void setApplication(java.lang.String application) {
        this.application = application;
    }


    /**
     * Gets the company value for this RequestDetailsData.
     * 
     * @return company
     */
    public java.lang.String getCompany() {
        return company;
    }


    /**
     * Sets the company value for this RequestDetailsData.
     * 
     * @param company
     */
    public void setCompany(java.lang.String company) {
        this.company = company;
    }


    /**
     * Gets the customField value for this RequestDetailsData.
     * 
     * @return customField
     */
    public oracle.iam.connectors.sap.grc.ws.submitreq.CustomFieldsDTO[] getCustomField() {
        return customField;
    }


    /**
     * Sets the customField value for this RequestDetailsData.
     * 
     * @param customField
     */
    public void setCustomField(oracle.iam.connectors.sap.grc.ws.submitreq.CustomFieldsDTO[] customField) {
        this.customField = customField;
    }


    /**
     * Gets the department value for this RequestDetailsData.
     * 
     * @return department
     */
    public java.lang.String getDepartment() {
        return department;
    }


    /**
     * Sets the department value for this RequestDetailsData.
     * 
     * @param department
     */
    public void setDepartment(java.lang.String department) {
        this.department = department;
    }


    /**
     * Gets the emailAddress value for this RequestDetailsData.
     * 
     * @return emailAddress
     */
    public java.lang.String getEmailAddress() {
        return emailAddress;
    }


    /**
     * Sets the emailAddress value for this RequestDetailsData.
     * 
     * @param emailAddress
     */
    public void setEmailAddress(java.lang.String emailAddress) {
        this.emailAddress = emailAddress;
    }


    /**
     * Gets the employeeType value for this RequestDetailsData.
     * 
     * @return employeeType
     */
    public java.lang.String getEmployeeType() {
        return employeeType;
    }


    /**
     * Sets the employeeType value for this RequestDetailsData.
     * 
     * @param employeeType
     */
    public void setEmployeeType(java.lang.String employeeType) {
        this.employeeType = employeeType;
    }


    /**
     * Gets the firstName value for this RequestDetailsData.
     * 
     * @return firstName
     */
    public java.lang.String getFirstName() {
        return firstName;
    }


    /**
     * Sets the firstName value for this RequestDetailsData.
     * 
     * @param firstName
     */
    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }


    /**
     * Gets the functionalArea value for this RequestDetailsData.
     * 
     * @return functionalArea
     */
    public java.lang.String getFunctionalArea() {
        return functionalArea;
    }


    /**
     * Sets the functionalArea value for this RequestDetailsData.
     * 
     * @param functionalArea
     */
    public void setFunctionalArea(java.lang.String functionalArea) {
        this.functionalArea = functionalArea;
    }


    /**
     * Gets the lastName value for this RequestDetailsData.
     * 
     * @return lastName
     */
    public java.lang.String getLastName() {
        return lastName;
    }


    /**
     * Sets the lastName value for this RequestDetailsData.
     * 
     * @param lastName
     */
    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }


    /**
     * Gets the locale value for this RequestDetailsData.
     * 
     * @return locale
     */
    public java.lang.String getLocale() {
        return locale;
    }


    /**
     * Sets the locale value for this RequestDetailsData.
     * 
     * @param locale
     */
    public void setLocale(java.lang.String locale) {
        this.locale = locale;
    }


    /**
     * Gets the location value for this RequestDetailsData.
     * 
     * @return location
     */
    public java.lang.String getLocation() {
        return location;
    }


    /**
     * Sets the location value for this RequestDetailsData.
     * 
     * @param location
     */
    public void setLocation(java.lang.String location) {
        this.location = location;
    }


    /**
     * Gets the managerTelephone value for this RequestDetailsData.
     * 
     * @return managerTelephone
     */
    public java.lang.String getManagerTelephone() {
        return managerTelephone;
    }


    /**
     * Sets the managerTelephone value for this RequestDetailsData.
     * 
     * @param managerTelephone
     */
    public void setManagerTelephone(java.lang.String managerTelephone) {
        this.managerTelephone = managerTelephone;
    }


    /**
     * Gets the mgrEmailAddress value for this RequestDetailsData.
     * 
     * @return mgrEmailAddress
     */
    public java.lang.String getMgrEmailAddress() {
        return mgrEmailAddress;
    }


    /**
     * Sets the mgrEmailAddress value for this RequestDetailsData.
     * 
     * @param mgrEmailAddress
     */
    public void setMgrEmailAddress(java.lang.String mgrEmailAddress) {
        this.mgrEmailAddress = mgrEmailAddress;
    }


    /**
     * Gets the mgrFirstName value for this RequestDetailsData.
     * 
     * @return mgrFirstName
     */
    public java.lang.String getMgrFirstName() {
        return mgrFirstName;
    }


    /**
     * Sets the mgrFirstName value for this RequestDetailsData.
     * 
     * @param mgrFirstName
     */
    public void setMgrFirstName(java.lang.String mgrFirstName) {
        this.mgrFirstName = mgrFirstName;
    }


    /**
     * Gets the mgrId value for this RequestDetailsData.
     * 
     * @return mgrId
     */
    public java.lang.String getMgrId() {
        return mgrId;
    }


    /**
     * Sets the mgrId value for this RequestDetailsData.
     * 
     * @param mgrId
     */
    public void setMgrId(java.lang.String mgrId) {
        this.mgrId = mgrId;
    }


    /**
     * Gets the mgrLastName value for this RequestDetailsData.
     * 
     * @return mgrLastName
     */
    public java.lang.String getMgrLastName() {
        return mgrLastName;
    }


    /**
     * Sets the mgrLastName value for this RequestDetailsData.
     * 
     * @param mgrLastName
     */
    public void setMgrLastName(java.lang.String mgrLastName) {
        this.mgrLastName = mgrLastName;
    }


    /**
     * Gets the priority value for this RequestDetailsData.
     * 
     * @return priority
     */
    public java.lang.String getPriority() {
        return priority;
    }


    /**
     * Sets the priority value for this RequestDetailsData.
     * 
     * @param priority
     */
    public void setPriority(java.lang.String priority) {
        this.priority = priority;
    }


    /**
     * Gets the requestReason value for this RequestDetailsData.
     * 
     * @return requestReason
     */
    public java.lang.String getRequestReason() {
        return requestReason;
    }


    /**
     * Sets the requestReason value for this RequestDetailsData.
     * 
     * @param requestReason
     */
    public void setRequestReason(java.lang.String requestReason) {
        this.requestReason = requestReason;
    }


    /**
     * Gets the requestType value for this RequestDetailsData.
     * 
     * @return requestType
     */
    public java.lang.String getRequestType() {
        return requestType;
    }


    /**
     * Sets the requestType value for this RequestDetailsData.
     * 
     * @param requestType
     */
    public void setRequestType(java.lang.String requestType) {
        this.requestType = requestType;
    }


    /**
     * Gets the requestorEmailAddress value for this RequestDetailsData.
     * 
     * @return requestorEmailAddress
     */
    public java.lang.String getRequestorEmailAddress() {
        return requestorEmailAddress;
    }


    /**
     * Sets the requestorEmailAddress value for this RequestDetailsData.
     * 
     * @param requestorEmailAddress
     */
    public void setRequestorEmailAddress(java.lang.String requestorEmailAddress) {
        this.requestorEmailAddress = requestorEmailAddress;
    }


    /**
     * Gets the requestorFirstName value for this RequestDetailsData.
     * 
     * @return requestorFirstName
     */
    public java.lang.String getRequestorFirstName() {
        return requestorFirstName;
    }


    /**
     * Sets the requestorFirstName value for this RequestDetailsData.
     * 
     * @param requestorFirstName
     */
    public void setRequestorFirstName(java.lang.String requestorFirstName) {
        this.requestorFirstName = requestorFirstName;
    }


    /**
     * Gets the requestorId value for this RequestDetailsData.
     * 
     * @return requestorId
     */
    public java.lang.String getRequestorId() {
        return requestorId;
    }


    /**
     * Sets the requestorId value for this RequestDetailsData.
     * 
     * @param requestorId
     */
    public void setRequestorId(java.lang.String requestorId) {
        this.requestorId = requestorId;
    }


    /**
     * Gets the requestorLastName value for this RequestDetailsData.
     * 
     * @return requestorLastName
     */
    public java.lang.String getRequestorLastName() {
        return requestorLastName;
    }


    /**
     * Sets the requestorLastName value for this RequestDetailsData.
     * 
     * @param requestorLastName
     */
    public void setRequestorLastName(java.lang.String requestorLastName) {
        this.requestorLastName = requestorLastName;
    }


    /**
     * Gets the requestorTelephone value for this RequestDetailsData.
     * 
     * @return requestorTelephone
     */
    public java.lang.String getRequestorTelephone() {
        return requestorTelephone;
    }


    /**
     * Sets the requestorTelephone value for this RequestDetailsData.
     * 
     * @param requestorTelephone
     */
    public void setRequestorTelephone(java.lang.String requestorTelephone) {
        this.requestorTelephone = requestorTelephone;
    }


    /**
     * Gets the roles value for this RequestDetailsData.
     * 
     * @return roles
     */
    public oracle.iam.connectors.sap.grc.ws.submitreq.RoleData[] getRoles() {
        return roles;
    }


    /**
     * Sets the roles value for this RequestDetailsData.
     * 
     * @param roles
     */
    public void setRoles(oracle.iam.connectors.sap.grc.ws.submitreq.RoleData[] roles) {
        this.roles = roles;
    }


    /**
     * Gets the sNCName value for this RequestDetailsData.
     * 
     * @return sNCName
     */
    public java.lang.String getSNCName() {
        return sNCName;
    }


    /**
     * Sets the sNCName value for this RequestDetailsData.
     * 
     * @param sNCName
     */
    public void setSNCName(java.lang.String sNCName) {
        this.sNCName = sNCName;
    }


    /**
     * Gets the telephone value for this RequestDetailsData.
     * 
     * @return telephone
     */
    public java.lang.String getTelephone() {
        return telephone;
    }


    /**
     * Sets the telephone value for this RequestDetailsData.
     * 
     * @param telephone
     */
    public void setTelephone(java.lang.String telephone) {
        this.telephone = telephone;
    }


    /**
     * Gets the unsecureLogon value for this RequestDetailsData.
     * 
     * @return unsecureLogon
     */
    public java.lang.Boolean getUnsecureLogon() {
        return unsecureLogon;
    }


    /**
     * Sets the unsecureLogon value for this RequestDetailsData.
     * 
     * @param unsecureLogon
     */
    public void setUnsecureLogon(java.lang.Boolean unsecureLogon) {
        this.unsecureLogon = unsecureLogon;
    }


    /**
     * Gets the userId value for this RequestDetailsData.
     * 
     * @return userId
     */
    public java.lang.String getUserId() {
        return userId;
    }


    /**
     * Sets the userId value for this RequestDetailsData.
     * 
     * @param userId
     */
    public void setUserId(java.lang.String userId) {
        this.userId = userId;
    }


    /**
     * Gets the validFrom value for this RequestDetailsData.
     * 
     * @return validFrom
     */
    public java.lang.String getValidFrom() {
        return validFrom;
    }


    /**
     * Sets the validFrom value for this RequestDetailsData.
     * 
     * @param validFrom
     */
    public void setValidFrom(java.lang.String validFrom) {
        this.validFrom = validFrom;
    }


    /**
     * Gets the validTo value for this RequestDetailsData.
     * 
     * @return validTo
     */
    public java.lang.String getValidTo() {
        return validTo;
    }


    /**
     * Sets the validTo value for this RequestDetailsData.
     * 
     * @param validTo
     */
    public void setValidTo(java.lang.String validTo) {
        this.validTo = validTo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RequestDetailsData)) return false;
        RequestDetailsData other = (RequestDetailsData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.application==null && other.getApplication()==null) || 
             (this.application!=null &&
              this.application.equals(other.getApplication()))) &&
            ((this.company==null && other.getCompany()==null) || 
             (this.company!=null &&
              this.company.equals(other.getCompany()))) &&
            ((this.customField==null && other.getCustomField()==null) || 
             (this.customField!=null &&
              java.util.Arrays.equals(this.customField, other.getCustomField()))) &&
            ((this.department==null && other.getDepartment()==null) || 
             (this.department!=null &&
              this.department.equals(other.getDepartment()))) &&
            ((this.emailAddress==null && other.getEmailAddress()==null) || 
             (this.emailAddress!=null &&
              this.emailAddress.equals(other.getEmailAddress()))) &&
            ((this.employeeType==null && other.getEmployeeType()==null) || 
             (this.employeeType!=null &&
              this.employeeType.equals(other.getEmployeeType()))) &&
            ((this.firstName==null && other.getFirstName()==null) || 
             (this.firstName!=null &&
              this.firstName.equals(other.getFirstName()))) &&
            ((this.functionalArea==null && other.getFunctionalArea()==null) || 
             (this.functionalArea!=null &&
              this.functionalArea.equals(other.getFunctionalArea()))) &&
            ((this.lastName==null && other.getLastName()==null) || 
             (this.lastName!=null &&
              this.lastName.equals(other.getLastName()))) &&
            ((this.locale==null && other.getLocale()==null) || 
             (this.locale!=null &&
              this.locale.equals(other.getLocale()))) &&
            ((this.location==null && other.getLocation()==null) || 
             (this.location!=null &&
              this.location.equals(other.getLocation()))) &&
            ((this.managerTelephone==null && other.getManagerTelephone()==null) || 
             (this.managerTelephone!=null &&
              this.managerTelephone.equals(other.getManagerTelephone()))) &&
            ((this.mgrEmailAddress==null && other.getMgrEmailAddress()==null) || 
             (this.mgrEmailAddress!=null &&
              this.mgrEmailAddress.equals(other.getMgrEmailAddress()))) &&
            ((this.mgrFirstName==null && other.getMgrFirstName()==null) || 
             (this.mgrFirstName!=null &&
              this.mgrFirstName.equals(other.getMgrFirstName()))) &&
            ((this.mgrId==null && other.getMgrId()==null) || 
             (this.mgrId!=null &&
              this.mgrId.equals(other.getMgrId()))) &&
            ((this.mgrLastName==null && other.getMgrLastName()==null) || 
             (this.mgrLastName!=null &&
              this.mgrLastName.equals(other.getMgrLastName()))) &&
            ((this.priority==null && other.getPriority()==null) || 
             (this.priority!=null &&
              this.priority.equals(other.getPriority()))) &&
            ((this.requestReason==null && other.getRequestReason()==null) || 
             (this.requestReason!=null &&
              this.requestReason.equals(other.getRequestReason()))) &&
            ((this.requestType==null && other.getRequestType()==null) || 
             (this.requestType!=null &&
              this.requestType.equals(other.getRequestType()))) &&
            ((this.requestorEmailAddress==null && other.getRequestorEmailAddress()==null) || 
             (this.requestorEmailAddress!=null &&
              this.requestorEmailAddress.equals(other.getRequestorEmailAddress()))) &&
            ((this.requestorFirstName==null && other.getRequestorFirstName()==null) || 
             (this.requestorFirstName!=null &&
              this.requestorFirstName.equals(other.getRequestorFirstName()))) &&
            ((this.requestorId==null && other.getRequestorId()==null) || 
             (this.requestorId!=null &&
              this.requestorId.equals(other.getRequestorId()))) &&
            ((this.requestorLastName==null && other.getRequestorLastName()==null) || 
             (this.requestorLastName!=null &&
              this.requestorLastName.equals(other.getRequestorLastName()))) &&
            ((this.requestorTelephone==null && other.getRequestorTelephone()==null) || 
             (this.requestorTelephone!=null &&
              this.requestorTelephone.equals(other.getRequestorTelephone()))) &&
            ((this.roles==null && other.getRoles()==null) || 
             (this.roles!=null &&
              java.util.Arrays.equals(this.roles, other.getRoles()))) &&
            ((this.sNCName==null && other.getSNCName()==null) || 
             (this.sNCName!=null &&
              this.sNCName.equals(other.getSNCName()))) &&
            ((this.telephone==null && other.getTelephone()==null) || 
             (this.telephone!=null &&
              this.telephone.equals(other.getTelephone()))) &&
            ((this.unsecureLogon==null && other.getUnsecureLogon()==null) || 
             (this.unsecureLogon!=null &&
              this.unsecureLogon.equals(other.getUnsecureLogon()))) &&
            ((this.userId==null && other.getUserId()==null) || 
             (this.userId!=null &&
              this.userId.equals(other.getUserId()))) &&
            ((this.validFrom==null && other.getValidFrom()==null) || 
             (this.validFrom!=null &&
              this.validFrom.equals(other.getValidFrom()))) &&
            ((this.validTo==null && other.getValidTo()==null) || 
             (this.validTo!=null &&
              this.validTo.equals(other.getValidTo())));
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
        if (getApplication() != null) {
            _hashCode += getApplication().hashCode();
        }
        if (getCompany() != null) {
            _hashCode += getCompany().hashCode();
        }
        if (getCustomField() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCustomField());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCustomField(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDepartment() != null) {
            _hashCode += getDepartment().hashCode();
        }
        if (getEmailAddress() != null) {
            _hashCode += getEmailAddress().hashCode();
        }
        if (getEmployeeType() != null) {
            _hashCode += getEmployeeType().hashCode();
        }
        if (getFirstName() != null) {
            _hashCode += getFirstName().hashCode();
        }
        if (getFunctionalArea() != null) {
            _hashCode += getFunctionalArea().hashCode();
        }
        if (getLastName() != null) {
            _hashCode += getLastName().hashCode();
        }
        if (getLocale() != null) {
            _hashCode += getLocale().hashCode();
        }
        if (getLocation() != null) {
            _hashCode += getLocation().hashCode();
        }
        if (getManagerTelephone() != null) {
            _hashCode += getManagerTelephone().hashCode();
        }
        if (getMgrEmailAddress() != null) {
            _hashCode += getMgrEmailAddress().hashCode();
        }
        if (getMgrFirstName() != null) {
            _hashCode += getMgrFirstName().hashCode();
        }
        if (getMgrId() != null) {
            _hashCode += getMgrId().hashCode();
        }
        if (getMgrLastName() != null) {
            _hashCode += getMgrLastName().hashCode();
        }
        if (getPriority() != null) {
            _hashCode += getPriority().hashCode();
        }
        if (getRequestReason() != null) {
            _hashCode += getRequestReason().hashCode();
        }
        if (getRequestType() != null) {
            _hashCode += getRequestType().hashCode();
        }
        if (getRequestorEmailAddress() != null) {
            _hashCode += getRequestorEmailAddress().hashCode();
        }
        if (getRequestorFirstName() != null) {
            _hashCode += getRequestorFirstName().hashCode();
        }
        if (getRequestorId() != null) {
            _hashCode += getRequestorId().hashCode();
        }
        if (getRequestorLastName() != null) {
            _hashCode += getRequestorLastName().hashCode();
        }
        if (getRequestorTelephone() != null) {
            _hashCode += getRequestorTelephone().hashCode();
        }
        if (getRoles() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRoles());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRoles(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSNCName() != null) {
            _hashCode += getSNCName().hashCode();
        }
        if (getTelephone() != null) {
            _hashCode += getTelephone().hashCode();
        }
        if (getUnsecureLogon() != null) {
            _hashCode += getUnsecureLogon().hashCode();
        }
        if (getUserId() != null) {
            _hashCode += getUserId().hashCode();
        }
        if (getValidFrom() != null) {
            _hashCode += getValidFrom().hashCode();
        }
        if (getValidTo() != null) {
            _hashCode += getValidTo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestDetailsData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "RequestDetailsData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("application");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "application"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("company");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "company"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customField");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "customField"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "CustomFieldsDTO"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "CustomFieldsDTO"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("department");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "department"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emailAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "emailAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("employeeType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "employeeType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "firstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("functionalArea");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "functionalArea"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "lastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("locale");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "locale"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("location");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "location"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("managerTelephone");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "managerTelephone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mgrEmailAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "mgrEmailAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mgrFirstName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "mgrFirstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mgrId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "mgrId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mgrLastName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "mgrLastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priority");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "priority"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestReason");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "requestReason"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "requestType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestorEmailAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "requestorEmailAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestorFirstName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "requestorFirstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestorId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "requestorId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestorLastName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "requestorLastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestorTelephone");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "requestorTelephone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roles");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "roles"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "RoleData"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "RoleData"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SNCName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "sNCName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("telephone");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "telephone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unsecureLogon");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "unsecureLogon"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "userId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("validFrom");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "validFrom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("validTo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:com.virsa.ae.ejbutil", "validTo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
