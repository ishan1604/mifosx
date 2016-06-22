INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES
('transaction_savings', 'POSTINTEREST_SAVINGSACCOUNT', 'SAVINGSACCOUNT', 'POSTINTEREST', '1'),
('transaction_savings', 'POSTINTEREST_SAVINGSACCOUNT_CHECKER', 'SAVINGSACCOUNT', 'POSTINTEREST', '0');


ALTER TABLE `m_savings_product`
ADD COLUMN `interest_posting_period_enum` SMALLINT(5) NOT NULL DEFAULT 4 AFTER `interest_compounding_period_enum`;

INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('interest_posting_period_enum',4,'Monthly','Monthly',0);
INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('interest_posting_period_enum',5,'Quarterly','Quarterly',0);
INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('interest_posting_period_enum',6,'Biannual','Biannual',0);
INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('interest_posting_period_enum',7,'Annual','Annual',0);

ALTER TABLE `m_savings_account`
ADD COLUMN `interest_posting_period_enum` SMALLINT(5) NOT NULL DEFAULT 4 AFTER `interest_compounding_period_enum`;