package Backend.BusinessLayer;

public interface SupplierRepository {

    void newAgreement(SupplierAgreementBL agreementBL);
    void removeAgreement(int supplierID,int agreementID);
    void updateAgreement(SupplierAgreementBL agreementBL);

    void newOrder(OrderBL orderBL);
    void removeOrder(int orderID);
    void updateOrder(OrderBL orderBL);

    void newProduct(ProductBL productBL);
    void removeProduct(int productID);

    void newProductPrice(int supplierID,int agreementId, int productId, double price);
    void removeProductPrice(int supplierID, int agreementId, int ProductId);
    void updateProductPrice(int supplierID, int agreementId, int ProductId, double price);

    void newProductQuantityDiscount(int supplierID, int agreementId,int productID, int quantity, int discountPercentage);
    void removeProductQuantityDiscount(int supplierID, int agreementId, int productID, int quantity, int discount);
    void updateProductQuantityDiscount(int supplierID, int agreementId,int productID, int quantity, int discountPercentage);
    void removeProductsQuantityDiscount(int supplierID,int agreementId,int productID);
    void newProductQuantityOrder(int OrderId,int productID, int quantity);
    void removeProductQuantityOrder(int OrderId,int productID);
    void updateProductQuantityOrder(int OrderId,int productID, int quantity);

    void newProductPriceOrder(int orderId, int productId, double price);
    void removeProductPriceOrder(int OrderId,int productID);
    void updateProductPriceOrder(int orderId, int productId, double price);

    void newSupplier(SupplierBL supplierBL);
    void removeSupplier(int SupplierID);
    void updateSupplier(SupplierBL supplierBL);

    void newCompany(int supplierID, String company);
    void removeCompany(int supplierID, String company);

    void newEmployee(EmployeeBL employeeBL);
    void removeEmployee(int employeeId);

    void newSupplierEmployee(int supplierId,int employeeId);
    void removeSupplierEmployee(int supplierId,int employeeId);

    void loadData();
    void deleteData();
}
