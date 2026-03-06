CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL
);

INSERT INTO projects (name, username) VALUES ('Projet A', 'Lucas');
INSERT INTO projects (name, username) VALUES ('Projet B', 'Lucas');
INSERT INTO projects (name, username) VALUES ('Projet C', 'Nicolas');
INSERT INTO projects (name, username) VALUES ('Projet D', 'Nicolas');