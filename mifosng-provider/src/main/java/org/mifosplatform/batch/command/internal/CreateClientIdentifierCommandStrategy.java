/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.batch.command.internal;

import org.mifosplatform.batch.command.CommandStrategy;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.batch.exception.ErrorHandler;
import org.mifosplatform.batch.exception.ErrorInfo;
import org.mifosplatform.portfolio.client.api.ClientIdentifiersApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;

/**
 * Implements {@link CommandStrategy} to handle
 * activation of a pending client. It passes the contents of the body from the
 * BatchRequest to
 * {@link ClientIdentifiersApiResource} and gets
 * back the response. This class will also catch any errors raised by
 * {@link ClientIdentifiersApiResource} and map
 * those errors to appropriate status codes in BatchResponse.
 *
 * @author Sander van der Heijden
 *
 * @see CommandStrategy
 * @see BatchRequest
 * @see BatchResponse
 */
@Component
public class CreateClientIdentifierCommandStrategy implements CommandStrategy {

    private final ClientIdentifiersApiResource clientIdentifiersApiResource;

    @Autowired
    public CreateClientIdentifierCommandStrategy(final ClientIdentifiersApiResource clientIdentifiersApiResource) {
        this.clientIdentifiersApiResource = clientIdentifiersApiResource;
    }

    @Override
    public BatchResponse execute(final BatchRequest request, @SuppressWarnings("unused") UriInfo uriInfo) {

        final BatchResponse response = new BatchResponse();
        final String responseBody;

        response.setRequestId(request.getRequestId());
        response.setHeaders(request.getHeaders());

        final String[] pathParameters = request.getRelativeUrl().split("/");
        final Long clientId = Long.parseLong(pathParameters[1]);

        // Try-catch blocks to map exceptions to appropriate status codes
        try {

            // Calls 'create' function from 'ClientIdentifiersApiResource' to create a client identifier
            responseBody = clientIdentifiersApiResource.createClientIdentifier (clientId, request.getBody());

            response.setStatusCode(200);
            // Sets the body of the response after the successful activation of
            // the client
            response.setBody(responseBody);

        } catch (RuntimeException e) {

            // Gets an object of type ErrorInfo, containing information about
            // raised exception
            ErrorInfo ex = ErrorHandler.handler(e);

            response.setStatusCode(ex.getStatusCode());
            response.setBody(ex.getMessage());
        }

        return response;
    }

}
