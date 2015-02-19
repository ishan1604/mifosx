/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.data;

public class DatatableCheckStatusData {

    private final String name;
    private final int code;

    public DatatableCheckStatusData(final String name, final int code ){
        this.name = name;
        this.code = code;
    }

}
