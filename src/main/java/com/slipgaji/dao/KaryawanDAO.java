package com.slipgaji.dao;

import com.slipgaji.model.Employee;
import com.slipgaji.service.DatabaseService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KaryawanDAO {

    private final DatabaseService db = DatabaseService.getInstance();

    public Employee findByBarcode(String barcode) {
        String sql = "SELECT *, COALESCE(employment_type, 'TETAP') as emp_type FROM employees WHERE barcode = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapEmployee(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Employee findById(int id) {
        String sql = "SELECT *, COALESCE(employment_type, 'TETAP') as emp_type FROM employees WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapEmployee(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Employee findByEmployeeId(String employeeId) {
        String sql = "SELECT *, COALESCE(employment_type, 'TETAP') as emp_type FROM employees WHERE employee_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapEmployee(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Employee> getAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT *, COALESCE(employment_type, 'TETAP') as emp_type FROM employees ORDER BY name";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapEmployee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int save(Employee emp) {
        String sql = "INSERT INTO employees (employee_id, name, email, position, department, base_salary, employment_type, birth_date, photo, barcode, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, emp.getEmployeeId());
            ps.setString(2, emp.getName());
            ps.setString(3, emp.getEmail() != null ? emp.getEmail() : "");
            ps.setString(4, emp.getPosition());
            ps.setString(5, emp.getDepartment());
            ps.setDouble(6, emp.getBaseSalary());
            ps.setString(7, emp.getEmploymentType() != null ? emp.getEmploymentType() : "TETAP");
            ps.setDate(8, emp.getBirthDate() != null ? Date.valueOf(emp.getBirthDate()) : null);
            ps.setString(9, emp.getPhoto());
            ps.setString(10, emp.getBarcode());
            ps.setString(11, emp.getStatus() != null ? emp.getStatus() : "Aktif");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void update(Employee emp) {
        String sql = "UPDATE employees SET name=?, email=?, position=?, department=?, base_salary=?, " +
                "employment_type=?, birth_date=?, photo=?, barcode=?, status=? WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail() != null ? emp.getEmail() : "");
            ps.setString(3, emp.getPosition());
            ps.setString(4, emp.getDepartment());
            ps.setDouble(5, emp.getBaseSalary());
            ps.setString(6, emp.getEmploymentType() != null ? emp.getEmploymentType() : "TETAP");
            ps.setDate(7, emp.getBirthDate() != null ? Date.valueOf(emp.getBirthDate()) : null);
            ps.setString(8, emp.getPhoto());
            ps.setString(9, emp.getBarcode());
            ps.setString(10, emp.getStatus() != null ? emp.getStatus() : "Aktif");
            ps.setInt(11, emp.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCount() {
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM employees");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setId(rs.getInt("id"));
        e.setEmployeeId(rs.getString("employee_id"));
        e.setName(rs.getString("name"));
        e.setEmail(rs.getString("email"));
        e.setPosition(rs.getString("position"));
        e.setDepartment(rs.getString("department"));
        e.setBaseSalary(rs.getDouble("base_salary"));
        e.setEmploymentType(rs.getString("emp_type"));
        Date bd = rs.getDate("birth_date");
        if (bd != null) e.setBirthDate(bd.toLocalDate());
        e.setPhoto(rs.getString("photo"));
        e.setBarcode(rs.getString("barcode"));
        e.setStatus(rs.getString("status"));
        return e;
    }
}
