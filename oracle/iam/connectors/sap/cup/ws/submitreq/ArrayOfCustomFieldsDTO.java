 
package oracle.iam.connectors.sap.cup.ws.submitreq;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfCustomFieldsDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCustomFieldsDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CustomFieldsDTO" type="{urn:com.virsa.ae.ejbutil}CustomFieldsDTO" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCustomFieldsDTO", propOrder = {
    "customFieldsDTO"
})
public class ArrayOfCustomFieldsDTO {

    @XmlElement(name = "CustomFieldsDTO", nillable = true)
    protected List<CustomFieldsDTO> customFieldsDTO;

    /**
     * Gets the value of the customFieldsDTO property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the customFieldsDTO property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCustomFieldsDTO().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomFieldsDTO }
     * 
     * 
     */
    public List<CustomFieldsDTO> getCustomFieldsDTO() {
        if (customFieldsDTO == null) {
            customFieldsDTO = new ArrayList<CustomFieldsDTO>();
        }
        return this.customFieldsDTO;
    }

}
