/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.data;

import java.util.List;

public class DataExportRequestData {

    // Specify the baseEntity which is used as the base object:
    public DataExportBaseEntityEnum baseEntity;

    // Contains the various filter queries for each tableName
    public List<DataExportFilter> dataExportFiltersList;

    // List of other (data)tables to retrieve:
    public List<String> exportDatatables;

    private String select;
    private String from;
    private String where;
    private String order;


    public DataExportRequestData(DataExportBaseEntityEnum baseEntity, List<DataExportFilter> dataExportFiltersList, List<String> exportDatatables) {
        this.baseEntity = baseEntity;
        this.dataExportFiltersList = dataExportFiltersList;
        this.exportDatatables = exportDatatables;
    }



}
