CREATE TABLE IF NOT EXISTS feedback_sessions (
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL,
    course_id VARCHAR NOT NULL REFERENCES courses(id),
    timezone VARCHAR NOT NULL,
    creator_email VARCHAR NOT NULL,
    instructions VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);
