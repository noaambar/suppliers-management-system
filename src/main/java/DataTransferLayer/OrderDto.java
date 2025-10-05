package DataTransferLayer;

import java.util.Date;

public class OrderDto {
    private Integer orderID;
    private Integer supplierID;
    private Date date;
    private String phonenumber;
    private String status;

    public OrderDto(Integer orderID,Integer supplierID, Date date, String phonenumber, String status)
    {
        this.orderID=orderID;
        this.date=date;
        this.status=status;
        this.phonenumber=phonenumber;
        this.supplierID=supplierID;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public Date getDate() {
        return date;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getStatus() {
        return status;
    }

    public Integer getSupplierID() {
        return supplierID;
    }
}
