package Backend.DataAccessLayer.FutureOrder;

import java.util.List;
import DataTransferLayer.FutureOrderDto;

public class FutureOrderSql implements FutureOrderDao {

    private final FutureOrderController controller = new FutureOrderController();

    @Override
    public void create(FutureOrderDto order) {
        controller.insert(order);
    }

    @Override
    public List<FutureOrderDto> read(int day) {
        return controller.getFutureOrdersByDay(day);
    }

    @Override
    public List<FutureOrderDto> readAll() {
        return controller.getAllFutureOrders();
    }

    @Override
    public void update(FutureOrderDto order) {
        controller.update(order);
    }

    @Override
    public void delete(int day) {
        controller.deleteFutureOrdersByDay(day);
    }

    @Override
    public void deleteAll() {
        controller.deleteAllFutureOrders();
    }



}
