package Backend.DataAccessLayer.Order;

import DataTransferLayer.OrderDto;

import java.util.List;

public class OrderDaoSql implements OrderDao {

    private OrderController controller = new OrderController();

    @Override
    public void create(OrderDto order) {
        controller.insert(order);
    }

    @Override
    public OrderDto read(int orderID) {
        return controller.find(orderID);
    }

    @Override
    public List<OrderDto> readAll() {
        return controller.getAll();
    }

    @Override
    public void update(OrderDto order) {
        controller.update(order);
    }

    @Override
    public void delete(int orderID) {
        controller.delete(orderID);
    }

    @Override
    public void deleteAll() {
        controller.deleteAll();
    }
}
