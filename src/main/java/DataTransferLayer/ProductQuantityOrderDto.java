package DataTransferLayer;

public class ProductQuantityOrderDto {
    private Integer orderID;
    private Integer productId;
    private Integer quantity;

    public ProductQuantityOrderDto(Integer orderID, Integer productId, Integer quantity)
    {
        this.orderID=orderID;
        this.productId=productId;
        this.quantity=quantity;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getProductId() {
        return productId;
    }
}
