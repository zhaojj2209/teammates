CREATE   SCHEMA IF NOT EXISTS core;

CREATE TABLE IF NOT EXISTS core.courses (
    id VARCHAR NOT NULL PRIMARY KEY,
    institute VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    timezone VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- TODO: make account_id foreign key to the accounts table
-- TODO: make email and course_id a composite primary key
CREATE TABLE IF NOT EXISTS core.instructors (
    id VARCHAR NOT NULL PRIMARY KEY,
    account_id VARCHAR,
    course_id VARCHAR UNIQUE,
    email VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    registration_key VARCHAR NOT NULL,
    displayed_name VARCHAR NOT NULL,
    role VARCHAR NOT NULL,
    instructor_privileges TEXT NOT NULL,
    is_displayed_to_students BOOLEAN NOT NULL,
    is_archived BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES core.courses (id)
);
