package Backend.DataAccessLayer.ItemInstance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;
import DataTransferLayer.ItemInstanceDto;

public class ItemInstanceController {

    private static final String DB_URL = DatabaseConnector.getDbUrl();

    public static final String TABLE_NAME = "ItemInstances";
    public static final String ITEM_TYPE_ID_COLUMN = "itemType_Id";
    public static final String ID_COLUMN = "id";
    public static final String EXPIRATION_DATE_COLUMN = "expirationDate";
    public static final String DEFECTIVE_COLUMN = "defective";
    public static final String LOCATION_COLUMN = "location";
    public static final String LOCATION_PASS_COLUMN = "location_pass";
    public static final String LOCATION_SHELF_COLUMN = "location_shelf";

    public ItemInstanceController() {
        createItemsTableIfNotExists();
    }

    private void createItemsTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                ITEM_TYPE_ID_COLUMN + " INTEGER NOT NULL, " +
                ID_COLUMN + " INTEGER NOT NULL, " +
                EXPIRATION_DATE_COLUMN + " DATE NOT NULL, " +
                DEFECTIVE_COLUMN + " INTEGER NOT NULL, " +
                LOCATION_COLUMN + " INTEGER NOT NULL, " +
                LOCATION_PASS_COLUMN + " INTEGER NOT NULL, " +
                LOCATION_SHELF_COLUMN + " INTEGER NOT NULL, " +
                "PRIMARY KEY (" + ITEM_TYPE_ID_COLUMN + ", " + ID_COLUMN + ")" +
                ");";

            try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
                stmt.execute(sql);
               
            } catch (SQLException e) {
                throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
            }
        }

    public void insert(ItemInstanceDto item) {
        String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + ID_COLUMN + " = ? AND " + ITEM_TYPE_ID_COLUMN + " = ?";
            String insertSql = "INSERT INTO " + TABLE_NAME + " (" +
                    ITEM_TYPE_ID_COLUMN + ", " + ID_COLUMN + ", " +
                    EXPIRATION_DATE_COLUMN + ", " + DEFECTIVE_COLUMN + ", " +
                    LOCATION_COLUMN + ", " + LOCATION_PASS_COLUMN + ", " +
                    LOCATION_SHELF_COLUMN +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnector.getConnection()) {
            
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, item.getId());
                    checkStmt.setInt(2, item.getItemTypeId());
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        return; 
                    }
                }

                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, item.getItemTypeId());
                    insertStmt.setInt(2, item.getId());
                    insertStmt.setDate(3, item.getExpirationDate());
                    insertStmt.setInt(4, item.getDefective());
                    insertStmt.setInt(5, item.getLocation());
                    insertStmt.setInt(6, item.getLocation_pass());
                    insertStmt.setInt(7, item.getLocation_shelf());
                    insertStmt.executeUpdate();
                }

            } catch (SQLException e) {
                throw new RuntimeException("Error persisting item instance: " + e.getMessage(), e);
            }
        }

    public void deleteItemTypeIdById(int ItemTypeId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + ITEM_TYPE_ID_COLUMN + " = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ItemTypeId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting items by ID: " + e.getMessage(), e);
        }
    }

    public void deleteItemById(int ItemTypeId, int id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " +
                ITEM_TYPE_ID_COLUMN + " = ? AND " + ID_COLUMN + " = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ItemTypeId);
            pstmt.setInt(2, id);
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

    public ItemInstanceDto find(int ItemTypeId, int id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                ITEM_TYPE_ID_COLUMN + " = ? AND " + ID_COLUMN + " = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ItemTypeId);
            pstmt.setInt(2, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new ItemInstanceDto(
                        rs.getInt(ITEM_TYPE_ID_COLUMN),
                        rs.getInt(ID_COLUMN),
                        rs.getDate(EXPIRATION_DATE_COLUMN),
                        rs.getInt(DEFECTIVE_COLUMN),
                        rs.getInt(LOCATION_COLUMN),
                        rs.getInt(LOCATION_PASS_COLUMN),
                        rs.getInt(LOCATION_SHELF_COLUMN)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving item: " + e.getMessage(), e);
        }
        return null;
    }

    public List<ItemInstanceDto> getItemsByItemsId(int itemsId) {
        List<ItemInstanceDto> items = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + ITEM_TYPE_ID_COLUMN + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, itemsId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(new ItemInstanceDto(
                        rs.getInt(ITEM_TYPE_ID_COLUMN),
                        rs.getInt(ID_COLUMN),
                        rs.getDate(EXPIRATION_DATE_COLUMN),
                        rs.getInt(DEFECTIVE_COLUMN),
                        rs.getInt(LOCATION_COLUMN),
                        rs.getInt(LOCATION_PASS_COLUMN),
                        rs.getInt(LOCATION_SHELF_COLUMN)
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving items by id: " + e.getMessage(), e);
        }

        return items;
    }

    public void update(ItemInstanceDto item) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                EXPIRATION_DATE_COLUMN + " = ?, " +
                DEFECTIVE_COLUMN + " = ?, " +
                LOCATION_COLUMN + " = ?, " +
                LOCATION_PASS_COLUMN + " = ?, " +
                LOCATION_SHELF_COLUMN + " = ? " +
                "WHERE " + ITEM_TYPE_ID_COLUMN + " = ? AND " + ID_COLUMN + " = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, item.getExpirationDate());
            pstmt.setInt(2, item.getDefective());
            pstmt.setInt(3, item.getLocation());
            pstmt.setInt(4, item.getLocation_pass());
            pstmt.setInt(5, item.getLocation_shelf());
            pstmt.setInt(6, item.getItemTypeId());
            pstmt.setInt(7, item.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating item: " + e.getMessage(), e);
        }
    }
}
