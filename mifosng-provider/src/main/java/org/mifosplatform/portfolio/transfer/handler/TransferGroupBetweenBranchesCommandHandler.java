package org.mifosplatform.portfolio.transfer.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.transfer.service.TransferWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferGroupBetweenBranchesCommandHandler implements NewCommandSourceHandler {

    private final TransferWritePlatformService writePlatformService;

    @Autowired
    public TransferGroupBetweenBranchesCommandHandler(TransferWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        return this.writePlatformService.transferGroupBetweenBranches(command.entityId(), command);
    }
}