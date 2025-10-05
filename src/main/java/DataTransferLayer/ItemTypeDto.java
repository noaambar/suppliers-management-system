package DataTransferLayer;

import java.sql.Date;

public class ItemTypeDto {
    private int id;
    private String name; 
    private int quantity;
    private int deliveryTime = 7;
    private int available;
    private double originalPrice;
    private double itemDiscount;
    private Date expirationDateItemDiscount;
    private double categoryDiscount;
    private Date expirationDateCategoryDiscount;
    private double cost;
    private String manufacture;
    private String mainCategory;
    private String subCategory;
    private String subSubCategory;
    private int sales = 0;
    private boolean toSupply;
    private boolean isOpenOrder;

    public ItemTypeDto(int id, String name, int quantity, int available, double originalPrice,
            double itemDiscount, Date expirationDateItemDiscount, double categoryDiscount,
            Date expirationDateCategoryDiscount, double cost, String manufacture, String mainCategory,
            String subCategory, String subSubCategory, int sales, boolean toSupply, boolean isOpenOrder) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.available = available;
        this.originalPrice = originalPrice;
        this.itemDiscount = itemDiscount;
        this.expirationDateItemDiscount = expirationDateItemDiscount;
        this.categoryDiscount = categoryDiscount;
        this.expirationDateCategoryDiscount = expirationDateCategoryDiscount;
        this.cost = cost;
        this.manufacture = manufacture;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
        this.sales = sales;
        this.toSupply = toSupply;
        this.isOpenOrder = isOpenOrder;
    }

        public ItemTypeDto(int id, String name, double originalPrice,
            double itemDiscount, Date expirationDateItemDiscount, double categoryDiscount,
            Date expirationDateCategoryDiscount, double cost, String manufacture, String mainCategory,
            String subCategory, String subSubCategory, int sales, boolean toSupply, boolean isOpenOrder) {
        this.id = id;
        this.name = name;
        this.originalPrice = originalPrice;
        this.itemDiscount = itemDiscount;
        this.expirationDateItemDiscount = expirationDateItemDiscount;
        this.categoryDiscount = categoryDiscount;
        this.expirationDateCategoryDiscount = expirationDateCategoryDiscount;
        this.cost = cost;
        this.manufacture = manufacture;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
        this.sales = sales;
        this.toSupply = toSupply;
        this.isOpenOrder = isOpenOrder;
    }

      public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public int getDeliveryTime() {
        return deliveryTime;
    }
    
    public int getAvailable() {
        return available;
    }
    
    public double getOriginalPrice() {
        return originalPrice;
    }
    
    public double getItemDiscount() {
        return itemDiscount;
    }
    
    public Date getExpirationDateItemDiscount() {
        return expirationDateItemDiscount;
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
    
    public String getManufacture() {
        return manufacture;
    }
    
    public String getMainCategory() {
        return mainCategory;
    }
    
    public String getSubCategory() {
        return subCategory;
    }
    
    public String getSubSubCategory() {
        return subSubCategory;
    }
    
    public int getSales() {
        return sales;
    }

    public boolean toSupply() {
        return toSupply;
    }

    public boolean isOpenOrder(){
        return isOpenOrder;
    }
    
    public String toString() {
        return "{" +
                "id: " + id +
                ", name: " + name+
                '}';
    }
}
