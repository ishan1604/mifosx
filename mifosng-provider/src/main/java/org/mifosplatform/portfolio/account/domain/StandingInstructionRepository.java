/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.account.domain;

import java.util.Collection;

import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.savings.domain.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StandingInstructionRepository extends JpaRepository<AccountTransferStandingInstruction, Long>,
        JpaSpecificationExecutor<AccountTransferStandingInstruction> {
    
    public final static String FIND_BY_LOAN_AND_STATUS_QUERY = "select accountTransferStandingInstruction "
            + "from AccountTransferStandingInstruction accountTransferStandingInstruction "
            + "where accountTransferStandingInstruction.status = :status "
            + "and (accountTransferStandingInstruction.accountTransferDetails.toLoanAccount = :loan "
            + "or accountTransferStandingInstruction.accountTransferDetails.fromLoanAccount = :loan)";
    
    public final static String FIND_BY_SAVINGS_AND_STATUS_QUERY = "select accountTransferStandingInstruction "
            + "from AccountTransferStandingInstruction accountTransferStandingInstruction "
            + "where accountTransferStandingInstruction.status = :status "
            + "and (accountTransferStandingInstruction.accountTransferDetails.toSavingsAccount = :savingsAccount "
            + "or accountTransferStandingInstruction.accountTransferDetails.fromSavingsAccount = :savingsAccount)";
    
    public final static String FIND_ONE_BY_NAME_QUERY = "select accountTransferStandingInstruction "
            + "from AccountTransferStandingInstruction accountTransferStandingInstruction "
            + "where accountTransferStandingInstruction.name = :name ";
    
    @Query(FIND_BY_LOAN_AND_STATUS_QUERY)
    public Collection<AccountTransferStandingInstruction> findByLoanAccountAndStatus(@Param("loan") Loan loan, @Param("status") Integer status);
    
    @Query(FIND_BY_SAVINGS_AND_STATUS_QUERY)
    public Collection<AccountTransferStandingInstruction> findBySavingsAccountAndStatus(@Param("savingsAccount") SavingsAccount savingsAccount, @Param("status") Integer status);
    
    @Query(FIND_ONE_BY_NAME_QUERY)
    public AccountTransferStandingInstruction findOneByName(@Param("name") String name);
}