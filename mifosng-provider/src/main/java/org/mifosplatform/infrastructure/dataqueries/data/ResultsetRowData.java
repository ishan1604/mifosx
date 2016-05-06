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
    private final Map<String, String> rowWithColumName;

    public static ResultsetRowData create(final List<String> rowValues) {
        return new ResultsetRowData(rowValues);
    }

    public static ResultsetRowData createWithColumName(final Map<String, String>  rowValues) {
        return new ResultsetRowData(rowValues);
    }

    private ResultsetRowData(final List<String> rowValues) {
        this.row = rowValues;
        this.rowWithColumName = null;
    }

    private ResultsetRowData(final Map<String, String>  rowValues) {
        this.row = null;
        this.rowWithColumName = rowValues;
    }

    public List<String> getRow() {
        return this.row;
    }


}
