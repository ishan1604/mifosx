update m_payment_type
set `value` = concat(`value`, '_deleted_', id )
where is_deleted = 1;

alter table m_payment_type
drop index unique_payment_type;

alter table m_payment_type
add constraint unique_payment_type unique (`value`);

alter table m_payment_type
drop column deletion_token;