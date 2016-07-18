/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.data;

public class DataExportFilter {
    public String tableName;
    public String searchQuery;

    public DataExportFilter(String tableName, String searchQuery) {
        this.tableName = tableName;
        this.searchQuery = searchQuery;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

}
