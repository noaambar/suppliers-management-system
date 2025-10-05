package Backend.BusinessLayer;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DataTransferLayer.FutureOrderDto;

public interface ItemRepository {

    //גטרים
    HashMap<String, HashMap<String, HashMap<String, HashMap<Integer, ItemTypeBL>>>> getCategories();
    HashMap<Integer, ItemTypeBL> getItemTypeMap();
    HashMap<Integer, OutgoingOrderBL> getPeriodicOrders();
    Map<Integer, Map<Integer, AbstractMap.SimpleEntry<Integer,Double>>> getFutureOrders();

    // פריטים
    void newItemInstances(int itemTypeId, List<ItemInstanceBL> stock);
    void removeItemInstances(int itemTypeId, List<Integer> sellItems);
    void updateItemInstance(int itemTypeId, ItemInstanceBL item);
    void updateItemInstances(int itemTypeId, List<ItemInstanceBL> moveItems);
    void removeItemType(int itemTypeId);

    // סוגי פריטים
    void newItemType(ItemTypeBL itemType);
    void updateItemType(ItemTypeBL item);

    // הזמנות תקופתיות
    void newOrder(OutgoingOrderBL order);
    void removeOrder(int orderId);
    void newOrder_ItemType(int orderID, int itemTypeId, int quantity);
    void removeOrder_Items(int orderId, int itemTypeId);
    void updateOrder_ItemType(int orderID, int itemTypeId, int quantity);

    // הזמנות עתידיות
    void newFutureOrder(FutureOrderDto futureOrderDto);
    void updateFutureOrder(FutureOrderDto futureOrderDto);
    void deleteFutureOrders(int day);

    void loadData();
    void deleteData();
}
