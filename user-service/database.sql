-- =====================================================
-- DATABASE SETUP FOR USER SERVICE
-- =====================================================
-- Execute this script in phpMyAdmin or MySQL CLI

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS gdldv_user_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE gdldv_user_db;

-- Note: Tables will be created automatically by Hibernate
-- with spring.jpa.hibernate.ddl-auto=update
