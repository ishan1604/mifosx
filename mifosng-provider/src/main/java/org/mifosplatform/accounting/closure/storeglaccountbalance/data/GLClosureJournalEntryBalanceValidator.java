/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.storeglaccountbalance.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.accounting.closure.data.GLClosureData;
import org.mifosplatform.accounting.closure.exception.GLClosureNotFoundException;
import org.mifosplatform.accounting.closure.storeglaccountbalance.api.StoreGLAccountBalanceResource;
import org.mifosplatform.accounting.closure.storeglaccountbalance.helper.UriQueryParameterHelper;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.springframework.stereotype.Component;

@Component
public class GLClosureJournalEntryBalanceValidator {
    /**
     * Validates the request to generate a closure journal entry account balance report
     * 
     * @param officeId
     * @param startClosure
     * @param endClosure
     */
    public void validateGenerateReportRequest(final Long officeId, final GLClosureData startClosure, 
            final GLClosureData endClosure) {
        
        final Long endClosureId = (endClosure != null) ? endClosure.getId() : null;
        
        final String resourceNameToLowerCase = StringUtils.lowerCase(StoreGLAccountBalanceResource.
                CLOSURE_ACCOUNT_BALANCE_REPORT_ENTITY_NAME);
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder dataValidatorBuilder = new DataValidatorBuilder(dataValidationErrors).
                resource(resourceNameToLowerCase);
        
        dataValidatorBuilder.reset().parameter(UriQueryParameterHelper.OFFICE_ID_PARAMETER_NAME).
                value(officeId).notBlank();
        dataValidatorBuilder.reset().parameter(UriQueryParameterHelper.END_CLOSURE_ID_PARAMETER_NAME).
                value(endClosureId).notBlank();
        
        if (startClosure != null && startClosure.isDeleted()) {
            throw new GLClosureNotFoundException(startClosure.getId());
        }
        
        if (endClosure != null && endClosure.isDeleted()) {
            throw new GLClosureNotFoundException(endClosureId);
        }
        
        if (startClosure != null && endClosure != null) {
            final LocalDate startClosureClosingDate = startClosure.getClosingDate();
            final LocalDate endClosureClosingDate = endClosure.getClosingDate();
            
            if (endClosureClosingDate.isBefore(startClosureClosingDate)) {
                dataValidatorBuilder.failWithCodeNoParameterAddedToErrorCode("error.msg." + resourceNameToLowerCase
                        + ".end.closure.closing.date.cannot.be.before.start.closure.closing.date", "Closing "
                                + "date of end closure must be after closing date of start closure.");
            }
            
            if (startClosure.getId().equals(endClosure.getId())) {
                dataValidatorBuilder.failWithCodeNoParameterAddedToErrorCode("error.msg." + resourceNameToLowerCase
                        + ".end.closure.cannot.be.equal.to.start.closure", "End closure cannot be equal "
                                + "to start closure.");
            }
        }
        
        if (officeId != null && startClosure != null && !startClosure.getOfficeId().equals(officeId)) {
            dataValidatorBuilder.failWithCodeNoParameterAddedToErrorCode("error.msg." + resourceNameToLowerCase 
                    + ".start.closure.office.id.must.be.equal.to.provided.office.id", "The start closure "
                            + "office id is different from provided office id");
        }
        
        if (officeId != null && endClosure != null && !endClosure.getOfficeId().equals(officeId)) {
            dataValidatorBuilder.failWithCodeNoParameterAddedToErrorCode("error.msg." + resourceNameToLowerCase 
                    + ".end.closure.office.id.must.be.equal.to.provided.office.id", "The end closure "
                            + "office id is different from provided office id");
        }
        
        // throw data validation exception if there are any validation errors 
        this.throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    /** 
     * throw a PlatformApiDataValidationException exception if there are validation errors
     * 
     * @param dataValidationErrors -- list of ApiParameterError objects
     * @return None
     **/
    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { 
            throw new PlatformApiDataValidationException(dataValidationErrors); 
        }
    }
}
