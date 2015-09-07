ALTER TABLE `m_product_loan`
ADD COLUMN `reverse_overduedays_npa_interest` TINYINT(1) NOT NULL DEFAULT '0';

ALTER TABLE `m_loan_repayment_schedule`
	ADD COLUMN `suspended_interest_derived` DECIMAL(19,6) NULL DEFAULT NULL AFTER `accrual_interest_derived`,
	ADD COLUMN `suspended_fee_charges_derived` DECIMAL(19,6) NULL DEFAULT NULL AFTER `accrual_fee_charges_derived`,
	ADD COLUMN `suspended_penalty_charges_derived` DECIMAL(19,6) NULL DEFAULT NULL AFTER `accrual_penalty_charges_derived`;



ALTER TABLE `m_loan`
 ADD COLUMN `is_suspended_income` TINYINT(1) NOT NULL DEFAULT '0' AFTER `is_npa`;



 ALTER TABLE `m_loan_transaction`
	ADD COLUMN `suspended_interest_portion_derived` DECIMAL(19,6) NULL DEFAULT NULL AFTER `unrecognized_income_portion`,
	ADD COLUMN `suspended_fee_charges_portion_derived` DECIMAL(19,6) NULL DEFAULT NULL AFTER `suspended_interest_portion_derived`,
	ADD COLUMN `suspended_penalty_charges_portion_derived` DECIMAL(19,6) NULL DEFAULT NULL AFTER `suspended_fee_charges_portion_derived`;



INSERT INTO `acc_gl_account` (`id`, `name`, `parent_id`, `hierarchy`, `gl_code`, `disabled`, `manual_journal_entries_allowed`, `account_usage`, `classification_enum`, `tag_id`, `description`) VALUES (NULL, 'Suspended interest And Fees Account (Temp)', NULL, '.', 'SIFA12', '0', '0', '1', '2', NULL, 'suspended income');

INSERT INTO `acc_product_mapping` (`gl_account_id`,`product_id`,`product_type`,`payment_type`,`charge_id`,`financial_account_type`)
select (select max(id) from acc_gl_account where classification_enum=2 and account_usage=1 LIMIT 1), mapping.product_id, mapping.product_type,null,null, 13
from acc_product_mapping mapping
where mapping.financial_account_type = 7 and mapping.product_type=1;
