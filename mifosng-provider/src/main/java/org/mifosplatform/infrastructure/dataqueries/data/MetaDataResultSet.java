package org.mifosplatform.infrastructure.dataqueries.data;


public class MetaDataResultSet {
    private Long id;
    private String columnName;
    private String labelName;
    private Long order;

    public static MetaDataResultSet createMetaDataResultSet(final Long id,final String columnName,final String labelName, final Long order){
        return new MetaDataResultSet(id,columnName,labelName,order);
    }
    private MetaDataResultSet(final Long id,final String columnName, final String labelName, final Long order) {
        this.id = id;
        this.columnName = columnName;
        this.labelName = labelName;
        this.order = order;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public String getLabelName() {
        return this.labelName;
    }

    public Long getOrder() {
        return this.order;
    }

    public Long getId() {
        return this.id;
    }
}
