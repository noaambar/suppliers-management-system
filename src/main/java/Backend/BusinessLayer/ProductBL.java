package Backend.BusinessLayer;

import DataTransferLayer.ProductDto;

public class ProductBL {
    private Integer itemID;
    private String itemName;
    private String unit;
    private Integer amount;
    private String producer;

    public ProductBL(Integer itemID, String itemName, String unit, String producer, Integer amount) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.unit = unit;
        this.producer = producer;
        this.amount = amount;
    }
    public ProductBL(ProductDto productDto){
        this.itemID = productDto.getItemID();
        this.itemName = productDto.getItemName();
        this.unit = productDto.getUnit();
        this.producer = productDto.getProducer();
        this.amount = productDto.getAmount();
    }

    public Integer getItemID() {
        return itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public String getUnit() {
        return unit;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getProducer() {
        return producer;
    }

    public ProductDto toDto() {
        return new ProductDto(itemID, itemName, unit, producer, amount);
    }
}
