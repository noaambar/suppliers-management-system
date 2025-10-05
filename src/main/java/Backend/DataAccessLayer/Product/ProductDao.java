package Backend.DataAccessLayer.Product;

import DataTransferLayer.ProductDto;

import java.util.List;

public interface ProductDao {
    void create(ProductDto productDto);
    void delete(int productID);
    void deleteAll();
    ProductDto read(int productID);
    List<ProductDto> readAll();

}
