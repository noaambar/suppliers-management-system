package Backend.BusinessLayer;

import java.util.*;
import java.util.stream.Collectors;

public class SupplierFacade {
    SuppliersRepositoryDB suppliersRepository;
    private Integer orderCount;
    private static SupplierFacade instance = null;

    // Public static method to access the singleton instance
    public static SupplierFacade getInstance() {
        if (instance == null) {
            instance = new SupplierFacade();
        }
        return instance;
    }

    private SupplierFacade() {
        this.suppliersRepository = new SuppliersRepositoryDB();
        this.orderCount = 0;
    }

    public void loadData() {
        suppliersRepository.loadData();
        calculateIdOrder();
    }

    public void deleteData() {
        suppliersRepository.deleteData();
    }

    private void calculateIdOrder() {
        Map<Integer, ArrayList<OrderBL>> orders = suppliersRepository.getOrders();
        for (ArrayList<OrderBL> orderList : orders.values()) {
            if (!orderList.isEmpty()) {
                OrderBL lastOrder = orderList.get(orderList.size() - 1);
                orderCount = Math.max(orderCount, lastOrder.getOrderID() + 1);
            }
        }
    }

    public void createSupplier(String supplierName, Integer supplierID, String address, Integer bankAccount,
            ArrayList<Integer> employeesID,
            Boolean isTransport, Boolean isDays, String[] days, ArrayList<String> componies) {

        Map<Integer, SupplierBL> dictionarySupplierBL = suppliersRepository.getDictionarySupplierBL();
        if (dictionarySupplierBL.get(supplierID) != null) {
            throw new RuntimeException("Supplier ID is already exists");
        }
        SupplierBL supplier = new SupplierBL(supplierName, supplierID, address, bankAccount,
                employeesID, isTransport, isDays, days, componies);
        addSupplier(supplier);
    }

    public void addSupplier(SupplierBL supplier) {
        suppliersRepository.newSupplier(supplier);

    }

    public void addEmployee(Integer supplierID, Integer employeeID, String employeeName, String phoneNumber) {
        SupplierBL supplier = getSupplier(supplierID);

        if (supplier == null) {
            throw new RuntimeException("Supplier ID doesn't exist");
        }

        if (employeeExists(employeeID)) {
            supplier.addEmployee(employeeID);
            suppliersRepository.newSupplierEmployee(supplierID, employeeID);
        } else {
            EmployeeBL em = new EmployeeBL(employeeName, employeeID, phoneNumber);
            supplier.addEmployee(employeeID);
            suppliersRepository.newEmployee(em);
            suppliersRepository.newSupplierEmployee(supplierID, employeeID);

        }
    }

    public void addEmployeeStart(Integer employeeID, String employeeName, String phoneNumber) {
        EmployeeBL em = new EmployeeBL(employeeName, employeeID, phoneNumber);
        suppliersRepository.newEmployee(em);

    }

    public Integer createOrder(Integer supplierID, Integer supplierAgreementID, Map<Integer, Integer> itemsQuantity,
            String branch) {
        Map<Integer, SupplierBL> dictionarySupplierBL = suppliersRepository.getDictionarySupplierBL();
        Map<Integer, ArrayList<OrderBL>> orders = suppliersRepository.getOrders();

        if (dictionarySupplierBL.get(supplierID).validOrder(supplierAgreementID, itemsQuantity)) {

            OrderBL order = new OrderBL(orderCount, supplierID, itemsQuantity,
                    getPhoneNumber(dictionarySupplierBL.get(supplierID).getPhoneNumber()));
            if (!orders.containsKey(supplierID)) {
                orders.put(supplierID, new ArrayList<>());
            }
            orders.get(supplierID).add(order);
            order.setItemsPrice(dictionarySupplierBL.get(supplierID).setPrices(supplierAgreementID, itemsQuantity));
            suppliersRepository.newOrder(order);
            orderCount++;
        }
        return orderCount - 1;
    }

    public void removeSupplier(Integer supplierID) {
        Map<Integer, ArrayList<OrderBL>> orders = suppliersRepository.getOrders();
        ArrayList<OrderBL> orderSupplier = orders.get(supplierID);
        for (OrderBL orderBL : orderSupplier) {
            if (orderBL.getStatus().equals("in progress"))
                throw new RuntimeException("cannot remove supplier beacus he have order/s in progress");
        }
        suppliersRepository.removeSupplier(supplierID);

    }

    public boolean supplierExists(Integer supplierID) {
        Map<Integer, SupplierBL> dictionarySupplierBL = suppliersRepository.getDictionarySupplierBL();
        return dictionarySupplierBL.containsKey(supplierID);
    }

    public boolean orderDone(Integer supplierId, Integer orderId) {
        Map<Integer, ArrayList<OrderBL>> orders = suppliersRepository.getOrders();
        for (OrderBL order : orders.get(supplierId)) {
            if (order.getOrderID().equals(orderId)) {
                order.setStatus();
                suppliersRepository.updateOrder(order);
                return true;
            }
        }
        throw new RuntimeException("order ID doesn't exist");
    }

    public void removeEmployee(Integer supplierID, Integer employeeID) {
        Map<Integer, SupplierBL> dictionarySupplierBL = suppliersRepository.getDictionarySupplierBL();
        Map<Integer, EmployeeBL> employees = suppliersRepository.getEmployees();
        Map<Integer, ArrayList<OrderBL>> orders = suppliersRepository.getOrders();
        if (dictionarySupplierBL.get(supplierID).numbersOfEmployees() == 1)
            throw new RuntimeException("cannot remove this employee beacuase supplier dosent have more employees");
        String phoneNumber = "";
        if (employees.containsKey(employeeID)) {
            phoneNumber = employees.get(employeeID).getPhoneNumber();
        } else {
            throw new RuntimeException("employee ID doesn't exist");
        }
        dictionarySupplierBL.get(supplierID).removeEmployee(employeeID);
        suppliersRepository.removeEmployee(employeeID);
        for (OrderBL order : orders.get(supplierID)) {
            if (order.getStatus().equals("done")) {
                if (order.getPhonenumber().equals(phoneNumber)) {
                    order.setPhoneNumber(getPhoneNumber(dictionarySupplierBL.get(supplierID).getPhoneNumber()));
                    suppliersRepository.updateOrder(order);

                }
            }
        }
    }

    public String getCatalogicNumber(Integer supplierID) {
        Map<Integer, SupplierBL> dictionarySupplierBL = suppliersRepository.getDictionarySupplierBL();
        Set<Integer> numbers = dictionarySupplierBL.get(supplierID).getCatalogicNumber();
        return numbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("\n"));
    }

    public void addDiscount(Integer supplierID, Integer supplierAgreementID, Integer itemID, Integer count,
            Integer discount) {
        SupplierBL supplier = getSupplier(supplierID);
        if (!itemExists(itemID)) {
            throw new RuntimeException("Item is not exists, cannot add discount");
        }
        if (!supplier.getAgreement(supplierAgreementID).getItemPrice().containsKey(itemID)) {
            throw new RuntimeException("Item not in agreement, cannot add discount");
        }
        supplier.addDiscount(supplierAgreementID, itemID, count, discount);
        suppliersRepository.newProductQuantityDiscount(supplierID, supplierAgreementID, itemID, count, discount);
    }

    public Integer createAgreement(Integer supplierID, String paymentMethod, Map<Integer, Double> itemPriceId,
            String paymentTime) {
        SupplierBL supplier = getSupplier(supplierID);
        if (supplier == null) {
            throw new RuntimeException("Supplier ID doesn't exist");
        }
        Map<ProductBL, Double> itemPrices = new HashMap<>();

        for (Integer key : itemPriceId.keySet()) {
            itemPrices.put(getProductById(key), itemPriceId.get(key));
        }
        Integer supplierAgreementID = supplier.createAgreement(paymentMethod, itemPrices, paymentTime);
        suppliersRepository.newAgreement(getSupplier(supplierID).getAgreement(supplierAgreementID));
        suppliersRepository.updateSupplier(suppliersRepository.getDictionarySupplierBL().get(supplierID));
        return supplierAgreementID;

    }

    public void addProduct(Integer itemID, String itemName, String unit, String producer, Integer amount) {
        Map<Integer, ProductBL> items = suppliersRepository.getItems();
        ProductBL item = new ProductBL(itemID, itemName, unit, producer, amount);
        if (items.containsKey(itemID)) {
            throw new RuntimeException("this item already exist");
        }
        suppliersRepository.newProduct(item);
    }

    public boolean employeeExists(Integer employeeID) {
        return (suppliersRepository.getEmployees().containsKey(employeeID));
    }

    public String getItemsNames(Integer supplierID) {
        Set<String> s = getSupplierNames(getSupplier(supplierID).getItemsNames());
        return String.join(", ", s);

    }

    public boolean itemExists(Integer itemID) {
        Collection<ProductBL> items = suppliersRepository.getItems().values();
        for (ProductBL item : items) {
            if (item.getItemID().equals(itemID)) {
                return true;
            }
        }
        return false;
    }

    public ProductBL isSupplied(Integer itemID) {
        Map<Integer, ProductBL> items = suppliersRepository.getItems();
        if (items.containsKey(itemID)) {
            return items.get(itemID);
        }
        return null;
    }

    public void removeAgreement(Integer supplierID, Integer AgreementID) {
        Map<Integer, SupplierBL> dictionarySupplierBL = suppliersRepository.getDictionarySupplierBL();
        suppliersRepository.removeAgreement(supplierID, AgreementID);
        dictionarySupplierBL.get(supplierID).removeAgreement(AgreementID);
        suppliersRepository.updateSupplier(suppliersRepository.getDictionarySupplierBL().get(supplierID));
    }

    public void removeProduct(Integer supplierID, Integer AgreementID, Integer product) {
        if (!itemExists(product)) {
            throw new RuntimeException("item ID doesn't exists");
        }
        SupplierBL supplier = getSupplier(supplierID);
        supplier.removeItem(AgreementID, product);
        suppliersRepository.removeProductPrice(supplierID, AgreementID, product);
    }

    public void addItemToSupplier(Integer supplierID, Integer AgreementID, Integer item, Double price) {
        if (!itemExists(item)) {
            throw new RuntimeException("item ID doesn't exists");
        }
        SupplierBL supplier = getSupplier(supplierID);
        supplier.addItem(AgreementID, item, price);
        suppliersRepository.updateAgreement(supplier.getAgreement(AgreementID));
        suppliersRepository.newProductPrice(supplierID, AgreementID, item, price);
    }

    public void removeDiscount(Integer supplierID, Integer AgreementID, Integer item, Integer count, Integer discount) {
        if (!itemExists(item)) {
            throw new RuntimeException("item ID doesn't exists");
        }
        SupplierBL supplier = getSupplier(supplierID);
        // supplier.removeDiscount(AgreementID, item, count, discount);
        // suppliersRepository.updateAgreement(supplier.getAgreement(AgreementID));
        suppliersRepository.removeProductQuantityDiscount(supplierID, AgreementID, item, count, discount);
    }

    public void setPaymentTime(Integer supplierID, Integer AgreementID, String paymentTime) {
        suppliersRepository.getDictionarySupplierBL().get(supplierID).setPaymentTime(AgreementID, paymentTime);
        suppliersRepository.updateAgreement(
                suppliersRepository.getDictionarySupplierBL().get(supplierID).getAgreement(AgreementID));
    }

    public void setPaymentMethod(Integer supplierID, Integer AgreementID, String paymentMethod) {
        suppliersRepository.getDictionarySupplierBL().get(supplierID).setPaymentMethod(AgreementID, paymentMethod);
        suppliersRepository.updateAgreement(
                suppliersRepository.getDictionarySupplierBL().get(supplierID).getAgreement(AgreementID));

    }

    public Map<Integer, Map<Integer, Map<Integer, Double>>> createOrderToAllSuppliers(int day,
            Map<Integer, Integer> products) {
        Map<Integer, Map<Integer, Map<Integer, Double>>> dayProductsQuantityPrices = new HashMap<>();
        Map<Integer, Map<Integer, Double>> suppliersProductPrices = new HashMap<>();
        Map<Integer, Map<Integer, Double>> finalSuppliersProductPrices = new HashMap<>();
        Map<Integer, Double> ip;
        for (SupplierBL supplier : suppliersRepository.getDictionarySupplierBL().values()) {
            suppliersProductPrices.put(supplier.getSupplierID(), new HashMap<>());
            suppliersProductPrices.put(supplier.getSupplierID(), supplier.getPrices(products));
            finalSuppliersProductPrices.put(supplier.getSupplierID(), new HashMap<>());
        }
        for (Integer product : products.keySet()) {
            Double bestprice = Double.MAX_VALUE;
            Integer supplierID = 0;

            for (Integer supplierid : suppliersProductPrices.keySet()) {
                if (suppliersProductPrices.get(supplierid).get(product) != null) {
                    if (suppliersProductPrices.get(supplierid).get(product) < bestprice) {
                        bestprice = suppliersProductPrices.get(supplierid).get(product);
                        supplierID = supplierid;
                    }
                }
            }
            if (bestprice != Double.MAX_VALUE) {
                ip = finalSuppliersProductPrices.get(supplierID);
                ip.put(product, bestprice);
                finalSuppliersProductPrices.replace(supplierID, ip);
            }
        }
        for (Integer supplierID : finalSuppliersProductPrices.keySet()) {
            if (!finalSuppliersProductPrices.get(supplierID).isEmpty()) {
                Map<Integer, Integer> productQuantity = new HashMap<>();
                Map<Integer, Double> quantityPrice = new HashMap<>();
                Map<Integer, Map<Integer, Double>> itemQuantityPrice;
                Integer delivaryDay = suppliersRepository.getDictionarySupplierBL().get(supplierID).getDay(day);
                if (dayProductsQuantityPrices.containsKey(delivaryDay)) {
                    itemQuantityPrice = dayProductsQuantityPrices
                            .get(suppliersRepository.getDictionarySupplierBL().get(supplierID).getDay(day));
                } else {
                    itemQuantityPrice = new HashMap<>();
                }
                for (Integer product : finalSuppliersProductPrices.get(supplierID).keySet()) {
                    productQuantity.put(product, products.get(product));
                    quantityPrice.put(products.get(product), finalSuppliersProductPrices.get(supplierID).get(product));
                    itemQuantityPrice.put(product, quantityPrice);
                }
                dayProductsQuantityPrices.put(suppliersRepository.getDictionarySupplierBL().get(supplierID).getDay(day),
                        itemQuantityPrice);
                OrderBL order = new OrderBL(orderCount, supplierID, productQuantity,
                        getPhoneNumber(suppliersRepository.getDictionarySupplierBL().get(supplierID).getPhoneNumber()));
                suppliersRepository.newOrder(order);
                orderCount++;
            }

        }
        return dayProductsQuantityPrices;
    }

    public Map<Integer, Map<Integer, Double>> periodOrder(int today,
            Map<Integer/* ID PRODUCT */, Integer/* AMOUNT */> products) {
        Map<ProductBL, Integer> itemQ = new HashMap<>();
        Map<Integer, SupplierBL> dictionarySupplierBL = suppliersRepository.getDictionarySupplierBL();
        Map<Integer, ArrayList<OrderBL>> orders = suppliersRepository.getOrders();
        Map<Integer, Map<Integer, Double>> finalPrices = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> supplierOrder = new HashMap<>();

        for (Integer key : products.keySet()) {
            itemQ.put(getProductById(key), products.get(key));
        }

        for (ProductBL product : itemQ.keySet()) {
            Map<Integer, Double> qp = new HashMap<>();
            Map<Integer, Double> priceToCurrentP;
            Map<Integer, Integer> currentProductToSupplier = new HashMap<>();// product, quantity
            currentProductToSupplier.put(product.getItemID(), itemQ.get(product));
            for (SupplierBL supplier : dictionarySupplierBL.values()) {
                if (supplier.getDay(today + 1) == (today + 1)) {
                    priceToCurrentP = supplier.getPrices(currentProductToSupplier);
                    if (!priceToCurrentP.isEmpty()) {
                        Map<Integer, Integer> iq = currentProductToSupplier;
                        qp.put(itemQ.get(product), priceToCurrentP.get(product.getItemID()));
                        finalPrices.put(product.getItemID(), qp);
                        iq.put(product.getItemID(), itemQ.get(product));
                        supplierOrder.put(supplier.getSupplierID(), iq);
                        break;
                    }

                }
            }
        }
        for (Integer supplierID : supplierOrder.keySet()) {
            OrderBL order = new OrderBL(orderCount, supplierID, supplierOrder.get(supplierID),
                    getPhoneNumber(dictionarySupplierBL.get(supplierID).getPhoneNumber()));
            suppliersRepository.newOrder(order);
            orderCount++;
        }
        return finalPrices;
    }

    public SupplierBL getSupplier(Integer supplierID) {
        Map<Integer, SupplierBL> dictionarySupplierBL = suppliersRepository.getDictionarySupplierBL();
        // or throw an exception if preferred
        return dictionarySupplierBL.getOrDefault(supplierID, null);
    }

    public ProductBL getProductById(Integer itemID) {
        Map<Integer, ProductBL> items = suppliersRepository.getItems();
        if (items.containsKey(itemID)) {
            return items.get(itemID);
        }
        return null; // or throw an exception if preferred
    }

    public String getPhoneNumber(Integer employeeId) {
        Map<Integer, EmployeeBL> employees = suppliersRepository.getEmployees();
        if (employees.containsKey(employeeId)) {
            return employees.get(employeeId).getPhoneNumber();
        }
        throw new RuntimeException("employee ID doesn't exist");
    }

    public Set<String> getSupplierNames(Set<Integer> items) {
        Map<Integer, ProductBL> dictionaryItems = suppliersRepository.getItems();

        return items.stream()
                .map(dictionaryItems::get)
                .filter(Objects::nonNull)
                .map(ProductBL::getItemName)
                .collect(Collectors.toSet());
    }

    public boolean hasSupplier(int productId) {
        Map<Integer, SupplierBL> dictionarySupplierBL = suppliersRepository.getDictionarySupplierBL();
        for (SupplierBL supplier : dictionarySupplierBL.values()) {
            List<SupplierAgreementBL> agreementBLS = supplier.getSupplierAgreement();
            for (SupplierAgreementBL agreementBL : agreementBLS) {
                for (Integer productid : agreementBL.getItemPrice().keySet()) {
                    if (productid == productId)
                        return true;
                }
            }
        }
        return false;

    }
}
