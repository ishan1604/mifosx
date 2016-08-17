ALTER TABLE `m_code`
ADD COLUMN `code_label` VARCHAR(100) NULL After `code_name`;

update `m_code` mc
set mc.code_label = mc.code_name
where mc.code_label is null;