package com.example.uno.services;

import com.example.uno.models.ConnectionData;
import com.example.uno.models.Player;
import java.util.List;
import org.springframework.stereotype.Service;
import repos.PlayerRepo;

/**
 * An implementation of IPlayerService.
 */
@Service
public class PlayerService implements IPlayerService {

  private final PlayerRepo playerRepo;

  public PlayerService() {
    this.playerRepo = PlayerRepo.getInstance();
  }

  @Override
  public void addPlayer(String sessionId, String playerName, ConnectionData connectionData) {
    Player player = new Player(sessionId, playerName, connectionData);
    playerRepo.add(sessionId, player);
  }

  @Override
  public void removePlayer(String sessionId) {
    playerRepo.remove(sessionId);
  }

  @Override
  public Player getPlayer(String playerSessionId) {
    return playerRepo.get(playerSessionId);
  }

  @Override
  public List<String> getPlayerSessionIdList() {
    return playerRepo.getAllKeys();
  }
}
