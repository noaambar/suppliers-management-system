package Backend.BusinessLayer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import DataTransferLayer.OrderDto;

public class OrderBL {
    private Integer orderID;
    private Integer supplierID;
    private Map<Integer,Double> itemsPrice;
    private Map<Integer, Integer> itemsQuantity;
    private Date date;
    private String phonenumber;
    private String status;

    public OrderBL(Integer orderID, Integer supplierID,Map<Integer, Integer> itemsQuantity, String phonenumber) {
        this.orderID = orderID;
        this.supplierID = supplierID;
        this.date = new Date();
        this.itemsPrice = new HashMap<>();
        this.itemsQuantity = itemsQuantity;
        this.status="inProgress";
        this.phonenumber=phonenumber;
    }
    public OrderBL(OrderDto orderDto){
        this.orderID = orderDto.getOrderID();
        this.supplierID = orderDto.getSupplierID();
        this.date = orderDto.getDate();
        this.itemsPrice = new HashMap<>();
        this.itemsQuantity = new HashMap<>();
        this.status=orderDto.getStatus();
        this.phonenumber=orderDto.getPhonenumber();
    }
    public void setItemsPrice(Map<Integer,Double> itemsPrice){
        this.itemsPrice = itemsPrice;
    }
    public void setItemsQuantity(Map<Integer, Integer> itemsQuantity){
        this.itemsQuantity=itemsQuantity;
    }
    public String getPhonenumber(){
        return phonenumber;
    }
    public void setPhoneNumber(String PhoneNumber){
        this.phonenumber=PhoneNumber;
    }
    public void SetPrices(Map<Integer,Double> itemsPrice)
    {
        this.itemsPrice=itemsPrice;
    }
    public void setStatus()
    {
        this.status="done";
    }
    public String getStatus()
    {
        return this.status;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public Integer getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(Integer supplierID) {
        this.supplierID = supplierID;
    }

    public Map<Integer, Double> getItemsPrice() {
        return itemsPrice;
    }

    public Map<Integer, Integer> getItemsQuantity() {
        return itemsQuantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

     public OrderDto toDto() {
        return new OrderDto(
                getOrderID(),
                getSupplierID(),
                 getDate(),
                getPhonenumber(),
                getStatus()
        );
    }
}

