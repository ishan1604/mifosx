/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.service;

import org.mifosplatform.portfolio.collateral.data.CollateralData;
import org.mifosplatform.portfolio.loanaccount.data.GroupLoanMembersAllocationData;

import java.util.List;

public interface GroupLoanMembersAllocationReadPlatformService {

    /**
     * Validates the passed in loanId before retrieving Collaterals for the same
     * 
     * @param loanId
     * @return
     */

    List<GroupLoanMembersAllocationData> retrieveGroupLoanMembersAllocation(Long loanId);


}
