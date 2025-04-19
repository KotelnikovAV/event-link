drop table if exists users, friends;

create table if not exists users
(
    id                  BIGINT          GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email               VARCHAR(255)    NOT NULL UNIQUE,
    name                VARCHAR(255)    NOT NULL,
    rating              INTEGER         NOT NULL,
    count_followers     INTEGER         NOT NULL,
    count_friends       INTEGER         NOT NULL
);

create table if not exists friends
(
    user1_id            BIGINT          REFERENCES users (id),
    user2_id            BIGINT          REFERENCES users (id),
    initiator_id        BIGINT          NOT NULL,
    confirmed           BOOLEAN         NOT NULL,
    request_date        TIMESTAMP       NOT NULL,
    confirmation_date   TIMESTAMP,
    PRIMARY KEY (user1_id, user2_id),
    CHECK (user1_id < user2_id),
    CHECK (user1_id <> user2_id),
    CHECK (initiator_id = user1_id OR initiator_id = user2_id)
);

CREATE INDEX idx_friends_user1 ON friends (user1_id);
CREATE INDEX idx_friends_user2 ON friends (user2_id);