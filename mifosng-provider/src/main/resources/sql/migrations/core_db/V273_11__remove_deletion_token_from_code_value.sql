update m_code_value
set code_value = concat(code_value, '_deleted_', id )
where is_active = 0;

alter table m_code_value
drop index unique_code_value;

alter table m_code_value
add constraint unique_code_value unique (code_value, code_id);

alter table m_code_value
drop column deletion_token;
