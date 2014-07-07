package org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanSchedulePeriodData;

public interface LoanRescheduleModalPeriod {
	
	LoanSchedulePeriodData toData();
	
	Integer periodNumber();
	
	Integer oldPeriodNumber();

    LocalDate periodFromDate();

    LocalDate periodDueDate();

    BigDecimal principalDue();

    BigDecimal interestDue();

    BigDecimal feeChargesDue();

    BigDecimal penaltyChargesDue();
    
    boolean isNew();
}
