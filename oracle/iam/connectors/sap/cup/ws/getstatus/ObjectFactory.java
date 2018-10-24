 
package oracle.iam.connectors.sap.cup.ws.getstatus;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the examples.webservices.simple_client.requeststatus package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetStatusComVirsaAeCoreBOException_QNAME = new QName("urn:SAPGRC_AC_IDM_REQUESTSTATUSWsd/SAPGRC_AC_IDM_REQUESTSTATUSVi", "getStatus_com.virsa.ae.core.BOException");
    private final static QName _AEExceptionMessageCode_QNAME = new QName("urn:com.virsa.ae.core", "messageCode");
    private final static QName _RequestStatusDTOMsgCode_QNAME = new QName("urn:com.virsa.ae.ejbutil.requeststatus", "msgCode");
    private final static QName _RequestStatusDTOStatus_QNAME = new QName("urn:com.virsa.ae.ejbutil.requeststatus", "status");
    private final static QName _RequestStatusDTOMsgDesc_QNAME = new QName("urn:com.virsa.ae.ejbutil.requeststatus", "msgDesc");
    private final static QName _RequestStatusDTOStage_QNAME = new QName("urn:com.virsa.ae.ejbutil.requeststatus", "stage");
    private final static QName _RequestStatusDTODueDate_QNAME = new QName("urn:com.virsa.ae.ejbutil.requeststatus", "dueDate");
    private final static QName _RequestStatusDTORequestNumber_QNAME = new QName("urn:com.virsa.ae.ejbutil.requeststatus", "requestNumber");
    private final static QName _RequestStatusDTOMsgType_QNAME = new QName("urn:com.virsa.ae.ejbutil.requeststatus", "msgType");
    private final static QName _RequestStatusDTOUserName_QNAME = new QName("urn:com.virsa.ae.ejbutil.requeststatus", "userName");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: examples.webservices.simple_client.requeststatus
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetStatus }
     * 
     */
    public GetStatus createGetStatus() {
        return new GetStatus();
    }

    /**
     * Create an instance of {@link BOException }
     * 
     */
    public BOException createBOException() {
        return new BOException();
    }

    /**
     * Create an instance of {@link GetStatusResponse }
     * 
     */
    public GetStatusResponse createGetStatusResponse() {
        return new GetStatusResponse();
    }

    /**
     * Create an instance of {@link AEException }
     * 
     */
    public AEException createAEException() {
        return new AEException();
    }

    /**
     * Create an instance of {@link RequestStatusDTO }
     * 
     */
    public RequestStatusDTO createRequestStatusDTO() {
        return new RequestStatusDTO();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BOException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:SAPGRC_AC_IDM_REQUESTSTATUSWsd/SAPGRC_AC_IDM_REQUESTSTATUSVi", name = "getStatus_com.virsa.ae.core.BOException")
    public JAXBElement<BOException> createGetStatusComVirsaAeCoreBOException(BOException value) {
        return new JAXBElement<BOException>(_GetStatusComVirsaAeCoreBOException_QNAME, BOException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.core", name = "messageCode", scope = AEException.class)
    public JAXBElement<String> createAEExceptionMessageCode(String value) {
        return new JAXBElement<String>(_AEExceptionMessageCode_QNAME, String.class, AEException.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.requeststatus", name = "msgCode", scope = RequestStatusDTO.class)
    public JAXBElement<String> createRequestStatusDTOMsgCode(String value) {
        return new JAXBElement<String>(_RequestStatusDTOMsgCode_QNAME, String.class, RequestStatusDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.requeststatus", name = "status", scope = RequestStatusDTO.class)
    public JAXBElement<String> createRequestStatusDTOStatus(String value) {
        return new JAXBElement<String>(_RequestStatusDTOStatus_QNAME, String.class, RequestStatusDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.requeststatus", name = "msgDesc", scope = RequestStatusDTO.class)
    public JAXBElement<String> createRequestStatusDTOMsgDesc(String value) {
        return new JAXBElement<String>(_RequestStatusDTOMsgDesc_QNAME, String.class, RequestStatusDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.requeststatus", name = "stage", scope = RequestStatusDTO.class)
    public JAXBElement<String> createRequestStatusDTOStage(String value) {
        return new JAXBElement<String>(_RequestStatusDTOStage_QNAME, String.class, RequestStatusDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.requeststatus", name = "dueDate", scope = RequestStatusDTO.class)
    public JAXBElement<String> createRequestStatusDTODueDate(String value) {
        return new JAXBElement<String>(_RequestStatusDTODueDate_QNAME, String.class, RequestStatusDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.requeststatus", name = "requestNumber", scope = RequestStatusDTO.class)
    public JAXBElement<String> createRequestStatusDTORequestNumber(String value) {
        return new JAXBElement<String>(_RequestStatusDTORequestNumber_QNAME, String.class, RequestStatusDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.requeststatus", name = "msgType", scope = RequestStatusDTO.class)
    public JAXBElement<String> createRequestStatusDTOMsgType(String value) {
        return new JAXBElement<String>(_RequestStatusDTOMsgType_QNAME, String.class, RequestStatusDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.requeststatus", name = "userName", scope = RequestStatusDTO.class)
    public JAXBElement<String> createRequestStatusDTOUserName(String value) {
        return new JAXBElement<String>(_RequestStatusDTOUserName_QNAME, String.class, RequestStatusDTO.class, value);
    }

}
