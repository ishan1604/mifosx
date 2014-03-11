package org.mifosplatform.infrastructure.survey.api;

import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.survey.data.PovertyLineData;
import org.mifosplatform.infrastructure.survey.service.PovertyLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Created by Cieyou on 3/11/14.
 */
@Path("/povertyLine")
@Component
@Scope("singleton")
public class PovertyLineApiResource {

    private final static Logger logger = LoggerFactory.getLogger(PovertyLineApiResource.class);
    private final DefaultToApiJsonSerializer<PovertyLineData> toApiJsonSerializer;
    private final PlatformSecurityContext context;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final PovertyLineService readService;


    @Autowired
    PovertyLineApiResource(final PlatformSecurityContext context,
                           final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
                           final DefaultToApiJsonSerializer<PovertyLineData> toApiJsonSerializer,
                           final PovertyLineService readService) {

        this.context = context;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.readService = readService;

    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo){

        this.context.authenticatedUser().validateHasReadPermission(PovertyLineApiConstants.POVERTY_LINE_RESOURCE_NAME);

        List<PovertyLineData> povertyLine = this.readService.retrieveAll();
        return this.toApiJsonSerializer.serialize(povertyLine);

    }
}
