package Backend.DataAccessLayer.SupplierCompanies;


import DataTransferLayer.SupplierCompaniesDto;

import java.util.List;

public class SupplierCompaniesDaoSql implements SupplierCompaniesDao {

    private final SupplierCompaniesController controller = new SupplierCompaniesController();

    @Override
    public void create(SupplierCompaniesDto dto) {
        controller.insert(dto);
    }

    @Override
    public List<SupplierCompaniesDto> readAllBySupplier(int supplierID) {
        return controller.find(supplierID);

    }

    @Override
    public List<SupplierCompaniesDto> readAll() {
        return controller.getAll();
    }


    @Override
    public void delete(int supplierID, String company) {
        controller.delete(supplierID,company);
    }

    @Override
    public void deleteAll() {
        controller.deleteAll();
    }
}
