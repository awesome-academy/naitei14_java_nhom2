CREATE TABLE activity_logs
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_activity_logs PRIMARY KEY (id)
);

CREATE TABLE positions
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_positions PRIMARY KEY (id)
);

CREATE TABLE project_leadership_history
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_project_leadership_history PRIMARY KEY (id)
);

CREATE TABLE project_members
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_project_members PRIMARY KEY (id)
);

CREATE TABLE projects
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_projects PRIMARY KEY (id)
);

CREATE TABLE skills
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_skills PRIMARY KEY (id)
);

CREATE TABLE team_leadership_history
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_team_leadership_history PRIMARY KEY (id)
);

CREATE TABLE team_memberships
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_team_memberships PRIMARY KEY (id)
);

CREATE TABLE teams
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_teams PRIMARY KEY (id)
);

CREATE TABLE user_position_history
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_user_position_history PRIMARY KEY (id)
);

CREATE TABLE user_skills
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_user_skills PRIMARY KEY (id)
);

CREATE TABLE users
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);