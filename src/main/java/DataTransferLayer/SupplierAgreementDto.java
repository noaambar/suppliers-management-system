package DataTransferLayer;

public class SupplierAgreementDto {
    private Integer supplierAgreementID;
    private Integer supplierID;
    private String paymentMethod;
    private String paymentTime;

    // Constructor
    public SupplierAgreementDto(Integer supplierAgreementID, Integer supplierID, String paymentMethod, String paymentTime) {
        this.supplierAgreementID = supplierAgreementID;
        this.supplierID = supplierID;
        this.paymentMethod = paymentMethod;
        this.paymentTime = paymentTime;
    }

    // Getters
    public Integer getSupplierAgreementID() {
        return supplierAgreementID;
    }

    public Integer getSupplierID() {
        return supplierID;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentTime() {
        return paymentTime;
    }
}

