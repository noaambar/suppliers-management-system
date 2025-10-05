package Backend.DataAccessLayer.Employee;


import DataTransferLayer.EmployeeDto;

import java.util.List;

public class EmployeeDaoSql implements EmployeeDao {
    private final EmployeeController controller = new EmployeeController();

    @Override
    public void create(EmployeeDto dto) {
        controller.insert(dto);
    }

    @Override
    public EmployeeDto read(int employeeId) {
        return controller.find(employeeId);
    }

    @Override
    public List<EmployeeDto> readAll() {
        return controller.getAll();
    }

    @Override
    public void delete(int employeeId) {
        controller.delete(employeeId);
    }

    @Override
    public void deleteAll() {
        controller.deleteAll();
    }
}
