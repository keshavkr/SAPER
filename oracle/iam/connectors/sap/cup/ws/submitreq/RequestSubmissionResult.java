 
package oracle.iam.connectors.sap.cup.ws.submitreq;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RequestSubmissionResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestSubmissionResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{urn:com.virsa.ae.ejbutil}ServiceStatusDTO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestSubmissionResult", namespace = "urn:com.virsa.ae.ejbutil.submitrequest", propOrder = {
    "requestNo",
    "status"
})
public class RequestSubmissionResult {

    @XmlElementRef(name = "requestNo", namespace = "urn:com.virsa.ae.ejbutil.submitrequest", type = JAXBElement.class)
    protected JAXBElement<String> requestNo;
    @XmlElementRef(name = "status", namespace = "urn:com.virsa.ae.ejbutil.submitrequest", type = JAXBElement.class)
    protected JAXBElement<ServiceStatusDTO> status;

    /**
     * Gets the value of the requestNo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRequestNo() {
        return requestNo;
    }

    /**
     * Sets the value of the requestNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRequestNo(JAXBElement<String> value) {
        this.requestNo = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ServiceStatusDTO }{@code >}
     *     
     */
    public JAXBElement<ServiceStatusDTO> getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ServiceStatusDTO }{@code >}
     *     
     */
    public void setStatus(JAXBElement<ServiceStatusDTO> value) {
        this.status = ((JAXBElement<ServiceStatusDTO> ) value);
    }

}
