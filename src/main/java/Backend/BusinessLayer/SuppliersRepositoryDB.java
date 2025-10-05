package Backend.BusinessLayer;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import Backend.DataAccessLayer.Agreement.AgreementDao;
import Backend.DataAccessLayer.Employee.EmployeeDao;
import Backend.DataAccessLayer.Employee.EmployeeDaoSql;
import Backend.DataAccessLayer.Order.OrderDao;
import Backend.DataAccessLayer.Order.OrderDaoSql;
import Backend.DataAccessLayer.ProductQuantityDiscount.ProductQuantityDiscountDaoSql;
import Backend.DataAccessLayer.ProductQuantityOrder.ProductQuantityOrderDao;
import Backend.DataAccessLayer.ProductQuantityOrder.ProductQuantityOrderDaoSql;
import Backend.DataAccessLayer.ProductQuantityDiscount.ProductQuantityDiscountDao;
import Backend.DataAccessLayer.ProductsPriceOrder.ProductsPriceOrderDao;
import Backend.DataAccessLayer.ProductsPriceOrder.ProductsPriceOrderDaoSql;
import Backend.DataAccessLayer.ProductPrice.ProductPriceDaoSql;
import Backend.DataAccessLayer.SupplierCompanies.SupplierCompaniesDao;
import Backend.DataAccessLayer.SupplierCompanies.SupplierCompaniesDaoSql;
import Backend.DataAccessLayer.SupplierEmployee.SupplierEmployeeDao;
import Backend.DataAccessLayer.SupplierEmployee.SupplierEmployeeDaoSql;
import DataTransferLayer.*;
import Backend.DataAccessLayer.Product.ProductDao;
import Backend.DataAccessLayer.Product.ProductDaoSql;
import Backend.DataAccessLayer.ProductPrice.ProductPriceDao;
import Backend.DataAccessLayer.Supplier.SupplierDao;
import Backend.DataAccessLayer.Supplier.SupplierDaoSql;
import Backend.DataAccessLayer.Agreement.AgreementDaoSql;

public class SuppliersRepositoryDB implements SupplierRepository {

    private AgreementDao agreementDao;
    private OrderDao orderDao;
    private ProductDao productDao;
    private ProductPriceDao productPrice;
    private ProductQuantityDiscountDao productQuantityDiscountDao;
    private ProductsPriceOrderDao productsPriceOrderDao;
    private ProductQuantityOrderDao productQuantityOrderDao;
    private SupplierDao supplierDao;
    private SupplierCompaniesDao supplierCompaniesDao;
    private SupplierEmployeeDao supplierEmployeeDao;
    private EmployeeDao employeeDao;

    private Map<Integer, SupplierBL> dictionarySupplierBL;
    private Map<Integer, ArrayList<OrderBL>> orders;
    private Map<Integer, EmployeeBL> employees;
    private Map<Integer, ProductBL> items;

    // Database connection and other necessary fields would be defined here
    public SuppliersRepositoryDB() {
        this.agreementDao = new AgreementDaoSql();
        this.orderDao = new OrderDaoSql();
        this.productDao = new ProductDaoSql();  
        this.productPrice = new ProductPriceDaoSql();
        this.productQuantityDiscountDao = new ProductQuantityDiscountDaoSql();
        this.productsPriceOrderDao = new ProductsPriceOrderDaoSql();
        this.productQuantityOrderDao = new ProductQuantityOrderDaoSql();
        this.supplierDao = new SupplierDaoSql();
        this.supplierCompaniesDao = new SupplierCompaniesDaoSql();  
        this.supplierEmployeeDao = new SupplierEmployeeDaoSql();
        this.employeeDao = new EmployeeDaoSql();

        this.dictionarySupplierBL = new HashMap<>();
        this.orders = new HashMap<>();
        this.employees = new HashMap<>();
        this.items = new HashMap<>();
    }

    private AgreementDao getAgreementDao() {
        return agreementDao;
    }

    private OrderDao getOrderDao() {
        return orderDao;
    }

    private ProductDao getProductDao() {
        return productDao;
    }

    private ProductPriceDao getProductPriceDao() {
        return productPrice;
    }

    private ProductQuantityDiscountDao getProductQuantityDiscountDao() {
        return productQuantityDiscountDao;
    }

    private ProductsPriceOrderDao getProductsPriceOrderDao() {
        return productsPriceOrderDao;
    }

    private ProductQuantityOrderDao getProductQuantityOrderDao() {
        return productQuantityOrderDao;
    }

    private SupplierDao getSupplierDao() {
        return supplierDao;
    }

    private SupplierCompaniesDao getSupplierCompaniesDao() {
        return supplierCompaniesDao;
    }

    private SupplierEmployeeDao getSupplierEmployeeDao() {
        return supplierEmployeeDao;
    }

    private EmployeeDao getEmployeeDao() {
        return employeeDao;
    }

    public Map<Integer, SupplierBL> getDictionarySupplierBL() {
        return dictionarySupplierBL;
    }

    public Map<Integer, ArrayList<OrderBL>> getOrders() {
        return orders;
    }

    public Map<Integer, EmployeeBL> getEmployees() {
        return employees;
    }

    public Map<Integer, ProductBL> getItems() {
        return items;
    }

    public void newAgreement(SupplierAgreementBL agreementBL){
        getAgreementDao().create(agreementBL.toDto());
        for (Map.Entry<Integer, Double> entry : agreementBL.getItemPrice().entrySet()) {
            Integer productId = entry.getKey();
            double price = entry.getValue();
            newProductPrice(agreementBL.getSupplierID(), agreementBL.getSupplierAgreementID(), productId, price);
        }
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : agreementBL.getQuantityDiscountAgreement().entrySet()) {
            Integer productId = entry.getKey();
            Map<Integer, Integer> discountMap = entry.getValue();
            for (Map.Entry<Integer, Integer> discountEntry : discountMap.entrySet()) {
                Integer quantity = discountEntry.getKey();
                Integer discountPercentage = discountEntry.getValue();
                newProductQuantityDiscount(agreementBL.getSupplierID(),agreementBL.getSupplierAgreementID(), productId, quantity, discountPercentage);
            }
        }
    }
    
    public void removeAgreement(int supplier, int agreementID){
        SupplierAgreementBL agreement = getSupplierAgreementBL(agreementID);
        for (Map.Entry<Integer, Double> entry : agreement.getItemPrice().entrySet()) {
            int productId = entry.getKey();
            removeProductPrice(supplier, agreementID, productId);
        }

        for (Map.Entry<Integer, Map<Integer, Integer>> entry : agreement.getQuantityDiscountAgreement().entrySet()) {
            int productId = entry.getKey();
            Map<Integer, Integer> discountMap = entry.getValue();
            for (Map.Entry<Integer, Integer> discountEntry : discountMap.entrySet()) {
                int quantity = discountEntry.getKey();
                removeProductQuantityDiscount(supplier, agreementID, productId, quantity,discountEntry.getValue());
            }
        }
        getAgreementDao().delete(agreementID);
    }
    
    public void removeProductsQuantityDiscount(int supplier, int agreementId, int productID) {
        getProductQuantityDiscountDao().deleteByProduct(supplier, agreementId, productID);
    }
    
    public void updateAgreement(SupplierAgreementBL agreementBL){
        getAgreementDao().update(agreementBL.toDto());
    }
    @Override
    public void updateOrder(OrderBL orderBL) {
        getOrderDao().update(orderBL.toDto());

        // Update product quantities
        for (Map.Entry<Integer, Integer> entry : orderBL.getItemsQuantity().entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            updateProductQuantityOrder(orderBL.getOrderID(), productId, quantity);
        }

        // Update product prices
        for (Map.Entry<Integer, Double> entry : orderBL.getItemsPrice().entrySet()) {
            int productId = entry.getKey();
            double price = entry.getValue();
            updateProductPriceOrder(orderBL.getOrderID(), productId, price);
        }

    }

    public void newOrder(OrderBL orderBL){
        getOrderDao().create(orderBL.toDto());
        for (Map.Entry<Integer, Integer> entry : orderBL.getItemsQuantity().entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            newProductQuantityOrder(orderBL.getOrderID(), productId, quantity);     
        }
        for (Map.Entry<Integer, Double> entry : orderBL.getItemsPrice().entrySet()) {
            int productId = entry.getKey();
            double price = entry.getValue();
            newProductPriceOrder(orderBL.getOrderID(), productId, price);
        }
        getOrders().computeIfAbsent(orderBL.getSupplierID(), k -> new ArrayList<>()).add(orderBL);
    }

    public void removeOrder(int orderID){
        getOrderDao().delete(orderID);
        OrderBL order = null;
        for (Map.Entry<Integer, ArrayList<OrderBL>> entry : getOrders().entrySet()) {
            for (OrderBL o : entry.getValue()) {
                if (o.getOrderID() == orderID) {
                    order = o;
                    entry.getValue().remove(order);
                    break;
                }
            }
            break;
        }
        if (order != null) {
            for (Map.Entry<Integer, Integer> entry : order.getItemsQuantity().entrySet()) {
                int productId = entry.getKey();
                removeProductQuantityOrder(orderID, productId);
            }
            for (Map.Entry<Integer, Double> entry : order.getItemsPrice().entrySet()) {
                int productId = entry.getKey();
                removeProductPriceOrder(productId, orderID);
            }
        }
        
    }

    public void newProduct(ProductBL productBL) {
        getProductDao().create(productBL.toDto());
        getItems().put(productBL.getItemID(), productBL);
    }

    public void removeProduct(int productID) {
        getProductDao().delete(productID);
        getItems().remove(productID);
    }
    
    public void newProductPrice(int supplierID, int agreementId, int productId, double price) {
        getProductPriceDao().create(new ProductPriceDto(supplierID,agreementId, productId, price));
    }

    public void removeProductPrice(int supplier,int agreementId, int productId) {
        getProductPriceDao().delete(supplier, agreementId, productId);
    }

    public void updateProductPrice(int supplierID,int agreementId, int productId, double price) {
        getProductPriceDao().update(new ProductPriceDto(supplierID,agreementId, productId, price));
    }

    public void newProductQuantityDiscount(int supplierID, int agreementId, int productID, int quantity, int discountPercentage) {
        getProductQuantityDiscountDao().create(new ProductQuantityDiscountDto(supplierID, agreementId, productID, quantity, discountPercentage));
    }

    public void removeProductQuantityDiscount(int supplier, int agreementId, int productID, int quantity, int discount){
        getProductQuantityDiscountDao().delete(supplier, agreementId, productID, quantity,discount);
    }

    public void updateProductQuantityDiscount(int supplierID, int agreementId, int productID, int quantity, int discountPercentage) {
        getProductQuantityDiscountDao().update(new ProductQuantityDiscountDto(supplierID, agreementId, productID, quantity, discountPercentage));
    }

    public void newProductQuantityOrder(int orderId, int productID, int quantity) {
        getProductQuantityOrderDao().create(new ProductQuantityOrderDto(orderId, productID, quantity));
    }

    public void removeProductQuantityOrder(int orderId, int productID) {
        getProductQuantityOrderDao().delete(orderId, productID);
    }
    
    public void updateProductQuantityOrder(int orderId, int productID, int quantity) {
        getProductQuantityOrderDao().update(new ProductQuantityOrderDto(orderId, productID, quantity));
    }

    public void newProductPriceOrder(int orderId, int productID, double price) {
        getProductsPriceOrderDao().create(new ProductsPriceOrderDto(orderId, productID, price));
    }

    public void removeProductPriceOrder(int orderId, int productID) {
        getProductsPriceOrderDao().delete(orderId, productID);
    }
    
    public void updateProductPriceOrder(int orderId, int productID, double price) {
        getProductsPriceOrderDao().update(new ProductsPriceOrderDto(orderId, productID, price));
    }

    public void newSupplier(SupplierBL supplierBL){
        getSupplierDao().create(supplierBL.toDto());
        getDictionarySupplierBL().put(supplierBL.getSupplierID(), supplierBL);
        for (SupplierAgreementBL agreement : supplierBL.getSupplierAgreement()) {
            newAgreement(agreement);
        }
        List<String> companies= supplierBL.getCompanies();
        for(int i=0; i<supplierBL.getCompanies().size(); i++){
            newCompany(supplierBL.getSupplierID(), companies.get(i));
        }
        for (Integer employeeId : supplierBL.getEmployees()) {
            newSupplierEmployee(supplierBL.getSupplierID(), employeeId);
        }
        orders.put(supplierBL.getSupplierID(), new ArrayList<>());
    }

    public void removeSupplier(int SupplierID) {
        getSupplierDao().delete(SupplierID);
        SupplierBL supplier = getDictionarySupplierBL().get(SupplierID);
        if (supplier == null) {
            return; // Supplier not found
        }
        getDictionarySupplierBL().remove(SupplierID);
        for (SupplierAgreementBL agreement : supplier.getSupplierAgreement()) {
            removeAgreement(agreement.getSupplierID(), agreement.getSupplierAgreementID());
        }
        for (String company : supplier.getCompanies()) {
            removeCompany(supplier.getSupplierID(), company);
        }
        for (Integer employeeId : supplier.getEmployees()) {
            removeSupplierEmployee(SupplierID, employeeId);
        }
    }

    public void updateSupplier(SupplierBL supplierBL) {
        getSupplierDao().update(supplierBL.toDto());
    }
    
    public void newCompany(int supplierID, String company) {
        getSupplierCompaniesDao().create( new SupplierCompaniesDto(supplierID, company));
    }

    public void removeCompany(int supplierID, String company) {
        getSupplierCompaniesDao().delete(supplierID, company);
    }

    public void newEmployee(EmployeeBL employeeBL) {
        getEmployeeDao().create(employeeBL.toDto());
        getEmployees().put(employeeBL.getEmployeeID(), employeeBL);
    }

    public void removeEmployee(int employeeId) {
        getEmployeeDao().delete(employeeId);
        getEmployees().remove(employeeId);

        getSupplierEmployeeDao().deletebyEmployee(employeeId);
    }

    public void newSupplierEmployee(int supplierID, int employeeId) {
        getSupplierEmployeeDao().create(new SupplierEmployeeDto(supplierID, employeeId));
    }

    public void removeSupplierEmployee(int supplierID, int employeeId) {
        getSupplierEmployeeDao().delete(supplierID, employeeId);
    }     

    private SupplierAgreementBL getSupplierAgreementBL(int agreementID) {
        for (Map.Entry<Integer, SupplierBL> entry : getDictionarySupplierBL().entrySet()) {
            SupplierBL supplier = entry.getValue();
            return supplier.getAgreements(agreementID);
        }
        return null;
    }

    @Override
    public void loadData() {
        loadSuppliers();
        loadOrders();
        loadEmployees();
        loadProducts();
    }

    private void loadSuppliers() {
        List<SupplierDto> suppliers = getSupplierDao().readAll();
        for (SupplierDto supplierDto : suppliers) {
            SupplierBL supplierBL = new SupplierBL(supplierDto);
            List<SupplierEmployeeDto> employeeDtos = getSupplierEmployeeDao().readAllBySupplier(supplierBL.getSupplierID());
            List<Integer> employeeIDs= new ArrayList<>();
            for(int i=0; i<employeeDtos.size(); i++){
                employeeIDs.add(employeeDtos.get(i).getEmployeeID());
            }
            supplierBL.setEmployees(employeeIDs);
            
            // Load agreements
            List<SupplierAgreementDto> agreements = getAgreementDao().readAllBySupplier(supplierBL.getSupplierID());
            List<SupplierAgreementBL> agreementBLs = new ArrayList<>();
            for (SupplierAgreementDto agreementDto : agreements) {
                SupplierAgreementBL agreementBL = new SupplierAgreementBL(agreementDto);
                loadAgreementDetails(agreementBL);
                agreementBLs.add(agreementBL);
            }
            supplierBL.setAgreements(agreementBLs);

            // Load companies
            List<SupplierCompaniesDto> companies = getSupplierCompaniesDao().readAllBySupplier(supplierBL.getSupplierID());
            List<String> companyNames = new ArrayList<>();
            for (SupplierCompaniesDto companyDto : companies) {
                companyNames.add(companyDto.getCompany());
            }
            supplierBL.setCompanies(companyNames);

            // Load employees
            List<SupplierEmployeeDto> employees = getSupplierEmployeeDao().readAllBySupplier(supplierBL.getSupplierID());
            List<Integer> employeeIds = new ArrayList<>();
            for (SupplierEmployeeDto employeeDto : employees) {
                employeeIds.add(employeeDto.getEmployeeID());
            }
            supplierBL.setEmployees(employeeIds);

            // Add supplier to the dictionary
            getDictionarySupplierBL().put(supplierBL.getSupplierID(), supplierBL);
        }
    }

    private void loadAgreementDetails(SupplierAgreementBL agreementBL) {
        // Load item prices
        List<ProductPriceDto> productPrices = getProductPriceDao().readAllByAgreement(agreementBL.getSupplierID(), agreementBL.getSupplierAgreementID());
        HashMap<Integer, Double> itemPrices = new HashMap<>();
        for (ProductPriceDto productPriceDto : productPrices) {
            itemPrices.put(productPriceDto.getProductID(), productPriceDto.getPrice());
        }
        agreementBL.setItemPrices(itemPrices);

        // Load quantity discounts
        List<ProductQuantityDiscountDto> quantityDiscounts = getProductQuantityDiscountDao().readAllByAgreement(agreementBL.getSupplierID(), agreementBL.getSupplierAgreementID());
        HashMap<Integer, Map<Integer, Integer>> quantityDiscountsMap = new HashMap<>();
        for (ProductQuantityDiscountDto discountDto : quantityDiscounts) {
            int productId = discountDto.getProductID();
            int quantity = discountDto.getQuantity();
            int discountPercentage = discountDto.getDiscountPercentage();
            quantityDiscountsMap.computeIfAbsent(productId, k -> new HashMap<>())
                                .put(quantity, discountPercentage);
        }
        agreementBL.setQuantityDiscountAgreement(quantityDiscountsMap);
    }

    private void loadOrders() {
        List<OrderDto> orderDtos = getOrderDao().readAll();
        for (OrderDto orderDto : orderDtos) {
            OrderBL orderBL = new OrderBL(orderDto);
            // Load items and prices
            List<ProductQuantityOrderDto> productQuantities = getProductQuantityOrderDao().readAllByOrder(orderBL.getOrderID());
            HashMap<Integer, Integer> itemsQuantity = new HashMap<>();
            for (ProductQuantityOrderDto productQuantity : productQuantities) {
                itemsQuantity.put(productQuantity.getProductId(), productQuantity.getQuantity());
            }
            orderBL.setItemsQuantity(itemsQuantity);

            List<ProductsPriceOrderDto> productsPrices = getProductsPriceOrderDao().readAllByOrder(orderBL.getOrderID());
            HashMap<Integer, Double> itemsPrice = new HashMap<>();
            for (ProductsPriceOrderDto productsPrice : productsPrices) {
                itemsPrice.put(productsPrice.getProductID(), productsPrice.getPrice());
            }
            orderBL.setItemsPrice(itemsPrice);

            // Add order to the orders map
            getOrders().computeIfAbsent(orderBL.getSupplierID(), k -> new ArrayList<>()).add(orderBL);
        }
    }

    private void loadEmployees() {
        List<EmployeeDto> employeeDtos = getEmployeeDao().readAll();
        for (EmployeeDto employeeDto : employeeDtos) {
            EmployeeBL employeeBL = new EmployeeBL(employeeDto);
            getEmployees().put(employeeBL.getEmployeeID(), employeeBL);
        }
    }

    private void loadProducts() {
        List<ProductDto> productDtos = getProductDao().readAll();
        for (ProductDto productDto : productDtos) {
            ProductBL productBL = new ProductBL(productDto);
            getItems().put(productBL.getItemID(), productBL);
        }
    }

    @Override
    public void deleteData() {
        getAgreementDao().deleteAll();
        getOrderDao().deleteAll();
        getProductDao().deleteAll();
        getProductPriceDao().deleteAll();
        getProductQuantityDiscountDao().deleteAll();
        getProductsPriceOrderDao().deleteAll();
        getProductQuantityOrderDao().deleteAll();
        getSupplierDao().deleteAll();
        getSupplierCompaniesDao().deleteAll();
        getSupplierEmployeeDao().deleteAll();
        getEmployeeDao().deleteAll();

        // Clear in-memory maps
        getDictionarySupplierBL().clear();
        getOrders().clear();
        getEmployees().clear();
        getItems().clear();
    }

}
