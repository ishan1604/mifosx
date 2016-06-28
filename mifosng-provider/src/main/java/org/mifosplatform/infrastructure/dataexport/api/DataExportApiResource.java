package org.mifosplatform.infrastructure.dataexport.api;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.dataexport.service.DataExportReadPlatformService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/dataexport")
@Component
@Scope("singleton")
public class DataExportApiResource {

    private final DataExportReadPlatformService dataExportReadPlatformService;
    private final ToApiJsonSerializer<Object> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;

    @Autowired
    public DataExportApiResource(final DataExportReadPlatformService dataExportReadPlatformService,
                             final ToApiJsonSerializer<Object> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper
                             ) {

        this.dataExportReadPlatformService = dataExportReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;

    }

    @GET
    @Path("/template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveDataExportTemplate(@Context final UriInfo uriInfo) {

        return dataExportReadPlatformService.retrieveDataExportTemplate().toString();
    }
}
