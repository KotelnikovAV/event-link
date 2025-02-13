drop table if exists likes;

create table if not exists likes
(
    id         bigint       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id   bigint       NOT NULL,
    user_id    bigint       NOT NULL,
    status     varchar(8)   NOT NULL,
    created    TIMESTAMP
);