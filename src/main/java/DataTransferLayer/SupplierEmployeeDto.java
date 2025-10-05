package DataTransferLayer;

public class SupplierEmployeeDto {
    private Integer supplierID;
    private Integer employeeID;


    public SupplierEmployeeDto(Integer supplierID, Integer employeeID)
    {
        this.supplierID=supplierID;
        this.employeeID=employeeID;
    }

    public Integer getEmployeeID() {
        return employeeID;
    }

    public Integer getSupplierID() {
        return supplierID;
    }


}
