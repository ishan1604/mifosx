/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.service;

import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.dataqueries.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class EntityDatatableChecksReadPlatformServiceImpl implements EntityDatatableChecksReadService {

    private final JdbcTemplate jdbcTemplate;
    private final RegisterDataTableMapper registerDataTableMapper;
    private  final EntityDataTableChecksMapper entityDataTableChecksMapper;

    @Autowired
    public EntityDatatableChecksReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.registerDataTableMapper = new RegisterDataTableMapper();
        this.entityDataTableChecksMapper = new EntityDataTableChecksMapper();
    }

    @Override
    public List<EntityDataTableChecksData> retrieveAll (final Long status, final String entity, final Long productLoanId){


        String sql = "select " + this.entityDataTableChecksMapper.schema();

        String and="";

        if(status !=null || entity !=null || productLoanId !=null )
        sql +=" where ";

        if(status !=null) {

            sql +="  status_enum ="+ status;
            and = " and ";
        }

        if(entity !=null){

            sql += and + " t.application_table_name = '"+ entity+"'";
            and = " and ";
        }

        if(productLoanId !=null){

            sql += and + " t.product_loan_id = "+ productLoanId;
        }

        return this.jdbcTemplate.query(sql, this.entityDataTableChecksMapper);

    }

    @Override
    public EntityDataTableChecksTemplateData retrieveTemplate (){

        List<DatatableCheksData> dataTables = getDataTables();
        List<String> entities = EntityTables.getEntitiesList();
        List<DatatableCheckStatusData> status = StatusEnum.getStatusList();

        return new EntityDataTableChecksTemplateData(entities,status,dataTables);

    }

    private List<DatatableCheksData> getDataTables(){
        final String sql = "select " + this.registerDataTableMapper.schema();

        return this.jdbcTemplate.query(sql, this.registerDataTableMapper);
    }


    protected static final class RegisterDataTableMapper implements RowMapper<DatatableCheksData> {

        @Override
        public DatatableCheksData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String tableName = rs.getString("tableName");

            return new DatatableCheksData(id, tableName);
        }

        public String schema() {
            return " t.id as id, t.registered_table_name as tableName from x_registered_table t where application_table_name IN( 'm_client','m_group','m_savings_account','m_loan')";
        }
    }

    protected static final class EntityDataTableChecksMapper implements RowMapper<EntityDataTableChecksData> {

        @Override
        public EntityDataTableChecksData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String entity = rs.getString("entity");
            final Long status = rs.getLong("status");
            final String datatableName = rs.getString("datatableName");
            final String displayName = rs.getString("displayName");
            final boolean systemDefined = rs.getBoolean("systemDefined");
            final Long loanProductId =JdbcSupport.getLong(rs, "loanProductId");

            return new EntityDataTableChecksData(id,entity,status,datatableName,systemDefined,displayName,loanProductId);
        }

        public String schema() {
            return " rt.display_name as displayName, t.id as id,t.application_table_name as entity, t.status_enum as status, t.system_defined as systemDefined, rt.registered_table_name as datatableName, t.product_loan_id as loanProductId from m_entity_datatable_check as t join x_registered_table rt on rt.id = t.x_registered_table_id";
        }
    }


}