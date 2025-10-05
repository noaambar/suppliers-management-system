package Backend.DataAccessLayer.ItemType;

import java.util.List;

import DataTransferLayer.ItemTypeDto;

public class ItemTypeDaoSql implements ItemTypeDao {

    private ItemTypeController controller = new ItemTypeController();

    @Override
    public void create(ItemTypeDto item) {
        controller.insert(item);
    }

    @Override
    public ItemTypeDto read(int itemsID) {
       return controller.find(itemsID);
    }

    @Override
    public List<ItemTypeDto> readAll() {
        return controller.getAll();
    }

    @Override
    public void update(ItemTypeDto item) {
        controller.update(item);
    }

    @Override
    public void delete(int itemsID) {
        controller.deleteById(itemsID);
    }

    @Override
    public void deleteAll() {
        controller.deleteAll();
    }

 

}