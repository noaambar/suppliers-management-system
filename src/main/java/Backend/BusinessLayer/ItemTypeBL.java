package Backend.BusinessLayer;

import java.sql.Date;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import DataTransferLayer.ItemTypeDto;

public class ItemTypeBL {

    public enum Available {
        AVAILABLE, 
        MINIMAL_AMOUNT, 
        NOT_AVAILABLE
    }

    private int itemTypeId;
    private String name;
    private int deliveryTime = 7;
    private AbstractMap.SimpleEntry<Double, Double> price;
    private Date expirationDateItemDiscount = null;
    private double categoryDiscount = 0;
    private Date expirationDateCategoryDiscount = null;
    private double cost = -1;
    private String manufacture;
    private List<ItemInstanceBL> itemsList = new ArrayList<>();
    private List<ItemInstanceBL> defectiveItems = new ArrayList<>();
    private int itemInstanceId = 0;
    private String mainCategory;
    private String subCategory;
    private String subSubCategory;
    private int sales = 0;
    private boolean isOpenOrder = false;
    private boolean toSupply = true;
  
    public ItemTypeBL(int itemTypeId, String name, AbstractMap.SimpleEntry<Double, Double> price,
                   String manufacture, String mainCategory, String subCategory, String subSubCategory) {
        if (!isValidString(name)) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (!isValidString(manufacture)) {
            throw new IllegalArgumentException("Manufacture cannot be null or empty");
        }
        if (!isValidString(mainCategory)) {
            throw new IllegalArgumentException("Main category cannot be empty");
        }
        if (subCategory == null || subSubCategory == null) {
            throw new IllegalArgumentException("Sub category and sub-sub category cannot be null");
        }
        if (price == null || price.getKey() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }

        this.itemTypeId = itemTypeId;
        this.name = name;
        this.price = price;
        this.manufacture = manufacture;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;

    }

    public ItemTypeBL(ItemTypeDto itemsDTO) {
        this.itemTypeId = itemsDTO.getId();
        this.name = itemsDTO.getName();
        this.price = new AbstractMap.SimpleEntry<>(
            itemsDTO.getOriginalPrice(),
            itemsDTO.getItemDiscount() );
        this.expirationDateItemDiscount = itemsDTO.getExpirationDateItemDiscount();
        this.categoryDiscount = itemsDTO.getCategoryDiscount(); 
        this.expirationDateCategoryDiscount = itemsDTO.getExpirationDateCategoryDiscount();
        this.cost = itemsDTO.getCost();
        this.manufacture = itemsDTO.getManufacture();
        this.mainCategory = itemsDTO.getMainCategory();
        this.subCategory = itemsDTO.getSubCategory();
        this.subSubCategory = itemsDTO.getSubSubCategory();
        this.sales = itemsDTO.getSales();
        this.toSupply = itemsDTO.toSupply();
       
    }

    public int getId() {
        return itemTypeId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return getItemsList().size();
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    private int getDemand() {
        if(getSales() == 0) {
            return 0;
        }
        return getQuantity()/getSales();
    }

    public void setDiscoutCategory(double categoryDiscount, Date expirationDateDiscount) {
        if (categoryDiscount < 0 || categoryDiscount > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        this.categoryDiscount = categoryDiscount;
        this.expirationDateCategoryDiscount = expirationDateDiscount;
    }

    public int getMinQuantity() {
        return getDemand() * getDeliveryTime();
    }

    public int getSales() {
        return sales;
    }

    public void setDeliveryTime(int deliveryTime) {
        if (deliveryTime < 0) {
            throw new IllegalArgumentException("Minimal quantity cannot be negative");
        }
        this.deliveryTime = deliveryTime;
    }

    public Available getAvailable() {
       if (getQuantity() > getMinQuantity()) {
            return Available.AVAILABLE;
        } else if (getQuantity() == 0) {
            return Available.NOT_AVAILABLE;
        } else {           
            return Available.MINIMAL_AMOUNT;
        }
    }

    private double afterCategoryDiscount(double price){
        if (expirationDateCategoryDiscount != null) {
            Date currentDate = new Date(System.currentTimeMillis());
            if (currentDate.after(expirationDateCategoryDiscount)) {
                // The discount has expired, use the default price
                return price;
            } else {
                return price * (1 - categoryDiscount / 100); // Assuming discount is in percentage
            }
        }
        return price; // No discount applied

    }

    public double getPrice() {
        double price = this.price.getKey(); // Assuming 0 is the key for the default price
        double discount = this.price.getValue(); // Assuming 1 is the key for the discount price
        if (expirationDateItemDiscount != null) {
            Date currentDate = new Date(System.currentTimeMillis());
            if (currentDate.after(expirationDateItemDiscount)) {
                // The discount has expired, use the default price
                return afterCategoryDiscount(price);
            } else {
                return afterCategoryDiscount(price * (1 - discount / 100)); // Assuming discount is in percentage
            }
        }
        return afterCategoryDiscount(price); // No discount applied
    }

    public void setOriginalPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        this.price = new AbstractMap.SimpleEntry<>(price, this.price.getValue()); 
    }

    public double getItemDiscount() {
        return this.price.getValue();
    }

    public void setItemDiscount(double discount) {
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }

        this.price = new AbstractMap.SimpleEntry<>(this.price.getKey(), discount);
    }

    public Date getExpirationDateItemDiscount() {
        return expirationDateItemDiscount;
    }

    public void setExpirationDateItemDiscount(Date expiredDiscount) {
        if (expiredDiscount == null) {
            throw new IllegalArgumentException("Expire date cannot be null");
        }

        this.expirationDateItemDiscount = expiredDiscount;
    }

    public double getCategoryDiscount() {
        return categoryDiscount;
    }

    public Date getExpirationDateCategoryDiscount() {
        return expirationDateCategoryDiscount;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        if (cost > this.price.getKey()) {
            price = new AbstractMap.SimpleEntry<>(cost*2, this.price.getValue());
        }

        this.cost = cost;
    }

    public String getManufacture() {
        return manufacture;
    }

    public List<ItemInstanceBL> getItemsList() {
        return itemsList;
    }

    public List<ItemInstanceBL> getDefectiveItems() {
        return defectiveItems;
    }

    public List<ItemInstanceBL> moveItems(List<Integer> ids, String[] destination) {
        if(destination == null || destination.length != 3) {
            throw new IllegalArgumentException("Destination cannot be null and must have 3 components");
        }
        int [] destinationInt = new int[3];
        for (int i = 0; i < destination.length; i++) {
                destinationInt[i] = Integer.parseInt(destination[i]);
        }
        if(destinationInt[0]!= 0 && destinationInt[0] !=1){
            throw new IllegalArgumentException("Destination must be either 0 or 1");
        }
        if(destinationInt[1] < 0 || destinationInt[2] < 0){
            throw new IllegalArgumentException("Destination cannot be negative");
        }
        List<ItemInstanceBL> updated = new ArrayList<>();
        for (int id : ids) {
            for (ItemInstanceBL item : itemsList) {
                if (item.getId() == id) {
                    item.setLocation(destinationInt);
                    updated.add(item);
                    break;
                }
            }
        }
        if (updated.isEmpty()) {
            throw new IllegalArgumentException("No items found with the provided IDs");
        }
        return updated;
    }

    public List<ItemInstanceBL> addStock(Date expirationDate, int amount, int[] location) {
        if (location == null || location.length != 3) {
            throw new IllegalArgumentException("Location cannot be null and must have 3 components");
        }
        if (expirationDate == null) {
            throw new IllegalArgumentException("Expire date cannot be null");
        }
        List<ItemInstanceBL> stock = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            ItemInstanceBL item = new ItemInstanceBL(getId(), itemInstanceId, expirationDate, location);
            stock.add(item);
            itemInstanceId++;
        }

        itemsList.addAll(stock);
        return stock;
    }

    public List<Integer> sellItems(List<Integer> ids) {
    
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("IDs cannot be null or empty");
        }
        List<Integer> foundIds = new ArrayList<>();
        for (int id : ids) {
            Iterator<ItemInstanceBL> iterator = itemsList.iterator();
            while (iterator.hasNext()) {
                ItemInstanceBL item = iterator.next();
                if (item.getId() == id) {
                    iterator.remove(); 
                    foundIds.add(id);
                    break;
                }
            }
        }
        if (foundIds.isEmpty()) {
            throw new IllegalArgumentException("No items found with the provided IDs");
        }
    
        sales += foundIds.size();
    
        return foundIds;
    }


    public ItemInstanceBL reportDefectiveItem(int itemId) {
        for (Iterator<ItemInstanceBL> iterator = itemsList.iterator(); iterator.hasNext(); ) {
            ItemInstanceBL item = iterator.next();
            if (item.getId() == itemId) {
                item.setDefective(ItemInstanceBL.Defective.DEFECTIVE);
                defectiveItems.add(item);
                iterator.remove();           
                return item;
            }
        }
        throw new IllegalArgumentException("Item with ID " + itemId + " not found or already reported as a defective item.");
    }

    public List<ItemInstanceBL> itemsExpireBeforeDate(Date date) {
        List<ItemInstanceBL> expiredItems = new ArrayList<>();
        for (ItemInstanceBL item : itemsList) {
            if (item.getExpirationDate().before(date) || item.getExpirationDate().equals(date)) {
                expiredItems.add(item);
            }
        }
        return expiredItems;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        if (!isValidString(mainCategory)) {
            throw new IllegalArgumentException("Main category cannot be empty");
        }

        this.mainCategory = mainCategory;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        if (subCategory == null || subCategory.trim().isEmpty() && subSubCategory.trim().isEmpty()) {
            throw new IllegalArgumentException("Sub category cannot be null or empty");
        }

        this.subCategory = subCategory;
    }

    public String getSubSubCategory() {
        return subSubCategory;
    }

    public void setSubSubCategory(String subSubCategory) {
        if (subSubCategory == null || (subCategory.trim().isEmpty() && !subSubCategory.trim().isEmpty())) {
            throw new IllegalArgumentException("Sub-sub category cannot be null or empty");
        }
   
        this.subSubCategory = subSubCategory;
    }

    public double getOriginalPrice() {
        return price.getKey();
    }

    private boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public void setToSupply(boolean toSupply) {
        if(toSupply() && toSupply) {
            throw new IllegalArgumentException("Item is already marked for supply");
        }
        if(!toSupply() && !toSupply) {
            throw new IllegalArgumentException("Item is already not marked for supply");
        }

        this.toSupply = toSupply;
    }

    public boolean toSupply() {
        return toSupply;
    }

    public void setItemList(List<ItemInstanceBL> itemsList) {
        this.itemsList = itemsList;
        calculateItemId();
        for (ItemInstanceBL item : this.itemsList) {
            if (item.getDefective() == ItemInstanceBL.Defective.DEFECTIVE) {
                defectiveItems.add(item);
                itemsList.remove(item);
            }
        }
    }

    private void calculateItemId() {
        if (itemsList.isEmpty() && defectiveItems.isEmpty()) {
            itemInstanceId = 0;
        } else {
            List<ItemInstanceBL> allItems = new ArrayList<>();
            allItems.addAll(itemsList);
            allItems.addAll(defectiveItems);
            for (ItemInstanceBL item : allItems) {
                if (item.getId() > itemInstanceId) {
                    itemInstanceId = item.getId();
                }
            }
        }
        itemInstanceId++;
    }

    public boolean isOpenOrder() {
        return isOpenOrder;
    }

    public void setIsOpenOrder(boolean isOpenOrder) {
        this.isOpenOrder = isOpenOrder;
    }

    public List<Integer> expiredItems() {
        List<Integer> result  = new ArrayList<>();
        List<ItemInstanceBL> toDelete  = new ArrayList<>();
        for (ItemInstanceBL instance : getItemsList()){
            if (instance.checkExpiration()){
                result.add(instance.getId());
                toDelete.add(instance);
            }
        }
        itemsList.removeAll(toDelete);
        return result;
    }

    public ItemTypeDto toDto() {
        return new ItemTypeDto(
                getId(),
                getName(),
                getQuantity(),
                getAvailable().ordinal(),
                getOriginalPrice(),
                getItemDiscount(),
                getExpirationDateItemDiscount(),
                getCategoryDiscount(),
                getExpirationDateCategoryDiscount(),
                getCost(),
                getManufacture(),
                getMainCategory(),
                getSubCategory(),
                getSubSubCategory(),
                getSales(),
                toSupply(),
                isOpenOrder()
        );
    }

}