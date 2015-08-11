/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.portfolio.charge.domain.Charge;
import org.mifosplatform.portfolio.charge.domain.ChargeCalculationType;
import org.mifosplatform.portfolio.charge.domain.ChargePaymentMode;
import org.mifosplatform.portfolio.charge.domain.ChargeTimeType;
import org.mifosplatform.portfolio.charge.exception.LoanChargeWithoutMandatoryFieldException;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.loanaccount.command.LoanChargeCommand;
import org.mifosplatform.portfolio.loanaccount.data.LoanChargePaidDetail;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

@Entity
@Table(name = "m_group_loan_member_allocation")
public class GroupLoanMemberAllocation extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", referencedColumnName = "id", nullable = false)
    private Loan loan;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", referencedColumnName = "id", nullable = false)
    private Client client;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;

    public static GroupLoanMemberAllocation createNewFromJson(final Loan loan,Client client, final JsonCommand command) {
        final BigDecimal amount = command.bigDecimalValueOfParameterNamed("amount");
        //final long clientId = command.longValueOfParameterNamed("client_id");

        return new GroupLoanMemberAllocation(loan,client,amount);

    }

    public static GroupLoanMemberAllocation createNew(final Loan loan,Client client, final BigDecimal amount) {

        return new GroupLoanMemberAllocation(loan,client,amount);

    }

    public static GroupLoanMemberAllocation createNewWithoutLoan(final Client client, final BigDecimal amount) {
        return new GroupLoanMemberAllocation(null,client,amount);
    }


    protected GroupLoanMemberAllocation(final Loan loan, final Client client, final BigDecimal amount) {
        this.loan = loan;
        this.client = client;
        this.amount = amount;
    }


    public void update(final BigDecimal amount) {
        this.amount = amount;
    }

    public void associateWith(final Loan loan) {
        this.loan = loan;
    }

    public Loan loan(){
        return this.loan;
    }

    public Client client(){
        return this.client;
    }


    public BigDecimal amount() {
        return this.amount;
    }

    protected GroupLoanMemberAllocation() {
        //
    }



}