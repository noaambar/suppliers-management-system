package Backend.DataAccessLayer.ItemInstance;

import java.util.List;

import DataTransferLayer.ItemInstanceDto;

public interface ItemInstanceDao {

    void create(ItemInstanceDto item);
    ItemInstanceDto read(int itemTypeId, int itemInstanceId);
    List<ItemInstanceDto> readItems(int itemTypeId);
    void update(ItemInstanceDto item);
    void delete(int itemTypeId, int itemInstanceId);
    void deleteAll();

}