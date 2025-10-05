package DataTransferLayer;

public class ProductQuantityDiscountDto {
    private Integer supplierID;
    private Integer agreementID;
    private Integer productID;
    private Integer quantity;
    private Integer discountPercentage;

    public ProductQuantityDiscountDto(Integer supplierID, Integer agreementID,Integer productID, Integer quantity, Integer discountPercentage)
    {
        this.supplierID=supplierID;
        this.agreementID=agreementID;
        this.discountPercentage=discountPercentage;
        this.productID=productID;
        this.quantity=quantity;
    }
    public Integer getSupplierID(){
        return supplierID;
    }
    public Integer getProductID() {
        return productID;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }
    public Integer getAgreementID()
    {
        return agreementID;
    }
}
