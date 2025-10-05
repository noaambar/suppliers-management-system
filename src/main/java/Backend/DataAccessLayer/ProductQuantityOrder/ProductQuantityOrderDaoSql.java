package Backend.DataAccessLayer.ProductQuantityOrder;


import DataTransferLayer.ProductQuantityOrderDto;

import java.util.List;

public class ProductQuantityOrderDaoSql implements ProductQuantityOrderDao {
    private final ProductQuantityOrderController controller = new ProductQuantityOrderController();

    @Override
    public void create(ProductQuantityOrderDto dto) {
        controller.insert(dto);
    }

    @Override
    public void delete(int orderID, int productId) {
        controller.delete(orderID, productId);
    }

    @Override
    public void deleteAll() {
        List<ProductQuantityOrderDto> all = controller.getAll();
        for (ProductQuantityOrderDto dto : all) {
            controller.delete(dto.getOrderID(), dto.getProductId());
        }
    }

    @Override
    public ProductQuantityOrderDto read(int orderID, int ProductId) {
        return controller.find(orderID, ProductId);
    }

    @Override
    public List<ProductQuantityOrderDto> readAll() {
        return controller.getAll();
    }

    @Override
    public void update(ProductQuantityOrderDto dto) {
        controller.update(dto);
    }

    public List<ProductQuantityOrderDto> readAllByOrder(int orderIDVal) {
        return controller.readAllByOrder(orderIDVal);
    }
}