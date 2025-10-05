package DataTransferLayer;

public class EmployeeDto {
    private Integer employeeId;
    private String name;
    private String phoneNumber;

    public EmployeeDto(Integer emId, String name, String phoneNumber)
    {
        this.employeeId=emId;
        this.name=name;
        this.phoneNumber=phoneNumber;

    }

    public Integer getEmployeeId()
    {
        return this.employeeId;
    }
    public String getPhoneNumber()
    {
        return this.phoneNumber;
    }
    public String getName()
    {
        return this.name;
    }
}
