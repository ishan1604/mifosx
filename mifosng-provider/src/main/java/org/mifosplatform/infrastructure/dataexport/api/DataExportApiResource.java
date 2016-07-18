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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Path("/dataexport")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DataExportApiResource {

    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("entity","id", "name", "displayName", "externalId",
            "principalAmount","outstandingAmount", "office", "status"));
    private final PlatformSecurityContext platformSecurityContext;
    private final DataExportReadPlatformService dataExportReadPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ToApiJsonSerializer<Object> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;

    @Autowired
    public DataExportApiResource(final DataExportReadPlatformService dataExportReadPlatformService,
                                 final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                                 final PlatformSecurityContext platformSecurityContext,
                                 final ToApiJsonSerializer<Object> toApiJsonSerializer,
                                 final ApiRequestParameterHelper apiRequestParameterHelper) {
        this.platformSecurityContext = platformSecurityContext;
        this.dataExportReadPlatformService = dataExportReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public String retrieveAllDataExportProcesses(@Context final UriInfo uriInfo){

        return this.dataExportReadPlatformService.retrieveDataExportRequestDataCollection().toString();
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveDataExportTemplate(@QueryParam(DataExportApiConstants.ENTITY) final String entity,
                                             @Context final UriInfo uriInfo) {

        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(DataExportBaseEntityEnum.valueOf(entity).name());

        final DataExportRequestData dataExportRequestData = this.dataExportReadPlatformService.retrieveDataExportRequestData(entity);
        final DataExportTemplateData dataExportTemplate = this.dataExportReadPlatformService.retrieveDataExportTemplate(entity);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings,dataExportTemplate);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createDataExport(@QueryParam(DataExportApiConstants.ENTITY) final String entity, final String apiRequestBodyAsJson) {

        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(DataExportBaseEntityEnum.valueOf(entity).name());

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .withJson(apiRequestBodyAsJson) //
                .createDataExport() //
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

        this.platformSecurityContext.authenticatedUser().validateHasReadPermission(DataExportBaseEntityEnum.valueOf(entity).name());

        return this.dataExportReadPlatformService.downloadDataExportFile(entity, dataExportProcessId, fileFormat);
    }
}
