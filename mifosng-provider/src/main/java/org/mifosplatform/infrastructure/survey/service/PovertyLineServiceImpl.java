package org.mifosplatform.infrastructure.survey.service;

import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.survey.data.LikeliHoodPovertyLineData;
import org.mifosplatform.infrastructure.survey.data.PovertyLineData;
import org.mifosplatform.infrastructure.survey.data.PpiPovertyLineData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cieyou on 3/11/14.
 */
@Service
public class PovertyLineServiceImpl implements PovertyLineService{

    private final static Logger logger = LoggerFactory.getLogger(PovertyLineService.class);
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Autowired
    PovertyLineServiceImpl(final RoutingDataSource dataSource){
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);

    }


    @Override
    public PpiPovertyLineData retrieveAll()
    {

        String sql = "SELECT pl.id, sc.score_from, sc.score_to , pl.poverty_line,lkh.code , lkh.name , pl.ppi_name FROM ppi_poverty_line"
                    + "JOIN ppi_likelihoods lkh on lkh.id = pl.likelihood_id "
                    + "JOIN ppi_scores sc on sc.id = pl.score_id ";

        final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);

        PpiPovertyLineData ppiPovertyLineData = null;

        List<LikeliHoodPovertyLineData> listOfLikeliHoodPovertyLineData = null;

        final List<PovertyLineData> listOfPovertyLine = null;

        while (rs.next()) {


            listOfPovertyLine.add(new PovertyLineData(rs.getLong("id"),
                    rs.getLong("score_from"),
                    rs.getLong("score_to"),
                    rs.getLong("poverty_line")
            ));


            listOfLikeliHoodPovertyLineData.add(LikeliHoodPovertyLineData(listOfPovertyLine,rs.getString('name'),rs.getString('code')));

            final PovertyLineData povertyLine =
            LikeliHoodPovertyLineData.


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


        final List<PovertyLineData> povertyLineData = new ArrayList<PovertyLineData>();
        return  povertyLineData;
    }

//    public PovertyLineData retrieve()
//    {
//
//    }
}
