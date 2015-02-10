/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.charge.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/** 
 * Creating a "disbursement - paid with repayment" charge as penalty is not allowed
 * 
 * {@link AbstractPlatformDomainRuleException} 
 **/
public class ChargeDisbursementPaidWithRepaymentCannotBePenaltyException extends AbstractPlatformDomainRuleException {

	public ChargeDisbursementPaidWithRepaymentCannotBePenaltyException(final String name) {
        super("error.msg.charge.disbursement.paid.with.repayment.cannot.be.penalty", "Charge '" + name + "' is invalid.", name, name);
    }
}
