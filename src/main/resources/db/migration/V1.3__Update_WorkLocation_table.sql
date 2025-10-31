alter table work_location
    modify created_at datetime default CURRENT_TIMESTAMP not null;

alter table work_location
    modify updated_at datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP;
