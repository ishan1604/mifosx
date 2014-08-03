package org.mifosplatform.portfolio.transfer.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class UndoTransferNotFoundException extends AbstractPlatformDomainRuleException {

    public UndoTransferNotFoundException(final Long id) {
        super("error.msg.new.client.added.after.transfer.exception",
                "The Client with id `" + id + "` added after transfer", id);
    }
}
