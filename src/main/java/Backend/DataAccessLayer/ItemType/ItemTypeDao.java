package Backend.DataAccessLayer.ItemType;

import java.util.List;

import DataTransferLayer.ItemTypeDto;

public interface ItemTypeDao {
    void create(ItemTypeDto item);
    ItemTypeDto read(int itemTypeId);
    List<ItemTypeDto> readAll();
    void update(ItemTypeDto item);
    void delete(int itemTypId);
    void deleteAll();
    
}
