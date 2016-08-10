/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.rescheduleloan.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Represents the "waive loan charge" request. The properties of this class are similar to the parameters that are
 * passed in an API request to waive a loan charge.
 */
public class WaiveLoanChargeRequest {
    @SuppressWarnings("unused")
    private final String dueDate;
    @SuppressWarnings("unused")
    private final String locale;
    @SuppressWarnings("unused")
    private final String dateFormat;
    @SuppressWarnings("unused")
    private final Integer installmentNumber;
    
    /**
     * @param dueDate
     * @param locale
     * @param dateFormat
     * @param installmentNumber
     */
    private WaiveLoanChargeRequest(final String dueDate, final String locale, final String dateFormat, 
            final Integer installmentNumber) {
        this.dueDate = dueDate;
        this.locale = locale;
        this.dateFormat = dateFormat;
        this.installmentNumber = installmentNumber;
    }
    
    /**
     * Creates a new {@link WaiveLoanChargeRequest} object
     * 
     * @param dueDate
     * @param locale
     * @param dateFormat
     * @param installmentNumber
     * @return {@link WaiveLoanChargeRequest} object
     */
    public static WaiveLoanChargeRequest newInstance(final String dueDate, final String locale, final String dateFormat, 
            final Integer installmentNumber) {
        return new WaiveLoanChargeRequest(dueDate, locale, dateFormat, installmentNumber);
    }
    
    /**
     * serializes {@link WaiveLoanChargeRequest} into its equivalent Json representation
     * 
     * @return Json representation of {@link WaiveLoanChargeRequest}
     */
    public String toJson() {
        Gson gson = new Gson();
        
        return gson.toJson(this);
    }
    
    /**
     * converts to a class representing an element of Json.
     * 
     * @return {@link JsonElement} object
     */
    public JsonElement toJsonElement() {
        Gson gson = new Gson();
        
        return gson.toJsonTree(this);
    }
}
