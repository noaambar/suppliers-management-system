package Backend.DataAccessLayer.OutgoingOrder;

import java.util.List;

import DataTransferLayer.OutgoingOrderDto;

public interface OutgoingOrderDao {
    void create(OutgoingOrderDto order);
    void delete(int orderId);
    void deleteAll();
    OutgoingOrderDto read(int orderId);
    List<OutgoingOrderDto> readAll();  
}
