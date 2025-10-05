package Frontend.PresentationLayer;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import Backend.ServiceLayer.SupplierService;

public class SuppliersProgram {
    SupplierService supplierService;
    CLI cli;

    public SuppliersProgram(SupplierService supplierService, CLI cli) {
        this.supplierService = supplierService;
        this.cli= cli;
    }

    public void initialDB(String jsonFilePath) {
    Gson gson = new Gson();
    Path path = Paths.get(jsonFilePath);

    try (FileReader reader = new FileReader(path.toFile())) {
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

        // Load Employees
        JsonArray employees = jsonObject.getAsJsonArray("employees");
        for (JsonElement e : employees) {
            JsonObject emp = e.getAsJsonObject();
            int id = emp.get("id").getAsInt();
            String name = emp.get("name").getAsString();
            String phone = emp.get("phone").getAsString();
            supplierService.addEmployeeStart(id, name, phone);
        }

        // Load Products
        JsonArray products = jsonObject.getAsJsonArray("products");
        for (JsonElement p : products) {
            JsonObject prod = p.getAsJsonObject();
            int id = prod.get("id").getAsInt();
            String name = prod.get("name").getAsString();
            String unit = prod.get("unit").getAsString();
            String manufacturer = prod.get("manufacturer").getAsString();
            int categoryId = prod.get("categoryId").getAsInt();
            supplierService.addItem(id, name, unit, manufacturer, categoryId);
        }

        // Load Suppliers
        JsonArray suppliers = jsonObject.getAsJsonArray("suppliers");
        for (JsonElement s : suppliers) {
            JsonObject sup = s.getAsJsonObject();
            int id = sup.get("id").getAsInt();
            String name = sup.get("name").getAsString();
            String address = sup.get("address").getAsString();
            int bankAccount = sup.get("bankAccount").getAsInt();

            // Employees array
            JsonArray empArr = sup.getAsJsonArray("employees");
            ArrayList<Integer> employeeIds = new ArrayList<>();
            for (JsonElement emp : empArr) {
                employeeIds.add(emp.getAsInt());
            }

            // Deliveries
            JsonObject deliveries = sup.getAsJsonObject("deliveries");
            String[] deliveryDays = new String[7];
            deliveryDays[0] = deliveries.get("Sunday").getAsString();
            deliveryDays[1] = deliveries.get("Monday").getAsString();
            deliveryDays[2] = deliveries.get("Tuesday").getAsString();
            deliveryDays[3] = deliveries.get("Wednesday").getAsString();
            deliveryDays[4] = deliveries.get("Thursday").getAsString();
            deliveryDays[5] = deliveries.get("Friday").getAsString();
            deliveryDays[6] = deliveries.get("Saturday").getAsString();

            // Companies Represented
            JsonArray companiesArr = sup.getAsJsonArray("companiesRepresented");
            ArrayList<String> companies = new ArrayList<>();
            for (JsonElement c : companiesArr) {
                companies.add(c.getAsString());
            }

            // Create Supplier
            supplierService.createSupplier(
                name,
                id,
                address,
                bankAccount,
                employeeIds,
                true, // isTransport
                true, // isDays
                deliveryDays,
                companies
            );
        }

        // Load Agreements
        JsonArray agreements = jsonObject.getAsJsonArray("agreements");
        for (JsonElement a : agreements) {
            JsonObject agreement = a.getAsJsonObject();
            int supplierId = agreement.get("supplierId").getAsInt();
            String paymentMethod = agreement.get("paymentMethod").getAsString();
            String paymentTime = agreement.get("paymentTime").getAsString();

            JsonObject pricesJson = agreement.getAsJsonObject("prices");
            Map<Integer, Double> prices = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : pricesJson.entrySet()) {
                prices.put(Integer.parseInt(entry.getKey()), entry.getValue().getAsDouble());
            }

            int agreementID = Integer.parseInt(
                supplierService.createAgreement(supplierId, paymentMethod, prices, paymentTime)
                    .replace("supplierAgreementID is ", "").trim());
            
            // Load discounts
            JsonArray discounts = agreement.getAsJsonArray("discounts");
            if (discounts != null) {
                for (JsonElement d : discounts) {
                    JsonObject discount = d.getAsJsonObject();
                    int productId = discount.get("productId").getAsInt();
                    int amountThreshold = discount.get("amountThreshold").getAsInt();
                    int discountPercentage = discount.get("discountPercentage").getAsInt();
                    supplierService.addDiscount(supplierId, agreementID, productId, amountThreshold, discountPercentage);
                }
            }
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}

    public boolean purchasingManagerFunc(String chooseNumber, String branch) {
        Integer tryNumber = 0;
        if (chooseNumber.equals("0")) {
            return false;
        }
        if (chooseNumber.equals("1")) {// Create order

            displayMessage("enter supplier ID");
            String supplierID = getInput();
            while (supplierID.isEmpty() || !supplierID.matches("\\d+")
                    || !(supplierService.supplierExists(Integer.parseInt(supplierID)).equals("true"))) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input. Enter supplier ID (numbers only):");
                supplierID = getInput();
            }
            displayMessage("enter Agreement ID");
            String supplierAgreementID = getInput();
            while (supplierAgreementID.isEmpty() || !supplierAgreementID.matches("\\d+")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input, please try again");
                supplierAgreementID = getInput();
            }
            displayMessage("enter item ID and then enter quantity seperated by space to finish your order enter 0"
                    + System.lineSeparator() +
                    "for example 45869 4");
            Map<Integer, Integer> listOfProducts = new HashMap<Integer, Integer>();
            String product = getInput();
            while (product.equals("0")) {
                displayMessage("cannot make an order without items, please try again");
                product = getInput();
            }
            String[] parts;
            while (!product.equals("0")) {
                if (!product.matches("\\d+ \\d+")) {
                    tryNumber++;
                    if (tryNumber.equals(3)) {
                        displayMessage(
                                "You have entered invalid values three times in this action – exiting the action");
                        return true;
                    }
                    displayMessage("Invalid input, please try again");
                    product = getInput();
                } else {
                    parts = product.split(" ");
                    listOfProducts.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                    product = getInput();
                }
            }
            displayMessage(supplierService.createOrder(Integer.parseInt(supplierID),
                    Integer.parseInt(supplierAgreementID), listOfProducts, branch));
        }
        if (chooseNumber.equals("2")) { // Get catalog number of items
            displayMessage("enter supplier ID");
            String supplierID = getInput();
            while (supplierID.isEmpty() || !supplierID.matches("\\d+")
                    || !(supplierService.supplierExists(Integer.parseInt(supplierID)).equals("true"))) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input. Enter supplier ID (numbers only):");
                supplierID = getInput();
            }
            displayMessage(supplierService.getCatalogicNumber(Integer.parseInt(supplierID)));
        }
        if (chooseNumber.equals("3")) { // Report order is done
            displayMessage("enter supplier ID");
            String supplierID = getInput();
            while (supplierID.isEmpty() || !supplierID.matches("\\d+")
                    || !(supplierService.supplierExists(Integer.parseInt(supplierID)).equals("true"))) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input. Enter supplier ID (numbers only):");
                supplierID = getInput();
            }
            displayMessage("enter order ID");
            String orderID = getInput();
            while (orderID.isEmpty() || !orderID.matches("\\d+")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input. Enter supplier ID (numbers only):");
                orderID = getInput();
            }
            displayMessage(supplierService.orderDone(Integer.parseInt(supplierID), Integer.parseInt(orderID)));

        }
        if (chooseNumber.equals("4")) { // Add item
            displayMessage("enter item ID");
            String itemID = getInput();
            while (itemID.isEmpty() || !itemID.matches("\\d+")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input. Enter item ID (numbers only):");
                itemID = getInput();
            }
            displayMessage("enter item's Name");
            String itemName = getInput();
            displayMessage("enter item's unit");
            String unit = getInput();
            displayMessage("enter item's producer");
            String producer = getInput();
            displayMessage("enter item's amount");
            String amount = getInput();
            while (amount.isEmpty() || !amount.matches("\\d+")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input. Enter amount (numbers only):");
                amount = getInput();
            }
            displayMessage(supplierService.addItem(Integer.parseInt(itemID), itemName, unit, producer,
                    Integer.parseInt(amount)));
        }
        if (chooseNumber.equals("5")) {
            displayMessage("enter supplier ID");
            String supplierID = getInput();
            while (supplierID.isEmpty() || !supplierID.matches("\\d+")
                    || !(supplierService.supplierExists(Integer.parseInt(supplierID)).equals("true"))) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input. Enter supplier ID (numbers only):");
                supplierID = getInput();
            }
            displayMessage(supplierService.getItemsNames(Integer.parseInt(supplierID)));
        }
        return true;

    }

    public boolean supplierFunc(String chooseNumber) {
        Integer tryNumber = 0;
        if (chooseNumber.equals("0")) {
            return false;
        }
        if (chooseNumber.equals("1")) { // Add new supplier
            displayMessage("Enter supplier name:");
            String supplierName = getInput();
            while (!supplierName.matches("[a-zA-Z]+")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid Name. Try again:");
                supplierName = getInput();
            }
            displayMessage("Enter supplier ID (numbers only):");
            String supplierIDStr = getInput();
            while (!supplierIDStr.matches("\\d+")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid ID. Try again:");
                supplierIDStr = getInput();
            }
            int supplierID = Integer.parseInt(supplierIDStr);

            displayMessage("Enter address:");
            String address = getInput();
            while (!address.matches("[\\w\\s]+")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid address. Try again:");
                address = getInput();
            }
            displayMessage("Enter bank account number(at least 6 numbers):");
            String bankAccountStr = getInput();
            while (!bankAccountStr.matches("\\d+") || bankAccountStr.length() < 6) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid bank account. Try again:");
                bankAccountStr = getInput();
            }
            int bankAccount = Integer.parseInt(bankAccountStr);

            // Companies
            ArrayList<String> companies = new ArrayList<>();
            displayMessage("Enter companies represented (type 'done' to finish):");
            String c = getInput();
            while (c.equalsIgnoreCase("done") || !c.matches("\\w+")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("cannot create supplier with no companies");
                c = getInput();
            }
            while (!c.equalsIgnoreCase("done")) {
                if (!c.matches("\\w+")) {
                    tryNumber++;
                    if (tryNumber.equals(3)) {
                        displayMessage(
                                "You have entered invalid values three times in this action – exiting the action");
                        return true;
                    }
                    displayMessage("Invalid company. Try again:");
                    c = getInput();
                } else {
                    companies.add(c);
                    c = getInput();
                }
            }

            // Transport
            displayMessage("Does the supplier transport the items? (true/false):");
            String isTransportS = getInput();
            while (!isTransportS.equals("true") && !isTransportS.equals("false")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("true false only. Try again:");
                isTransportS = getInput();
            }
            Boolean isTransport = Boolean.parseBoolean(isTransportS);
            // Supply Days
            displayMessage("Does the supplier have fixed supply days? (true/false):");
            String isDaysS = getInput();
            while (!isDaysS.equals("true") && !isDaysS.equals("false")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("true false only. Try again:");
                isDaysS = getInput();
            }
            Boolean isDays = Boolean.parseBoolean(isDaysS);
            String[] days = new String[0];
            if (isDays) {
                displayMessage("Enter days of supply (e.g., sunday,monday...) type 'done' to finish:");
                ArrayList<String> daysList = new ArrayList<>();
                String d = getInput().trim();
                if (d.equals("done")) {
                    displayMessage("changing to: supplier doesn't have fixed days");
                    isDays = false;
                }
                while (!d.equalsIgnoreCase("done")) {
                    if ((d.equals("sunday") || d.equals("monday") || d.equals("tuesday") ||
                            d.equals("wednesday") || d.equals("thursday") || d.equals("friday") ||
                            d.equals("saturday")) && !daysList.contains(d)) {
                        daysList.add(d);
                        d = getInput();
                    } else {
                        tryNumber++;
                        if (tryNumber.equals(3)) {
                            displayMessage(
                                    "You have entered invalid values three times in this action – exiting the action");
                            return true;
                        }
                        displayMessage("Invalid day. Try again:");
                        d = getInput();
                    }
                }
                if (daysList.size() == 7) {
                    displayMessage("you wrote all days, changing to supllier dosen't have fixed days");
                    isDays = false;
                } else
                    days = daysList.toArray(new String[0]);
            }
            displayMessage("enter employee ID to finish your order enter 0");
            ArrayList<Integer> employees = new ArrayList<>();
            String employee = getInput();
            while (employee.equals("0") || !employee.matches("\\d+")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("cannot create supplier without employees, please try again");
                employee = getInput();
            }
            while (!employee.equals("0")) {
                if (!employee.matches("\\d+")) {
                    tryNumber++;
                    if (tryNumber.equals(3)) {
                        displayMessage(
                                "You have entered invalid values three times in this action – exiting the action");
                        return true;
                    }
                    displayMessage("Invalid input, please try again");
                    employee = getInput();
                } else {
                    employees.add(Integer.parseInt(employee));
                    employee = getInput();
                }
            }
            displayMessage(supplierService.createSupplier(supplierName, supplierID, address, bankAccount,
                    employees, isTransport, isDays, days, companies));
        }
        if (chooseNumber.equals("2")) { // Add agreement
            displayMessage("Enter supplier ID:");
            String supplierID = getInput();
            while (supplierID.isEmpty() || !supplierID.matches("\\d+")
                    || !(supplierService.supplierExists(Integer.parseInt(supplierID)).equals("true"))) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid ID. Try again:");
                supplierID = getInput();
            }
            int id = Integer.parseInt(supplierID);
            displayMessage("Enter payment method(cash, checks, credit cards, debit cards, bank transfers):");

            String paymentSystem = getInput();
            while (!(paymentSystem.equals("cash") || paymentSystem.equals("checks")
                    || paymentSystem.equals("credit cards") ||
                    paymentSystem.equals("debit cards") || paymentSystem.equals("bank transfers"))) {
                displayMessage("Invalid payment method. Try again:");
                paymentSystem = getInput();
            }

            displayMessage("enter item ID and then enter price seperated by space to finish your order enter 0");
            Map<Integer, Double> itemsPrice = new HashMap<Integer, Double>();
            String item = getInput();

            while (!item.matches("\\d+ \\d+") && !item.matches("\\d+ \\d+.\\d+")) {

                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("cannot make an order without items, please try again");
                item = getInput();
            }
            String[] parts;
            while (!item.equals("0")) {
                if (!item.matches("\\d+ \\d+") && !item.matches("\\d+ \\d+.\\d+")) {
                    tryNumber++;
                    if (tryNumber.equals(3)) {
                        displayMessage(
                                "You have entered invalid values three times in this action – exiting the action");
                        return true;
                    }
                    displayMessage("Invalid input, please try again");
                    item = getInput();
                } else {
                    parts = item.split(" ");
                    itemsPrice.put(Integer.parseInt(parts[0]), Double.parseDouble(parts[1]));
                    item = getInput();
                }
            }
            displayMessage(
                    "Enter payment Time(a - in advance, b - when getting the delivery, c - in the beginning of the month , d - end of the month):");
            String paymentTime = getInput();
            while (!(paymentTime.equals("a") || paymentTime.equals("b") || paymentTime.equals("c") ||
                    paymentTime.equals("d"))) {
                displayMessage("Invalid payment method. Try again:");
                paymentTime = getInput();
            }
            if (paymentTime.equals("a")) {
                paymentTime = "in advance";
            } else if (paymentTime.equals("b")) {
                paymentTime = "when getting the delivery";

            } else if (paymentTime.equals("c")) {
                paymentTime = "in the beginning of the month";
            } else {
                paymentTime = "end of the month";
            }
            displayMessage(supplierService.createAgreement(id, paymentSystem, itemsPrice, paymentTime));
        }
        if (chooseNumber.equals("3")) {// Edit agreement
            displayMessage("Enter supplier ID:");
            String supplierID = getInput();
            while (supplierID.isEmpty() || !supplierID.matches("\\d+")
                    || !(supplierService.supplierExists(Integer.parseInt(supplierID)).equals("true"))) {
                displayMessage("Invalid input. Enter numeric supplier ID:");
                supplierID = getInput();
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
            }
            displayMessage("Enter agreement ID:");
            String agreementID = getInput();
            while (agreementID.isEmpty() || !agreementID.matches("\\d+")) {
                displayMessage("Invalid input. Enter numeric agreement ID:");
                agreementID = getInput();
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
            }
            displayMessage("Choose action" + System.lineSeparator() + "1.Add discount" + System.lineSeparator()
                    + "2.remove discount " + System.lineSeparator() +
                    "3.Add item" + System.lineSeparator() + "4.Remove item" + System.lineSeparator()
                    + "5.Edit payment method" + System.lineSeparator() + "6.Edit payment time");
            chooseNumber = getInput();
            String valid_chars = "123456";
            while (chooseNumber.isEmpty() | !valid_chars.contains(chooseNumber)) {
                chooseNumber = getInput();
            }
            if (chooseNumber.equals("1")) { // add discount

                displayMessage("Enter item ID:");
                String itemID = getInput();
                while (itemID.isEmpty() || !itemID.matches("\\d+")) {
                    displayMessage("Invalid input. Enter numeric item ID:");
                    itemID = getInput();
                    tryNumber++;
                    if (tryNumber.equals(3)) {
                        displayMessage(
                                "You have entered invalid values three times in this action – exiting the action");
                        return true;
                    }
                }

                displayMessage(
                        "Enter quantity and discount (e.g., '5 10' means 10% off for 5 items). Type 0 to finish:");

                String input = getInput();
                String result = "";
                int quantity = 0;
                int discount = 0;
                while (!input.equals("0")) {
                    if (!input.matches("\\d+ \\d+")) {
                        displayMessage("Invalid input. Try again:");
                        input = getInput();
                    } else {
                        String[] parts = input.split(" ");
                        quantity = Integer.parseInt(parts[0]);
                        discount = Integer.parseInt(parts[1]);

                    }
                    input = getInput();

                }
                result = supplierService.addDiscount(
                        Integer.parseInt(supplierID),
                        Integer.parseInt(agreementID),
                        Integer.parseInt(itemID),
                        quantity,
                        discount);
                displayMessage(result);
            }
            if (chooseNumber.equals("2")) { // remove discount
                displayMessage("Enter item ID:");
                String itemID = getInput();
                while (itemID.isEmpty() || !itemID.matches("\\d+")) {
                    displayMessage("Invalid input. Enter numeric item ID:");
                    itemID = getInput();
                    tryNumber++;
                    if (tryNumber.equals(3)) {
                        displayMessage(
                                "You have entered invalid values three times in this action – exiting the action");
                        return true;
                    }
                }
                displayMessage(
                        "Enter quantity and discount (e.g., '5 10' means 10% off for 5 items). Type 0 to finish:");

                String input = getInput();
                int quantity = 0;
                int discount = 0;
                while (!input.equals("0")) {
                    if (!input.matches("\\d+ \\d+")) {
                        displayMessage("Invalid input. Try again:");
                    } else {
                        String[] parts = input.split(" ");
                        quantity = Integer.parseInt(parts[0]);
                        discount = Integer.parseInt(parts[1]);

                    }
                    input = getInput();

                }
                String result = supplierService.removeDiscount(
                        Integer.parseInt(supplierID),
                        Integer.parseInt(agreementID),
                        Integer.parseInt(itemID),
                        quantity,
                        discount);
                displayMessage(result);
            }
            if (chooseNumber.equals("3")) { // add item
                displayMessage("Enter item ID:");
                String itemID = getInput();
                while (itemID.isEmpty() || !itemID.matches("\\d+")) {
                    displayMessage("Invalid input. Enter numeric item ID:");
                    itemID = getInput();
                    tryNumber++;
                    if (tryNumber.equals(3)) {
                        displayMessage(
                                "You have entered invalid values three times in this action – exiting the action");
                        return true;
                    }
                }
                displayMessage("Enter price:");
                String price = getInput();
                while (price.isEmpty() || (!price.matches("\\d+") && !price.matches("\\d+.\\d+"))) {
                    displayMessage("Invalid input. Enter numeric item ID:");
                    price = getInput();
                }
                String result = supplierService.addItemToSupplier(Integer.parseInt(supplierID),
                        Integer.parseInt(agreementID), Integer.parseInt(itemID), Double.parseDouble(price));
                displayMessage(result);
            }
            if (chooseNumber.equals("4")) { // remove item
                displayMessage("Enter item ID:");
                String itemID = getInput();
                while (itemID.isEmpty() || !itemID.matches("\\d+")) {
                    displayMessage("Invalid input. Enter numeric item ID:");
                    itemID = getInput();
                    tryNumber++;
                    if (tryNumber.equals(3)) {
                        displayMessage(
                                "You have entered invalid values three times in this action – exiting the action");
                        return true;
                    }
                }

                String result = supplierService.removeItem(Integer.parseInt(supplierID), Integer.parseInt(agreementID),
                        Integer.parseInt(itemID));
                displayMessage(result);
            }
            if (chooseNumber.equals("5")) { // edit payment method
                displayMessage("Enter payment method(cash, checks, credit cards, debit cards, bank transfers):");
                String paymentSystem = getInput();
                while (!(paymentSystem.equals("cash") || paymentSystem.equals("checks")
                        || paymentSystem.equals("credit cards") ||
                        paymentSystem.equals("debit cards") || paymentSystem.equals("bank transfers"))) {
                    displayMessage("Invalid payment method. Try again:");
                    paymentSystem = getInput();
                    tryNumber++;
                    if (tryNumber.equals(3)) {
                        displayMessage(
                                "You have entered invalid values three times in this action – exiting the action");
                        return true;
                    }
                }
                String result = supplierService.setPaymentMethod(Integer.parseInt(supplierID),
                        Integer.parseInt(agreementID), paymentSystem);
                displayMessage(result);
            }
            if (chooseNumber.equals("6")) { // edit payment time
                displayMessage(
                        "Enter payment Time(a - in advance, b - when getting the delivery, c - in the beginning of the month , d - end of the month):");
                String paymentTime = getInput();
                while (!(paymentTime.equals("a") || paymentTime.equals("b") || paymentTime.equals("c") ||
                        paymentTime.equals("d"))) {
                    displayMessage("Invalid payment method. Try again:");
                    paymentTime = getInput();
                    tryNumber++;
                    if (tryNumber.equals(3)) {
                        displayMessage(
                                "You have entered invalid values three times in this action – exiting the action");
                        return true;
                    }
                }
                if (paymentTime.equals("A")) {
                    paymentTime = "in advance";
                } else if (paymentTime.equals("B")) {
                    paymentTime = "when getting the delivery";

                } else if (paymentTime.equals("C")) {
                    paymentTime = "in the beginning of the month";
                } else {
                    paymentTime = "end of the month";
                }
                String result = supplierService.setPaymentTime(Integer.parseInt(supplierID),
                        Integer.parseInt(agreementID), paymentTime);
                displayMessage(result);
            }
            return true;
        }
        if (chooseNumber.equals("4")) { // Add employee
            displayMessage("Enter supplier ID:");
            String supplierID = getInput();
            while (supplierID.isEmpty() || !supplierID.matches("\\d+")
                    || !(supplierService.supplierExists(Integer.parseInt(supplierID)).equals("true"))) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input. Enter numeric supplier ID:");
                supplierID = getInput();
            }
            displayMessage("Enter employee ID:");
            String employeeID = getInput();
            while (supplierID.isEmpty() || !supplierID.matches("\\d+")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input. Enter numeric supplier ID:");
                employeeID = getInput();
            }
            displayMessage("Enter employee name:");
            String employeeName = getInput();
            while (employeeName.isEmpty() || !employeeName.matches("[A-Za-z]+")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input:");
                employeeName = getInput();
            }

            displayMessage("Enter phone number:");
            String phoneNumber = getInput();
            while (phoneNumber.isEmpty() || !phoneNumber.matches("\\d{3}-\\d{7}")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input:");
                phoneNumber = getInput();
            }
            displayMessage(supplierService.addEmployee(Integer.parseInt(supplierID), Integer.parseInt(employeeID),
                    employeeName, phoneNumber));
        }
        if (chooseNumber.equals("5")) { // Remove employee
            displayMessage("Enter supplier ID:");
            String supplierID = getInput();
            while (supplierID.isEmpty() || !supplierID.matches("\\d+")
                    || !(supplierService.supplierExists(Integer.parseInt(supplierID)).equals("true"))) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input. Enter numeric supplier ID:");
                supplierID = getInput();
            }
            displayMessage("Enter employee ID:");
            String employeeID = getInput();
            while (supplierID.isEmpty() || !supplierID.matches("\\d+")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input. Enter numeric supplier ID:");
                employeeID = getInput();
            }
            String answer = supplierService.removeEmployee(Integer.parseInt(supplierID), Integer.parseInt(employeeID));
            // if (answer.equals("false")) {
            // print("employee ID doesn't exist");
            // } else if (!answer.equals("true")) {
            // print(answer);
            // }
            displayMessage(answer);
        }

        if (chooseNumber.equals("6")) { // Remove supplier
            displayMessage("Enter supplier ID:");
            String supplierID = getInput();
            while (supplierID.isEmpty() || !supplierID.matches("\\d+")
                    || !(supplierService.supplierExists(Integer.parseInt(supplierID)).equals("true"))) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input. Enter numeric supplier ID:");
                supplierID = getInput();
            }
            displayMessage(supplierService.removeSupplier(Integer.parseInt(supplierID)));
        }
        if (chooseNumber.equals("7")) {
            displayMessage("Enter supplier ID:");
            String supplierID = getInput();
            while (supplierID.isEmpty() || !supplierID.matches("\\d+")
                    || !(supplierService.supplierExists(Integer.parseInt(supplierID)).equals("true"))) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input. Enter numeric supplier ID:");
                supplierID = getInput();
            }
            displayMessage("enter Agreement ID");
            String supplierAgreementID = getInput();
            while (supplierAgreementID.isEmpty() || !supplierAgreementID.matches("\\d+")) {
                tryNumber++;
                if (tryNumber.equals(3)) {
                    displayMessage("You have entered invalid values three times in this action – exiting the action");
                    return true;
                }
                displayMessage("Invalid input, please try again");
                supplierAgreementID = getInput();
            }
            displayMessage(supplierService.removeAgreement(Integer.parseInt(supplierID),
                    Integer.parseInt(supplierAgreementID)));

        }
        return true;
    }

    public void runSupplyManagement() {
        displayMessage("Choose role" + System.lineSeparator() + "1.Purchasing manager" + System.lineSeparator()
                + "2.Supplier");
        String chooseNumber = getInput();
        String valid_chars = "12";
        boolean isContinue = true;
        while (chooseNumber.isEmpty() | !valid_chars.contains(chooseNumber)) {
            chooseNumber = getInput();
        }
        if (chooseNumber.equals("1")) {
            displayMessage("choose branch" + System.lineSeparator() + "1.Haifa branch" + System.lineSeparator() +
                    "2.Tel Aviv branch" + System.lineSeparator() + "3.Beer Sheva branch");
            String branch = getInput();
            valid_chars = "123";
            while (branch.isEmpty() | !valid_chars.contains(branch)) {
                displayMessage("choose valid number");
                branch = getInput();
            }
            if (branch.equals("1")) {
                branch = "Haifa branch";
            }
            if (branch.equals("2")) {
                branch = "Tel Aviv branch";
            }
            if (branch.equals("3")) {
                branch = "Beer Sheva branch";
            }
            while (isContinue) {
                displayMessage("Choose action" + System.lineSeparator() + "1.Create order" + System.lineSeparator()
                        + "2.Get catalog number of items" + System.lineSeparator() + "3.Report order is done"
                        + System.lineSeparator()
                        + "4.Add item" + System.lineSeparator() + "5.Get items names" + System.lineSeparator()
                        + "0.exit role");
                chooseNumber = getInput();
                valid_chars = "012345";
                while (chooseNumber.isEmpty() | !valid_chars.contains(chooseNumber)) {
                    displayMessage("choose valid number");
                    chooseNumber = getInput();
                }
                isContinue = purchasingManagerFunc(chooseNumber, branch);
            }
        }
        if (chooseNumber.equals("2")) {
            while (isContinue) {
                displayMessage("Choose action" + System.lineSeparator() + "1.Add new supplier" + System.lineSeparator()
                        + "2.Add agreement" + System.lineSeparator() +
                        "3.Edit agreement" + System.lineSeparator() + "4.Add employee" + System.lineSeparator()
                        + "5.Remove employee" + System.lineSeparator() + "6.Remove supplier"
                        + System.lineSeparator() + "7.remove agreement" + System.lineSeparator() + "0.exit role");
                chooseNumber = getInput();
                valid_chars = "01234567";
                while (chooseNumber.isEmpty() | !valid_chars.contains(chooseNumber)) {
                    displayMessage("choose valid number");
                    chooseNumber = getInput();
                }
                isContinue = supplierFunc(chooseNumber);
            }
        }
    }

    public int displayMenu() {
        return 0;
    }

    public void loadData() {
        supplierService.loadData();
    }

    public void deleteData() {
        supplierService.deleteData();
    }

    private void displayMessage(String message) {
        cli.displayMessage(message);
    }

    private String getInput() {
        return cli.getInput();
    }
}
