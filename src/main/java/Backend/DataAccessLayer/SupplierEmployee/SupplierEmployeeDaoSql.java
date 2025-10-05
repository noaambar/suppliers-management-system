package Backend.DataAccessLayer.SupplierEmployee;

import DataTransferLayer.SupplierEmployeeDto;

import java.util.List;

public class SupplierEmployeeDaoSql implements SupplierEmployeeDao{

    private final SupplierEmployeeController controller = new SupplierEmployeeController();

    @Override
    public void create(SupplierEmployeeDto dto) {
        controller.insert(dto);
    }

    @Override
    public List<SupplierEmployeeDto> readAllBySupplier(int supplierID) {
        return controller.find(supplierID);
    }

    @Override
    public List<SupplierEmployeeDto> readAll() {
        return controller.getAll();
    }

    @Override
    public void delete(int supplierID, int employeeID) {
        controller.delete(supplierID, employeeID);
    }

    @Override
    public void deletebyEmployee(int employeeID) {
        controller.deleteByEmployee(employeeID);
    }

    @Override
    public void deleteAll() {
        controller.deleteAll();
    }
}
