package repos;

import java.util.List;
import java.util.Map;

public interface MapRepo<T, V> {

  void add(T key, V itemToAdd);

  void remove(T key);

  V get(T key);

  List<T> getAllKeys();

  List<V> getAllItems();

  Map<T, V> getEntries();


}
