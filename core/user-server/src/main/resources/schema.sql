drop table if exists users;

create table if not exists users
(
    id      bigint generated always as identity primary key,
    email   varchar(255) not null unique,
    name    varchar(255) not null,
    rating  INTEGER not null
);