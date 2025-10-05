package Backend.DataAccessLayer.Agreement;

import DataTransferLayer.SupplierAgreementDto;

import java.util.List;

public interface AgreementDao {
    void create(SupplierAgreementDto agreement);

    void delete(int agreementID);

    void deleteAll();

    SupplierAgreementDto read(int agreementID);

    List<SupplierAgreementDto> readAllBySupplier(int supplierID);

    List<SupplierAgreementDto> readAll();

    void update(SupplierAgreementDto agreement);
}
