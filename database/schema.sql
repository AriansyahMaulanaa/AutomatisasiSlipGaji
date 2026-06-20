-- =============================================================
-- SlipGaji Pro v1.1.0 - Database Schema
-- Database : slipgaji_db
-- Engine   : MariaDB / MySQL
-- =============================================================

CREATE DATABASE IF NOT EXISTS slipgaji_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE slipgaji_db;

-- ===================== USERS =====================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================== EMPLOYEES =====================
CREATE TABLE IF NOT EXISTS employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    position VARCHAR(100),
    department VARCHAR(100),
    base_salary DOUBLE DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================== PAYSLIPS =====================
CREATE TABLE IF NOT EXISTS payslips (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    period VARCHAR(20) NOT NULL,
    days_present INT DEFAULT 0,
    days_absent INT DEFAULT 0,
    overtime_hours DOUBLE DEFAULT 0,
    base_salary DOUBLE DEFAULT 0,
    overtime_pay DOUBLE DEFAULT 0,
    deductions DOUBLE DEFAULT 0,
    allowances DOUBLE DEFAULT 0,
    net_salary DOUBLE DEFAULT 0,
    night_shift_incentive DOUBLE DEFAULT 0,
    is_night_shift TINYINT DEFAULT 0,
    pdf_path TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================== SEND HISTORY =====================
CREATE TABLE IF NOT EXISTS send_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    payslip_id INT NOT NULL,
    employee_name VARCHAR(255),
    employee_email VARCHAR(255),
    period VARCHAR(20),
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('SUCCESS','FAILED'),
    error_message TEXT,
    sent_by VARCHAR(100),
    FOREIGN KEY (payslip_id) REFERENCES payslips(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================== SETTINGS =====================
CREATE TABLE IF NOT EXISTS settings (
    `key` VARCHAR(100) PRIMARY KEY,
    `value` TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================== SEED DATA =====================

-- Users (password akan di-hash otomatis oleh aplikasi saat pertama login)
INSERT IGNORE INTO users (username, password, role) VALUES
    ('spv', 'spv123', 'SPV'),
    ('manager', 'manager123', 'MANAGER');

-- Default settings
INSERT IGNORE INTO settings (`key`, `value`) VALUES
    ('smtp_host', 'smtp.gmail.com'),
    ('smtp_port', '587'),
    ('smtp_email', ''),
    ('smtp_password', ''),
    ('company_name', 'CV. Mandiri Sukses Pratama'),
    ('company_address', 'Taman Royal, Jl. Pinus Niaga Center No.081, Banten 15119'),
    ('crewstore_overtime_rate_per_hour', '25000'),
    ('crewstore_daily_rate_divisor', '22'),
    ('crewstore_transport_allowance', '500000'),
    ('crewstore_meal_allowance', '300000'),
    ('crewstore_night_shift_rate', '50000'),
    ('store_leader_overtime_rate_per_hour', '30000'),
    ('store_leader_daily_rate_divisor', '22'),
    ('store_leader_transport_allowance', '600000'),
    ('store_leader_meal_allowance', '350000'),
    ('store_leader_night_shift_rate', '55000'),
    ('manager_overtime_rate_per_hour', '35000'),
    ('manager_daily_rate_divisor', '22'),
    ('manager_transport_allowance', '700000'),
    ('manager_meal_allowance', '400000'),
    ('manager_night_shift_rate', '60000');
