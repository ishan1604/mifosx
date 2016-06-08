/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.storeglaccountbalance.service;

import java.io.File;
import java.util.Collection;

import javax.ws.rs.core.MultivaluedMap;

import org.joda.time.LocalDate;
import org.mifosplatform.accounting.closure.storeglaccountbalance.data.GLClosureJournalEntryData;

public interface GLClosureJournalEntryBalanceReadPlatformService {
    Collection<GLClosureJournalEntryData> retrieveAllJournalEntries(Long officeId, LocalDate maxEntryDate);
    File generateGLClosureAccountBalanceReport(MultivaluedMap<String, String> uriQueryParameters);
}
