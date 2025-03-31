package repos;

import com.example.uno.models.Lobby;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LobbyRepo implements MapRepo<String, Lobby> {

  private static LobbyRepo instance;
  private final Map<String, Lobby> lobbyMap;

  private LobbyRepo() {
    this.lobbyMap = new ConcurrentHashMap<>();
  }

  public static LobbyRepo getInstance() {
    if (instance == null) {
      instance = new LobbyRepo();
    }
    return instance;
  }

  @Override
  public void add(String key, Lobby itemToAdd) {
    this.lobbyMap.put(key, itemToAdd);
  }

  @Override
  public void remove(String key) {
    this.lobbyMap.remove(key);
  }

  @Override
  public Lobby get(String key) {
    return this.lobbyMap.get(key);
  }

  @Override
  public List<String> getAllKeys() {
    return this.lobbyMap.keySet().stream().toList();
  }

  @Override
  public List<Lobby> getAllItems() {
    return this.lobbyMap.values().stream().toList();
  }

  @Override
  public Map<String, Lobby> getEntries() {
    return this.lobbyMap;
  }

}
