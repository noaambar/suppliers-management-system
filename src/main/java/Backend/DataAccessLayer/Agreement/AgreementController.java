package Backend.DataAccessLayer.Agreement;

import DataTransferLayer.SupplierAgreementDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;

public class AgreementController {
    private static final String DB_URL = DatabaseConnector.getDbUrl();
    public static final String TABLE_NAME = "Supplier_Agreements";

    public static final String SupplierAgreementID = "supplier_agreement_ID";
    public static final String supplierID = "supplier_ID";
    public static final String paymentMethod = "payment_method";
    public static final String paymentTime = "payment_time";

    public AgreementController() {
        createAgreementControllerTableIfNotExists();
    }

    private void createAgreementControllerTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                SupplierAgreementID + " INTEGER PRIMARY KEY, " +
                supplierID + " INTEGER NOT NULL, " +
                paymentMethod + " TEXT NOT NULL, " +
                paymentTime + " TEXT NOT NULL" +
                ");";

        try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
            stmt.execute(sql);
           
        } catch (SQLException e) {
            throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
        }
    }

    public void insert(SupplierAgreementDto agreement) {
        String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + SupplierAgreementID + " = ?";
        String insertSql = "INSERT INTO " + TABLE_NAME + " (" +
                SupplierAgreementID + ", " + supplierID + ", " +
                paymentMethod + ", " + paymentTime + ") VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection()) {
            // לבדוק אם קיים
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, agreement.getSupplierAgreementID());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    return; // קיים → לא מכניסים שוב
                }
            }

            // אם לא קיים → להכניס חדש
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, agreement.getSupplierAgreementID());
                insertStmt.setInt(2, agreement.getSupplierID());
                insertStmt.setString(3, agreement.getPaymentMethod());
                insertStmt.setString(4, agreement.getPaymentTime());
                insertStmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error persisting supplier agreement: " + e.getMessage(), e);
        }
    }

    public void delete(int agreementId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + SupplierAgreementID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agreementId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting supplier agreement: " + e.getMessage(), e);
        }
    }

    public void deleteAll() {
        String sql = "DELETE FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all supplier agreements: " + e.getMessage(), e);
        }
    }

    public SupplierAgreementDto find(int agreementId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + SupplierAgreementID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agreementId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt(SupplierAgreementID);
                int supID = rs.getInt(supplierID);
                String payMethod = rs.getString(paymentMethod);
                String payTime = rs.getString(paymentTime);
                return new SupplierAgreementDto(id, supID, payMethod, payTime);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding supplier agreement: " + e.getMessage(), e);
        }

        return null;
    }

    public List<SupplierAgreementDto> getAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<SupplierAgreementDto> agreements = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt(SupplierAgreementID);
                int supID = rs.getInt(supplierID);
                String payMethod = rs.getString(paymentMethod);
                String payTime = rs.getString(paymentTime);
                agreements.add(new SupplierAgreementDto(id, supID, payMethod, payTime));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all supplier agreements: " + e.getMessage(), e);
        }

        return agreements;
    }

    public List<SupplierAgreementDto> getAllBySupplier(int supplierId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + supplierID + " = ?";
        List<SupplierAgreementDto> agreements = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt(SupplierAgreementID);
                int supID = rs.getInt(supplierID);
                String payMethod = rs.getString(paymentMethod);
                String payTime = rs.getString(paymentTime);
                agreements.add(new SupplierAgreementDto(id, supID, payMethod, payTime));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving supplier agreements by supplier: " + e.getMessage(), e);
        }

        return agreements;
    }

    public boolean update(SupplierAgreementDto agreement) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                supplierID + " = ?, " +
                paymentMethod + " = ?, " +
                paymentTime + " = ? " +
                "WHERE " + SupplierAgreementID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, agreement.getSupplierID());
            pstmt.setString(2, agreement.getPaymentMethod());
            pstmt.setString(3, agreement.getPaymentTime());
            pstmt.setInt(4, agreement.getSupplierAgreementID());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
          
            return false;
        }
    }

}
