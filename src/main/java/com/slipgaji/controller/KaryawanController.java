package com.slipgaji.controller;

import com.slipgaji.dao.KaryawanDAO;
import com.slipgaji.model.Employee;

import java.time.LocalDate;
import java.util.List;

public class KaryawanController {

    private final KaryawanDAO karyawanDAO = new KaryawanDAO();

    public List<Employee> getAllKaryawan() {
        return karyawanDAO.getAll();
    }

    public Employee getById(int id) {
        return karyawanDAO.findById(id);
    }

    public Employee getByEmployeeId(String employeeId) {
        return karyawanDAO.findByEmployeeId(employeeId);
    }

    public int addKaryawan(String employeeId, String name, String email, String position,
                           String department, double baseSalary, String employmentType,
                           LocalDate birthDate, String photo, String barcode, String status) {
        Employee emp = new Employee();
        emp.setEmployeeId(employeeId);
        emp.setName(name);
        emp.setEmail(email);
        emp.setPosition(position);
        emp.setDepartment(department);
        emp.setBaseSalary(baseSalary);
        emp.setEmploymentType(employmentType);
        emp.setBirthDate(birthDate);
        emp.setPhoto(photo);
        emp.setBarcode(barcode);
        emp.setStatus(status);
        return karyawanDAO.save(emp);
    }

    public void updateKaryawan(int id, String employeeId, String name, String email, String position,
                               String department, double baseSalary, String employmentType,
                               LocalDate birthDate, String photo, String barcode, String status) {
        Employee emp = new Employee();
        emp.setId(id);
        emp.setEmployeeId(employeeId);
        emp.setName(name);
        emp.setEmail(email);
        emp.setPosition(position);
        emp.setDepartment(department);
        emp.setBaseSalary(baseSalary);
        emp.setEmploymentType(employmentType);
        emp.setBirthDate(birthDate);
        emp.setPhoto(photo);
        emp.setBarcode(barcode);
        emp.setStatus(status);
        karyawanDAO.update(emp);
    }

    public void deleteKaryawan(int id) {
        Employee emp = karyawanDAO.findById(id);
        if (emp != null && emp.getPhoto() != null) {
            com.slipgaji.util.PhotoUtil.deletePhoto(emp.getPhoto());
        }
        karyawanDAO.delete(id);
    }

    public int getKaryawanCount() {
        return karyawanDAO.getCount();
    }
}
