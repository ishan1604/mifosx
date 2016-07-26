/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataexport.domain;

import org.joda.time.LocalDateTime;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "m_data_export_process")
public class DataExportProcess extends AbstractPersistable<Long> {

    @Column(name = "data_export_id",nullable = false)
    private Long dataExport;

    @Column(name = "process_status", nullable = false)
    private Integer status;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "started_by_user_id", nullable = false)
    private Long startedByUser;

    @Column(name = "started_date", nullable = false)
    private Date startDate;

    @Column(name = "ended_date")
    private Date endDate;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "file_download_count",nullable = false)
    private Integer fileDownloadCount;

    /**
     * @param dataExport
     * @param fileName
     * @param status
     * @param startedByUser
     * @param startDate
     * @param endDate
     * @param errorMessage
     * @param fileDownloadCount
     */
    private DataExportProcess(final Long dataExport, final String fileName, final Integer status, final Long startedByUser,
                              final Date startDate, final Date endDate, final String errorMessage, final Integer fileDownloadCount) {
        this.dataExport = dataExport;
        this.fileName = fileName;
        this.status = status;
        this.startedByUser = startedByUser;
        this.startDate = startDate;
        this.endDate = endDate;
        this.errorMessage = errorMessage;
        this.fileDownloadCount = fileDownloadCount;
    }

    /**
     * {@link DataExportProcess} protected no-arg constructor
     *
     * (An entity class must have a no-arg public/protected constructor according to the JPA specification)
     **/
    protected DataExportProcess(){}

    /**
     * @param dataExport
     * @param fileName
     * @param status
     * @param startedByUser
     * @param startDate
     * @param endDate
     * @param errorMessage
     * @param fileDownloadCount
     * @return {@link DataExportProcess} object
     */
    public static DataExportProcess instance(final Long dataExport, final String fileName, final Integer status, final Long startedByUser,
                                      final Date startDate, final Date endDate, final String errorMessage, final Integer fileDownloadCount) {
        return new DataExportProcess(dataExport,fileName,status,startedByUser,startDate,endDate,errorMessage,fileDownloadCount);
    }

    public Long getDataExport() {
        return dataExport;
    }

    public Integer getStatus() {
        return status;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * update the error message property
     *
     * @param errorMessage
     * @return None
     **/
    public void updateErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * update the end date property
     *
     * @param endDate
     * @return None
     **/
    public void updateEndDate(final LocalDateTime endDate) {
        if (endDate != null) {
            this.endDate = endDate.toDate();
        }
    }

    /**
     * updates the fileDownloadCount property
     * @param fileDownloadCount
     * @return None
     */
    public void updateFileDownloadCount(final Integer fileDownloadCount) {
        this.fileDownloadCount = fileDownloadCount;
    }

    /**
     * update the status property
     *
     * @param status
     * @return None
     **/
    public void updateStatus(final Integer status) {
        if (status != null) {
            this.status = status;
        }
    }

    public Long getStartedByUser() {
        return startedByUser;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Integer getFileDownloadCount() {
        return fileDownloadCount;
    }
}
