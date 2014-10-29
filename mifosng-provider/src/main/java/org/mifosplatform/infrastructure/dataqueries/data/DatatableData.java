/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.data;

import java.util.List;

/**
 * Immutable data object representing datatable data.
 */
public class DatatableData {

    @SuppressWarnings("unused")
    private final String applicationTableName;
    @SuppressWarnings("unused")
    private final String registeredTableName;
    @SuppressWarnings("unused")
    private final Long category;
    @SuppressWarnings("unused")
    private final List<ResultsetColumnHeaderData> columnHeaderData;
    @SuppressWarnings("unused")
    private final List<MetaDataResultSet> metaDataResultSets;


    public static DatatableData create(final String applicationTableName, final String registeredTableName,
            final List<ResultsetColumnHeaderData> columnHeaderData,final Long category,final List<MetaDataResultSet> metaDataResultSets) {
        return new DatatableData(applicationTableName, registeredTableName, columnHeaderData,category,metaDataResultSets);
    }

    private DatatableData(final String applicationTableName, final String registeredTableName,
            final List<ResultsetColumnHeaderData> columnHeaderData,final Long category,final List<MetaDataResultSet> metaDataResultSets) {
        this.applicationTableName = applicationTableName;
        this.registeredTableName = registeredTableName;
        this.columnHeaderData = columnHeaderData;
        this.category  = category;
        this.metaDataResultSets = metaDataResultSets;
    }
}