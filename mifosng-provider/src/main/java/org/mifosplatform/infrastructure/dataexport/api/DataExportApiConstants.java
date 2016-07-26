/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.api;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.mifosplatform.infrastructure.dataexport.data.DataExportBaseEntityEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataExportApiConstants {

    // base path constants
    public static final String SYSTEM_USER_HOME_PROPERTY_KEY = "user.home";
    public static final String SYSTEM_USER_HOME_DIR_PATH_STRING = System.getProperty(SYSTEM_USER_HOME_PROPERTY_KEY);
    public static final String APPLICATION_BASE_DIR_NAME = "mifosx\\mifosng-provider";
    public static final String APPLICATION_BASE_DIR_PATH_STRING = SYSTEM_USER_HOME_DIR_PATH_STRING +
            File.separator + APPLICATION_BASE_DIR_NAME;
    public static final String MYSQL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String MYSQL_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String WINDOWS_END_OF_LINE_CHARACTER = "\r\n";
    public static final String UNIX_END_OF_LINE_CHARACTER = "\n";
    public static final String XLS_FILE_CONTENT_TYPE = "text/xlsx; charset=utf-8";
    public static final String CSV_FILE_CONTENT_TYPE = "text/csv; charset=utf-8";
    public static final String XML_FILE_CONTENT_TYPE = "text/xml; charset=utf-8";
    public static final String CSV_FILE_FORMAT = "csv";
    public static final String XML_FILE_FORMAT = "xml";
    public static final String XLS_FILE_FORMAT = "xlsx";

    // json parameter and format constants
    public final static String FILE_FORMAT = "fileFormat";
    public final static String DATA_EXPORT_PROCESS_ID = "dataExportProcessId";
    public final static String DATA_EXPORT = "DATAEXPORT";
    public final static String ENTITY_NAME = "entityName";
    public final static String ENTITY_TABLE = "entityTable";
    public final static String DATATABLE_NAME = "datatableName";
    public final static String ENTITY_ID = "id";
    public final static String ENTITY_STATUS = "status";
    public final static String ENTITY_TYPE = "type";
    public final static String ENTITY_OFFICE = "office";
    public final static String ENTITY_STAFF = "staff";
    public final static String ENTITY_CLIENT_ID = "clientId";
    public final static String ENTITY_GROUP_ID = "groupId";
    public final static String ENTITY_SUBMITDATE = "submittedondate";
    public final static String ENTITY_PRINCIPAL = "principal";
    public final static String ENTITY_OUTSTANDING = "outstanding";
    public final static String ENTITY_BALANCE = "balance";
    public final static String SUBMITTEDON_DATE_FORMAT = "dd-MM-yyyy";
    public final static String ENTITY = "entity";
    public static final String DATA_EXPORT_FILENAME_DATETIME_FORMAT_PATTERN = "yyyyMMddHHmmss";

    // field name constants
    public final static String CLIENT_ID = "client_id";
    public final static String GROUP_ID = "group_id";
    public final static String LOAN_ID = "loan_id";
    public final static String SAVINGS_ACCOUNT_ID = "savingsaccount_id";
    public final static String OFFICE_TABLE = "m_office";
    public final static String ACCOUNT_NO = "account_no";
    public final static String EXTERNAL_ID = "external_id";
    public final static String OFFICE_ID = "office_id";
    public final static String OFFICE_NAME = "name";
    public final static String FULL_NAME = "display_name";
    public final static String STATUS = "status_enum";
    public final static String LOAN_STATUS = "loan_status_id";
    public final static String LOAN_TYPE = "loan_type_enum";
    public final static String SAVINGS_PRODUCT_TYPE = "product_id";
    public final static String LOAN_PRINCIPAL = "principal_amount";
    public final static String TOTAL_OUTSTANDING = "total_outstanding_derived";
    public final static String SUBMITTED_ON_DATE = "submittedon_date";
    public final static String MOBILE_NO = "mobile_no";
    public final static String STAFF_ID = "staff_id";
    public final static String ACCOUNT_BALANCE = "account_balance_derived";

    // supported parameter lists
    public final static ImmutableList<String> CLIENT_SUPPORTED_PARAMETERS =
            ImmutableList.of(ENTITY,ENTITY_ID,ENTITY_OFFICE,ENTITY_STATUS,ENTITY_SUBMITDATE);
    public final static ImmutableList<String> GROUP_SUPPORTED_PARAMETERS =
            ImmutableList.of(ENTITY,ENTITY_ID,ENTITY_OFFICE,ENTITY_STATUS,ENTITY_SUBMITDATE,ENTITY_STAFF);
    public final static ImmutableList<String> LOAN_SUPPORTED_PARAMETERS =
            ImmutableList.of(ENTITY,ENTITY_ID,ENTITY_CLIENT_ID,ENTITY_GROUP_ID,ENTITY_TYPE,ENTITY_STATUS,ENTITY_SUBMITDATE);
    public final static ImmutableList<String> SAVINGS_ACCOUNT_SUPPORTED_PARAMETERS =
            ImmutableList.of(ENTITY,ENTITY_ID,ENTITY_CLIENT_ID,ENTITY_GROUP_ID,ENTITY_TYPE,ENTITY_STATUS,ENTITY_SUBMITDATE);

    // maps linking json parameter constants to matching field name constants
    public final static ImmutableMap<String, String> CLIENT_PARAMETERS_TO_FIELD_NAMES =
            ImmutableMap.of(ENTITY_STATUS,STATUS,ENTITY_OFFICE,OFFICE_ID,ENTITY_SUBMITDATE,SUBMITTED_ON_DATE);
    public final static ImmutableMap<String, String> GROUP_PARAMETERS_TO_FIELD_NAMES =
            ImmutableMap.of(ENTITY_STATUS,STATUS,ENTITY_OFFICE,OFFICE_ID,ENTITY_SUBMITDATE,SUBMITTED_ON_DATE,ENTITY_STAFF,STAFF_ID);
    public final static ImmutableMap<String, String> LOAN_PARAMETERS_TO_FIELD_NAMES =
            ImmutableMap.of(ENTITY_TYPE,LOAN_TYPE,ENTITY_STATUS,LOAN_STATUS,ENTITY_CLIENT_ID,CLIENT_ID,ENTITY_GROUP_ID,GROUP_ID,ENTITY_SUBMITDATE,SUBMITTED_ON_DATE);
    public final static ImmutableMap<String, String> SAVINGS_ACCOUNT_PARAMETERS_TO_FIELD_NAMES =
            ImmutableMap.of(ENTITY_STATUS,STATUS,ENTITY_TYPE,SAVINGS_PRODUCT_TYPE,ENTITY_SUBMITDATE,SUBMITTED_ON_DATE,ENTITY_CLIENT_ID,CLIENT_ID,ENTITY_GROUP_ID,GROUP_ID);

    // map giving user friendly labels to field names
    public final static ImmutableMap<Object, Object> FIELD_NAME_LABELS =
            ImmutableMap.builder().put(CLIENT_ID,"Client").put(GROUP_ID,"Group").put(OFFICE_NAME,"Office").put(FULL_NAME,"Name")
            .put(STATUS, "Status").put(LOAN_PRINCIPAL, "Principal").put(TOTAL_OUTSTANDING, "Outstanding").put(MOBILE_NO, "Mobile")
            .put(SUBMITTED_ON_DATE, "Submitted On").put(ACCOUNT_BALANCE, "Account Balance").build();

    // lists of fields to be entered in the data export for each entity type
    public final static ImmutableList<String> CLIENT_FIELD_NAMES =
            ImmutableList.of(ENTITY_ID,ACCOUNT_NO,FULL_NAME, OFFICE_ID,MOBILE_NO,STATUS, SUBMITTED_ON_DATE);
    public final static ImmutableList<String> GROUP_FIELD_NAMES =
            ImmutableList.of(ENTITY_ID,EXTERNAL_ID,FULL_NAME, OFFICE_ID,STAFF_ID,STATUS, SUBMITTED_ON_DATE);
    public final static ImmutableList<String> LOAN_FIELD_NAMES =
            ImmutableList.of(ENTITY_ID,ACCOUNT_NO,LOAN_TYPE,CLIENT_ID,GROUP_ID,LOAN_STATUS,SUBMITTED_ON_DATE,LOAN_PRINCIPAL,TOTAL_OUTSTANDING);
    public final static ImmutableList<String> SAVINGS_ACCOUNT_FIELD_NAMES =
            ImmutableList.of(ENTITY_ID,ACCOUNT_NO,CLIENT_ID,GROUP_ID,SAVINGS_PRODUCT_TYPE,STATUS, SUBMITTED_ON_DATE, ACCOUNT_BALANCE);

    // list of field constants that refer to entities other than baseEntity
    /*public final static ImmutableMap<String,String> ENTITY_REFERRAL =
            ImmutableMap.of(OFFICE_ID,OFFICE_NAME,CLIENT_ID,FULL_NAME,GROUP_ID,FULL_NAME);
            */
}
