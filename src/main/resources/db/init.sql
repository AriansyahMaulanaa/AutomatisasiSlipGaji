CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('SUPERVISOR','GENERAL_MANAGER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

CREATE TABLE IF NOT EXISTS settings (
    `key` VARCHAR(100) PRIMARY KEY,
    `value` TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO users (username, password, role) VALUES
    ('supervisor', 'supervisor123', 'SUPERVISOR'),
    ('gm', 'manager123', 'GENERAL_MANAGER');

INSERT IGNORE INTO settings (`key`, `value`) VALUES
    ('smtp_host', 'smtp.gmail.com'),
    ('smtp_port', '587'),
    ('smtp_email', ''),
    ('smtp_password', ''),
    ('company_name', 'CV. Mandiri Sukses Pratama'),
    ('company_address', 'Taman Royal, Jl. Pinus Niaga Center No.081, Banten 15119'),
    ('overtime_rate_per_hour', '25000'),
    ('daily_rate_divisor', '22'),
    ('transport_allowance', '500000'),
    ('meal_allowance', '300000'),
    ('night_shift_rate', '50000');
