package Backend.DataAccessLayer.Product;

import DataTransferLayer.ProductDto;
import java.util.List;


public class ProductDaoSql implements ProductDao {
    private final ProductController controller = new ProductController();

    @Override
    public void create(ProductDto item) {
        controller.insert(item);
    }

    @Override
    public void delete(int itemID) {
        controller.delete(itemID);
    }

    @Override
    public void deleteAll() {
        controller.deleteAll();
    }

    @Override
    public ProductDto read(int itemID) {
        return controller.find(itemID);
    }

    @Override
    public List<ProductDto> readAll() {
        return controller.getAll();
    }

}
