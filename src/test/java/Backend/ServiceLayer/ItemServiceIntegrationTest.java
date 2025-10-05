package Backend.ServiceLayer;

import DataTransferLayer.ItemTypeDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ItemServiceIntegrationTest {

    private ItemService itemService;

    @BeforeEach
    public void setUp() {
        itemService = new ItemService();
        itemService.loadData();
    }




    @Test
    public void testAvailabilityOrdersTriggerCorrectly() {
        int itemTypeId = 205; // Apples

        Response<String> availabilityOrder = itemService.sendAvailablityOrders();
        assertFalse(availabilityOrder.isError());

        itemService.nextDay();

        Response<HashMap<Integer, String>> availableItems = itemService.getAvailabelItems();
        assertFalse(availableItems.isError());
        
    }

    @Test
    public void testCreatePeriodicOrderAndVerify() {
        int itemTypeId = 206; // Sugar

        HashMap<Integer, Integer> items = new HashMap<>();
        items.put(itemTypeId, 10);
        int today = LocalDate.now().getDayOfMonth();

        Response<String> orderResponse = itemService.createPeriodicOrder(items, today);
        assertFalse(orderResponse.isError());
        assertNotNull(orderResponse.getData());
        assertTrue(orderResponse.getData().contains("The order id:"));
    }

    @Test
    public void testPeriodicOrderFailsForInvalidItem() {
        HashMap<Integer, Integer> items = new HashMap<>();
        items.put(9999, 5); // Invalid item ID

        Response<String> response = itemService.createPeriodicOrder(items, 1);
        assertTrue(response.isError());
        assertNull(response.getData());
        assertTrue(response.getError().contains("does not exist"));
    }

    @Test
    void testCreatePeriodicOrderAndAdvanceDay() {
        int itemTypeId = 207; // Rice

        HashMap<Integer, Integer> items = new HashMap<>();
        items.put(itemTypeId, 15);

        int tomorrow = 2;

        Response<String> orderResponse = itemService.createPeriodicOrder(items, tomorrow);
        assertFalse(orderResponse.isError());
        assertTrue(orderResponse.getData().contains("The order id:"));

        Response<String> nextDayResponse = itemService.nextDay();
        assertFalse(nextDayResponse.isError());

        Response<HashMap<Integer, String>> availableItems = itemService.getAvailabelItems();
        assertFalse(availableItems.isError());
       
    }

    //  @Test
    // public void testStopAndResupplyItem() {
    //     int itemTypeId = 205; // Apples

    //     // Step 1: Stop supplying the item
    //     Response<String> stopResponse = itemService.stopSupplying(itemTypeId);
    //     assertFalse(stopResponse.isError());
    //     assertEquals("Supply stopped successfully.", stopResponse.getData());

    //     // Step 2: Resupply the item
    //     Response<String> resupplyResponse = itemService.resupplyItem(itemTypeId);
    //     assertFalse(resupplyResponse.isError(), resupplyResponse.getError());
    //     assertEquals("Item resupplied successfully.", resupplyResponse.getData());
    // }

    // @Test
    // public void testUpdateSinglePeriodicOrder() {
    //     int itemTypeId = 206; // Sugar

    //     // Step 1: Create periodic order
    //     HashMap<Integer, Integer> items = new HashMap<>();
    //     items.put(itemTypeId, 10);
    //     int today = 1;

    //     Response<String> createResponse = itemService.createPeriodicOrder(items, today);
    //     assertFalse(createResponse.isError(), createResponse.getError());

    //     String[] parts = createResponse.getData().split(": ");
    //     int orderId = Integer.parseInt(parts[1].trim());

    //     // Step 2: Update the order quantity
    //     Response<String> updateResponse = itemService.updatePeriodicOrder(orderId, itemTypeId, 20);
    //     assertFalse(updateResponse.isError(), updateResponse.getError());
    //     assertEquals("Periodic order updated successfully.", updateResponse.getData());
    // }

}
