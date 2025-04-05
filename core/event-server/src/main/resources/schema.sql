drop table if exists categories, location, events, compilations, compilations_events;

create table if not exists categories
(
    id   bigint         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(100)   NOT NULL
);

create table if not exists location
(
    id  bigint  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lat float   NOT NULL,
    lon float   NOT NULL
);

create table if not exists events
(
    id                 bigint       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation         varchar,
    category_id        bigint       REFERENCES categories (id) ON DELETE CASCADE ON UPDATE CASCADE,
    created_on         TIMESTAMP    NOT NULL,
    description        varchar      NOT NULL,
    event_date         TIMESTAMP    NOT NULL,
    initiator_id       bigint       NOT NULL,
    location_id        bigint       REFERENCES location (id) ON DELETE CASCADE ON UPDATE CASCADE,
    paid               BOOLEAN      NOT NULL,
    participant_limit  INTEGER      NOT NULL,
    published_on       TIMESTAMP,
    request_moderation BOOLEAN      NOT NULL,
    state              varchar      NOT NULL,
    title              varchar      NOT NULL,
    confirmed_requests INTEGER      NOT NULL,
    likes              INTEGER      NOT NULL
);

create table if not exists compilations
(
    id     bigint           GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pinned boolean          NOT NULL,
    title  varchar(255)     NOT NULL
);

create table if not exists compilations_events
(
    compilation_id bigint REFERENCES compilations (id),
    event_id       bigint REFERENCES events (id)
);

create index annotation_event ON events (LEFT(annotation, 1500));
create index description_event ON events (LEFT(description, 1500));
create index name_categories ON categories (LOWER(name));