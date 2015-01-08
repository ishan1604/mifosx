alter table m_loan_credit_check change is_active is_deleted tinyint(1) not null default 0;
alter table m_loan_credit_check add sqlStatement text null after message;
alter table m_loan_credit_check drop has_been_triggered;
alter table m_loan_credit_check drop triggered_on_date;
alter table m_loan_credit_check drop foreign key m_loan_credit_check_ibfk_1;
alter table m_loan_credit_check drop triggered_by_user_id;

update m_loan_credit_check set is_deleted = 0 where is_deleted = 1;