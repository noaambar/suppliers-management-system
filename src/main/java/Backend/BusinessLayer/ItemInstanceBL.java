package Backend.BusinessLayer;

import java.sql.Date;

import DataTransferLayer.ItemInstanceDto;

public class ItemInstanceBL {

    public enum Defective {
        GOOD, EXPIRED, DEFECTIVE
    }

    private int id;
    private Date expirationDate;
    private Defective defective;
    private int[] location = new int[3]; 

    public ItemInstanceBL(int itemTypeId, int id, Date expirationDate, int[] location) {
        if (expirationDate == null) {
            throw new IllegalArgumentException("Expire date cannot be null");
        }
        if (location == null || location.length != 3) {
            throw new IllegalArgumentException("Location cannot be null and must have 3 components");
        }
        this.id = id;
        this.expirationDate = expirationDate;
        this.defective = Defective.GOOD;
        this.location = location;
    }

    public ItemInstanceBL(ItemInstanceDto dto) {
        this.id = dto.getId();
        this.expirationDate = dto.getExpirationDate();
        this.defective = Defective.values()[dto.getDefective()];
        this.location[0] = dto.getLocation();
        this.location[1] = dto.getLocation_pass();
        this.location[2] = dto.getLocation_shelf();
    }

    public int getId() {
        return id;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public Defective getDefective() {
        return defective;
    }

    public int[] getLocation() {
        return location;
    }

    public void setLocation(int[] location) {
        if (location == null || location.length != 3) {
            throw new IllegalArgumentException("Location cannot be null and must have 3 components");
        }
        this.location = location;
    }

    public void setDefective(Defective defective) {
        if (defective == null) {
            throw new IllegalArgumentException("Defective cannot be null");
        }
        this.defective = defective;
    }

    public boolean checkExpiration() {
        Date currentDate = new Date(System.currentTimeMillis());
        if (currentDate.after(expirationDate)) {
            defective = Defective.EXPIRED;
            return true;
        }
        return false;
    }

    @Override
    public String toString() { 
        return "id = " + id + ", expirationDate = " + expirationDate + ", defective = " + defective + ", Location = " +  getLocationString();
    }

    public String getLocationString() {
        String locationString = "{";
        if(location[0] == 0) {
            locationString += "STORE, ";
        } else{ 
            locationString += "WAREHOUSE, ";
        }
        locationString += "pass: " + location[1] + ", shelf: " + location[2] + "}";
        return locationString;

    }

    public ItemInstanceDto toDto(int itemTypeId) {
         return new ItemInstanceDto(
            itemTypeId,
                getId(),
                getExpirationDate(),
                getDefective().ordinal(),
                getLocation()[0],
                getLocation()[1],
                getLocation()[2]
        );
    }

}