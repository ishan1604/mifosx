/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.transfer.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class UndoTransferEntityNotFound extends AbstractPlatformResourceNotFoundException {
    public UndoTransferEntityNotFound(Long resourceId){
        super("error.msg.undoTransfer.entity.not.found", "UndoTransfer with identifier `" + resourceId + "` does not exist", resourceId);
    }
}
