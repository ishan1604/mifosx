/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.data;


import org.mifosplatform.infrastructure.dataexport.domain.DataExport;

public enum DataExportFieldLabel {

    INVALID("invalid", "invalid", "invalid", null),
    ENTITY_ID("id", "id", "'ID'", null),
    ACCOUNT_NO("accountNo", "account_no", "'Account'", null),
    CLIENT_ID("clientId", "client_id", "'Client'", "m_client"),
    GROUP_ID("groupId", "group_id", "'Group'", "m_group"),
    OFFICE("office", "office_id", "'Office'", "m_office"),
    ENTITY_NAME("entityName", "display_name", "'Name'", null),
    OFFICE_NAME("entityName", "name", "'Office'", null),
    STATUS("status", "status_enum", "'Status'", null),
    LOAN_STATUS("status", "loan_status_id", "'Status'", null),
    LOAN_TYPE("type", "loan_type_enum", "'Type'", null),
    SAVINGS_TYPE("type", "product_id", "'Type'", null),
    PRINCIPAL_AMOUNT("principal", "principal_amount", "'Principal'", null),
    TOTAL_OUTSTANDING("outstanding", "total_outstanding_derived", "'Outstanding'", null),
    MOBILE_NO("mobileNo", "mobile_no", "'Mobile'", null),
    EXTERNAL_ID("externalId", "external_id", '"' + "'External Id" + '"', null),
    STAFF_ID("staff", "staff_id", "'Staff'", "m_staff"),
    SUBMITTED_ON_DATE("submittedondate", "submittedon_date", "'Submitted On'", null),
    ACCOUNT_BALANCE("balance", "account_balance_derived", "'Account Balance'", null);

    private String parameter;
    private String field;
    private String label;
    private String referenceTable;

    public String getParameter() {return parameter;}

    public String getField() {return field;}

    public String getLabel() {return label;}

    public String getReferenceTable() {return referenceTable;}

    public boolean isClientId(){return this.equals(CLIENT_ID);}
    public boolean isOfficeId(){return this.equals(OFFICE);}
    public boolean isGroupId(){return this.equals(GROUP_ID);}
    public boolean isStaffId(){return this.equals(STAFF_ID);}
    public boolean isInvalid(){return this.equals(INVALID);}

    DataExportFieldLabel(String parameter, String field, String label, String referenceTable){
        this.parameter = parameter;
        this.field = field;
        this.label = label;
        this.referenceTable = referenceTable;
    }

    public static DataExportFieldLabel fromParam(String parameter, DataExportBaseEntityEnum entity){
        DataExportFieldLabel enumeration = DataExportFieldLabel.INVALID;
        switch (parameter) {
            case "id":
                enumeration = DataExportFieldLabel.ENTITY_ID;
                break;
            case "accountNo":
                enumeration = DataExportFieldLabel.ACCOUNT_NO;
                break;
            case "clientId":
                enumeration = DataExportFieldLabel.CLIENT_ID;
                break;
            case "groupId":
                enumeration = DataExportFieldLabel.GROUP_ID;
                break;
            case "office":
                enumeration = DataExportFieldLabel.OFFICE;
                break;
            case "entityName":
                enumeration = DataExportFieldLabel.ENTITY_NAME;
                break;
            case "status":
                if(entity != null && entity.isLoan()){enumeration = DataExportFieldLabel.LOAN_STATUS;
                }else{enumeration = DataExportFieldLabel.STATUS;}
                break;
            case "type":
                if(entity == null){break;}
                if(entity.isLoan()) {enumeration = DataExportFieldLabel.LOAN_TYPE;}
                if(entity.isSavingsAccount()) {enumeration = DataExportFieldLabel.SAVINGS_TYPE;}
                break;
            case "externalId":
                enumeration = DataExportFieldLabel.EXTERNAL_ID;
                break;
            case "staff":
                enumeration = DataExportFieldLabel.STAFF_ID;
                break;
            case "principal":
                enumeration = DataExportFieldLabel.PRINCIPAL_AMOUNT;
                break;
            case "outstanding":
                enumeration = DataExportFieldLabel.TOTAL_OUTSTANDING;
                break;
            case "mobileNo":
                enumeration = DataExportFieldLabel.MOBILE_NO;
                break;
            case "submittedondate":
                enumeration = DataExportFieldLabel.SUBMITTED_ON_DATE;
                break;
            case "balance":
                enumeration = DataExportFieldLabel.ACCOUNT_BALANCE;
                break;
            default: break;
        }
        return enumeration;
    }

    public static DataExportFieldLabel fromField(String parameter){
        DataExportFieldLabel enumeration = DataExportFieldLabel.INVALID;
        switch (parameter) {
            case "id":
                enumeration = DataExportFieldLabel.ENTITY_ID;
                break;
            case "account_no":
                enumeration = DataExportFieldLabel.ACCOUNT_NO;
                break;
            case "client_id":
                enumeration = DataExportFieldLabel.CLIENT_ID;
                break;
            case "group_id":
                enumeration = DataExportFieldLabel.GROUP_ID;
                break;
            case "office_id":
                enumeration = DataExportFieldLabel.OFFICE;
                break;
            case "display_name":
                enumeration = DataExportFieldLabel.ENTITY_NAME;
                break;
            case "status_enum":
                enumeration = DataExportFieldLabel.STATUS;
                break;
            case "loan_status_id":
                enumeration = DataExportFieldLabel.LOAN_STATUS;
                break;
            case "loan_type_enum":
                enumeration = DataExportFieldLabel.LOAN_TYPE;
                break;
            case "product_id":
                enumeration = DataExportFieldLabel.SAVINGS_TYPE;
                break;
            case "principal_amount":
                enumeration = DataExportFieldLabel.PRINCIPAL_AMOUNT;
                break;
            case "total_outstanding_derived":
                enumeration = DataExportFieldLabel.TOTAL_OUTSTANDING;
                break;
            case "mobile_no":
                enumeration = DataExportFieldLabel.MOBILE_NO;
                break;
            case "submittedon_date":
                enumeration = DataExportFieldLabel.SUBMITTED_ON_DATE;
                break;
            case "account_balance_derived":
                enumeration = DataExportFieldLabel.ACCOUNT_BALANCE;
                break;
            case "external_id":
                enumeration = DataExportFieldLabel.EXTERNAL_ID;
                break;
            case "staff_id":
                enumeration = DataExportFieldLabel.STAFF_ID;
                break;
            default: break;
        }
        return enumeration;
    }

    public static DataExportFieldLabel fromLabel(String label, DataExportBaseEntityEnum entity){
        DataExportFieldLabel enumeration = DataExportFieldLabel.INVALID;
        switch (label) {
            case "ID":
                enumeration = DataExportFieldLabel.ENTITY_ID;
                break;
            case "Account":
                enumeration = DataExportFieldLabel.ACCOUNT_NO;
                break;
            case "Client":
                enumeration = DataExportFieldLabel.CLIENT_ID;
                break;
            case "Group":
                enumeration = DataExportFieldLabel.GROUP_ID;
                break;
            case "Office":
                enumeration = DataExportFieldLabel.OFFICE;
                break;
            case "Name":
                enumeration = DataExportFieldLabel.ENTITY_NAME;
                break;
            case "Status":
                if(entity != null && entity.isLoan()){enumeration = DataExportFieldLabel.LOAN_STATUS;
                }else{enumeration = DataExportFieldLabel.STATUS;}
                break;
            case "Type":
                if(entity == null){break;}
                if(entity.isLoan()) {enumeration = DataExportFieldLabel.LOAN_TYPE;}
                if(entity.isSavingsAccount()) {enumeration = DataExportFieldLabel.SAVINGS_TYPE;}
                break;
            case "Principal":
                enumeration = DataExportFieldLabel.PRINCIPAL_AMOUNT;
                break;
            case "Outstanding":
                enumeration = DataExportFieldLabel.TOTAL_OUTSTANDING;
                break;
            case "Mobile":
                enumeration = DataExportFieldLabel.MOBILE_NO;
                break;
            case "Submitted On":
                enumeration = DataExportFieldLabel.SUBMITTED_ON_DATE;
                break;
            case "Account Balance":
                enumeration = DataExportFieldLabel.ACCOUNT_BALANCE;
                break;
            case "External Id":
                enumeration = DataExportFieldLabel.EXTERNAL_ID;
                break;
            case "Staff":
                enumeration = DataExportFieldLabel.STAFF_ID;
                break;
            default: break;
        }
        return enumeration;
    }

    public static DataExportFieldLabel refer(String table){
        DataExportFieldLabel enumeration = DataExportFieldLabel.INVALID;
        switch(table){
            case "m_staff":
            case "m_group":
            case "m_client":
                enumeration = DataExportFieldLabel.ENTITY_NAME;
                break;
            case "m_office":
                enumeration = DataExportFieldLabel.OFFICE_NAME;
        }
        return enumeration;
    }
}
