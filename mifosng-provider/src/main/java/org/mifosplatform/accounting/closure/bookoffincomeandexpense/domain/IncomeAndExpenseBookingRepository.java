/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.bookoffincomeandexpense.domain;

import org.mifosplatform.accounting.closure.domain.GLClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface IncomeAndExpenseBookingRepository  extends JpaRepository<IncomeAndExpenseBooking, Long>, JpaSpecificationExecutor<IncomeAndExpenseBooking> {
   IncomeAndExpenseBooking findByGlClosureAndReversedIsFalse(final GLClosure glClosure);
}
