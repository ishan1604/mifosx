/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.api;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.mifosplatform.infrastructure.dataexport.data.DataExportBaseEntityEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataExportApiConstants {

    public final static String FILE_FORMAT = "fileFormat";
    public final static String DATA_EXPORT_PROCESS_ID = "dataExportProcessId";
    public final static String ENTITY_NAME = "entityName";
    public final static String ENTITY_TABLE = "entityTable";
    public final static String DATATABLE_NAME = "datatableName";
    public final static String ENTITY_ID = "id";
    public final static String ENTITY_STATUS = "status";
    public final static String ENTITY_OFFICE = "office";
    public final static String ENTITY_SUBMITDATE = "submittedondate";
    public final static String SUBMITTEDON_DATE_FORMAT = "dd-MM-yyyy";

    public final static String ENTITY = "entity";
    public final static String CLIENT_ID = "id";
    public final static String GROUP_ID = "groupId";
    public final static String LOAN_ID = "loanId";
    public final static String SAVINGS_ACCOUNT_ID = "savingsaccountId";

    public final static String OFFICE_TABLE = "m_office";
    public final static String ACCOUNT_NO = "account_no";
    public final static String OFFICE_ID = "office_id";
    public final static String OFFICE_NAME = "name";
    public final static String FULL_NAME = "display_name";
    public final static String STATUS = "status_enum";
    public final static String SUBMITTED_ON_DATE = "submittedon_date";
    public final static String MOBILE_NO = "mobile_no";

    public final static ImmutableList<String> SUPPORTED_PARAMETERS =
            ImmutableList.of(ENTITY,ENTITY_ID,ENTITY_OFFICE,ENTITY_STATUS,ENTITY_SUBMITDATE);

    public final static ImmutableMap<String, String> CLIENT_PARAMETERS_TO_FIELD_NAMES =
            ImmutableMap.of(ENTITY_STATUS,STATUS,ENTITY_OFFICE,OFFICE_ID,ENTITY_SUBMITDATE,SUBMITTED_ON_DATE);
    public final static ImmutableMap<String, String> GROUP_PARAMETERS_TO_FIELD_NAMES =
            ImmutableMap.of(ENTITY_STATUS,STATUS,ENTITY_OFFICE,OFFICE_ID,ENTITY_SUBMITDATE,SUBMITTED_ON_DATE);
    public final static ImmutableMap<String, String> LOAN_PARAMETERS_TO_FIELD_NAMES =
            ImmutableMap.of(ENTITY_STATUS,STATUS,ENTITY_OFFICE,OFFICE_ID,ENTITY_SUBMITDATE,SUBMITTED_ON_DATE);
    public final static ImmutableMap<String, String> SAVINGS_ACCOUNTS_TO_PARAMETER_FIELD_NAMES =
            ImmutableMap.of(ENTITY_STATUS,STATUS,ENTITY_OFFICE,OFFICE_ID,ENTITY_SUBMITDATE,SUBMITTED_ON_DATE);

    public final static ImmutableList<String> CLIENT_FIELD_NAMES =
            ImmutableList.of(CLIENT_ID,ACCOUNT_NO,FULL_NAME, OFFICE_ID,MOBILE_NO,STATUS, SUBMITTED_ON_DATE);
    public final static ImmutableList<String> GROUP_FIELD_NAMES =
            ImmutableList.of(CLIENT_ID,ACCOUNT_NO,FULL_NAME, OFFICE_ID,MOBILE_NO,STATUS, SUBMITTED_ON_DATE);
    public final static ImmutableList<String> LOAN_FIELD_NAMES =
            ImmutableList.of(CLIENT_ID,ACCOUNT_NO,FULL_NAME, OFFICE_ID,MOBILE_NO,STATUS, SUBMITTED_ON_DATE);
    public final static ImmutableList<String> SAVINGS_ACCOUNT_FIELD_NAMES =
            ImmutableList.of(CLIENT_ID,ACCOUNT_NO,FULL_NAME, OFFICE_ID,MOBILE_NO,STATUS, SUBMITTED_ON_DATE);
}
