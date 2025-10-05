package Backend.DataAccessLayer.ProductQuantityDiscount;

import DataTransferLayer.ProductQuantityDiscountDto;

import java.util.List;

public interface ProductQuantityDiscountDao {
    void create(ProductQuantityDiscountDto product);

    public void delete(int supplier, int agreementId,int productID, int quantity, int discount);
    
    void deleteAll();

    ProductQuantityDiscountDto read(int supplier, int agreementId, int ProductID, int quantity);

    List<ProductQuantityDiscountDto> readAllByAgreement(int supplier, int agreementId);

    List<ProductQuantityDiscountDto> readAll();

    void update(ProductQuantityDiscountDto product);

    void deleteByProduct(int supplier, int agreementId, int productID);

}
