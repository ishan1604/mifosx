package org.mifosplatform.portfolio.transfer.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class UndoTransferEntityNotFound extends AbstractPlatformResourceNotFoundException {
    public UndoTransferEntityNotFound(Long resourceId){
        super("error.msg.undoTransfer.entity.not.found", "UndoTransfer with identifier `" + resourceId + "` does not exist", resourceId);
    }
}
