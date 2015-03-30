DROP TABLE IF EXISTS `m_undo_transfer`;

CREATE TABLE `m_undo_transfer`(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20),
  `group_id` bigint(20),
  `staff_id` bigint(20),
  `transfer_from_office_id` bigint(20),
  `transfer_from_group_id` bigint(20),
  `transfer_from_staff_id` bigint(20),
  `submittedon_date` date,
  `submittedon_userid` bigint(20),
  `approvedon_date` date,
  `approvedon_userid` bigint(20),
  `office_joining_date` date,
  `is_group_transfer` tinyint(1) default 0,
  `is_transfer_undone` tinyint(1) default 0,
  PRIMARY KEY (`id`),
  INDEX(`client_id`),
  INDEX(`group_id`),
  INDEX(`staff_id`),
  FOREIGN KEY (`submittedon_userid`) REFERENCES `m_appuser`(`id`),
  FOREIGN KEY (`approvedon_userid`) REFERENCES `m_appuser`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



INSERT	INTO `m_permission` (grouping,code,`entity_name`,`action_name`,`can_maker_checker`)
values('portfolio','UNDOTRANSFER_CLIENT','CLIENT','UNDOTRANSFER',0);

INSERT	INTO `m_permission` (grouping,code,`entity_name`,`action_name`,`can_maker_checker`)
values('portfolio','UNDOTRANSFER_CLIENT_CHECKER','CLIENT','UNDOTRANSFER',0);


INSERT	INTO `m_permission` (grouping,code,`entity_name`,`action_name`,`can_maker_checker`)
values('portfolio_group','UNDOTRANSFER_GROUP','GROUP','UNDOTRANSFER',0);

INSERT	INTO `m_permission` (grouping,code,`entity_name`,`action_name`,`can_maker_checker`)
values('portfolio_group','UNDOTRANSFER_GROUP_CHECKER','GROUP','UNDOTRANSFER',0);


INSERT	INTO `m_permission` (grouping,code,`entity_name`,`action_name`,`can_maker_checker`)
values('portfolio','READ_UNDOTRANSFER','UNDOTRANSFER','READ',0);



