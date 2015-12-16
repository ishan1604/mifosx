/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.bookoffincomeandexpense.service;

import org.mifosplatform.accounting.closure.bookoffincomeandexpense.data.IncomeAndExpenseBookingData;
import org.mifosplatform.infrastructure.core.api.JsonQuery;

import java.util.Collection;


public interface CalculateIncomeAndExpenseBooking {
    Collection<IncomeAndExpenseBookingData> CalculateIncomeAndExpenseBookings(JsonQuery query);
}
