package DataTransferLayer;

public class ProductDto {
    private Integer itemID;
    private String itemName;
    private String unit;
    private Integer amount;
    private String producer;

    public ProductDto(Integer itemID, String itemName, String unit, String producer, Integer amount) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.unit = unit;
        this.producer = producer;
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getItemName() {
        return itemName;
    }

    public Integer getItemID() {
        return itemID;
    }

    public String getProducer() {
        return producer;
    }

    public String getUnit() {
        return unit;
    }
}
