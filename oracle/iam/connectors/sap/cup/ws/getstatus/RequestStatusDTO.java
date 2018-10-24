 
package oracle.iam.connectors.sap.cup.ws.getstatus;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RequestStatusDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestStatusDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dueDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="msgCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="msgDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="msgType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestStatusDTO", namespace = "urn:com.virsa.ae.ejbutil.requeststatus", propOrder = {
    "dueDate",
    "msgCode",
    "msgDesc",
    "msgType",
    "requestNumber",
    "stage",
    "status",
    "userName"
})
public class RequestStatusDTO {

    @XmlElementRef(name = "dueDate", namespace = "urn:com.virsa.ae.ejbutil.requeststatus", type = JAXBElement.class)
    protected JAXBElement<String> dueDate;
    @XmlElementRef(name = "msgCode", namespace = "urn:com.virsa.ae.ejbutil.requeststatus", type = JAXBElement.class)
    protected JAXBElement<String> msgCode;
    @XmlElementRef(name = "msgDesc", namespace = "urn:com.virsa.ae.ejbutil.requeststatus", type = JAXBElement.class)
    protected JAXBElement<String> msgDesc;
    @XmlElementRef(name = "msgType", namespace = "urn:com.virsa.ae.ejbutil.requeststatus", type = JAXBElement.class)
    protected JAXBElement<String> msgType;
    @XmlElementRef(name = "requestNumber", namespace = "urn:com.virsa.ae.ejbutil.requeststatus", type = JAXBElement.class)
    protected JAXBElement<String> requestNumber;
    @XmlElementRef(name = "stage", namespace = "urn:com.virsa.ae.ejbutil.requeststatus", type = JAXBElement.class)
    protected JAXBElement<String> stage;
    @XmlElementRef(name = "status", namespace = "urn:com.virsa.ae.ejbutil.requeststatus", type = JAXBElement.class)
    protected JAXBElement<String> status;
    @XmlElementRef(name = "userName", namespace = "urn:com.virsa.ae.ejbutil.requeststatus", type = JAXBElement.class)
    protected JAXBElement<String> userName;

    /**
     * Gets the value of the dueDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDueDate() {
        return dueDate;
    }

    /**
     * Sets the value of the dueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDueDate(JAXBElement<String> value) {
        this.dueDate = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the msgCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMsgCode() {
        return msgCode;
    }

    /**
     * Sets the value of the msgCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMsgCode(JAXBElement<String> value) {
        this.msgCode = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the msgDesc property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMsgDesc() {
        return msgDesc;
    }

    /**
     * Sets the value of the msgDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMsgDesc(JAXBElement<String> value) {
        this.msgDesc = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the msgType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMsgType() {
        return msgType;
    }

    /**
     * Sets the value of the msgType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMsgType(JAXBElement<String> value) {
        this.msgType = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the requestNumber property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRequestNumber() {
        return requestNumber;
    }

    /**
     * Sets the value of the requestNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRequestNumber(JAXBElement<String> value) {
        this.requestNumber = ((JAXBElement<String> ) value);
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
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUserName(JAXBElement<String> value) {
        this.userName = ((JAXBElement<String> ) value);
    }

}
