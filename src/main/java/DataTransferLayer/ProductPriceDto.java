package DataTransferLayer;

public class ProductPriceDto {
    private double price;
    private Integer productID;
    private Integer agreementID;    
    private Integer supplierID;

    public  ProductPriceDto (Integer supplierID,Integer agreementID, Integer productID, double price){
        this.price=price;
        this.productID=productID;
        this.agreementID=agreementID;
        this.supplierID=supplierID;
    }
    public Integer getSupplierID(){
        return supplierID;
    }
    public double getPrice() {
        return price;
    }

    public Integer getProductID() {
        return productID;
    }

    public Integer getAgreementID()
    {
        return agreementID;
    }
}
