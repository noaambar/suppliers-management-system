package Backend.BusinessLayer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Backend.DataAccessLayer.ItemInstance.ItemInstanceDao;
import Backend.DataAccessLayer.ItemInstance.ItemInstanceDaoSql;
import Backend.DataAccessLayer.ItemType.ItemTypeDao;
import Backend.DataAccessLayer.ItemType.ItemTypeDaoSql;
import Backend.DataAccessLayer.OutgoingOrder.OutgoingOrderDao;
import Backend.DataAccessLayer.OutgoingOrder.OutgoingOrderDaoSql;
import Backend.DataAccessLayer.OutgoingOrder_ItemType.OutgoingOrder_ItemTypeDao;
import Backend.DataAccessLayer.OutgoingOrder_ItemType.OutgoingOrder_ItemTypeDaoSql;
import Backend.DataAccessLayer.FutureOrder.FutureOrderDao;
import Backend.DataAccessLayer.FutureOrder.FutureOrderSql;
import DataTransferLayer.FutureOrderDto;
import DataTransferLayer.ItemInstanceDto;
import DataTransferLayer.ItemTypeDto;
import DataTransferLayer.OutgoingOrderDto;
import DataTransferLayer.OutgoingOrder_ItemTypeDto;

public class ItemRepositoryDB implements ItemRepository {

    private ItemInstanceDao itemInstanceDao;
    private ItemTypeDao itemTypeDao;
    private OutgoingOrderDao outgoingOrderDao;
    private OutgoingOrder_ItemTypeDao outgoingOrder_itemTypeDao;
    private FutureOrderDao futureOrderDao;

    private final HashMap<String, HashMap<String, HashMap<String, HashMap<Integer, ItemTypeBL>>>> categories;
    private final HashMap<Integer, ItemTypeBL> itemTypeMap;
    private final HashMap<Integer, OutgoingOrderBL> periodicOrders;
    private final Map<Integer, Map<Integer, AbstractMap.SimpleEntry<Integer,Double>>> futureOrders;

    public ItemRepositoryDB() {
        this.itemInstanceDao = new ItemInstanceDaoSql();
        this.itemTypeDao = new ItemTypeDaoSql();
        this.outgoingOrderDao = new OutgoingOrderDaoSql();
        this.outgoingOrder_itemTypeDao = new OutgoingOrder_ItemTypeDaoSql();
        this.futureOrderDao = new FutureOrderSql();
        this.categories = new HashMap<>();
        this.itemTypeMap = new HashMap<>();
        this.periodicOrders = new HashMap<>();
        this.futureOrders = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            this.futureOrders.put(i, new HashMap<>());
        }
    }

    private ItemInstanceDao getItemInstanceDao() {
        return itemInstanceDao;
    }
    private ItemTypeDao getItemTypeDao() {
        return itemTypeDao;
    }
    private OutgoingOrderDao getOutgoingOrderDao() {
        return outgoingOrderDao;
    }
    private OutgoingOrder_ItemTypeDao getOutgoingOrder_itemTypeDao() {
        return outgoingOrder_itemTypeDao;
    }
    private FutureOrderDao getFutureOrderDao() {
        return futureOrderDao;
    }
    public Map<Integer, Map<Integer, AbstractMap.SimpleEntry<Integer,Double>>> getFutureOrders() {
        return futureOrders;
    }
    public HashMap<String, HashMap<String, HashMap<String, HashMap<Integer, ItemTypeBL>>>> getCategories() {
        return categories;
    }
    public HashMap<Integer, ItemTypeBL> getItemTypeMap() {
        return itemTypeMap;
    }
    public HashMap<Integer, OutgoingOrderBL> getPeriodicOrders() {
        return periodicOrders;
    }
    public void newItemType(ItemTypeBL itemType) {
        getItemTypeDao().create(itemType.toDto());
    }
    public void updateItemType(ItemTypeBL item) {
        getItemTypeDao().update(item.toDto());
    }
    public void newItemInstances(int itemTypeId, List<ItemInstanceBL> stock) {
        for (ItemInstanceBL item : stock) {
            getItemInstanceDao().create(item.toDto(itemTypeId));
        }
    }
    public void removeItemInstances(int itemTypeId, List<Integer> sellItems) {
        for (int itemId : sellItems) {
            getItemInstanceDao().delete(itemTypeId, itemId);
        }
    }
    public void removeItemType(int itemTypeId) {
        getItemTypeDao().delete(itemTypeId);
    }
    public void updateItemInstance(int itemTypeId, ItemInstanceBL item) {
        getItemInstanceDao().update(item.toDto(itemTypeId));
    }
    public void updateItemInstances(int itemTypeId, List<ItemInstanceBL> moveItems) {
        for (ItemInstanceBL item : moveItems) {
            getItemInstanceDao().update(item.toDto(itemTypeId));
        }
    }
    public void newOrder(OutgoingOrderBL order) {
        getOutgoingOrderDao().create(order.toDto());
        for (Entry<Integer, Integer> entry : order.getItemsQuantity().entrySet()) {
            int itemId = entry.getKey();
            int quantity = entry.getValue();
            newOrder_ItemType(order.getOrderID(), itemId, quantity);
        }
    }
    public void removeOrder(int orderId) {
        getOutgoingOrderDao().delete(orderId);
    }
    public void newOrder_ItemType(int orderId, int itemTypeId, int quantity) {
        getOutgoingOrder_itemTypeDao().create(getOutgoingOrder_ItemsDto(orderId, itemTypeId, quantity));
    }
        public void removeOrder_Items(int orderId, int itemTypeId) {
        getOutgoingOrder_itemTypeDao().delete(orderId, itemTypeId);
    }
    public void updateOrder_ItemType(int orderId, int itemTypeId, int quantity) {
        getOutgoingOrder_itemTypeDao().update(getOutgoingOrder_ItemsDto(orderId, itemTypeId, quantity));
    }

    private OutgoingOrder_ItemTypeDto getOutgoingOrder_ItemsDto(int orderID, int itemTypeId, int quantity) {
        return new OutgoingOrder_ItemTypeDto(
                orderID,
                itemTypeId,
                quantity
        );
    }
    
    public void newFutureOrder(FutureOrderDto futureOrderDto) {
        getFutureOrderDao().create(futureOrderDto);
    }

    public void updateFutureOrder(FutureOrderDto futureOrderDto) {
        getFutureOrderDao().update(futureOrderDto);
    }

    public void deleteFutureOrders(int day) {
        getFutureOrderDao().delete(day);
    }

    public void loadData() {
        List<ItemTypeDto> loadItems = getItemTypeDao().readAll();
        for (ItemTypeDto items : loadItems) {
            ItemTypeBL itemsBL = new ItemTypeBL(items);
            List<ItemInstanceDto> loadItem = getItemInstanceDao().readItems(items.getId());
            List<ItemInstanceBL> itemList = new ArrayList<>();
            for (ItemInstanceDto item : loadItem) {
                ItemInstanceBL itemBL = new ItemInstanceBL(item);
                if (itemBL.checkExpiration()) {
                    getItemInstanceDao().delete(items.getId(), item.getId());
                } else {
                    itemList.add(itemBL);
                }
            }
            itemsBL.setItemList(itemList);
            itemTypeMap.put(items.getId(), itemsBL);
            categories
                .computeIfAbsent(items.getMainCategory(), k -> new HashMap<>())
                .computeIfAbsent(items.getSubCategory(), k -> new HashMap<>())
                .computeIfAbsent(items.getSubSubCategory(), k -> new HashMap<>())
                .put(items.getId(), itemsBL);
        }

        List<OutgoingOrderDto> orders = getOutgoingOrderDao().readAll();
        for (OutgoingOrderDto order : orders) {
            OutgoingOrderBL orderBL = new OutgoingOrderBL(order);
            List<OutgoingOrder_ItemTypeDto> orderItems = getOutgoingOrder_itemTypeDao().read(order.getOrderID());
            for (OutgoingOrder_ItemTypeDto item : orderItems) {
                orderBL.setItemTypeQuantity(item.getItemsID(), item.getQuantity());
            }
            periodicOrders.put(order.getOrderID(), orderBL);
        }

        List<FutureOrderDto> futureOrdersList = getFutureOrderDao().readAll();
        for (FutureOrderDto futureOrder : futureOrdersList) {
            int day = futureOrder.getDay();
            int itemTypeId = futureOrder.getItemTypeId();
            int quantity = futureOrder.getQuantity();
            double cost = futureOrder.getCost();

            // עדכון לפי המבנה החדש
            this.futureOrders

                .computeIfAbsent(day, k -> new HashMap<>())
                .put(itemTypeId, new AbstractMap.SimpleEntry<>(quantity, cost));

        }
    }

    public void deleteData() {
        getItemTypeDao().deleteAll();
        getItemInstanceDao().deleteAll();
        getOutgoingOrderDao().deleteAll();
        getOutgoingOrder_itemTypeDao().deleteAll();
        getFutureOrderDao().deleteAll();
        categories.clear();
        itemTypeMap.clear();
        periodicOrders.clear();
        futureOrders.clear();
    }   
    
}
