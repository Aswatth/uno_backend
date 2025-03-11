package com.example.uno.services;

import com.example.uno.models.Game;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * An implementation of IGameService interface.
 */
@Service
public class GameService implements IGameService {

  private final ConcurrentHashMap<String, Game> gameMap;

  public GameService() {
    gameMap = new ConcurrentHashMap<>();
  }

  @Override
  public String createGame(String gameName, int minPlayers) {

    Game game = new Game(minPlayers, gameName);

    String gameId = UUID.randomUUID().toString();

    gameMap.put(gameId, game);

    return gameId;
  }

  @Override
  public List<Game> browseGames() {
    return List.of();
  }

  @Override
  public void joinGame(String gameId, String playerSessionId) {
    Game game = gameMap.get(gameId);
    game.addPlayer(playerSessionId);
  }
}
