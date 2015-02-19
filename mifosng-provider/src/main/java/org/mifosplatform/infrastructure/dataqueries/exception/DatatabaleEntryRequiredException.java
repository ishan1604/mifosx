/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.exception;

import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link RuntimeException} thrown when datatable resources are not found.
 */
public class DatatabaleEntryRequiredException extends RuntimeException {


    public DatatabaleEntryRequiredException(final String datatableName) {

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final String defaultMessage = "The datatable " + datatableName + " needs to be filled in before the current action can be proceeded";
        final String messageCode = "error.msg.entry.required.in.datatable."+datatableName;
        final ApiParameterError error = ApiParameterError.parameterError(messageCode,
                defaultMessage,"", 1, 0);
        dataValidationErrors.add(error);

        throw new PlatformApiDataValidationException(dataValidationErrors);
    }

}