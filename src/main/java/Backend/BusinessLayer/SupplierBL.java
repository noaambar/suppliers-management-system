package Backend.BusinessLayer;

import java.util.*;

import DataTransferLayer.SupplierDto;

public class SupplierBL {

    private String supplierName;
    private Integer supplierID;
    private String address;
    private Integer bankAccount;;
    private List<Integer> employees;
    private Boolean isTransport;
    private Boolean isDays;
    private String[] days;
    private List<SupplierAgreementBL> supplierAgreement;
    private Integer countSupplierAgreement;
    private List<String> companies;
    private Integer fairPhoneNumber;


    public SupplierBL(String supplierName, Integer supplierID, String address, Integer bankAccount,
             List<Integer> employees,
                      Boolean isTransport, Boolean isDays, String[] days,List<String> companies) {
        this.supplierName = supplierName;
        this.supplierID = supplierID;
        this.address = address;
        this.bankAccount = bankAccount;
        this.employees =  employees;
        this.isTransport = isTransport;
        this.isDays = isDays;
        this.days = (days != null) ? days : new String[0];
        this.supplierAgreement=new ArrayList<>();
        this.countSupplierAgreement=0;
        this.companies = companies;
        this.fairPhoneNumber=0;

    }
    public SupplierBL(SupplierDto supplierDto) {
        this.supplierName = supplierDto.getSupplierName();
        this.supplierID = supplierDto.getSupplierID();
        this.address = supplierDto.getAddress();
        this.bankAccount = supplierDto.getBankAccount();
        this.isTransport = supplierDto.getIsTransport();
        this.isDays = supplierDto.getIsDays();
        this.days = new String[7];
        int i=0;
        if (supplierDto.getSunday()) {
            days[i] = "sunday";
            i++;
        }
        if (supplierDto.getMonday()) {
            days[i] = "monday";
            i++;
        }
        if (supplierDto.getTuesday()) {
            days[i] = "tuesday";
            i++;
        }
        if (supplierDto.getWednesday()) {
            days[i] = "Wednesday";
            i++;
        }
        if (supplierDto.getThursday()) {
            days[i] = "thursday";
            i++;
        }
        if (supplierDto.getFriday()) {
            days[i] = "friday";
            i++;
        }
        if (supplierDto.getSaturday()) {
            days[i] = "saturday";
            i++;
        }
        for(int j=i; j<7; j++){
            days[j]=" ";
        }
        this.countSupplierAgreement=supplierDto.getCountSupplierAgreement();
        this.fairPhoneNumber=0;
    }
    public void setAgreements(List<SupplierAgreementBL> agreement) {
        this.supplierAgreement=agreement;
        for (int i=0;i<agreement.size();i++) {
            if (supplierAgreement.get(i).getSupplierAgreementID() > countSupplierAgreement) {
                countSupplierAgreement=supplierAgreement.get(i).getSupplierAgreementID();
            }
        }
        countSupplierAgreement++;
    }
    public void setEmployees(List<Integer> employee) {
        this.employees=employee;
    }
    public void setCompanies(List<String> companies) {
        this.companies=companies;
    }
    public SupplierAgreementBL getAgreement(int agreementId)
    {
        for (SupplierAgreementBL agreementBL : supplierAgreement) {
            if (agreementBL.getSupplierAgreementID() == agreementId)
                return agreementBL;
        }
        return null;
    }

    public Integer getPhoneNumber(){
        Integer em = employees.get(fairPhoneNumber%employees.size());
        fairPhoneNumber++;
        return em;
    }
    public Integer numbersOfEmployees(){
        return employees.size();
    }
    public void addEmployee(Integer employee) {
        for (int i=0; i<employees.size();i++)
        {
            if (employees.get(i).equals(employee))
                throw new RuntimeException("employee ID is already exist");
        }
        employees.add(employee);
    }
    public boolean removeEmployee(Integer employeeID){
        for (Integer employee: employees) {
            if (employee.equals(employeeID)) {
                employees.remove(employee);
                return true;
            }
        }
        throw new RuntimeException("this employee ID doesn't exist in this supplier");
    }
    public SupplierAgreementBL getAgreements(Integer agreementID){
        for(int i=0; i<supplierAgreement.size(); i++ ){
            if(supplierAgreement.get(i).getSupplierAgreementID()==agreementID)
                return supplierAgreement.get(i);
        }
        return null;
    }
    public boolean addDiscount(Integer supplierAgreementID, Integer item, Integer count, Integer discount){

        for(int i=0; i<supplierAgreement.size(); i++){
            if(supplierAgreement.get(i).getSupplierAgreementID().equals(supplierAgreementID)) {
                supplierAgreement.get(i).addDiscount(item, count, discount);
                return true;
            }
        }
        throw new RuntimeException("this agreement ID doesn't exist in this supplier");
    }

    public String getSupplierName() {
        return supplierName;
    }
    public boolean validOrder( Integer supplierAgreementID, Map<Integer, Integer> itemsQuantity){
        for(int i=0; i<supplierAgreement.size(); i++){
            if(supplierAgreement.get(i).getSupplierAgreementID().equals(supplierAgreementID))
                return supplierAgreement.get(i).validOrderAgreement(itemsQuantity);
        }
        throw new RuntimeException("this agreement doesn't exist in this supplier");


    }
    public Map<Integer, Double> setPrices(Integer supplierAgreementID, Map<Integer,Integer> itemsQuantity)
    {
        Map<Integer,Double> setPrices = new HashMap<>();
        for(int i=0; i<supplierAgreement.size(); i++){
            if(supplierAgreement.get(i).getSupplierAgreementID().equals(supplierAgreementID)) {
                setPrices = supplierAgreement.get(supplierAgreementID).setPrices(itemsQuantity);
                return setPrices;
            }
        }
        throw new RuntimeException("this agreement is not exist at this supplier");
    }
    public Integer createAgreement(String paymentMethodAgreement, Map<ProductBL, Double> itemPrice, String paymentTime){
        Map<Integer, Double> itemIDPrice=new HashMap<>();
        for(ProductBL item: itemPrice.keySet()){
            if(!companies.contains(item.getProducer()))
                throw new RuntimeException("cannot add item that the supplier doesn't have his producer");
            itemIDPrice.put(item.getItemID(),itemPrice.get(item));

        }
        this.supplierAgreement.add(new SupplierAgreementBL(supplierID,paymentMethodAgreement,countSupplierAgreement,itemIDPrice, paymentTime));
        countSupplierAgreement++;
        return countSupplierAgreement-1;
    }
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getSupplierID() {
        return supplierID;
    }
    public Set<Integer> getCatalogicNumber(){
        Set<Integer> mainset = new HashSet<>();
        Set<Integer> mergedSet;
        for(int i=0; i<supplierAgreement.size(); i++) {
            mergedSet=supplierAgreement.get(i).getCatalogicNumber();
            mainset=mergeCatalogicNumbers(mainset,mergedSet);
        }
        return mainset;
    }
    public Set<Integer> getItemsNames(){
        Set<Integer> mainset = new HashSet<>();
        Set<Integer> mergedSet;
        for(int i=0; i<supplierAgreement.size(); i++) {
            mergedSet=supplierAgreement.get(i).getItemsName();
            mainset=mergeItemsName(mainset,mergedSet);
        }
        return mainset;
    }
    public Set<Integer> mergeCatalogicNumbers(Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> mergedSet = new HashSet<>(set1);
        mergedSet.addAll(set2);
        return mergedSet;
    }
    public Set<Integer> mergeItemsName(Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> mergedSet = new HashSet<>(set1);
        mergedSet.addAll(set2);
        return mergedSet;
    }

    public void setSupplierID(Integer supplierID) {
        this.supplierID = supplierID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(Integer bankAccount) {
        this.bankAccount = bankAccount;
    }


    public List<Integer> getEmployees() {
        return employees;
    }
    public Boolean getIsTransport() {
        return isTransport;
    }

    public void setIsTransport(Boolean isTransport) {
        this.isTransport = isTransport;
    }

    public Boolean getIsDays() {
        return isDays;
    }

    public void setIsDays(Boolean isDays) {
        this.isDays = isDays;
    }

    public String[] getDays() {
        return days;
    }

    public void setDays(String[] days) {
        this.days = days;
    }
    public void addCompamy(String company)
    {
        companies.add(company);
    }
    public void removeCompamy(String company)
    {
        companies.remove(company);
    }
    public List<String> getCompanies()
    {
        return this.companies;
    }
    public List<SupplierAgreementBL> getSupplierAgreement()
    {
        return this.supplierAgreement;
    }
    public boolean removeAgreement(Integer agrementID){

        for(int i=0; i<supplierAgreement.size(); i++) {
            if (supplierAgreement.get(i).getSupplierAgreementID().equals(agrementID)) {
                supplierAgreement.remove(supplierAgreement.get(i));
                return true;
            }
        }
        throw new RuntimeException("agrementID doesnt exist");
    }
    public void removeItem(Integer agrementID,Integer item) {
        boolean isExist=false;
        for(int i=0; i<supplierAgreement.size(); i++){
            if(supplierAgreement.get(i).getSupplierAgreementID().equals(agrementID)) {
                supplierAgreement.get(i).removeItem(item);
                isExist = true;
            }
        }
        if(isExist==false)
            throw new RuntimeException("this agreement id doesnt exist in this supplier");

    }
        public void addItem(Integer agrementID,Integer item, Double price) {
        boolean isExist=false;
        for(int i=0; i<supplierAgreement.size(); i++){
                if(supplierAgreement.get(i).getSupplierAgreementID().equals(agrementID)) {
                    supplierAgreement.get(i).addItem(item, price);
                    isExist = true;
                }
            }
        if(isExist==false)
            throw new RuntimeException("this agreement id doesnt exist in this supplier");

        }
        public void removeDiscount(Integer agrementID, Integer item, Integer count, Integer discount) {
            boolean isExist=false;
            for(int i=0; i<supplierAgreement.size(); i++){
                if(supplierAgreement.get(i).getSupplierAgreementID().equals(agrementID)) {
                    supplierAgreement.get(i).removeDiscount(item, count, discount);
                    isExist = true;
                }
            }
            if(isExist==false)
                throw new RuntimeException("this agreement id doesnt exist in this supplier");
        }
        public void setPaymentTime(Integer agrementID, String paymentTime){
            boolean isExist=false;
            for(int i=0; i<supplierAgreement.size(); i++){
                if(supplierAgreement.get(i).getSupplierAgreementID().equals(agrementID)) {
                    supplierAgreement.get(i).setPaymentTime(paymentTime);
                    isExist = true;
                }
            }
            if(isExist==false)
                throw new RuntimeException("this agreement id doesnt exist in this supplier");
        }
        public void setPaymentMethod(Integer agrementID, String paymentMethod){
        boolean isExist=false;
        for(int i=0; i<supplierAgreement.size(); i++){
                if(supplierAgreement.get(i).getSupplierAgreementID().equals(agrementID)) {
                    supplierAgreement.get(i).setPaymentMethod(paymentMethod);
                    isExist = true;
                }
            }
            if(isExist == false)
                throw new RuntimeException("this agreement id doesnt exist in this supplier");
        }

        public SupplierAgreementBL getSupplierAgreement(Integer agreementID){
            for(int i=0; i<supplierAgreement.size(); i++){
                if(supplierAgreement.get(i).getSupplierAgreementID().equals(agreementID)) {
                    return supplierAgreement.get(i);
                }
            }
            return null;
        }

        public Map<Integer,Double> getPrices(Map<Integer,Integer> products) {
            
            Boolean isFirst = true;
            Map<Integer,Double> setFinal = new HashMap<>();
            Map<Integer,Double> setItemPrices = new HashMap<>();
            for (int i=0; i<supplierAgreement.size(); i++)
            {
                if (isFirst)
                {
                    setFinal = supplierAgreement.get(i).setPrices(products);
                    isFirst = false;
                }
                else
                {
                    setItemPrices = supplierAgreement.get(i).setPrices(products);
                    for (Integer product : setFinal.keySet())
                    {
                        Double price = setFinal.get(product);
                        if (price == null)
                        {
                            setFinal.replace(product,setItemPrices.get(product));
                        }
                        else {
                            if (setItemPrices.get(product) != null && price > setItemPrices.get(product))
                                setFinal.replace(product,setItemPrices.get(product));
                        }
                    }

                }

            }
            return setFinal;
    }
    
    private List<Integer> convertDaysToInts() {
        List<Integer> dayInts = new ArrayList<>();
        for (String day : days) {
            switch (day.toLowerCase()) {
                case "sunday":
                    dayInts.add(1);
                    break;
                case "monday":
                    dayInts.add(2);
                    break;
                case "tuesday":
                    dayInts.add(3);
                    break;
                case "wednesday":
                    dayInts.add(4);
                    break;
                case "thursday":
                    dayInts.add(5);
                    break;
                case "friday":
                    dayInts.add(6);
                    break;
                case "saturday":
                    dayInts.add(7);
                    break;
            }
        }
        return dayInts;
    }


        public Integer getDay(Integer today)
        {
            if (isDays==false)
                return today;

            List<Integer> daysInt = convertDaysToInts();
                for (int i=0; i<=6; i++)
                {
                    if (daysInt.contains(today))
                        return today;
                    today=(today+1)%7;
                }
            return today;
        }
        public Boolean isDayExist(String day){
            if(!isDays)
                return true;
            else {
                for (int i = 0; i<days.length; i++){
                    if(days[i].equals(day))
                        return true;
                }
            }
            return false;
        }
        public SupplierDto toDto() {
            return new SupplierDto(
                    supplierName,
                    supplierID,
                    address,
                    bankAccount,
                    isTransport,
                    isDays,
                    isDayExist("sunday"),
                isDayExist("monday"),
                isDayExist("tuesday"),
                isDayExist("wednesday"),
                isDayExist("thursday"),
                isDayExist("friday"),
                isDayExist("saturday"),
                    countSupplierAgreement,
                    fairPhoneNumber


            );
        }
}
