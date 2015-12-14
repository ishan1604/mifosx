
CREATE TABLE `acc_income_and_expense_bookings` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`gl_closure_id` BIGINT(20) NOT NULL,
	`journal_entry_transaction_id` VARCHAR(60) NOT NULL,
	`office_id` BIGINT(20) NOT NULL,
  `is_reversed` tinyint(1) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`),
	 FOREIGN KEY (`gl_closure_id`) REFERENCES `acc_gl_closure` (`id`),
   FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`),
   UNIQUE (journal_entry_transaction_id)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

ALTER TABLE `acc_gl_closure`
DROP INDEX office_id_closing_date;
