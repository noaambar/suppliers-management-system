package Backend.DataAccessLayer.ProductQuantityDiscount;

import DataTransferLayer.ProductQuantityDiscountDto;

import java.util.List;

public class ProductQuantityDiscountDaoSql implements ProductQuantityDiscountDao{
    private ProductQuantityDiscountController controller = new ProductQuantityDiscountController();

    @Override
    public void create(ProductQuantityDiscountDto dto) {
        controller.insert(dto);
    }

    @Override
    public ProductQuantityDiscountDto read(int supplier, int agreementId,int productID, int quantity) {
        return controller.find(agreementId,productID, quantity);
    }

    @Override
    public List<ProductQuantityDiscountDto> readAll() {
        return controller.getAll();
    }

    @Override
    public List<ProductQuantityDiscountDto> readAllByAgreement(int supplier, int agreementId) {
        return controller.getAllByAgreement(supplier, agreementId);
    }

    @Override
    public void update(ProductQuantityDiscountDto dto) {
        controller.update(dto);
    }

    @Override
    public void delete(int supplier, int agreementId,int productID, int quantity, int discount) {
        controller.delete(supplier, agreementId,productID, quantity, discount);
    }

    @Override
    public void deleteByProduct(int supplier, int agreementID, int productID) {
        controller.deleteByProduct(supplier, agreementID, productID);
    }

    @Override
    public void deleteAll() {
        controller.deleteAll();
    }
}
