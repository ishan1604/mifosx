
CREATE TABLE IF NOT EXISTS `m_group_loan_member_allocation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  `amount` decimal(10,0) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `m_group_loan_member_allocation`
--
ALTER TABLE `m_group_loan_member_allocation`
  ADD CONSTRAINT `m_group_loan_member_allocation_ibfk_2` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
  ADD CONSTRAINT `m_group_loan_member_allocation_ibfk_1` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`);