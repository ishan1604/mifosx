/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.staff.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class staffCannotBeSetToNonActive extends AbstractPlatformResourceNotFoundException {

    public staffCannotBeSetToNonActive(final Long id) {
        super("error.msg.staff.id.cannot.be.non.active", "Staff with identifier " + id + " cannot be non active, staff is attached to an active group,loan or a savings");
    }
}
