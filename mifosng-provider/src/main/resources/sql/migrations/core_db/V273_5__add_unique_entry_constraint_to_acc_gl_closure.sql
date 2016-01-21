delete from acc_gl_closure
where id in (select * from (
SELECT id
FROM acc_gl_closure
GROUP BY office_id, closing_date, is_deleted
HAVING count( * ) >1) temp);

alter table acc_gl_closure
add unique index unique_office_closing_date_is_deleted (office_id, closing_date, is_deleted);