CREATE TABLE `ppi_kenya_2005` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` int NOT NULL,
  `ppi_household_members_cv_q1_householdmembers` int(11) NOT NULL,
  `ppi_highestschool_cv_q2_highestschool` int(11) NOT NULL,
  `ppi_businessoccupation_cv_q3_businessoccupation` int(11) NOT NULL,
  `ppi_habitablerooms_cv_q4_habitablerooms` int(11) NOT NULL,
  `ppi_floortype_cv_q5_floortype` int(11) NOT NULL,
  `ppi_lightingsource_cv_q6_lightingsource` INT(11) NOT NULL,
  `ppi_irons_cv_q7_irons` INT(11) NOT NULL,
  `ppi_mosquitonets_cv_q8_mosquitonets` INT(11) NOT NULL,
  `ppi_towels_cv_q9_towels` INT(11) NOT NULL,
  `ppi_fryingpans_cv_q10_fryingpans` INT(11) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Question 1:
INSERT INTO `m_code` ( `code_name`, `is_system_defined`)
VALUES
	('ppi_household_members', 1);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_household_members'), 'Nine or More', 1, 0);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_household_members'), 'Seven or eight', 2, 5);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_household_members'), 'Six', 3, 8);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_household_members'), 'Five', 4, 12);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_household_members'), 'Four', 5, 18);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_household_members'), 'Three', 6, 22);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_household_members'), 'One or Two', 7, 32);
	
-- Question 2
INSERT INTO `m_code` ( `code_name`, `is_system_defined`)
VALUES
	('ppi_highestschool', 1);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_highestschool'), 'None or pre-school', 1, 0);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_highestschool'), 'Primary standards 1 to 6', 2, 1);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_highestschool'), 'Primary standard 7', 3, 2);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_highestschool'), 'Primary standard 8, or secondary forms 1 to 3', 4, 6);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_highestschool'), 'No female head/spouse', 5, 6);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_highestschool'), 'Secondary form 4 or higher', 6, 11);		
	
-- Question 3:
INSERT INTO `m_code` ( `code_name`, `is_system_defined`)
VALUES
	('ppi_businessoccupation', 1);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_businessoccupation'), 'Does not work', 1, 0);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_businessoccupation'), 'no male head/spouse', 2, 3);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_businessoccupation'), 'Agriculture, hunting forestry, fishing, mining or quarrying', 3,7);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_businessoccupation'), 'Any other', 9);	

-- Question 4:
INSERT INTO `m_code` ( `code_name`, `is_system_defined`)
VALUES
	('ppi_habitablerooms', 1);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_habitablerooms'), 'One', 1, 0);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_habitablerooms'), 'Two', 2, 2);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_habitablerooms'), 'Three', 3, 5);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_habitablerooms'), 'Four or more', 4, 8);

-- Question 5:
INSERT INTO `m_code` ( `code_name`, `is_system_defined`)
VALUES
	('ppi_floortype', 1);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_floortype'), 'Wood, earth or other', 1, 0);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_floortype'), 'Cement or tiles', 2, 3);	
	
-- Question 6
INSERT INTO `m_code` ( `code_name`, `is_system_defined`)
VALUES
	('ppi_lightingsource', 1);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_lightingsource'), 'Collected firewood, purchased firewood, grass, or dry cell (torch)', 1, 0);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_lightingsource'), 'Paraffin, candles, biogas, or other', 2, 6);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_lightingsource'), 'Electricity, solar, or gas', 3, 12);	
	
-- Question 7
INSERT INTO `m_code` ( `code_name`, `is_system_defined`)
VALUES
	('ppi_irons', 1);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_irons'), 'No', 1,0);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_irons'), 'Yes', 2,4);	


-- Questions 8
INSERT INTO `m_code` ( `code_name`, `is_system_defined`)
VALUES
	('ppi_mosquitonets', 1);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_mosquitonets'), 'None', 1, 0);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_mosquitonets'), 'One', 2, 2);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_mosquitonets'), 'Two or more', 3, 4);	

-- Questions 9
INSERT INTO `m_code` ( `code_name`, `is_system_defined`)
VALUES
	('ppi_towels', 1);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_towels'), 'None', 1, 0);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_towels'), 'One', 2, 6);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_towels'), 'Two or more', 3, 10);	

INSERT INTO `m_code` ( `code_name`, `is_system_defined`)
VALUES
	('ppi_fryingpans', 1);
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_fryingpans'), 'None', 1, 0);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_fryingpans'), 'One', 2, 3);	
INSERT INTO `m_code_value` ( `code_id`, `code_value`, `order_position`, `code_score`)
VALUES
	((select id from m_code where code_name = 'ppi_fryingpans'), 'Two or more', 3, 7);	


