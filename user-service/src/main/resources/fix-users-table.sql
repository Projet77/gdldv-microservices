-- ===================================================================
-- Script de correction de la table users
-- Supprime la colonne 'role' car nous utilisons user_roles (ManyToMany)
-- ===================================================================

USE gdldv_user_db;

-- Supprimer la colonne role (on utilise user_roles à la place)
ALTER TABLE users DROP COLUMN IF EXISTS role;

-- Vérifier la structure
DESCRIBE users;
DESCRIBE user_roles;
