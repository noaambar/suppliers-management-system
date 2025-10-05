package Backend.DataAccessLayer.FutureOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;
import DataTransferLayer.FutureOrderDto;

public class FutureOrderController {

    private static final String DB_URL = DatabaseConnector.getDbUrl();

    public static final String TABLE_NAME = "Future_Orders";
    public static final String DAY_COLUMN = "day";
    public static final String ITEM_TYPE_ID_COLUMN = "itemType_Id"; 
    public static final String QUANTITY_COLUMN = "quantity";
    public static final String COST_COLUMN = "cost";
    
    public FutureOrderController() {
        createFutureOrdersTableIfNotExists();
    }

    private void createFutureOrdersTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                DAY_COLUMN + " INTEGER NOT NULL, " +
                ITEM_TYPE_ID_COLUMN + " INTEGER NOT NULL, " +
                QUANTITY_COLUMN + " INTEGER NOT NULL, " +
                COST_COLUMN + " DOUBLE NOT NULL, " +
                "PRIMARY KEY (" + DAY_COLUMN + ", " + ITEM_TYPE_ID_COLUMN + ")" +
                ");";

            try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
        stmt.execute(sql);
       
    } catch (SQLException e) {
        throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
    }
}

    public void insert(FutureOrderDto order) {
        String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + DAY_COLUMN + " = ? AND " + ITEM_TYPE_ID_COLUMN + " = ?";
        String insertSql = "INSERT INTO " + TABLE_NAME + " (" +
                DAY_COLUMN + ", " + ITEM_TYPE_ID_COLUMN + ", " +
                QUANTITY_COLUMN + ", " + COST_COLUMN +
                ") VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection()) {
            // לבדוק אם קיים כבר FutureOrder באותו יום ואותו פריט
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, order.getDay());
                checkStmt.setInt(2, order.getItemTypeId());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    return; // קיים → לא מכניסים שוב
                }
            }

            // אם לא קיים → להכניס חדש
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, order.getDay());
                insertStmt.setInt(2, order.getItemTypeId());
                insertStmt.setInt(3, order.getQuantity());
                insertStmt.setDouble(4, order.getCost());
                insertStmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error persisting future order: " + e.getMessage(), e);
        }
    }

    public List<FutureOrderDto> getFutureOrdersByDay(int day) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + DAY_COLUMN + " = ?";
        List<FutureOrderDto> futureOrders = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, day);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                futureOrders.add(new FutureOrderDto(
                        rs.getInt(DAY_COLUMN),
                        rs.getInt(ITEM_TYPE_ID_COLUMN),
                        rs.getInt(QUANTITY_COLUMN),
                        rs.getDouble(COST_COLUMN)
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading future orders: " + e.getMessage(), e);
        }
        return futureOrders;
    }

    public List<FutureOrderDto> getAllFutureOrders() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<FutureOrderDto> futureOrders = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                futureOrders.add(new FutureOrderDto(
                        rs.getInt(DAY_COLUMN),
                        rs.getInt(ITEM_TYPE_ID_COLUMN),
                        rs.getInt(QUANTITY_COLUMN),
                        rs.getDouble(COST_COLUMN)
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading all future orders: " + e.getMessage(), e);
        }
        return futureOrders;
    }

    public void update(FutureOrderDto order) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                QUANTITY_COLUMN + " = ?, " +
                COST_COLUMN + " = ? " +
                "WHERE " + DAY_COLUMN + " = ? AND " + ITEM_TYPE_ID_COLUMN + " = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, order.getQuantity());
            pstmt.setDouble(2, order.getCost());
            pstmt.setInt(3, order.getDay());
            pstmt.setInt(4, order.getItemTypeId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating future order: " + e.getMessage(), e);
        }
    }

    public void deleteFutureOrdersByDay(int day) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + DAY_COLUMN + " = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, day);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting future order by day: " + e.getMessage(), e);
        }
    }

    public void deleteAllFutureOrders() {
        String sql = "DELETE FROM " + TABLE_NAME;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all future orders: " + e.getMessage(), e);
        }
    }

    
}
