package DataTransferLayer;

public class FutureOrderDto {
    private int day;
    private int itemTypeId;
    private int quantity;
    private double cost;

    public FutureOrderDto(int day, int itemTypeId, int quantity, double cost) {
        this.day = day;
        this.itemTypeId = itemTypeId;
        this.quantity = quantity;
        this.cost = cost;
    }

    public int getDay() {
        return day;
    }
    public int getItemTypeId() {
        return itemTypeId;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getCost() {
        return cost;
    }

}
