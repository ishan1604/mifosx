/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.account.data;

public class PortfolioAccountDTO {

    private final Integer accountTypeId;
    private final Long clientId;
    private final String currencyCode;
    private final long[] accountStatus;
    private final Integer depositType;
    private final boolean excludeOverDraftAccounts;
    private final Long groupId;

    public PortfolioAccountDTO(final Integer accountTypeId, final Long clientId, final String currencyCode, final long[] accountStatus,
            final Integer depositType, final boolean excludeOverDraftAccounts) {
        this.accountTypeId = accountTypeId;
        this.clientId = clientId;
        this.currencyCode = currencyCode;
        this.accountStatus = accountStatus;
        this.depositType = depositType;
        this.excludeOverDraftAccounts = excludeOverDraftAccounts;
        this.groupId = null;
    }

    public PortfolioAccountDTO(final Integer accountTypeId, final Long clientId, final long[] accountStatus) {
        this.accountTypeId = accountTypeId;
        this.clientId = clientId;
        this.currencyCode = null;
        this.accountStatus = accountStatus;
        this.depositType = null;
        this.excludeOverDraftAccounts = false;
        this.groupId = null;
    }
    
    public PortfolioAccountDTO(final Integer accountTypeId, final Long clientId, final String currencyCode, final long[] accountStatus,
            final Integer depositType) {
        this.accountTypeId = accountTypeId;
        this.clientId = clientId;
        this.currencyCode = currencyCode;
        this.accountStatus = accountStatus;
        this.depositType = depositType;
        this.excludeOverDraftAccounts = false;
        this.groupId = null;
    }
    public PortfolioAccountDTO(final Integer accountTypeId,final String currencyCode, final long[] accountStatus,
                               final Integer depositType,final Long groupId) {
        this.accountTypeId = accountTypeId;
        this.clientId = null;
        this.currencyCode = currencyCode;
        this.accountStatus = accountStatus;
        this.depositType = depositType;
        this.excludeOverDraftAccounts = false;
        this.groupId = groupId;
    }

    public Integer getAccountTypeId() {
        return this.accountTypeId;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public long[] getAccountStatus() {
        return this.accountStatus;
    }

    public Integer getDepositType() {
        return this.depositType;
    }

    public boolean isExcludeOverDraftAccounts() {
        return this.excludeOverDraftAccounts;
    }

    public Long getGroupId(){return this.groupId;}

}
