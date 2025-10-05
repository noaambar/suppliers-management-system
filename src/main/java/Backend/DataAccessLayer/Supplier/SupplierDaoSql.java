package Backend.DataAccessLayer.Supplier;

import DataTransferLayer.SupplierDto;

import java.util.List;

public class SupplierDaoSql implements  SupplierDao{
        private final SupplierController controller = new SupplierController();

        public void create(SupplierDto dto) {
            controller.insert(dto);
        }

        public SupplierDto read(int supplierID) {
            return controller.find(supplierID);
        }

        public List<SupplierDto> readAll() {
            return controller.getAll();
        }

        public void update(SupplierDto dto) {
            controller.update(dto);
        }

        public void delete(int supplierID) {
            controller.delete(supplierID);
        }

        public void deleteAll() {
            controller.deleteAll();
        }

}
