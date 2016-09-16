create table if not exists scheduled_email_campaign (
id bigint(20) NOT NULL AUTO_INCREMENT,
campaign_name varchar(100) NOT NULL,
campaign_type int NOT NULL,
businessRule_id int NOT NULL,
param_value text,
status_enum int NOT NULL,
closedon_date date,
closedon_userid bigint(20),
submittedon_date date,
submittedon_userid bigint(20),
approvedon_date date,
approvedon_userid bigint(20),
recurrence varchar(100),
next_trigger_date datetime,
last_trigger_date datetime,
recurrence_start_date datetime,
email_subject varchar(100) not null,
email_message text not null,
email_attachment_file_format varchar(10) not null,
stretchy_report_id int not null,
stretchy_report_param_map text null,
previous_run_status varchar(10) null,
previous_run_error_log text null,
previous_run_error_message text null,
is_visible tinyint(1) null,
foreign key (submittedon_userid) references m_appuser(id),
foreign key (stretchy_report_id) references stretchy_report(id),
  PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS scheduled_email_messages_outbound (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `staff_id` bigint(20) DEFAULT NULL,
  `email_campaign_id` bigint(20) DEFAULT NULL,
  `status_enum` int(5) NOT NULL DEFAULT '100',
  `email_address` varchar(50) NOT NULL,
  `email_subject` varchar(50) NOT NULL,
  `message` text NOT NULL,
  `campaign_name` varchar(200) DEFAULT NULL,
  `submittedon_date` date,
  `error_message` text,
  PRIMARY KEY (`id`),
  KEY `SEFKGROUP000000001` (`group_id`),
  KEY `SEFKCLIENT00000001` (`client_id`),
  key `SEFKSTAFF000000001` (`staff_id`),
  CONSTRAINT `SEFKGROUP000000001` FOREIGN KEY (`group_id`) REFERENCES `m_group` (`id`),
  CONSTRAINT `SEFKCLIENT00000001` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `SEFKSTAFF000000001` FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`),
  CONSTRAINT `fk_schedule_email_campign` FOREIGN KEY (`email_campaign_id`) REFERENCES `scheduled_email_campaign` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table if not exists scheduled_email_configuration (
id int primary key auto_increment,
name varchar(50) not null,
`value` varchar(200) not null,
constraint unique_name unique (name)
);

DELETE FROM `m_permission` WHERE `code`='READ_EMAIL';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'READ_EMAIL', 'EMAIL', 'READ', 0);

DELETE FROM `m_permission` WHERE `code`='CREATE_EMAIL';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'CREATE_EMAIL', 'EMAIL', 'CREATE', 0);

DELETE FROM `m_permission` WHERE `code`='CREATE_EMAIL_CHECKER';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'CREATE_EMAIL_CHECKER', 'EMAIL', 'CREATE_CHECKER', 0);

DELETE FROM `m_permission` WHERE `code`='UPDATE_EMAIL';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'UPDATE_EMAIL', 'EMAIL', 'UPDATE', 0);

DELETE FROM `m_permission` WHERE `code`='UPDATE_EMAIL_CHECKER';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'UPDATE_EMAIL_CHECKER', 'EMAIL', 'UPDATE_CHECKER', 0);

DELETE FROM `m_permission` WHERE `code`='DELETE_EMAIL';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'DELETE_EMAIL', 'EMAIL', 'DELETE', 0);

DELETE FROM `m_permission` WHERE `code`='DELETE_EMAIL_CHECKER';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'DELETE_EMAIL_CHECKER', 'EMAIL', 'DELETE_CHECKER', 0);

DELETE FROM `m_permission` WHERE `code`='READ_EMAIL_CAMPAIGN';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'READ_EMAIL_CAMPAIGN', 'EMAIL_CAMPAIGN', 'READ', 0);

DELETE FROM `m_permission` WHERE `code`='CREATE_EMAIL_CAMPAIGN';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'CREATE_EMAIL_CAMPAIGN', 'EMAIL_CAMPAIGN', 'CREATE', 0);

DELETE FROM `m_permission` WHERE `code`='CREATE_EMAIL_CAMPAIGN_CHECKER';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'CREATE_EMAIL_CAMPAIGN_CHECKER', 'EMAIL_CAMPAIGN', 'CREATE_CHECKER', 0);

DELETE FROM `m_permission` WHERE `code`='UPDATE_EMAIL_CAMPAIGN';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'UPDATE_EMAIL_CAMPAIGN', 'EMAIL_CAMPAIGN', 'UPDATE', 0);

DELETE FROM `m_permission` WHERE `code`='UPDATE_EMAIL_CAMPAIGN_CHECKER';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'UPDATE_EMAIL_CAMPAIGN_CHECKER', 'EMAIL_CAMPAIGN', 'UPDATE_CHECKER', 0);

DELETE FROM `m_permission` WHERE `code`='DELETE_EMAIL_CAMPAIGN';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'DELETE_EMAIL_CAMPAIGN', 'EMAIL_CAMPAIGN', 'DELETE', 0);

DELETE FROM `m_permission` WHERE `code`='DELETE_EMAIL_CAMPAIGN_CHECKER';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'DELETE_EMAIL_CAMPAIGN_CHECKER', 'EMAIL_CAMPAIGN', 'DELETE_CHECKER', 0);

DELETE FROM `m_permission` WHERE `code`='CLOSE_EMAIL_CAMPAIGN';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'CLOSE_EMAIL_CAMPAIGN', 'EMAIL_CAMPAIGN', 'CLOSE', 0);

DELETE FROM `m_permission` WHERE `code`='ACTIVATE_EMAIL_CAMPAIGN';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'ACTIVATE_EMAIL_CAMPAIGN', 'EMAIL_CAMPAIGN', 'ACTIVATE', 0);

DELETE FROM `m_permission` WHERE `code`='REACTIVATE_EMAIL_CAMPAIGN';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'REACTIVATE_EMAIL_CAMPAIGN', 'EMAIL_CAMPAIGN', 'REACTIVATE', 0);

Alter table m_client
ADD Column email_address varchar(150);

Alter table m_staff
ADD Column email_address varchar(150);


insert into job (name, display_name, cron_expression, create_time)
values ('Execute Email', 'Execute Email', '0 0/10 * * * ?', NOW());

insert into job (name, display_name, cron_expression, create_time)
values ('Update Email Outbound with campaign message', 'Update Email Outbound with campaign message', '0 0/15 * * * ?', NOW());


