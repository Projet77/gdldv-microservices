-- ===================================================================
-- Script d'insertion des rôles pour le système GDLDV
-- À exécuter AVANT la première inscription d'utilisateur
-- ===================================================================

USE gdldv_user_db;

-- Insertion des rôles de base
INSERT INTO roles (name) VALUES ('ROLE_CLIENT');
INSERT INTO roles (name) VALUES ('ROLE_EMPLOYEE');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Vérifier l'insertion
SELECT * FROM roles;
