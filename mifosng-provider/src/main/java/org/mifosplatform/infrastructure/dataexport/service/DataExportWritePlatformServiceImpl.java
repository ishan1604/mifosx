/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.service;


import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.dataexport.api.DataExportApiConstants;
import org.mifosplatform.infrastructure.dataexport.data.DataExportBaseEntityEnum;
import org.mifosplatform.infrastructure.dataexport.data.DataExportFilter;
import org.mifosplatform.infrastructure.dataexport.data.ExportDataValidator;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.lang.reflect.Type;
import java.util.*;

@Service
public class DataExportWritePlatformServiceImpl implements DataExportWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(DataExportWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final ExportDataValidator fromApiJsonDeserializer;
    private final DataExportReadPlatformService readPlatformService;
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public DataExportWritePlatformServiceImpl(final PlatformSecurityContext context, final FromJsonHelper fromApiJsonHelper,
                                              final ExportDataValidator fromApiJsonDeserializer,
                                              final DataExportReadPlatformService readPlatformService){
        this.context = context;
        this.fromApiJsonHelper = fromApiJsonHelper;
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
            if(entity==null){parameters = null;}
            else if(entity.isClient()){parameters = DataExportApiConstants.CLIENT_FIELD_NAMES;}
            else if(entity.isGroup()){parameters = DataExportApiConstants.GROUP_FIELD_NAMES;}
            else if(entity.isLoan()){parameters = DataExportApiConstants.LOAN_FIELD_NAMES;}
            else if(entity.isSavingsAccount()){parameters = DataExportApiConstants.SAVINGS_ACCOUNT_FIELD_NAMES;}
            else {throw new InvalidParameterException(entity.name());}

            final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
            this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, command.json(), new HashSet<>(parameters));

            final JsonElement element = this.fromApiJsonHelper.parse(command.json());

            final Set<DataExportFilter> dataExportFilterSet = new HashSet<>();
            for (String parameter : parameters){
                String paramValue = command.jsonFragment(parameter);
                if(paramValue.length()>0){
                    DataExportFilter dataExportFilter = new DataExportFilter(entity.getTablename(),paramValue,parameter);
                    dataExportFilterSet.add(dataExportFilter);
                }
            }

            Map<String, List<String>> sql = getBaseEntitySql(entity,parameters);
            if(dataExportFilterSet.size()>0){
                for(DataExportFilter filter : dataExportFilterSet){
                    sql.get("where").add(filter.getSearchQuery());
                }
            }

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
            select.add(entity.getTablename() + "." + parameter);
        }

         from.add(entity.getTablename());

        if(parameters.contains(DataExportApiConstants.OFFICE_ID)){
            select.add(DataExportApiConstants.OFFICE_TABLE + "." + DataExportApiConstants.OFFICE_NAME);
            from.add(DataExportApiConstants.OFFICE_TABLE);
        }

        sql.put("select",select);
        sql.put("from",from);
        sql.put("where",where);
        sql.put("order",order);

        return sql;
    }
}
