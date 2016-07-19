/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.dataexport.api.DataExportApiConstants;
import org.mifosplatform.infrastructure.dataexport.data.DataExportBaseEntityEnum;
import org.mifosplatform.infrastructure.dataexport.data.DataExportRequestData;
import org.mifosplatform.infrastructure.dataexport.data.DataExportTemplateData;
import org.mifosplatform.infrastructure.dataqueries.domain.RegisteredTable;
import org.mifosplatform.infrastructure.dataqueries.domain.RegisteredTableRepository;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepository;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.*;

@Service
public class DataExportReadPlatformServiceImpl implements DataExportReadPlatformService {
    private final RegisteredTableRepository registeredTableRepository;
    private final ClientRepository clientRepository;
    private final LoanRepository loanRepository;

    @Autowired
    public DataExportReadPlatformServiceImpl(final RegisteredTableRepository registeredTableRepository,
            final ClientRepository clientRepository, final LoanRepository loanRepository) {
        this.registeredTableRepository = registeredTableRepository;
        this.clientRepository = clientRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    public DataExportRequestData retrieveDataExportRequestData(final String entity){
        try {
            DataExportBaseEntityEnum baseEntity = null;
            if (entity != null) {
                baseEntity = DataExportBaseEntityEnum.valueOf(entity.toUpperCase());
            }
            if (baseEntity == null || baseEntity.getTablename().isEmpty()) {
                throw new InvalidParameterException(entity);
            }

            List<String> exportDatatables = assembleExportDataTables(baseEntity);

            return new DataExportRequestData(baseEntity, null, exportDatatables);
        } catch(InvalidParameterException e){return null;}
    }

    @Override
    public Collection<DataExportRequestData> retrieveDataExportRequestDataCollection(){
        Collection<DataExportRequestData> dataExportRequestDatas = new ArrayList<>();
        for(DataExportBaseEntityEnum entity:DataExportBaseEntityEnum.values()){
            dataExportRequestDatas.add(retrieveDataExportRequestData(entity.getName()));
        }
        return dataExportRequestDatas;
    }

    @Override
    public Response downloadDataExportFile(final String entity, final Long dataExportProcessId, final String fileFormat) {
        return null;
    }

    @Override
    public DataExportTemplateData retrieveDataExportTemplate(final String entityName) {
        List<DataExportBaseEntityEnum> entities = new ArrayList<>();

        if (entityName != null){
            DataExportBaseEntityEnum entity = DataExportBaseEntityEnum.valueOf(entityName);
            if(entity == null || entity.getTablename().isEmpty()){throw new InvalidRequestException(entityName);}
            entities.add(entity);
        } else {
            for(DataExportBaseEntityEnum entity : DataExportBaseEntityEnum.values()){
                entities.add(entity);
            }
        }

        List<Map<String, String>> entityMaps = assembleDataTableNames(entities, DataExportApiConstants.ENTITY_TABLE);
        List<Map<String,String>> dataTables = assembleDataTableNames(entities, DataExportApiConstants.DATATABLE_NAME);

        return new DataExportTemplateData(entityMaps,dataTables);
    }

    /*@Override
    public List<String> retrieveEntityParameters (final DataExportBaseEntityEnum entity, final JsonCommand command){
        List<String> parameterMap = new ArrayList<>();

        if(entity.equals(DataExportBaseEntityEnum.CLIENT)){
            parameterMap.put(DataExportApiConstants.CLIENT_ID,command.integerValueOfParameterNamed(DataExportApiConstants.CLIENT_ID));
            parameterMap.put(DataExportApiConstants.ACCOUNT_NO,command.stringValueOfParameterNamed(DataExportApiConstants.ACCOUNT_NO));
            parameterMap.put(DataExportApiConstants.FULL_NAME,command.stringValueOfParameterNamed(DataExportApiConstants.FULL_NAME));
            parameterMap.put(DataExportApiConstants.OFFICE_NAME,command.integerValueOfParameterNamed(DataExportApiConstants.OFFICE_NAME));
            parameterMap.put(DataExportApiConstants.MOBILE_NO,command.stringValueOfParameterNamed(DataExportApiConstants.MOBILE_NO));
            parameterMap.put(DataExportApiConstants.STATUS,command.integerValueOfParameterNamed(DataExportApiConstants.STATUS));
            return parameterMap;
        } else if (entity.equals(DataExportBaseEntityEnum.GROUP)){
            return parameterMap;
        } else if (entity.equals(DataExportBaseEntityEnum.LOAN)){
            return parameterMap;
        } else if (entity.equals(DataExportBaseEntityEnum.SAVINGSACCOUNT)){
            return parameterMap;
        } else {throw new InvalidParameterException(entity.name());}
    }*/

    private List<Map<String, String>> assembleDataTableNames(final List<DataExportBaseEntityEnum> entityNames, String key){
        List<Map<String,String>> tables = new ArrayList<>();

        for (DataExportBaseEntityEnum entity : entityNames){
            for(RegisteredTable registeredTable : this.registeredTableRepository.findAllByApplicationTableName(entity.getTablename())){
                Map<String,String> table = new HashMap<>();
                table.put(DataExportApiConstants.ENTITY_NAME,entity.name());
                table.put(key,registeredTable.getRegisteredTableName());
                tables.add(table);
            }
        }
        return tables;
    }

    private List<String> assembleExportDataTables(DataExportBaseEntityEnum entity){
        List<String> exportDataTables = new ArrayList<>();
        for (RegisteredTable registeredTable : this.registeredTableRepository.findAllByApplicationTableName(entity.getTablename())){
            exportDataTables.add(registeredTable.getRegisteredTableName());
        }
        return exportDataTables;
    }
}
