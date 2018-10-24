
package oracle.iam.connectors.sap.cup.ws.audit;
 
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfAuditLogDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfAuditLogDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AuditLogDTO" type="{urn:com.virsa.ae.ejbutil.auditlogs}AuditLogDTO" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfAuditLogDTO", propOrder = {
    "auditLogDTO"
})
public class ArrayOfAuditLogDTO {

    @XmlElement(name = "AuditLogDTO", nillable = true)
    protected List<AuditLogDTO> auditLogDTO;

    /**
     * Gets the value of the auditLogDTO property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the auditLogDTO property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuditLogDTO().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AuditLogDTO }
     * 
     * 
     */
    public List<AuditLogDTO> getAuditLogDTO() {
        if (auditLogDTO == null) {
            auditLogDTO = new ArrayList<AuditLogDTO>();
        }
        return this.auditLogDTO;
    }

}
