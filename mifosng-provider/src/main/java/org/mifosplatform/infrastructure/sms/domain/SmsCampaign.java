/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.domain;

import org.apache.commons.lang.ObjectUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.dataqueries.domain.Report;
import org.mifosplatform.infrastructure.sms.data.SmsCampaignValidator;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "sms_campaign")
public class SmsCampaign extends AbstractPersistable<Long> {

    @Column(name = "campaign_name", nullable = false)
    private String campaignName;

    @Column(name = "campaign_type", nullable = false)
    private Integer campaignType;

    @ManyToOne
    @JoinColumn(name = "runreport_id", nullable = false)
    private Report businessRuleId ;

    @Column(name = "param_value")
    private String paramValue;

    @Column(name = "status_enum", nullable = false)
    private Integer status;

    @Column(name ="message", nullable =false)
    private String message;

    @Column(name = "closedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date closureDate;

    @ManyToOne(optional = true)
    @JoinColumn(name = "closedon_userid", nullable = true)
    private AppUser closedBy;

    @Column(name = "submittedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date submittedOnDate;

    @ManyToOne(optional = true)
    @JoinColumn(name = "submittedon_userid", nullable = true)
    private AppUser submittedBy;

    @Column(name = "approvedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date approvedOnDate;

    @ManyToOne(optional = true)
    @JoinColumn(name = "approvedon_userid", nullable = true)
    private AppUser approvedBy;

    @Column(name = "recurrence", nullable = false)
    private String recurrence;

    @Column(name = "next_trigger_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextTriggerDate;

    @Column(name = "last_trigger_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastTriggerDate;

    @Column(name = "recurrence_start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date recurrenceStartDate;

    public SmsCampaign() {
    }

    private SmsCampaign(final String campaignName, final Integer campaignType,
                        final Report businessRuleId, final String paramValue,
                        final String message,final LocalDate submittedOnDate,
                        final AppUser submittedBy,final String recurrence,final LocalDateTime localDateTime) {
        this.campaignName = campaignName;
        this.campaignType = SmsCampaignType.fromInt(campaignType).getValue();
        this.businessRuleId = businessRuleId;
        this.paramValue = paramValue;
        this.status     = SmsCampaignStatus.PENDING.getValue();
        this.message    = message;
        this.submittedOnDate = submittedOnDate.toDate();
        this.submittedBy = submittedBy;
        this.recurrence = recurrence;
        LocalDateTime recurrenceStartDate = new LocalDateTime();
        if(localDateTime != null){
            this.recurrenceStartDate = localDateTime.toDate();
        }else{
            this.recurrenceStartDate = recurrenceStartDate.toDate();
        }

    }

    public static SmsCampaign instance(final AppUser submittedBy,final Report report,final JsonCommand command){

        final String campaignName = command.stringValueOfParameterNamed(SmsCampaignValidator.campaignName);
        final Long  campaignType = command.longValueOfParameterNamed(SmsCampaignValidator.campaignType);

        final String paramValue = command.stringValueOfParameterNamed(SmsCampaignValidator.paramValue);

        final String message   = command.stringValueOfParameterNamed(SmsCampaignValidator.message);
        LocalDate submittedOnDate = new LocalDate();
        if (command.hasParameter(SmsCampaignValidator.submittedOnDateParamName)) {
            submittedOnDate = command.localDateValueOfParameterNamed(SmsCampaignValidator.submittedOnDateParamName);
        }

        final String recurrence   = command.stringValueOfParameterNamed(SmsCampaignValidator.recurrenceParamName);
        final Locale locale = command.extractLocale();
        final  DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

        LocalDateTime recurrenceStartDate = new  LocalDateTime();
        if(SmsCampaignType.fromInt(campaignType.intValue()).isSchedule()) {
            if(command.hasParameter(SmsCampaignValidator.recurrenceStartDate)){
                recurrenceStartDate = LocalDateTime.parse(command.stringValueOfParameterNamed(SmsCampaignValidator.recurrenceStartDate),fmt);
            }
        } else{
            recurrenceStartDate = null;
        }


        return new SmsCampaign(campaignName,campaignType.intValue(),report,paramValue,message,submittedOnDate,submittedBy,recurrence,recurrenceStartDate);

    }


    public void activate(final AppUser currentUser, final DateTimeFormatter formatter, final LocalDate activationLocalDate){

        if(isActive()){
            //handle errors if already activated
            final String defaultUserMessage = "Cannot activate campaign. Campaign is already active.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.campaign.already.active", defaultUserMessage,
                    SmsCampaignValidator.activationDateParamName, activationLocalDate.toString(formatter));

            final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
        this.approvedOnDate = activationLocalDate.toDate();
        this.approvedBy = currentUser;
        this.status = SmsCampaignStatus.ACTIVE.getValue();

       validate();
    }

    public void close(final AppUser currentUser,final DateTimeFormatter dateTimeFormatter, final LocalDate closureLocalDate){
        this.closedBy = currentUser;
        this.closureDate = closureLocalDate.toDate();
        this.status     = SmsCampaignStatus.CLOSED.getValue();
    }


    public boolean isActive(){
        return SmsCampaignStatus.fromInt(this.status).isActive();
    }

    public boolean isPending(){
        return  SmsCampaignStatus.fromInt(this.status).isPending();
    }

    public boolean isClosed(){
        return SmsCampaignStatus.fromInt(this.status).isClosed();
    }

    public boolean isDirect(){
        return SmsCampaignType.fromInt(this.campaignType).isDirect();
    }

    public boolean isSchedule(){
        return SmsCampaignType.fromInt(this.campaignType).isSchedule();
    }

    private void validate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        validateActivationDate(dataValidationErrors);
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    private void validateActivationDate(final List<ApiParameterError> dataValidationErrors) {

        if (getSubmittedOnDate() != null && isDateInTheFuture(getSubmittedOnDate())) {

            final String defaultUserMessage = "submitted date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.submittedOnDate.in.the.future",
                    defaultUserMessage, SmsCampaignValidator.submittedOnDateParamName, this.submittedOnDate);

            dataValidationErrors.add(error);
        }

        if (getActivationLocalDate() != null && getSubmittedOnDate() != null && getSubmittedOnDate().isAfter(getActivationLocalDate())) {

            final String defaultUserMessage = "submitted date cannot be after the activation date";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.submittedOnDate.after.activation.date",
                    defaultUserMessage, SmsCampaignValidator.submittedOnDateParamName, this.submittedOnDate);

            dataValidationErrors.add(error);
        }

        if (getActivationLocalDate() != null && isDateInTheFuture(getActivationLocalDate())) {

            final String defaultUserMessage = "Activation date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.activationDate.in.the.future",
                    defaultUserMessage, SmsCampaignValidator.activationDateParamName, getActivationLocalDate());

            dataValidationErrors.add(error);
        }

    }

    public LocalDate getSubmittedOnDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.submittedOnDate), null);

    }

    public LocalDate getActivationLocalDate() {
        LocalDate activationLocalDate = null;
        if (this.approvedOnDate != null) {
            activationLocalDate = LocalDate.fromDateFields(this.approvedOnDate);
        }
        return activationLocalDate;
    }
    private boolean isDateInTheFuture(final LocalDate localDate) {
        return localDate.isAfter(DateUtils.getLocalDateOfTenant());
    }

    public Report getBusinessRuleId() {
        return this.businessRuleId;
    }

    public String getCampaignName() {
        return this.campaignName;
    }


    public String getMessage() {
        return this.message;
    }

    public String getParamValue() {
        return this.paramValue;
    }

    public String getRecurrence() {
        return this.recurrence;
    }

    public LocalDate getRecurrenceStartDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.recurrenceStartDate), null);
    }
    public LocalDateTime getRecurrenceStartDateTime() {
        return (LocalDateTime) ObjectUtils.defaultIfNull(new LocalDateTime(this.recurrenceStartDate), null);
    }



    public void setLastTriggerDate(Date lastTriggerDate) {
        this.lastTriggerDate = lastTriggerDate;
    }

    public void setNextTriggerDate(Date nextTriggerDate) {
        this.nextTriggerDate = nextTriggerDate;
    }

    public LocalDateTime getNextTriggerDate() {
        return (LocalDateTime) ObjectUtils.defaultIfNull(new LocalDateTime(this.nextTriggerDate), null);

    }

    public Date getNextTriggerDateInDate(){
        return this.nextTriggerDate;
    }

    public LocalDate getLastTriggerDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.lastTriggerDate), null);
    }
}
