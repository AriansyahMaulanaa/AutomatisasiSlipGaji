package com.slipgaji.model;

public class SendHistory {
    private int id;
    private int payslipId;
    private String employeeName;
    private String employeeEmail;
    private String period;
    private String sentAt;
    private String status; // SUCCESS or FAILED
    private String errorMessage;
    private String sentBy;

    public SendHistory() {}

    public SendHistory(int payslipId, String employeeName, String employeeEmail,
                       String period, String status, String errorMessage, String sentBy) {
        this.payslipId = payslipId;
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
        this.period = period;
        this.status = status;
        this.errorMessage = errorMessage;
        this.sentBy = sentBy;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPayslipId() { return payslipId; }
    public void setPayslipId(int payslipId) { this.payslipId = payslipId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getEmployeeEmail() { return employeeEmail; }
    public void setEmployeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getSentBy() { return sentBy; }
    public void setSentBy(String sentBy) { this.sentBy = sentBy; }
}
