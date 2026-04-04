CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT NOT NULL CHECK(role IN ('SUPERVISOR','GENERAL_MANAGER')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employees (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_id TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    position TEXT,
    department TEXT,
    base_salary REAL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payslips (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_id INTEGER NOT NULL,
    period TEXT NOT NULL,
    days_present INTEGER DEFAULT 0,
    days_absent INTEGER DEFAULT 0,
    overtime_hours REAL DEFAULT 0,
    base_salary REAL DEFAULT 0,
    overtime_pay REAL DEFAULT 0,
    deductions REAL DEFAULT 0,
    allowances REAL DEFAULT 0,
    net_salary REAL DEFAULT 0,
    pdf_path TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS send_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    payslip_id INTEGER NOT NULL,
    employee_name TEXT,
    employee_email TEXT,
    period TEXT,
    sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    status TEXT CHECK(status IN ('SUCCESS','FAILED')),
    error_message TEXT,
    sent_by TEXT,
    FOREIGN KEY (payslip_id) REFERENCES payslips(id)
);

CREATE TABLE IF NOT EXISTS settings (
    key TEXT PRIMARY KEY,
    value TEXT
);

INSERT OR IGNORE INTO users (username, password, role) VALUES
    ('supervisor', 'supervisor123', 'SUPERVISOR'),
    ('gm', 'manager123', 'GENERAL_MANAGER');

INSERT OR IGNORE INTO settings (key, value) VALUES
    ('smtp_host', 'smtp.gmail.com'),
    ('smtp_port', '587'),
    ('smtp_email', ''),
    ('smtp_password', ''),
    ('company_name', 'CV. Mandiri Sukses Pratama'),
    ('company_address', 'Taman Royal, Jl. Pinus Niaga Center No.081, Banten 15119'),
    ('overtime_rate_per_hour', '25000'),
    ('daily_rate_divisor', '22'),
    ('transport_allowance', '500000'),
    ('meal_allowance', '300000');
