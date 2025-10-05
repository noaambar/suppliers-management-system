package Frontend.PresentationLayer;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import Backend.ServiceLayer.ItemService;
import Backend.ServiceLayer.Response;

class InventoryProgram {

    private final ItemService itemService;
    private boolean terminate = false;
    private final CLI cli;

    public InventoryProgram(ItemService itemService, CLI cli) {
        this.itemService = itemService;
        this.cli = cli;
    }

    public int displayMenu() {
        displayMessage("\nPlease choose an option from the menu below:");
        displayMessage("\n================= ITEM MANAGEMENT MENU =================");
        displayMessage("1. Add a new item to the inventory catalog");
        displayMessage("2. Remove an item from the inventory catalog");
        displayMessage("3. Move items");
        displayMessage("4. Update item price");
        displayMessage("5. Update item discount");
        displayMessage("6. Update category discount");
        displayMessage("7. Get available items");
        displayMessage("8. Sell items");
        displayMessage("9. Report defective item");
        displayMessage("10. Generate defective items report");
        displayMessage("11. Generate category report");
        displayMessage("12. Generate expired items report");
        displayMessage("13. Update main category for an item");
        displayMessage("14. Update sub-category for an item");
        displayMessage("15. Update sub-sub-category for an item");
        displayMessage("16. Create new periodic order");
        displayMessage("17. Update periodic order");
        displayMessage("18. Cancel periodic order");
        displayMessage("19. Resupply an item");
        displayMessage("20. Change module");
        displayMessage("========================================================");

        displayMessage("Enter your choice: ");
        int choice = 0;
        try {
            choice = Integer.parseInt(getInput());
        } catch (Exception e) {
            displayMessage(e.getMessage());
            return displayMenu(); // Try again recursively
        }

        return choice;
    }

    public void initialDB(String jsonFilePath) {
        
        Gson gson = new Gson();
        Path path = Paths.get(jsonFilePath);

        try (FileReader reader = new FileReader(path.toFile())) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            JsonArray inventory = jsonObject.getAsJsonArray("inventory");

            for (JsonElement itemElement : inventory) {
                JsonObject item = itemElement.getAsJsonObject();

                int id = item.get("id").getAsInt();
                double price = item.get("price").getAsDouble();

                JsonObject category = item.getAsJsonObject("category");
                String mainCategory = category.get("main").getAsString();
                String subCategory = category.get("sub").getAsString();
                String subSubCategory = category.get("subSub").getAsString();

                itemService.newItem(id, price, mainCategory, subCategory, subSubCategory);

            }
            JsonArray periodicOrders = jsonObject.getAsJsonArray("periodicOrders");
            if (periodicOrders != null) {
                for (JsonElement orderElement : periodicOrders) {
                    JsonObject order = orderElement.getAsJsonObject();
                    int arrivalDay = order.get("issueDay").getAsInt();
                    JsonArray itemsArray = order.getAsJsonArray("items");
                    HashMap<Integer, Integer> items = new HashMap<>();
                    for (JsonElement itemElement : itemsArray) {
                        JsonObject item = itemElement.getAsJsonObject();
                        int itemId = item.get("itemId").getAsInt();
                        int quantity = item.get("quantity").getAsInt();
                        items.put(itemId, quantity);
                    }
                    itemService.createPeriodicOrder(items, arrivalDay);
                }
            }
            // send orders
            prossesResponse(itemService.updateOrders());
            prossesResponse(itemService.sendPeriodicOrders());
            prossesResponse(itemService.sendAvailablityOrders());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateReport(Response<?> report, String reportName) {
        if (report.isError) {
            displayMessage("Error: " + report.getError());
        } else {
            writeJsonFile(report.getData(), reportName);
            displayMessage("Report generated successfully.");
        }
    }

    private void writeJsonFile(Object data, String reportName) {
        String filePath = reportName + ".json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            String json = gson.toJson(data);
            writer.write(json);
        } catch (IOException e) {
            displayMessage("Error writing to text file: " + e.getMessage());
        }
    }

    public void runInventoryManagement() {
        displayMessage("Warning: this are the items that have low availability");
        displayUnavailableItems();
        terminate = false;
        while (!terminate) {
            int choice = displayMenu();
            try {
                switch (choice) {
                    case 1:
                        newItem();
                        break;
                    case 2:
                        removeItem();
                        break;
                    case 3:
                        moveItems();
                        break;
                    case 4:
                        updateItemPrice();
                        break;
                    case 5:
                        updateItemDiscount();
                        break;
                    case 6:
                        updateCategoryDiscount();
                        break;
                    case 7:
                        getAvailabelItems();
                        break;
                    case 8:
                        sellItems();
                        break;
                    case 9:
                        reportDefectiveItem();
                        break;
                    case 10:
                        defectiveReport();
                        break;
                    case 11:
                        categoriesReport();
                        break;
                    case 12:
                        itemsExpireBeforeDateReport();
                        break;
                    case 13:
                        updateMainCategory();
                        break;
                    case 14:
                        updateSubCategory();
                        break;
                    case 15:
                        updateSubSubCategory();
                        break;
                    case 16:
                        createPeriodicOrder();
                        break;
                    case 17:
                        updatePeriodicOrder();
                        break;
                    case 18:
                        cancelPeriodicOrder();
                        break;
                    case 19:
                        resupplyItem();
                        break;
                    case 20:
                        terminate = true;
                        break;
                    default:
                        displayMessage("Invalid choice. Please try again.");
                        break;
                }
            } catch (Exception e) {
                displayMessage("An error occurred: " + e.getMessage());
            }

        }
    }

    private void updatePeriodicOrder() {
        try {
            displayMessage("Enter periodic order ID to update: ");
            int orderId = Integer.parseInt(getInput());
            displayMessage("Enter new periodic order details: ");
            displayMessage("Enter new/old items ID: ");
            int itemsId = Integer.parseInt(getInput());
            displayMessage("Enter new quantity: ");
            int quantity = Integer.parseInt(getInput());
            prossesResponse(itemService.updatePeriodicOrder(orderId, itemsId, quantity));
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    private void createPeriodicOrder() {
        displayMessage("Enter periodic order details: ");
        boolean continueInput = true;
        HashMap<Integer, Integer> items = new HashMap<>();
        try {
            while (continueInput) {
                displayMessage("Enter item Type ID: ");
                int itemsId = Integer.parseInt(getInput());
                displayMessage("Enter quantity: ");
                int quantity = Integer.parseInt(getInput());
                displayMessage("Do you want to add another item? (yes/no): ");
                String answer = getInput();
                if (answer.equalsIgnoreCase("no")) {
                    continueInput = false;
                }
                items.put(itemsId, quantity);
            }
            displayMessage("Enter arrival day: ");
            int arrivalDay = Integer.parseInt(getInput());
            prossesResponse(itemService.createPeriodicOrder(items, arrivalDay));

        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    private void cancelPeriodicOrder() {
        displayMessage("Enter periodic order ID to cancel: ");
        try {
            int orderId = Integer.parseInt(getInput());
            prossesResponse(itemService.cancelPeriodicOrder(orderId));
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    private void removeItem() {
        displayMessage("Enter item ID to remove: ");
        try {
            int itemId = Integer.parseInt(getInput());
            Response<?> res = itemService.removeItem(itemId);
            if (res.isError && res.getError().equals("Item with stock")) {
                displayMessage("Item cannot be removed because it has stock.");
                displayMessage("Would you like to stop buying this product from the suppliers? (yes/no)");
                String answer = getInput();
                if (answer.equalsIgnoreCase("yes")) {
                    prossesResponse(itemService.stopSupplying(itemId));
                } else if (answer.equalsIgnoreCase("no")) {
                    return;
                } else {
                    displayMessage("Invalid input. Please enter 'yes' or 'no'.");
                }
            } else {
                prossesResponse(res);
            }
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    private void resupplyItem() {
        displayMessage("Enter item ID to resupply: ");
        try {
            int itemId = Integer.parseInt(getInput());
            prossesResponse(itemService.resupplyItem(itemId));
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    private void prossesResponse(Response<?> res) {
        if (res.isError) {
            displayMessage("Error: " + res.getError());
        } else {
            displayMessage("Success: " + res.getData());
        }
    }

    private void newItem() {
        try {
            displayMessage("Enter item ID from suppliers catalog: ");
            int itemId = Integer.parseInt(getInput());
            displayMessage("Enter price: ");
            double price = Double.parseDouble(getInput());
            displayMessage("Enter main category: ");
            String mainCategory = getInput();
            displayMessage("Enter sub category: ");
            String subCategory = getInput();
            displayMessage("Enter sub sub category: ");
            String subSubCategory = getInput();
            prossesResponse(itemService.newItem(itemId, price, mainCategory, subCategory, subSubCategory));
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    private void moveItems() {
        displayMessage("Enter item ID to move: ");
        try {
            int itemId = Integer.parseInt(getInput());
            displayMessage("Enter destination (0 for STORE, 1 for WAREHOUSE): ");
            String destination = getInput();
            displayMessage("Enter pass (number between 1-10): ");
            String pass = getInput();
            displayMessage("Enter shelf (number between 1-10): ");
            String shelf = getInput();

            String[] location = { destination, pass, shelf };

            displayMessage("Enter item instances IDs to move (comma separated): ");
            String[] itemIds = getInput().split(",");
            List<Integer> ids = List.of(itemIds).stream().map(Integer::parseInt).toList();

            prossesResponse(itemService.moveItems(itemId, ids, location));
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    private void updateItemPrice() {
        try {
            displayMessage("Enter item ID to update price: ");
            int itemId = Integer.parseInt(getInput());
            displayMessage("Enter new price: ");
            double price = Double.parseDouble(getInput());
            prossesResponse(itemService.updatePrice(itemId, price));
        } catch (Exception e) {
            displayMessage("Invalid input. Please enter a valid number.");
        }
    }

    private void updateItemDiscount() {
        try {
            displayMessage("Enter item ID to update discount: ");
            int itemId = Integer.parseInt(getInput());
            displayMessage("Enter new discount: ");
            double discount = Double.parseDouble(getInput());
            displayMessage("Enter expiration date for discount (YYYY-MM-DD): ");
            Date expirationDate = Date.valueOf(getInput());

            prossesResponse(itemService.updateDiscount(itemId, discount, expirationDate));
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    private void updateCategoryDiscount() {
        displayMessage("Enter main category: ");
        String mainCategory = getInput();

        String subCategory = null;
        String subSubCategory = null;

        displayMessage("Do you want to enter a sub category? (yes/no): ");
        String answer = getInput();

        if (answer.equalsIgnoreCase("yes")) {
            displayMessage("Enter sub category: ");
            subCategory = getInput();

            displayMessage("Do you want to enter a sub sub category? (yes/no): ");
            String subSubAnswer = getInput();

            if (subSubAnswer.equalsIgnoreCase("yes")) {
                displayMessage("Enter sub sub category: ");
                subSubCategory = getInput();
            }
        }

        try {
            displayMessage("Enter new discount: ");
            double discount = Double.parseDouble(getInput());

            displayMessage("Enter expiration date for discount (YYYY-MM-DD): ");
            Date expirationDate = Date.valueOf(getInput());

            prossesResponse(itemService.updateDiscountPerCategories(
                    mainCategory,
                    subCategory,
                    subSubCategory,
                    discount,
                    expirationDate));
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    private void getAvailabelItems() {
        prossesResponse(itemService.getAvailabelItems());
    }

    private void displayUnavailableItems() {
        prossesResponse(itemService.minimalAmountReport());
    }

    private void sellItems() {
        try {
            displayMessage("Enter item ID to sell: ");
            int itemId = Integer.parseInt(getInput());
            displayMessage("Enter item instances IDs to sell (comma separated): ");
            String[] itemIds = getInput().split(",");
            List<Integer> ids = List.of(itemIds).stream().map(Integer::parseInt).toList();

            prossesResponse(itemService.sellItems(itemId, ids));
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    private void reportDefectiveItem() {
        try {
            displayMessage("Enter items ID to report as defective: ");
            int itemId = Integer.parseInt(getInput());
            displayMessage("Enter id item: ");
            int ids = Integer.parseInt(getInput());
            prossesResponse(itemService.reportDefectiveItem(itemId, ids));
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    private void defectiveReport() {
        generateReport(itemService.defectiveReport(), "defectiveReport");
    }

    private void categoriesReport() {
        List<String[]> categoriesList = new ArrayList<>();
        boolean moreCategories = true;

        while (moreCategories) {
            displayMessage("Enter main category (required): ");
            String mainCategory = getInput().trim();

            String subCategory = null;
            String subSubCategory = null;

            displayMessage("Do you want to enter a sub category? (yes/no): ");
            String addSub = getInput().trim();

            if (addSub.equalsIgnoreCase("yes")) {
                displayMessage("Enter sub category: ");
                subCategory = getInput().trim();

                displayMessage("Do you want to enter a sub-sub category? (yes/no): ");
                String addSubSub = getInput().trim();

                if (addSubSub.equalsIgnoreCase("yes")) {
                    displayMessage("Enter sub-sub category: ");
                    subSubCategory = getInput().trim();
                }
            }

            if (subCategory == null) {
                categoriesList.add(new String[] { mainCategory });
            } else if (subSubCategory == null) {
                categoriesList.add(new String[] { mainCategory, subCategory });
            } else {
                categoriesList.add(new String[] { mainCategory, subCategory, subSubCategory });
            }

            displayMessage("Do you want to add another main category? (yes/no): ");
            String addMore = getInput().trim();

            if (addMore.equalsIgnoreCase("no")) {
                moreCategories = false;
            } else if (!addMore.equalsIgnoreCase("yes")) {
                displayMessage("Invalid input. Please enter 'yes' or 'no'.");
            }
        }

        generateReport(itemService.report(categoriesList), "categoriesReport");
    }

    private void itemsExpireBeforeDateReport() {
        displayMessage("Enter expiration date (YYYY-MM-DD): ");
        Date expirationDate = Date.valueOf(getInput());
        generateReport(itemService.itemsExpireBeforeDate(expirationDate), "expiredItemsReport");

    }

    private void updateMainCategory() {
        try {
            displayMessage("Enter item ID to update main category: ");
            int itemId = Integer.parseInt(getInput());
            displayMessage("Enter new main category: ");
            String mainCategory = getInput();

            prossesResponse(itemService.updateMainCategory(itemId, mainCategory));
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    private void updateSubCategory() {
        try {
            displayMessage("Enter item ID to update sub category: ");
            int itemId = Integer.parseInt(getInput());
            displayMessage("Enter new sub category: ");
            String subCategory = getInput();

            prossesResponse(itemService.updateSubCategory(itemId, subCategory));
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    private void updateSubSubCategory() {
        try {
            displayMessage("Enter item ID to update sub sub category: ");
            int itemId = Integer.parseInt(getInput());
            displayMessage("Enter new sub sub category: ");
            String subSubCategory = getInput();

            prossesResponse(itemService.updateSubSubCategory(itemId, subSubCategory));
        } catch (Exception e) {
            displayMessage(e.getMessage());
        }
    }

    public void nextDay() {
        prossesResponse(itemService.nextDay());
        sendAvailablityOrders();
    }

    private void sendAvailablityOrders() {
        prossesResponse(itemService.sendAvailablityOrders());
    }

    public void loadData(boolean hasPath) {
        prossesResponse(itemService.loadData());
        if(!hasPath) {
            prossesResponse(itemService.updateOrders());
            prossesResponse(itemService.sendPeriodicOrders());
        }
        sendAvailablityOrders();
    }

    public void deleteData() {
        prossesResponse(itemService.deleteData());
    }

    private void displayMessage(String message) {
        cli.displayMessage(message);
    }

    private String getInput() {
        return cli.getInput();
    }

}
