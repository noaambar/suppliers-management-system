package Frontend.PresentationLayer;

import java.util.Scanner;


public class CLI {

    private Scanner scanner;

    public CLI() {
        this.scanner = new Scanner(System.in);
    }    
    
    public void displayMessage(String message) {
        System.out.println(message);
    }

    public String getInput() {
        String input = scanner.nextLine();
        return input.toLowerCase();
    }

    public void close() {
        scanner.close();
    }
}



