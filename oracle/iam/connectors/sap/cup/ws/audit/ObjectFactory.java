 
package oracle.iam.connectors.sap.cup.ws.audit;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the examples.webservices.simple_client.audittrial package. 
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

    private final static QName _AEExceptionMessageCode_QNAME = new QName("urn:com.virsa.ae.core", "messageCode");
    private final static QName _GetAuditLogsComVirsaAeCoreBOException_QNAME = new QName("urn:SAPGRC_AC_IDM_AUDITTRAILWsd/SAPGRC_AC_IDM_AUDITTRAILVi", "getAuditLogs_com.virsa.ae.core.BOException");
    private final static QName _RequestHistoryDTOId_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "id");
    private final static QName _RequestHistoryDTODisplayString_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "displayString");
    private final static QName _RequestHistoryDTOActionDate_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "actionDate");
    private final static QName _RequestHistoryDTOUserId_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "userId");
    private final static QName _RequestHistoryDTOChildDTOs_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "childDTOs");
    private final static QName _RequestHistoryDTODescription_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "description");
    private final static QName _RequestHistoryDTODependentId_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "dependentId");
    private final static QName _RequestHistoryDTOPath_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "path");
    private final static QName _RequestHistoryDTOActionValue_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "actionValue");
    private final static QName _RequestHistoryDTOStage_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "stage");
    private final static QName _RequestHistoryDTOReqNo_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "reqNo");
    private final static QName _ServiceStatusDTOMsgType_QNAME = new QName("urn:com.virsa.ae.ejbutil", "msgType");
    private final static QName _ServiceStatusDTOMsgDesc_QNAME = new QName("urn:com.virsa.ae.ejbutil", "msgDesc");
    private final static QName _ServiceStatusDTOMsgCode_QNAME = new QName("urn:com.virsa.ae.ejbutil", "msgCode");
    private final static QName _AuditLogResultStatus_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "status");
    private final static QName _AuditLogResultAuditLogDTO_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "auditLogDTO");
    private final static QName _AuditLogDTORequestId_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "requestId");
    private final static QName _AuditLogDTOPriority_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "priority");
    private final static QName _AuditLogDTORequestHst_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "requestHst");
    private final static QName _AuditLogDTOLogDetails_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "logDetails");
    private final static QName _AuditLogDTOSubmittedBy_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "submittedBy");
    private final static QName _AuditLogDTOCreateDate_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "createDate");
    private final static QName _AuditLogDTORequestedBy_QNAME = new QName("urn:com.virsa.ae.ejbutil.auditlogs", "requestedBy");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: examples.webservices.simple_client.audittrial
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AEException }
     * 
     */
    public AEException createAEException() {
        return new AEException();
    }

    /**
     * Create an instance of {@link ArrayOfRequestHistoryDTO }
     * 
     */
    public ArrayOfRequestHistoryDTO createArrayOfRequestHistoryDTO() {
        return new ArrayOfRequestHistoryDTO();
    }

    /**
     * Create an instance of {@link RequestHistoryDTO }
     * 
     */
    public RequestHistoryDTO createRequestHistoryDTO() {
        return new RequestHistoryDTO();
    }

    /**
     * Create an instance of {@link BaseDTO }
     * 
     */
    public BaseDTO createBaseDTO() {
        return new BaseDTO();
    }

    /**
     * Create an instance of {@link ServiceStatusDTO }
     * 
     */
    public ServiceStatusDTO createServiceStatusDTO() {
        return new ServiceStatusDTO();
    }

    /**
     * Create an instance of {@link GetAuditLogsResponse }
     * 
     */
    public GetAuditLogsResponse createGetAuditLogsResponse() {
        return new GetAuditLogsResponse();
    }

    /**
     * Create an instance of {@link GetAuditLogs }
     * 
     */
    public GetAuditLogs createGetAuditLogs() {
        return new GetAuditLogs();
    }

    /**
     * Create an instance of {@link BOException }
     * 
     */
    public BOException createBOException() {
        return new BOException();
    }

    /**
     * Create an instance of {@link AuditLogResult }
     * 
     */
    public AuditLogResult createAuditLogResult() {
        return new AuditLogResult();
    }

    /**
     * Create an instance of {@link ArrayOfAuditLogDTO }
     * 
     */
    public ArrayOfAuditLogDTO createArrayOfAuditLogDTO() {
        return new ArrayOfAuditLogDTO();
    }

    /**
     * Create an instance of {@link AuditLogDTO }
     * 
     */
    public AuditLogDTO createAuditLogDTO() {
        return new AuditLogDTO();
    }

    /**
     * Create an instance of {@link ArrayOfAuditLogDTO1 }
     * 
     */
    public ArrayOfAuditLogDTO1 createArrayOfAuditLogDTO1() {
        return new ArrayOfAuditLogDTO1();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link BOException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:SAPGRC_AC_IDM_AUDITTRAILWsd/SAPGRC_AC_IDM_AUDITTRAILVi", name = "getAuditLogs_com.virsa.ae.core.BOException")
    public JAXBElement<BOException> createGetAuditLogsComVirsaAeCoreBOException(BOException value) {
        return new JAXBElement<BOException>(_GetAuditLogsComVirsaAeCoreBOException_QNAME, BOException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "id", scope = RequestHistoryDTO.class)
    public JAXBElement<String> createRequestHistoryDTOId(String value) {
        return new JAXBElement<String>(_RequestHistoryDTOId_QNAME, String.class, RequestHistoryDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "displayString", scope = RequestHistoryDTO.class)
    public JAXBElement<String> createRequestHistoryDTODisplayString(String value) {
        return new JAXBElement<String>(_RequestHistoryDTODisplayString_QNAME, String.class, RequestHistoryDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "actionDate", scope = RequestHistoryDTO.class)
    public JAXBElement<XMLGregorianCalendar> createRequestHistoryDTOActionDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RequestHistoryDTOActionDate_QNAME, XMLGregorianCalendar.class, RequestHistoryDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "userId", scope = RequestHistoryDTO.class)
    public JAXBElement<String> createRequestHistoryDTOUserId(String value) {
        return new JAXBElement<String>(_RequestHistoryDTOUserId_QNAME, String.class, RequestHistoryDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfRequestHistoryDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "childDTOs", scope = RequestHistoryDTO.class)
    public JAXBElement<ArrayOfRequestHistoryDTO> createRequestHistoryDTOChildDTOs(ArrayOfRequestHistoryDTO value) {
        return new JAXBElement<ArrayOfRequestHistoryDTO>(_RequestHistoryDTOChildDTOs_QNAME, ArrayOfRequestHistoryDTO.class, RequestHistoryDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "description", scope = RequestHistoryDTO.class)
    public JAXBElement<String> createRequestHistoryDTODescription(String value) {
        return new JAXBElement<String>(_RequestHistoryDTODescription_QNAME, String.class, RequestHistoryDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "dependentId", scope = RequestHistoryDTO.class)
    public JAXBElement<String> createRequestHistoryDTODependentId(String value) {
        return new JAXBElement<String>(_RequestHistoryDTODependentId_QNAME, String.class, RequestHistoryDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "path", scope = RequestHistoryDTO.class)
    public JAXBElement<String> createRequestHistoryDTOPath(String value) {
        return new JAXBElement<String>(_RequestHistoryDTOPath_QNAME, String.class, RequestHistoryDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "actionValue", scope = RequestHistoryDTO.class)
    public JAXBElement<String> createRequestHistoryDTOActionValue(String value) {
        return new JAXBElement<String>(_RequestHistoryDTOActionValue_QNAME, String.class, RequestHistoryDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "stage", scope = RequestHistoryDTO.class)
    public JAXBElement<String> createRequestHistoryDTOStage(String value) {
        return new JAXBElement<String>(_RequestHistoryDTOStage_QNAME, String.class, RequestHistoryDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "reqNo", scope = RequestHistoryDTO.class)
    public JAXBElement<String> createRequestHistoryDTOReqNo(String value) {
        return new JAXBElement<String>(_RequestHistoryDTOReqNo_QNAME, String.class, RequestHistoryDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "msgType", scope = ServiceStatusDTO.class)
    public JAXBElement<String> createServiceStatusDTOMsgType(String value) {
        return new JAXBElement<String>(_ServiceStatusDTOMsgType_QNAME, String.class, ServiceStatusDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "msgDesc", scope = ServiceStatusDTO.class)
    public JAXBElement<String> createServiceStatusDTOMsgDesc(String value) {
        return new JAXBElement<String>(_ServiceStatusDTOMsgDesc_QNAME, String.class, ServiceStatusDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "msgCode", scope = ServiceStatusDTO.class)
    public JAXBElement<String> createServiceStatusDTOMsgCode(String value) {
        return new JAXBElement<String>(_ServiceStatusDTOMsgCode_QNAME, String.class, ServiceStatusDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceStatusDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "status", scope = AuditLogResult.class)
    public JAXBElement<ServiceStatusDTO> createAuditLogResultStatus(ServiceStatusDTO value) {
        return new JAXBElement<ServiceStatusDTO>(_AuditLogResultStatus_QNAME, ServiceStatusDTO.class, AuditLogResult.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfAuditLogDTO1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "auditLogDTO", scope = AuditLogResult.class)
    public JAXBElement<ArrayOfAuditLogDTO1> createAuditLogResultAuditLogDTO(ArrayOfAuditLogDTO1 value) {
        return new JAXBElement<ArrayOfAuditLogDTO1>(_AuditLogResultAuditLogDTO_QNAME, ArrayOfAuditLogDTO1 .class, AuditLogResult.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfAuditLogDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "auditLogDTO", scope = ArrayOfAuditLogDTO1 .class)
    public JAXBElement<ArrayOfAuditLogDTO> createArrayOfAuditLogDTO1AuditLogDTO(ArrayOfAuditLogDTO value) {
        return new JAXBElement<ArrayOfAuditLogDTO>(_AuditLogResultAuditLogDTO_QNAME, ArrayOfAuditLogDTO.class, ArrayOfAuditLogDTO1 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "status", scope = AuditLogDTO.class)
    public JAXBElement<String> createAuditLogDTOStatus(String value) {
        return new JAXBElement<String>(_AuditLogResultStatus_QNAME, String.class, AuditLogDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "requestId", scope = AuditLogDTO.class)
    public JAXBElement<String> createAuditLogDTORequestId(String value) {
        return new JAXBElement<String>(_AuditLogDTORequestId_QNAME, String.class, AuditLogDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "priority", scope = AuditLogDTO.class)
    public JAXBElement<String> createAuditLogDTOPriority(String value) {
        return new JAXBElement<String>(_AuditLogDTOPriority_QNAME, String.class, AuditLogDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfRequestHistoryDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "requestHst", scope = AuditLogDTO.class)
    public JAXBElement<ArrayOfRequestHistoryDTO> createAuditLogDTORequestHst(ArrayOfRequestHistoryDTO value) {
        return new JAXBElement<ArrayOfRequestHistoryDTO>(_AuditLogDTORequestHst_QNAME, ArrayOfRequestHistoryDTO.class, AuditLogDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "logDetails", scope = AuditLogDTO.class)
    public JAXBElement<String> createAuditLogDTOLogDetails(String value) {
        return new JAXBElement<String>(_AuditLogDTOLogDetails_QNAME, String.class, AuditLogDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "submittedBy", scope = AuditLogDTO.class)
    public JAXBElement<String> createAuditLogDTOSubmittedBy(String value) {
        return new JAXBElement<String>(_AuditLogDTOSubmittedBy_QNAME, String.class, AuditLogDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "createDate", scope = AuditLogDTO.class)
    public JAXBElement<XMLGregorianCalendar> createAuditLogDTOCreateDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_AuditLogDTOCreateDate_QNAME, XMLGregorianCalendar.class, AuditLogDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.auditlogs", name = "requestedBy", scope = AuditLogDTO.class)
    public JAXBElement<String> createAuditLogDTORequestedBy(String value) {
        return new JAXBElement<String>(_AuditLogDTORequestedBy_QNAME, String.class, AuditLogDTO.class, value);
    }

}
