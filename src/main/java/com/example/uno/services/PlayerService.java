package com.example.uno.services;

import com.example.uno.models.ConnectionData;
import com.example.uno.models.Player;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * An implementation of IPlayerService.
 */
@Service
public class PlayerService implements IPlayerService {

  private final ConcurrentHashMap<String, Player> playerMap;

  public PlayerService() {
    playerMap = new ConcurrentHashMap<>();
  }

  @Override
  public void addPlayer(String sessionId, String playerName, ConnectionData connectionData) {
    Player player = new Player(sessionId, playerName, connectionData);
    playerMap.put(sessionId, player);

//    System.out.printf("CONNECTED - %s\n", player);
  }

  @Override
  public void removePlayer(String sessionId) {
    Player removedPlayer = playerMap.remove(sessionId);
//    System.out.printf("DISCONNECTED - %s\n", removedPlayer);
  }

  @Override
  public List<String> getPlayerSessionIdList() {
    return playerMap.keySet().stream().toList();
  }
}
