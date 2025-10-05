package Backend.DataAccessLayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//בטסטים להוריד בנתיב את "dev/nitotz_EEE"
public class DatabaseConnector {
    private static final String DB_URL = "jdbc:sqlite:src/database.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);

        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getDbUrl() {
        return DB_URL;
    }
}
