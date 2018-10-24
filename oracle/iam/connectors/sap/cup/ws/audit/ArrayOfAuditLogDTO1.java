
package oracle.iam.connectors.sap.cup.ws.audit;
 
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfAuditLogDTO1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfAuditLogDTO1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="auditLogDTO" type="{urn:com.virsa.ae.ejbutil.auditlogs}ArrayOfAuditLogDTO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfAuditLogDTO1", propOrder = {
    "auditLogDTO"
})
public class ArrayOfAuditLogDTO1 {

    @XmlElementRef(name = "auditLogDTO", namespace = "urn:com.virsa.ae.ejbutil.auditlogs", type = JAXBElement.class)
    protected JAXBElement<ArrayOfAuditLogDTO> auditLogDTO;

    /**
     * Gets the value of the auditLogDTO property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfAuditLogDTO }{@code >}
     *     
     */
    public JAXBElement<ArrayOfAuditLogDTO> getAuditLogDTO() {
        return auditLogDTO;
    }

    /**
     * Sets the value of the auditLogDTO property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfAuditLogDTO }{@code >}
     *     
     */
    public void setAuditLogDTO(JAXBElement<ArrayOfAuditLogDTO> value) {
        this.auditLogDTO = ((JAXBElement<ArrayOfAuditLogDTO> ) value);
    }

}
