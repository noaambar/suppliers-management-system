package Backend.DataAccessLayer.Product;
import DataTransferLayer.ProductDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;
public class ProductController {
    private static final String DB_URL = DatabaseConnector.getDbUrl();
    public static final String TABLE_NAME = "Products";

    public static final String itemID = "item_ID";
    public static final String itemName = "Item_name";
    public static final String unit = "unit";
    public static final String amount = "amount";
    public static final String producer = "producer";

    public ProductController() {
        createItemTableIfNotExists();
    }

    private void createItemTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                itemID + " INTEGER PRIMARY KEY, " +
                itemName + " TEXT NOT NULL, " +
                unit + " TEXT NOT NULL, " +
                amount + " INTEGER NOT NULL, " +
                producer + " TEXT NOT NULL" +
                ");";

            try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
        stmt.execute(sql);
      
    } catch (SQLException e) {
        throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
    }
}

    // public void insert(ProductDto item) {
    //     String sql = "INSERT INTO " + TABLE_NAME + " (" +
    //             itemID + ", " + itemName + ", " +
    //             unit + ", " + amount + ", " + producer +
    //             ") VALUES (?, ?, ?, ?, ?)";

    //     try (Connection conn = DriverManager.getConnection(DB_URL);
    //          PreparedStatement pstmt = conn.prepareStatement(sql)) {

    //         pstmt.setInt(1, item.getItemID());
    //         pstmt.setString(2, item.getItemName());
    //         pstmt.setString(3, item.getUnit());
    //         pstmt.setInt(4, item.getAmount());
    //         pstmt.setString(5, item.getProducer());
    //         pstmt.executeUpdate();

    //     } catch (SQLException e) {
    //         throw new RuntimeException("Error inserting item: " + e.getMessage(), e);
    //     }
    // }
        public void insert(ProductDto item) {
        String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + itemID + " = ?";
        String insertSql = "INSERT INTO " + TABLE_NAME + " (" +
                itemID + ", " + itemName + ", " +
                unit + ", " + amount + ", " + producer +
                ") VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection()) {
          
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, item.getItemID());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    return; // הפריט כבר קיים 
            }
        }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, item.getItemID());
                insertStmt.setString(2, item.getItemName());
                insertStmt.setString(3, item.getUnit());
                insertStmt.setInt(4, item.getAmount());
                insertStmt.setString(5, item.getProducer());
                insertStmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting item: " + e.getMessage(), e);
        }
    
    }


    public void delete(int itemId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + itemID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting item: " + e.getMessage(), e);
        }
    }

    public void deleteAll() {
        String sql = "DELETE FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all items: " + e.getMessage(), e);
        }
    }

    public ProductDto find(int itemId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + itemID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new ProductDto(
                        rs.getInt(itemID),
                        rs.getString(itemName),
                        rs.getString(unit),
                        rs.getString(producer),
                        rs.getInt(amount)
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding item: " + e.getMessage(), e);
        }

        return null;
    }

    public List<ProductDto> getAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<ProductDto> items = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(new ProductDto(
                        rs.getInt(itemID),
                        rs.getString(itemName),
                        rs.getString(unit),
                        rs.getString(producer),
                        rs.getInt(amount)
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all items: " + e.getMessage(), e);
        }

        return items;
    }

    public boolean update(ProductDto item) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                itemName + " = ?, " +
                unit + " = ?, " +
                amount + " = ?, " +
                producer + " = ? " +
                "WHERE " + itemID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getItemName());
            pstmt.setString(2, item.getUnit());
            pstmt.setInt(3, item.getAmount());
            pstmt.setString(4, item.getProducer());
            pstmt.setInt(5, item.getItemID());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            
            return false;
        }
    }
}
