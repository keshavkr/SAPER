 
package oracle.iam.connectors.sap.cup.ws.audit;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceStatusDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceStatusDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="msgCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="msgDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="msgType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceStatusDTO", namespace = "urn:com.virsa.ae.ejbutil", propOrder = {
    "msgCode",
    "msgDesc",
    "msgType"
})
public class ServiceStatusDTO {

    @XmlElementRef(name = "msgCode", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> msgCode;
    @XmlElementRef(name = "msgDesc", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> msgDesc;
    @XmlElementRef(name = "msgType", namespace = "urn:com.virsa.ae.ejbutil", type = JAXBElement.class)
    protected JAXBElement<String> msgType;

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

}
