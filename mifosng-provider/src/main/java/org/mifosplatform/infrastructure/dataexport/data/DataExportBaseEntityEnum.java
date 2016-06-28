package org.mifosplatform.infrastructure.dataexport.data;

import java.util.ArrayList;
import java.util.List;

public enum DataExportBaseEntityEnum {

    CLIENT("client","m_client"),
    LOAN("client","m_loan"),
    SAVINGS("savings","m_savingsaccount");

    private String name;

    public String getTablename() {
        return tableName;
    }

    private String tableName;

    private DataExportBaseEntityEnum(String name, String tableName){

        this.name = name;
        this.tableName = tableName;

    }

}
