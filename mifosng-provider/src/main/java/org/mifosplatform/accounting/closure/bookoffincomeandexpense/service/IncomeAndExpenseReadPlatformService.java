/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.bookoffincomeandexpense.service;


import org.joda.time.LocalDate;
import org.mifosplatform.accounting.closure.bookoffincomeandexpense.data.IncomeAndExpenseJournalEntryData;

import java.util.List;

public interface IncomeAndExpenseReadPlatformService {
    List<IncomeAndExpenseJournalEntryData> retrieveAllIncomeAndExpenseJournalEntryData(Long officeId,LocalDate date,String currencyCode);
}
