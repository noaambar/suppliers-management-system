
import Backend.DataAccessLayer.DatabaseConnector;
import Frontend.PresentationLayer.*;


public class Main {

    public static void main(String[] args) {
        MainProgram mainProgram = new MainProgram();
        mainProgram.Initialize(args);
        DatabaseConnector.closeConnection();
    
      }
  }  

