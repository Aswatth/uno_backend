package repos;

import com.example.uno.models.Player;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerRepo implements MapRepo<String, Player> {

  private static PlayerRepo instance;
  private final Map<String, Player> playerMap;

  private PlayerRepo() {
    this.playerMap = new ConcurrentHashMap<>();
  }

  public static PlayerRepo getInstance() {
    if (instance == null) {
      instance = new PlayerRepo();
    }
    return instance;
  }

  @Override
  public void add(String key, Player itemToAdd) {
    this.playerMap.put(key, itemToAdd);
  }

  @Override
  public void remove(String key) {
    this.playerMap.remove(key);
  }

  @Override
  public Player get(String key) {
    return this.playerMap.get(key);
  }

  @Override
  public List<String> getAllKeys() {
    return this.playerMap.keySet().stream().toList();
  }

  @Override
  public List<Player> getAllItems() {
    return this.playerMap.values().stream().toList();
  }

  @Override
  public Map<String, Player> getEntries() {
    return this.playerMap;
  }
}
