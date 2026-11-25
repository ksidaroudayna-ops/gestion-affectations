-- Schéma MySQL (équivalent de ce que Hibernate génère avec ddl-auto=update)
CREATE DATABASE IF NOT EXISTS affectations_db CHARACTER SET utf8mb4;
USE affectations_db;

CREATE TABLE utilisateur (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  username  VARCHAR(50)  NOT NULL UNIQUE,
  password  VARCHAR(255) NOT NULL,                       -- hash BCrypt
  role      VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN','RESPONSABLE'))
);

CREATE TABLE enseignant (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom         VARCHAR(80)  NOT NULL,
  prenom      VARCHAR(80)  NOT NULL,
  email       VARCHAR(120) NOT NULL UNIQUE,              -- RG1
  grade       VARCHAR(30)  NOT NULL,
  departement VARCHAR(80)
);

CREATE TABLE matiere (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  code           VARCHAR(20)  NOT NULL UNIQUE,           -- RG2
  intitule       VARCHAR(150) NOT NULL,
  volume_horaire INT NOT NULL CHECK (volume_horaire > 0),-- RG3
  semestre       INT CHECK (semestre IN (1,2))
);

CREATE TABLE affectation (
  id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
  enseignant_id       BIGINT NOT NULL,
  matiere_id          BIGINT NOT NULL,
  type_seance         VARCHAR(5) NOT NULL CHECK (type_seance IN ('CM','TD','TP')),
  heures              INT NOT NULL CHECK (heures > 0),   -- RG4
  annee_universitaire VARCHAR(9) NOT NULL,
  CONSTRAINT fk_aff_ens FOREIGN KEY (enseignant_id) REFERENCES enseignant(id),  -- RG8
  CONSTRAINT fk_aff_mat FOREIGN KEY (matiere_id)    REFERENCES matiere(id),     -- RG9
  CONSTRAINT uq_aff UNIQUE (enseignant_id, matiere_id, type_seance, annee_universitaire) -- RG7
);
-- RG5 (charge max par grade) et RG6 (volume horaire matière) sont contrôlées dans AffectationService.
