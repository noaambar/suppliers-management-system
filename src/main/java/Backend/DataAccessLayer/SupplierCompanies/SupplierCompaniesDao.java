package Backend.DataAccessLayer.SupplierCompanies;

import DataTransferLayer.SupplierCompaniesDto;

import java.util.List;

public interface SupplierCompaniesDao {
    public void create(SupplierCompaniesDto dto);

    public List<SupplierCompaniesDto> readAllBySupplier(int supplierID);

    public List<SupplierCompaniesDto> readAll();

    public void delete(int supplierID,String company);
    
    public void deleteAll();
}
