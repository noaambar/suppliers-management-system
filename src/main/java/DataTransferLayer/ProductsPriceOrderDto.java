package DataTransferLayer;

public class ProductsPriceOrderDto {
    private Integer orderID;
    private Integer productID;
    private Double price;

    public ProductsPriceOrderDto(Integer orderID,Integer productID,Double price)
    {
        this.orderID=orderID;
        this.productID=productID;
        this.price=price;
    }

    public Integer getProductID() {
        return productID;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getOrderID() {
        return orderID;
    }
}
