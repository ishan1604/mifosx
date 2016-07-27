/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.data;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.dataexport.api.DataExportApiConstants;
import org.mifosplatform.infrastructure.dataexport.service.DataExportReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ExportDataValidator {

    private final FromJsonHelper fromApiJsonHelper;
    private final DataExportReadPlatformService readPlatformService;

    @Autowired
    public ExportDataValidator(final FromJsonHelper fromApiJsonHelper,final DataExportReadPlatformService readPlatformService) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.readPlatformService = readPlatformService;
    }

    public void validateForCreate(JsonCommand command){
        final String json = command.json();
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final JsonElement element = this.fromApiJsonHelper.parse(json);
        final String entityName = this.fromApiJsonHelper.extractStringNamed(DataExportApiConstants.ENTITY,element);
        final DataExportRequestData requestData = this.readPlatformService.retrieveDataExportRequestData(entityName);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();


        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, requestData.getSupportedParameters());

    }
}
