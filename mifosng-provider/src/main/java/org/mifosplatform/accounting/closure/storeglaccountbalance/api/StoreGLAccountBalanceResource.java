/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.closure.storeglaccountbalance.api;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.mifosplatform.accounting.closure.storeglaccountbalance.service.GLClosureJournalEntryBalanceReadPlatformService;
import org.mifosplatform.infrastructure.security.exception.NoAuthorizationException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/glclosureaccountbalance")
@Component
@Scope("singleton")
public class StoreGLAccountBalanceResource {
    private final GLClosureJournalEntryBalanceReadPlatformService glClosureJournalEntryBalanceReadPlatformService;
    private final PlatformSecurityContext platformSecurityContext;
    public static final String CLOSURE_ACCOUNT_BALANCE_REPORT_ENTITY_NAME = "CLOSUREACCOUNTBALANCEREPORT";
    public static final String CLOSURE_ACCOUNT_BALANCE_REPORT_PERMISSION_ENTITY_NAME = "GLClosureAccountBalanceReport";
    
    /**
     * @param glClosureJournalEntryBalanceReadPlatformService
     * @param platformSecurityContext
     */
    @Autowired
    private StoreGLAccountBalanceResource(
            final GLClosureJournalEntryBalanceReadPlatformService glClosureJournalEntryBalanceReadPlatformService, 
            final PlatformSecurityContext platformSecurityContext) {
        this.glClosureJournalEntryBalanceReadPlatformService = glClosureJournalEntryBalanceReadPlatformService;
        this.platformSecurityContext = platformSecurityContext;
    }

    @GET
    @Path("/report")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM })
    public Response report(@Context final UriInfo uriInfo){
        final AppUser appUser = this.platformSecurityContext.authenticatedUser();
        
        // ensure that application user has the right to read the report
        if (appUser.hasNotPermissionForReport(CLOSURE_ACCOUNT_BALANCE_REPORT_PERMISSION_ENTITY_NAME)) {
            throw new NoAuthorizationException("Not authorized to run report: "
                    + CLOSURE_ACCOUNT_BALANCE_REPORT_PERMISSION_ENTITY_NAME);
        }
        
        final MultivaluedMap<String, String> uriQueryParameters = uriInfo.getQueryParameters();
        final File file = this.glClosureJournalEntryBalanceReadPlatformService.
                generateGLClosureAccountBalanceReport(uriQueryParameters);
        
        // this will change to an ok response if and only if a File object is returned above
        ResponseBuilder response = Response.serverError();
        
        if (file != null) {
            response = Response.ok(file);
            
            response.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        }
        
        return response.build();
    }
}
