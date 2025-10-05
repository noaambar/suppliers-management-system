package DataTransferLayer;

import java.sql.Date;

public class ItemInstanceDto {
    private int itemTypeId;
    private int id;
    private Date expirationDate;
    private int defective;
    private int location;
    private int location_pass;
    private int location_shelf;

    public ItemInstanceDto(int itemTypeId, int id, Date expirationDate, int defective, int location, int location_pass, int location_shelf) {
        this.itemTypeId = itemTypeId;
        this.id = id;
        this.expirationDate = expirationDate;
        this.defective = defective;
        this.location = location;
        this.location_pass = location_pass;
        this.location_shelf = location_shelf;
    }

    public int getItemTypeId() {
        return itemTypeId;
    }

    public int getId() {
        return id;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public int getDefective() {
        return defective;
    }

    public int getLocation() {
        return location;
    }

    public int getLocation_pass() {
        return location_pass;
    }

    public int getLocation_shelf() {
        return location_shelf;
    }
    
}
