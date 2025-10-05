package Backend.DataAccessLayer.ItemType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;
import DataTransferLayer.ItemTypeDto;

public class ItemTypeController {

    private static final String DB_URL = DatabaseConnector.getDbUrl();

    // קבועים לשמות הטבלה והעמודות
    public static final String TABLE_NAME = "ItemTypes";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String ORIGINAL_PRICE = "original_price";
    public static final String ITEM_DISCOUNT = "item_discount";
    public static final String EXPIRATION_DATE_ITEM_DISCOUNT = "expiration_date_item_discount";
    public static final String CATEGORY_DISCOUNT = "category_discount";
    public static final String EXPIRATION_DATE_CATEGORY_DISCOUNT = "expiration_date_category_discount";
    public static final String COST = "cost";
    public static final String MANUFACTURE = "manufacture";
    public static final String MAIN_CATEGORY = "main_category";
    public static final String SUB_CATEGORY = "sub_category";
    public static final String SUB_SUB_CATEGORY = "sub_sub_category";
    public static final String SALES = "sales";
    public static final String TO_SUPPLY = "to_supply";
    public static final String IS_OPEN_ORDER = "is_open_order";

    public ItemTypeController() {
        createItemsTableIfNotExists();
    }

    private void createItemsTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY," +
                NAME + " TEXT NOT NULL," +
                ORIGINAL_PRICE + " DOUBLE NOT NULL," +
                ITEM_DISCOUNT + " DOUBLE," +
                EXPIRATION_DATE_ITEM_DISCOUNT + " DATE," +
                CATEGORY_DISCOUNT + " DOUBLE," +
                EXPIRATION_DATE_CATEGORY_DISCOUNT + " DATE," +
                COST + " DOUBLE NOT NULL," +
                MANUFACTURE + " TEXT," +
                MAIN_CATEGORY + " TEXT," +
                SUB_CATEGORY + " TEXT," +
                SUB_SUB_CATEGORY + " TEXT," +
                SALES + " INTEGER DEFAULT 0," +
                TO_SUPPLY + " BOOLEAN DEFAULT TRUE," +
                IS_OPEN_ORDER + " BOOLEAN DEFAULT FALSE" +
                ");";

      try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
        stmt.execute(sql);
      
    } catch (SQLException e) {
        throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
    }
}

    public void insert(ItemTypeDto item) {
         String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + ID + " = ?";
        String insertSql = "INSERT INTO " + TABLE_NAME + " (" +
                ID + ", " + NAME + ", " +
                ORIGINAL_PRICE + ", " + ITEM_DISCOUNT + ", " + EXPIRATION_DATE_ITEM_DISCOUNT + ", " +
                CATEGORY_DISCOUNT + ", " + EXPIRATION_DATE_CATEGORY_DISCOUNT + ", " + COST + ", " +
                MANUFACTURE + ", " + MAIN_CATEGORY + ", " + SUB_CATEGORY + ", " + SUB_SUB_CATEGORY + ", " +
                SALES + ", " + TO_SUPPLY + ", " + IS_OPEN_ORDER +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection()) {
            // שלב 1: לבדוק אם כבר קיים עם אותו ID
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, item.getId());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    return; // כבר קיים → לא מכניסים שוב
                }
            }

            // שלב 2: להכניס חדש
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, item.getId());
                pstmt.setString(2, item.getName());
                pstmt.setDouble(3, item.getOriginalPrice());
                pstmt.setDouble(4, item.getItemDiscount());
                pstmt.setDate(5, item.getExpirationDateItemDiscount());
                pstmt.setDouble(6, item.getCategoryDiscount());
                pstmt.setDate(7, item.getExpirationDateCategoryDiscount());
                pstmt.setDouble(8, item.getCost());
                pstmt.setString(9, item.getManufacture());
                pstmt.setString(10, item.getMainCategory());
                pstmt.setString(11, item.getSubCategory());
                pstmt.setString(12, item.getSubSubCategory());
                pstmt.setInt(13, item.getSales());
                pstmt.setBoolean(14, item.toSupply());
                pstmt.setBoolean(15, item.isOpenOrder());

                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error persisting item type: " + e.getMessage(), e);
        }
    }

    public boolean update(ItemTypeDto item) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                ORIGINAL_PRICE + " = ?, " + ITEM_DISCOUNT + " = ?, " +
                EXPIRATION_DATE_ITEM_DISCOUNT + " = ?, " + CATEGORY_DISCOUNT + " = ?, " +
                EXPIRATION_DATE_CATEGORY_DISCOUNT + " = ?, " + COST + " = ?, " +
                MAIN_CATEGORY + " = ?, " + SUB_CATEGORY + " = ?, " + SUB_SUB_CATEGORY + " = ?, " +
                SALES + " = ?, " + TO_SUPPLY + " = ? WHERE " + ID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, item.getOriginalPrice());
            pstmt.setDouble(2, item.getItemDiscount());
            pstmt.setDate(3, item.getExpirationDateItemDiscount());
            pstmt.setDouble(4, item.getCategoryDiscount());
            pstmt.setDate(5, item.getExpirationDateCategoryDiscount());
            pstmt.setDouble(6, item.getCost());
            pstmt.setString(7, item.getMainCategory());
            pstmt.setString(8, item.getSubCategory());
            pstmt.setString(9, item.getSubSubCategory());
            pstmt.setInt(10, item.getSales());
            pstmt.setBoolean(11, item.toSupply());
            pstmt.setInt(12, item.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
           
        }
        return false;
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            
        }
    }

    public List<ItemTypeDto> getAll() {
        List<ItemTypeDto> items = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(new ItemTypeDto(
                        rs.getInt(ID),
                        rs.getString(NAME),
                        rs.getDouble(ORIGINAL_PRICE),
                        rs.getDouble(ITEM_DISCOUNT),
                        rs.getDate(EXPIRATION_DATE_ITEM_DISCOUNT),
                        rs.getDouble(CATEGORY_DISCOUNT),
                        rs.getDate(EXPIRATION_DATE_CATEGORY_DISCOUNT),
                        rs.getDouble(COST),
                        rs.getString(MANUFACTURE),
                        rs.getString(MAIN_CATEGORY),
                        rs.getString(SUB_CATEGORY),
                        rs.getString(SUB_SUB_CATEGORY),
                        rs.getInt(SALES),
                        rs.getBoolean(TO_SUPPLY),
                        rs.getBoolean(IS_OPEN_ORDER)
                ));
            }

        } catch (SQLException e) {
           
        }
        return items;
    }

    public ItemTypeDto find(int id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                new ItemTypeDto(
                        rs.getInt(ID),
                        rs.getString(NAME),
                        rs.getDouble(ORIGINAL_PRICE),
                        rs.getDouble(ITEM_DISCOUNT),
                        rs.getDate(EXPIRATION_DATE_ITEM_DISCOUNT),
                        rs.getDouble(CATEGORY_DISCOUNT),
                        rs.getDate(EXPIRATION_DATE_CATEGORY_DISCOUNT),
                        rs.getDouble(COST),
                        rs.getString(MANUFACTURE),
                        rs.getString(MAIN_CATEGORY),
                        rs.getString(SUB_CATEGORY),
                        rs.getString(SUB_SUB_CATEGORY),
                        rs.getInt(SALES),
                        rs.getBoolean(TO_SUPPLY),
                        rs.getBoolean(IS_OPEN_ORDER)
                );
            }

        } catch (SQLException e) {
           
        }
        return null;
    }

    public void deleteAll() {
        String sql = "DELETE FROM " + TABLE_NAME;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
           
        }
    }
}
