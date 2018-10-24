/* $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/hrms/util/SAPAttributeMapBean.java /main/8 2009/08/14 02:21:03 ddkumar Exp $ */
/* Copyright (c) 2009, Oracle and/or its affiliates. All rights reserved. */
/*
 DESCRIPTION
 <short description of component this file declares/defines>

 PRIVATE CLASSES
 <list of private classes defined - with one-line descriptions>

 NOTES
 <other useful comments, qualifications, etc.>

 MODIFIED    (MM/DD/YY)
 ddkumar     01/14/09 - This class represents the SAP attribute mapping
 configuration and contains the VO attributes
 ddkumar     01/14/09 - Creation
 */
/**
 *  @version $Header: oimcp/connectors/sap/src/main/oracle/iam/connectors/sap/hrms/util/SAPAttributeMapBean.java /main/8 2009/08/14 02:21:03 ddkumar Exp $
 *  @author  ddkumar
 *  @since   release specific (what release of product did this appear in)
 */
package oracle.iam.connectors.sap.hrms.util;

/**
 * Description: Represents the SAP HRMS attribute mapping configuration and contains the VO attributes
 */
public class SAPAttributeMapBean {
    private String sSegmentName;
    private String sOIMFieldName;
    private String sSAPAttributeName;
    private String sSubType;
    private String sFieldValue;
    private String sStartPosition;
    private String sEndPosition;
    private String sBeginDatePosition;
    private String sEndDatePosition;
    private String sSubTypeStartPosition;
    private String sSubTypeEndPosition;
    private String sFieldType;

    /**
     * Description: Gets the Segment Name
     * @return Segment Name
     */
    public String getSSegmentName() {
        return sSegmentName;
    }

    /**
     * Description: Sets the Begin Date Position
     * @param segmentName
     *              Name of the segment
     */
    public void setSSegmentName(String segmentName) {
        sSegmentName = segmentName;
    }

    /**
     * Description: Gets the Field Value 
     * @return sFieldValue
     */
    public String getValue() {
        return sFieldValue;
    }

    
    /**
     * Description: Sets the Segment Name
     * @param value
     *              Field Value
     */
    public void setValue(String value) {
        sFieldValue = value;
    }

    /**
     * Description: Gets the End Postion
     * @return sEndPosition
     */
    public String getSEndPosition() {
        return sEndPosition;
    }
    
    /**
     * Description: Sets the End Postion
     * @param endPosition
     *              End Position value
     */
    public void setSEndPosition(String endPosition) {
        sEndPosition = endPosition;
    }

    /**
     * Description: Gets the Start Postion
     * @return sStartPosition
     */
    public String getSStartPosition() {
        return sStartPosition;
    }

    /**
     * Description: Sets the Start Postion
     * @param startPosition
     *              Start Position value
     */
    public void setSStartPosition(String startPosition) {
        sStartPosition = startPosition;
    }

    
    /**
     * Description: Gets the OIM Field Name
     * @return sOIMFieldName
     */
    public String getSOIMFieldName() {
        return sOIMFieldName;
    }

    
    /**
     * Description: Sets the OIM Field Name
     * @param fieldName
     *          OIM Field Name
     */
    public void setSOIMFieldName(String fieldName) {
        sOIMFieldName = fieldName;
    }

    
    
    /**
     * Description: Gets the SAP Attribute Name
     * @return sSAPAttributeName
     */
    public String getSSAPAttributeName() {
        return sSAPAttributeName;
    }

    
    /**
     * Description: Sets the SAP Attribute Name
     * @param attributeName
     *          SAP Attribute Field Name
     */
    public void setSSAPAttributeName(String attributeName) {
        sSAPAttributeName = attributeName;
    }

    
    /**
     * Description: Gets the Begin Date Position
     * @return sBeginDatePosition
     */
    public String getSBeginDatePosition() {
        return sBeginDatePosition;
    }

    
    /**
     * Description: Sets the Begin Date Position
     * @param beginDatePosition
     *          Begin Date Position for the BEGDA field of SAP
     */
    public void setSBeginDatePosition(String beginDatePosition) {
        sBeginDatePosition = beginDatePosition;
    }

    
    /**
     * Description: Gets the End Date Position
     * @return sEndDatePosition
     */
    public String getSEndDatePosition() {
        return sEndDatePosition;
    }

    
    /**
     * Description: Sets the End Date Position
     * @param endDatePosition
     *       End Date Position for the BEGDA field of SAP
     */
    public void setSEndDatePosition(String endDatePosition) {
        sEndDatePosition = endDatePosition;
    }

    
    /**
     * Description: Gets the End Date Position
     * @return sSubType
     */
    public String getSSubType() {
        return sSubType;
    }

        
    /**
     * Description: Sets the Sub type 
     * @param subType
     *          SUBTYP Field of SAP
     */
    public void setSSubType(String subType) {
        sSubType = subType;
    }

    
    /**
     * Description: Gets the End Sub Type Position
     * @return sSubTypeEndPosition
     */
    public String getSSubTypeEndPosition() {
        return sSubTypeEndPosition;
    }

    
    /**
     * Description: Sets the End Sub Type Position
     * @param subTypeEndPosition
     *          End Position for SUBTY Field of SAP
     */
    public void setSSubTypeEndPosition(String subTypeEndPosition) {
        sSubTypeEndPosition = subTypeEndPosition;
    }

    /**
     * Description: Gets the Start Sub Type Position
     * @return sSubTypeEndPosition
     */
    public String getSSubTypeStartPosition() {
        return sSubTypeStartPosition;
    }

    /**
     * Description: Sets the Start Sub Type Position
     * @param subTypeStartPosition
     *          Start Position for SUBTY Field of SAP
     */
    public void setSSubTypeStartPosition(String subTypeStartPosition) {
        sSubTypeStartPosition = subTypeStartPosition;
    }

    
    /**
     * Description: Gets the Field type
     * @return sFieldType
     */
		public String getSFieldType() {
			return sFieldType;
		}

		
		 /**
     * Description: Sets the Field Type
     * @param fieldType
     *          Field Type
     */
		public void setSFieldType(String fieldType) {
			sFieldType = fieldType;
		}
}
