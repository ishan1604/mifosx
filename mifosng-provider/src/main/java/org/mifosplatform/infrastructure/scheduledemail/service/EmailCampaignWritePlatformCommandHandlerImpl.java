/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.scheduledemail.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.api.JsonQuery;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.dataqueries.data.GenericResultsetData;
import org.mifosplatform.infrastructure.dataqueries.domain.Report;
import org.mifosplatform.infrastructure.dataqueries.domain.ReportRepository;
import org.mifosplatform.infrastructure.dataqueries.exception.ReportNotFoundException;
import org.mifosplatform.infrastructure.dataqueries.service.GenericDataService;
import org.mifosplatform.infrastructure.dataqueries.service.ReadReportingService;
import org.mifosplatform.infrastructure.scheduledemail.exception.EmailCampaignMustBeClosedToBeDeletedException;
import org.mifosplatform.infrastructure.scheduledemail.exception.EmailCampaignMustBeClosedToEditException;
import org.mifosplatform.infrastructure.scheduledemail.exception.EmailCampaignNotFound;
import org.mifosplatform.infrastructure.jobs.annotation.CronTarget;
import org.mifosplatform.infrastructure.jobs.exception.JobExecutionException;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.infrastructure.jobs.service.SchedularWritePlatformService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.scheduledemail.data.PreviewCampaignMessage;
import org.mifosplatform.infrastructure.scheduledemail.data.EmailCampaignData;
import org.mifosplatform.infrastructure.scheduledemail.data.EmailCampaignValidator;
import org.mifosplatform.infrastructure.scheduledemail.domain.EmailCampaign;
import org.mifosplatform.infrastructure.scheduledemail.domain.EmailCampaignRepository;
import org.mifosplatform.infrastructure.scheduledemail.domain.EmailMessage;
import org.mifosplatform.infrastructure.scheduledemail.domain.EmailMessageRepository;
import org.mifosplatform.portfolio.calendar.service.CalendarUtils;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.template.domain.TemplateRepository;
import org.mifosplatform.template.service.TemplateMergeService;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

@Service
public class EmailCampaignWritePlatformCommandHandlerImpl implements EmailCampaignWritePlatformService {


    private final static Logger logger = LoggerFactory.getLogger(EmailCampaignWritePlatformCommandHandlerImpl.class);

    private final PlatformSecurityContext context;

    private final EmailCampaignRepository emailCampaignRepository;
    private final EmailCampaignValidator emailCampaignValidator;
    private final EmailCampaignReadPlatformService emailCampaignReadPlatformService;
    private final ReportRepository reportRepository;
    private final TemplateRepository templateRepository;
    private final TemplateMergeService templateMergeService;
    private final EmailMessageRepository emailMessageRepository;
    private final ClientRepository clientRepository;
    private final SchedularWritePlatformService schedularWritePlatformService;
    private final ReadReportingService readReportingService;
    private final GenericDataService genericDataService;
    private final FromJsonHelper fromJsonHelper;



    @Autowired
    public EmailCampaignWritePlatformCommandHandlerImpl(final PlatformSecurityContext context, final EmailCampaignRepository emailCampaignRepository,
        final EmailCampaignValidator emailCampaignValidator,final EmailCampaignReadPlatformService emailCampaignReadPlatformService,
        final ReportRepository reportRepository,final TemplateRepository templateRepository, final TemplateMergeService templateMergeService,
        final EmailMessageRepository emailMessageRepository,final ClientRepository clientRepository,final SchedularWritePlatformService schedularWritePlatformService,
        final ReadReportingService readReportingService, final GenericDataService genericDataService,final FromJsonHelper fromJsonHelper) {
        this.context = context;
        this.emailCampaignRepository = emailCampaignRepository;
        this.emailCampaignValidator = emailCampaignValidator;
        this.emailCampaignReadPlatformService = emailCampaignReadPlatformService;
        this.reportRepository = reportRepository;
        this.templateRepository = templateRepository;
        this.templateMergeService = templateMergeService;
        this.emailMessageRepository = emailMessageRepository;
        this.clientRepository = clientRepository;
        this.schedularWritePlatformService = schedularWritePlatformService;
        this.readReportingService = readReportingService;
        this.genericDataService = genericDataService;
        this.fromJsonHelper = fromJsonHelper;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(JsonCommand command) {

        final AppUser currentUser = this.context.authenticatedUser();

        this.emailCampaignValidator.validateCreate(command.json());

        final Long businessRuleId = command.longValueOfParameterNamed(EmailCampaignValidator.businessRuleId);

        final Report businessRule  = this.reportRepository.findOne(businessRuleId);
        if(businessRule == null){
            throw new ReportNotFoundException(businessRuleId);
        }

        final Long reportId = command.longValueOfParameterNamed(EmailCampaignValidator.stretchyReportId);

        final Report report  = this.reportRepository.findOne(reportId);
        if(report == null){
            throw new ReportNotFoundException(reportId);
        }

        EmailCampaign emailCampaign = EmailCampaign.instance(currentUser,businessRule,report,command);

        this.emailCampaignRepository.save(emailCampaign);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(emailCampaign.getId()) //
                .build();
    }
    @Transactional
    @Override
    public CommandProcessingResult update(final Long resourceId, final JsonCommand command) {
        try{
            final AppUser currentUser = this.context.authenticatedUser();

            this.emailCampaignValidator.validateForUpdate(command.json());
            final EmailCampaign emailCampaign = this.emailCampaignRepository.findOne(resourceId);

            if(emailCampaign == null){ throw new EmailCampaignNotFound(resourceId);}
            if(emailCampaign.isActive()){ throw new EmailCampaignMustBeClosedToEditException(emailCampaign.getId());}
            final Map<String, Object> changes = emailCampaign.update(command);

            if(changes.containsKey(EmailCampaignValidator.businessRuleId)){
                final Long newValue = command.longValueOfParameterNamed(EmailCampaignValidator.businessRuleId);
                final Report reportId = this.reportRepository.findOne(newValue);
                if(reportId == null){ throw new ReportNotFoundException(newValue);}
                emailCampaign.updateBusinessRuleId(reportId);
            }

            if(!changes.isEmpty()){
                this.emailCampaignRepository.saveAndFlush(emailCampaign);
            }
            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(resourceId) //
                    .with(changes) //
                    .build();
        }catch(final DataIntegrityViolationException dve){
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }

    }
    @Transactional
    @Override
    public CommandProcessingResult delete(final Long resourceId) {
        final AppUser currentUser = this.context.authenticatedUser();

        final EmailCampaign emailCampaign = this.emailCampaignRepository.findOne(resourceId);

        if(emailCampaign == null){ throw new EmailCampaignNotFound(resourceId);}
        if(emailCampaign.isActive()){ throw new EmailCampaignMustBeClosedToBeDeletedException(emailCampaign.getId());}

        /*
          Do not delete but set a boolean is_visible to zero
         */
        emailCampaign.delete();
        this.emailCampaignRepository.saveAndFlush(emailCampaign);

        return new CommandProcessingResultBuilder() //
                .withEntityId(emailCampaign.getId()) //
                .build();

    }


    private void insertDirectCampaignIntoEmailOutboundTable(final String emailParams, final String emailSubject,
                                                          final String messageTemplate,final String campaignName){
        try{
            HashMap<String,String> campaignParams = new ObjectMapper().readValue(emailParams, new TypeReference<HashMap<String,String>>(){});

            HashMap<String,String> queryParamForRunReport =  new ObjectMapper().readValue(emailParams, new TypeReference<HashMap<String,String>>(){});

            List<HashMap<String,Object>> runReportObject = this.getRunReportByServiceImpl(campaignParams.get("reportName"),queryParamForRunReport);

            if(runReportObject !=null){
                for(HashMap<String,Object> entry : runReportObject){
                    String message = this.compileEmailTemplate(messageTemplate, campaignName, entry);
                    Integer clientId = (Integer)entry.get("id");

                    Client client =  this.clientRepository.findOne(clientId.longValue());
                    String emailAddress = client.emailAddress();

                    if(emailAddress !=null) {
                        EmailMessage emailMessage = EmailMessage.pendingEmail(null,null,client,null,emailSubject,message,null,emailAddress,campaignName);
                        this.emailMessageRepository.save(emailMessage);
                    }
                }
            }
        }catch(final IOException e){
            // TODO throw something here
        }

    }

    @Override
    @CronTarget(jobName = JobName.UPDATE_EMAIL_OUTBOUND_WITH_CAMPAIGN_MESSAGE)
    public void storeTemplateMessageIntoEmailOutBoundTable() throws JobExecutionException {
        final Collection<EmailCampaignData>  emailCampaignDataCollection = this.emailCampaignReadPlatformService.retrieveAllScheduleActiveCampaign();
        if(emailCampaignDataCollection != null){
            for(EmailCampaignData  emailCampaignData : emailCampaignDataCollection){
                LocalDateTime tenantDateNow = tenantDateTime();
                LocalDateTime nextTriggerDate = emailCampaignData.getNextTriggerDate().toLocalDateTime();

                logger.info("tenant time " + tenantDateNow.toString() + " trigger time "+nextTriggerDate.toString());
                if(nextTriggerDate.isBefore(tenantDateNow)){
                    insertDirectCampaignIntoEmailOutboundTable(emailCampaignData.getParamValue(),emailCampaignData.getEmailSubject(), emailCampaignData.getMessage(),emailCampaignData.getCampaignName());
                    this.updateTriggerDates(emailCampaignData.getId());
                }
            }
        }
    }

    private void updateTriggerDates(Long campaignId){
        final EmailCampaign emailCampaign = this.emailCampaignRepository.findOne(campaignId);
        if(emailCampaign == null){
            throw new EmailCampaignNotFound(campaignId);
        }
        LocalDateTime nextTriggerDate = emailCampaign.getNextTriggerDate();
        emailCampaign.setLastTriggerDate(nextTriggerDate.toDate());
        //calculate new trigger date and insert into next trigger date

        /**
         * next run time has to be in the future if not calculate a new future date
         */
        LocalDate nextRuntime = CalendarUtils.getNextRecurringDate(emailCampaign.getRecurrence(), emailCampaign.getNextTriggerDate().toLocalDate(),nextTriggerDate.toLocalDate()) ;
        if(nextRuntime.isBefore(DateUtils.getLocalDateOfTenant())){ // means next run time is in the past calculate a new future date
            nextRuntime = CalendarUtils.getNextRecurringDate(emailCampaign.getRecurrence(), emailCampaign.getNextTriggerDate().toLocalDate(),DateUtils.getLocalDateOfTenant()) ;
        }
        final LocalDateTime getTime = emailCampaign.getRecurrenceStartDateTime();
        final String dateString = nextRuntime.toString() + " " + getTime.getHourOfDay()+":"+getTime.getMinuteOfHour()+":"+getTime.getSecondOfMinute();
        final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        final LocalDateTime newTriggerDateWithTime = LocalDateTime.parse(dateString,simpleDateFormat);


        emailCampaign.setNextTriggerDate(newTriggerDateWithTime.toDate());
        this.emailCampaignRepository.saveAndFlush(emailCampaign);
    }

    @Transactional
    @Override
    public CommandProcessingResult activateEmailCampaign(Long campaignId, JsonCommand command) {
        final AppUser currentUser = this.context.authenticatedUser();

        this.emailCampaignValidator.validateActivation(command.json());

        final EmailCampaign emailCampaign = this.emailCampaignRepository.findOne(campaignId);

        if(emailCampaign == null){
            throw new EmailCampaignNotFound(campaignId);
        }



        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        final LocalDate activationDate = command.localDateValueOfParameterNamed("activationDate");

        emailCampaign.activate(currentUser,fmt,activationDate);

        this.emailCampaignRepository.saveAndFlush(emailCampaign);

        if(emailCampaign.isDirect()){
            insertDirectCampaignIntoEmailOutboundTable(emailCampaign.getParamValue(),emailCampaign.getEmailSubject(),emailCampaign.getEmailMessage(),emailCampaign.getCampaignName());
        }else {
            if (emailCampaign.isSchedule()) {

                /**
                 * if recurrence start date is in the future calculate
                 * next trigger date if not use recurrence start date us next trigger
                 * date when activating
                 */
                LocalDate nextTriggerDate = null;
                if(emailCampaign.getRecurrenceStartDateTime().isBefore(tenantDateTime())){
                    nextTriggerDate = CalendarUtils.getNextRecurringDate(emailCampaign.getRecurrence(), emailCampaign.getRecurrenceStartDate(), DateUtils.getLocalDateOfTenant());
                }else{
                    nextTriggerDate = emailCampaign.getRecurrenceStartDate();
                }
                // to get time of tenant
                final LocalDateTime getTime = emailCampaign.getRecurrenceStartDateTime();

                final String dateString = nextTriggerDate.toString() + " " + getTime.getHourOfDay()+":"+getTime.getMinuteOfHour()+":"+getTime.getSecondOfMinute();
                final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                final LocalDateTime nextTriggerDateWithTime = LocalDateTime.parse(dateString,simpleDateFormat);

                emailCampaign.setNextTriggerDate(nextTriggerDateWithTime.toDate());
                this.emailCampaignRepository.saveAndFlush(emailCampaign);
            }
        }

        /*
          if campaign is direct insert campaign message into scheduledemail outbound table
          else if its a schedule create a job process for it
         */
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(emailCampaign.getId()) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult closeEmailCampaign(Long campaignId, JsonCommand command) {

        final AppUser currentUser = this.context.authenticatedUser();
        this.emailCampaignValidator.validateClosedDate(command.json());

        final EmailCampaign emailCampaign = this.emailCampaignRepository.findOne(campaignId);
        if(emailCampaign == null){
            throw new EmailCampaignNotFound(campaignId);
        }

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        final LocalDate closureDate = command.localDateValueOfParameterNamed("closureDate");

        emailCampaign.close(currentUser,fmt,closureDate);

        this.emailCampaignRepository.saveAndFlush(emailCampaign);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(emailCampaign.getId()) //
                .build();
    }

    private String compileEmailTemplate(final String textMessageTemplate,final String campaignName , final Map<String, Object> emailParams)  {
        final MustacheFactory mf = new DefaultMustacheFactory();
        final Mustache mustache = mf.compile(new StringReader(textMessageTemplate), campaignName);

        final StringWriter stringWriter = new StringWriter();
        mustache.execute(stringWriter, emailParams);

        return stringWriter.toString();
    }

    private List<HashMap<String,Object>> getRunReportByServiceImpl(final String reportName,final Map<String, String> queryParams) throws IOException {
        final String reportType ="report";

        List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
        final GenericResultsetData results = this.readReportingService.retrieveGenericResultSetForEmailCampaign(reportName,
                reportType, queryParams);
        final String response = this.genericDataService.generateJsonFromGenericResultsetData(results);
        resultList = new ObjectMapper().readValue(response, new TypeReference<List<HashMap<String,Object>>>(){});
        //loop changes array date to string date
        for(HashMap<String,Object> entry : resultList){
            for(Map.Entry<String,Object> map: entry.entrySet()){
                String key = map.getKey();
                Object ob  = map.getValue();
                if(ob instanceof ArrayList && ((ArrayList) ob).size() == 3){
                    String changeArrayDateToStringDate =  ((ArrayList) ob).get(2).toString() +"-"+((ArrayList) ob).get(1).toString() +"-"+((ArrayList) ob).get(0).toString();
                    entry.put(key,changeArrayDateToStringDate);
                }
            }
        }
        return resultList;
    }

    @Override
    public PreviewCampaignMessage previewMessage(final JsonQuery query) {
        PreviewCampaignMessage campaignMessage = null;
        final AppUser currentUser = this.context.authenticatedUser();
        this.emailCampaignValidator.validatePreviewMessage(query.json());
        final String emailParams = this.fromJsonHelper.extractStringNamed("paramValue", query.parsedJson()) ;
        final String textMessageTemplate = this.fromJsonHelper.extractStringNamed("emailMessage", query.parsedJson());

        try{
            HashMap<String,String> campaignParams = new ObjectMapper().readValue(emailParams, new TypeReference<HashMap<String,String>>(){});

            HashMap<String,String> queryParamForRunReport =  new ObjectMapper().readValue(emailParams, new TypeReference<HashMap<String,String>>(){});

            List<HashMap<String,Object>> runReportObject = this.getRunReportByServiceImpl(campaignParams.get("reportName"),queryParamForRunReport);

            if(runReportObject !=null){
                for(HashMap<String,Object> entry : runReportObject){
                    // add string object to campaignParam object
                    String textMessage = this.compileEmailTemplate(textMessageTemplate,"EmailCampaign", entry);
                    if(!textMessage.isEmpty()) {
                        final Integer totalMessage = runReportObject.size();
                        campaignMessage = new PreviewCampaignMessage(textMessage,totalMessage);
                        break;
                    }
                }
            }
        }catch(final IOException e){
            // TODO throw something here
        }

        return campaignMessage;

    }
    @Transactional
    @Override
    public CommandProcessingResult reactivateEmailCampaign(final Long campaignId, JsonCommand command) {

        this.emailCampaignValidator.validateActivation(command.json());

        final AppUser currentUser = this.context.authenticatedUser();

        final EmailCampaign emailCampaign = this.emailCampaignRepository.findOne(campaignId);

        if(emailCampaign == null){ throw new EmailCampaignNotFound(campaignId);}

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        final LocalDate reactivationDate = command.localDateValueOfParameterNamed("activationDate");
        emailCampaign.reactivate(currentUser,fmt,reactivationDate);
        if (emailCampaign.isSchedule()) {

            /**
             * if recurrence start date is in the future calculate
             * next trigger date if not use recurrence start date us next trigger
             * date when activating
             */
            LocalDate nextTriggerDate = null;
            if(emailCampaign.getRecurrenceStartDateTime().isBefore(tenantDateTime())){
                nextTriggerDate = CalendarUtils.getNextRecurringDate(emailCampaign.getRecurrence(), emailCampaign.getRecurrenceStartDate(), DateUtils.getLocalDateOfTenant());
            }else{
                nextTriggerDate = emailCampaign.getRecurrenceStartDate();
            }
            // to get time of tenant
            final LocalDateTime getTime = emailCampaign.getRecurrenceStartDateTime();

            final String dateString = nextTriggerDate.toString() + " " + getTime.getHourOfDay()+":"+getTime.getMinuteOfHour()+":"+getTime.getSecondOfMinute();
            final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            final LocalDateTime nextTriggerDateWithTime = LocalDateTime.parse(dateString,simpleDateFormat);

            emailCampaign.setNextTriggerDate(nextTriggerDateWithTime.toDate());
            this.emailCampaignRepository.saveAndFlush(emailCampaign);
        }



        return new CommandProcessingResultBuilder() //
                .withEntityId(emailCampaign.getId()) //
                .build();

    }

    private void handleDataIntegrityIssues(@SuppressWarnings("unused") final JsonCommand command, final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();

        throw new PlatformDataIntegrityException("error.msg.scheduledemail.campaign.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }

    private LocalDateTime tenantDateTime(){
        LocalDateTime today = new LocalDateTime();
        final MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();

        if (tenant != null) {
            final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
            if (zone != null) {
                today = new LocalDateTime(zone);
            }
        }
        return  today;
    }
}
