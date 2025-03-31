package repos;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerLobbyRepo implements MapRepo<String, String> {

  private static PlayerLobbyRepo instance;
  private final Map<String, String> playerLobbyMap;

  private PlayerLobbyRepo() {
    this.playerLobbyMap = new ConcurrentHashMap<>();
  }

  public static PlayerLobbyRepo getInstance() {
    if (instance == null) {
      instance = new PlayerLobbyRepo();
    }
    return instance;
  }

  @Override
  public void add(String key, String itemToAdd) {
    this.playerLobbyMap.put(key, itemToAdd);
  }

  @Override
  public void remove(String key) {
    this.playerLobbyMap.remove(key);
  }

  @Override
  public String get(String key) {
    return this.playerLobbyMap.get(key);
  }

  @Override
  public List<String> getAllKeys() {
    return this.playerLobbyMap.keySet().stream().toList();
  }

  @Override
  public List<String> getAllItems() {
    return this.playerLobbyMap.values().stream().toList();
  }

  @Override
  public Map<String, String> getEntries() {
    return this.playerLobbyMap;
  }
}
