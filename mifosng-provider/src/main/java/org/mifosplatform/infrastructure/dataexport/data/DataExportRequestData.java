/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.data;

import org.mifosplatform.infrastructure.dataexport.api.DataExportApiConstants;
import org.yaml.snakeyaml.constructor.ConstructorException;

import java.security.InvalidParameterException;
import java.util.*;

public class DataExportRequestData {

    // Specify the baseEntity which is used as the base object:
    private DataExportBaseEntityEnum baseEntity;

    // Specify the field names that are displayed in the export file
    private List<String> displayedFieldNames;

    // Contains the various filter queries for each tableName
    private List<DataExportFilter> dataExportFiltersList;

    // List of other (data)tables to retrieve:
    private List<String> exportDatatables;
    private Map<String,String> paramsToFields;
    private Set<String> supportedParameters;

    public DataExportRequestData(DataExportBaseEntityEnum baseEntity, List<DataExportFilter> dataExportFiltersList,
                                 List<String> exportDatatables, List<String> displayedFieldNames) {
        this.baseEntity = baseEntity;
        this.dataExportFiltersList = dataExportFiltersList;
        this.exportDatatables = exportDatatables;
        this.displayedFieldNames = displayedFieldNames;
        assignParameters();
    }

    public DataExportRequestData(DataExportBaseEntityEnum baseEntity, List<DataExportFilter> dataExportFiltersList,
                                 List<String> exportDatatables){
        this.baseEntity = baseEntity;
        this.dataExportFiltersList = dataExportFiltersList;
        this.exportDatatables = exportDatatables;
        assignParameters();
    }

    public DataExportRequestData(DataExportBaseEntityEnum baseEntity){
        this(baseEntity,new ArrayList<DataExportFilter>(),new ArrayList<String>());
    }

    public void addExportDataTable(String datatable){
        this.exportDatatables.add(datatable);
    }

    public void addDataExportFilter(DataExportFilter filter){
        this.dataExportFiltersList.add(filter);
    }

    public DataExportBaseEntityEnum getBaseEntity(){return this.baseEntity;}

    public List<DataExportFilter> getDataExportFiltersList(){return this.dataExportFiltersList;}

    public List<String> getExportDatatables(){return this.exportDatatables;}

    public List<String> getDisplayedFieldNames(){return this.displayedFieldNames;}

    public Set<String> getSupportedParameters(){return this.supportedParameters;}

    public Map<String,String> getParamsToFields(){return this.paramsToFields;}

    private void assignParameters(){
        switch(this.baseEntity){
            case CLIENT:
                this.supportedParameters = (supportedParameters==null?new HashSet<>(DataExportApiConstants.CLIENT_SUPPORTED_PARAMETERS):supportedParameters);
                this.paramsToFields = (paramsToFields==null?DataExportApiConstants.CLIENT_PARAMETERS_TO_FIELD_NAMES:paramsToFields);
                this.displayedFieldNames = (displayedFieldNames==null?DataExportApiConstants.CLIENT_FIELD_NAMES:displayedFieldNames);
                break;
            case GROUP:
                this.supportedParameters = (supportedParameters==null?new HashSet<>(DataExportApiConstants.GROUP_SUPPORTED_PARAMETERS):supportedParameters);
                this.paramsToFields = (paramsToFields==null?DataExportApiConstants.GROUP_PARAMETERS_TO_FIELD_NAMES:paramsToFields);
                this.displayedFieldNames = (displayedFieldNames==null?DataExportApiConstants.GROUP_FIELD_NAMES:displayedFieldNames);
                break;
            case LOAN:
                this.supportedParameters = (supportedParameters==null?new HashSet<>(DataExportApiConstants.LOAN_SUPPORTED_PARAMETERS):supportedParameters);
                this.paramsToFields = (paramsToFields==null?DataExportApiConstants.LOAN_PARAMETERS_TO_FIELD_NAMES:paramsToFields);
                this.displayedFieldNames = (displayedFieldNames==null?DataExportApiConstants.LOAN_FIELD_NAMES:displayedFieldNames);
                break;
            case SAVINGSACCOUNT:
                this.supportedParameters = (supportedParameters==null?new HashSet<>(DataExportApiConstants.SAVINGS_ACCOUNT_SUPPORTED_PARAMETERS):supportedParameters);
                this.paramsToFields = (paramsToFields==null?DataExportApiConstants.SAVINGS_ACCOUNT_PARAMETERS_TO_FIELD_NAMES:paramsToFields);
                this.displayedFieldNames = (displayedFieldNames==null?DataExportApiConstants.SAVINGS_ACCOUNT_FIELD_NAMES:displayedFieldNames);
                break;
            default:
                throw new InvalidParameterException(this.baseEntity.name());
        }
    }
}
