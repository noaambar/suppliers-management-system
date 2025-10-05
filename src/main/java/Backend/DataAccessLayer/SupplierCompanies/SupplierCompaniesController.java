package Backend.DataAccessLayer.SupplierCompanies;

import DataTransferLayer.SupplierCompaniesDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;

public class SupplierCompaniesController {
    private static final String DB_URL = DatabaseConnector.getDbUrl();
    public static final String TABLE_NAME = "Supplier_Companies";

    public static final String SUPPLIER_ID = "supplier_ID";
    public static final String COMPANY = "company";

    public SupplierCompaniesController() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
         String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            SUPPLIER_ID + " INTEGER NOT NULL, " +
            COMPANY + " TEXT NOT NULL, " +
            "PRIMARY KEY (" + SUPPLIER_ID + ", " + COMPANY + ")" +
            ");";

    try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
        stmt.execute(sql);
     
    } catch (SQLException e) {
        throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
    }
}

public void insert(SupplierCompaniesDto dto) {
    String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + SUPPLIER_ID + " = ? AND " + COMPANY + " = ?";
    String insertSql = "INSERT INTO " + TABLE_NAME + " (" + SUPPLIER_ID + ", " + COMPANY + ") VALUES (?, ?)";

    try (Connection conn = DriverManager.getConnection(DB_URL)) {
        // Step 1: Check if the (supplierID, company) pair already exists
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, dto.getSupplierID());
            checkStmt.setString(2, dto.getCompany());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // The pair already exists → do not insert
                return;
            }
        }

        // Step 2: Insert new record
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setInt(1, dto.getSupplierID());
            insertStmt.setString(2, dto.getCompany());
            insertStmt.executeUpdate();
        }

    } catch (SQLException e) {
        throw new RuntimeException("Error inserting supplier company (supplierID=" + dto.getSupplierID() +
                                   ", company=" + dto.getCompany() + "): " + e.getMessage(), e);
    }
}



    public void delete(int supplierID, String company) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + SupplierCompaniesController.SUPPLIER_ID + " = ? AND company = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierID);
            pstmt.setString(2, company);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting supplier company: " + e.getMessage(), e);
        }
    }


    public void deleteAll() {
        String sql = "DELETE FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all supplier companies: " + e.getMessage(), e);
        }
    }

    public List<SupplierCompaniesDto> find(int supplierIDVal) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + SUPPLIER_ID + " = ?";
        List<SupplierCompaniesDto> companies = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierIDVal);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String companyVal = rs.getString(COMPANY);
                companies.add(new SupplierCompaniesDto(supplierIDVal, companyVal));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding supplier companies: " + e.getMessage(), e);
        }

        return companies;
    }


    public List<SupplierCompaniesDto> getAll() {
        List<SupplierCompaniesDto> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int sid = rs.getInt(SUPPLIER_ID);
                String comp = rs.getString(COMPANY);
                list.add(new SupplierCompaniesDto(sid, comp));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all supplier companies: " + e.getMessage(), e);
        }

        return list;
    }

    public boolean update(SupplierCompaniesDto dto) {
        String sql = "UPDATE " + TABLE_NAME + " SET " + COMPANY + " = ? WHERE " + SUPPLIER_ID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getCompany());
            pstmt.setInt(2, dto.getSupplierID());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
          
            return false;
        }
    }
}
