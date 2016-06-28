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
