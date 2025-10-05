package DataTransferLayer;

public class OutgoingOrderDto {
    private int id;
    private int day;

    public OutgoingOrderDto(int id, int day) {
        this.id = id;
        this.day = day;
    }

    public int getOrderID() {
        return id;
    }

    public int getDay() {
        return day;
    }

}
