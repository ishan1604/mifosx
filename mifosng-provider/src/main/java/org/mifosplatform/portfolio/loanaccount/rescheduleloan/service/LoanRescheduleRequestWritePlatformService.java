package org.mifosplatform.portfolio.loanaccount.rescheduleloan.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface LoanRescheduleRequestWritePlatformService {
	
	CommandProcessingResult create(JsonCommand jsonCommand);

	CommandProcessingResult approve(JsonCommand jsonCommand);
	
	CommandProcessingResult reject(JsonCommand jsonCommand);
}
