create table acc_gl_closure_journal_entry_balance(
id bigint primary key auto_increment,
closure_id bigint not null,
account_id bigint not null,
amount decimal(19,6) not null,
created_date datetime not null,
createdby_id bigint not null,
lastmodified_date datetime not null,
lastmodifiedby_id bigint not null,
is_deleted tinyint(1) default 0 not null,
foreign key (closure_id) references acc_gl_closure (id),
foreign key (account_id) references acc_gl_account (id),
foreign key (createdby_id) references m_appuser (id),
foreign key (lastmodifiedby_id) references m_appuser (id)
);

insert into c_configuration (name, enabled, description)
values ('store-journal-entry-balance-at-period-closure', 1, 'If enabled, the latest journal entry (entry date less than or equal to the period closure closing date) running balance will be stored per GL account.');

insert into m_permission (grouping, code, entity_name, action_name, can_maker_checker)
values ('report', 'READ_GLClosureAccountBalanceReport', 'GLClosureAccountBalanceReport', 'READ', 0);