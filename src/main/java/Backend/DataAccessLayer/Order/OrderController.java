package Backend.DataAccessLayer.Order;

import DataTransferLayer.OrderDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;

public class OrderController {
    private static final String DB_URL = DatabaseConnector.getDbUrl();
    public static final String TABLE_NAME = "Orders";

    public static final String orderID = "order_ID";
    public static final String supplierID = "supplier_ID";
    public static final String date = "date";
    public static final String phonenumber = "phone_number";
    public static final String status = "status";

    public OrderController() {
        createOrderTableIfNotExists();
    }

    private void createOrderTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                orderID + " INTEGER PRIMARY KEY, " +
                supplierID + " INTEGER NOT NULL, " +
                date + " TEXT NOT NULL, " +
                phonenumber + " TEXT NOT NULL, " +
                status + " TEXT NOT NULL" +
                ");";

            try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
                stmt.execute(sql);
               
            } catch (SQLException e) {
                throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
            }
        }

    public void insert(OrderDto order) {
        String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + orderID + " = ?";
        String insertSql = "INSERT INTO " + TABLE_NAME + " (" +
                orderID + ", " + supplierID + ", " +
                date + ", " + phonenumber + ", " + status + ") VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Step 1: Check if order already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, order.getOrderID());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Order already exists → do not insert
                    return;
                }
            }

            // Step 2: Insert new order
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, order.getOrderID());
                insertStmt.setInt(2, order.getSupplierID());
                insertStmt.setString(3, order.getDate().toString());
                insertStmt.setString(4, order.getPhonenumber());
                insertStmt.setString(5, order.getStatus());
                insertStmt.executeUpdate();
               
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting order: " + e.getMessage(), e);
        }
    }


    public void delete(int id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + orderID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
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

    public OrderDto find(int id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + orderID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int oid = rs.getInt(orderID);
                int sid = rs.getInt(supplierID);
                Date orderDate = Date.valueOf(rs.getString(date));
                String phone = rs.getString(phonenumber);
                String stat = rs.getString(status);
                return new OrderDto(oid, sid, orderDate, phone, stat);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding order: " + e.getMessage(), e);
        }

        return null;
    }

    public List<OrderDto> getAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<OrderDto> orders = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int oid = rs.getInt(orderID);
                int sid = rs.getInt(supplierID);
                Date orderDate = Date.valueOf(rs.getString(date));
                String phone = rs.getString(phonenumber);
                String stat = rs.getString(status);
                orders.add(new OrderDto(oid, sid, orderDate, phone, stat));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all orders: " + e.getMessage(), e);
        }

        return orders;
    }

    public boolean update(OrderDto order) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                supplierID + " = ?, " +
                date + " = ?, " +
                phonenumber + " = ?, " +
                status + " = ? " +
                "WHERE " + orderID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, order.getSupplierID());
            pstmt.setString(2, order.getDate().toString());
            pstmt.setString(3, order.getPhonenumber());
            pstmt.setString(4, order.getStatus());
            pstmt.setInt(5, order.getOrderID());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
           
            return false;
        }
    }
}
