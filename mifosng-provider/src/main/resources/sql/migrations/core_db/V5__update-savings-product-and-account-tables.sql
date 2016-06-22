ALTER TABLE `m_savings_product`
DROP COLUMN `nominal_interest_rate_period_frequency_enum`,
CHANGE COLUMN `nominal_interest_rate_per_period` `nominal_annual_interest_rate` DECIMAL(19,6) NOT NULL,
CHANGE COLUMN `interest_period_enum` `interest_compounding_period_enum` SMALLINT(5) NOT NULL;

INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('interest_compounding_period_enum',1,'Daily','Daily',0);
INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('interest_compounding_period_enum',2,'Monthly','Monthly',0);
INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('interest_compounding_period_enum',3,'Quarterly','Quarterly',0);
INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('interest_compounding_period_enum',4,'Semi-Annual','Semi-Annual',0);
INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('interest_compounding_period_enum',5,'Annually','Annually',0);

INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('lockin_period_frequency_enum',0,'Days','Days',0);
INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('lockin_period_frequency_enum',1,'Weeks','Weeks',0);
INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('lockin_period_frequency_enum',2,'Months','Months',0);
INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('lockin_period_frequency_enum',3,'Years','Years',0);
INSERT INTO `r_enum_value` (`enum_name`,`enum_id`,`enum_message_property`,`enum_value`,`enum_type`)
VALUES ('lockin_period_frequency_enum',4,'Invalid','Invalid',0);


ALTER TABLE `m_savings_account`
DROP COLUMN `annual_nominal_interest_rate`,
DROP COLUMN `nominal_interest_rate_period_frequency_enum`,
CHANGE COLUMN `nominal_interest_rate_per_period` `nominal_annual_interest_rate` DECIMAL(19,6) NOT NULL,
CHANGE COLUMN `interest_period_enum` `interest_compounding_period_enum` SMALLINT(5) NOT NULL;