package Backend.ServiceLayer;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Backend.BusinessLayer.ItemFacade;
import DataTransferLayer.ItemInstanceDto;
import DataTransferLayer.ItemTypeDto;

public class ItemService {

    private ItemFacade iF;

    ItemService() {
        iF = ItemFacade.getInstance();
    }

    public Response<String> loadData() {
        try {
            iF.loadData();
            Response<String> res = new Response<String>("data loaded successfully", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }

    public Response<String> deleteData() {
        try {
            iF.deleteData();
            Response<String> res = new Response<String>("data deleted successfully", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }

    public Response<String> nextDay() {
        try {
            int day = iF.nextDay();
            Response<String> res = new Response<String>("The day passed successfully." + " Today is: " + day, null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }

    public Response<String> sendAvailablityOrders() {
        try {
            List<ItemTypeDto> ordered = iF.sendAvailablityOrders();
            String result = "There was no need to order shortages.";
            if(!ordered.isEmpty()){
                result = "New orders sent successfully due to the lack of stock of the next items: " + ordered.toString();
            }
            Response<String> res = new Response<String>(result, null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }

    public Response<String> sendPeriodicOrders() {
        try {
            iF.sendPeriodicOrders();
            Response<String> res = new Response<String>("Periodic orders were sent successfully", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }

    public Response<String> newItem(int itemTypeId, double price, String mainCategory, String subCategory, String subSubCategory) {
        try {
            iF.newItem(itemTypeId, price, mainCategory, subCategory, subSubCategory);
            Response<String> res = new Response<String>("New Item added to the system successfully. A new order was sent for the new item stock", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }

    public Response<String> createPeriodicOrder(HashMap<Integer, Integer> items, int IssueDay) {
        try {
            int id = iF.createPeriodicOrder(items, IssueDay);
            Response<String> res = new Response<String>("The order id: " + id, null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }

    public Response<String> cancelPeriodicOrder(int orderId) {
        try {
            iF.cancelPeriodicOrder(orderId);
            Response<String> res = new Response<String>("Periodic order canceled successfully.", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }

    public Response<String> removeItem(int itemTypeId) {
        try {
            boolean ans = iF.removeItem(itemTypeId);
            if(!ans) {
                return new Response<String>( null, "Item with stock");
            }
            Response<String> res = new Response<String>("Item removed successfully.", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null,e.getMessage());
        }
    }

    public Response<String> moveItems(int itemTypeId, List<Integer> itemID, String []location) {
        try {
            iF.moveItems(itemTypeId, itemID, location);
            Response<String> res = new Response<String>("Items moved successfully.", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null,e.getMessage());
        }
    }

    public Response<String> updatePrice(int itemTypeId, double price) {
        try {
            iF.updatePrice(itemTypeId, price);
            Response<String> res = new Response<String>("Price updated successfully.", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null,e.getMessage());
        }
    }

    public Response<String> updateDiscount(int itemTypeId, double discount, Date expiredDiscount) {

        try {
            iF.updateDiscount(itemTypeId, discount, expiredDiscount);
            Response<String> res = new Response<String>("Discount updated successfully.", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null,e.getMessage());
        }
    }

    public Response<String> updateDiscountPerCategories(String mainCategory, String subCategory, String subSubCategory, double discount, Date expiredDiscount) {
        try {
            iF.updateDiscountPerCategories(mainCategory, subCategory, subSubCategory, discount, expiredDiscount);
            Response<String> res = new Response<String>("Discount updated successfully.", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null,e.getMessage());
        }
    }

    public Response<String> sellItems(int itemTypeId, List<Integer> ids) {
        try {
            boolean newOrder = iF.sellItems(itemTypeId, ids);
            String result = "Items sells successfully.";
            if (newOrder){
                result += " A new order was sent due to a lack of stock of the current item type";
            }
            Response<String> res = new Response<String>(result, null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null,e.getMessage());
        }
    }

    public Response<HashMap<Integer, String>> getAvailabelItems(){
        try {
            HashMap<Integer, String> ids = iF.getAvailableItems();
            Response<HashMap<Integer, String>> res = new Response<HashMap<Integer, String>>(ids, null);
            return res;
        } catch (Exception e) {
            return new Response<HashMap<Integer, String>>(null, e.getMessage());
        }
    }

    public Response<String> reportDefectiveItem(int itemTypeId, int itemInstanceId) {
        try {
             boolean newOrder = iF.reportDefectiveItem(itemTypeId, itemInstanceId);
            String result = "Items sells successfully.";
            if (newOrder){
                result += " A new order was sent due to a lack of stock of the current item type";
            }
            Response<String> res = new Response<String>(result, null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }
  
    public Response<List<ItemGroupSL>> defectiveReport() {
        try {
            HashMap<ItemTypeDto, List<ItemInstanceDto>> reported = iF.defectiveReport();
            List<ItemGroupSL> result = new ArrayList<>();

            for (Map.Entry<ItemTypeDto, List<ItemInstanceDto>> entry : reported.entrySet()) {
                result.add(new ItemGroupSL(entry.getKey(), entry.getValue()));
            }
            return new Response<>(result, null);
        } catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }


    public Response<List<ItemTypeDto>> report(List<String[]> categories) {
        try {
            return new Response<>(iF.report(categories), null);
        } catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }

    public Response<List<ItemGroupSL>> itemsExpireBeforeDate(Date date) {
        try {
            HashMap<ItemTypeDto, List<ItemInstanceDto>> reported = iF.itemsExpireBeforeDate(date);
            List<ItemGroupSL> result = new ArrayList<>();
    
            for (Map.Entry<ItemTypeDto, List<ItemInstanceDto>> entry : reported.entrySet()) {
                result.add(new ItemGroupSL(entry.getKey(), entry.getValue()));
            }
    
            return new Response<>(result, null);
        } catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }

    public Response<List<ItemTypeDto>> minimalAmountReport() {
        try {
            return new Response<>(iF.minimalAmountReport(), null);
        } catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }

    public Response<String> updateMainCategory(int itemsID, String mainCategory) {
        try {
            iF.updateMainCategory(itemsID, mainCategory);
            Response<String> res = new Response<String>("update main Category successfully", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }

    public Response<String> updateSubCategory(int itemsID, String subCategory) {
        try {
            iF.updateSubCategory(itemsID, subCategory);
            Response<String> res = new Response<String>("update Sub Category successfully", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }

    public Response<String> updateSubSubCategory(int itemsID, String subSubCategory) {
        try {
            iF.updateSubSubCategory(itemsID, subSubCategory);
            Response<String> res = new Response<String>("update SubSub Category successfully", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    
    }

    public class ItemGroupSL {
        private ItemTypeDto itemType;
        private List<ItemInstanceDto> itemInstances;
    
        public ItemGroupSL(ItemTypeDto itemType, List<ItemInstanceDto> itemInstances) {
            this.itemType = itemType;
            this.itemInstances = itemInstances;
        }
    
        public ItemTypeDto getItemType() {
            return itemType;
        }
    
        public List<ItemInstanceDto> getInstances() {
            return itemInstances;
        }
    }

    public Response<String> stopSupplying(int itemTypeId) {
        try {
            iF.stopSupplying(itemTypeId);
            Response<String> res = new Response<String>("Supply stopped successfully.", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }

    public Response<String> resupplyItem(int itemTypeId) {
        try {
            iF.resupplyItem(itemTypeId);
            Response<String> res = new Response<String>("Item resupplied successfully.", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }

    public Response<String> updatePeriodicOrder(int orderId, int itemTypeId, int amount) {
        try {
            iF.updateOrder(orderId, itemTypeId, amount);
            Response<String> res = new Response<String>("Periodic order updated successfully.", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }

    public Response<String> updateOrders() {
        try {
            iF.updateOrders();
            Response<String> res = new Response<String>("Periodic Orders updated successfully.", null);
            return res;
        } catch (Exception e) {
            return new Response<String>(null, e.getMessage());
        }
    }

}

