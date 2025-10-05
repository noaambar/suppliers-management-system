package Backend.DataAccessLayer.ProductQuantityDiscount;

import DataTransferLayer.ProductQuantityDiscountDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;

public class ProductQuantityDiscountController {
    private static final String DB_URL = DatabaseConnector.getDbUrl();
    public static final String TABLE_NAME = "Product_Quantity_Discount";
    public static final String supplierID = "supplier_ID";
    public static final String agreementID = "agreement_ID";
    public static final String productID = "product_ID";
    public static final String quantity = "quantity";
    public static final String discountPercentage = "discount_percentage";

    public ProductQuantityDiscountController() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                supplierID + " INTEGER NOT NULL," +
                agreementID + " INTEGER NOT NULL, " +
                productID + " INTEGER NOT NULL, " +
                quantity + " INTEGER NOT NULL, " +
                discountPercentage + " INTEGER NOT NULL, " +
                "PRIMARY KEY (" + agreementID + ", " + productID + ", " + quantity + ")" +
                ");";

        try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
        }
    }

    public void insert(ProductQuantityDiscountDto discount) {
        String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " +
                agreementID + " = ? AND " + productID + " = ? AND " + quantity + " = ?";

        String insertSql = "INSERT INTO " + TABLE_NAME + " (" +
                supplierID + ", " + agreementID + ", " + productID + ", " + quantity + ", " + discountPercentage +
                ") VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, discount.getAgreementID());
                checkStmt.setInt(2, discount.getProductID());
                checkStmt.setInt(3, discount.getQuantity());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    return; // Already exists
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, discount.getSupplierID());
                insertStmt.setInt(2, discount.getAgreementID());
                insertStmt.setInt(3, discount.getProductID());
                insertStmt.setInt(4, discount.getQuantity());
                insertStmt.setInt(5, discount.getDiscountPercentage());
                insertStmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting product quantity discount: " + e.getMessage(), e);
        }
    }

    public void delete(int supplier, int agreementId, int productIDValue, int quantityValue, int discount) {
        String sql = "DELETE FROM " + TABLE_NAME
                + " WHERE " + supplierID + " = ? AND " + agreementID + " = ? AND "
                + productID + " = ? AND "
                + quantity + " = ? AND "
                + discountPercentage + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplier);
            pstmt.setInt(2, agreementId);
            pstmt.setInt(3, productIDValue);
            pstmt.setInt(4, quantityValue);
            pstmt.setInt(5, discount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product quantity discount: " + e.getMessage(), e);
        }
    }

    public void deleteAll() {
        String sql = "DELETE FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all discounts: " + e.getMessage(), e);
        }
    }

    public ProductQuantityDiscountDto find(int agreementIdValue, int productIDKey, int quantityKey) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                agreementID + " = ? AND " + productID + " = ? AND " + quantity + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agreementIdValue);
            pstmt.setInt(2, productIDKey);
            pstmt.setInt(3, quantityKey);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new ProductQuantityDiscountDto(
                        rs.getInt(supplierID),
                        rs.getInt(agreementID),
                        rs.getInt(productID),
                        rs.getInt(quantity),
                        rs.getInt(discountPercentage));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding product quantity discount: " + e.getMessage(), e);
        }

        return null;
    }

    public List<ProductQuantityDiscountDto> getAllByAgreement(int supplierIdValue, int agreementIdValue) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + agreementID + " = ? AND " + supplierID + " = ?";
        List<ProductQuantityDiscountDto> discounts = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agreementIdValue);
            pstmt.setInt(2, supplierIdValue);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                discounts.add(new ProductQuantityDiscountDto(
                        rs.getInt(supplierID),
                        rs.getInt(agreementID),
                        rs.getInt(productID),
                        rs.getInt(quantity),
                        rs.getInt(discountPercentage)));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving discounts by agreement: " + e.getMessage(), e);
        }
        return discounts;
    }

    public List<ProductQuantityDiscountDto> getAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<ProductQuantityDiscountDto> discounts = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                discounts.add(new ProductQuantityDiscountDto(
                        rs.getInt(supplierID),
                        rs.getInt(agreementID),
                        rs.getInt(productID),
                        rs.getInt(quantity),
                        rs.getInt(discountPercentage)));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all product quantity discounts: " + e.getMessage(), e);
        }
        return discounts;
    }

    public boolean update(ProductQuantityDiscountDto discount) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                discountPercentage + " = ? WHERE " +
                productID + " = ? AND " + quantity + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, discount.getDiscountPercentage());
            pstmt.setInt(2, discount.getProductID());
            pstmt.setInt(3, discount.getQuantity());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public void deleteByProduct(int supplierValue, int agreementIdValue, int productIdValue) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " +
                supplierID + " = ? AND " + agreementID + " = ? AND " + productID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierValue);
            pstmt.setInt(2, agreementIdValue);
            pstmt.setInt(3, productIdValue);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product by ID: " + e.getMessage(), e);
        }
    }
}
