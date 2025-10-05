package Backend.DataAccessLayer.OutgoingOrder_ItemType;

import java.util.List;

import DataTransferLayer.OutgoingOrder_ItemTypeDto;

public class OutgoingOrder_ItemTypeDaoSql implements OutgoingOrder_ItemTypeDao {

    private final OutgoingOrder_ItemsController controller = new OutgoingOrder_ItemsController();
    
    @Override
    public void create(OutgoingOrder_ItemTypeDto order) {
        controller.insert(order);
    }
    @Override
    public void update(OutgoingOrder_ItemTypeDto order) {
        controller.update(order);
    }
    @Override
    public void delete(int orderId, int itemsId) {
        controller.delete(orderId, itemsId);
    }
    @Override
    public void deleteAll() {
        controller.deleteAll();
    }
    @Override
    public OutgoingOrder_ItemTypeDto read(int orderId, int itemsId) {
        return controller.find(orderId, itemsId);
    }
    @Override
    public List<OutgoingOrder_ItemTypeDto> read(int orderId) {
        return controller.findByOrderId(orderId);
    }

    


    
}
