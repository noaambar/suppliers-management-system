package Backend.DataAccessLayer.ProductsPriceOrder;

import DataTransferLayer.ProductsPriceOrderDto;

import java.util.List;

public class ProductsPriceOrderDaoSql implements ProductsPriceOrderDao {

    private final ProductsPriceOrderController controller = new ProductsPriceOrderController();

    public void create(ProductsPriceOrderDto dto) {
        controller.insert(dto);
    }

    public ProductsPriceOrderDto read(int orderID, int productID) {
        return controller.find(orderID, productID);
    }

    public List<ProductsPriceOrderDto> readAll() {
        return controller.getAll();
    }

    public void update(ProductsPriceOrderDto dto) {
        controller.update(dto);
    }

    public void delete(int orderID, int productID) {
        controller.delete(orderID, productID);
    }

    public void deleteAll() {
        controller.deleteAll();
    }

    public List<ProductsPriceOrderDto> readAllByOrder(int orderIDVal)
    {
        return controller.readAllByOrder(orderIDVal);
    }

}
