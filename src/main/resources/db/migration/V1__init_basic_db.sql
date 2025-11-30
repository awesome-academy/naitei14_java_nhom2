CREATE DATABASE IF NOT EXISTS member_management;
USE member_management;

CREATE TABLE Users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       birthday DATE,
                       role ENUM('ADMIN', 'MEMBER') NOT NULL DEFAULT 'MEMBER',
                       status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       deleted_at TIMESTAMP NULL,
                       INDEX idx_users_role_status (role, status)
);

CREATE TABLE Teams (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL UNIQUE,
                       description TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       deleted_at TIMESTAMP NULL,
                       INDEX idx_teams_name (name)
);

CREATE TABLE Positions (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(255) NOT NULL UNIQUE,
                           abbreviation VARCHAR(50) NOT NULL UNIQUE,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           deleted_at TIMESTAMP NULL,
                           INDEX idx_positions_name (name)
);

CREATE TABLE Projects (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          abbreviation VARCHAR(50),
                          start_date DATE,
                          end_date DATE,
                          status ENUM('PLANNING', 'ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'PLANNING',
                          team_id BIGINT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          deleted_at TIMESTAMP NULL,
                          CONSTRAINT fk_projects_team FOREIGN KEY (team_id) REFERENCES Teams(id),
                          INDEX idx_projects_team (team_id),
                          INDEX idx_projects_team_status (team_id, status)
);

CREATE TABLE Skills (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        description TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        deleted_at TIMESTAMP NULL,
                        INDEX idx_skills_name (name)
);

CREATE TABLE Team_Members (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 user_id BIGINT NOT NULL,
                                 team_id BIGINT NOT NULL,
                                 status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
                                 joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 left_at TIMESTAMP NULL,

                                 is_active_generated INT AS (IF(status = 'ACTIVE', 1, NULL)) STORED,

                                 CONSTRAINT fk_tm_user FOREIGN KEY (user_id) REFERENCES Users(id),
                                 CONSTRAINT fk_tm_team FOREIGN KEY (team_id) REFERENCES Teams(id),

                                 INDEX idx_tm_user (user_id),
                                 INDEX idx_tm_team (team_id),

                                 UNIQUE INDEX unique_active_team_member (user_id, is_active_generated)
);

CREATE TABLE Project_Members (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                project_id BIGINT NOT NULL,
                                status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
                                joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                left_at TIMESTAMP NULL,

                                is_active_generated INT AS (IF(status = 'ACTIVE', 1, NULL)) STORED,

                                CONSTRAINT fk_pm_user FOREIGN KEY (user_id) REFERENCES Users(id),
                                CONSTRAINT fk_pm_project FOREIGN KEY (project_id) REFERENCES Projects(id),

                                INDEX idx_pm_user (user_id),
                                INDEX idx_pm_project (project_id),

                                UNIQUE INDEX unique_active_project_member (user_id, project_id, is_active_generated)
);

CREATE TABLE User_Position_History (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     user_id BIGINT NOT NULL,
                                     position_id BIGINT NOT NULL,
                                     started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     ended_at TIMESTAMP NULL,

                                     is_active_generated INT AS (IF(ended_at IS NULL, 1, NULL)) STORED,

                                     CONSTRAINT fk_uph_user FOREIGN KEY (user_id) REFERENCES Users(id),
                                     CONSTRAINT fk_uph_position FOREIGN KEY (position_id) REFERENCES Positions(id),

                                     INDEX idx_uph_user (user_id),
                                     INDEX idx_uph_position (position_id),

                                     UNIQUE INDEX unique_active_position (user_id, is_active_generated)
);

CREATE TABLE Team_Leadership_History (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       team_id BIGINT NOT NULL,
                                       leader_id BIGINT NOT NULL,
                                       started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       ended_at TIMESTAMP NULL,

                                       is_active_generated INT AS (IF(ended_at IS NULL, 1, NULL)) STORED,

                                       CONSTRAINT fk_tlh_team FOREIGN KEY (team_id) REFERENCES Teams(id),
                                       CONSTRAINT fk_tlh_leader FOREIGN KEY (leader_id) REFERENCES Users(id),

                                       INDEX idx_tlh_team (team_id),
                                       INDEX idx_tlh_leader (leader_id),

                                       UNIQUE INDEX unique_active_team_leader (team_id, is_active_generated)
);

CREATE TABLE Project_Leadership_History (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          project_id BIGINT NOT NULL,
                                          leader_id BIGINT NOT NULL,
                                          started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          ended_at TIMESTAMP NULL,

                                          is_active_generated INT AS (IF(ended_at IS NULL, 1, NULL)) STORED,

                                          CONSTRAINT fk_plh_project FOREIGN KEY (project_id) REFERENCES Projects(id),
                                          CONSTRAINT fk_plh_leader FOREIGN KEY (leader_id) REFERENCES Users(id),

                                          INDEX idx_plh_project (project_id),
                                          INDEX idx_plh_leader (leader_id),

                                          UNIQUE INDEX unique_active_project_leader (project_id, is_active_generated)
);


CREATE TABLE User_Skills (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            skill_id BIGINT NOT NULL,
                            level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT') NOT NULL DEFAULT 'BEGINNER',
                            used_year_number DECIMAL(4,2) NOT NULL DEFAULT 0,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                            CONSTRAINT fk_us_user FOREIGN KEY (user_id) REFERENCES Users(id),
                            CONSTRAINT fk_us_skill FOREIGN KEY (skill_id) REFERENCES Skills(id),

                            UNIQUE INDEX idx_unique_user_skill (user_id, skill_id),
                            INDEX idx_us_user (user_id),
                            INDEX idx_us_skill (skill_id)
);

CREATE TABLE Activity_Logs (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              action VARCHAR(100) NOT NULL,
                              entity_type VARCHAR(50) NULL,
                              entity_id BIGINT NULL,
                              description TEXT,
                              user_id BIGINT NULL,
                              ip_address VARCHAR(45) NULL,
                              user_agent TEXT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT fk_logs_user FOREIGN KEY (user_id) REFERENCES Users(id),

                              INDEX idx_logs_user (user_id),
                              INDEX idx_logs_action (action),
                              INDEX idx_logs_entity (entity_type, entity_id),
                              INDEX idx_logs_created (created_at)
);