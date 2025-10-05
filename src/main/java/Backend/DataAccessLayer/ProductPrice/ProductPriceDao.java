package Backend.DataAccessLayer.ProductPrice;
import DataTransferLayer.ProductPriceDto;
import java.util.List;

public interface ProductPriceDao {
    void create(ProductPriceDto productPrice);
    ProductPriceDto read(int supplier, int productID, int agreementID);
    List<ProductPriceDto> readAllByAgreement(int supplier, int agreementID);
    List<ProductPriceDto> readAll();
    void update(ProductPriceDto productPrice);
    void delete(int supplier, int productID, int agreementID);
    void deleteAll();
}
