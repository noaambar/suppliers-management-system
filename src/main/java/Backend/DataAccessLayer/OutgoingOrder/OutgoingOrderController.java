package Backend.DataAccessLayer.OutgoingOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;
import DataTransferLayer.OutgoingOrderDto;

public class OutgoingOrderController {

    private static final String DB_URL = DatabaseConnector.getDbUrl();

    // קבועים לשמות הטבלה והעמודות
    public static final String TABLE_NAME = "Outgoing_Orders";
    public static final String ID = "id";
    public static final String DATE = "date";

    public OutgoingOrderController() {
        createOutgoingOrdersTableIfNotExists();
    }

    private void createOutgoingOrdersTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                     ID + " INTEGER PRIMARY KEY, " +
                     DATE + " INTEGER NOT NULL" +
                     ");";

            try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
                stmt.execute(sql);
               
            } catch (SQLException e) {
                throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
            }
        }

    public void insert(OutgoingOrderDto order) {
            String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + ID + " = ?";
            String insertSql = "INSERT INTO " + TABLE_NAME + " (" + ID + ", " + DATE + ") VALUES (?, ?)";

            try (Connection conn = DatabaseConnector.getConnection()) {
                // שלב 1: לבדוק אם כבר קיים עם אותו ID
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, order.getOrderID());
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        return; // כבר קיים → לא מכניסים שוב
                    }
                }

                // שלב 2: להכניס חדש
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setInt(1, order.getOrderID());
                    pstmt.setInt(2, order.getDay()); // מניח שה-date מאוחסן כ-int (למשל YYYYMMDD)
                    pstmt.executeUpdate();
                }

            } catch (SQLException e) {
                throw new RuntimeException("Error persisting order: " + e.getMessage(), e);
            }
        }

    public void delete(int orderId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting order: " + e.getMessage(), e);
        }
    }

    public void deleteAll() {
        String sql = "DELETE FROM " + TABLE_NAME;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all orders: " + e.getMessage(), e);
        }
    }

    public OutgoingOrderDto find(int orderId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt(ID);
                int date = rs.getInt(DATE);
                return new OutgoingOrderDto(id, date);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving order by ID: " + e.getMessage(), e);
        }

        return null;
    }

    public List<OutgoingOrderDto> getAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<OutgoingOrderDto> orders = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt(ID);
                int date = rs.getInt(DATE);
                orders.add(new OutgoingOrderDto(id, date));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all orders: " + e.getMessage(), e);
        }

        return orders;
    }
}
