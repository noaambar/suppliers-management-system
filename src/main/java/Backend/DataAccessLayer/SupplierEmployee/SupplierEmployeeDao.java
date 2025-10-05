package Backend.DataAccessLayer.SupplierEmployee;

import DataTransferLayer.SupplierEmployeeDto;

import java.util.List;

public interface SupplierEmployeeDao {
    public void create(SupplierEmployeeDto dto);

    public List<SupplierEmployeeDto> readAllBySupplier(int supplierID);

    public List<SupplierEmployeeDto> readAll();

    public void delete(int supplierID, int employeeID);

    public void deleteAll();

    public void deletebyEmployee(int employeeId);
}
