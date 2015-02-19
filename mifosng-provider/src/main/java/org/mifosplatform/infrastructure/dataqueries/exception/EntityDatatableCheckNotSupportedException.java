/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when datatable resources are not found.
 */
public class EntityDatatableCheckNotSupportedException extends AbstractPlatformResourceNotFoundException {

    public EntityDatatableCheckNotSupportedException(final String datatableName, final String entityName) {
        super("error.msg.entity.datatable.check.combination.not.supported", "Entity Datatable check on table datatable: is not supported for entity entity:", datatableName ,entityName);
    }

}