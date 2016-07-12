/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.data;

import java.util.ArrayList;
import java.util.List;

public enum DataExportBaseEntityEnum {

    CLIENT("client","m_client"),
    GROUP("group","m_group"),
    LOAN("client","m_loan"),
    SAVINGS("savings","m_savings_account");

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
