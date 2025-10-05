package DataTransferLayer;

public class SupplierDto {
    private String supplierName;
    private Integer supplierID;
    private String address;
    private Integer bankAccount;
    private Boolean isTransport;
    private Boolean isDays;
    private Boolean sunday;
    private Boolean monday;
    private Boolean tuesday;
    private Boolean wednesday;
    private Boolean thursday;
    private Boolean friday;
    private Boolean saturday;
    private Integer countSupplierAgreement;
    private Integer fairPhoneNumber;

    public SupplierDto(String supplierName, Integer supplierID, String address, Integer bankAccount,
                       Boolean isTransport, Boolean isDays,  Boolean sunday, Boolean monday, Boolean tuesday, Boolean wednesday, Boolean thursday, Boolean friday, Boolean saturday,
                       int countSupplierAgreement, int fairPhoneNumber) {

        this.supplierName = supplierName;
        this.supplierID = supplierID;
        this.address = address;
        this.bankAccount = bankAccount;
        this.isTransport = isTransport;
        this.isDays = isDays;
        this.sunday=sunday;
        this.monday=monday;
        this.thursday=thursday;
        this.tuesday=tuesday;
        this.wednesday=wednesday;
        this.friday=friday;
        this.saturday=saturday;
        this.countSupplierAgreement=countSupplierAgreement;
        this.fairPhoneNumber=fairPhoneNumber;

    }
    public String getSupplierName() {
        return supplierName;
    }

    public Integer getSupplierID() {
        return supplierID;
    }

    public String getAddress() {
        return address;
    }

    public Integer getBankAccount() {
        return bankAccount;
    }

    public Boolean getIsTransport() {
        return isTransport;
    }

    public Boolean getIsDays() {
        return isDays;
    }

    public Boolean getFriday() {
        return friday;
    }

    public Boolean getMonday() {
        return monday;
    }

    public Boolean getSaturday() {
        return saturday;
    }

    public Boolean getSunday() {
        return sunday;
    }

    public Boolean getThursday() {
        return thursday;
    }

    public Boolean getTuesday() {
        return tuesday;
    }

    public Boolean getWednesday() {
        return wednesday;
    }

    public int getCountSupplierAgreement() {
        return countSupplierAgreement;
    }

    public int getFairPhoneNumber() {
        return fairPhoneNumber;
    }

}
