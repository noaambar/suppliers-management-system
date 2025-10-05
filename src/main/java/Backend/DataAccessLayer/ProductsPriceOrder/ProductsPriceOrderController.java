package Backend.DataAccessLayer.ProductsPriceOrder;
import DataTransferLayer.ProductsPriceOrderDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;
public class ProductsPriceOrderController {
    private static final String DB_URL = DatabaseConnector.getDbUrl();
    public static final String TABLE_NAME = "Product_Price_Orders";

    public static final String orderID = "order_ID";
    public static final String productID = "product_ID";
    public static final String price = "price";

    public ProductsPriceOrderController() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                orderID + " INTEGER NOT NULL, " +
                productID + " INTEGER NOT NULL, " +
                price + " REAL NOT NULL, " +
                "PRIMARY KEY (" + orderID + ", " + productID + ")" +
                ");";

    try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
        stmt.execute(sql);
       
    } catch (SQLException e) {
        throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
    }
}

    public void insert(ProductsPriceOrderDto dto) {
        String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + orderID + " = ? AND " + productID + " = ?";
        String insertSql = "INSERT INTO " + TABLE_NAME + " (" + orderID + ", " + productID + ", " + price + ") VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Step 1: Check if the (orderID, productID) pair already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, dto.getOrderID());
                checkStmt.setInt(2, dto.getProductID());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Already exists → do not insert
                    return;
                }
            }

            // Step 2: Insert new record
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, dto.getOrderID());
                insertStmt.setInt(2, dto.getProductID());
                insertStmt.setDouble(3, dto.getPrice());
                insertStmt.executeUpdate();
              
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting product price: " + e.getMessage(), e);
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
            throw new RuntimeException("Error deleting product price: " + e.getMessage(), e);
        }
    }

    public void deleteAll() {
        String sql = "DELETE FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all product prices: " + e.getMessage(), e);
        }
    }

    public ProductsPriceOrderDto find(int orderIDVal, int productIDVal) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + orderID + " = ? AND " + productID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderIDVal);
            pstmt.setInt(2, productIDVal);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double priceVal = rs.getDouble(price);
                return new ProductsPriceOrderDto(orderIDVal, productIDVal, priceVal);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding product price: " + e.getMessage(), e);
        }

        return null;
    }

    public List<ProductsPriceOrderDto> getAll() {
        List<ProductsPriceOrderDto> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int oid = rs.getInt(orderID);
                int pid = rs.getInt(productID);
                double priceVal = rs.getDouble(price);
                list.add(new ProductsPriceOrderDto(oid, pid, priceVal));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all product prices: " + e.getMessage(), e);
        }

        return list;
    }

    public boolean update(ProductsPriceOrderDto dto) {
        String sql = "UPDATE " + TABLE_NAME + " SET " + price + " = ? " +
                "WHERE " + orderID + " = ? AND " + productID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, dto.getPrice());
            pstmt.setInt(2, dto.getOrderID());
            pstmt.setInt(3, dto.getProductID());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
           
            return false;
        }
    }
    public List<ProductsPriceOrderDto> readAllByOrder(int orderIDVal) {
        List<ProductsPriceOrderDto> prices = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + orderID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderIDVal);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                prices.add(new ProductsPriceOrderDto(
                        rs.getInt(orderID),
                        rs.getInt(productID),
                        rs.getDouble(price)
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving product prices by orderID: " + e.getMessage(), e);
        }

        return prices;
    }

}
