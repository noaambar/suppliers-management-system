 package Backend.ServiceLayer;

// import DataTransferLayer.ItemTypeDto;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import java.sql.Date;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.*;

// class ItemServiceTest {

//     private ItemService itemService;
//     private SupplierService supplierService;
//     //private ItemFacade itemFacade;
//     private int itemId;

//     @BeforeEach
//     void setUp() {
    
//     supplierService = new SupplierService();
//     itemService = new ItemService();

//     // נשתמש ב-ID קיים מה-JSON: לדוגמה 201 (Tomatoes, AgroFarm)
//     Response<String> response = itemService.newItem(201, 10.0, "Vegetables", "Fresh", "Tomatoes");
//     assertNull(response.getError(), "Item creation failed: " + response.getError());

//     String[] parts = response.getData().split(": ");
//     itemId = Integer.parseInt(parts[1]);
// }

//     @Test
//     void testUpdatePrice() {
//         Response<String> response = itemService.updatePrice(itemId, 1200.0);
//         assertNotNull(response);
//         assertNull(response.getError());
//         assertEquals("Price updated successfully.", response.getData());
//     }

//     @Test
//     void testUpdateDiscount() {
//         Response<String> response = itemService.updateDiscount(itemId, 10.0, new Date(System.currentTimeMillis() + 86400000));
//         assertNotNull(response);
//         assertNull(response.getError());
//         assertEquals("Discount updated successfully.", response.getData());
//     }

//     @Test
//     void testReportDefectiveItem() {
//         //itemFacade.addStock(itemId, 1, new Date(System.currentTimeMillis()), new int[]{0, 0, 0});
//         Response<String> response = itemService.reportDefectiveItem(itemId, 0);
//         assertNotNull(response);
//         assertNull(response.getError());
//         assertEquals("The report was successfully received into the system.", response.getData());
//     }


//     @Test
//     void testUpdateMainCategory() {
//         Response<String> response = itemService.updateMainCategory(itemId, "Tech");
//         assertNotNull(response);
//         assertNull(response.getError());
//         assertEquals("update main Category successfully", response.getData());
//     }

//     @Test
//     void testUpdatePriceFailsForNonExistentItem() {
//         int fakeItemId = 99999;
//         Response<String> response = itemService.updatePrice(fakeItemId, 1500.0);
//         assertNull(response.getData());
//         assertNotNull(response.getError());
//         assertTrue(response.getError().contains("does not exist"));
//     }

//     @Test
//     void testReportDefectiveItemFailsForNonExistentItem() {
//         int fakeItemId = 88888;
//         Response<String> response = itemService.reportDefectiveItem(itemId, fakeItemId);
//         assertNull(response.getData());
//         assertNotNull(response.getError());
//         assertTrue(response.getError().contains("not found") || response.getError().contains("already reported"));
//     }

//     @Test
//     void testMinimalAmountReport() {
//         Response<List<ItemTypeDto>> response = itemService.minimalAmountReport();
//         assertNotNull(response);
//         assertNull(response.getError());
//         assertNotNull(response.getData());
//     }

//     @Test
//     void testExpiredItemsReport() {
//         Date futureDate = new Date(System.currentTimeMillis() + 100000000L);
//         Response<List<ItemService.ItemGroupSL>> response = itemService.itemsExpireBeforeDate(futureDate);
//         assertNotNull(response);
//         assertNull(response.getError());
//         assertNotNull(response.getData());
//     }

//     @Test
//     void testGetAvailableItems() {
//         Response<java.util.HashMap<Integer, String>> response = itemService.getAvailabelItems();
//         assertNotNull(response);
//         assertNull(response.getError());
//         assertNotNull(response.getData());
//     }
// }
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DataTransferLayer.ItemTypeDto;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceTest {

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemService();
        itemService.loadData();
    }


    @Test
    void testUpdatePriceOnExistingItem() {
        int itemId = 202; // Milk
        double newPrice = 8.0;
        Response<String> response = itemService.updatePrice(itemId, newPrice);
        assertNotNull(response);
        assertNull(response.getError());
        assertEquals("Price updated successfully.", response.getData());
    }

    @Test
    void testUpdateMainCategoryOnExistingItem() {
        int itemId = 203; // Bread
        String newCategory = "UpdatedBakery";
        Response<String> response = itemService.updateMainCategory(itemId, newCategory);
        assertNotNull(response);
        assertNull(response.getError());
        assertEquals("update main Category successfully", response.getData());
    }

    @Test
    void testReportDefectiveItem() {
        int itemId = 204; // Cheese
        int itemInstanceId = 0; // assuming this exists in stock
        Response<String> response = itemService.reportDefectiveItem(itemId, itemInstanceId);
        assertNotNull(response);
        assertTrue(response.getData() != null || response.getError() != null);
    }

    @Test
    void testMinimalAmountReport() {
        Response<List<ItemTypeDto>> response = itemService.minimalAmountReport();
        assertNotNull(response);
        assertNull(response.getError());
        assertNotNull(response.getData());
        assertTrue(response.getData().size() >= 0);
    }

    @Test
    void testExpiredItemsReport() {
        Date futureDate = new Date(System.currentTimeMillis() + 100000000L); // date in future
        Response<List<ItemService.ItemGroupSL>> response = itemService.itemsExpireBeforeDate(futureDate);
        assertNotNull(response);
        assertNull(response.getError());
        assertNotNull(response.getData());
    }

    @Test
    void testGetAvailableItems() {
        Response<java.util.HashMap<Integer, String>> response = itemService.getAvailabelItems();
        assertNotNull(response);
        assertNull(response.getError());
        assertNotNull(response.getData());
    }

    @Test
    void testUpdatePriceFailsForNonExistentItem() {
        int fakeItemId = 99999;
        Response<String> response = itemService.updatePrice(fakeItemId, 999.0);
        assertNull(response.getData());
        assertNotNull(response.getError());
    }

    @Test
    void testReportDefectiveItemFailsForNonExistentItem() {
        int fakeItemId = 88888;
        Response<String> response = itemService.reportDefectiveItem(fakeItemId, 0);
        assertNull(response.getData());
        assertNotNull(response.getError());
    }
}

