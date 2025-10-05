package Backend.DataAccessLayer.ProductQuantityOrder;


import DataTransferLayer.ProductQuantityOrderDto;
import java.util.List;

public interface ProductQuantityOrderDao {
    void create(ProductQuantityOrderDto dto);
    void delete(int orderID,int productID);
    void deleteAll();
    ProductQuantityOrderDto read(int orderID, int productID);
    List<ProductQuantityOrderDto> readAll();
    void update(ProductQuantityOrderDto dto);
    List<ProductQuantityOrderDto> readAllByOrder(int orderIDVal);

    }
