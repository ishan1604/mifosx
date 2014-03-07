package org.mifosplatform.infrastructure.survey.service;

import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.dataqueries.api.DataTableApiConstant;
import org.mifosplatform.infrastructure.dataqueries.data.DatatableData;
import org.mifosplatform.infrastructure.dataqueries.data.ResultsetColumnHeaderData;
import org.mifosplatform.infrastructure.dataqueries.service.GenericDataService;
import org.mifosplatform.infrastructure.dataqueries.service.ReadWriteNonCoreDataService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.survey.data.SurveyDataTableData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


@Service
public class ReadSurveyServiceImpl implements ReadSurveyService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final GenericDataService genericDataService;
    private final ReadWriteNonCoreDataService readWriteNonCoreDataService;
    private final static Logger logger = LoggerFactory.getLogger(ReadSurveyServiceImpl.class);

    @Autowired
    public ReadSurveyServiceImpl(final PlatformSecurityContext context,
                                 final RoutingDataSource dataSource,
                                 final FromJsonHelper fromJsonHelper,
                                 final GenericDataService genericDataService,
                                 final ReadWriteNonCoreDataService readWriteNonCoreDataService){

        this.context = context;
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
        this.genericDataService = genericDataService;
        this.readWriteNonCoreDataService = readWriteNonCoreDataService;
    }

    @Override
    public List<SurveyDataTableData> retrieveAllSurveys()
    {

        String sql = this.retrieveAllSurveySQL("");

        final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);

        final List<SurveyDataTableData> surveyDataTables = new ArrayList<SurveyDataTableData>();
        while (rs.next()) {
            final String appTableName = rs.getString("application_table_name");
            final String registeredDatatableName = rs.getString("registered_table_name");
            final boolean enabled = rs.getBoolean("enabled");
            final List<ResultsetColumnHeaderData> columnHeaderData = this.genericDataService
                    .fillResultsetColumnHeaders(registeredDatatableName);

            surveyDataTables.add(
                    SurveyDataTableData.create(
                            DatatableData.create(appTableName, registeredDatatableName, columnHeaderData),
                            enabled
                    )
            );
        }

        return surveyDataTables;
    }

    private  String retrieveAllSurveySQL(String andClause )
    {
        // PERMITTED datatables
        return "select application_table_name, cf.enabled, registered_table_name" + " from x_registered_table "
                + " left join c_configuration cf on x_registered_table.registered_table_name = cf.name "
                + " where exists"
                + " (select 'f'" + " from m_appuser_role ur " + " join m_role r on r.id = ur.role_id"
                + " left join m_role_permission rp on rp.role_id = r.id" + " left join m_permission p on p.id = rp.permission_id"
                + " where ur.appuser_id = " + this.context.authenticatedUser().getId()
                + " and (p.code in ('ALL_FUNCTIONS', 'ALL_FUNCTIONS_READ') or p.code = concat('READ_', registered_table_name))) "
                + " and x_registered_table.category = "+ DataTableApiConstant.CATEGORY_PPI
                + andClause + " order by application_table_name, registered_table_name";
    }

    @Override
    public SurveyDataTableData retrieveSurvey(String surveyName)
    {
        final String sql = "select cf.enabled, application_table_name, registered_table_name" + " from x_registered_table "
                + " left join c_configuration cf on x_registered_table.registered_table_name = cf.name "
                + " where exists"
                + " (select 'f'" + " from m_appuser_role ur " + " join m_role r on r.id = ur.role_id"
                + " left join m_role_permission rp on rp.role_id = r.id" + " left join m_permission p on p.id = rp.permission_id"
                + " where ur.appuser_id = " + this.context.authenticatedUser().getId() + " and registered_table_name='" + surveyName + "'"
                + " and (p.code in ('ALL_FUNCTIONS', 'ALL_FUNCTIONS_READ') or p.code = concat('READ_', registered_table_name))) "
                + " order by application_table_name, registered_table_name";

        final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);

        SurveyDataTableData datatableData = null;
        while (rs.next()) {
            final String appTableName = rs.getString("application_table_name");
            final String registeredDatatableName = rs.getString("registered_table_name");
            final boolean enabled = rs.getBoolean("enabled");
            final List<ResultsetColumnHeaderData> columnHeaderData = this.genericDataService
                    .fillResultsetColumnHeaders(registeredDatatableName);

                    datatableData = SurveyDataTableData.create(
                            DatatableData.create(appTableName, registeredDatatableName, columnHeaderData),
                            enabled
                    );

        }

        return datatableData;
    }
}
