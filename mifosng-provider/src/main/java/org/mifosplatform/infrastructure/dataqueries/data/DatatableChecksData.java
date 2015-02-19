/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.data;

import java.io.Serializable;

public class DatatableChecksData implements Serializable {

    private static final long serialVersionUID = 3113568562509206452L;
    private final long id;
    private final String dataTableName;

    public DatatableChecksData(final long id, final String dataTableName){
        this.id = id;
        this.dataTableName = dataTableName;
    }
}
