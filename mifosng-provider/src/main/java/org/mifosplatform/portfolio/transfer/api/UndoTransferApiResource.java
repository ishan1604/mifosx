/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.transfer.api;

import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.transfer.data.UndoTransferClientData;
import org.mifosplatform.portfolio.transfer.data.UndoTransferGroupData;
import org.mifosplatform.portfolio.transfer.service.UndoTransferReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;

@Path("/undoTransfer")
@Component
@Scope("singleton")
public class UndoTransferApiResource {

    private final String resourceNameForPermissions = "UNDOTRANSFER";


    private final PlatformSecurityContext context;
    private final ToApiJsonSerializer<UndoTransferClientData> toApiJsonSerializer;
    private final ToApiJsonSerializer<UndoTransferGroupData>  undoTransferGroupDataToApiJsonSerializer;
    private final UndoTransferReadPlatformService undoTransferReadPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;

    @Autowired
    public UndoTransferApiResource(final PlatformSecurityContext context,final ToApiJsonSerializer<UndoTransferClientData> toApiJsonSerializer,
                                   final ToApiJsonSerializer<UndoTransferGroupData> undoTransferGroupDataToApiJsonSerializer,
                                   final UndoTransferReadPlatformService undoTransferReadPlatformService,final ApiRequestParameterHelper apiRequestParameterHelper) {
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.undoTransferGroupDataToApiJsonSerializer = undoTransferGroupDataToApiJsonSerializer;
        this.undoTransferReadPlatformService = undoTransferReadPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
    }

    @GET()
    @Path("clients/")
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllUndoTransferClientData(@PathParam("clientId") final Long clientId,@Context final UriInfo uriInfo){
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final Collection<UndoTransferClientData> undoTransferClientData = this.undoTransferReadPlatformService.retrieveAllTransferredClients();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings,undoTransferClientData);
    }



    @GET()
    @Path("clients/{clientId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveUndoTransferClientData(@PathParam("clientId") final Long clientId,@Context final UriInfo uriInfo){
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final UndoTransferClientData undoTransferClientData = this.undoTransferReadPlatformService.retrieveUndoTransferClientData(clientId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings,undoTransferClientData);
    }

    @GET()
    @Path("groups/{groupId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveUndoTransferGroupData(@PathParam("groupId") final Long groupId,@Context final UriInfo uriInfo){
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final UndoTransferGroupData undoTransferGroupData = this.undoTransferReadPlatformService.retrieveUndoTransferGroupData(groupId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.undoTransferGroupDataToApiJsonSerializer.serialize(settings,undoTransferGroupData);
    }

    @GET()
    @Path("groups")
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllUndoTransferGroupData(@PathParam("groupId") final Long groupId,@Context final UriInfo uriInfo){
        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);
        final Collection<UndoTransferGroupData> undoTransferGroupData = this.undoTransferReadPlatformService.retrieveAllTransferredGroups();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.undoTransferGroupDataToApiJsonSerializer.serialize(settings,undoTransferGroupData);
    }
}
