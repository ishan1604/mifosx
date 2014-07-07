package org.mifosplatform.portfolio.loanaccount.rescheduleloan.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.loanaccount.rescheduleloan.service.LoanRescheduleRequestWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApproveLoanRescheduleRequestCommandHandler implements NewCommandSourceHandler {
	private final LoanRescheduleRequestWritePlatformService loanRescheduleRequestWritePlatformService;
	
	@Autowired
	public ApproveLoanRescheduleRequestCommandHandler(
			LoanRescheduleRequestWritePlatformService loanRescheduleRequestWritePlatformService) {
		this.loanRescheduleRequestWritePlatformService = loanRescheduleRequestWritePlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand jsonCommand) {
		return this.loanRescheduleRequestWritePlatformService.approve(jsonCommand);
	}
}
