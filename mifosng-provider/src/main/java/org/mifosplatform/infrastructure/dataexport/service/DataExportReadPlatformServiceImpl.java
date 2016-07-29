/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.service;

import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.dataexport.api.DataExportApiConstants;
import org.mifosplatform.infrastructure.dataexport.data.*;
import org.mifosplatform.infrastructure.dataexport.domain.*;
import org.mifosplatform.infrastructure.dataexport.exception.EntityMismatchException;
import org.mifosplatform.infrastructure.dataexport.helper.FileHelper;
import org.mifosplatform.infrastructure.dataexport.helper.XmlFileHelper;
import org.mifosplatform.infrastructure.dataqueries.domain.RegisteredTable;
import org.mifosplatform.infrastructure.dataqueries.domain.RegisteredTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.security.InvalidParameterException;
import java.util.*;

@Service
public class DataExportReadPlatformServiceImpl implements DataExportReadPlatformService {
    private final RegisteredTableRepository registeredTableRepository;
    private final JdbcTemplate jdbcTemplate;
    private final DataExportRepository dataExportRepository;
    private final DataExportProcessRepository dataExportProcessRepository;
    private final EnumValueCollectionRepositoryWrapper enumValueCollectionRepositoryWrapper;
    private final EntityLabelRepository entityLabelRepository;

    @Autowired
    public DataExportReadPlatformServiceImpl(final RegisteredTableRepository registeredTableRepository, final RoutingDataSource dataSource,
            final DataExportRepository dataExportRepository, final DataExportProcessRepository dataExportProcessRepository,
            final EnumValueCollectionRepositoryWrapper enumValueCollectionRepositoryWrapper, final EntityLabelRepository entityLabelRepository) {
        this.registeredTableRepository = registeredTableRepository;
        this.dataExportRepository = dataExportRepository;
        this.dataExportProcessRepository = dataExportProcessRepository;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.enumValueCollectionRepositoryWrapper = enumValueCollectionRepositoryWrapper;
        this.entityLabelRepository = entityLabelRepository;
    }

    @Override
    public DataExportRequestData retrieveDataExportRequestData(final String entity){
        return retrieveDataExportRequestData(entity, new ArrayList<DataExportFilter>(), new ArrayList<String>());
    }

    @Override
    public DataExportRequestData retrieveDataExportRequestData(final String entity,
            final List<DataExportFilter> filters, final List<String> exportDatatables){
        try {
            DataExportBaseEntityEnum baseEntity = null;

            if (entity != null) {
                baseEntity = DataExportBaseEntityEnum.valueOf(entity.toUpperCase());
            }
            if (baseEntity == null || baseEntity.getTablename().isEmpty() || filters == null || exportDatatables == null) {
                throw new InvalidParameterException(entity);
            }

            final Set<String> supportedParameters = new HashSet<>();
            final List<EntityLabel> entityLabels = new ArrayList<>(this.entityLabelRepository.findAllByTable(baseEntity.getTablename()));

            supportedParameters.add(DataExportApiConstants.ENTITY);
            supportedParameters.add(DataExportApiConstants.ENTITY_ID);
            for(EntityLabel entityLabel : entityLabels){
                supportedParameters.add(entityLabel.getJsonParam());
            }

            return new DataExportRequestData(baseEntity, filters, exportDatatables,supportedParameters);
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

        try {
            final DataExportProcess dataExportProcess = this.dataExportProcessRepository.findOne(dataExportProcessId);
            final DataExport dataExport = this.dataExportRepository.findOne(dataExportProcess.getDataExport());
            final DataExportRequestData requestData = retrieveDataExportRequestData(entity!=null?entity:dataExport.getBaseEntity());
            if (entity!=null && !entity.equals(dataExport.getBaseEntity())) {
                throw new EntityMismatchException(entity, dataExport.getBaseEntity());
            }

            final DataExportFileFormat dataExportFileFormat = DataExportFileFormat.valueOf(fileFormat.toUpperCase());
            final DataExportFileData dataExportFileData = downloadDataExportFileData(dataExportProcess, requestData, dataExportFileFormat, dataExport.getSql());
            // increase the file download count by one
            Integer fileDownloadCount = dataExportProcess.getFileDownloadCount();
            fileDownloadCount += 1;
            dataExportProcess.updateFileDownloadCount(fileDownloadCount);

            // save the outgoing payment process entity and flush the changes immediately
            dataExportProcessRepository.saveAndFlush(dataExportProcess);

            return Response.ok(dataExportFileData.getInputStream()).
                    header("Content-Disposition", "attachment; filename=\"" + dataExportFileData.getFileName() + "\"").
                    header("Content-Type", dataExportFileData.getContentType())
                    .build();
        }catch(EntityMismatchException eme){return Response.status(Response.Status.BAD_REQUEST).type(eme.getLocalizedMessage()).build();
        }catch(Exception e) {return Response.serverError().tag(e.getMessage()).build();}
    }

    @Override
    public DataExportTemplateData retrieveDataExportTemplate(final String entityName) {
        List<DataExportBaseEntityEnum> entities = new ArrayList<>();

        try {
            if (entityName != null) {
                DataExportBaseEntityEnum entity = DataExportBaseEntityEnum.valueOf(entityName.toUpperCase());
                if (entity == null || entity.getTablename().isEmpty()) {
                    throw new IllegalArgumentException(entityName);
                }
                entities.add(entity);
            } else {throw new IllegalArgumentException();}
        } catch (IllegalArgumentException iae){
            for (DataExportBaseEntityEnum entity : DataExportBaseEntityEnum.values()) {
                entities.add(entity);
            }
        }

        List<Map<String, String>> entityMaps = assembleDataTableNames(entities, DataExportApiConstants.ENTITY_TABLE);
        List<Map<String,String>> dataTables = assembleDataTableNames(entities, DataExportApiConstants.DATATABLE_NAME);

        return new DataExportTemplateData(entityMaps,dataTables);
    }

    private List<Map<String, String>> assembleDataTableNames(final List<DataExportBaseEntityEnum> entityNames, final String type){
        List<Map<String,String>> tables = new ArrayList<>();

        for (DataExportBaseEntityEnum entity : entityNames){
            if(type.equals(DataExportApiConstants.DATATABLE_NAME)) {
                for (RegisteredTable registeredTable : this.registeredTableRepository.findAllByApplicationTableName(entity.getTablename())) {
                    Map<String, String> table = new HashMap<>();
                    table.put(DataExportApiConstants.ENTITY_NAME, entity.name());
                    table.put(type, registeredTable.getRegisteredTableName());
                    tables.add(table);
                }
            }
            if(type.equals(DataExportApiConstants.ENTITY_TABLE)){
                Map<String, String> table = new HashMap<>();
                table.put(DataExportApiConstants.ENTITY_NAME, entity.name());
                table.put(type, entity.getTablename());
                tables.add(table);
                if(tables.isEmpty()){throw new RuntimeException("Something went wrong while assembling the datatables.");}
            }
        }

        return tables;
    }

    private List<String[]> getFileData(final List<Map<String, Object>> rawfileData){
        try {
            final List<String[]> fileData = new ArrayList<>();
            final Integer arraySize = rawfileData.get(0).size();
            final String[] row = new String[arraySize];
            int i = 0;

            for (String key : rawfileData.get(0).keySet()) {
                row[i] = key;
                i++;
            }

            for(Map<String, Object> entry : rawfileData){
                String[] valuerow = new String[arraySize];
                for(i = 0; i < arraySize ; i++){
                    if(entry.get(row[i])!=null) {
                        valuerow[i] = '"' + entry.get(row[i]).toString() + '"';
                    }
                }
                fileData.add(valuerow);
            }

            return fileData;
        } catch (InvalidParameterException ipe){return null;
        } catch (IndexOutOfBoundsException iob){return null;
        } catch (NullPointerException npe){return null;}
    }

    private List<String> assembleExportDataTables(final DataExportBaseEntityEnum entity){
        List<String> exportDataTables = new ArrayList<>();
        for (RegisteredTable registeredTable : this.registeredTableRepository.findAllByApplicationTableName(entity.getTablename())){
            exportDataTables.add(registeredTable.getRegisteredTableName());
        }
        return exportDataTables;
    }

    private DataExportFileData downloadDataExportFileData(final DataExportProcess dataExportProcess,
            final DataExportRequestData requestData, final DataExportFileFormat fileFormat, final String sql) {

        final String fileName = dataExportProcess.getFileName();
        final List<Map<String, Object>> rawfileData = reassignEnumValues(this.jdbcTemplate.queryForList(sql), requestData.getBaseEntity());
        final List<String[]> fileData = getFileData(rawfileData);
        DataExportFileData dataExportFileData = null;

        switch (fileFormat) {
            case XLS:
                dataExportFileData = FileHelper.createDataExportXlsFile(rawfileData, fileName);
                break;
            case CSV:
                String[] csvFileHeaders;
                List<String> fieldNames = new ArrayList<>(rawfileData.get(0).keySet());

                csvFileHeaders = fieldNames.toArray(new String[fieldNames.size()]);

                dataExportFileData = FileHelper.createDataExportCsvFile(fileData, fileName,
                        csvFileHeaders);
                break;

            case XML:
                dataExportFileData = FileHelper.createDataExportXmlFile(fileName);

                // write data to XML file
                XmlFileHelper.writeToFile(fileData, dataExportFileData.getFile());
                break;
        }

        return dataExportFileData;
    }

    private List<Map<String, Object>> reassignEnumValues(final List<Map<String, Object>> rawfileData, DataExportBaseEntityEnum entity) {
        final List<Map<String, Object>> fileData = new ArrayList<>();

        for(Map<String, Object> entry : rawfileData) {
            for (String key : entry.keySet()) {
                Object value;
                try{
                    EntityLabel entityLabel = this.entityLabelRepository.findOneByTableAndJsonParam(entity.getTablename(),key);
                    String fieldName = entityLabel.getField();
                    value = this.enumValueCollectionRepositoryWrapper.findOneByFieldNameAndId(fieldName,Long.valueOf(entry.get(key).toString())).getValue();
                }catch (Exception e){value = entry.get(key);}
                entry.put(key,value);
            }
            fileData.add(entry);
        }
        return fileData;
    }
}
