/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
        return columnName;
    }

    public String getLabelName() {
        return labelName;
    }

    public Long getOrder() {
        return order;
    }

    public Long getId() {
        return this.id;
    }
}
