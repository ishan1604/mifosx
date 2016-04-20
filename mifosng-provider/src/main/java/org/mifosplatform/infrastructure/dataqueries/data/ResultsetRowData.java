/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.data;


import java.util.List;
import java.util.Map;

public class ResultsetRowData {

    private final List<String> row;
    private final Map<String, String> rowWithColumnName;

    public static ResultsetRowData create(final List<String> rowValues) {
        return new ResultsetRowData(rowValues);
    }

    public static ResultsetRowData createWithColumnName(final List<String> rowValues,final Map<String, String>  rowValuesWithColumn) {
        return new ResultsetRowData(rowValues,rowValuesWithColumn);
    }

    private ResultsetRowData(final List<String> rowValues) {
        this.row = rowValues;
        this.rowWithColumnName = null;
    }

    private ResultsetRowData(final List<String> rowValues,final Map<String, String>  rowValuesWithColumn) {
        this.row = rowValues;
        this.rowWithColumnName = rowValuesWithColumn;
    }

    public List<String> getRow() {
        return this.row;
    }

    public Map<String, String> getRowWithColumnName() {return this.rowWithColumnName;}
}
