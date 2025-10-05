package Backend.DataAccessLayer.ProductsPriceOrder;



import DataTransferLayer.ProductsPriceOrderDto;

import java.util.List;

public interface ProductsPriceOrderDao {

    public void create(ProductsPriceOrderDto dto);

    public ProductsPriceOrderDto read(int orderID, int productID);

    public List<ProductsPriceOrderDto> readAll();

    public void update(ProductsPriceOrderDto dto);

    public void delete(int orderID, int productID);

    public void deleteAll() ;

    List<ProductsPriceOrderDto> readAllByOrder(int orderIDVal);

    }
