/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "m_data_export")
public class DataExport extends AbstractPersistable<Long> {

    @Column(name="base_entity", nullable = false)
    private String baseEntity;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "entity_status")
    private Integer status;

    @Column(name = "submitted_on_date")
    private Date submittedOnDate;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "office_id")
    private Long officeId;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "data_sql", nullable = false)
    private String sql;

    /**
     * {@link DataExport} protected no-arg constructor
     *
     * (An entity class must have a no-arg public/protected constructor according to the JPA specification)
     **/
    protected DataExport() { }

    /**
     * @param baseEntity
     * @param entityId
     * @param status
     * @param submittedOnDate
     * @param accountNo
     * @param officeId
     * @param displayName
     * @param mobileNo
     * @param sql
     */
    private DataExport(final String baseEntity, final Long entityId, final Integer status, final Date submittedOnDate,
                       final String accountNo, final Long officeId, final String displayName, final String mobileNo,
                       final String sql) {
        this.baseEntity = baseEntity;
        this.entityId = entityId;
        this.status = status;
        this.submittedOnDate = submittedOnDate;
        this.accountNo = accountNo;
        this.officeId = officeId;
        this.displayName = displayName;
        this.mobileNo = mobileNo;
        this.sql = sql;
    }

    /**
     * @param baseEntity
     * @param entityId
     * @param status
     * @param submittedOnDate
     * @param accountNo
     * @param office
     * @param displayName
     * @param mobileNo
     * @param sql
     * @return {@link DataExport} object
     */
    public static DataExport instance(final String baseEntity, final Long entityId, final Integer status, final Date submittedOnDate,
                                      final String accountNo, final Long office, final String displayName, final String mobileNo,
                                      final String sql) {
        return new DataExport(baseEntity,entityId,status,submittedOnDate,accountNo,office,displayName,mobileNo,sql);
    }

    public String getBaseEntity() {
        return baseEntity;
    }

    public Long getEntityId() {
        return entityId;
    }

    public Integer getStatus() {
        return status;
    }

    public Date getSubmittedOnDate() {
        return submittedOnDate;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getSql() {
        return sql;
    }
}
