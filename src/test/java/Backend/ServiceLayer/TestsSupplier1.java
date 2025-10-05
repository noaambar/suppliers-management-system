package Backend.ServiceLayer;

import Backend.BusinessLayer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestsSupplier1 {

    private SupplierBL supplier;
    private SupplierFacade facade;
    private OrderBL order;
    private Map<ProductBL, Integer> itemsQuantity;

    @BeforeEach
    public void setUp() {
        ArrayList<Integer> employeeBLS = new ArrayList<>();
        employeeBLS.add(202);

        facade = SupplierFacade.getInstance();
        facade.addEmployeeStart(202, "Noa", "054-7788222");
        supplier = new SupplierBL("Test Supplier", 1, "Address", 1234,
                employeeBLS, true, false,
                new String[] { "Monday" }, new ArrayList<>(List.of("Tnuva", "Osem")));
        try {
            facade.addSupplier(supplier);
        } catch (RuntimeException e) {
            System.out.println("Skipping supplier insert: " + e.getMessage());
        }

        Map<ProductBL, Integer> itemPrice = new HashMap<>();
        ProductBL item1 = new ProductBL(1, "Apple", "Kg", "FruitCo", 1);
        ProductBL item2 = new ProductBL(2, "Banana", "Kg", "FruitCo", 1);
        itemPrice.put(item1, 10); // price of Apple is 10
        itemPrice.put(item2, 8); // price of Banana is 8

        Map<ProductBL, Map<Integer, Integer>> quantityDiscountAgreement = new HashMap<>();
        Map<Integer, Integer> appleDiscounts = new HashMap<>();
        appleDiscounts.put(5, 10); // 10% discount for 5 or more apples
        quantityDiscountAgreement.put(item1, appleDiscounts);

        Map<Integer, Integer> bananaDiscounts = new HashMap<>();
        bananaDiscounts.put(3, 20); // 20% discount for 3 or more bananas
        quantityDiscountAgreement.put(item2, bananaDiscounts);

        itemsQuantity = new HashMap<>();
        itemsQuantity.put(item1, 6); // Ordering 6 Apples
        itemsQuantity.put(item2, 4); // Ordering 4 Bananas
        // לתקן איתחול הזמנה
        Map<Integer, Integer> itemIDQuantity = new HashMap<>();
        for (Map.Entry<ProductBL, Integer> entry : itemsQuantity.entrySet()) {
            itemIDQuantity.put(entry.getKey().getItemID(), entry.getValue());
        }

        order = new OrderBL(1, 1, itemIDQuantity, "hifa branch");
    }

    @Test
    public void testAddEmployee() {
        EmployeeBL employeeBL = new EmployeeBL("David", 101, "050-1111111");
        supplier.addEmployee(101);
        assertEquals(2, supplier.getEmployees().size());
        assertEquals(101, supplier.getEmployees().get(1).intValue());
    }

    @Test
    public void testRemoveEmployee() {
        supplier.addEmployee(102);
        supplier.removeEmployee(102);
        assertEquals(1, supplier.getEmployees().size());
    }

    @Test
    public void testCreateAgreementIncrementsCounter() {
        Map<ProductBL, Double> itemPrice = new HashMap<>();
        itemPrice.put(new ProductBL(1, "Sugar", "kg", "Tnuva", 50), Double.parseDouble("10"));
        supplier.createAgreement("Credit", itemPrice, "in advanced");
        supplier.createAgreement("Cash", itemPrice, "in advanced");

        assertEquals(2, supplier.getSupplierAgreement().size());
    }

    @Test
    public void testValidOrderAgreement() {
        ProductBL item = new ProductBL(1, "Sugar", "kg", "Tnuva", 50);
        Map<ProductBL, Double> itemPrice = new HashMap<>();
        itemPrice.put(item, Double.parseDouble("10"));
        supplier.createAgreement("Credit", itemPrice, "in advanced");

        Map<Integer, Integer> orders = new HashMap<>();
        orders.put(1, 5);
        assertTrue(supplier.validOrder(0, orders));
    }

    @Test
    public void testOrderCreation() {
        Map<Integer, Integer> orders = new HashMap<>();
        orders.put(1, 5);
        Map<ProductBL, Double> itemPrice = new HashMap<>();
        itemPrice.put(new ProductBL(1, "Sugar", "kg", "Tnuva", 50), Double.parseDouble("10"));
        System.out.println(supplier.createAgreement("Credit", itemPrice, "in advanced"));
        facade.createOrder(1, 0, orders, "hifa branch");
        assertEquals(1, order.getOrderID());
        assertEquals(1, order.getSupplierID());
        assertNotNull(order.getDate()); // Check if the date is set
    }

    @Test
    public void testSetPrices() {
        Map<Integer, Double> prices = new HashMap<>();
        new ProductBL(1, "Apple", "Kg", "FruitCo", 1);
        new ProductBL(2, "Banana", "Kg", "FruitCo", 1);
        prices.put(1, Double.parseDouble("5"));
        prices.put(2, Double.parseDouble("3"));

        order.SetPrices(prices);
        assertEquals(prices, order.getItemsPrice());
    }

    @Test
    public void testSetStatus() {
        assertEquals("inProgress", order.getStatus());
        order.setStatus();
        assertEquals("done", order.getStatus());
    }

    @Test
    public void testSetPricesWithDiscounts() {
        ProductBL apple = new ProductBL(1, "Apple", "Kg", "FruitCo", 1);

        Map<Integer, Double> itemPrices = new HashMap<>();
        itemPrices.put(1, 10.0);

        SupplierAgreementBL agreement = new SupplierAgreementBL(1, "Credit", 1, itemPrices, "in advanced");

        agreement.addDiscount(1, 5, 10);

        Map<Integer, Integer> orderQuantities = new HashMap<>();
        orderQuantities.put(1, 6);

        Map<Integer, Double> expectedPrices = new HashMap<>();
        expectedPrices.put(1, 55.0);

        Map<Integer, Double> actualPrices = agreement.setPrices(orderQuantities);

        assertEquals(expectedPrices, actualPrices);
    }

    @Test
    void testCreateSupplierSuccessfullyAddsSupplier() {
        ArrayList<String> payments = new ArrayList<>(List.of("Credit", "Cash"));
        ArrayList<String> companies = new ArrayList<>(List.of("Coca-Cola", "Pepsi"));
        ArrayList<Integer> employeeIds = new ArrayList<>();

        facade.addEmployeeStart(101, "John Doe", "0501234567");
        facade.addEmployeeStart(102, "Jane Smith", "0507654321");

        employeeIds.add(101);
        employeeIds.add(102);

        facade.createSupplier("CoolSupplier", 2, "Main St", 123456,
                employeeIds, true, true,
                new String[] { "Sunday", "Wednesday" }, companies);

        assertTrue(facade.supplierExists(2));
    }

    @Test
    void testRemoveSupplierDeletesSupplier() {
        ArrayList<String> payments = new ArrayList<>(List.of("Credit", "Cash"));
        ArrayList<String> companies = new ArrayList<>(List.of("Coca-Cola", "Pepsi"));
        ArrayList<Integer> employeeIds = new ArrayList<>();

        facade.addEmployeeStart(103, "John Doe", "0501234567");
        facade.addEmployeeStart(104, "Jane Smith", "0507654321");

        employeeIds.add(103);
        employeeIds.add(104);

        facade.createSupplier("CoolSupplier", 3, "Main St", 123456,
                employeeIds, true, true,
                new String[] { "Sunday", "Wednesday" }, companies);
        assertTrue(facade.supplierExists(3));
        facade.removeSupplier(3);
        assertFalse(facade.supplierExists(3));
    }

    @Test
    void testCreateOrderToAllSuppliers1() {
        supplier.addCompamy("FruitCo");
        facade.addSupplier(supplier);
        ArrayList<Integer> employeeBLS = new ArrayList<>();
        employeeBLS.add(203);
        facade.addEmployeeStart(203, "reut", "054-7788223");
        facade.createSupplier("Test Supplier2", 2, "Address", 1234,
                employeeBLS, true, false,
                new String[] {}, new ArrayList<>(List.of("Tnuva", "Osem", "FruitCo")));
        Map<Integer, Double> prices = new HashMap<>();
        facade.addProduct(1, "Apple", "Kg", "FruitCo", 1);
        facade.addProduct(2, "Banana", "Kg", "FruitCo", 1);
        prices.put(1, Double.parseDouble("10"));
        prices.put(2, Double.parseDouble("3"));
        int agreementID = facade.createAgreement(1, "cash", prices, "in advanced");
        facade.addDiscount(1, agreementID, 1, 10, 10);
        facade.addDiscount(1, agreementID, 2, 10, 10);
        int agreementID2 = facade.createAgreement(2, "cash", prices, "in advanced");
        facade.addDiscount(1, agreementID2, 1, 5, 5);
        facade.addDiscount(1, agreementID2, 2, 5, 5);

        Map<Integer, Integer> itemQ = new HashMap<>();
        itemQ.put(1, 10);
        itemQ.put(2, 5);
        Map<Integer, Map<Integer, Map<Integer, Double>>> expected = new HashMap<>();
        Map<Integer, Map<Integer, Double>> iqp1 = new HashMap<>();
        Map<Integer, Map<Integer, Double>> iqp2 = new HashMap<>();
        Map<Integer, Double> qp1 = new HashMap<>();
        Map<Integer, Double> qp2 = new HashMap<>();
        qp1.put(10, 9.0);
        qp2.put(5, 14.25);
        iqp1.put(1, qp1);
        iqp2.put(2, qp2);
        expected.put(2, iqp1);
        expected.put(1, iqp2);
        Map<Integer, Map<Integer, Map<Integer, Double>>> actualRecipt = facade.createOrderToAllSuppliers(1, itemQ);
        assertEquals(expected, actualRecipt);
    }

    @Test
    void testCreateOrderToAllSuppliers2() {
        facade.addProduct(3, "Avocado", "Kg", "FruitCo", 1);
        Map<Integer, Integer> itemQ = new HashMap<>();
        itemQ.put(1, 10);
        itemQ.put(3, 5);
        Map<Integer, Map<Integer, Map<Integer, Double>>> expected = new HashMap<>();
        Map<Integer, Map<Integer, Double>> iqp1 = new HashMap<>();
        Map<Integer, Map<Integer, Double>> iqp2 = new HashMap<>();
        Map<Integer, Double> qp1 = new HashMap<>();
        qp1.put(10, 9.0);
        iqp1.put(1, qp1);
        expected.put(2, iqp1);
        expected.put(1, iqp2);
        Map<Integer, Map<Integer, Map<Integer, Double>>> actualRecipt = facade.createOrderToAllSuppliers(1, itemQ);
        assertEquals(expected, actualRecipt);
    }

    @Test
    void testCreateOrderToAllSuppliers3() {
        facade.addProduct(4, "Lemon", "Kg", "FruitCo", 1);
        Map<Integer, Integer> itemQ = new HashMap<>();
        itemQ.put(4, 5);
        Map<Integer, Map<Integer, Map<Integer, Double>>> expected = new HashMap<>();
        Map<Integer, Map<Integer, Map<Integer, Double>>> actualRecipt = facade.createOrderToAllSuppliers(1, itemQ);
        assertEquals(expected, actualRecipt);
    }

    @Test
    void testOrderPeriod1() {
        Map<Integer, Integer> itemQ = new HashMap<>();
        itemQ.put(1, 10);
        itemQ.put(2, 5);
        Map<Integer, Map<Integer, Double>> iqp = new HashMap<>();
        Map<Integer, Double> qp1 = new HashMap<>();
        Map<Integer, Double> qp2 = new HashMap<>();
        qp1.put(10, 9.0);
        qp2.put(5, 13.5);
        iqp.put(1, qp1);
        iqp.put(2, qp2);
        Map<Integer, Map<Integer, Map<Integer, Double>>> actualRecipt = facade.createOrderToAllSuppliers(1, itemQ);
        assertEquals(iqp, actualRecipt);

    }

    @Test
    void testOrderPeriod2() {
        ArrayList<Integer> employeeBLS = new ArrayList<>();
        employeeBLS.add(203);
        facade.createSupplier("Test Supplier3", 3, "Address", 1234,
                employeeBLS, true, false,
                new String[] { "Sunday" }, new ArrayList<>(List.of("Tnuva", "Osem", "FruitCo")));
        Map<Integer, Double> prices = new HashMap<>();
        facade.addProduct(1, "Apple", "Kg", "FruitCo", 1);
        facade.addProduct(2, "Banana", "Kg", "FruitCo", 1);
        prices.put(1, Double.parseDouble("10"));
        prices.put(2, Double.parseDouble("3"));
        prices.put(3, Double.parseDouble("10"));
        int agreementID3 = facade.createAgreement(1, "cash", prices, "in advanced");
        facade.addDiscount(3, agreementID3, 3, 10, 10);
        Map<Integer, Integer> itemQ = new HashMap<>();
        itemQ.put(3, 10);
        Map<Integer, Map<Integer, Double>> iqp = new HashMap<>();
        Map<Integer, Double> qp1 = new HashMap<>();
        Map<Integer, Double> qp2 = new HashMap<>();
        qp1.put(10, 9.0);
        iqp.put(3, qp1);
        Map<Integer, Map<Integer, Map<Integer, Double>>> actualRecipt = facade.createOrderToAllSuppliers(0, itemQ);
        assertEquals(iqp, actualRecipt);

    }
}
