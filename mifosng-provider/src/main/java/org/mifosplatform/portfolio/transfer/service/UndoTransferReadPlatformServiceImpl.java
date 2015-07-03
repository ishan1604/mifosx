/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.transfer.service;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.portfolio.transfer.data.UndoTransferClientData;
import org.mifosplatform.portfolio.transfer.data.UndoTransferGroupData;
import org.mifosplatform.portfolio.transfer.exception.UndoTransferEntityNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Service
public class UndoTransferReadPlatformServiceImpl implements UndoTransferReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final UndoTransferGroupMapper undoTransferGroupMapper = new UndoTransferGroupMapper();
    private final UndoTransferClientMapper undoTransferClientMapper = new UndoTransferClientMapper();

    @Autowired
    public UndoTransferReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class UndoTransferClientMapper implements RowMapper<UndoTransferClientData>{
        final String schema;

        private UndoTransferClientMapper() {
            final StringBuilder builder = new StringBuilder(300);

            builder.append("ut.id as id, ");
            builder.append("ut.client_id as clientId, ");
            builder.append("ut.submittedon_date as submittedOnDate, ");
            builder.append("c.display_name as displayName, ");
            builder.append("o.id as transferFromOfficeId, ");
            builder.append("o.name as transferFromOfficeName, ");
            builder.append("mf.name as transferToOfficeName, ");
            builder.append("g.id as transferFromGroupId, ");
            builder.append("g.display_name as transferFromGroupName, ");
            builder.append("mg.display_name as transferToGroupName, ");
            builder.append("s.id as transferFromStaffId, ");
            builder.append("s.display_name as transferFromStaffName, ");
            builder.append("a.username as approvedUser, ");
            builder.append("ut.is_transfer_undone as isTransferUndone ");
            builder.append("from m_undo_transfer ut ");
            builder.append("left join m_client c on c.id = ut.client_id ");
            builder.append("left join m_office o on o.id = ut.transfer_from_office_id ");
            builder.append("left join m_office mf on mf.id = c.office_id ");
            builder.append("left join m_group g on g.id = ut.transfer_from_group_id ");
            builder.append("left join m_group_client gc on gc.client_id = c.id ");
            builder.append("left join m_group mg on mg.id = gc.group_id ");
            builder.append("left join m_staff s on s.id = ut.transfer_from_staff_id ");
            builder.append("left join m_appuser a on a.id = ut.approvedon_userid ");

            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }

        @Override
        public UndoTransferClientData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final String clientName = rs.getString("displayName");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final boolean isTransferUndone = rs.getBoolean("isTransferUndone");
            final Long transferFromOfficeId = JdbcSupport.getLong(rs, "transferFromOfficeId");
            final String transferFromOfficeName = rs.getString("transferFromOfficeName");

            final Long transferFromGroupId =JdbcSupport.getLong(rs, "transferFromGroupId");
            final String transferFromGroupName =rs.getString("transferFromGroupName");

            final Long transferFromStaffId =JdbcSupport.getLong(rs, "transferFromStaffId");
            final String transferFromStaffName =  rs.getString("transferFromStaffName");
            final String approvedUser  =  rs.getString("approvedUser");
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs,"submittedOnDate");
            final String transferToGroupName =   rs.getString("transferToGroupName");
            final String transferToOfficeName =   rs.getString("transferToOfficeName");


            return UndoTransferClientData.instance(id,clientId,clientName,isTransferUndone,transferFromOfficeId,transferFromOfficeName,
                    transferFromGroupId,transferFromGroupName,transferFromStaffId,transferFromStaffName,submittedOnDate,approvedUser,
                    transferToGroupName,transferToOfficeName);


        }
    }


    private static final class UndoTransferGroupMapper implements RowMapper<UndoTransferGroupData>{

        final String schema;

        private UndoTransferGroupMapper() {
            final StringBuilder builder = new StringBuilder(300);

            builder.append("ut.id as id, ");
            builder.append("ut.group_id as groupId, ");
            builder.append("ut.submittedon_date as submittedOnDate, ");
            builder.append("mg.display_name as displayName, ");
            builder.append("o.id as transferFromOfficeId, ");
            builder.append("o.name as transferFromOfficeName, ");
            builder.append("mf.name as transferToOfficeName, ");
            builder.append("g.id as transferFromGroupId, ");
            builder.append("g.display_name as transferFromGroupName, ");
            builder.append("s.id as transferFromStaffId, ");
            builder.append("s.display_name as transferFromStaffName, ");
            builder.append("a.username as approvedUser, ");
            builder.append("ut.is_transfer_undone as isTransferUndone ");
            builder.append("from m_undo_transfer ut ");
            builder.append("left join m_group mg on mg.id = ut.group_id ");
            builder.append("left join m_office o on o.id = ut.transfer_from_office_id ");
            builder.append("left join m_group g on g.id = ut.transfer_from_group_id ");
            builder.append("left join m_office mf on mf.id = g.office_id ");
            builder.append("left join m_staff s on s.id = ut.transfer_from_staff_id ");
            builder.append("left join m_appuser a on a.id = ut.approvedon_userid ");

            this.schema = builder.toString();
        }

        public String schema() {
            return this.schema;
        }


        @Override
        public UndoTransferGroupData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final String groupName = rs.getString("displayName");
            final Long groupId = JdbcSupport.getLong(rs, "groupId");
            final boolean isTransferUndone = rs.getBoolean("isTransferUndone");
            final Long transferFromOfficeId = JdbcSupport.getLong(rs, "transferFromOfficeId");
            final String transferFromOfficeName = rs.getString("transferFromOfficeName");

            final Long transferFromGroupId =JdbcSupport.getLong(rs, "transferFromGroupId");
            final String transferFromGroupName =rs.getString("transferFromGroupName");

            final Long transferFromStaffId =JdbcSupport.getLong(rs, "transferFromStaffId");
            final String transferFromStaffName =  rs.getString("transferFromStaffName");
            final String approvedUser  =  rs.getString("approvedUser");
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs,"submittedOnDate");
            final String transferToOfficeName =  rs.getString("transferToOfficeName");

            return UndoTransferGroupData.instance(id,groupId,groupName,isTransferUndone,transferFromOfficeId,transferFromOfficeName,
                    transferFromGroupId,transferFromGroupName,transferFromStaffId,transferFromStaffName,submittedOnDate,approvedUser,
                    transferToOfficeName);

        }
    }


    @Override
    public UndoTransferClientData retrieveUndoTransferClientData(Long clientId) {
        try{
            final String sql = "select " + this.undoTransferClientMapper.schema + " where ut.client_id = ? and " +
                    "ut.id=(select max(t.id) from m_undo_transfer t where t.client_id = ? and t.is_transfer_undone = 0)";
            return this.jdbcTemplate.queryForObject(sql,this.undoTransferClientMapper, new Object[] { clientId,clientId});

        }catch(final EmptyResultDataAccessException e){
            throw new UndoTransferEntityNotFound(clientId);
        }

    }

    @Override
    public UndoTransferGroupData retrieveUndoTransferGroupData(Long groupId) {
        try{
            final String sql = "select " + this.undoTransferGroupMapper.schema + " where ut.group_id = ? and " +
                    "ut.id=(select max(t.id) from m_undo_transfer t where t.group_id = ? and t.is_transfer_undone = 0)";
            return this.jdbcTemplate.queryForObject(sql, this.undoTransferGroupMapper, new Object[] { groupId,groupId });
        }catch (final EmptyResultDataAccessException e) {
            throw new UndoTransferEntityNotFound(groupId);
        }
    }


    @Override
    public Collection<UndoTransferClientData> retrieveAllTransferredClients() {
        final String sql = "select " + this.undoTransferClientMapper.schema + " where ut.is_transfer_undone = 0 and ut.group_id is null " +
                "and ut.is_group_transfer = 0 and ut.id in (select max(u.id) from m_undo_transfer u where u.group_id is null and u.is_group_transfer = 0 and u.is_transfer_undone =0 group by u.client_id)";
        return this.jdbcTemplate.query(sql, this.undoTransferClientMapper, new Object[] {});
    }

    @Override
    public Collection<UndoTransferGroupData> retrieveAllTransferredGroups() {
        final String sql = "select " + this.undoTransferGroupMapper.schema + " where ut.is_transfer_undone = 0 and ut.group_id is not null " +
                "and ut.id in (select max(u.id) from m_undo_transfer u where u.group_id is not null and u.is_transfer_undone = 0 group by u.group_id)";
        return this.jdbcTemplate.query(sql, this.undoTransferGroupMapper, new Object[] {});    }
}
