package repos;

import com.example.uno.models.Game;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameRepo implements MapRepo<String, Game> {

  private static GameRepo instance;
  private final Map<String, Game> gameMap;

  private GameRepo() {
    this.gameMap = new ConcurrentHashMap<>();
  }

  public static GameRepo getInstance() {
    if (instance == null) {
      instance = new GameRepo();
    }
    return instance;
  }

  @Override
  public void add(String key, Game itemToAdd) {
    this.gameMap.put(key, itemToAdd);
  }

  @Override
  public void remove(String key) {
    this.gameMap.remove(key);
  }

  @Override
  public Game get(String key) {
    return this.gameMap.get(key);
  }

  @Override
  public List<String> getAllKeys() {
    return this.gameMap.keySet().stream().toList();
  }

  @Override
  public List<Game> getAllItems() {
    return this.gameMap.values().stream().toList();
  }

  @Override
  public Map<String, Game> getEntries() {
    return this.gameMap;
  }
}
