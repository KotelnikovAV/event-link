drop table if exists requests;

create table if not exists requests
(
    id           bigint         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created      TIMESTAMP      NOT NULL,
    event_id     bigint         NOT NULL,
    requester_id bigint         NOT NULL,
    status       varchar(20)    NOT NULL
);