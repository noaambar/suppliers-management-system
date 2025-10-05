package Backend.DataAccessLayer.Employee;


import DataTransferLayer.EmployeeDto;

import java.util.List;

public interface EmployeeDao {
    void create(EmployeeDto dto);
    EmployeeDto read(int employeeId);
    List<EmployeeDto> readAll();
    void delete(int employeeId);
    void deleteAll();
}
