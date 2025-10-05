package DataTransferLayer;

public class OutgoingOrder_ItemTypeDto {
    private int orderID;
    private int itemsID;
    private int quantity;

    public OutgoingOrder_ItemTypeDto(int orderID, int itemsID, int quantity) {
        this.orderID = orderID;
        this.itemsID = itemsID;
        this.quantity = quantity;
    }   

    public int getOrderID() {
        return orderID;
    }

    public int getItemsID() {
        return itemsID;
    }

    public int getQuantity() {
        return quantity;
    }    
}
