CREATE TABLE IF NOT EXISTS rooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

INSERT INTO rooms (name) VALUES ('Salle A');
INSERT INTO rooms (name) VALUES ('Salle B');
INSERT INTO rooms (name) VALUES ('Salle Innovation');
INSERT INTO rooms (name) VALUES ('Salle Réunion 1');
INSERT INTO rooms (name) VALUES ('Salle Réunion 2');
INSERT INTO rooms (name) VALUES ('Salle Conférence');