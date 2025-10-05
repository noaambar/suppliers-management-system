package Backend.DataAccessLayer.Agreement;

import DataTransferLayer.SupplierAgreementDto;

import java.util.List;

public class AgreementDaoSql implements AgreementDao {

    private AgreementController controller = new AgreementController();

    @Override
    public void create(SupplierAgreementDto agreement) {
        controller.insert(agreement);
    }

    @Override
    public SupplierAgreementDto read(int agreementID) {
        return controller.find(agreementID);
    }

    @Override
    public List<SupplierAgreementDto> readAll() {
        return controller.getAll();
    }

    @Override
    public List<SupplierAgreementDto> readAllBySupplier(int supplierID) {
        return controller.getAllBySupplier(supplierID);
    }

    @Override
    public void update(SupplierAgreementDto agreement) {
        controller.update(agreement);
    }

    @Override
    public void delete(int agreementID) {
        controller.delete(agreementID);
    }

    @Override
    public void deleteAll() {
        controller.deleteAll();
    }

}
