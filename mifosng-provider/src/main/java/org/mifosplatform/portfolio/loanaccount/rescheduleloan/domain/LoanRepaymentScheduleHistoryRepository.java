package org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanRepaymentScheduleHistoryRepository extends JpaRepository<LoanRepaymentScheduleHistory, Long>, JpaSpecificationExecutor<LoanRepaymentScheduleHistory> {

}
