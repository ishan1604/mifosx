/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepositoryWrapper;
import org.mifosplatform.portfolio.loanaccount.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class GroupLoanMemberAllocationAssembler {

    private final FromJsonHelper fromApiJsonHelper;
    private final GroupLoanMemberAllocationRepository groupLoanMemberAllocationRepository;
    private final ClientRepositoryWrapper clientRepository;

    @Autowired
    public GroupLoanMemberAllocationAssembler(final FromJsonHelper fromApiJsonHelper, 
            final GroupLoanMemberAllocationRepository groupLoanMemberAllocationRepository, 
            final ClientRepositoryWrapper clientRepository) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.groupLoanMemberAllocationRepository = groupLoanMemberAllocationRepository;
        this.clientRepository = clientRepository;
    }

    public Set<GroupLoanMemberAllocation> fromParsedJson(final JsonElement element) {

        final Set<GroupLoanMemberAllocation> groupLoanMemberAllocations = new HashSet<>();

        if (element.isJsonObject()) {
            final JsonObject topLevelJsonElement = element.getAsJsonObject();
            final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

            if (topLevelJsonElement.has("groupMemberAllocation") && topLevelJsonElement.get("groupMemberAllocation").isJsonArray()) {
                final JsonArray array = topLevelJsonElement.get("groupMemberAllocation").getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {

                    final JsonObject loanChargeElement = array.get(i).getAsJsonObject();

                    final Long id = this.fromApiJsonHelper.extractLongNamed("id", loanChargeElement);
                    final Long clientId = this.fromApiJsonHelper.extractLongNamed("client_id", loanChargeElement);
                    final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed("amount", loanChargeElement, locale);

                    if (id == null) {
                        final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);

                        groupLoanMemberAllocations.add(GroupLoanMemberAllocation.from(client,amount));

                    } else {
                        final Long groupLoanMemberAllocationId = id;
                        final GroupLoanMemberAllocation groupLoanMemberAllocation = this.groupLoanMemberAllocationRepository.findOne(groupLoanMemberAllocationId);
                        if (groupLoanMemberAllocation == null) { /* throw new LoanChargeNotFoundException(loanChargeId);*/ }

                        groupLoanMemberAllocation.update(amount);

                        groupLoanMemberAllocations.add(groupLoanMemberAllocation);
                    }
                }
            }
        }

        return groupLoanMemberAllocations;
    }

    public Set<GroupLoanMemberAllocation> fromParsedJsonWithLoan(final JsonElement element,final Loan loan) {

        final Set<GroupLoanMemberAllocation> groupLoanMemberAllocations = new HashSet<>();

        if (element.isJsonObject()) {
            final JsonObject topLevelJsonElement = element.getAsJsonObject();
            final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(topLevelJsonElement);

            if (topLevelJsonElement.has("groupMemberAllocation") && topLevelJsonElement.get("groupMemberAllocation").isJsonArray()) {
                final JsonArray array = topLevelJsonElement.get("groupMemberAllocation").getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {

                    final JsonObject loanChargeElement = array.get(i).getAsJsonObject();

                    final Long id = this.fromApiJsonHelper.extractLongNamed("id", loanChargeElement);
                    final Long clientId = this.fromApiJsonHelper.extractLongNamed("client_id", loanChargeElement);
                    final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalNamed("amount", loanChargeElement, locale);

                    if (id == null) {
                        final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);

                        final GroupLoanMemberAllocation groupLoanMemberAllocation = GroupLoanMemberAllocation.createNew(loan,client,amount) ;
                        groupLoanMemberAllocations.add(groupLoanMemberAllocation);

                    } else {
                        final Long groupLoanMemberAllocationId = id;
                        final GroupLoanMemberAllocation groupLoanMemberAllocation = this.groupLoanMemberAllocationRepository.findOne(groupLoanMemberAllocationId);
                        if (groupLoanMemberAllocation == null) { /* throw new LoanChargeNotFoundException(loanChargeId);*/ }

                        groupLoanMemberAllocation.update(amount);

                        groupLoanMemberAllocations.add(groupLoanMemberAllocation);
                    }
                }
            }
        }

        return groupLoanMemberAllocations;
    }
}