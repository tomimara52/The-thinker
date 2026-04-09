
CREATE TABLE IF NOT EXISTS "user"
(
    user_id BIGSERIAL,
    name VARCHAR(32) NOT NULL,
    email VARCHAR(64) NOT NULL,
    password VARCHAR(128) NOT NULL,
    role VARCHAR(8) NOT NULL,
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS idea
(
    id BIGSERIAL,
    title VARCHAR(64) NOT NULL,
    description VARCHAR(256),
    visibility VARCHAR(16) NOT NULL,
    owner_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (owner_id)
        REFERENCES "user"(user_id) ON DELETE CASCADE
);