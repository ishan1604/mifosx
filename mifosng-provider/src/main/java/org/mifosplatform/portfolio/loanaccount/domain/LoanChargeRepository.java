/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanChargeRepository extends JpaRepository<LoanCharge, Long>, JpaSpecificationExecutor<LoanCharge> {
    /** 
     * Get a collection of LoanCharge objects by loan ID
     * 
     * @param loanId ID of a loan
     * @return collection of LoanCharge objects with loan ID similar to the one passed 
     **/
	Collection<LoanCharge> findByLoanId(Long loanId);
}
