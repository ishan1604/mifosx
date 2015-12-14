/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.api;

import com.google.gson.JsonElement;
import org.apache.commons.lang.StringUtils;
import org.mifosplatform.accounting.closure.bookoffincomeandexpense.data.IncomeAndExpenseBookingData;
import org.mifosplatform.accounting.closure.bookoffincomeandexpense.service.CalculateIncomeAndExpenseBooking;
import org.mifosplatform.accounting.closure.data.GLClosureData;
import org.mifosplatform.accounting.closure.service.GLClosureReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.api.JsonQuery;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/glclosures")
@Component
@Scope("singleton")
public class GLClosuresApiResource {

    private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "officeId", "officeName",
            "closingDate", "deleted", "createdDate", "lastUpdatedDate", "createdByUserId", "createdByUsername", "lastUpdatedByUserId",
            "lastUpdatedByUsername"));

    private final String resourceNameForPermission = "GLCLOSURE";

    private final GLClosureReadPlatformService glClosureReadPlatformService;
    private final OfficeReadPlatformService officeReadPlatformService;
    private final DefaultToApiJsonSerializer<GLClosureData> apiJsonSerializerService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PlatformSecurityContext context;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final FromJsonHelper fromJsonHelper;
    private final CalculateIncomeAndExpenseBooking calculateIncomeAndExpenseBooking;
    private final DefaultToApiJsonSerializer<IncomeAndExpenseBookingData> incomeAndExpenseBookingDataDefaultToApiJsonSerializer;



    @Autowired
    public GLClosuresApiResource(final PlatformSecurityContext context, final GLClosureReadPlatformService glClosureReadPlatformService,
            final DefaultToApiJsonSerializer<GLClosureData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final OfficeReadPlatformService officeReadPlatformService,
            final FromJsonHelper fromJsonHelper,final CalculateIncomeAndExpenseBooking calculateIncomeAndExpenseBooking,
            final DefaultToApiJsonSerializer<IncomeAndExpenseBookingData> incomeAndExpenseBookingDataDefaultToApiJsonSerializer) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiJsonSerializerService = toApiJsonSerializer;
        this.glClosureReadPlatformService = glClosureReadPlatformService;
        this.officeReadPlatformService = officeReadPlatformService;
        this.fromJsonHelper = fromJsonHelper;
        this.calculateIncomeAndExpenseBooking = calculateIncomeAndExpenseBooking;
        this.incomeAndExpenseBookingDataDefaultToApiJsonSerializer = incomeAndExpenseBookingDataDefaultToApiJsonSerializer;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllClosures(@Context final UriInfo uriInfo, @QueryParam("officeId") final Long officeId) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermission);
        final List<GLClosureData> glClosureDatas = this.glClosureReadPlatformService.retrieveAllGLClosures(officeId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.apiJsonSerializerService.serialize(settings, glClosureDatas, RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{glClosureId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveClosure(@PathParam("glClosureId") final Long glClosureId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermission);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        final GLClosureData glClosureData = this.glClosureReadPlatformService.retrieveGLClosureById(glClosureId);
        if (settings.isTemplate()) {
            glClosureData.setAllowedOffices(this.officeReadPlatformService.retrieveAllOfficesForDropdown());
        }

        return this.apiJsonSerializerService.serialize(settings, glClosureData, RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createGLClosure(final String jsonRequestBody) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createGLClosure().withJson(jsonRequestBody).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }

    @PUT
    @Path("{glClosureId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateGLClosure(@PathParam("glClosureId") final Long glClosureId, final String jsonRequestBody) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateGLClosure(glClosureId).withJson(jsonRequestBody).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }

    @DELETE
    @Path("{glClosureId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteGLClosure(@PathParam("glClosureId") final Long glClosureId,final String jsonRequestBody) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteGLClosure(glClosureId).withJson(jsonRequestBody).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializerService.serialize(result);
    }


    @POST
    @Path("previewIncomeAndExpense")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String previewIncomeAndExpenseBooking(@QueryParam("command") final String commandParam,
                                                               @Context final UriInfo uriInfo, final String apiRequestBodyAsJson) {
//        if (is(commandParam, "previewIncomeAndExpense")) {

            final JsonElement parsedQuery = this.fromJsonHelper.parse(apiRequestBodyAsJson);
            final JsonQuery query = JsonQuery.from(apiRequestBodyAsJson, parsedQuery, this.fromJsonHelper);
//
            final Collection<IncomeAndExpenseBookingData> incomeAndExpenseBookingCollection = this.calculateIncomeAndExpenseBooking.CalculateIncomeAndExpenseBookings(query);
//
            final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
            return this.incomeAndExpenseBookingDataDefaultToApiJsonSerializer.serialize(settings, incomeAndExpenseBookingCollection, new HashSet<String>());
//        }


    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }

}