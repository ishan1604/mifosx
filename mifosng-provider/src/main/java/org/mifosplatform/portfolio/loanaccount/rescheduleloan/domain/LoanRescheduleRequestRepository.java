package org.mifosplatform.portfolio.loanaccount.rescheduleloan.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanRescheduleRequestRepository extends JpaRepository<LoanRescheduleRequest, Long>, JpaSpecificationExecutor<LoanRescheduleRequest> {

}
