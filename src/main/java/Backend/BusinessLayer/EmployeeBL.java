package Backend.BusinessLayer;

import DataTransferLayer.EmployeeDto;

public class EmployeeBL {
    private String employeeName;
    private Integer employeeID;
    private String phoneNumber;

    public EmployeeBL(String employeeName, Integer employeeID, String phoneNumber) {
        this.employeeName = employeeName;
        this.employeeID = employeeID;
        this.phoneNumber = phoneNumber;
    }
    public EmployeeBL(EmployeeDto employeeDto){
        this.employeeName = employeeDto.getName();
        this.employeeID = employeeDto.getEmployeeId();
        this.phoneNumber = employeeDto.getPhoneNumber();
    }
    // Getters

    public String getEmployeeName() {
        return employeeName;
    }

    public Integer getEmployeeID() {
        return employeeID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public EmployeeDto toDto() {
        return new EmployeeDto(getEmployeeID(),getPhoneNumber(),getEmployeeName());
    }


    }



