/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.bookoffincomeandexpense.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class RunningBalanceZeroException extends AbstractPlatformDomainRuleException {
    public RunningBalanceZeroException(final String officeName) {
        super("error.msg.running.balance.is.zero", officeName + " running balance is zero");
    }
}
