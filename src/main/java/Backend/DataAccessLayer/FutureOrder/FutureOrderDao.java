package Backend.DataAccessLayer.FutureOrder;

import java.util.List;

import DataTransferLayer.FutureOrderDto;

public interface FutureOrderDao {
    void create(FutureOrderDto order);
    List<FutureOrderDto> read(int day);
    List<FutureOrderDto> readAll();
    void update(FutureOrderDto order);
    void delete(int day);
    void deleteAll();
  
}