package Backend.DataAccessLayer.OutgoingOrder;

import java.util.List;

import DataTransferLayer.OutgoingOrderDto;

public class OutgoingOrderDaoSql implements OutgoingOrderDao {

    private final OutgoingOrderController controller = new OutgoingOrderController();

    @Override
    public void create(OutgoingOrderDto order) {
        controller.insert(order);
    }

    @Override
    public void delete(int orderId) {
        controller.delete(orderId);
    }

    @Override
    public void deleteAll() {
        controller.deleteAll();
    }

    @Override
    public OutgoingOrderDto read(int orderId) {
        return controller.find(orderId);
    }

    @Override
    public List<OutgoingOrderDto> readAll() {
        return controller.getAll();
    }
    
}
