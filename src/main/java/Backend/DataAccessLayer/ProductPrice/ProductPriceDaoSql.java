package Backend.DataAccessLayer.ProductPrice;


import DataTransferLayer.ProductPriceDto;
import java.util.List;

public class ProductPriceDaoSql implements ProductPriceDao{
    private ProductPriceController controller = new ProductPriceController();

    @Override
    public void create(ProductPriceDto productPrice) {
        controller.insert(productPrice);
    }

    @Override
    public ProductPriceDto read(int supplier, int productID, int agreementID) {
        return controller.find(supplier, productID, agreementID);
    }

    @Override
    public List<ProductPriceDto> readAllByAgreement(int supplier, int agreementID) {
        return controller.getAllByAgreement(supplier, agreementID);
    }

    @Override
    public List<ProductPriceDto> readAll() {
        return controller.getAll();
    }

    @Override
    public void update(ProductPriceDto productPrice) {
        controller.update(productPrice);
    }

    @Override
    public void delete(int supplier,int productID, int agreementID) {
        controller.delete(supplier,productID, agreementID);
    }

    @Override
    public void deleteAll() {
        controller.deleteAll();
    }
}
