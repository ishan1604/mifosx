/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.storeglaccountbalance.service;

import org.mifosplatform.accounting.closure.bookoffincomeandexpense.domain.IncomeAndExpenseBooking;
import org.mifosplatform.accounting.closure.domain.GLClosure;

public interface GLClosureJournalEntryBalanceWritePlatformService {
    public void storeJournalEntryRunningBalance(final GLClosure glClosure, 
            final IncomeAndExpenseBooking incomeAndExpenseBooking);
    public void deleteJournalEntryRunningBalances(final GLClosure glClosure);
}
