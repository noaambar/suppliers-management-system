package Frontend.PresentationLayer;

import Backend.ServiceLayer.ServiceFactory;

public class MainProgram {

    private final InventoryProgram IP;
    private final SuppliersProgram SP;
    private final CLI cli = new CLI();

    public MainProgram() {
        ServiceFactory serviceFactory = new ServiceFactory();
        IP = new InventoryProgram(serviceFactory.getItemService(), cli);
        SP = new SuppliersProgram(serviceFactory.getSupplierService(), cli);
    }

    public void Initialize(String[] args) {
        displayMessage("Welcome to the Management System!");

        displayMessage("Would you like to:");
        displayMessage("L - Load existing data");
        displayMessage("D - Delete existing data and start fresh");

        String choice = getInput();
        boolean hasPath = args.length > 0;

        switch (choice.toUpperCase()) {
            case "L":
                loadData(hasPath);
                break;
            case "D":
                deleteData();
                break;
            default:
                displayMessage("Invalid input. Skipping data loading/deletion.");
                break;
        }

        if (hasPath) {
            SP.initialDB(args[0]);
            IP.initialDB(args[0]);
            displayMessage("Intialization complete with path: " + args[0]);
        }
        // Start main interaction loop
        while (true) {
            start();
        }
    }

    private int displayMenu() {
        displayMessage("Please choose an option:");
        displayMessage("1. Inventory Management");
        displayMessage("2. Supply Management");
        displayMessage("3. System Management");
        displayMessage("4. Exit");
        String input = getInput();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            displayMessage("Invalid input. Please enter a number.");
            return displayMenu();
        }
    }

    private void start() {
        int input = displayMenu();

        if (input == 1) {
            displayMessage("You have chosen Inventory Management.");
            IP.runInventoryManagement();
        } else if (input == 2) {
            displayMessage("You have chosen Supply Management.");
            SP.runSupplyManagement();
        } else if (input == 3) {
            displayMessage("You have chosen System Management.");
            runSystemManagement();
        } else {
            displayMessage("Invalid choice. Please try again.");

        }
    }

    private void runSystemManagement() {
        displayMessage("Would you like passing a day in the system? (yes/no)");
        String answer = getInput();
        if (answer.equalsIgnoreCase("yes")) {
            IP.nextDay();
        } else if (answer.equalsIgnoreCase("no")) {
            displayMessage("Returning to main menu.");
            start();
        } else {
            displayMessage("Invalid input. Please enter 'yes' or 'no'.");
            runSystemManagement();
        }
    }

    private void loadData(boolean hasPath) {
        try {
            displayMessage("Loading data...");
            SP.loadData();
            IP.loadData(hasPath);
        } catch (Exception e) {
            displayMessage("Error loading data: " + e.getMessage());
        }

    }

    private void deleteData() {
        try {
            displayMessage("Deleting data...");
            SP.deleteData();
            IP.deleteData();
        } catch (Exception e) {
            displayMessage("Error deleting data: " + e.getMessage());
        }
    }

    private void displayMessage(String string) {
        cli.displayMessage(string);
    }

    private String getInput() {
        return cli.getInput();
    }

}
