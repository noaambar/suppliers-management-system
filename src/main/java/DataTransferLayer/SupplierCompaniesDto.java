package DataTransferLayer;

public class SupplierCompaniesDto {
    private Integer supplierID;
    private String company;

    public SupplierCompaniesDto(Integer supplierID, String company){
        this.company=company;
        this.supplierID=supplierID;
    }

    public Integer getSupplierID() {
        return supplierID;
    }

    public String getCompany() {
        return company;
    }
}
