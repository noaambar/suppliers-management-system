package Backend.DataAccessLayer.ProductPrice;

import DataTransferLayer.ProductPriceDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;

public class ProductPriceController {
    private static final String DB_URL = DatabaseConnector.getDbUrl();
    public static final String TABLE_NAME = "Product_Prices";
    private static final String supplierID = "supplier_ID";
    private static final String productID = "product_ID";
    private static final String agreementID = "agreement_ID";
    private static final String price = "price";

    public ProductPriceController() {
        createProductPriceTableIfNotExists();
    }

    private void createProductPriceTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                supplierID + " INTEGER NOT NULL, " +
                productID + " INTEGER NOT NULL, " +
                agreementID + " INTEGER NOT NULL, " +
                price + " DOUBLE NOT NULL, " +
                "PRIMARY KEY (" + supplierID + ", " + productID + ", " + agreementID + ")" +
                ");";

        try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
        }
    }

    public void insert(ProductPriceDto productPrice) {
        String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " +
                supplierID + " = ? AND " + productID + " = ? AND " + agreementID + " = ?";
        String insertSql = "INSERT INTO " + TABLE_NAME + " (" +
                supplierID + ", " + productID + ", " + agreementID + ", " + price + ") VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, productPrice.getSupplierID());
                checkStmt.setInt(2, productPrice.getProductID());
                checkStmt.setInt(3, productPrice.getAgreementID());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    return; // Already exists
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, productPrice.getSupplierID());
                insertStmt.setInt(2, productPrice.getProductID());
                insertStmt.setInt(3, productPrice.getAgreementID());
                insertStmt.setDouble(4, productPrice.getPrice());
                insertStmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting product price: " + e.getMessage(), e);
        }
    }

    public void delete(int supplierIdVal, int agreementIDVal, int productIDVal) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " +
                supplierID + " = ? AND " + agreementID + " = ? AND " + productID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierIdVal);
            pstmt.setInt(2, agreementIDVal);
            pstmt.setInt(3, productIDVal);
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

    public ProductPriceDto find(int supplierIDVal, int productIDVal, int agreementIDVal) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                supplierID + " = ? AND " + productID + " = ? AND " + agreementID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierIDVal);
            pstmt.setInt(2, productIDVal);
            pstmt.setInt(3, agreementIDVal);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double priceVal = rs.getDouble(price);
                return new ProductPriceDto(supplierIDVal, agreementIDVal, productIDVal, priceVal);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding product price: " + e.getMessage(), e);
        }

        return null;
    }

    public List<ProductPriceDto> getAllByAgreement(int supplierIDVal, int agreementIDVal) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                supplierID + " = ? AND " + agreementID + " = ?";
        List<ProductPriceDto> productPrices = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierIDVal);
            pstmt.setInt(2, agreementIDVal);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int productIDVal = rs.getInt(productID);
                double priceVal = rs.getDouble(price);
                productPrices.add(new ProductPriceDto(supplierIDVal, agreementIDVal, productIDVal, priceVal));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving product prices by agreement: " + e.getMessage(), e);
        }

        return productPrices;
    }

    public List<ProductPriceDto> getAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<ProductPriceDto> productPrices = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int supplierIDVal = rs.getInt(supplierID);
                int productIDVal = rs.getInt(productID);
                int agreementIDVal = rs.getInt(agreementID);
                double priceVal = rs.getDouble(price);
                productPrices.add(new ProductPriceDto(supplierIDVal, agreementIDVal, productIDVal, priceVal));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all product prices: " + e.getMessage(), e);
        }

        return productPrices;
    }

    public boolean update(ProductPriceDto productPrice) {
        String sql = "UPDATE " + TABLE_NAME + " SET " + price + " = ? WHERE " +
                supplierID + " = ? AND " + productID + " = ? AND " + agreementID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, productPrice.getPrice());
            pstmt.setInt(2, productPrice.getSupplierID());
            pstmt.setInt(3, productPrice.getProductID());
            pstmt.setInt(4, productPrice.getAgreementID());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }
}
