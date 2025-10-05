package Backend.DataAccessLayer.OutgoingOrder_ItemType;

import java.util.List;

import DataTransferLayer.OutgoingOrder_ItemTypeDto;

public interface OutgoingOrder_ItemTypeDao {
    void create(OutgoingOrder_ItemTypeDto order);
    void update(OutgoingOrder_ItemTypeDto order);
    void delete(int orderId, int itemsId);
    void deleteAll();
    OutgoingOrder_ItemTypeDto read(int orderId, int itemTypeId);
    List<OutgoingOrder_ItemTypeDto> read(int orderId);
}
