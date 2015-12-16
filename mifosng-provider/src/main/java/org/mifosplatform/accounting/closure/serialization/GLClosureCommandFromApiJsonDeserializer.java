/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.serialization;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.accounting.closure.api.GLClosureJsonInputParams;
import org.mifosplatform.accounting.closure.command.GLClosureCommand;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.serialization.AbstractFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.serialization.FromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.loanaccount.guarantor.command.GuarantorCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link FromApiJsonDeserializer} for
 * {@link GuarantorCommand}'s.
 */
@Component
public final class GLClosureCommandFromApiJsonDeserializer extends AbstractFromApiJsonDeserializer<GLClosureCommand> {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public GLClosureCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonfromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonfromApiJsonHelper;
    }

    @Override
    public GLClosureCommand commandFromApiJson(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        final Set<String> supportedParameters = GLClosureJsonInputParams.getAllValues();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final Long id = this.fromApiJsonHelper.extractLongNamed(GLClosureJsonInputParams.ID.getValue(), element);
        final Long officeId = this.fromApiJsonHelper.extractLongNamed(GLClosureJsonInputParams.OFFICE_ID.getValue(), element);
        final String comments = this.fromApiJsonHelper.extractStringNamed(GLClosureJsonInputParams.COMMENTS.getValue(), element);
        final LocalDate closingDate = this.fromApiJsonHelper.extractLocalDateNamed(GLClosureJsonInputParams.CLOSING_DATE.getValue(),
                element);
        final Boolean bookOffIncomeAndExpense = this.fromApiJsonHelper.extractBooleanNamed(GLClosureJsonInputParams.BOOK_OFF_INCOME_AND_EXPENSE.getValue(),element);
        final Long equityGlAccountId = this.fromApiJsonHelper.extractLongNamed(GLClosureJsonInputParams.EQUITY_GL_ACCOUNT_ID.getValue(),element);
        final String currencyCode = this.fromApiJsonHelper.extractStringNamed(GLClosureJsonInputParams.CURRENCY_CODE.getValue(), element);
        final Boolean subBranches = this.fromApiJsonHelper.extractBooleanNamed(GLClosureJsonInputParams.SUB_BRANCHES.getValue(),element);
        final Boolean reverseIncomeAndExpenseBooking = this.fromApiJsonHelper.extractBooleanNamed(GLClosureJsonInputParams.REVERSE_INCOME_AND_EXPENSE_BOOKING.getValue(),element);
        final String incomeAndExpenseComments   = this.fromApiJsonHelper.extractStringNamed(GLClosureJsonInputParams.INCOME_AND_EXPENSE_COMMENTS.getValue(),element);

        return new GLClosureCommand(id, officeId, closingDate, comments,bookOffIncomeAndExpense,equityGlAccountId,currencyCode,reverseIncomeAndExpenseBooking,subBranches,incomeAndExpenseComments);
    }
}