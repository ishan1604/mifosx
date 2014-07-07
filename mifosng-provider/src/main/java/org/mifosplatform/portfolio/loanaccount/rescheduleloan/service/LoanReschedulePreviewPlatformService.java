package org.mifosplatform.portfolio.loanaccount.rescheduleloan.service;

import org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleModel;

public interface LoanReschedulePreviewPlatformService {
	
	public LoanRescheduleModel previewLoanReschedule(Long requestId);
}
