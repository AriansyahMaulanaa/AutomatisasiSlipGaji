package com.slipgaji.service;

import com.slipgaji.model.*;
import com.slipgaji.util.Constants;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private static DatabaseService instance;
    private Connection connection;

    private DatabaseService() {}

    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    public void initialize() throws SQLException, IOException {
        Constants.ensureDirectories();
        String url = "jdbc:sqlite:" + Constants.DB_PATH;
        connection = DriverManager.getConnection(url);
        connection.setAutoCommit(true);
        executeSqlScript();
    }

    private void executeSqlScript() throws IOException, SQLException {
        try (InputStream is = getClass().getResourceAsStream("/db/init.sql")) {
            if (is == null) throw new IOException("init.sql not found in resources");
            String sql = new String(is.readAllBytes());
            String[] statements = sql.split(";");
            try (Statement stmt = connection.createStatement()) {
                for (String s : statements) {
                    String trimmed = s.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                        stmt.execute(trimmed);
                    }
                }
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    // ================= USER OPERATIONS =================

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        try (PreparedStatement ps = connection.prepareStatement(check)) {
            ps.setString(1, emp.getEmployeeId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String update = "UPDATE employees SET name=?, email=?, position=?, department=?, base_salary=? WHERE id=?";
                try (PreparedStatement ups = connection.prepareStatement(update)) {
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
                try (PreparedStatement ips = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
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
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
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
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM employees")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // ================= PAYSLIP OPERATIONS =================

    public int savePayslip(Payslip payslip) {
        // Check if payslip already exists for this employee + period
        String check = "SELECT id FROM payslips WHERE employee_id = ? AND period = ?";
        try (PreparedStatement ps = connection.prepareStatement(check)) {
            ps.setInt(1, payslip.getEmployeeId());
            ps.setString(2, payslip.getPeriod());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String update = "UPDATE payslips SET days_present=?, days_absent=?, overtime_hours=?, " +
                        "base_salary=?, overtime_pay=?, deductions=?, allowances=?, net_salary=?, pdf_path=? WHERE id=?";
                try (PreparedStatement ups = connection.prepareStatement(update)) {
                    ups.setInt(1, payslip.getDaysPresent());
                    ups.setInt(2, payslip.getDaysAbsent());
                    ups.setDouble(3, payslip.getOvertimeHours());
                    ups.setDouble(4, payslip.getBaseSalary());
                    ups.setDouble(5, payslip.getOvertimePay());
                    ups.setDouble(6, payslip.getDeductions());
                    ups.setDouble(7, payslip.getAllowances());
                    ups.setDouble(8, payslip.getNetSalary());
                    ups.setString(9, payslip.getPdfPath());
                    ups.setInt(10, id);
                    ups.executeUpdate();
                }
                return id;
            }
        } catch (SQLException e) { e.printStackTrace(); }

        String insert = "INSERT INTO payslips (employee_id, period, days_present, days_absent, overtime_hours, " +
                "base_salary, overtime_pay, deductions, allowances, net_salary, pdf_path) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
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
            ps.setString(11, payslip.getPdfPath());
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
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT period FROM payslips ORDER BY period DESC")) {
            while (rs.next()) {
                periods.add(rs.getString("period"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return periods;
    }

    public Payslip getPayslipById(int id) {
        String sql = "SELECT p.*, e.name as emp_name, e.email as emp_email, e.employee_id as emp_code, " +
                "e.position, e.department FROM payslips p " +
                "JOIN employees e ON p.employee_id = e.id WHERE p.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapPayslip(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void updatePayslipPdfPath(int payslipId, String pdfPath) {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE payslips SET pdf_path = ? WHERE id = ?")) {
            ps.setString(1, pdfPath);
            ps.setInt(2, payslipId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int getPayslipCount() {
        try (Statement stmt = connection.createStatement();
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
        p.setPdfPath(rs.getString("pdf_path"));
        p.setCreatedAt(rs.getString("created_at"));
        p.setEmployeeName(rs.getString("emp_name"));
        p.setEmployeeEmail(rs.getString("emp_email"));
        p.setEmployeeIdCode(rs.getString("emp_code"));
        p.setPosition(rs.getString("position"));
        p.setDepartment(rs.getString("department"));
        return p;
    }

    // ================= SEND HISTORY OPERATIONS =================

    public void saveSendHistory(SendHistory history) {
        String sql = "INSERT INTO send_history (payslip_id, employee_name, employee_email, period, status, error_message, sent_by) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM send_history WHERE status='SUCCESS'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getFailedCount() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM send_history WHERE status='FAILED'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // ================= SETTINGS OPERATIONS =================

    public String getSetting(String key) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT value FROM settings WHERE key = ?")) {
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("value");
        } catch (SQLException e) { e.printStackTrace(); }
        return "";
    }

    public void saveSetting(String key, String value) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR REPLACE INTO settings (key, value) VALUES (?, ?)")) {
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
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
