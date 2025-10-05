package Backend.BusinessLayer;

import DataTransferLayer.SupplierAgreementDto;

import java.util.*;

public class SupplierAgreementBL {
    private Integer SupplierAgreementID;
    private Integer supplierID;
    private Map<Integer, Double> itemPrice;
    private Map<Integer, Map<Integer, Integer>> quantityDiscountAgreement;
    private String paymentMethod;
    private String paymentTime;


    public SupplierAgreementBL(Integer supplierID, String paymentMethod, Integer SupplierAgreementID, Map<Integer, Double> itemPrice, String paymentTime) {
        this.supplierID = supplierID;
        this.quantityDiscountAgreement = new HashMap<>();
        this.paymentMethod=paymentMethod;
        this.SupplierAgreementID=SupplierAgreementID;
        this.itemPrice=itemPrice;
        this.paymentTime=paymentTime;
        addItemToDiscount();
    }
    public SupplierAgreementBL(SupplierAgreementDto supplierAgreementDto) {
        this.supplierID = supplierAgreementDto.getSupplierID();
        this.paymentMethod=supplierAgreementDto.getPaymentMethod();
        this.SupplierAgreementID=supplierAgreementDto.getSupplierAgreementID();
        this.paymentTime=supplierAgreementDto.getPaymentTime();
        addItemToDiscount();
    }
    public void setItemPrices(Map<Integer, Double> itemPrice){
        this.itemPrice=itemPrice;
        addItemToDiscount();
    }
    public void setQuantityDiscountAgreement(Map<Integer, Map<Integer, Integer>> quantityDiscountAgreement){
        this.quantityDiscountAgreement=quantityDiscountAgreement;
    }
    public void setPaymentTime(String paymentTime){
        this.paymentTime=paymentTime;
    }
    public void setPaymentMethod(String paymentMethod){
        this.paymentMethod=paymentMethod;
    }
    public void addDiscount(Integer item, Integer count, Integer discount) {
        if(quantityDiscountAgreement.containsKey(item)){
            if(quantityDiscountAgreement.get(item).containsKey(count)){
                quantityDiscountAgreement.get(item).remove(count);
                quantityDiscountAgreement.get(item).put(count, discount);
            }
            else
                quantityDiscountAgreement.get(item).put(count, discount);
        }
        else
            throw new RuntimeException("this item dosent exist in the agreement");
    }
    public void removeDiscount(Integer item, Integer count, Integer discount) {
        if(quantityDiscountAgreement.containsKey(item)){
            if(quantityDiscountAgreement.get(item).containsKey(count)){
                quantityDiscountAgreement.get(item).remove(count);
            }
            else
                throw new RuntimeException("this discount of item is not exist in the agreement");
        }
        else
            throw new RuntimeException("this item is already not in the agreement");

    }

    public void addItemToDiscount()
    {
        for (Integer item: itemPrice.keySet())
            quantityDiscountAgreement.put(item,new HashMap<>());
    }
    public void addItem(Integer item, Double price) {
        if (itemPrice.containsKey(item)) {
            if(itemPrice.get(item).equals(price))
                throw  new RuntimeException("this agreement already have this item and price");
            itemPrice.replace(item, price);
        } else {
            itemPrice.put(item, price);
            quantityDiscountAgreement.put(item,new HashMap<>());
        }
    }
    public void removeItem(Integer item) {
        if (itemPrice.containsKey(item)) {
            if(itemPrice.size()==1)
                throw new RuntimeException("this agreement have only one item-cannot remove it");
            itemPrice.remove(item);
            quantityDiscountAgreement.remove(item);

        }
        else
            throw  new RuntimeException("this agreement doesnt have this item");
    }
    public  Map<Integer, Double> getItemPrice(){
        return itemPrice;
    }
    public Map<Integer, Double> setPrices(Map<Integer, Integer> itemQuantity) {
        Map<Integer, Double> updatedPrices = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : itemQuantity.entrySet()) {
            Integer item = entry.getKey();
            int quantity = entry.getValue();

            Map<Integer, Integer> discountMap = quantityDiscountAgreement.get(item);
            if(discountMap == null)
                updatedPrices.put(item, null);
            else {
                double discountedPrice = calculateTotalPrice(quantity, itemPrice.get(item), discountMap);
                updatedPrices.put(item, discountedPrice);
            }
        }

        return updatedPrices;
    }
    public double calculateTotalPrice(int totalQuantity,Double pricePerItem, Map<Integer, Integer> discountMap) {
        double[] dp = new double[totalQuantity + 1];
        Arrays.fill(dp, Double.MAX_VALUE);
        dp[0] = 0; // base case

        for (int i = 1; i <= totalQuantity; i++) {
            for (Map.Entry<Integer, Integer> entry : discountMap.entrySet()) {
                int groupSize = entry.getKey();
                double discount = entry.getValue() / 100.0;

                if (i >= groupSize && dp[i - groupSize] != Double.MAX_VALUE) {
                    double costWithThisGroup = groupSize * pricePerItem * (1 - discount);
                    dp[i] = Math.min(dp[i], dp[i - groupSize] + costWithThisGroup);
                }
            }
        }

        // Final cost = best combo for any i + full price for leftovers
        double minTotalCost = Double.MAX_VALUE;
        for (int i = 0; i <= totalQuantity; i++) {
            if (dp[i] != Double.MAX_VALUE) {
                int leftover = totalQuantity - i;
                double fullPriceForLeftover = leftover * pricePerItem;
                minTotalCost = Math.min(minTotalCost, dp[i] + fullPriceForLeftover);
            }
        }

        return minTotalCost;
    }



    public boolean validOrderAgreement(Map<Integer, Integer> itemsQuantity) {
        for (Integer item : itemsQuantity.keySet()) {
            if (!itemPrice.containsKey(item)) {
                throw new RuntimeException("there is item that doesn't exist in the agreement");
            }
        }
        return true;
    }
    public Integer getSupplierAgreementID() {
        return SupplierAgreementID;
    }
    public Integer getSupplierID() {
        return supplierID;
    }
    public Map<Integer, Map<Integer, Integer>> getQuantityDiscountAgreement() {
        return quantityDiscountAgreement;
    }

    public Set<Integer> getCatalogicNumber() {
        Set<Integer> catalogicNumber = new HashSet<>();
        for (Integer item : itemPrice.keySet()) {
            catalogicNumber.add(10000 * supplierID + item);
        }
        return catalogicNumber;
    }
    public Set<Integer> getItemsName() {
        Set<Integer> itemsName = new HashSet<>();
        for (Integer item : itemPrice.keySet()) {
            itemsName.add(item);
        }
        return itemsName;
    }

    public SupplierAgreementDto toDto() {
        return new SupplierAgreementDto(
        SupplierAgreementID,
        supplierID,
        paymentMethod,
        paymentTime
        );
    }

}
