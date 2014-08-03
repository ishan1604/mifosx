package org.mifosplatform.portfolio.transfer.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.transfer.service.TransferWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UndoGroupTransferCommandHandler implements NewCommandSourceHandler {

    private final TransferWritePlatformService transferWritePlatformService;

    @Autowired
    public UndoGroupTransferCommandHandler(TransferWritePlatformService transferWritePlatformService) {
        this.transferWritePlatformService = transferWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        return this.transferWritePlatformService.undoGroupTransfer(command.entityId(), command);
    }
}
