 
package oracle.iam.connectors.sap.cup.ws.audit;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RequestHistoryDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestHistoryDTO">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:com.virsa.ae.core}BaseDTO">
 *       &lt;sequence>
 *         &lt;element name="actionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="actionValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="childDTOs" type="{urn:com.virsa.ae.ejbutil.auditlogs}ArrayOfRequestHistoryDTO" minOccurs="0"/>
 *         &lt;element name="dependentId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="displayString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="path" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reqNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestHistoryDTO", propOrder = {
    "actionDate",
    "actionValue",
    "childDTOs",
    "dependentId",
    "description",
    "displayString",
    "id",
    "path",
    "reqNo",
    "stage",
    "userId"
})
public class RequestHistoryDTO
    extends BaseDTO
{

    @XmlElementRef(name = "actionDate", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> actionDate;
    @XmlElementRef(name = "actionValue", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> actionValue;
    @XmlElementRef(name = "childDTOs", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<ArrayOfRequestHistoryDTO> childDTOs;
    @XmlElementRef(name = "dependentId", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> dependentId;
    @XmlElementRef(name = "description", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> description;
    @XmlElementRef(name = "displayString", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> displayString;
    @XmlElementRef(name = "id", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> id;
    @XmlElementRef(name = "path", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> path;
    @XmlElementRef(name = "reqNo", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> reqNo;
    @XmlElementRef(name = "stage", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> stage;
    @XmlElementRef(name = "userId", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<String> userId;

    /**
     * Gets the value of the actionDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getActionDate() {
        return actionDate;
    }

    /**
     * Sets the value of the actionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setActionDate(JAXBElement<XMLGregorianCalendar> value) {
        this.actionDate = ((JAXBElement<XMLGregorianCalendar> ) value);
    }

    /**
     * Gets the value of the actionValue property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getActionValue() {
        return actionValue;
    }

    /**
     * Sets the value of the actionValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setActionValue(JAXBElement<String> value) {
        this.actionValue = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the childDTOs property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRequestHistoryDTO }{@code >}
     *     
     */
    public JAXBElement<ArrayOfRequestHistoryDTO> getChildDTOs() {
        return childDTOs;
    }

    /**
     * Sets the value of the childDTOs property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRequestHistoryDTO }{@code >}
     *     
     */
    public void setChildDTOs(JAXBElement<ArrayOfRequestHistoryDTO> value) {
        this.childDTOs = ((JAXBElement<ArrayOfRequestHistoryDTO> ) value);
    }

    /**
     * Gets the value of the dependentId property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDependentId() {
        return dependentId;
    }

    /**
     * Sets the value of the dependentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDependentId(JAXBElement<String> value) {
        this.dependentId = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDescription(JAXBElement<String> value) {
        this.description = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the displayString property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDisplayString() {
        return displayString;
    }

    /**
     * Sets the value of the displayString property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDisplayString(JAXBElement<String> value) {
        this.displayString = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setId(JAXBElement<String> value) {
        this.id = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPath(JAXBElement<String> value) {
        this.path = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the reqNo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getReqNo() {
        return reqNo;
    }

    /**
     * Sets the value of the reqNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setReqNo(JAXBElement<String> value) {
        this.reqNo = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the stage property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getStage() {
        return stage;
    }

    /**
     * Sets the value of the stage property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setStage(JAXBElement<String> value) {
        this.stage = ((JAXBElement<String> ) value);
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

}
