package Backend.DataAccessLayer.Order;

import DataTransferLayer.OrderDto;

import java.util.List;

public interface OrderDao {

        void create(OrderDto order);
        void delete(int orderID);
        void deleteAll();
        OrderDto read(int orderID);
        List<OrderDto> readAll();
        void update(OrderDto order);


}
