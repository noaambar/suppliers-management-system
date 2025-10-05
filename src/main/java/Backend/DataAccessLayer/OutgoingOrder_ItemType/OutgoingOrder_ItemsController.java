package Backend.DataAccessLayer.OutgoingOrder_ItemType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;
import DataTransferLayer.OutgoingOrder_ItemTypeDto;

public class OutgoingOrder_ItemsController {

    private static final String DB_URL = DatabaseConnector.getDbUrl();

    // שמות טבלה ועמודות כקבועים
    public static final String TABLE_NAME = "Outgoing_Orders_Items";
    public static final String ORDER_ID = "order_Id";
    public static final String ITEM_TYPE_ID = "itemType_Id";
    public static final String QUANTITY = "quantity";

    public OutgoingOrder_ItemsController() {
        createOutgoingOrderItemsTableIfNotExists();
    }

    private void createOutgoingOrderItemsTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                     ORDER_ID + " INTEGER NOT NULL, " +
                     ITEM_TYPE_ID + " INTEGER NOT NULL, " +
                     QUANTITY + " INTEGER NOT NULL, " +
                     "PRIMARY KEY (" + ORDER_ID + ", " + ITEM_TYPE_ID + ")" +
                     ");";

            try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
                stmt.execute(sql);
               
            } catch (SQLException e) {
                throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
            }
        }

    public void insert(OutgoingOrder_ItemTypeDto item) {
        String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + ORDER_ID + " = ? AND " + ITEM_TYPE_ID + " = ?";
        String insertSql = "INSERT INTO " + TABLE_NAME + " (" + ORDER_ID + ", " + ITEM_TYPE_ID + ", " + QUANTITY + ") VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection()) {
            // שלב 1: לבדוק אם כבר קיים עם אותו ORDER_ID ו-ITEM_TYPE_ID
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, item.getOrderID());
                checkStmt.setInt(2, item.getItemsID());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    return; // כבר קיים → לא מכניסים שוב
                }
            }

            // שלב 2: להכניס חדש
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, item.getOrderID());
                pstmt.setInt(2, item.getItemsID());
                pstmt.setInt(3, item.getQuantity());
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error persisting outgoing order item: " + e.getMessage(), e);
        }
    }

    public void delete(int orderId, int itemsId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + ORDER_ID + " = ? AND " + ITEM_TYPE_ID + " = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setInt(2, itemsId); // Assuming itemsID is the same as orderId for deletion
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

    public void update(OutgoingOrder_ItemTypeDto item) {
        String sql = "UPDATE " + TABLE_NAME + " SET " + QUANTITY + " = ? WHERE " + ORDER_ID + " = ? AND " + ITEM_TYPE_ID + " = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, item.getQuantity());
            pstmt.setInt(2, item.getOrderID());
            pstmt.setInt(3, item.getItemsID());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating item: " + e.getMessage(), e);
        }
    }

    public OutgoingOrder_ItemTypeDto find(int orderId, int itemsId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + ORDER_ID + " = ? AND " + ITEM_TYPE_ID + " = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setInt(2, itemsId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new OutgoingOrder_ItemTypeDto(rs.getInt(ORDER_ID), rs.getInt(ITEM_TYPE_ID), rs.getInt(QUANTITY));
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding item: " + e.getMessage(), e);
        }
    }

    public List<OutgoingOrder_ItemTypeDto> findByOrderId(int orderId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + ORDER_ID + " = ?";
        List<OutgoingOrder_ItemTypeDto> items = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(new OutgoingOrder_ItemTypeDto(rs.getInt(ORDER_ID), rs.getInt(ITEM_TYPE_ID), rs.getInt(QUANTITY)));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding items by order ID: " + e.getMessage(), e);
        }

        return items;
    }
}
