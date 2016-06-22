ALTER TABLE `m_savings_account`
	ADD COLUMN `account_type_enum` SMALLINT(5) NOT NULL DEFAULT '1' AFTER `status_enum`;

INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('account_type_enum',0,'Invalid','Invalid',0);
INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('account_type_enum',1,'Individual','Individual',0);
INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('account_type_enum',2,'Group','Group',0);
INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('account_type_enum',3,'JLG','JLG',0);