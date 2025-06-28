-- PostgreSQL Initialization Script for DesiFans Platform
-- This script creates databases and users for financial services

-- Create additional databases
CREATE DATABASE desifans_payments;
CREATE DATABASE desifans_notifications;
CREATE DATABASE desifans_analytics;

-- Create application user
CREATE USER desifans_app WITH PASSWORD 'desifans123';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE desifans_payments TO desifans_app;
GRANT ALL PRIVILEGES ON DATABASE desifans_notifications TO desifans_app;
GRANT ALL PRIVILEGES ON DATABASE desifans_analytics TO desifans_app;

-- Connect to payments database and create initial schema
\c desifans_payments;

-- Create schema for payment service
CREATE SCHEMA IF NOT EXISTS payments;
CREATE SCHEMA IF NOT EXISTS subscriptions;
CREATE SCHEMA IF NOT EXISTS transactions;

-- Grant schema privileges
GRANT ALL ON SCHEMA payments TO desifans_app;
GRANT ALL ON SCHEMA subscriptions TO desifans_app;
GRANT ALL ON SCHEMA transactions TO desifans_app;
