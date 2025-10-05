package Backend.DataAccessLayer.SupplierEmployee;

import DataTransferLayer.SupplierEmployeeDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;

public class SupplierEmployeeController {
    private static final String DB_URL =DatabaseConnector.getDbUrl();
    public static final String TABLE_NAME = "Supplier_Employees";

    public static final String supplierID = "supplier_ID";
    public static final String employeeID = "employee_ID";

    public SupplierEmployeeController() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                supplierID + " INTEGER NOT NULL, " +
                employeeID + " INTEGER NOT NULL, " +
                "PRIMARY KEY (" + supplierID + ", " + employeeID + ")" +
                ");";

    try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
        stmt.execute(sql);
      
    } catch (SQLException e) {
        throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
    }
}

    public void insert(SupplierEmployeeDto dto) {
        String sql = "INSERT INTO " + TABLE_NAME + " (" + supplierID + ", " + employeeID + ") VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dto.getSupplierID());
            pstmt.setInt(2, dto.getEmployeeID());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting supplier employee: " + e.getMessage(), e);
        }
    }

    public void delete(int supplierIDVal, int employeeIDVal) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + supplierID + " = ? AND " + employeeID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierIDVal);
            pstmt.setInt(2, employeeIDVal);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting supplier employee: " + e.getMessage(), e);
        }
    }

    public void deleteAll() {
        String sql = "DELETE FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all supplier employees: " + e.getMessage(), e);
        }
    }

    public List<SupplierEmployeeDto> find(int supplierIDVal) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + supplierID + " = ?";
        List<SupplierEmployeeDto> employees = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierIDVal);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int employeeIDVal = rs.getInt(employeeID);
                employees.add(new SupplierEmployeeDto(supplierIDVal, employeeIDVal));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding supplier employees: " + e.getMessage(), e);
        }

        return employees;
    }



    public List<SupplierEmployeeDto> getAll() {
        List<SupplierEmployeeDto> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int sid = rs.getInt(supplierID);
                int eid = rs.getInt(employeeID);


                list.add(new SupplierEmployeeDto(sid, eid));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all supplier employees: " + e.getMessage(), e);
        }

        return list;
    }


    public boolean update(SupplierEmployeeDto dto, Integer newSupplierID, Integer newEmployeeID) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                supplierID + " = ?, " +
                employeeID + " = ? " +
                "WHERE " + supplierID + " = ? AND " + employeeID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newSupplierID);
            pstmt.setInt(2, newEmployeeID);
            pstmt.setInt(3, dto.getSupplierID());
            pstmt.setInt(4, dto.getEmployeeID());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
           
            return false;
        }
    }

    public void deleteByEmployee(int employeeID2) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + employeeID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeID2);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting supplier employees by employee ID: " + e.getMessage(), e);
        }
    }
}
