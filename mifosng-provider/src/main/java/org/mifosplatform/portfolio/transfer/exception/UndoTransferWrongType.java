package org.mifosplatform.portfolio.transfer.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class UndoTransferWrongType extends AbstractPlatformDomainRuleException {
    public UndoTransferWrongType() {
        super("error.msg.transfer.type.is.group.exception",
                "The undo Transfer is of type Group");
    }
}
