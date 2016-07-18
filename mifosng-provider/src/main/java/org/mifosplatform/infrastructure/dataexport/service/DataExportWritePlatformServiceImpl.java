/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.service;


import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.dataexport.api.DataExportApiConstants;
import org.mifosplatform.infrastructure.dataexport.data.DataExportBaseEntityEnum;
import org.mifosplatform.infrastructure.dataexport.data.ExportDataValidator;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataExportWritePlatformServiceImpl implements DataExportWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(DataExportWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final ExportDataValidator fromApiJsonDeserializer;
    private final DataExportReadPlatformService readPlatformService;

    @Autowired
    public DataExportWritePlatformServiceImpl(final PlatformSecurityContext context,
                                              final ExportDataValidator fromApiJsonDeserializer,
                                              final DataExportReadPlatformService readPlatformService){
        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.readPlatformService = readPlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult createDataExport(final JsonCommand command) {

        try{
            final AppUser currentUser = this.context.authenticatedUser();

            this.fromApiJsonDeserializer.validateForCreate(command);

            DataExportBaseEntityEnum entity = DataExportBaseEntityEnum.valueOf(command.stringValueOfParameterNamed(DataExportApiConstants.ENTITY));
            List<String> parameters;
            if(entity.equals(DataExportBaseEntityEnum.CLIENT)){parameters = DataExportApiConstants.CLIENT_FIELD_NAMES;}
            else if(entity.equals(DataExportBaseEntityEnum.GROUP)){parameters = null;}
            else if(entity.equals(DataExportBaseEntityEnum.LOAN)){parameters = null;}
            else if(entity.equals(DataExportBaseEntityEnum.SAVINGSACCOUNT)){parameters = null;}
            else {parameters = null;}

            Map<String, List<String>> sql = getBaseEntitySql(entity,parameters);

            return CommandProcessingResult.commandOnlyResult(command.commandId());

        }catch(final Exception e){return CommandProcessingResult.empty();}
    }

    private Map<String, List<String>> getBaseEntitySql (DataExportBaseEntityEnum entity, List<String> parameters){
        Map<String,List<String>> sql = new HashMap<>();
        List<String> select = new ArrayList<>();
        List<String> from = new ArrayList<>();
        List<String> where = new ArrayList<>();
        List<String> order = new ArrayList<>();

        for(String parameter : parameters){
            select.add(parameter);
        }

         from.add(entity.getTablename());

        sql.put("select",select);
        sql.put("from",from);
        sql.put("where",where);
        sql.put("order",order);

        return sql;
    }
}
