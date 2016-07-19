/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.api;


import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class DataExportApiConstants {

    public final static String FILE_FORMAT = "fileFormat";
    public final static String DATA_EXPORT_PROCESS_ID = "dataExportProcessId";
    public final static String ENTITY_NAME = "entityName";
    public final static String ENTITY_TABLE = "entityTable";
    public final static String DATATABLE_NAME = "datatableName";

    public final static String ENTITY = "entity";
    public final static String CLIENT_ID = "id";
    public final static String GROUP_ID = "groupId";
    public final static String LOAN_ID = "loanId";
    public final static String SAVINGS_ACCOUNT_ID = "savingsaccountId";
    public final static String OFFICE_TABLE = "m_office";

    public final static String ACCOUNT_NO = "account_no";
    public final static String OFFICE_ID = "office_id";
    public final static String OFFICE_NAME = "office";
    public final static String FULL_NAME = "display_name";
    public final static String STATUS = "status_enum";
    public final static String SUBMITTED_ON_DATE = "submitted_on_date";
    public final static String MOBILE_NO = "mobile_no";

    public final static ImmutableList<String> CLIENT_FIELD_NAMES =
            ImmutableList.of(DataExportApiConstants.CLIENT_ID,DataExportApiConstants.ACCOUNT_NO,DataExportApiConstants.FULL_NAME,
                    DataExportApiConstants.OFFICE_ID,DataExportApiConstants.MOBILE_NO,DataExportApiConstants.STATUS,
                    DataExportApiConstants.SUBMITTED_ON_DATE);
    public final static ImmutableList<String> GROUP_FIELD_NAMES =
            ImmutableList.of(DataExportApiConstants.CLIENT_ID,DataExportApiConstants.ACCOUNT_NO,DataExportApiConstants.FULL_NAME,
                    DataExportApiConstants.OFFICE_ID,DataExportApiConstants.MOBILE_NO,DataExportApiConstants.STATUS,
                    DataExportApiConstants.SUBMITTED_ON_DATE);
    public final static ImmutableList<String> LOAN_FIELD_NAMES =
            ImmutableList.of(DataExportApiConstants.CLIENT_ID,DataExportApiConstants.ACCOUNT_NO,DataExportApiConstants.FULL_NAME,
                    DataExportApiConstants.OFFICE_ID,DataExportApiConstants.MOBILE_NO,DataExportApiConstants.STATUS,
                    DataExportApiConstants.SUBMITTED_ON_DATE);
    public final static ImmutableList<String> SAVINGS_ACCOUNT_FIELD_NAMES =
            ImmutableList.of(DataExportApiConstants.CLIENT_ID,DataExportApiConstants.ACCOUNT_NO,DataExportApiConstants.FULL_NAME,
                    DataExportApiConstants.OFFICE_ID,DataExportApiConstants.MOBILE_NO,DataExportApiConstants.STATUS,
                    DataExportApiConstants.SUBMITTED_ON_DATE);
}
