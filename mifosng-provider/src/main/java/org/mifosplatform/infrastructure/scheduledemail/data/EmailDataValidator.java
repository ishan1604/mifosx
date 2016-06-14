/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.scheduledemail.data;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.scheduledemail.EmailApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public final class EmailDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public EmailDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, EmailApiConstants.CREATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(EmailApiConstants.RESOURCE_NAME);

        if (this.fromApiJsonHelper.parameterExists(EmailApiConstants.groupIdParamName, element)) {
            final Long groupId = this.fromApiJsonHelper.extractLongNamed(EmailApiConstants.groupIdParamName, element);
            baseDataValidator.reset().parameter(EmailApiConstants.groupIdParamName).value(groupId).notNull().integerGreaterThanZero();

            // ensure clientId and staffId are not passed
            if (this.fromApiJsonHelper.parameterExists(EmailApiConstants.clientIdParamName, element)) {
                baseDataValidator.reset().parameter(EmailApiConstants.clientIdParamName).failWithCode("cannot.be.passed.with.groupId");
            }

            if (this.fromApiJsonHelper.parameterExists(EmailApiConstants.staffIdParamName, element)) {
                baseDataValidator.reset().parameter(EmailApiConstants.staffIdParamName).failWithCode("cannot.be.passed.with.groupId");
            }
        } else if (this.fromApiJsonHelper.parameterExists(EmailApiConstants.clientIdParamName, element)) {
            final Long clientId = this.fromApiJsonHelper.extractLongNamed(EmailApiConstants.clientIdParamName, element);
            baseDataValidator.reset().parameter(EmailApiConstants.clientIdParamName).value(clientId).notNull().integerGreaterThanZero();

            // ensure groupId and staffId are not passed
            if (this.fromApiJsonHelper.parameterExists(EmailApiConstants.groupIdParamName, element)) {
                baseDataValidator.reset().parameter(EmailApiConstants.groupIdParamName).failWithCode("cannot.be.passed.with.clientId");
            }

            if (this.fromApiJsonHelper.parameterExists(EmailApiConstants.staffIdParamName, element)) {
                baseDataValidator.reset().parameter(EmailApiConstants.staffIdParamName).failWithCode("cannot.be.passed.with.clientId");
            }
        } else if (this.fromApiJsonHelper.parameterExists(EmailApiConstants.staffIdParamName, element)) {
            final Long staffId = this.fromApiJsonHelper.extractLongNamed(EmailApiConstants.staffIdParamName, element);
            baseDataValidator.reset().parameter(EmailApiConstants.staffIdParamName).value(staffId).ignoreIfNull().longGreaterThanZero();

            // ensure groupId and clientId are not passed
            if (this.fromApiJsonHelper.parameterExists(EmailApiConstants.groupIdParamName, element)) {
                baseDataValidator.reset().parameter(EmailApiConstants.groupIdParamName).failWithCode("cannot.be.passed.with.staffId");
            }

            if (this.fromApiJsonHelper.parameterExists(EmailApiConstants.clientIdParamName, element)) {
                baseDataValidator.reset().parameter(EmailApiConstants.clientIdParamName).failWithCode("cannot.be.passed.with.staffId");
            }
        }

        if (!this.fromApiJsonHelper.parameterExists(EmailApiConstants.groupIdParamName, element)
                && !this.fromApiJsonHelper.parameterExists(EmailApiConstants.clientIdParamName, element)
                && !this.fromApiJsonHelper.parameterExists(EmailApiConstants.staffIdParamName, element)) {
            baseDataValidator.reset().parameter(EmailApiConstants.staffIdParamName)
                    .failWithCodeNoParameterAddedToErrorCode("no.entity.provided");
        }

        final String message = this.fromApiJsonHelper.extractStringNamed(EmailApiConstants.messageParamName, element);
        baseDataValidator.reset().parameter(EmailApiConstants.messageParamName).value(message).notBlank().notExceedingLengthOf(1000);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, EmailApiConstants.UPDATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(EmailApiConstants.RESOURCE_NAME);

        if (this.fromApiJsonHelper.parameterExists(EmailApiConstants.messageParamName, element)) {
            final String message = this.fromApiJsonHelper.extractStringNamed(EmailApiConstants.messageParamName, element);
            baseDataValidator.reset().parameter(EmailApiConstants.messageParamName).value(message).notBlank().notExceedingLengthOf(1000);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}