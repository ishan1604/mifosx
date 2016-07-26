/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.data;


public enum DataExportFieldLabel {

    INVALID("invalid", "invalid", "invalid", null),
    ACCOUNT_NO("accountNo", "account_no", "Account", null),
    CLIENT_ID("clientId", "client_id", "Client", "m_client"),
    GROUP_ID("groupId", "group_id", "Group", "m_group"),
    OFFICE("office", "office_id", "Office", "m_office"),
    ENTITY_NAME("entityName", "display_name", "Name", null),
    OFFICE_NAME("entityName", "name", "Office", "m_office"),
    STATUS("status", "status_enum", "Status", null),
    LOAN_STATUS("status", "loan_status_id", "Status", "m_loan"),
    LOAN_TYPE("type", "loan_type_enum", "Type", "m_loan"),
    SAVINGS_TYPE("type", "product_id", "Type", "m_savings_account"),
    PRINCIPAL_AMOUNT("principal", "principal_amount", "Principal", null),
    TOTAL_OUTSTANDING("outstanding", "total_outstanding_derived", "Outstanding", null),
    MOBILE_NO("mobileNo", "mobile_no", "Mobile", "m_client"),
    SUBMITTED_ON_DATE("submittedondate", "submittedon_date", "Submitted On", null),
    ACCOUNT_BALANCE("balance", "account_balance_derived", "Account Balance", null);

    private String parameter;
    private String field;
    private String label;
    private String table;

    public String getParameter() {return parameter;}

    public String getField() {return field;}

    public String getLabel() {return label;}

    public String getTable() {return table;}

    public boolean isClientId(){return this.equals(CLIENT_ID);}
    public boolean isOfficeId(){return this.equals(OFFICE);}
    public boolean isGroupId(){return this.equals(GROUP_ID);}

    DataExportFieldLabel(String parameter, String field, String label, String table){
        this.parameter = parameter;
        this.field = field;
        this.label = label;
        this.table = table;
    }

    public static DataExportFieldLabel fromParam(String parameter, DataExportBaseEntityEnum entity){
        DataExportFieldLabel enumeration = DataExportFieldLabel.INVALID;
        switch (parameter) {
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
            default: break;
        }
        return enumeration;
    }

    public static DataExportFieldLabel getReferral(String table){
        DataExportFieldLabel enumeration = DataExportFieldLabel.INVALID;
        switch(table){
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
