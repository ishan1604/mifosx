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
    private boolean systemDefined;
    private String displayCondition;


    public static MetaDataResultSet createMetaDataResultSet(final Long id,final String columnName,final String labelName, final Long order,final boolean systemDefined, final String displayCondition){
        return new MetaDataResultSet(id,columnName,labelName,order,systemDefined, displayCondition);
    }
    private MetaDataResultSet(final Long id,final String columnName, final String labelName, final Long order,final boolean systemDefined, final String displayCondition) {
        this.id = id;
        this.columnName = columnName;
        this.labelName = labelName;
        this.order = order;
        this.systemDefined = systemDefined;
        this.displayCondition = displayCondition;
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

    public boolean isSystemDefined() {return this.systemDefined;}

    public String getDisplayCondition() { return displayCondition; }
}
