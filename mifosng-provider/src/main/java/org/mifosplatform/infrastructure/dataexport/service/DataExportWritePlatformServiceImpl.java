/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.service;


import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.hibernate.JDBCException;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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
import org.mifosplatform.infrastructure.dataexport.data.*;
import org.mifosplatform.infrastructure.dataexport.domain.*;
import org.mifosplatform.infrastructure.hooks.data.Entity;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.group.domain.Group;
import org.mifosplatform.portfolio.group.domain.GroupRepository;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepository;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountRepository;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.EntityNotFoundException;
import javax.persistence.JoinColumn;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.*;

import static org.mifosplatform.infrastructure.dataexport.api.DataExportApiConstants.DATA_EXPORT_FILENAME_DATETIME_FORMAT_PATTERN;

@Service
public class DataExportWritePlatformServiceImpl implements DataExportWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(DataExportWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final ExportDataValidator fromApiJsonDeserializer;
    private final DataExportReadPlatformService readPlatformService;
    private final FromJsonHelper fromApiJsonHelper;
    private final DataExportRepository dataExportRepository;
    private final DataExportProcessRepository dataExportProcessRepository;
    private final GroupRepository groupRepository;
    private final ClientRepository clientRepository;
    private final EntityLabelRepository entityLabelRepository;

    @Autowired
    public DataExportWritePlatformServiceImpl(final PlatformSecurityContext context, final FromJsonHelper fromApiJsonHelper,
                                              final ExportDataValidator fromApiJsonDeserializer,
                                              final DataExportReadPlatformService readPlatformService,
                                              final DataExportRepository dataExportRepository,
                                              final DataExportProcessRepository dataExportProcessRepository,
                                              final ClientRepository clientRepository,
                                              final GroupRepository groupRepository,
                                              final EntityLabelRepository entityLabelRepository){
        this.context = context;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.readPlatformService = readPlatformService;
        this.dataExportRepository = dataExportRepository;
        this.clientRepository = clientRepository;
        this.dataExportProcessRepository = dataExportProcessRepository;
        this.groupRepository = groupRepository;
        this.entityLabelRepository = entityLabelRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult createDataExport(final JsonCommand command) {

        try{
            final AppUser currentUser = this.context.authenticatedUser();

            this.fromApiJsonDeserializer.validateForCreate(command);

            JsonElement element = command.parsedJson();

            final String entityName = this.fromApiJsonHelper.extractStringNamed(DataExportApiConstants.ENTITY,element);
            final DataExportRequestData requestData = this.readPlatformService.retrieveDataExportRequestData(entityName);
            final DataExportBaseEntityEnum entity = requestData.getBaseEntity();

            final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
            this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, command.json(),
                    requestData.getSupportedParameters());

            final Map<String, String> jsonRequestMap = this.fromApiJsonHelper.extractDataMap(typeOfMap, command.json());

            for (String paramKey : jsonRequestMap.keySet()){
                String paramValue = jsonRequestMap.get(paramKey);
                if(paramValue != null && paramValue.length()>0 && !paramKey.equals(DataExportApiConstants.ENTITY)){
                    String fieldName = paramKey;
                    EntityLabel label = this.entityLabelRepository.findOneByTableAndJsonParam(entity.getTablename(),paramValue);
                    if(label != null){
                        fieldName = label.getField();
                    }
                    DataExportFilter dataExportFilter = new DataExportFilter(entity.getTablename(),paramValue,fieldName);
                    requestData.addDataExportFilter(dataExportFilter);
                }
            }

            final Map<String, List<String>> sqlMap = getBaseEntitySql(requestData);

            final DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern(DataExportApiConstants.SUBMITTEDON_DATE_FORMAT);
            final String submitDate = this.fromApiJsonHelper.extractStringNamed(DataExportApiConstants.ENTITY_SUBMITDATE,element);
            final Date submittedOnDate = (submitDate != null && submitDate.length()>0 ? dateStringFormat.parseLocalDate(submitDate).toDate() : LocalDate.now().toDate());

            final Long entityId = this.fromApiJsonHelper.extractLongNamed(DataExportApiConstants.ENTITY_ID,element);
            final Integer status = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(DataExportApiConstants.ENTITY_STATUS,element);
            final String accountNo = this.fromApiJsonHelper.extractStringNamed(DataExportApiConstants.ACCOUNT_NO,element);
            final Long officeId = this.fromApiJsonHelper.extractLongNamed(DataExportApiConstants.ENTITY_OFFICE,element);
            final Client client = (entityId != null && entity.isClient()? this.clientRepository.findOne(entityId) : null);
            final Group group = (entityId != null && entity.isGroup()? this.groupRepository.findOne(entityId) : null);
            final String displayName = (client != null ? client.getDisplayName() : group != null ? group.getName() : null);
            final String mobileNo = (client != null ? client.mobileNo() : null);
            final String sql = assembleSqlString(sqlMap);


            final DataExport newDataExport = DataExport.instance(entity.getName(),entityId,status,submittedOnDate,accountNo,officeId,displayName,mobileNo,sql);

            this.dataExportRepository.save(newDataExport);

            final Integer processStatus = DataExportProcessStatus.PROCESSING.getId();
            final LocalDateTime processEndDate = DateUtils.getLocalDateTimeOfTenant();
            final String fileName = processEndDate.toString(DATA_EXPORT_FILENAME_DATETIME_FORMAT_PATTERN);

            final DataExportProcess dataExportProcess = DataExportProcess.instance(newDataExport.getId(),fileName,processStatus,currentUser.getId(),submittedOnDate,null,null,0);

            this.dataExportRepository.saveAndFlush(newDataExport);
            this.dataExportProcessRepository.saveAndFlush(dataExportProcess);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(dataExportProcess.getId()) //
                    .build();

        }catch(final InvalidJsonException ije){return CommandProcessingResult.empty();
        }catch(final UnsupportedParameterException upe){return CommandProcessingResult.empty();}
    }

    private Map<String, List<String>> getBaseEntitySql (final DataExportRequestData requestData){
        final Map<String,List<String>> sql = new HashMap<>();
        final List<String> select = new ArrayList<>();
        final List<String> from = new ArrayList<>();
        final List<String> where = new ArrayList<>();
        final List<String> group = new ArrayList<>();
        final List<String> order = new ArrayList<>();
        final DataExportBaseEntityEnum entity = requestData.getBaseEntity();
        final List<String> fieldNames = requestData.getDisplayedFieldNames();
        final List<DataExportFilter> dataExportFilters = requestData.getDataExportFiltersList();
        final List<EntityLabel> labels = new ArrayList<>();

        for(String fieldName : fieldNames){
            EntityLabel label = this.entityLabelRepository.findOneByTableAndField(entity.getTablename(),fieldName);
            if(label == null){throw new InputMismatchException("No EntityLabel found with entity table " + entity.getTablename() + " and field name " + fieldName + ".");}
            if(label.getReferenceTable()!=null) {
                labels.add(label);
            } else {select.add(entity.getTablename() + "." + fieldName + " as " + label.getLabel());}
        }

         from.add(entity.getTablename());

        if(labels.size()>0){
            for(EntityLabel label : labels){
                select.add(label.getReferenceTable() + "." + label.getReferenceField() + " as " + label.getLabel());
                from.add("left join " + label.getReferenceTable() + " on " + label.getReferenceTable() + "." + DataExportApiConstants.ENTITY_ID
                        + " = " + entity.getTablename() + "." + label.getField());
            }
        }

        if(dataExportFilters.size()>0){
            for(DataExportFilter filter : dataExportFilters){
                where.add(filter.getSearchQuery());
            }
        }

        sql.put("select",select);
        sql.put("from",from);
        sql.put("where",where);
        sql.put("group by",group);
        sql.put("order by",order);

        return sql;
    }

    private String assembleSqlString (final Map<String, List<String>> sqlMap){
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
