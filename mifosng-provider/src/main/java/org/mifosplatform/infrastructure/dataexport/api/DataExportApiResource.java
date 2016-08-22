/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.api;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.dataexport.data.DataExportBaseEntityEnum;
import org.mifosplatform.infrastructure.dataexport.data.DataExportRequestData;
import org.mifosplatform.infrastructure.dataexport.data.DataExportTemplateData;
import org.mifosplatform.infrastructure.dataexport.domain.DataExport;
import org.mifosplatform.infrastructure.dataexport.domain.DataExportProcess;
import org.mifosplatform.infrastructure.dataexport.domain.DataExportProcessRepository;
import org.mifosplatform.infrastructure.dataexport.domain.DataExportRepository;
import org.mifosplatform.infrastructure.dataexport.service.DataExportReadPlatformService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.*;

@Path("/dataexport")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DataExportApiResource {

    private final PlatformSecurityContext platformSecurityContext;
    private final DataExportReadPlatformService dataExportReadPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ToApiJsonSerializer<Object> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DataExportProcessRepository dataExportProcessRepository;
    private final DataExportRepository dataExportRepository;

    @Autowired
    public DataExportApiResource(final DataExportReadPlatformService dataExportReadPlatformService,
                                 final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                                 final PlatformSecurityContext platformSecurityContext, final DataExportProcessRepository dataExportProcessRepository,
                                 final ToApiJsonSerializer<Object> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
                                 final DataExportRepository dataExportRepository) {
        this.platformSecurityContext = platformSecurityContext;
        this.dataExportReadPlatformService = dataExportReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.dataExportProcessRepository = dataExportProcessRepository;
        this.dataExportRepository = dataExportRepository;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public String retrieveAllDataExportProcesses(@Context final UriInfo uriInfo){

        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(DataExportApiConstants.DATA_EXPORT);

        final Collection<DataExportProcess> dataExportProcesses = this.dataExportProcessRepository.findAll();

        return this.toApiJsonSerializer.serialize(dataExportProcesses);
    }

    @GET
    @Path("{resourceId}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public String retrieveOneDataExport(@PathParam("resourceId") final Long resourceId, @Context final UriInfo uriInfo){

        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(DataExportApiConstants.DATA_EXPORT);

        final DataExport dataExport = this.dataExportRepository.findOne(resourceId);

        return this.toApiJsonSerializer.serialize(dataExport);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveDataExportTemplate(@QueryParam(DataExportApiConstants.ENTITY) final String entity,
                                             @Context final UriInfo uriInfo) {

        List<String> entities = new ArrayList<>();
        for(DataExportBaseEntityEnum entityEnum:DataExportBaseEntityEnum.values()){entities.add(entityEnum.name());}

        if(entity!=null && entities.contains(entity)){
            this.platformSecurityContext.authenticatedUser().validateHasReadPermission(entity);
        }else{
            this.platformSecurityContext.authenticatedUser().validateHasPermissionTo("READ",entities);
        }

        final DataExportTemplateData dataExportTemplate = this.dataExportReadPlatformService.retrieveDataExportTemplate(entity);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings,dataExportTemplate);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createDataExport(@QueryParam(DataExportApiConstants.ENTITY) final String entity, final String apiRequestBodyAsJson) {

        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(entity);

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .createDataExport(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("/download")
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
    public Response downloadOutgoingPaymentsFile(@QueryParam(DataExportApiConstants.DATA_EXPORT_PROCESS_ID)
                                                 final Long dataExportProcessId,
                                                 @QueryParam(DataExportApiConstants.ENTITY) final String entity,
                                                 @QueryParam(DataExportApiConstants.FILE_FORMAT) final String fileFormat) {

        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(entity);

        return this.dataExportReadPlatformService.downloadDataExportFile(entity, dataExportProcessId, fileFormat);
    }
}
