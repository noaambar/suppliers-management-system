package Backend.ServiceLayer;

public class ServiceFactory {
    private ItemService itemService;
    private SupplierService supplierService;

    public ServiceFactory() {
        itemService = new ItemService();
        supplierService = new SupplierService();
    }

    public ItemService getItemService() {
        return itemService;
    }

    public SupplierService getSupplierService() {
        return supplierService;
    }
    
}
