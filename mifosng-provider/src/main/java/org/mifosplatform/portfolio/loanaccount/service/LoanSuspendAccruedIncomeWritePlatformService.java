/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.service;

import org.mifosplatform.portfolio.loanaccount.domain.Loan;

public interface LoanSuspendAccruedIncomeWritePlatformService {
    /**
     * Suspends accrued income when a loan goes into NPA (non-performing assets)
     */
    void suspendAccruedIncome();

    /**
     * reverse booking when out of npa. This is the corrected booking
     * This booking is made when a repayment is made and suspended interest,penalty or charges is present
     * @param loan
     */
    void suspendedIncomeOutOfNPA(Loan loan);

    /**
     * when repayment is reverse and loan is back to npa reverse the remaining corrected booking that
     * corrects all suspended income. The booking is of the transaction type 20
     * @param loan
     */
    void reverseSuspendedIncomeWhenRepaymentIsReversed(Loan loan,Long transactionId);
}
