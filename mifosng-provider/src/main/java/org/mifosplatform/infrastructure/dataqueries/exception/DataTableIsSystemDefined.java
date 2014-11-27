/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.exception;


import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class DataTableIsSystemDefined extends AbstractPlatformResourceNotFoundException {

    public DataTableIsSystemDefined(final String datatable) {
        super("error.msg.datatable.is.system.defined", "Cannot delete system defined data table: ", datatable);
    }
}
