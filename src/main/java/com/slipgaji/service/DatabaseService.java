package com.slipgaji.service;

import com.slipgaji.model.*;
import com.slipgaji.util.ConfigManager;
import com.slipgaji.util.Constants;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private static DatabaseService instance;
    private HikariDataSource dataSource;

    private DatabaseService() {}

    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    public void initialize() throws SQLException, IOException {
        Constants.ensureDirectories();

        String host = ConfigManager.getDbHost();
        int port = ConfigManager.getDbPort();
        String dbName = ConfigManager.getDbName();
        String user = ConfigManager.getDbUser();
        String pass = ConfigManager.getDbPassword();

        // First, create the database if it doesn't exist
        String baseUrl = "jdbc:mariadb://" + host + ":" + port + "/";
        try (Connection conn = DriverManager.getConnection(baseUrl, user, pass)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
            }
        }

        // Setup HikariCP connection pool
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(baseUrl + dbName);
        config.setUsername(user);
        config.setPassword(pass);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(300000);        // 5 minutes
        config.setConnectionTimeout(10000);   // 10 seconds
        config.setMaxLifetime(600000);        // 10 minutes
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);

        // Execute init SQL script
        executeSqlScript();
        runMigrations();
    }

    private void executeSqlScript() throws IOException, SQLException {
        try (InputStream is = getClass().getResourceAsStream("/db/init.sql")) {
            if (is == null) throw new IOException("init.sql not found in resources");
            String sql = new String(is.readAllBytes());
            // Split on semicolons, but be careful with statements
            String[] statements = sql.split(";");
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                for (String s : statements) {
                    String trimmed = s.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                        stmt.execute(trimmed);
                    }
                }
            }
        }
    }

<<<<<<< HEAD
=======
<<<<<<< HEAD
    public Connection getConnection() {
        return connection;
=======
>>>>>>> 0274c08
    /**
     * Run schema migrations for existing databases that don't have new columns.
     */
    private void runMigrations() {
        // Add night_shift_incentive column if missing
        addColumnIfNotExists("payslips", "night_shift_incentive", "DOUBLE DEFAULT 0");
        // Add is_night_shift column if missing
        addColumnIfNotExists("payslips", "is_night_shift", "TINYINT DEFAULT 0");
        // Add night_shift_rate setting if missing
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT IGNORE INTO settings (`key`, `value`) VALUES ('night_shift_rate', '50000')")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addColumnIfNotExists(String table, String column, String type) {
        try (Connection conn = getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, table, column);
            if (!rs.next()) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
                }
            }
            rs.close();
        } catch (SQLException e) {
            // Column might already exist; ignore
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
<<<<<<< HEAD
=======
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08
    }

    // ================= USER OPERATIONS =================

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(User.Role.valueOf(rs.getString("role")));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ================= EMPLOYEE OPERATIONS =================

    public int saveOrUpdateEmployee(Employee emp) {
        String check = "SELECT id FROM employees WHERE employee_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setString(1, emp.getEmployeeId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String update = "UPDATE employees SET name=?, email=?, position=?, department=?, base_salary=? WHERE id=?";
                try (PreparedStatement ups = conn.prepareStatement(update)) {
                    ups.setString(1, emp.getName());
                    ups.setString(2, emp.getEmail());
                    ups.setString(3, emp.getPosition());
                    ups.setString(4, emp.getDepartment());
                    ups.setDouble(5, emp.getBaseSalary());
                    ups.setInt(6, id);
                    ups.executeUpdate();
                }
                return id;
            } else {
                String insert = "INSERT INTO employees (employee_id, name, email, position, department, base_salary) VALUES (?,?,?,?,?,?)";
                try (PreparedStatement ips = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    ips.setString(1, emp.getEmployeeId());
                    ips.setString(2, emp.getName());
                    ips.setString(3, emp.getEmail());
                    ips.setString(4, emp.getPosition());
                    ips.setString(5, emp.getDepartment());
                    ips.setDouble(6, emp.getBaseSalary());
                    ips.executeUpdate();
                    ResultSet keys = ips.getGeneratedKeys();
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY name";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Employee e = new Employee();
                e.setId(rs.getInt("id"));
                e.setEmployeeId(rs.getString("employee_id"));
                e.setName(rs.getString("name"));
                e.setEmail(rs.getString("email"));
                e.setPosition(rs.getString("position"));
                e.setDepartment(rs.getString("department"));
                e.setBaseSalary(rs.getDouble("base_salary"));
                list.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getEmployeeCount() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM employees")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // ================= PAYSLIP OPERATIONS =================

    public int savePayslip(Payslip payslip) {
        // Check if payslip already exists for this employee + period
        String check = "SELECT id FROM payslips WHERE employee_id = ? AND period = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setInt(1, payslip.getEmployeeId());
            ps.setString(2, payslip.getPeriod());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String update = "UPDATE payslips SET days_present=?, days_absent=?, overtime_hours=?, " +
<<<<<<< HEAD
                        "base_salary=?, overtime_pay=?, deductions=?, allowances=?, net_salary=?, " +
                        "night_shift_incentive=?, is_night_shift=?, pdf_path=? WHERE id=?";
                try (PreparedStatement ups = conn.prepareStatement(update)) {
=======
<<<<<<< HEAD
                        "base_salary=?, overtime_pay=?, deductions=?, allowances=?, net_salary=?, pdf_path=? WHERE id=?";
                try (PreparedStatement ups = connection.prepareStatement(update)) {
=======
                        "base_salary=?, overtime_pay=?, deductions=?, allowances=?, net_salary=?, " +
                        "night_shift_incentive=?, is_night_shift=?, pdf_path=? WHERE id=?";
                try (PreparedStatement ups = conn.prepareStatement(update)) {
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08
                    ups.setInt(1, payslip.getDaysPresent());
                    ups.setInt(2, payslip.getDaysAbsent());
                    ups.setDouble(3, payslip.getOvertimeHours());
                    ups.setDouble(4, payslip.getBaseSalary());
                    ups.setDouble(5, payslip.getOvertimePay());
                    ups.setDouble(6, payslip.getDeductions());
                    ups.setDouble(7, payslip.getAllowances());
                    ups.setDouble(8, payslip.getNetSalary());
                    ups.setDouble(9, payslip.getNightShiftIncentive());
                    ups.setInt(10, payslip.isNightShift() ? 1 : 0);
                    ups.setString(11, payslip.getPdfPath());
                    ups.setInt(12, id);
                    ups.executeUpdate();
                }
                return id;
            }
        } catch (SQLException e) { e.printStackTrace(); }

        String insert = "INSERT INTO payslips (employee_id, period, days_present, days_absent, overtime_hours, " +
<<<<<<< HEAD
=======
<<<<<<< HEAD
                "base_salary, overtime_pay, deductions, allowances, net_salary, pdf_path) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
=======
>>>>>>> 0274c08
                "base_salary, overtime_pay, deductions, allowances, net_salary, night_shift_incentive, is_night_shift, pdf_path) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
<<<<<<< HEAD
=======
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08
            ps.setInt(1, payslip.getEmployeeId());
            ps.setString(2, payslip.getPeriod());
            ps.setInt(3, payslip.getDaysPresent());
            ps.setInt(4, payslip.getDaysAbsent());
            ps.setDouble(5, payslip.getOvertimeHours());
            ps.setDouble(6, payslip.getBaseSalary());
            ps.setDouble(7, payslip.getOvertimePay());
            ps.setDouble(8, payslip.getDeductions());
            ps.setDouble(9, payslip.getAllowances());
            ps.setDouble(10, payslip.getNetSalary());
            ps.setDouble(11, payslip.getNightShiftIncentive());
            ps.setInt(12, payslip.isNightShift() ? 1 : 0);
            ps.setString(13, payslip.getPdfPath());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Payslip> getPayslipsByPeriod(String period) {
        List<Payslip> list = new ArrayList<>();
        String sql = "SELECT p.*, e.name as emp_name, e.email as emp_email, e.employee_id as emp_code, " +
                "e.position, e.department FROM payslips p " +
                "JOIN employees e ON p.employee_id = e.id " +
                (period != null && !period.isEmpty() ? "WHERE p.period = ? " : "") +
                "ORDER BY e.name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (period != null && !period.isEmpty()) {
                ps.setString(1, period);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Payslip p = mapPayslip(rs);
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getPayslipPeriods() {
        List<String> periods = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT period FROM payslips ORDER BY period DESC")) {
            while (rs.next()) {
                periods.add(rs.getString("period"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return periods;
    }

<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
>>>>>>> 0274c08
    public List<PeriodSummary> getPayslipPeriodSummaries() {
        List<PeriodSummary> summaries = new ArrayList<>();
        String sql = "SELECT p.period, COUNT(p.id) as slip_count, SUM(p.net_salary) as total_salary, " +
                     "SUM(CASE WHEN p.pdf_path IS NOT NULL AND p.pdf_path != '' THEN 1 ELSE 0 END) as pdf_count " +
                     "FROM payslips p GROUP BY p.period ORDER BY p.period DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PeriodSummary summary = new PeriodSummary();
                String period = rs.getString("period");
                summary.setPeriod(period);
                summary.setSlipCount(rs.getInt("slip_count"));
                summary.setTotalSalary(rs.getDouble("total_salary"));
                summary.setPdfGeneratedCount(rs.getInt("pdf_count"));

                // We need to count how many successful emails were sent for this period
                int sentCount = 0;
                String sentSql = "SELECT COUNT(DISTINCT payslip_id) FROM send_history WHERE period = ? AND status = 'SUCCESS'";
                try (PreparedStatement ps = conn.prepareStatement(sentSql)) {
                    ps.setString(1, period);
                    ResultSet rsSent = ps.executeQuery();
                    if (rsSent.next()) {
                        sentCount = rsSent.getInt(1);
                    }
                }
                summary.setEmailSentCount(sentCount);
                
                summaries.add(summary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summaries;
    }

<<<<<<< HEAD
=======
>>>>>>> e7da53e (update fitur dan db)
>>>>>>> 0274c08
    public Payslip getPayslipById(int id) {
        String sql = "SELECT p.*, e.name as emp_name, e.email as emp_email, e.employee_id as emp_code, " +
                "e.position, e.department FROM payslips p " +
                "JOIN employees e ON p.employee_id = e.id WHERE p.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapPayslip(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void updatePayslipPdfPath(int payslipId, String pdfPath) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE payslips SET pdf_path = ? WHERE id = ?")) {
            ps.setString(1, pdfPath);
            ps.setInt(2, payslipId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int getPayslipCount() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM payslips")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Payslip mapPayslip(ResultSet rs) throws SQLException {
        Payslip p = new Payslip();
        p.setId(rs.getInt("id"));
        p.setEmployeeId(rs.getInt("employee_id"));
        p.setPeriod(rs.getString("period"));
        p.setDaysPresent(rs.getInt("days_present"));
        p.setDaysAbsent(rs.getInt("days_absent"));
        p.setOvertimeHours(rs.getDouble("overtime_hours"));
        p.setBaseSalary(rs.getDouble("base_salary"));
        p.setOvertimePay(rs.getDouble("overtime_pay"));
        p.setDeductions(rs.getDouble("deductions"));
        p.setAllowances(rs.getDouble("allowances"));
        p.setNetSalary(rs.getDouble("net_salary"));
        p.setNightShiftIncentive(getColumnSafe(rs, "night_shift_incentive", 0.0));
        p.setNightShift(getColumnSafe(rs, "is_night_shift", 0) == 1);
        p.setPdfPath(rs.getString("pdf_path"));
        p.setCreatedAt(rs.getString("created_at"));
        p.setEmployeeName(rs.getString("emp_name"));
        p.setEmployeeEmail(rs.getString("emp_email"));
        p.setEmployeeIdCode(rs.getString("emp_code"));
        p.setPosition(rs.getString("position"));
        p.setDepartment(rs.getString("department"));
        return p;
    }

    private double getColumnSafe(ResultSet rs, String column, double defaultValue) {
        try {
            return rs.getDouble(column);
        } catch (SQLException e) {
            return defaultValue;
        }
    }

    private int getColumnSafe(ResultSet rs, String column, int defaultValue) {
        try {
            return rs.getInt(column);
        } catch (SQLException e) {
            return defaultValue;
        }
    }

    public void deletePayslip(int id) {
        String sql = "DELETE FROM payslips WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================= SEND HISTORY OPERATIONS =================

    public void saveSendHistory(SendHistory history) {
        String sql = "INSERT INTO send_history (payslip_id, employee_name, employee_email, period, status, error_message, sent_by) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, history.getPayslipId());
            ps.setString(2, history.getEmployeeName());
            ps.setString(3, history.getEmployeeEmail());
            ps.setString(4, history.getPeriod());
            ps.setString(5, history.getStatus());
            ps.setString(6, history.getErrorMessage());
            ps.setString(7, history.getSentBy());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<SendHistory> getSendHistory(String period) {
        List<SendHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM send_history " +
                (period != null && !period.isEmpty() ? "WHERE period = ? " : "") +
                "ORDER BY sent_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (period != null && !period.isEmpty()) {
                ps.setString(1, period);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SendHistory h = new SendHistory();
                h.setId(rs.getInt("id"));
                h.setPayslipId(rs.getInt("payslip_id"));
                h.setEmployeeName(rs.getString("employee_name"));
                h.setEmployeeEmail(rs.getString("employee_email"));
                h.setPeriod(rs.getString("period"));
                h.setSentAt(rs.getString("sent_at"));
                h.setStatus(rs.getString("status"));
                h.setErrorMessage(rs.getString("error_message"));
                h.setSentBy(rs.getString("sent_by"));
                list.add(h);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getSentCount() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM send_history WHERE status='SUCCESS'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getFailedCount() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM send_history WHERE status='FAILED'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // ================= SETTINGS OPERATIONS =================

    public String getSetting(String key) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT `value` FROM settings WHERE `key` = ?")) {
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("value");
        } catch (SQLException e) { e.printStackTrace(); }
        return "";
    }

    public void saveSetting(String key, String value) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO settings (`key`, `value`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `value` = VALUES(`value`)")) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getSettingDouble(String key, double defaultValue) {
        String val = getSetting(key);
        try {
            return val != null && !val.isEmpty() ? Double.parseDouble(val) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
