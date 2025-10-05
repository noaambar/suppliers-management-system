package Backend.DataAccessLayer.Supplier;

import DataTransferLayer.SupplierDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;

public class SupplierController {
    private static final String DB_URL = DatabaseConnector.getDbUrl();
    public static final String TABLE_NAME = "Suppliers";

    public static final String SUPPLIER_ID = "supplier_id";
    public static final String SUPPLIER_NAME = "supplier_name";
    public static final String ADDRESS = "address";
    public static final String BANK_ACCOUNT = "bank_account";
    public static final String IS_TRANSPORT = "is_transport";
    public static final String IS_DAYS = "is_days";
    public static final String SUNDAY = "sunday";
    public static final String MONDAY = "monday";
    public static final String TUESDAY = "tuesday";
    public static final String WEDNESDAY = "wednesday";
    public static final String THURSDAY = "thursday";
    public static final String FRIDAY = "friday";
    public static final String SATURDAY = "saturday";
    public static final String COUNTSUPPLIERAGREEMENT = "count_supplier_agreement";
    public static final String FAIR_PHONE_NUMBER = "fair_phone_number";

    public SupplierController(){
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                SUPPLIER_ID + " INTEGER PRIMARY KEY, " +
                SUPPLIER_NAME + " TEXT NOT NULL, " +
                ADDRESS + " TEXT NOT NULL, " +
                BANK_ACCOUNT + " INTEGER NOT NULL, " +
                IS_TRANSPORT + " BOOLEAN NOT NULL, " +
                IS_DAYS + " BOOLEAN NOT NULL, " +
                SUNDAY + " BOOLEAN NOT NULL, " +
                MONDAY + " BOOLEAN NOT NULL, " +
                TUESDAY + " BOOLEAN NOT NULL, " +
                WEDNESDAY + " BOOLEAN NOT NULL, " +
                THURSDAY + " BOOLEAN NOT NULL, " +
                FRIDAY + " BOOLEAN NOT NULL, " +
                SATURDAY + " BOOLEAN NOT NULL, " +
                COUNTSUPPLIERAGREEMENT + " INTEGER NOT NULL, " +
                FAIR_PHONE_NUMBER + " INTEGER NOT NULL" +
                ");";

      try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
        stmt.execute(sql);
     
    } catch (SQLException e) {
        throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
    }
}
    public void insert(SupplierDto dto) {
        String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + SUPPLIER_ID + " = ?";
        String insertSql = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Step 1: Check if supplier with the same ID already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, dto.getSupplierID());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Supplier already exists → do not insert
                    return;
                }
            }

            // Step 2: Insert new supplier
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, dto.getSupplierID());
                pstmt.setString(2, dto.getSupplierName());
                pstmt.setString(3, dto.getAddress());
                pstmt.setInt(4, dto.getBankAccount());
                pstmt.setBoolean(5, dto.getIsTransport());
                pstmt.setBoolean(6, dto.getIsDays());
                pstmt.setBoolean(7, dto.getSunday());
                pstmt.setBoolean(8, dto.getMonday());
                pstmt.setBoolean(9, dto.getTuesday());
                pstmt.setBoolean(10, dto.getWednesday());
                pstmt.setBoolean(11, dto.getThursday());
                pstmt.setBoolean(12, dto.getFriday());
                pstmt.setBoolean(13, dto.getSaturday());
                pstmt.setInt(14, dto.getCountSupplierAgreement());
                pstmt.setInt(15, dto.getFairPhoneNumber());

                pstmt.executeUpdate();
                
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting supplier: " + e.getMessage(), e);
        }
    }


    public void delete(int supplierId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + SUPPLIER_ID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting supplier: " + e.getMessage(), e);
        }
    }

    public void deleteAll() {
        String sql = "DELETE FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all suppliers: " + e.getMessage(), e);
        }
    }

    public SupplierDto find(int supplierId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + SUPPLIER_ID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new SupplierDto(
                        rs.getString(SUPPLIER_NAME),
                        rs.getInt(SUPPLIER_ID),
                        rs.getString(ADDRESS),
                        rs.getInt(BANK_ACCOUNT),
                        rs.getBoolean(IS_TRANSPORT),
                        rs.getBoolean(IS_DAYS),
                        rs.getBoolean(SUNDAY),
                        rs.getBoolean(MONDAY),
                        rs.getBoolean(TUESDAY),
                        rs.getBoolean(WEDNESDAY),
                        rs.getBoolean(THURSDAY),
                        rs.getBoolean(FRIDAY),
                        rs.getBoolean(SATURDAY),
                        rs.getInt(COUNTSUPPLIERAGREEMENT),
                        rs.getInt(FAIR_PHONE_NUMBER)
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding supplier: " + e.getMessage(), e);
        }

        return null;
    }

    public List<SupplierDto> getAll() {
        List<SupplierDto> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new SupplierDto(
                        rs.getString(SUPPLIER_NAME),
                        rs.getInt(SUPPLIER_ID),
                        rs.getString(ADDRESS),
                        rs.getInt(BANK_ACCOUNT),
                        rs.getBoolean(IS_TRANSPORT),
                        rs.getBoolean(IS_DAYS),
                        rs.getBoolean(SUNDAY),
                        rs.getBoolean(MONDAY),
                        rs.getBoolean(TUESDAY),
                        rs.getBoolean(WEDNESDAY),
                        rs.getBoolean(THURSDAY),
                        rs.getBoolean(FRIDAY),
                        rs.getBoolean(SATURDAY),
                        rs.getInt(COUNTSUPPLIERAGREEMENT),
                        rs.getInt(FAIR_PHONE_NUMBER)
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all suppliers: " + e.getMessage(), e);
        }

        return list;
    }

    public boolean update(SupplierDto dto) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                SUPPLIER_NAME + " = ?, " +
                ADDRESS + " = ?, " +
                BANK_ACCOUNT + " = ?, " +
                IS_TRANSPORT + " = ?, " +
                IS_DAYS + " = ?, " +
                SUNDAY + " = ?, " +
                MONDAY + " = ?, " +
                TUESDAY + " = ?, " +
                WEDNESDAY + " = ?, " +
                THURSDAY + " = ?, " +
                FRIDAY + " = ?, " +
                SATURDAY + " = ?, " +
                COUNTSUPPLIERAGREEMENT + " = ?, " +
                FAIR_PHONE_NUMBER + " = ? " +
                "WHERE " + SUPPLIER_ID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getSupplierName());
            pstmt.setString(2, dto.getAddress());
            pstmt.setInt(3, dto.getBankAccount());
            pstmt.setBoolean(4, dto.getIsTransport());
            pstmt.setBoolean(6, dto.getIsDays());
            pstmt.setBoolean(7, dto.getSunday());
            pstmt.setBoolean(8, dto.getMonday());
            pstmt.setBoolean(9, dto.getTuesday());
            pstmt.setBoolean(10, dto.getWednesday());
            pstmt.setBoolean(11, dto.getThursday());
            pstmt.setBoolean(12, dto.getFriday());
            pstmt.setBoolean(13, dto.getSaturday());
            pstmt.setInt(13, dto.getCountSupplierAgreement());
            pstmt.setInt(14, dto.getSupplierID());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
         
            return false;
        }
    }
}
