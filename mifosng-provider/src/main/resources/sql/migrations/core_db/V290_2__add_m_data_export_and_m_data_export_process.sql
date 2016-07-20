CREATE TABLE `mifostenant-demo`.`m_data_export` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `base_entity` VARCHAR(20) NOT NULL,
  `entity_id` BIGINT(20) NOT NULL,
  `entity_status` INT(5) NOT NULL,
  `submitted_on_date` DATE NOT NULL,
  `account_no` VARCHAR(20) NULL,
  `office_id` BIGINT(20) NULL DEFAULT NULL,
  `display_name` VARCHAR(100) NULL DEFAULT NULL,
  `mobile_no` VARCHAR(50) NULL DEFAULT NULL,
  `data_sql` TEXT NOT NULL),
  PRIMARY KEY (`id`)
ENGINE = InnoDB
DEFAULT CHARSET = utf8;


CREATE TABLE `mifostenant-demo`.`m_data_export_process` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `data_export_id` BIGINT(20) NOT NULL,
  `process_status` INT(5) NOT NULL,
  `file_name` VARCHAR(200) NULL,
  `started_by_user_id` BIGINT(20) NOT NULL DEFAULT '1',
  `started_date` DATE NOT NULL,
  `ended_date` DATE NULL,
  `error_message` MEDIUMTEXT NULL DEFAULT NULL,
  `file_download_count` BIGINT(20) NOT NULL),
  PRIMARY KEY (`id`)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

INSERT INTO `m_permission`
(`grouping`,`code`,`entity_name`,`action_name`,`can_maker_checker`) VALUES
('dataexport','CREATE_DATAEXPORT','DATAEXPORT','CREATE',0),
('dataexport','READ_DATAEXPORT','DATAEXPORT','READ',0),
('dataexport','DELETE_DATAEXPORT','DATAEXPORT','DELETE',0),
('dataexport','CREATE_DATAEXPORTPROCESS','DATAEXPORTPROCESS','CREATE',0),
('dataexport','READ_DATAEXPORTPROCESS','DATAEXPORTPROCESS','READ',0),
('dataexport','DELETE_DATAEXPORTPROCESS','DATAEXPORTPROCESS','DELETE',0);