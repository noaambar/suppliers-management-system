package Backend.BusinessLayer;

import java.sql.Date;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import DataTransferLayer.FutureOrderDto;
import DataTransferLayer.ItemInstanceDto;
import DataTransferLayer.ItemTypeDto;

public class ItemFacade {

    private static ItemFacade instance = null;
    private int orderId = 0;
    private ItemRepository itemRepository = new ItemRepositoryDB();
    private SupplierFacade supplierFacade = SupplierFacade.getInstance();
    private int currentDay = 1;

    // Private constructor to prevent external instantiation
    private ItemFacade() {
    }

    // Public static method to access the singleton instance
    public static ItemFacade getInstance() {
        if (instance == null) {
            instance = new ItemFacade();
        }
        return instance;
    }

    public void loadData() {
        itemRepository.loadData();
        calculateOrderId();
        checkExpirationDates();
        updateDailyStock();
    }

    public void deleteData() {
        itemRepository.deleteData();
    }

    public int nextDay() {
        currentDay = (currentDay + 1) % 7;
        checkExpirationDates();
        updateDailyStock();
        updateOrders();
        sendPeriodicOrders();
        return currentDay;
    }

    private void updateDailyStock() {
        Map<Integer, Map<Integer, AbstractMap.SimpleEntry<Integer, Double>>> futureOrders = itemRepository
                .getFutureOrders();
        Map<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();

        if (futureOrders == null || !futureOrders.containsKey(currentDay)) {
            return;
        }

        Map<Integer, AbstractMap.SimpleEntry<Integer, Double>> todayOrders = futureOrders.get(currentDay);
        if (todayOrders == null || todayOrders.isEmpty()) {
            return;
        }

        for (Map.Entry<Integer, AbstractMap.SimpleEntry<Integer, Double>> entry : todayOrders.entrySet()) {
            int itemTypeId = entry.getKey();
            AbstractMap.SimpleEntry<Integer, Double> quantityCost = entry.getValue();

            if (quantityCost == null) {
                continue;
            }

            int quantity = quantityCost.getKey();
            Double cost = quantityCost.getValue();

            ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
            if (itemType == null) {
                continue;
            }

            updateCost(itemTypeId, cost);

            int randomDays = 5 + (int) (Math.random() * 26);
            LocalDate localDate = LocalDate.now().plusDays(randomDays);
            Date date = Date.valueOf(localDate);

            addStock(itemTypeId, quantity, date, new int[] { 1, 0, 0 });
            itemType.setIsOpenOrder(false);
        }

        futureOrders.remove(currentDay);
        itemRepository.deleteFutureOrders(currentDay);
    }

    private void calculateOrderId() {
        HashMap<Integer, OutgoingOrderBL> orders = itemRepository.getPeriodicOrders();
        for (Map.Entry<Integer, OutgoingOrderBL> entry : orders.entrySet()) {
            if (entry.getKey() > orderId) {
                orderId = entry.getKey();
            }
        }
        orderId++;
    }

    public void newItem(int itemTypeId, double price, String mainCategory, String subCategory, String subSubCategory) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        if (itemTypeMap.containsKey(itemTypeId)) {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " already exists.");
        }
        ProductBL product = supplierFacade.isSupplied(itemTypeId);
        if (product == null) {
            throw new IllegalArgumentException(
                    "Item with ID " + itemTypeId + " can't be added because it dosen't have supplier.");
        }
        HashMap<String, HashMap<String, HashMap<String, HashMap<Integer, ItemTypeBL>>>> categories = itemRepository
                .getCategories();
        // Normalize category names (ignore case, preserve spaces)
        String normMain = normalizeCategory(mainCategory);
        String normSub = normalizeCategory(subCategory);
        String normSubSub = normalizeCategory(subSubCategory);

        ItemTypeBL itemType = new ItemTypeBL(itemTypeId, product.getItemName(),
                new AbstractMap.SimpleEntry<>(price, 0.0), product.getProducer(), normMain, normSub, normSubSub);

        itemRepository.newItemType(itemType);
        itemTypeMap.put(itemTypeId, itemType);
        // Ensure the mainCategory, subCategory, and subSubCategory exist in the map
        categories.computeIfAbsent(normMain, k -> new HashMap<>())
                .computeIfAbsent(normSub, k -> new HashMap<>())
                .computeIfAbsent(normSubSub, k -> new HashMap<>())
                .put(itemTypeId, itemType);
        // sendAvailablityOrder(itemTypeId);
    }

    public boolean removeItem(int itemTypeId) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        if (itemTypeMap.containsKey(itemTypeId)) {
            ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
            if (itemType.isOpenOrder()) {
                throw new IllegalArgumentException("Item with ID " + itemTypeId + " is currently in an open order.");
            }
            if (itemType.getItemsList().isEmpty()) {
                itemRepository.removeItemType(itemTypeId);
                itemTypeMap.remove(itemTypeId);
                for (Entry<Integer, OutgoingOrderBL> entry : itemRepository.getPeriodicOrders().entrySet()) {
                    OutgoingOrderBL order = entry.getValue();
                    if (order.getItemsQuantity().containsKey(itemTypeId)) {
                        order.getItemsQuantity().remove(itemTypeId);
                        itemRepository.removeOrder_Items(order.getOrderID(), itemTypeId);
                        if (order.getItemsQuantity().isEmpty()) {
                            itemRepository.getPeriodicOrders().remove(order.getOrderID());
                            itemRepository.removeOrder(order.getOrderID());
                        }
                    }
                }
                removeFromCategories(itemTypeId, itemType.getMainCategory(), itemType.getSubCategory(),
                        itemType.getSubSubCategory());
                return true;
            } else {
                return false;
            }
        } else {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " does not exist.");
        }
    }

    public void stopSupplying(int itemTypeId) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        if (itemTypeMap.containsKey(itemTypeId)) {
            ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
            itemType.setToSupply(false);
            itemRepository.updateItemType(itemType);
            for (Entry<Integer, OutgoingOrderBL> entry : itemRepository.getPeriodicOrders().entrySet()) {
                OutgoingOrderBL order = entry.getValue();
                if (order.getItemsQuantity().containsKey(itemTypeId)) {
                    order.getItemsQuantity().remove(itemTypeId);
                    itemRepository.removeOrder_Items(order.getOrderID(), itemTypeId);
                    if (order.getItemsQuantity().isEmpty()) {
                        itemRepository.getPeriodicOrders().remove(order.getOrderID());
                        itemRepository.removeOrder(order.getOrderID());
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " does not exist.");
        }
    }

    private String normalizeCategory(String category) {
        return category == null ? "" : category.trim().toLowerCase();
    }

    public void updateDiscountPerCategories(String mainCategory, String subCategory, String subSubCategory,
            double discount, Date expiredDiscount) {

        HashMap<String, HashMap<String, HashMap<String, HashMap<Integer, ItemTypeBL>>>> categories = itemRepository
                .getCategories();
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        String keyMain = normalizeCategory(mainCategory);
        String keySub = normalizeCategory(subCategory);
        String keySubSub = normalizeCategory(subSubCategory);
        if (keyMain.isEmpty()) {
            throw new IllegalArgumentException("Main category names cannot be null");
        }
        if (!categories.containsKey(keyMain)) {
            throw new IllegalArgumentException("Main category does not exist");
        }
        if (keySub != null && !keySub.isEmpty() && categories.get(keyMain).get(keySub) == null) {
            throw new IllegalArgumentException("Sub category does not exist");
        }
        if (keySubSub != null && !keySubSub.isEmpty() && categories.get(keyMain).get(keySub).get(keySubSub) == null) {
            throw new IllegalArgumentException("Sub-sub category does not exist");
        }
        if (keySub.isEmpty()) {
            for (ItemTypeBL item : itemTypeMap.values()) {
                if (item.getMainCategory().equals(keyMain)) {
                    item.setDiscoutCategory(discount, expiredDiscount);
                    itemRepository.updateItemType(item);
                }
            }
        } else if (keySubSub.isEmpty()) {
            for (ItemTypeBL item : itemTypeMap.values()) {
                if (item.getMainCategory().equals(keyMain) && item.getSubCategory().equals(keySub)) {
                    item.setDiscoutCategory(discount, expiredDiscount);
                    itemRepository.updateItemType(item);
                }
            }
        } else {
            for (ItemTypeBL item : itemTypeMap.values()) {
                if (item.getMainCategory().equals(keyMain) && item.getSubCategory().equals(keySub)
                        && item.getSubSubCategory().equals(keySubSub)) {
                    item.setDiscoutCategory(discount, expiredDiscount);
                    itemRepository.updateItemType(item);
                }
            }
        }
    }

    private List<Integer> addStock(int itemTypeId, int quantity, Date date, int[] location) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        if (itemTypeMap.containsKey(itemTypeId)) {
            ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
            List<ItemInstanceBL> stock = itemType.addStock(date, quantity, location);
            itemRepository.newItemInstances(itemTypeId, stock);
            return stock.stream().map(ItemInstanceBL::getId).toList();
        } else {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " does not exist.");
        }
    }

    public void updatePrice(int itemTypeId, double price) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        if (itemTypeMap.containsKey(itemTypeId)) {
            ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
            itemType.setOriginalPrice(price);
            itemRepository.updateItemType(itemType);
        } else {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " does not exist.");
        }
    }

    public void updateDiscount(int itemTypeId, double discount, Date expiratioinDate) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        if (itemTypeMap.containsKey(itemTypeId)) {
            ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
            itemType.setExpirationDateItemDiscount(expiratioinDate);
            itemType.setItemDiscount(discount);
            itemRepository.updateItemType(itemType);
        } else {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " does not exist.");

        }
    }

    private void updateCost(int itemTypeId, double cost) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        if (itemTypeMap.containsKey(itemTypeId)) {
            ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
            itemType.setCost(cost);
            itemRepository.updateItemType(itemType);
        } else {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " does not exist.");
        }
    }

    public boolean sellItems(int itemTypeId, List<Integer> ids) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        if (itemTypeMap.containsKey(itemTypeId)) {
            ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
            itemRepository.removeItemInstances(itemTypeId, itemType.sellItems(ids));
            itemRepository.updateItemType(itemType);
            return sendAvailablityOrder(itemTypeId);
        } else {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " does not exist.");
        }
    }

    public List<ItemTypeDto> report(List<String[]> categoriesList) {
        HashMap<String, HashMap<String, HashMap<String, HashMap<Integer, ItemTypeBL>>>> categories = itemRepository
                .getCategories();
        List<ItemTypeBL> report = new ArrayList<>();

        for (String[] category : categoriesList) {
            if (category.length < 1 || category.length > 3) {
                throw new IllegalArgumentException(
                        "Category array must have 1 to 3 elements: mainCategory [required], subCategory [optional], subSubCategory [optional].");
            }

            String mainCategory = category[0];

            if (!categories.containsKey(mainCategory)) {
                throw new IllegalArgumentException("Main category '" + mainCategory + "' does not exist.");
            }

            if (category.length == 1) {
                for (HashMap<String, HashMap<Integer, ItemTypeBL>> subMap : categories.get(mainCategory).values()) {
                    if (subMap != null) {
                        for (HashMap<Integer, ItemTypeBL> itemTypeMap : subMap.values()) {
                            report.addAll(itemTypeMap.values());
                        }
                    }
                }
            }

            else if (category.length == 2) {
                String subCategory = category[1];
                HashMap<String, HashMap<Integer, ItemTypeBL>> subCategories = categories.get(mainCategory)
                        .get(subCategory);

                if (subCategories == null) {
                    throw new IllegalArgumentException(
                            "Subcategory '" + subCategory + "' does not exist under '" + mainCategory + "'.");
                }

                for (HashMap<Integer, ItemTypeBL> itemTypeMap : subCategories.values()) {
                    if (itemTypeMap != null) {
                        report.addAll(itemTypeMap.values());
                    }
                }
            }

            else if (category.length == 3) {
                String subCategory = category[1];
                String subSubCategory = category[2];

                HashMap<String, HashMap<Integer, ItemTypeBL>> subCategories = categories.get(mainCategory)
                        .get(subCategory);
                if (subCategories == null) {
                    throw new IllegalArgumentException(
                            "Subcategory '" + subCategory + "' does not exist under '" + mainCategory + "'.");
                }

                HashMap<Integer, ItemTypeBL> itemTypeMap = subCategories.get(subSubCategory);
                if (itemTypeMap == null) {
                    throw new IllegalArgumentException(
                            "Sub-subcategory '" + subSubCategory + "' does not exist under '" + subCategory + "'.");
                }

                report.addAll(itemTypeMap.values());
            }
        }

        List<ItemTypeDto> reportDto = new ArrayList<>();
        for (ItemTypeBL item : report) {
            reportDto.add(item.toDto());
        }
        return reportDto;
    }

    public HashMap<ItemTypeDto, List<ItemInstanceDto>> itemsExpireBeforeDate(Date date) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        HashMap<ItemTypeBL, List<ItemInstanceBL>> expiredItems = new HashMap<>();
        for (ItemTypeBL item : itemTypeMap.values()) {
            List<ItemInstanceBL> expired = item.itemsExpireBeforeDate(date);
            if (!expired.isEmpty()) {
                expiredItems.put(item, expired);
            }
        }

        HashMap<ItemTypeDto, List<ItemInstanceDto>> expiredItemsDto = new HashMap<>();
        for (Map.Entry<ItemTypeBL, List<ItemInstanceBL>> entry : expiredItems.entrySet()) {
            ItemTypeBL itemType = entry.getKey();
            List<ItemInstanceBL> expiredList = entry.getValue();
            List<ItemInstanceDto> expiredListDto = new ArrayList<>();
            for (ItemInstanceBL expiredItem : expiredList) {
                expiredListDto.add(expiredItem.toDto(itemType.getId()));
            }
            expiredItemsDto.put(itemType.toDto(), expiredListDto);
        }
        return expiredItemsDto;
    }

    public HashMap<ItemTypeDto, List<ItemInstanceDto>> defectiveReport() {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        HashMap<ItemTypeBL, List<ItemInstanceBL>> defectiveItems = new HashMap<>();
        for (ItemTypeBL item : itemTypeMap.values()) {
            List<ItemInstanceBL> defective = item.getDefectiveItems();
            if (!defective.isEmpty()) {
                defectiveItems.put(item, defective);
            }
        }
        HashMap<ItemTypeDto, List<ItemInstanceDto>> defectiveItemsDto = new HashMap<>();
        for (Map.Entry<ItemTypeBL, List<ItemInstanceBL>> entry : defectiveItems.entrySet()) {
            ItemTypeBL itemType = entry.getKey();
            List<ItemInstanceBL> defectiveList = entry.getValue();
            List<ItemInstanceDto> defectiveListDto = new ArrayList<>();
            for (ItemInstanceBL defectiveItem : defectiveList) {
                defectiveListDto.add(defectiveItem.toDto(itemType.getId()));
            }
            defectiveItemsDto.put(itemType.toDto(), defectiveListDto);
        }
        return defectiveItemsDto;
    }

    public boolean reportDefectiveItem(int itemTypeId, int itemInstanceId) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        if (itemTypeMap.containsKey(itemTypeId)) {
            ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
            ItemInstanceBL itemInstance = itemType.reportDefectiveItem(itemInstanceId);
            itemRepository.updateItemInstance(itemTypeId, itemInstance);
            return sendAvailablityOrder(itemTypeId);
        } else {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " does not exist.");
        }
    }

    public void moveItems(int itemTypeId, List<Integer> itemInstanceId, String[] destination) {
        if (itemInstanceId == null || itemInstanceId.isEmpty()) {
            throw new IllegalArgumentException("Item ID list cannot be null or empty.");
        }
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        if (itemTypeMap.containsKey(itemTypeId)) {
            ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
            itemRepository.updateItemInstances(itemTypeId, itemType.moveItems(itemInstanceId, destination));
        } else {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " does not exist.");
        }
    }

    public void updateMainCategory(int itemTypeId, String mainCategory) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
        if (itemType != null) {
            String oldMainCategory = itemType.getMainCategory();
            itemType.setMainCategory(mainCategory);
            removeFromCategories(itemType.getId(), oldMainCategory, itemType.getSubCategory(),
                    itemType.getSubSubCategory());
            addToCategories(itemType);
            itemRepository.updateItemType(itemType);
        }

    }

    public void updateSubCategory(int itemTypeId, String subCategory) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
        if (itemType != null) {
            String oldSubCategory = itemType.getSubCategory();
            itemType.setSubCategory(subCategory);
            removeFromCategories(itemType.getId(), itemType.getMainCategory(), oldSubCategory,
                    itemType.getSubSubCategory());
            addToCategories(itemType);
            itemRepository.updateItemType(itemType);
        }
    }

    public void updateSubSubCategory(int itemTypeId, String subSubCategory) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
        if (itemType != null) {
            String oldSubSubCategory = itemType.getSubSubCategory();
            itemType.setSubSubCategory(subSubCategory);
            removeFromCategories(itemType.getId(), itemType.getMainCategory(), itemType.getSubCategory(),
                    oldSubSubCategory);
            addToCategories(itemType);
            itemRepository.updateItemType(itemType);
        }
    }

    private void removeFromCategories(int id, String main, String sub, String subSub) {
        HashMap<String, HashMap<String, HashMap<String, HashMap<Integer, ItemTypeBL>>>> categories = itemRepository
                .getCategories();

        if (categories.containsKey(main)) {
            HashMap<String, HashMap<String, HashMap<Integer, ItemTypeBL>>> subMap = categories.get(main);
            if (subMap.containsKey(sub)) {
                HashMap<String, HashMap<Integer, ItemTypeBL>> subSubMap = subMap.get(sub);
                if (subSubMap.containsKey(subSub)) {
                    HashMap<Integer, ItemTypeBL> items = subSubMap.get(subSub);
                    items.remove(id);
                    if (items.isEmpty()) {
                        subSubMap.remove(subSub);
                        if (subSubMap.isEmpty()) {
                            subMap.remove(sub);
                            if (subMap.isEmpty()) {
                                categories.remove(main);
                            }
                        }
                    }
                }
            }
        }
    }

    private void addToCategories(ItemTypeBL item) {
        HashMap<String, HashMap<String, HashMap<String, HashMap<Integer, ItemTypeBL>>>> categories = itemRepository
                .getCategories();
        categories
                .computeIfAbsent(item.getMainCategory(), k -> new HashMap<>())
                .computeIfAbsent(item.getSubCategory(), k -> new HashMap<>())
                .computeIfAbsent(item.getSubSubCategory(), k -> new HashMap<>())
                .put(item.getId(), item);
    }

    public List<ItemTypeDto> minimalAmountReport() {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        List<ItemTypeDto> ItemType = new ArrayList<>();
        for (ItemTypeBL itemInstance : itemTypeMap.values()) {
            if (!(itemInstance.getAvailable() == ItemTypeBL.Available.AVAILABLE)) {
                ItemType.add(itemInstance.toDto());
            }
        }
        return ItemType;
    }

    public HashMap<Integer, String> getAvailableItems() {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        HashMap<Integer, String> availableItems = new HashMap<>();
        for (ItemTypeBL item : itemTypeMap.values()) {
            if (item.getAvailable() != ItemTypeBL.Available.NOT_AVAILABLE) {
                availableItems.put(item.getId(), item.getName());
            }
        }
        return availableItems;
    }

    public List<ItemTypeDto> sendAvailablityOrders() {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        HashMap<Integer, Integer> requiredItems = new HashMap<>();

        for (ItemTypeBL item : itemTypeMap.values()) {
            if (item.getAvailable() == ItemTypeBL.Available.NOT_AVAILABLE && item.toSupply() && !item.isOpenOrder()) {
                requiredItems.put(item.getId(), item.getMinQuantity() + 10);
            }
        }

        Map<Integer, Map<Integer, Map<Integer, Double>>> stock = supplierFacade
                .createOrderToAllSuppliers(currentDay, requiredItems);
        updateOpenOrder(stock);
        updateFutureMap(stock);
        List<ItemTypeBL> notSupplied = checkIfNotSupplied(requiredItems, stock);
        for (ItemTypeBL itemType : notSupplied) {
            stopSupplying(itemType.getId());
        }
        List<ItemTypeDto> ordered = new ArrayList<>();
        for (Integer id : getAllInnerKeys(stock)) {
            ItemTypeBL item = itemTypeMap.get(id);
            if (item != null) {
                ordered.add(item.toDto());
            }
        }
        return ordered;
    }

    private boolean sendAvailablityOrder(int itemTypeId) {
        ItemTypeBL item = itemRepository.getItemTypeMap().get(itemTypeId);
        if (item != null) {
            if (item.getAvailable() != ItemTypeBL.Available.AVAILABLE && item.toSupply() && !item.isOpenOrder()) {
                HashMap<Integer, Integer> requiredItems = new HashMap<>();
                requiredItems.put(itemTypeId, item.getMinQuantity() + 10);
                Map<Integer, Map<Integer, Map<Integer, Double>>> stock = supplierFacade
                        .createOrderToAllSuppliers(currentDay, requiredItems);
                if (!stock.isEmpty()) {
                    updateOpenOrder(stock);
                    updateFutureMap(stock);
                    return true;
                } else {
                    stopSupplying(itemTypeId);
                }
            }
            return false;
        } else {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " does not exist.");
        }
    }

    public int createPeriodicOrder(HashMap<Integer, Integer> items, int arrivalDay) {

        for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
            int itemId = entry.getKey();
            int quantity = entry.getValue();
            if (!itemRepository.getItemTypeMap().containsKey(itemId)) {
                throw new IllegalArgumentException("Item with ID " + itemId + " does not exist.");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0.");
            }

        }
        HashMap<Integer, OutgoingOrderBL> periodicOrders = itemRepository.getPeriodicOrders();
        OutgoingOrderBL order = new OutgoingOrderBL(orderId, items, arrivalDay);
        itemRepository.newOrder(order);
        periodicOrders.put(orderId, order);
        orderId++;
        return orderId - 1;
    }

    public void sendPeriodicOrders() {
        HashMap<Integer, OutgoingOrderBL> periodicOrders = itemRepository.getPeriodicOrders();
        HashMap<Integer, List<ItemTypeBL>> notSupplied = new HashMap<>();
        for (Map.Entry<Integer, OutgoingOrderBL> entry : periodicOrders.entrySet()) {
            OutgoingOrderBL order = entry.getValue();
            if (order.getDay() == (currentDay + 1) % 7) {
                HashMap<Integer, Integer> reqItems = order.getItemsQuantity();
                Map<Integer, Map<Integer, Double>> stock = supplierFacade
                        .periodOrder(currentDay, reqItems);
                Map<Integer, Map<Integer, Map<Integer, Double>>> newStock = new HashMap<>();
                newStock.put((currentDay + 1) % 7, stock);
                updateOpenOrder(newStock);
                updateFutureMap(newStock);
                notSupplied.put(order.getOrderID(), checkIfNotSupplied(reqItems, newStock));
            }
        }
        for (Map.Entry<Integer, List<ItemTypeBL>> entry : notSupplied.entrySet()) {
            int orderId = entry.getKey();
            List<ItemTypeBL> items = entry.getValue();
            for (ItemTypeBL item : items) {
                cancelPeriodicOrderItem(orderId, item.getId());
            }
        }
    }

    public void updateOrder(int orderId, int itemTypeId, int quantity) {
        ItemTypeBL itemType = itemRepository.getItemTypeMap().get(itemTypeId);
        if (itemType == null) {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " does not exist.");
        }
        if (itemType.toSupply() == false) {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " is not supplied.");
        }
        if (quantity == 0) {
            cancelPeriodicOrderItem(orderId, itemTypeId);
        }
        if (checkQuantity(itemTypeId, quantity) != quantity) {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " has not enough quantity.");
        }

        HashMap<Integer, OutgoingOrderBL> periodicOrders = itemRepository.getPeriodicOrders();
        if (periodicOrders.containsKey(orderId)) {

            OutgoingOrderBL order = periodicOrders.get(orderId);
            if (currentDay < order.getDay()) {
                boolean newItem = order.setItemTypeQuantity(itemTypeId, quantity);
                if (newItem) {
                    itemRepository.newOrder_ItemType(orderId, itemTypeId, quantity);
                } else {
                    itemRepository.updateOrder_ItemType(orderId, itemTypeId, quantity);
                }
            }
        } else {
            throw new IllegalArgumentException("Order with ID " + orderId + " does not exist.");
        }
    }

    public void cancelPeriodicOrder(int orderId) {
        HashMap<Integer, OutgoingOrderBL> periodicOrders = itemRepository.getPeriodicOrders();
        if (periodicOrders.containsKey(orderId)) {
            OutgoingOrderBL order = periodicOrders.get(orderId);
            for (Map.Entry<Integer, Integer> entry : order.getItemsQuantity().entrySet()) {
                int itemTypeId = entry.getKey();
                itemRepository.removeOrder_Items(orderId, itemTypeId);
            }
            periodicOrders.remove(orderId);
            itemRepository.removeOrder(orderId);
        } else {
            throw new IllegalArgumentException("Order with ID " + orderId + " does not exist.");
        }
    }

    public void cancelPeriodicOrderItem(int orderId, int itemTypeId) {
        HashMap<Integer, OutgoingOrderBL> periodicOrders = itemRepository.getPeriodicOrders();
        if (periodicOrders.containsKey(orderId)) {
            OutgoingOrderBL order = periodicOrders.get(orderId);
            if (order.getItemsQuantity().containsKey(itemTypeId)) {
                order.getItemsQuantity().remove(itemTypeId);
                itemRepository.removeOrder_Items(orderId, itemTypeId);
                if (order.getItemsQuantity().isEmpty()) {
                    periodicOrders.remove(orderId);
                    itemRepository.removeOrder(orderId);

                }
            }
        } else {
            throw new IllegalArgumentException("Order with ID " + orderId + " does not exist.");
        }
    }

    public void resupplyItem(int itemTypeId) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        if (itemTypeMap.containsKey(itemTypeId)) {
            if (supplierFacade.hasSupplier(itemTypeId)) {
                ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
                itemType.setToSupply(true);
                itemRepository.updateItemType(itemType);
            } else {
                throw new IllegalArgumentException("Item with ID " + itemTypeId + " does not have a supplier.");
            }
        } else {
            throw new IllegalArgumentException("Item with ID " + itemTypeId + " does not exist.");
        }
    }

    private AbstractMap.SimpleEntry<Integer, Double> convertToEntry(Map<Integer, Double> entry) {
        if (entry == null || entry.isEmpty()) {
            throw new IllegalArgumentException("Entry cannot be null or empty.");
        }

        Map.Entry<Integer, Double> singleEntry = entry.entrySet().iterator().next();
        return new AbstractMap.SimpleEntry<>(singleEntry.getKey(), singleEntry.getValue());

    }

    public void updateOrders() {
        for (Map.Entry<Integer, OutgoingOrderBL> entry : itemRepository.getPeriodicOrders().entrySet()) {
            OutgoingOrderBL order = entry.getValue();
            if (order.getDay() == (currentDay + 1) % 7) {
                HashMap<Integer, Integer> reqItems = order.getItemsQuantity();
                for (Map.Entry<Integer, Integer> entry1 : reqItems.entrySet()) {
                    int itemId = entry1.getKey();
                    int quantity = entry1.getValue();
                    int newQuantity = checkQuantity(itemId, quantity);
                    if (newQuantity != quantity) {
                        updateOrder(quantity, itemId, newQuantity);
                    }
                }
            }
        }
    }

    private int checkQuantity(int itemTypeId, int quantity) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        if (itemTypeMap.containsKey(itemTypeId)) {
            ItemTypeBL itemType = itemTypeMap.get(itemTypeId);
            if (quantity + itemType.getQuantity() < itemType.getMinQuantity()) {
                return itemType.getMinQuantity() - itemType.getQuantity();
            }

        }
        return quantity;
    }

    private void updateFutureMap(Map<Integer, Map<Integer, Map<Integer, Double>>> newStock) {
        Map<Integer, Map<Integer, AbstractMap.SimpleEntry<Integer, Double>>> futureOrders = itemRepository
                .getFutureOrders();
        for (Map.Entry<Integer, Map<Integer, Map<Integer, Double>>> entry1 : newStock.entrySet()) {
            int day = entry1.getKey();
            futureOrders.computeIfAbsent(day, k -> new HashMap<>());
            Map<Integer, Map<Integer, Double>> itemTypeMap = entry1.getValue();
            for (Map.Entry<Integer, Map<Integer, Double>> entry2 : itemTypeMap.entrySet()) {
                int itemTypeId = entry2.getKey();
                AbstractMap.SimpleEntry<Integer, Double> new_quantity_cost = convertToEntry(
                        entry2.getValue());
                AbstractMap.SimpleEntry<Integer, Double> quantity_cost = futureOrders.get(day).get(itemTypeId);
                if (quantity_cost != null) {
                    AbstractMap.SimpleEntry<Integer, Double> newEntry = new AbstractMap.SimpleEntry<>(
                            quantity_cost.getKey() + new_quantity_cost.getKey(), new_quantity_cost.getValue());
                    futureOrders.get(day).put(itemTypeId, newEntry);
                    itemRepository
                            .updateFutureOrder(
                                    new FutureOrderDto(day, itemTypeId, newEntry.getKey(), newEntry.getValue()));
                } else {
                    futureOrders.get(day).put(itemTypeId, new_quantity_cost);
                    itemRepository.newFutureOrder(
                            new FutureOrderDto(day, itemTypeId, new_quantity_cost.getKey(),
                                    new_quantity_cost.getValue()));
                }
            }
        }
    }

    public void setIsOpenOrderById(int itemTypeID, boolean isOpenOrder) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        if (itemTypeMap.containsKey(itemTypeID)) {
            ItemTypeBL itemType = itemTypeMap.get(itemTypeID);
            itemType.setIsOpenOrder(isOpenOrder);
            itemRepository.updateItemType(itemType);
        } else {
            throw new IllegalArgumentException("Item with ID " + itemTypeID + " does not exist.");
        }
    }

    private List<ItemTypeBL> checkIfNotSupplied(Map<Integer, Integer> requestedItems,
            Map<Integer, Map<Integer, Map<Integer, Double>>> actualStock) {
        List<ItemTypeBL> notSuppliedItems = new ArrayList<>();
        for (Integer itemTypeId : requestedItems.keySet()) {
            boolean wasSupplied = false;

            for (Map<Integer, Map<Integer, Double>> supplierStock : actualStock.values()) {
                if (supplierStock.containsKey(itemTypeId)) {
                    wasSupplied = true;
                    break;
                }
            }
            if (!wasSupplied) {
                ItemTypeBL item = itemRepository.getItemTypeMap().get(itemTypeId);
                if (item != null) {
                    notSuppliedItems.add(item);
                }
            }
        }
        return notSuppliedItems;
    }

    private void updateOpenOrder(Map<Integer, Map<Integer, Map<Integer, Double>>> newStock) {
        HashMap<Integer, ItemTypeBL> itemTypeMap = itemRepository.getItemTypeMap();
        for (Integer itemTypeId : getAllInnerKeys(newStock)) {
            if (itemTypeMap.containsKey(itemTypeId)) {
                ItemTypeBL item = itemTypeMap.get(itemTypeId);
                item.setIsOpenOrder(true);
                itemRepository.updateItemType(item);
            }
        }
    }

    private List<Integer> getAllInnerKeys(Map<Integer, Map<Integer, Map<Integer, Double>>> stock) {
        Set<Integer> result = new HashSet<>();

        for (Map<Integer, Map<Integer, Double>> middleMap : stock.values()) {
            result.addAll(middleMap.keySet());
        }

        return new ArrayList<>(result);
    }

    private void checkExpirationDates() {
        for (ItemTypeBL item : itemRepository.getItemTypeMap().values()) {
            List<Integer> expired = item.expiredItems();
            if (!expired.isEmpty()) {
                itemRepository.removeItemInstances(item.getId(), expired);
            }
        }
    }

}
