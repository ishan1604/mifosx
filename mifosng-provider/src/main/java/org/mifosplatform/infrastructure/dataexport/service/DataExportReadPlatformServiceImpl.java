/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.service;

import org.mifosplatform.infrastructure.dataexport.data.DataExportBaseEntityEnum;
import org.mifosplatform.infrastructure.dataexport.data.DataExportRequestData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataExportReadPlatformServiceImpl implements DataExportReadPlatformService {

    public DataExportRequestData setBaseClientDataTemplate(){
        List<String> dataTables = null;
        dataTables.add("m_office");
        dataTables.add("r_enum_values");
        return new DataExportRequestData(DataExportBaseEntityEnum.CLIENT,null,dataTables);
    }



    public DataExportRequestData retrieveDataExportTemplate() {
        DataExportRequestData dataExportRequestData = setBaseClientDataTemplate();

        return dataExportRequestData;
    }
}
