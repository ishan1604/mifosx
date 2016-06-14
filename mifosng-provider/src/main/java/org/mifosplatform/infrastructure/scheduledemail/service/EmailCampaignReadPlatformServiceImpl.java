/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.scheduledemail.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.scheduledemail.exception.EmailBusinessRuleNotFound;
import org.mifosplatform.infrastructure.scheduledemail.exception.EmailCampaignNotFound;
import org.mifosplatform.infrastructure.scheduledemail.data.EmailBusinessRulesData;
import org.mifosplatform.infrastructure.scheduledemail.data.EmailCampaignData;
import org.mifosplatform.infrastructure.scheduledemail.data.EmailCampaignTimeLine;
import org.mifosplatform.infrastructure.scheduledemail.domain.EmailCampaignStatus;
import org.mifosplatform.infrastructure.scheduledemail.domain.EmailCampaignStatusEnumerations;
import org.mifosplatform.infrastructure.scheduledemail.domain.EmailCampaignType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class EmailCampaignReadPlatformServiceImpl implements EmailCampaignReadPlatformService {


    private final JdbcTemplate jdbcTemplate;

    private final BusinessRuleMapper businessRuleMapper;

    private final EmailCampaignMapper emailCampaignMapper;

    @Autowired
    public EmailCampaignReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.businessRuleMapper = new BusinessRuleMapper();
        this.emailCampaignMapper = new EmailCampaignMapper();
    }


    private static final class EmailCampaignMapper implements RowMapper<EmailCampaignData>{

        final String schema;

        private EmailCampaignMapper() {
            final StringBuilder sql = new StringBuilder(400);
            sql.append("sc.id as id, ");
            sql.append("sc.campaign_name as campaignName, ");
            sql.append("sc.campaign_type as campaignType, ");
            sql.append("sc.runReport_id as runReportId, ");
            sql.append("sc.message as message, ");
            sql.append("sc.param_value as paramValue, ");
            sql.append("sc.status_enum as status, ");
            sql.append("sc.recurrence as recurrence, ");
            sql.append("sc.recurrence_start_date as recurrenceStartDate, ");
            sql.append("sc.next_trigger_date as nextTriggerDate, ");
            sql.append("sc.last_trigger_date as lastTriggerDate, ");
            sql.append("sc.submittedon_date as submittedOnDate, ");
            sql.append("sbu.username as submittedByUsername, ");
            sql.append("sc.closedon_date as closedOnDate, ");
            sql.append("clu.username as closedByUsername, ");
            sql.append("acu.username as activatedByUsername, ");
            sql.append("sc.approvedon_date as activatedOnDate ");
            sql.append("from email_campaign sc ");
            sql.append("left join m_appuser sbu on sbu.id = sc.submittedon_userid ");
            sql.append("left join m_appuser acu on acu.id = sc.approvedon_userid ");
            sql.append("left join m_appuser clu on clu.id = sc.closedon_userid ");

            this.schema = sql.toString();
        }
        public String schema() {
            return this.schema;
        }

        @Override
        public EmailCampaignData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final String campaignName = rs.getString("campaignName");
            final Integer campaignType = JdbcSupport.getInteger(rs, "campaignType");
            final Long runReportId = JdbcSupport.getLong(rs, "runReportId");
            final String paramValue = rs.getString("paramValue");
            final String message  = rs.getString("message");

            final Integer statusId = JdbcSupport.getInteger(rs, "status");
            final EnumOptionData status = EmailCampaignStatusEnumerations.status(statusId);
            final DateTime nextTriggerDate = JdbcSupport.getDateTime(rs, "nextTriggerDate");
            final LocalDate  lastTriggerDate = JdbcSupport.getLocalDate(rs, "lastTriggerDate");


            final LocalDate closedOnDate = JdbcSupport.getLocalDate(rs, "closedOnDate");
            final String closedByUsername = rs.getString("closedByUsername");


            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final String submittedByUsername = rs.getString("submittedByUsername");

            final LocalDate activatedOnDate = JdbcSupport.getLocalDate(rs, "activatedOnDate");
            final String activatedByUsername = rs.getString("activatedByUsername");
            final String recurrence  =rs.getString("recurrence");
            final DateTime recurrenceStartDate = JdbcSupport.getDateTime(rs, "recurrenceStartDate");
            final EmailCampaignTimeLine emailCampaignTimeLine = new EmailCampaignTimeLine(submittedOnDate,submittedByUsername,
                    activatedOnDate,activatedByUsername,closedOnDate,closedByUsername);



            return EmailCampaignData.instance(id,campaignName,campaignType,runReportId,paramValue,status,message,nextTriggerDate,lastTriggerDate,emailCampaignTimeLine,
                    recurrenceStartDate,recurrence);
        }
    }


    private static final class BusinessRuleMapper implements ResultSetExtractor<List<EmailBusinessRulesData>>{

        final String schema;

        private BusinessRuleMapper() {
            final StringBuilder sql = new StringBuilder(300);
            sql.append("sr.id as id, ");
            sql.append("sr.report_name as reportName, ");
            sql.append("sr.report_type as reportType, ");
            sql.append("sr.description as description, ");
            sql.append("sp.parameter_variable as params, ");
            sql.append("sp.parameter_FormatType as paramType, ");
            sql.append("sp.parameter_label as paramLabel, ");
            sql.append("sp.parameter_name as paramName ");
            sql.append("from stretchy_report sr ");
            sql.append("left join stretchy_report_parameter as srp on srp.report_id = sr.id ");
            sql.append("left join stretchy_parameter as sp on sp.id = srp.parameter_id ");

            this.schema = sql.toString();
        }

        public String schema(){
            return this.schema;
        }

        @Override
        public List<EmailBusinessRulesData> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<EmailBusinessRulesData> emailBusinessRulesDataList = new ArrayList<EmailBusinessRulesData>();

            EmailBusinessRulesData emailBusinessRulesData = null;

            Map<Long,EmailBusinessRulesData> mapOfSameObjects = new HashMap<Long, EmailBusinessRulesData>();

            while(rs.next()){
                final Long id = rs.getLong("id");
                emailBusinessRulesData  = mapOfSameObjects.get(id);
                if(emailBusinessRulesData == null){
                    final String reportName = rs.getString("reportName") ;
                    final String reportType = rs.getString("reportType");
                    final String paramName  = rs.getString("paramName");
                    final String paramLabel = rs.getString("paramLabel");
                    final String description = rs.getString("description");

                    Map<String,Object> hashMap = new HashMap<String, Object>();
                    hashMap.put(paramLabel,paramName);
                    emailBusinessRulesData = EmailBusinessRulesData.instance(id,reportName,reportType,hashMap,description);
                    mapOfSameObjects.put(id,emailBusinessRulesData);
                    //add to the list
                    emailBusinessRulesDataList.add(emailBusinessRulesData);
                }
                //add new paramType to the existing object
                Map<String,Object> hashMap = new HashMap<String, Object>();
                final String paramName  = rs.getString("paramName");
                final String paramLabel = rs.getString("paramLabel");
                hashMap.put(paramLabel,paramName);

                //get existing map and add new items to it
                emailBusinessRulesData.getReportParamName().putAll(hashMap);
            }

            return emailBusinessRulesDataList;
        }
    }

    @Override
    public Collection<EmailBusinessRulesData> retrieveAll() {
        final String searchType = "scheduledemail";
        final String sql = "select " + this.businessRuleMapper.schema() + " where sr.report_type = ?";

        return this.jdbcTemplate.query(sql, this.businessRuleMapper, searchType);
    }

    @Override
    public EmailBusinessRulesData retrieveOneTemplate(Long resourceId) {
        final String searchType = "scheduledemail";

        final String sql = "select " + this.businessRuleMapper.schema() + " where sr.report_type = ? and sr.id = ?";

        List<EmailBusinessRulesData> retrieveOne =  this.jdbcTemplate.query(sql, this.businessRuleMapper, searchType,resourceId);
        try{
            EmailBusinessRulesData emailBusinessRulesData = retrieveOne.get(0);
            return emailBusinessRulesData;
        }
        catch (final IndexOutOfBoundsException e){
            throw new EmailBusinessRuleNotFound(resourceId);
        }

    }

    @Override
    public EmailCampaignData retrieveOne(Long resourceId) {
        final Integer isVisible =1;
        try{
            final String sql = "select " + this.emailCampaignMapper.schema + " where sc.id = ? and sc.is_visible = ?";
            return this.jdbcTemplate.queryForObject(sql, this.emailCampaignMapper, resourceId,isVisible);
        } catch (final EmptyResultDataAccessException e) {
            throw new EmailCampaignNotFound(resourceId);
        }
    }

    @Override
    public Collection<EmailCampaignData> retrieveAllCampaign() {
        final Integer visible = 1;
        final String sql = "select " + this.emailCampaignMapper.schema() + " where sc.is_visible = ?";
        return this.jdbcTemplate.query(sql, this.emailCampaignMapper, visible);
    }

    @Override
    public Collection<EmailCampaignData> retrieveAllScheduleActiveCampaign() {
        final Integer scheduleCampaignType = EmailCampaignType.SCHEDULE.getValue();
        final Integer statusEnum  = EmailCampaignStatus.ACTIVE.getValue();
        final Integer visible     = 1;
        final String sql = "select " + this.emailCampaignMapper.schema() + " where sc.status_enum = ? and sc.campaign_type = ? and sc.is_visible = ?";
        return this.jdbcTemplate.query(sql,this.emailCampaignMapper, statusEnum,scheduleCampaignType,visible);
    }



}
