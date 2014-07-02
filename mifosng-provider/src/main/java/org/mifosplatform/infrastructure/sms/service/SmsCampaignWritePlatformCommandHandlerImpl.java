package org.mifosplatform.infrastructure.sms.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.api.JsonQuery;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.dataqueries.data.GenericResultsetData;
import org.mifosplatform.infrastructure.dataqueries.domain.Report;
import org.mifosplatform.infrastructure.dataqueries.domain.ReportRepository;
import org.mifosplatform.infrastructure.dataqueries.exception.ReportNotFoundException;
import org.mifosplatform.infrastructure.dataqueries.service.GenericDataService;
import org.mifosplatform.infrastructure.dataqueries.service.ReadReportingService;
import org.mifosplatform.infrastructure.jobs.annotation.CronTarget;
import org.mifosplatform.infrastructure.jobs.domain.ScheduledJobDetail;
import org.mifosplatform.infrastructure.jobs.exception.JobExecutionException;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.infrastructure.jobs.service.SchedularWritePlatformService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.sms.data.PreviewCampaignMessage;
import org.mifosplatform.infrastructure.sms.data.SmsCampaignData;
import org.mifosplatform.infrastructure.sms.data.SmsCampaignValidator;
import org.mifosplatform.infrastructure.sms.domain.SmsCampaign;
import org.mifosplatform.infrastructure.sms.domain.SmsCampaignRepository;
import org.mifosplatform.infrastructure.sms.domain.SmsMessage;
import org.mifosplatform.infrastructure.sms.domain.SmsMessageRepository;
import org.mifosplatform.infrastructure.sms.exception.SmsCampaignNotFound;
import org.mifosplatform.portfolio.calendar.service.CalendarUtils;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.template.domain.TemplateRepository;
import org.mifosplatform.template.exception.TemplateNotFoundException;
import org.mifosplatform.template.service.TemplateMergeService;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.RequestContextFilter;


import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class SmsCampaignWritePlatformCommandHandlerImpl implements SmsCampaignWritePlatformService {


    private final static Logger logger = LoggerFactory.getLogger(SmsCampaignWritePlatformCommandHandlerImpl.class);

    private final PlatformSecurityContext context;

    private final SmsCampaignRepository smsCampaignRepository;
    private final SmsCampaignValidator smsCampaignValidator;
    private final SmsCampaignReadPlatformService smsCampaignReadPlatformService;
    private final ReportRepository reportRepository;
    private final TemplateRepository templateRepository;
    private final TemplateMergeService templateMergeService;
    private final SmsMessageRepository smsMessageRepository;
    private final ClientRepository clientRepository;
    private final SchedularWritePlatformService schedularWritePlatformService;
    private final ReadReportingService readReportingService;
    private final GenericDataService genericDataService;
    private final FromJsonHelper fromJsonHelper;



    @Autowired
    public SmsCampaignWritePlatformCommandHandlerImpl(final PlatformSecurityContext context, final SmsCampaignRepository smsCampaignRepository,
        final SmsCampaignValidator smsCampaignValidator,final SmsCampaignReadPlatformService smsCampaignReadPlatformService,
        final ReportRepository reportRepository,final TemplateRepository templateRepository, final TemplateMergeService templateMergeService,
        final SmsMessageRepository smsMessageRepository,final ClientRepository clientRepository,final SchedularWritePlatformService schedularWritePlatformService,
        final ReadReportingService readReportingService, final GenericDataService genericDataService,final FromJsonHelper fromJsonHelper) {
        this.context = context;
        this.smsCampaignRepository = smsCampaignRepository;
        this.smsCampaignValidator = smsCampaignValidator;
        this.smsCampaignReadPlatformService = smsCampaignReadPlatformService;
        this.reportRepository = reportRepository;
        this.templateRepository = templateRepository;
        this.templateMergeService = templateMergeService;
        this.smsMessageRepository = smsMessageRepository;
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

        this.smsCampaignValidator.validateCreate(command.json());

        final Long runReportId = command.longValueOfParameterNamed(SmsCampaignValidator.runReportId);

        final Report report  = this.reportRepository.findOne(runReportId);
        if(report == null){
            throw new ReportNotFoundException(runReportId);
        }

        SmsCampaign smsCampaign = SmsCampaign.instance(currentUser,report,command);

        this.smsCampaignRepository.save(smsCampaign);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(smsCampaign.getId()) //
                .build();
    }

    @Override
    public CommandProcessingResult update(final Long resourceId, final JsonCommand command) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CommandProcessingResult delete(Long resourceId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    private void insertDirectCampaignIntoSmsOutboundTable(final String smsParams,
                                                          final String textMessageTemplate,final String campaignName){
        try{
            HashMap<String,String> campaignParams = new ObjectMapper().readValue(smsParams, new TypeReference<HashMap<String,String>>(){});

            HashMap<String,String> queryParamForRunReport =  new ObjectMapper().readValue(smsParams, new TypeReference<HashMap<String,String>>(){});

            List<HashMap<String,Object>> runReportObject = this.getRunReportByServiceImpl(campaignParams.get("reportName"),queryParamForRunReport);

            if(runReportObject !=null){
                for(HashMap<String,Object> entry : runReportObject){
                    // add string object to campaignParam object
                    Map<String,Object>  newCampaignParamObject = new HashMap<String, Object>();
                    newCampaignParamObject.putAll(campaignParams);
                    newCampaignParamObject.put("runreports",entry);
                    String textMessage = this.compileSmsTemplate(textMessageTemplate, campaignName, newCampaignParamObject);
                    Integer clientId = (Integer)entry.get("id");
                    Object mobileNo = entry.get("mobileNo");

                    Client client =  this.clientRepository.findOne(clientId.longValue());
                    if(mobileNo !=null) {
                        SmsMessage smsMessage = SmsMessage.pendingSms(null,null,client,null,textMessage,null,mobileNo.toString(),campaignName);
                        this.smsMessageRepository.save(smsMessage);
                    }
                }
            }
        }catch(final IOException e){
            // TODO throw something here
        }

    }

    @Override
    @CronTarget(jobName = JobName.UPDATE_SMS_OUTBOUND_WITH_CAMPAIGN_MESSAGE)
    public void storeTemplateMessageIntoSmsOutBoundTable() throws JobExecutionException {
        final Collection<SmsCampaignData>  smsCampaignDataCollection = this.smsCampaignReadPlatformService.retrieveAllScheduleActiveCampaign();
        if(smsCampaignDataCollection != null){
            for(SmsCampaignData  smsCampaignData : smsCampaignDataCollection){
                org.joda.time.DateTime tenantDateNow = DateUtils.getLocalDateOfTenant().toDateTime(new LocalTime(),DateTimeZone.UTC);
                org.joda.time.DateTime nextTriggerDate = smsCampaignData.getNextTriggerDate();
                logger.info("tenant time " + tenantDateNow.toString() + " trigger time "+nextTriggerDate.toString());
                if(nextTriggerDate.isBefore(tenantDateNow)){
                    insertDirectCampaignIntoSmsOutboundTable(smsCampaignData.getParamValue(),smsCampaignData.getMessage(),smsCampaignData.getCampaignName());
                    this.updateTriggerDates(smsCampaignData.getId());
                }
            }
        }
    }

    private void updateTriggerDates(Long campaignId){
        final SmsCampaign smsCampaign = this.smsCampaignRepository.findOne(campaignId);
        if(smsCampaign == null){
            throw new SmsCampaignNotFound(campaignId);
        }
        LocalDateTime nextTriggerDate = smsCampaign.getNextTriggerDate();
        smsCampaign.setLastTriggerDate(nextTriggerDate.toDate());
        //calculate new trigger date and insert into next trigger date
        final LocalDate nextDay = nextTriggerDate.plusDays(1).toLocalDate();
        final LocalDate nextRuntime = CalendarUtils.getNextRecurringDate(smsCampaign.getRecurrence(), smsCampaign.getNextTriggerDate().toLocalDate(), nextDay) ;
        final LocalDateTime getTime = smsCampaign.getRecurrenceStartDateTime();
        final String dateString = nextRuntime.toString() + " " + getTime.getHourOfDay()+":"+getTime.getMinuteOfHour()+":"+getTime.getSecondOfMinute();
        final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        final LocalDateTime newTriggerDateWithTime = LocalDateTime.parse(dateString,simpleDateFormat);
        smsCampaign.setNextTriggerDate(newTriggerDateWithTime.toDate());
        this.smsCampaignRepository.saveAndFlush(smsCampaign);
    }

    @Transactional
    @Override
    public CommandProcessingResult activateSmsCampaign(Long campaignId, JsonCommand command) {

        this.smsCampaignValidator.validateActivation(command.json());

        final SmsCampaign smsCampaign = this.smsCampaignRepository.findOne(campaignId);

        if(smsCampaign == null){
            throw new SmsCampaignNotFound(campaignId);
        }

        final AppUser currentUser = this.context.authenticatedUser();

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        final LocalDate activationDate = command.localDateValueOfParameterNamed("activationDate");

        smsCampaign.activate(currentUser,fmt,activationDate);

        this.smsCampaignRepository.saveAndFlush(smsCampaign);

        if(smsCampaign.isDirect()){
            insertDirectCampaignIntoSmsOutboundTable(smsCampaign.getParamValue(),smsCampaign.getMessage(),smsCampaign.getCampaignName());
        }else {
            if (smsCampaign.isSchedule()) {
                final LocalDate nextTriggerDate = CalendarUtils.getNextRecurringDate(smsCampaign.getRecurrence(), smsCampaign.getRecurrenceStartDate(), new LocalDate());
                final LocalDateTime getTime = smsCampaign.getRecurrenceStartDateTime();
                /*
                final String dateString = nextTriggerDate.toString() + " " + getTime.getHourOfDay()+":"+getTime.getMinuteOfHour()+":"+getTime.getSecondOfMinute();
                final DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                final LocalDateTime nextTriggerDateWithTime = LocalDateTime.parse(dateString,simpleDateFormat);
                */
                smsCampaign.setNextTriggerDate(getTime.toDate());
                this.smsCampaignRepository.saveAndFlush(smsCampaign);
            }
        }

        /*
          if campaign is direct insert campaign message into sms outbound table
          else if its a schedule create a job process for it
         */
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(smsCampaign.getId()) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult closeSmsCampaign(Long campaignId, JsonCommand command) {

        final AppUser currentUser = this.context.authenticatedUser();
        this.smsCampaignValidator.validateClosedDate(command.json());

        final SmsCampaign smsCampaign = this.smsCampaignRepository.findOne(campaignId);
        if(smsCampaign == null){
            throw new SmsCampaignNotFound(campaignId);
        }

        final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
        final LocalDate closureDate = command.localDateValueOfParameterNamed("closureDate");

        smsCampaign.close(currentUser,fmt,closureDate);

        this.smsCampaignRepository.saveAndFlush(smsCampaign);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(smsCampaign.getId()) //
                .build();
    }

    private String compileSmsTemplate(final String textMessageTemplate,final String campaignName , final Map<String, Object> smsParams)  {
        final MustacheFactory mf = new DefaultMustacheFactory();
        final Mustache mustache = mf.compile(new StringReader(textMessageTemplate), campaignName);

        final StringWriter stringWriter = new StringWriter();
        mustache.execute(stringWriter, smsParams);

        return stringWriter.toString();
    }

    private List<HashMap<String,Object>> getRunReportByServiceImpl(final String reportName,final Map<String, String> queryParams) throws IOException {
        final String reportType ="report";

        List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
        final GenericResultsetData results = this.readReportingService.retrieveGenericResultSetForSmsCampaign(reportName,
                reportType, queryParams);
        final String response = this.genericDataService.generateJsonFromGenericResultsetData(results);
        resultList = new ObjectMapper().readValue(response, new TypeReference<List<HashMap<String,Object>>>(){});
        return resultList;
    }

    @Override
    public PreviewCampaignMessage previewMessage(final JsonQuery query) {
        PreviewCampaignMessage campaignMessage = null;
        final AppUser currentUser = this.context.authenticatedUser();
        this.smsCampaignValidator.validatePreviewMessage(query.json());
        final String smsParams = this.fromJsonHelper.extractStringNamed("paramValue", query.parsedJson()) ;
        final String textMessageTemplate = this.fromJsonHelper.extractStringNamed("message", query.parsedJson());

        try{
            HashMap<String,String> campaignParams = new ObjectMapper().readValue(smsParams, new TypeReference<HashMap<String,String>>(){});

            HashMap<String,String> queryParamForRunReport =  new ObjectMapper().readValue(smsParams, new TypeReference<HashMap<String,String>>(){});

            List<HashMap<String,Object>> runReportObject = this.getRunReportByServiceImpl(campaignParams.get("reportName"),queryParamForRunReport);

            if(runReportObject !=null){
                for(HashMap<String,Object> entry : runReportObject){
                    // add string object to campaignParam object
                    Map<String,Object>  newCampaignParamObject = new HashMap<String, Object>();
                    newCampaignParamObject.putAll(campaignParams);
                    newCampaignParamObject.put("runreports",entry);
                    String textMessage = this.compileSmsTemplate(textMessageTemplate,"SmsCampaign", newCampaignParamObject);
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
}
