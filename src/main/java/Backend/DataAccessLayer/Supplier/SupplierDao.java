package Backend.DataAccessLayer.Supplier;


import DataTransferLayer.SupplierDto;

import java.util.List;

public interface SupplierDao {
    void create(DataTransferLayer.SupplierDto supplierDto);
    void delete(int supplierID);
    void deleteAll();
    DataTransferLayer.SupplierDto read(int supplierID);
    List<DataTransferLayer.SupplierDto> readAll();
    void update(SupplierDto newDto);

}
