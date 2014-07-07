package org.mifosplatform.portfolio.loanaccount.rescheduleloan.service;

import java.util.List;

import org.mifosplatform.portfolio.loanaccount.rescheduleloan.data.LoanRescheduleRequestData;

public interface LoanRescheduleRequestReadPlatformService {
	
	/** 
	 * get all loan reschedule requests by loan ID
	 * 
	 * @param loanId the loan identifier
	 * @return list of LoanRescheduleRequestData objects
	 **/
	public List<LoanRescheduleRequestData> readLoanRescheduleRequests(Long loanId);
	
	/** 
	 * get a single loan reschedule request by ID (primary key) 
	 * 
	 * @param requestId the loan reschedule request identifier
	 * @return a LoanRescheduleRequestData object
	 **/
	public LoanRescheduleRequestData readLoanRescheduleRequest(Long requestId);
	
	/** 
	 * get all loan reschedule requests filter by loan ID and status enum
	 * 
	 * @param loanId the loan identifier
	 * @return list of LoanRescheduleRequestData objects
	 **/
	public List<LoanRescheduleRequestData> readLoanRescheduleRequests(Long loanId, Integer statusEnum);
}
