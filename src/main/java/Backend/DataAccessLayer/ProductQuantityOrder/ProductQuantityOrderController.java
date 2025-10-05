package Backend.DataAccessLayer.ProductQuantityOrder;

import DataTransferLayer.ProductQuantityOrderDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;

public class ProductQuantityOrderController {
    private static final String DB_URL = DatabaseConnector.getDbUrl();
    public static final String TABLE_NAME = "Product_Quantity_Order";

    public static final String orderID = "order_ID";
    public static final String productID = "product_ID";
    public static final String quantity = "quantity";

    public ProductQuantityOrderController() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                orderID + " INTEGER NOT NULL, " +
                productID + " INTEGER NOT NULL, " +
                quantity + " INTEGER NOT NULL, " +
                "PRIMARY KEY (" + orderID + ", " + productID + ")" +
                ");";

    try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
        stmt.execute(sql);
       
    } catch (SQLException e) {
        throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
    }
}
    public void insert(ProductQuantityOrderDto dto) {
        String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + orderID + " = ? AND " + productID + " = ?";
        String insertSql = "INSERT INTO " + TABLE_NAME + " (" + orderID + ", " + productID + ", " + quantity + ") VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Step 1: Check if (orderID, productID) pair already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, dto.getOrderID());
                checkStmt.setInt(2, dto.getProductId());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // The pair already exists → do not insert
                    return;
                }
            }

            // Step 2: Insert new record
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, dto.getOrderID());
                insertStmt.setInt(2, dto.getProductId());
                insertStmt.setInt(3, dto.getQuantity());
                insertStmt.executeUpdate();
      
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting order: " + e.getMessage(), e);
        }
    }


    public void delete(int orderIDVal, int productIDVal) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + orderID + " = ? AND " + productID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderIDVal);
            pstmt.setInt(2, productIDVal);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting order: " + e.getMessage(), e);
        }
    }

    public ProductQuantityOrderDto find(int orderIDVal, int productIDVal) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + orderID + " = ? AND " + productID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderIDVal);
            pstmt.setInt(2, productIDVal);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new ProductQuantityOrderDto(
                        rs.getInt(orderID),
                        rs.getInt(productID),
                        rs.getInt(quantity)
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding order: " + e.getMessage(), e);
        }

        return null;
    }

    public List<ProductQuantityOrderDto> getAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<ProductQuantityOrderDto> orders = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(new ProductQuantityOrderDto(
                        rs.getInt(orderID),
                        rs.getInt(productID),
                        rs.getInt(quantity)
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving orders: " + e.getMessage(), e);
        }
        return orders;
    }

    public boolean update(ProductQuantityOrderDto dto) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                quantity + " = ? WHERE " +
                orderID + " = ? AND " + productID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dto.getQuantity());
            pstmt.setInt(2, dto.getOrderID());
            pstmt.setInt(3, dto.getProductId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
          
            return false;
        }
    }
    public List<ProductQuantityOrderDto> readAllByOrder(int orderIDVal) {
        List<ProductQuantityOrderDto> orders = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + orderID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderIDVal);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orders.add(new ProductQuantityOrderDto(
                        rs.getInt(orderID),
                        rs.getInt(productID),
                        rs.getInt(quantity)
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving orders by orderID: " + e.getMessage(), e);
        }

        return orders;
    }

}
