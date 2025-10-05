package Backend.DataAccessLayer.Employee;


import DataTransferLayer.EmployeeDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Backend.DataAccessLayer.DatabaseConnector;

public class EmployeeController {
    private static final String DB_URL = DatabaseConnector.getDbUrl();
    public static final String TABLE_NAME = "Employees";

    public static final String EMPLOYEE_ID = "employee_ID";
    public static final String NAME = "name";
    public static final String PHONE_NUMBER = "phone_number";

    public EmployeeController() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                EMPLOYEE_ID + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT NOT NULL, " +
                PHONE_NUMBER + " TEXT NOT NULL" +
                ");";

            try (Statement stmt = DatabaseConnector.getConnection().createStatement()) {
                stmt.execute(sql);
              
            } catch (SQLException e) {
                throw new RuntimeException("Error creating " + TABLE_NAME + " table: " + e.getMessage(), e);
            }
        }

    public void insert(EmployeeDto dto) {
        String checkSql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + EMPLOYEE_ID + " = ?";
        String insertSql = "INSERT INTO " + TABLE_NAME + " (" + EMPLOYEE_ID + ", " + NAME + ", " + PHONE_NUMBER + ") VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, dto.getEmployeeId());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    return; // כבר קיים → לא מכניסים שוב
                }
            }

            // שלב 2: להכניס חדש
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, dto.getEmployeeId());
                pstmt.setString(2, dto.getName());
                pstmt.setString(3, dto.getPhoneNumber());
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error persisting employee: " + e.getMessage(), e);
        }
    }

    public EmployeeDto find(int employeeId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + EMPLOYEE_ID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new EmployeeDto(
                        rs.getInt(EMPLOYEE_ID),
                        rs.getString(NAME),
                        rs.getString(PHONE_NUMBER)
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding employee: " + e.getMessage(), e);
        }

        return null;
    }

    public List<EmployeeDto> getAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<EmployeeDto> employees = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employees.add(new EmployeeDto(
                        rs.getInt(EMPLOYEE_ID),
                        rs.getString(NAME),
                        rs.getString(PHONE_NUMBER)
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employees: " + e.getMessage(), e);
        }

        return employees;
    }

    public void delete(int employeeId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + EMPLOYEE_ID + " = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting employee: " + e.getMessage(), e);
        }
    }

    public void deleteAll() {
        String sql = "DELETE FROM " + TABLE_NAME;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all employees: " + e.getMessage(), e);
        }
    }

}

