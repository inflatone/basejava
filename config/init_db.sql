drop table if exists contact;
drop table if exists resume;

create table resume
(
  uuid      char(36) not null
    primary key,
  full_name varchar  not null
);

create table contact
(
  id          serial,
  resume_uuid char(36) not null
    references resume (uuid)
      on delete cascade,
  type        varchar  not null,
  value       varchar  not null
);

create unique index contact_uuid_type_index
  on contact (resume_uuid, type);