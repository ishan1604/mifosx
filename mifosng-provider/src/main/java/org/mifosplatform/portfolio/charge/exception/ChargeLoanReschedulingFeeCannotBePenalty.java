/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.charge.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/** 
 * Creating a "loan rescheduling fee" charge as penalty is not allowed
 * 
 * {@link AbstractPlatformDomainRuleException} 
 **/
@SuppressWarnings("serial")
public class ChargeLoanReschedulingFeeCannotBePenalty extends AbstractPlatformDomainRuleException {

    public ChargeLoanReschedulingFeeCannotBePenalty(final String name) {
        super("error.msg.charge.loan.rescheduling.cannot.be.penalty", "Charge '" + name + "' is invalid.", name, name);
    }

}
