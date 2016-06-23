/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.paymenttype.service;

import java.util.Collection;

import org.mifosplatform.portfolio.paymenttype.data.PaymentTypeData;

public interface PaymentTypeReadPlatformService {

    /**
     * Retrieve a list of all payment types
     * 
     * @param includeDeletedPaymentTypes if set to true, all payment types including the ones marked as deleted
     * will be returned
     * @return list of {@link PaymentTypeData} objects
     */
    Collection<PaymentTypeData> retrieveAllPaymentTypes(boolean includeDeletedPaymentTypes);
    PaymentTypeData retrieveOne(Long paymentTypeId);

}
