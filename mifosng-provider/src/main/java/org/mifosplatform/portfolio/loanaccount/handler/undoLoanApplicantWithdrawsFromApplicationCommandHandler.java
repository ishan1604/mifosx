/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.loanaccount.service.LoanApplicationWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "LOAN", action = "UNDOWITHDRAW")
public class undoLoanApplicantWithdrawsFromApplicationCommandHandler implements NewCommandSourceHandler {

    private final LoanApplicationWritePlatformService loanApplicationWritePlatformService;

    @Autowired
    public undoLoanApplicantWithdrawsFromApplicationCommandHandler(final LoanApplicationWritePlatformService loanApplicationWritePlatformService) {
        this.loanApplicationWritePlatformService = loanApplicationWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        return this.loanApplicationWritePlatformService.undoApplicantWithdrawsFromApplication(command.entityId(), command);
    }
}
