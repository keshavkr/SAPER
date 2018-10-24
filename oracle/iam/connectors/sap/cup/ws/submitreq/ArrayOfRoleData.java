 
package oracle.iam.connectors.sap.cup.ws.submitreq;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfRoleData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfRoleData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RoleData" type="{urn:com.virsa.ae.ejbutil}RoleData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfRoleData", propOrder = {
    "roleData"
})
public class ArrayOfRoleData {

    @XmlElement(name = "RoleData", nillable = true)
    protected List<RoleData> roleData;

    /**
     * Gets the value of the roleData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the roleData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoleData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RoleData }
     * 
     * 
     */
    public List<RoleData> getRoleData() {
        if (roleData == null) {
            roleData = new ArrayList<RoleData>();
        }
        return this.roleData;
    }

}
