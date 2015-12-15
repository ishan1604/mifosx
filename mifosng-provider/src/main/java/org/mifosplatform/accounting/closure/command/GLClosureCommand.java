/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.command;

import org.joda.time.LocalDate;
import org.mifosplatform.accounting.closure.api.GLClosureJsonInputParams;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable command for adding an accounting closure
 */
public class GLClosureCommand {

    @SuppressWarnings("unused")
    private final Long id;
    private final Long officeId;
    private final LocalDate closingDate;
    private final String comments;
    private final Boolean bookOffIncomeAndExpense;
    private final Long equityGlAccountId;
    private final String currencyCode;
    private final Boolean reverseIncomeAndExpenseBooking;
    private final Boolean subBranches;
    private final String incomeAndExpenseComments;




    public GLClosureCommand(final Long id, final Long officeId, final LocalDate closingDate, final String comments,
                            final Boolean bookOffIncomeAndExpense, final Long equityGlAccountId,
                            final String currencyCode,final Boolean reverseIncomeAndExpenseBooking,
                            final Boolean subBranches,final String incomeAndExpenseComments) {
        this.id = id;
        this.officeId = officeId;
        this.closingDate = closingDate;
        this.comments = comments;
        if(bookOffIncomeAndExpense == null){
            this.bookOffIncomeAndExpense = Boolean.FALSE;
        }else{this.bookOffIncomeAndExpense = bookOffIncomeAndExpense;}
        this.equityGlAccountId = equityGlAccountId;
        this.currencyCode = currencyCode;
        if(reverseIncomeAndExpenseBooking == null){ this.reverseIncomeAndExpenseBooking = Boolean.FALSE;}
        else{this.reverseIncomeAndExpenseBooking = reverseIncomeAndExpenseBooking;}
        if(subBranches == null){ this.subBranches = Boolean.FALSE;}
        else{ this.subBranches = subBranches;}
        this.incomeAndExpenseComments = incomeAndExpenseComments;
    }

    public void validateForCreate() {

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("GLClosure");

        baseDataValidator.reset().parameter(GLClosureJsonInputParams.CLOSING_DATE.getValue()).value(this.closingDate).notBlank();
        baseDataValidator.reset().parameter(GLClosureJsonInputParams.OFFICE_ID.getValue()).value(this.officeId).notNull()
                .integerGreaterThanZero();
        baseDataValidator.reset().parameter(GLClosureJsonInputParams.COMMENTS.getValue()).value(this.comments).ignoreIfNull()
                .notExceedingLengthOf(500);
        if(this.bookOffIncomeAndExpense){
            baseDataValidator.reset().parameter(GLClosureJsonInputParams.EQUITY_GL_ACCOUNT_ID.getValue()).value(this.equityGlAccountId).notNull();
            baseDataValidator.reset().parameter(GLClosureJsonInputParams.CURRENCY_CODE.getValue()).value(this.currencyCode).notNull();
            baseDataValidator.reset().parameter(GLClosureJsonInputParams.COMMENTS.getValue()).value(this.incomeAndExpenseComments).ignoreIfNull()
                    .notExceedingLengthOf(500);
        }

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    public void validateForUpdate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("GLClosure");

        baseDataValidator.reset().parameter(GLClosureJsonInputParams.COMMENTS.getValue()).value(this.comments).ignoreIfNull()
                .notExceedingLengthOf(500);
        baseDataValidator.reset().anyOfNotNull(this.comments);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }


    public Long getOfficeId() {return this.officeId;}

    public LocalDate getClosingDate() {return this.closingDate;}

    public String getComments() {return this.comments;}

    public Boolean getBookOffIncomeAndExpense() {return this.bookOffIncomeAndExpense;}

    public Long getEquityGlAccountId() {return this.equityGlAccountId;}

    public String getCurrencyCode() {return this.currencyCode;}

    public Boolean getSubBranches() {return this.subBranches;}

    public Boolean getReverseIncomeAndExpenseBooking() {return this.reverseIncomeAndExpenseBooking;}

    public String getIncomeAndExpenseComments() {return this.incomeAndExpenseComments;}
}