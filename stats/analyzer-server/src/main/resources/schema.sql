drop table if exists event_similarity, user_action;

create table if not exists event_similarity
(
    event_A     bigint              NOT NULL,
    event_B     bigint              NOT NULL,
    score       double precision    NOT NULL,
    PRIMARY KEY (event_A, event_B)
);

create table if not exists user_action
(
    user_id         bigint              NOT NULL,
    event_id        bigint              NOT NULL,
    action_type     varchar(10)         NOT NULL,
    action_date     timestamp           NOT NULL,
    PRIMARY KEY (user_id, event_id)
);