 
package oracle.iam.connectors.sap.cup.ws.submitreq;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the examples.webservices.simple_client.submitrequest package. 
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

    private final static QName _CustomFieldsDTOValue_QNAME = new QName("urn:com.virsa.ae.ejbutil", "value");
    private final static QName _CustomFieldsDTOName_QNAME = new QName("urn:com.virsa.ae.ejbutil", "name");
    private final static QName _RoleDataRoleId_QNAME = new QName("urn:com.virsa.ae.ejbutil", "roleId");
    private final static QName _RoleDataComments_QNAME = new QName("urn:com.virsa.ae.ejbutil", "comments");
    private final static QName _RoleDataValidFrom_QNAME = new QName("urn:com.virsa.ae.ejbutil", "validFrom");
    private final static QName _RoleDataAction_QNAME = new QName("urn:com.virsa.ae.ejbutil", "action");
    private final static QName _RoleDataCompany_QNAME = new QName("urn:com.virsa.ae.ejbutil", "company");
    private final static QName _RoleDataValidTo_QNAME = new QName("urn:com.virsa.ae.ejbutil", "validTo");
    private final static QName _RoleDataSysId_QNAME = new QName("urn:com.virsa.ae.ejbutil", "sysId");
    private final static QName _RequestSubmissionResultStatus_QNAME = new QName("urn:com.virsa.ae.ejbutil.submitrequest", "status");
    private final static QName _RequestSubmissionResultRequestNo_QNAME = new QName("urn:com.virsa.ae.ejbutil.submitrequest", "requestNo");
    private final static QName _ServiceStatusDTOMsgType_QNAME = new QName("urn:com.virsa.ae.ejbutil", "msgType");
    private final static QName _ServiceStatusDTOMsgDesc_QNAME = new QName("urn:com.virsa.ae.ejbutil", "msgDesc");
    private final static QName _ServiceStatusDTOMsgCode_QNAME = new QName("urn:com.virsa.ae.ejbutil", "msgCode");
    private final static QName _RequestDetailsDataTelephone_QNAME = new QName("urn:com.virsa.ae.ejbutil", "telephone");
    private final static QName _RequestDetailsDataRequestReason_QNAME = new QName("urn:com.virsa.ae.ejbutil", "requestReason");
    private final static QName _RequestDetailsDataRequestorEmailAddress_QNAME = new QName("urn:com.virsa.ae.ejbutil", "requestorEmailAddress");
    private final static QName _RequestDetailsDataRoles_QNAME = new QName("urn:com.virsa.ae.ejbutil", "roles");
    private final static QName _RequestDetailsDataManagerTelephone_QNAME = new QName("urn:com.virsa.ae.ejbutil", "managerTelephone");
    private final static QName _RequestDetailsDataEmailAddress_QNAME = new QName("urn:com.virsa.ae.ejbutil", "emailAddress");
    private final static QName _RequestDetailsDataRequestType_QNAME = new QName("urn:com.virsa.ae.ejbutil", "requestType");
    private final static QName _RequestDetailsDataMgrEmailAddress_QNAME = new QName("urn:com.virsa.ae.ejbutil", "mgrEmailAddress");
    private final static QName _RequestDetailsDataLastName_QNAME = new QName("urn:com.virsa.ae.ejbutil", "lastName");
    private final static QName _RequestDetailsDataEmployeeType_QNAME = new QName("urn:com.virsa.ae.ejbutil", "employeeType");
    private final static QName _RequestDetailsDataRequestorLastName_QNAME = new QName("urn:com.virsa.ae.ejbutil", "requestorLastName");
    private final static QName _RequestDetailsDataApplication_QNAME = new QName("urn:com.virsa.ae.ejbutil", "application");
    private final static QName _RequestDetailsDataRequestorId_QNAME = new QName("urn:com.virsa.ae.ejbutil", "requestorId");
    private final static QName _RequestDetailsDataFirstName_QNAME = new QName("urn:com.virsa.ae.ejbutil", "firstName");
    private final static QName _RequestDetailsDataUserId_QNAME = new QName("urn:com.virsa.ae.ejbutil", "userId");
    private final static QName _RequestDetailsDataPriority_QNAME = new QName("urn:com.virsa.ae.ejbutil", "priority");
    private final static QName _RequestDetailsDataSNCName_QNAME = new QName("urn:com.virsa.ae.ejbutil", "sNCName");
    private final static QName _RequestDetailsDataFunctionalArea_QNAME = new QName("urn:com.virsa.ae.ejbutil", "functionalArea");
    private final static QName _RequestDetailsDataMgrId_QNAME = new QName("urn:com.virsa.ae.ejbutil", "mgrId");
    private final static QName _RequestDetailsDataUnsecureLogon_QNAME = new QName("urn:com.virsa.ae.ejbutil", "unsecureLogon");
    private final static QName _RequestDetailsDataMgrFirstName_QNAME = new QName("urn:com.virsa.ae.ejbutil", "mgrFirstName");
    private final static QName _RequestDetailsDataLocation_QNAME = new QName("urn:com.virsa.ae.ejbutil", "location");
    private final static QName _RequestDetailsDataCustomField_QNAME = new QName("urn:com.virsa.ae.ejbutil", "customField");
    private final static QName _RequestDetailsDataRequestorFirstName_QNAME = new QName("urn:com.virsa.ae.ejbutil", "requestorFirstName");
    private final static QName _RequestDetailsDataLocale_QNAME = new QName("urn:com.virsa.ae.ejbutil", "locale");
    private final static QName _RequestDetailsDataMgrLastName_QNAME = new QName("urn:com.virsa.ae.ejbutil", "mgrLastName");
    private final static QName _RequestDetailsDataRequestorTelephone_QNAME = new QName("urn:com.virsa.ae.ejbutil", "requestorTelephone");
    private final static QName _RequestDetailsDataDepartment_QNAME = new QName("urn:com.virsa.ae.ejbutil", "department");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: examples.webservices.simple_client.submitrequest
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RoleData }
     * 
     */
    public RoleData createRoleData() {
        return new RoleData();
    }

    /**
     * Create an instance of {@link CustomFieldsDTO }
     * 
     */
    public CustomFieldsDTO createCustomFieldsDTO() {
        return new CustomFieldsDTO();
    }

    /**
     * Create an instance of {@link ArrayOfCustomFieldsDTO }
     * 
     */
    public ArrayOfCustomFieldsDTO createArrayOfCustomFieldsDTO() {
        return new ArrayOfCustomFieldsDTO();
    }

    /**
     * Create an instance of {@link ServiceStatusDTO }
     * 
     */
    public ServiceStatusDTO createServiceStatusDTO() {
        return new ServiceStatusDTO();
    }

    /**
     * Create an instance of {@link RequestSubmissionResult }
     * 
     */
    public RequestSubmissionResult createRequestSubmissionResult() {
        return new RequestSubmissionResult();
    }

    /**
     * Create an instance of {@link GetSubmitRequest }
     * 
     */
    public GetSubmitRequest createGetSubmitRequest() {
        return new GetSubmitRequest();
    }

    /**
     * Create an instance of {@link ArrayOfRoleData }
     * 
     */
    public ArrayOfRoleData createArrayOfRoleData() {
        return new ArrayOfRoleData();
    }

    /**
     * Create an instance of {@link RequestDetailsData }
     * 
     */
    public RequestDetailsData createRequestDetailsData() {
        return new RequestDetailsData();
    }

    /**
     * Create an instance of {@link GetSubmitRequestResponse }
     * 
     */
    public GetSubmitRequestResponse createGetSubmitRequestResponse() {
        return new GetSubmitRequestResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "value", scope = CustomFieldsDTO.class)
    public JAXBElement<String> createCustomFieldsDTOValue(String value) {
        return new JAXBElement<String>(_CustomFieldsDTOValue_QNAME, String.class, CustomFieldsDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "name", scope = CustomFieldsDTO.class)
    public JAXBElement<String> createCustomFieldsDTOName(String value) {
        return new JAXBElement<String>(_CustomFieldsDTOName_QNAME, String.class, CustomFieldsDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "roleId", scope = RoleData.class)
    public JAXBElement<String> createRoleDataRoleId(String value) {
        return new JAXBElement<String>(_RoleDataRoleId_QNAME, String.class, RoleData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "comments", scope = RoleData.class)
    public JAXBElement<String> createRoleDataComments(String value) {
        return new JAXBElement<String>(_RoleDataComments_QNAME, String.class, RoleData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "validFrom", scope = RoleData.class)
    public JAXBElement<XMLGregorianCalendar> createRoleDataValidFrom(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RoleDataValidFrom_QNAME, XMLGregorianCalendar.class, RoleData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "action", scope = RoleData.class)
    public JAXBElement<String> createRoleDataAction(String value) {
        return new JAXBElement<String>(_RoleDataAction_QNAME, String.class, RoleData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "company", scope = RoleData.class)
    public JAXBElement<String> createRoleDataCompany(String value) {
        return new JAXBElement<String>(_RoleDataCompany_QNAME, String.class, RoleData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "validTo", scope = RoleData.class)
    public JAXBElement<XMLGregorianCalendar> createRoleDataValidTo(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RoleDataValidTo_QNAME, XMLGregorianCalendar.class, RoleData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "sysId", scope = RoleData.class)
    public JAXBElement<String> createRoleDataSysId(String value) {
        return new JAXBElement<String>(_RoleDataSysId_QNAME, String.class, RoleData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceStatusDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.submitrequest", name = "status", scope = RequestSubmissionResult.class)
    public JAXBElement<ServiceStatusDTO> createRequestSubmissionResultStatus(ServiceStatusDTO value) {
        return new JAXBElement<ServiceStatusDTO>(_RequestSubmissionResultStatus_QNAME, ServiceStatusDTO.class, RequestSubmissionResult.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil.submitrequest", name = "requestNo", scope = RequestSubmissionResult.class)
    public JAXBElement<String> createRequestSubmissionResultRequestNo(String value) {
        return new JAXBElement<String>(_RequestSubmissionResultRequestNo_QNAME, String.class, RequestSubmissionResult.class, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "telephone", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataTelephone(String value) {
        return new JAXBElement<String>(_RequestDetailsDataTelephone_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "validFrom", scope = RequestDetailsData.class)
    public JAXBElement<XMLGregorianCalendar> createRequestDetailsDataValidFrom(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RoleDataValidFrom_QNAME, XMLGregorianCalendar.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "requestReason", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataRequestReason(String value) {
        return new JAXBElement<String>(_RequestDetailsDataRequestReason_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "company", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataCompany(String value) {
        return new JAXBElement<String>(_RoleDataCompany_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "requestorEmailAddress", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataRequestorEmailAddress(String value) {
        return new JAXBElement<String>(_RequestDetailsDataRequestorEmailAddress_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfRoleData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "roles", scope = RequestDetailsData.class)
    public JAXBElement<ArrayOfRoleData> createRequestDetailsDataRoles(ArrayOfRoleData value) {
        return new JAXBElement<ArrayOfRoleData>(_RequestDetailsDataRoles_QNAME, ArrayOfRoleData.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "managerTelephone", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataManagerTelephone(String value) {
        return new JAXBElement<String>(_RequestDetailsDataManagerTelephone_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "emailAddress", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataEmailAddress(String value) {
        return new JAXBElement<String>(_RequestDetailsDataEmailAddress_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "requestType", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataRequestType(String value) {
        return new JAXBElement<String>(_RequestDetailsDataRequestType_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "mgrEmailAddress", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataMgrEmailAddress(String value) {
        return new JAXBElement<String>(_RequestDetailsDataMgrEmailAddress_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "lastName", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataLastName(String value) {
        return new JAXBElement<String>(_RequestDetailsDataLastName_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "employeeType", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataEmployeeType(String value) {
        return new JAXBElement<String>(_RequestDetailsDataEmployeeType_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "requestorLastName", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataRequestorLastName(String value) {
        return new JAXBElement<String>(_RequestDetailsDataRequestorLastName_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "application", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataApplication(String value) {
        return new JAXBElement<String>(_RequestDetailsDataApplication_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "requestorId", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataRequestorId(String value) {
        return new JAXBElement<String>(_RequestDetailsDataRequestorId_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "firstName", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataFirstName(String value) {
        return new JAXBElement<String>(_RequestDetailsDataFirstName_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "userId", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataUserId(String value) {
        return new JAXBElement<String>(_RequestDetailsDataUserId_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "priority", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataPriority(String value) {
        return new JAXBElement<String>(_RequestDetailsDataPriority_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "sNCName", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataSNCName(String value) {
        return new JAXBElement<String>(_RequestDetailsDataSNCName_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "validTo", scope = RequestDetailsData.class)
    public JAXBElement<XMLGregorianCalendar> createRequestDetailsDataValidTo(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RoleDataValidTo_QNAME, XMLGregorianCalendar.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "functionalArea", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataFunctionalArea(String value) {
        return new JAXBElement<String>(_RequestDetailsDataFunctionalArea_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "mgrId", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataMgrId(String value) {
        return new JAXBElement<String>(_RequestDetailsDataMgrId_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "unsecureLogon", scope = RequestDetailsData.class)
    public JAXBElement<Boolean> createRequestDetailsDataUnsecureLogon(Boolean value) {
        return new JAXBElement<Boolean>(_RequestDetailsDataUnsecureLogon_QNAME, Boolean.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "mgrFirstName", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataMgrFirstName(String value) {
        return new JAXBElement<String>(_RequestDetailsDataMgrFirstName_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "location", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataLocation(String value) {
        return new JAXBElement<String>(_RequestDetailsDataLocation_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfCustomFieldsDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "customField", scope = RequestDetailsData.class)
    public JAXBElement<ArrayOfCustomFieldsDTO> createRequestDetailsDataCustomField(ArrayOfCustomFieldsDTO value) {
        return new JAXBElement<ArrayOfCustomFieldsDTO>(_RequestDetailsDataCustomField_QNAME, ArrayOfCustomFieldsDTO.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "requestorFirstName", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataRequestorFirstName(String value) {
        return new JAXBElement<String>(_RequestDetailsDataRequestorFirstName_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "locale", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataLocale(String value) {
        return new JAXBElement<String>(_RequestDetailsDataLocale_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "mgrLastName", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataMgrLastName(String value) {
        return new JAXBElement<String>(_RequestDetailsDataMgrLastName_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "requestorTelephone", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataRequestorTelephone(String value) {
        return new JAXBElement<String>(_RequestDetailsDataRequestorTelephone_QNAME, String.class, RequestDetailsData.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:com.virsa.ae.ejbutil", name = "department", scope = RequestDetailsData.class)
    public JAXBElement<String> createRequestDetailsDataDepartment(String value) {
        return new JAXBElement<String>(_RequestDetailsDataDepartment_QNAME, String.class, RequestDetailsData.class, value);
    }

}
