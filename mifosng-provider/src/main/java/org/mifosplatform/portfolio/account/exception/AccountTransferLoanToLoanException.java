/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.account.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;


public class AccountTransferLoanToLoanException extends AbstractPlatformResourceNotFoundException {
    public AccountTransferLoanToLoanException(final Long id) {
        super("error.msg.accounttransfer.loan.to.loan.not.supported", "Account transfer from loan with identifier "+ id  +" to another loan is not supported");
    }

}
