/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.staff.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;

public interface StaffRepository extends JpaRepository<Staff, Long>, JpaSpecificationExecutor<Staff> {

    public final static String FIND_BY_OFFICE_QUERY = "select s from Staff s where s.id = :id AND s.office.id = :officeId";



    public final static String IS_ACTIVE_QUERY = "select " +
            "case when (sum(total) > 0)  then true else false end " +
            "from (" +
            "(select count(mg.id) as total " +
            "from m_staff ms " +
            "join m_group mg on mg.staff_id = ms.id " +
            "where ms.id = :staffId " +
            "and mg.status_enum in (100,300)) " +
            "union all " +
            "(select count(ml.id) as total " +
            "from m_loan ml " +
            "join m_staff ms on ml.loan_officer_id = ms.id " +
            "where ms.id = :staffId " +
            "and ml.loan_status_id in (100,200,300,700)) " +
            "union all " +
            "(select count(msa.id) as total " +
            "from m_savings_account msa " +
            "join m_staff ms on msa.field_officer_id = ms.id " +
            "where ms.id = :staffId and " +
            "msa.status_enum in (100,300))) as t1 ";
    /**
     * Find staff by officeid.
     */
    @Query(FIND_BY_OFFICE_QUERY)
    public Staff findByOffice(@Param("id") Long id, @Param("officeId") Long officeId);

    /**
     * Query finds if a staff is attached to active, pending group or loans or savings
     */
    @Query(value=IS_ACTIVE_QUERY,nativeQuery = true)
    public BigInteger activeStaff(@Param("staffId") Long staffId);

}