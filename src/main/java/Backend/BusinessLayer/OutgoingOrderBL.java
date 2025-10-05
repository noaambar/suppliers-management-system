package Backend.BusinessLayer;

import java.util.HashMap;

import DataTransferLayer.OutgoingOrderDto;

public class OutgoingOrderBL {

    private int orderID;
    private HashMap<Integer, Integer> itemsQuantity;
    private int day;

    public OutgoingOrderBL(int orderID, HashMap<Integer, Integer> itemsQuantity, int day) {
        this.orderID = orderID;
        this.itemsQuantity = itemsQuantity;
        this.day = day;
    }

    public OutgoingOrderBL(OutgoingOrderDto dto) {
        this.orderID = dto.getOrderID();
        this.day = dto.getDay();
        this.itemsQuantity = new HashMap<>();
    }

    public int getOrderID() {
        return orderID;
    }

    public HashMap<Integer, Integer> getItemsQuantity() {
        return itemsQuantity;
    }

    public boolean setItemTypeQuantity(int itemID, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        if (itemsQuantity.containsKey(itemID)) {
            itemsQuantity.put(itemID, quantity);
            return false;
        }
        itemsQuantity.put(itemID, quantity);
        return true;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        checkDate(day);
        this.day = day;
    }

    public void deleteItem(int itemID) {
        if (itemsQuantity.containsKey(itemID)) {
            itemsQuantity.remove(itemID);
        }
    }

    public OutgoingOrderDto toDto() {
        return new OutgoingOrderDto(
                getOrderID(),
                getDay()
        );
    }

    private void checkDate(int day) {
        if (day < 0 || day > 6) {
            throw new IllegalArgumentException("Day must be between 0 and 6.");
        }
    }

}
