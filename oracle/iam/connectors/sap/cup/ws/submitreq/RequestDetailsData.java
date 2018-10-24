 
package oracle.iam.connectors.sap.cup.ws.submitreq;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RequestDetailsData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestDetailsData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="application" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="company" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customField" type="{urn:com.virsa.ae.ejbutil}ArrayOfCustomFieldsDTO" minOccurs="0"/>
 *         &lt;element name="department" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="employeeType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="functionalArea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="locale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="managerTelephone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mgrEmailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mgrFirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mgrId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mgrLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="priority" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestorEmailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestorFirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestorId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestorLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestorTelephone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="roles" type="{urn:com.virsa.ae.ejbutil}ArrayOfRoleData" minOccurs="0"/>
 *         &lt;element name="sNCName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="telephone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="unsecureLogon" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="userId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="validFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="validTo" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestDetailsData", propOrder = {
    "application",
    "company",
    "customField",
    "department",
    "emailAddress",
    "employeeType",
    "firstName",
    "functionalArea",
    "lastName",
    "locale",
    "location",
    "managerTelephone",
    "mgrEmailAddress",
    "mgrFirstName",
    "mgrId",
    "mgrLastName",
    "priority",
    "requestReason",
    "requestType",
    "requestorEmailAddress",
    "requestorFirstName",
    "requestorId",
    "requestorLastName",
    "requestorTelephone",
    "roles",
    "sncName",
    "telephone",
    "unsecureLogon",
    "userId",
    "validFrom",
    "validTo"
})
public class RequestDetailsData {

    @XmlElementRef(name = "application", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> application;
    @XmlElementRef(name = "company", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> company;
    @XmlElementRef(name = "customField", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<ArrayOfCustomFieldsDTO> customField;
    @XmlElementRef(name = "department", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> department;
    @XmlElementRef(name = "emailAddress", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> emailAddress;
    @XmlElementRef(name = "employeeType", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> employeeType;
    @XmlElementRef(name = "firstName", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> firstName;
    @XmlElementRef(name = "functionalArea", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> functionalArea;
    @XmlElementRef(name = "lastName", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> lastName;
    @XmlElementRef(name = "locale", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> locale;
    @XmlElementRef(name = "location", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> location;
    @XmlElementRef(name = "managerTelephone", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> managerTelephone;
    @XmlElementRef(name = "mgrEmailAddress", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> mgrEmailAddress;
    @XmlElementRef(name = "mgrFirstName", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> mgrFirstName;
    @XmlElementRef(name = "mgrId", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> mgrId;
    @XmlElementRef(name = "mgrLastName", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> mgrLastName;
    @XmlElementRef(name = "priority", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> priority;
    @XmlElementRef(name = "requestReason", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> requestReason;
    @XmlElementRef(name = "requestType", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> requestType;
    @XmlElementRef(name = "requestorEmailAddress", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> requestorEmailAddress;
    @XmlElementRef(name = "requestorFirstName", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> requestorFirstName;
    @XmlElementRef(name = "requestorId", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> requestorId;
    @XmlElementRef(name = "requestorLastName", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> requestorLastName;
    @XmlElementRef(name = "requestorTelephone", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> requestorTelephone;
    @XmlElementRef(name = "roles", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<ArrayOfRoleData> roles;
    @XmlElementRef(name = "sNCName", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> sncName;
    @XmlElementRef(name = "telephone", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> telephone;
    @XmlElementRef(name = "unsecureLogon", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<Boolean> unsecureLogon;
    @XmlElementRef(name = "userId", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> userId;
    @XmlElementRef(name = "validFrom", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> validFrom;
    @XmlElementRef(name = "validTo", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> validTo;

    /**
     * Gets the value of the application property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getApplication() {
        return application;
    }

    /**
     * Sets the value of the application property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setApplication(JAXBElement<String> value) {
        this.application = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the company property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCompany() {
        return company;
    }

    /**
     * Sets the value of the company property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCompany(JAXBElement<String> value) {
        this.company = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the customField property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfCustomFieldsDTO }{@code >}
     *     
     */
    public JAXBElement<ArrayOfCustomFieldsDTO> getCustomField() {
        return customField;
    }

    /**
     * Sets the value of the customField property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfCustomFieldsDTO }{@code >}
     *     
     */
    public void setCustomField(JAXBElement<ArrayOfCustomFieldsDTO> value) {
        this.customField = ((JAXBElement<ArrayOfCustomFieldsDTO> ) value);
    }

    /**
     * Gets the value of the department property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDepartment() {
        return department;
    }

    /**
     * Sets the value of the department property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDepartment(JAXBElement<String> value) {
        this.department = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the emailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEmailAddress(JAXBElement<String> value) {
        this.emailAddress = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the employeeType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEmployeeType() {
        return employeeType;
    }

    /**
     * Sets the value of the employeeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEmployeeType(JAXBElement<String> value) {
        this.employeeType = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFirstName(JAXBElement<String> value) {
        this.firstName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the functionalArea property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFunctionalArea() {
        return functionalArea;
    }

    /**
     * Sets the value of the functionalArea property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFunctionalArea(JAXBElement<String> value) {
        this.functionalArea = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLastName(JAXBElement<String> value) {
        this.lastName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the locale property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLocale() {
        return locale;
    }

    /**
     * Sets the value of the locale property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLocale(JAXBElement<String> value) {
        this.locale = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLocation(JAXBElement<String> value) {
        this.location = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the managerTelephone property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getManagerTelephone() {
        return managerTelephone;
    }

    /**
     * Sets the value of the managerTelephone property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setManagerTelephone(JAXBElement<String> value) {
        this.managerTelephone = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the mgrEmailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMgrEmailAddress() {
        return mgrEmailAddress;
    }

    /**
     * Sets the value of the mgrEmailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMgrEmailAddress(JAXBElement<String> value) {
        this.mgrEmailAddress = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the mgrFirstName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMgrFirstName() {
        return mgrFirstName;
    }

    /**
     * Sets the value of the mgrFirstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMgrFirstName(JAXBElement<String> value) {
        this.mgrFirstName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the mgrId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMgrId() {
        return mgrId;
    }

    /**
     * Sets the value of the mgrId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMgrId(JAXBElement<String> value) {
        this.mgrId = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the mgrLastName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMgrLastName() {
        return mgrLastName;
    }

    /**
     * Sets the value of the mgrLastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMgrLastName(JAXBElement<String> value) {
        this.mgrLastName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPriority(JAXBElement<String> value) {
        this.priority = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the requestReason property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRequestReason() {
        return requestReason;
    }

    /**
     * Sets the value of the requestReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRequestReason(JAXBElement<String> value) {
        this.requestReason = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the requestType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRequestType() {
        return requestType;
    }

    /**
     * Sets the value of the requestType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRequestType(JAXBElement<String> value) {
        this.requestType = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the requestorEmailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRequestorEmailAddress() {
        return requestorEmailAddress;
    }

    /**
     * Sets the value of the requestorEmailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRequestorEmailAddress(JAXBElement<String> value) {
        this.requestorEmailAddress = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the requestorFirstName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRequestorFirstName() {
        return requestorFirstName;
    }

    /**
     * Sets the value of the requestorFirstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRequestorFirstName(JAXBElement<String> value) {
        this.requestorFirstName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the requestorId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRequestorId() {
        return requestorId;
    }

    /**
     * Sets the value of the requestorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRequestorId(JAXBElement<String> value) {
        this.requestorId = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the requestorLastName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRequestorLastName() {
        return requestorLastName;
    }

    /**
     * Sets the value of the requestorLastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRequestorLastName(JAXBElement<String> value) {
        this.requestorLastName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the requestorTelephone property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRequestorTelephone() {
        return requestorTelephone;
    }

    /**
     * Sets the value of the requestorTelephone property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRequestorTelephone(JAXBElement<String> value) {
        this.requestorTelephone = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the roles property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRoleData }{@code >}
     *     
     */
    public JAXBElement<ArrayOfRoleData> getRoles() {
        return roles;
    }

    /**
     * Sets the value of the roles property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRoleData }{@code >}
     *     
     */
    public void setRoles(JAXBElement<ArrayOfRoleData> value) {
        this.roles = ((JAXBElement<ArrayOfRoleData> ) value);
    }

    /**
     * Gets the value of the sncName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSNCName() {
        return sncName;
    }

    /**
     * Sets the value of the sncName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSNCName(JAXBElement<String> value) {
        this.sncName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the telephone property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTelephone() {
        return telephone;
    }

    /**
     * Sets the value of the telephone property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTelephone(JAXBElement<String> value) {
        this.telephone = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the unsecureLogon property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getUnsecureLogon() {
        return unsecureLogon;
    }

    /**
     * Sets the value of the unsecureLogon property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setUnsecureLogon(JAXBElement<Boolean> value) {
        this.unsecureLogon = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the userId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUserId(JAXBElement<String> value) {
        this.userId = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the validFrom property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getValidFrom() {
        return validFrom;
    }

    /**
     * Sets the value of the validFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setValidFrom(JAXBElement<XMLGregorianCalendar> value) {
        this.validFrom = ((JAXBElement<XMLGregorianCalendar> ) value);
    }

    /**
     * Gets the value of the validTo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getValidTo() {
        return validTo;
    }

    /**
     * Sets the value of the validTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setValidTo(JAXBElement<XMLGregorianCalendar> value) {
        this.validTo = ((JAXBElement<XMLGregorianCalendar> ) value);
    }

}
