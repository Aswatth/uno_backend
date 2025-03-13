package com.example.uno.services;

import com.example.uno.models.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * An implementation of IGameService interface.
 */
@Service
public class GameService implements IGameService {

  @Autowired
  IPlayerService playerService;

  private final ConcurrentHashMap<String, Game> gameMap;
  private final Map<String, Map<String, Object>> gameList;
  private final Map<String, String> playerGameMap;

  @Autowired
  SimpMessagingTemplate simpMessagingTemplate;

  public GameService() {
    gameMap = new ConcurrentHashMap<>();
    gameList = new HashMap<>();
    playerGameMap = new HashMap<>();
  }

  private void broadcast() {
    for (String player : playerService.getPlayerSessionIdList()) {

//      System.out.println("BROADCASTING TO PLAYER ID: " + player);
      SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(
          SimpMessageType.MESSAGE);
      headerAccessor.setSessionId(player);
      headerAccessor.setLeaveMutable(true);

      simpMessagingTemplate.convertAndSendToUser(player, "/queue/browse-games",
          gameList.values().stream().toList(), headerAccessor.getMessageHeaders());
    }

  }

  @Override
  public String createGame(String gameName, int minPlayers) {

    Game game = new Game(minPlayers, gameName);

    String gameId = UUID.randomUUID().toString();

    gameMap.put(gameId, game);

    return gameId;
  }


  @Override
  public List<Map<String, Object>> browseGames() {
    return gameList.values().stream().toList();
  }

  @Override
  public void joinGame(String gameId, String playerSessionId) {
    Game game = gameMap.get(gameId);

    Map<String, Object> map = new HashMap<>();

    game.addPlayer(playerSessionId);
    gameMap.put(gameId, game);
    playerGameMap.put(playerSessionId, gameId);

    map.put("gameId", gameId);
    map.put("gameName", game.getGameName());
    map.put("currentPlayers", game.getCurrentPlayers().size());

    gameList.put(gameId, map);

    broadcast();
  }

  @Override
  public void leaveGame(String playerSessionId) {
    String gameId = playerGameMap.get(playerSessionId);
    if (gameId != null) {
      Game game = gameMap.get(gameId);
      game.removePlayer(playerSessionId);

      gameMap.put(gameId, game);
      playerGameMap.remove(playerSessionId);

      if (game.getCurrentPlayers().isEmpty()) {
        gameMap.remove(gameId);
        gameList.remove(gameId);
//        return;
      } else {
        Map<String, Object> map = new HashMap<>();
        map.put("gameId", gameId);
        map.put("gameName", game.getGameName());
        map.put("currentPlayers", game.getCurrentPlayers().size());
        gameList.put(gameId, map);
      }
      broadcast();
    }
  }
}
