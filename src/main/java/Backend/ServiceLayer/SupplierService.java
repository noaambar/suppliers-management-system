package Backend.ServiceLayer;
import Backend.BusinessLayer.SupplierFacade;

import java.util.ArrayList;
import java.util.Map;

public class SupplierService {
    private SupplierFacade supplierFacade;

    public SupplierService() {
        supplierFacade = SupplierFacade.getInstance();
    }

    public SupplierService(SupplierFacade supplierFacade) {
        this.supplierFacade = supplierFacade;
    }

    public String createOrder(Integer supplierID, Integer supplierAgreementID, Map<Integer, Integer> itemsQuantity, String branch) {
        try {
            Integer s = supplierFacade.createOrder(supplierID, supplierAgreementID, itemsQuantity, branch);
            Response<String> response = new Response<>("Your orderID is " + s, null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String addEmployeeStart(Integer employeeID, String employeeName, String phoneNumber) {
        try {
            supplierFacade.addEmployeeStart(employeeID, employeeName, phoneNumber);
            Response<String> response = new Response<>("employee added", null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String orderDone(Integer supplierId, Integer orderId) {
        try {
            supplierFacade.orderDone(supplierId, orderId);
            Response<String> response = new Response<>("Order is done", null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String getCatalogicNumber(Integer supplierID) {
        try {
            String catalogicNumber = supplierFacade.getCatalogicNumber(supplierID);
            Response<String> response = new Response<>("catalogic Numbers"+"\n"+catalogicNumber, null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String addDiscount(Integer supplierID, Integer supplierAgreementID, Integer itemId, Integer count, Integer discount) {
        try {
            supplierFacade.addDiscount(supplierID, supplierAgreementID, itemId, count, discount);
            Response<String> response = new Response<>("the discount added successfully", null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String createSupplier(String supplierName, Integer supplierID, String address, Integer bankAccount,
                                 ArrayList<Integer> employees,
                                 Boolean isTransport, Boolean isDays, String[] days, ArrayList<String> componies) {
        try {
            supplierFacade.createSupplier(supplierName, supplierID, address, bankAccount, employees, isTransport, isDays, days, componies);
            Response<String> response = new Response<>("Supplier created successfully", null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String removeSupplier(Integer supplierID) {
        try {
            supplierFacade.removeSupplier(supplierID);
            Response<String> response = new Response<>("Supplier removed successfully", null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String removeEmployee(Integer supplierID, Integer employeeID) {
        try {
            supplierFacade.removeEmployee(supplierID, employeeID);
            Response<String> response = new Response<>("Employee removed successfully", null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String supplierExists(Integer supplierID) {
        try {
            String isExist = Boolean.toString(supplierFacade.supplierExists((supplierID)));
            Response<String> response = new Response<>(isExist, null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String createAgreement(Integer supplierID, String paymentMethod, Map<Integer, Double> itemPriceId, String paymentTime) {
        try {
            String s = Integer.toString(supplierFacade.createAgreement(supplierID, paymentMethod, itemPriceId, paymentTime));
            Response<String> response = new Response<>("supplierAgreementID is " + s, null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String addEmployee(Integer supplierID, Integer employeeID, String employeeName, String phoneNumber) {
        try {
            supplierFacade.addEmployee(supplierID, employeeID, employeeName, phoneNumber);
            Response<String> response = new Response<>("the employee added successfully", null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String employeeExists(Integer employeeID) {
        try {
            String isExist = Boolean.toString(supplierFacade.employeeExists(employeeID));
            Response<String> response = new Response<>(isExist, null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String itemExists(Integer itemID) {
        try {
            String isExist = Boolean.toString(supplierFacade.itemExists(itemID));
            Response<String> response = new Response<>(isExist, null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String addItem(Integer itemID, String itemName, String unit, String producer, Integer amount) {
        try {
            supplierFacade.addProduct(itemID, itemName, unit, producer, amount);
            Response<String> response = new Response<>("the item added successfully", null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String getItemsNames(Integer supplierID) {
        try {
            String answer = supplierFacade.getItemsNames(supplierID);
            Response<String> response = new Response<>(answer, null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String removeAgreement(Integer supplierID, Integer agreementID) {
        try {
            supplierFacade.removeAgreement(supplierID, agreementID);
            Response<String> response = new Response<>("the agreement removed successfully", null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String removeItem(Integer supplierID, Integer agreementID, Integer item) {
        try {
            supplierFacade.removeProduct(supplierID, agreementID, item);
            Response<String> response = new Response<>("the item removed successfully", null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String addItemToSupplier(Integer supplierID, Integer agreementID, Integer item, Double price) {
        try {
            supplierFacade.addItemToSupplier(supplierID, agreementID, item, price);
            Response<String> response = new Response<>("the item is added successfully", null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String removeDiscount(Integer supplierID, Integer agreementID, Integer item, Integer count, Integer discount) {
        try {
            supplierFacade.removeDiscount(supplierID, agreementID, item, count, discount);
            Response<String> response = new Response<>("the discount removed successfully", null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String setPaymentTime(Integer supplierID, Integer agreementID, String paymentTime) {
        try {
            supplierFacade.setPaymentTime(supplierID, agreementID, paymentTime);
            Response<String> response = new Response<>("the payment system changed successfully", null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public String setPaymentMethod(Integer supplierID, Integer agreementID, String paymentMethod) {
        try {
            supplierFacade.setPaymentMethod(supplierID, agreementID, paymentMethod);
            Response<String> response = new Response<>("the payment method changed successfully", null);
            return response.toString();
        } catch (Exception e) {
            Response<String> response = new Response<>(null, e.getMessage());
            return response.toString();
        }
    }

    public void loadData() {
        try {
            supplierFacade.loadData();
        } catch (Exception e) {
            throw new RuntimeException("Error loading data: " + e.getMessage(), e);
        }
    }

    public void deleteData() {
        try {
            supplierFacade.deleteData();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting data: " + e.getMessage(), e);
        }
    }
}
