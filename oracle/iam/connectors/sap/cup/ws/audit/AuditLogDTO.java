
package oracle.iam.connectors.sap.cup.ws.audit;
 
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for AuditLogDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuditLogDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="createDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="logDetails" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="priority" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestHst" type="{urn:com.virsa.ae.ejbutil.auditlogs}ArrayOfRequestHistoryDTO" minOccurs="0"/>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestedBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="submittedBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuditLogDTO", propOrder = {
    "createDate",
    "logDetails",
    "priority",
    "requestHst",
    "requestId",
    "requestedBy",
    "status",
    "submittedBy"
})
public class AuditLogDTO {

    @XmlElementRef(name = "createDate", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> createDate;
    @XmlElementRef(name = "logDetails", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> logDetails;
    @XmlElementRef(name = "priority", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> priority;
    @XmlElementRef(name = "requestHst", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<ArrayOfRequestHistoryDTO> requestHst;
    @XmlElementRef(name = "requestId", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> requestId;
    @XmlElementRef(name = "requestedBy", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> requestedBy;
    @XmlElementRef(name = "status", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> status;
    @XmlElementRef(name = "submittedBy", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> submittedBy;

    /**
     * Gets the value of the createDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getCreateDate() {
        return createDate;
    }

    /**
     * Sets the value of the createDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setCreateDate(JAXBElement<XMLGregorianCalendar> value) {
        this.createDate = ((JAXBElement<XMLGregorianCalendar> ) value);
    }

    /**
     * Gets the value of the logDetails property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLogDetails() {
        return logDetails;
    }

    /**
     * Sets the value of the logDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLogDetails(JAXBElement<String> value) {
        this.logDetails = ((JAXBElement<String> ) value);
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
     * Gets the value of the requestHst property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRequestHistoryDTO }{@code >}
     *     
     */
    public JAXBElement<ArrayOfRequestHistoryDTO> getRequestHst() {
        return requestHst;
    }

    /**
     * Sets the value of the requestHst property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRequestHistoryDTO }{@code >}
     *     
     */
    public void setRequestHst(JAXBElement<ArrayOfRequestHistoryDTO> value) {
        this.requestHst = ((JAXBElement<ArrayOfRequestHistoryDTO> ) value);
    }

    /**
     * Gets the value of the requestId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRequestId(JAXBElement<String> value) {
        this.requestId = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the requestedBy property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRequestedBy() {
        return requestedBy;
    }

    /**
     * Sets the value of the requestedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRequestedBy(JAXBElement<String> value) {
        this.requestedBy = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStatus(JAXBElement<String> value) {
        this.status = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the submittedBy property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSubmittedBy() {
        return submittedBy;
    }

    /**
     * Sets the value of the submittedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSubmittedBy(JAXBElement<String> value) {
        this.submittedBy = ((JAXBElement<String> ) value);
    }

}
