/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.data;

import java.io.Serializable;

/**
 * Created by Cieyou on 2/10/2015.
 */
public class DatatableCheksData implements Serializable {

    private final long id;
    private final String dataTableName;

    public DatatableCheksData(final long id, final String dataTableName){
        this.id = id;
        this.dataTableName = dataTableName;

    }

}
