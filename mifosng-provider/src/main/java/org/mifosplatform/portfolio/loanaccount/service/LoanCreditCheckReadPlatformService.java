/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.service;

import java.util.Collection;

import org.mifosplatform.portfolio.loanaccount.data.LoanAccountData;
import org.mifosplatform.portfolio.loanaccount.data.LoanCreditCheckData;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;

public interface LoanCreditCheckReadPlatformService {
    Collection<LoanCreditCheckData> retrieveLoanCreditChecks(Long loanId);
    LoanCreditCheckData retrieveLoanCreditCheckEnumOptions();
    LoanCreditCheckData retrieveLoanCreditCheck(Long loanId, Long loanCreditCheckId);
    Collection<LoanCreditCheckData> triggerLoanCreditChecks(Loan loan);
    Collection<LoanCreditCheckData> triggerLoanCreditChecks(LoanAccountData loanAccountData);
}
