/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.service;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.accounting.closure.data.GLClosureData;
import org.mifosplatform.accounting.closure.exception.GLClosureNotFoundException;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Service
public class GLClosureReadPlatformServiceImpl implements GLClosureReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");


    @Autowired
    public GLClosureReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class GLClosureMapper implements RowMapper<GLClosureData> {

        public String schema() {
            return " glClosure.id as id, glClosure.office_id as officeId,office.name as officeName ,glClosure.closing_date as closingDate,"
                    + " glClosure.is_deleted as isDeleted, creatingUser.id as creatingUserId,creatingUser.username as creatingUserName,"
                    + " updatingUser.id as updatingUserId,updatingUser.username as updatingUserName, glClosure.created_date as createdDate,"
                    + " glClosure.lastmodified_date as updatedDate, glClosure.comments as comments,ie.journal_entry_transaction_id as journalEntryTransactionId "
                    + " from acc_gl_closure as glClosure "
                    + " left join m_appuser as creatingUser on glClosure.createdby_id=creatingUser.id "
                    + " left join m_appuser as updatingUser on glClosure.lastmodifiedby_id=updatingUser.id "
                    + " left join m_office as office on glClosure.office_id=office.id "
                    + " left join acc_income_and_expense_bookings as ie on ie.gl_closure_id = glClosure.id ";
        }

        @Override
        public GLClosureData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long officeId = rs.getLong("officeId");
            final String officeName = rs.getString("officeName");
            final LocalDate closingDate = JdbcSupport.getLocalDate(rs, "closingDate");
            final Boolean deleted = rs.getBoolean("isDeleted");
            final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");
            final LocalDate lastUpdatedDate = JdbcSupport.getLocalDate(rs, "updatedDate");
            final Long creatingByUserId = rs.getLong("creatingUserId");
            final String createdByUserName = rs.getString("creatingUserName");
            final Long lastUpdatedByUserId = rs.getLong("updatingUserId");
            final String lastUpdatedByUserName = rs.getString("updatingUserName");
            final String comments = rs.getString("comments");
            final String journalEntryBookingForIncomeAndExpense = rs.getString("journalEntryTransactionId");

            return new GLClosureData(id, officeId, officeName, closingDate, deleted, createdDate, lastUpdatedDate, creatingByUserId,
                    createdByUserName, lastUpdatedByUserId, lastUpdatedByUserName, comments,journalEntryBookingForIncomeAndExpense);
        }
    }

    @Override
    public List<GLClosureData> retrieveAllGLClosures(final Long officeId) {
        final GLClosureMapper rm = new GLClosureMapper();

        String sql = "select " + rm.schema() + " where (glClosure.is_deleted = 0 or glClosure.is_deleted = 1) ";
        final Object[] objectArray = new Object[1];
        int arrayPos = 0;
        if (officeId != null && officeId != 0) {
            sql += " and glClosure.office_id = ?";
            objectArray[arrayPos] = officeId;
            arrayPos = arrayPos + 1;
        }

        sql = sql + " order by glClosure.closing_date desc";

        final Object[] finalObjectArray = Arrays.copyOf(objectArray, arrayPos);
        return this.jdbcTemplate.query(sql, rm, finalObjectArray);
    }

    @Override
    public GLClosureData retrieveGLClosureById(final long glClosureId) {
        try {

            final GLClosureMapper rm = new GLClosureMapper();
            final String sql = "select " + rm.schema() + " where glClosure.id = ?";

            final GLClosureData glAccountData = this.jdbcTemplate.queryForObject(sql, rm, new Object[] { glClosureId });

            return glAccountData;
        } catch (final EmptyResultDataAccessException e) {
            throw new GLClosureNotFoundException(glClosureId);
        }
    }
}
