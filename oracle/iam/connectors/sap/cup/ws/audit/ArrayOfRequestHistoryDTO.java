
package oracle.iam.connectors.sap.cup.ws.audit;
 
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfRequestHistoryDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfRequestHistoryDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestHistoryDTO" type="{urn:com.virsa.ae.ejbutil.auditlogs}RequestHistoryDTO" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfRequestHistoryDTO", propOrder = {
    "requestHistoryDTO"
})
public class ArrayOfRequestHistoryDTO {

    @XmlElement(name = "RequestHistoryDTO", nillable = true)
    protected List<RequestHistoryDTO> requestHistoryDTO;

    /**
     * Gets the value of the requestHistoryDTO property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requestHistoryDTO property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequestHistoryDTO().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RequestHistoryDTO }
     * 
     * 
     */
    public List<RequestHistoryDTO> getRequestHistoryDTO() {
        if (requestHistoryDTO == null) {
            requestHistoryDTO = new ArrayList<RequestHistoryDTO>();
        }
        return this.requestHistoryDTO;
    }

}
