/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.service;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.UnsupportedParameterException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.dataexport.api.DataExportApiConstants;
import org.mifosplatform.infrastructure.dataexport.data.DataExportBaseEntityEnum;
import org.mifosplatform.infrastructure.dataexport.data.DataExportFilter;
import org.mifosplatform.infrastructure.dataexport.data.ExportDataValidator;
import org.mifosplatform.infrastructure.dataexport.domain.DataExport;
import org.mifosplatform.infrastructure.dataexport.domain.DataExportRepository;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DataExportWritePlatformServiceImpl implements DataExportWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(DataExportWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final ExportDataValidator fromApiJsonDeserializer;
    private final DataExportReadPlatformService readPlatformService;
    private final FromJsonHelper fromApiJsonHelper;
    private final DataExportRepository dataExportRepository;
    private final OfficeRepository officeRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public DataExportWritePlatformServiceImpl(final PlatformSecurityContext context, final FromJsonHelper fromApiJsonHelper,
                                              final ExportDataValidator fromApiJsonDeserializer,
                                              final DataExportReadPlatformService readPlatformService,
                                              final DataExportRepository dataExportRepository,
                                              final OfficeRepository officeRepository,
                                              final ClientRepository clientRepository){
        this.context = context;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.readPlatformService = readPlatformService;
        this.dataExportRepository = dataExportRepository;
        this.officeRepository = officeRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult createDataExport(final JsonCommand command) {

        try{
            final AppUser currentUser = this.context.authenticatedUser();

            this.fromApiJsonDeserializer.validateForCreate(command);

            final DataExportBaseEntityEnum entity = DataExportBaseEntityEnum.valueOf(command.stringValueOfParameterNamed(DataExportApiConstants.ENTITY).toUpperCase());
            Map<String,String> paramsToFields;
            final Set<String> supportedParameters = new HashSet<>(DataExportApiConstants.SUPPORTED_PARAMETERS);
            List<String> fieldNames;
            if(entity==null){paramsToFields = null; fieldNames = null;}
            else if(entity.isClient()){
                paramsToFields = DataExportApiConstants.CLIENT_PARAMETERS_TO_FIELD_NAMES;
                fieldNames = DataExportApiConstants.CLIENT_FIELD_NAMES;
            }
            else if(entity.isGroup()){
                paramsToFields = DataExportApiConstants.GROUP_PARAMETERS_TO_FIELD_NAMES;
                fieldNames = DataExportApiConstants.GROUP_FIELD_NAMES;
            }
            else if(entity.isLoan()){
                paramsToFields = DataExportApiConstants.LOAN_PARAMETERS_TO_FIELD_NAMES;
                fieldNames = DataExportApiConstants.LOAN_FIELD_NAMES;
            }
            else if(entity.isSavingsAccount()){
                paramsToFields = DataExportApiConstants.SAVINGS_ACCOUNTS_TO_PARAMETER_FIELD_NAMES;
                fieldNames = DataExportApiConstants.SAVINGS_ACCOUNT_FIELD_NAMES;
            }
            else {throw new InvalidParameterException(entity.name());}

            final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
            this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, command.json(),
                    supportedParameters);

            final Map<String, String> requestMap = this.fromApiJsonHelper.extractDataMap(typeOfMap, command.json());

            final Set<DataExportFilter> dataExportFilterSet = new HashSet<>();
            for (String parameter : requestMap.keySet()){
                String paramValue = requestMap.get(parameter);
                if(paramValue != null && paramValue.length()>0 && !parameter.equals(DataExportApiConstants.ENTITY)){
                    String fieldName = parameter;
                    if(paramsToFields.containsKey(parameter)){fieldName = paramsToFields.get(parameter);}
                    DataExportFilter dataExportFilter = new DataExportFilter(entity.getTablename(),paramValue,fieldName);
                    dataExportFilterSet.add(dataExportFilter);
                }
            }

            Map<String, List<String>> sqlMap = getBaseEntitySql(entity,fieldNames);

            if(dataExportFilterSet.size()>0){
                for(DataExportFilter filter : dataExportFilterSet){
                    sqlMap.get("where").add(filter.getSearchQuery());
                }
            }

            JsonElement element = command.parsedJson();
            final DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern(DataExportApiConstants.SUBMITTEDON_DATE_FORMAT);
            final Date submittedOnDate = dateStringFormat.parseLocalDate(requestMap.get(DataExportApiConstants.ENTITY_SUBMITDATE)).toDate();

            final Long entityId = this.fromApiJsonHelper.extractLongNamed(DataExportApiConstants.ENTITY_ID,element);
            final Integer status = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(DataExportApiConstants.ENTITY_STATUS,element);
            //final Date submittedOnDate = DateUtils.parseLocalDate(requestMap.get(DataExportApiConstants.ENTITY_SUBMITDATE),"dd-MM-yyyy").toDate();
            final String accountNo = this.fromApiJsonHelper.extractStringNamed(DataExportApiConstants.ACCOUNT_NO,element);
            final Long officeId = this.fromApiJsonHelper.extractLongNamed(DataExportApiConstants.ENTITY_OFFICE,element);
            //final Office office = (officeId != null ? this.officeRepository.findOne(officeId) : null);
            final Client client = (entityId != null ? this.clientRepository.findOne(entityId) : null);
            final String displayName = (client != null ? client.getDisplayName() : null);
            final String mobileNo = (client != null ? client.mobileNo() : null);
            final String sql = assembleSqlString(sqlMap);

            DataExport newDataExport = DataExport.instance(entity.getName(),entityId,status,submittedOnDate,accountNo,officeId,displayName,mobileNo,sql);

            this.dataExportRepository.saveAndFlush(newDataExport);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(newDataExport.getId()) //
                    .build();

        }catch(final InvalidJsonException ije){return CommandProcessingResult.empty();
        }catch(final UnsupportedParameterException upe){return CommandProcessingResult.empty();}
    }

    private Map<String, List<String>> getBaseEntitySql (DataExportBaseEntityEnum entity, List<String> fieldNames){
        Map<String,List<String>> sql = new HashMap<>();
        List<String> select = new ArrayList<>();
        List<String> from = new ArrayList<>();
        List<String> where = new ArrayList<>();
        List<String> order = new ArrayList<>();

        for(String fieldName : fieldNames){
            select.add(entity.getTablename() + "." + fieldName);
        }

         from.add(entity.getTablename());

        if(fieldNames.contains(DataExportApiConstants.OFFICE_ID)){
            select.add(DataExportApiConstants.OFFICE_TABLE + "." + DataExportApiConstants.OFFICE_NAME);
            from.add("left join " + DataExportApiConstants.OFFICE_TABLE + " on " + DataExportApiConstants.OFFICE_TABLE + "."
                    + DataExportApiConstants.ENTITY_ID + " = " + entity.getTablename() + "." + DataExportApiConstants.OFFICE_ID);
        }

        sql.put("select",select);
        sql.put("from",from);
        sql.put("where",where);
        sql.put("order by",order);

        return sql;
    }

    private String assembleSqlString (Map<String, List<String>> sqlMap){
        StringBuilder sql = new StringBuilder("");

        for (String key : sqlMap.keySet()){
            if(sqlMap.get(key).size()>0){sql.append(" " + key + " ");}
            for(String param : sqlMap.get(key)){
                if(key.equals("select") && sqlMap.get(key).indexOf(param)>0){sql.append(", ");}
                if(key.equals("from") && sqlMap.get(key).indexOf(param)>0){sql.append(" ");}
                if(key.equals("where") && sqlMap.get(key).indexOf(param)>0){sql.append(" and ");}
                sql.append(param);
            }
        }

        return sql.toString();
    }
}
