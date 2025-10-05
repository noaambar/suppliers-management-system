package Backend.DataAccessLayer.ItemInstance;

import java.util.List;

import DataTransferLayer.ItemInstanceDto;

public class ItemInstanceDaoSql implements ItemInstanceDao {
    private final ItemInstanceController controller = new ItemInstanceController();
    
    
    public void create(ItemInstanceDto item){
        controller.insert(item);
    }

    public ItemInstanceDto read(int itemsId, int id) {
        return controller.find(itemsId, id);
    }

    public List<ItemInstanceDto> readItems(int itemsId) {
        return controller.getItemsByItemsId(itemsId);
    }
    
    public void update(ItemInstanceDto item) {
        controller.update(item);
    }

    public void delete(int itemsId, int id) {
        controller.deleteItemById(itemsId, id);
    }
    public void deleteAll() {
        controller.deleteAll();
    }

}