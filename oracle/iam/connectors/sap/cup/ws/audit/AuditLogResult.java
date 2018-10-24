
package oracle.iam.connectors.sap.cup.ws.audit;
 
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AuditLogResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuditLogResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="auditLogDTO" type="{urn:com.virsa.ae.ejbutil.auditlogs}ArrayOfAuditLogDTO1" minOccurs="0"/>
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
@XmlType(name = "AuditLogResult", propOrder = {
    "auditLogDTO",
    "status"
})
public class AuditLogResult {

    @XmlElementRef(name = "auditLogDTO", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<ArrayOfAuditLogDTO1> auditLogDTO;
    @XmlElementRef(name = "status", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<ServiceStatusDTO> status;

    /**
     * Gets the value of the auditLogDTO property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfAuditLogDTO1 }{@code >}
     *     
     */
    public JAXBElement<ArrayOfAuditLogDTO1> getAuditLogDTO() {
        return auditLogDTO;
    }

    /**
     * Sets the value of the auditLogDTO property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfAuditLogDTO1 }{@code >}
     *     
     */
    public void setAuditLogDTO(JAXBElement<ArrayOfAuditLogDTO1> value) {
        this.auditLogDTO = ((JAXBElement<ArrayOfAuditLogDTO1> ) value);
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
