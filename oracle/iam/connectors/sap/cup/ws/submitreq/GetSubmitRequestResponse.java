 
package oracle.iam.connectors.sap.cup.ws.submitreq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Response" type="{urn:com.virsa.ae.ejbutil.submitrequest}RequestSubmissionResult"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "response"
})
@XmlRootElement(name = "getSubmitRequestResponse", namespace = "urn:SAPGRC_AC_IDM_SUBMITREQUESTVi")
public class GetSubmitRequestResponse {

    @XmlElement(name = "Response", namespace = "urn:SAPGRC_AC_IDM_SUBMITREQUESTVi", required = true, nillable = true)
    protected RequestSubmissionResult response;

    /**
     * Gets the value of the response property.
     * 
     * @return
     *     possible object is
     *     {@link RequestSubmissionResult }
     *     
     */
    public RequestSubmissionResult getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestSubmissionResult }
     *     
     */
    public void setResponse(RequestSubmissionResult value) {
        this.response = value;
    }

}
