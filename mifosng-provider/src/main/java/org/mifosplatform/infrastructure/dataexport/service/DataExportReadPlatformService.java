/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.service;

import org.mifosplatform.infrastructure.dataexport.data.DataExportRequestData;
import org.mifosplatform.infrastructure.dataexport.data.DataExportTemplateData;
import javax.ws.rs.core.Response;

import java.util.Collection;

public interface DataExportReadPlatformService {

    DataExportTemplateData retrieveDataExportTemplate(String entity);

    DataExportRequestData retrieveDataExportRequestData (String entity);

    Collection<DataExportRequestData> retrieveDataExportRequestDataCollection ();

    Response downloadDataExportFile(String entity, Long dataExportProcessId, String fileFormat);

    //List<String> retrieveEntityParameters (DataExportBaseEntityEnum entity, JsonCommand command);
}
