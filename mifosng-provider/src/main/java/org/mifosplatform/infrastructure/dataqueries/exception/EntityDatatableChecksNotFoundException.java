/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when report resources are not found.
 */
public class EntityDatatableChecksNotFoundException extends AbstractPlatformResourceNotFoundException {

    public EntityDatatableChecksNotFoundException(final Long id) {
        super("error.msg.entity.datatable.check.not.found", "Entity datatable check with identifier " + id + " does not exist", id);
    }
}