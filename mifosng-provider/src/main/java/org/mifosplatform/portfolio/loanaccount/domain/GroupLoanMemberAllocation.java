/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.loanaccount.data.GroupLoanMembersAllocationData;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "m_group_loan_member_allocation")
public class GroupLoanMemberAllocation extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;


    public static GroupLoanMemberAllocation createNew(final Loan loan,Client client, final BigDecimal amount) {

        return new GroupLoanMemberAllocation(loan,client,amount);

    }

    public static GroupLoanMemberAllocation from(final Client client, final BigDecimal amount) {
        return new GroupLoanMemberAllocation(null,client,amount);
    }

    private GroupLoanMemberAllocation(final Loan loan, final Client client, final BigDecimal amount) {
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


    public GroupLoanMembersAllocationData toData() {

        return GroupLoanMembersAllocationData.newOne(null,null,this.client.toData(),this.amount());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        final GroupLoanMemberAllocation rhs = (GroupLoanMemberAllocation) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)) //
                .append(getId(), rhs.getId()) //
                .append(this.client.getId(), rhs.client.getId()) //
                .append(this.amount, this.amount)//
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(3, 5) //
                .append(getId()) //
                .append(this.client.getId()) //
                .append(this.amount)//
                .toHashCode();
    }
}
