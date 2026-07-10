package com.slipgaji.dao;

import com.slipgaji.model.Presensi;
import com.slipgaji.service.DatabaseService;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PresensiDAO {

    private final DatabaseService db = DatabaseService.getInstance();

    public int save(Presensi presensi) {
        String sql = "INSERT INTO presensi (employee_id, tanggal, jam, jenis_presensi) VALUES (?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, presensi.getEmployeeId());
            ps.setDate(2, Date.valueOf(presensi.getTanggal()));
            ps.setTime(3, Time.valueOf(presensi.getJam()));
            ps.setString(4, presensi.getJenisPresensi());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean isAlreadyCheckedIn(int employeeId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM presensi WHERE employee_id = ? AND tanggal = ? AND jenis_presensi = 'Masuk'";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isAlreadyCheckedOut(int employeeId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM presensi WHERE employee_id = ? AND tanggal = ? AND jenis_presensi = 'Pulang'";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Presensi> getTodayPresensi(int employeeId) {
        return getPresensiByDate(employeeId, LocalDate.now());
    }

    public List<Presensi> getPresensiByDate(int employeeId, LocalDate date) {
        List<Presensi> list = new ArrayList<>();
        String sql = "SELECT * FROM presensi WHERE employee_id = ? AND tanggal = ? ORDER BY jam";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPresensi(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Presensi> getHistoryByDate(LocalDate date) {
        List<Presensi> list = new ArrayList<>();
        String sql = "SELECT p.*, e.name as emp_name, e.employee_id as emp_code, e.position " +
                "FROM presensi p JOIN employees e ON p.employee_id = e.id " +
                "WHERE p.tanggal = ? ORDER BY p.jam DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Presensi pr = mapPresensi(rs);
                    pr.setEmployeeName(rs.getString("emp_name"));
                    pr.setEmployeeIdCode(rs.getString("emp_code"));
                    pr.setEmployeePosition(rs.getString("position"));
                    list.add(pr);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Presensi> getHistoryByDateRange(LocalDate start, LocalDate end) {
        List<Presensi> list = new ArrayList<>();
        String sql = "SELECT p.*, e.name as emp_name, e.employee_id as emp_code, e.position " +
                "FROM presensi p JOIN employees e ON p.employee_id = e.id " +
                "WHERE p.tanggal BETWEEN ? AND ? ORDER BY p.tanggal DESC, p.jam DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Presensi pr = mapPresensi(rs);
                    pr.setEmployeeName(rs.getString("emp_name"));
                    pr.setEmployeeIdCode(rs.getString("emp_code"));
                    pr.setEmployeePosition(rs.getString("position"));
                    list.add(pr);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getAggregatedHistoryByDate(LocalDate date) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.employee_id, e.employee_id AS emp_code, e.name AS emp_name, e.position, " +
                "p.tanggal, " +
                "MIN(CASE WHEN p.jenis_presensi = 'Masuk' THEN p.jam END) AS jam_masuk, " +
                "MAX(CASE WHEN p.jenis_presensi = 'Pulang' THEN p.jam END) AS jam_pulang " +
                "FROM presensi p " +
                "JOIN employees e ON p.employee_id = e.id " +
                "WHERE p.tanggal = ? " +
                "GROUP BY p.employee_id, p.tanggal, e.employee_id, e.name, e.position " +
                "ORDER BY e.name";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[7];
                    row[0] = rs.getInt("employee_id");
                    row[1] = rs.getString("emp_code");
                    row[2] = rs.getString("emp_name");
                    row[3] = rs.getString("position");
                    row[4] = rs.getDate("tanggal").toLocalDate();
                    Time tm = rs.getTime("jam_masuk");
                    row[5] = tm != null ? tm.toLocalTime() : null;
                    Time tp = rs.getTime("jam_pulang");
                    row[6] = tp != null ? tp.toLocalTime() : null;
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getCountByDateRange(LocalDate start, LocalDate end, int employeeId) {
        String sql = "SELECT COUNT(DISTINCT tanggal) FROM presensi WHERE employee_id = ? AND tanggal BETWEEN ? AND ? AND jenis_presensi = 'Masuk'";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Presensi mapPresensi(ResultSet rs) throws SQLException {
        Presensi p = new Presensi();
        p.setId(rs.getInt("id"));
        p.setEmployeeId(rs.getInt("employee_id"));
        p.setTanggal(rs.getDate("tanggal").toLocalDate());
        p.setJam(rs.getTime("jam").toLocalTime());
        p.setJenisPresensi(rs.getString("jenis_presensi"));
        p.setCreatedAt(rs.getString("created_at"));
        return p;
    }
}
