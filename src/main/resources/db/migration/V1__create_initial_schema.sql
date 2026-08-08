CREATE TABLE rooms (
    id BIGINT NOT NULL AUTO_INCREMENT,
    room_code VARCHAR(12) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_rooms_room_code UNIQUE (room_code)
);

CREATE TABLE participants (
    id BIGINT NOT NULL AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    nickname VARCHAR(30) NOT NULL,
    is_host BOOLEAN NOT NULL,
    connected BOOLEAN NOT NULL,
    joined_at DATETIME(6) NOT NULL,
    last_seen_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_participants_room_nickname UNIQUE (room_id, nickname),
    CONSTRAINT fk_participants_room FOREIGN KEY (room_id) REFERENCES rooms (id)
);

CREATE INDEX idx_participants_room ON participants (room_id);

CREATE TABLE penalties (
    id BIGINT NOT NULL AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    content VARCHAR(200) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_penalties_room FOREIGN KEY (room_id) REFERENCES rooms (id)
);

CREATE INDEX idx_penalties_room_active ON penalties (room_id, active);

CREATE TABLE roulette_rounds (
    id BIGINT NOT NULL AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    executed_by_participant_id BIGINT NOT NULL,
    selected_penalty_id BIGINT NOT NULL,
    penalty_snapshot VARCHAR(200) NOT NULL,
    roulette_snapshot JSON NOT NULL,
    started_at DATETIME(6) NOT NULL,
    duration_ms INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_rounds_room FOREIGN KEY (room_id) REFERENCES rooms (id),
    CONSTRAINT fk_rounds_participant FOREIGN KEY (executed_by_participant_id) REFERENCES participants (id),
    CONSTRAINT fk_rounds_penalty FOREIGN KEY (selected_penalty_id) REFERENCES penalties (id)
);

CREATE INDEX idx_rounds_room_started ON roulette_rounds (room_id, started_at DESC);

CREATE TABLE participant_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    participant_id BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    issued_at DATETIME(6) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    revoked_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_participant_sessions_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_sessions_participant FOREIGN KEY (participant_id) REFERENCES participants (id)
);

CREATE INDEX idx_sessions_participant ON participant_sessions (participant_id);
CREATE INDEX idx_sessions_expiration ON participant_sessions (expires_at);
