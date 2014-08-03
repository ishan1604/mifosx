/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, Long>, JpaSpecificationExecutor<LoanTransaction> {

    // no added behaviour

    public static final String FIND_ALL_TRANSACTIONS_AFTER_CLIENT_TRANSFER = "from LoanTransaction t1 where t1.loan.id = :loanId and t1.reversed=false and " +
            "t1.id > (select max(t2.id) from LoanTransaction t2 where t2.loan.id = :loanId and t2.typeOf = :enumType and t2.reversed=false)";

    public static final String FIND_THE_CURRENT_TRANSFER_TRANSACTIONS = "select * from m_loan_transaction t1 where t1.loan_id = :loanId and " +
            "t1.transaction_type_enum in (12,13) and t1.is_reversed=0 order by t1.id desc limit 2";

    public static final String FIND_THE_LAST_TRANSFER_TRANSACTIONS = "select * from m_loan_transaction t1 where t1.loan_id = :loanId and " +
            "t1.transaction_type_enum in (13) and t1.is_reversed=0 order by t1.id desc limit 1";

    @Query(value=FIND_THE_CURRENT_TRANSFER_TRANSACTIONS,nativeQuery = true)
    List<LoanTransaction> currentTransferTransaction(@Param("loanId") Long loanId);

    @Query(value=FIND_THE_LAST_TRANSFER_TRANSACTIONS,nativeQuery = true)
    LoanTransaction lastApprovedTransfer(@Param("loanId") Long loanId);

    @Query(FIND_ALL_TRANSACTIONS_AFTER_CLIENT_TRANSFER)
    List<LoanTransaction> transactionsAfterClientTransfer(@Param("loanId") Long loanId,@Param("enumType") Integer enumType);

}