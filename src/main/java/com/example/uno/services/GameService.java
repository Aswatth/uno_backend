package com.example.uno.services;

import com.example.uno.models.Game;
import com.example.uno.models.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    for (String playerSessionId : playerService.getPlayerSessionIdList()) {

//      System.out.println("BROADCASTING TO PLAYER ID: " + player);
      SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(
          SimpMessageType.MESSAGE);
      headerAccessor.setSessionId(playerSessionId);
      headerAccessor.setLeaveMutable(true);

      simpMessagingTemplate.convertAndSendToUser(playerSessionId, "/queue/browse-games",
          browseGames(), headerAccessor.getMessageHeaders());
    }

  }

  @Override
  public String createGame(String gameName, int minPlayers) {

    String gameId = UUID.randomUUID().toString();

    Game game = new Game(gameId, minPlayers, gameName);

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

    Player player = playerService.getPlayer(playerSessionId);

    game.addPlayer(player);
    playerGameMap.put(playerSessionId, gameId);
    gameMap.put(gameId, game);

    gameList.put(gameId, game.toMap());

    // Assigning the first player as host or a member otherwise
    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(
        SimpMessageType.MESSAGE);
    headerAccessor.setSessionId(playerSessionId);
    headerAccessor.setLeaveMutable(true);

    simpMessagingTemplate.convertAndSendToUser(playerSessionId, "/queue/host",
        game.isHost(playerSessionId), headerAccessor.getMessageHeaders());

    // Notify party members
    simpMessagingTemplate.convertAndSend("/topic/join-game/" + gameId,
        gameList.get(gameId));

    // Broadcast updated info to other players browsing games.
    broadcast();
  }

  @Override
  public void leaveGame(String playerSessionId) {
    String gameId = playerGameMap.get(playerSessionId);
    if (gameId != null) {
      Game game = gameMap.get(gameId);
      Player player = playerService.getPlayer(playerSessionId);
      game.removePlayer(player);

      gameMap.put(gameId, game);
      playerGameMap.remove(playerSessionId);

      if (game.getCurrentPlayers().isEmpty()) {
        gameMap.remove(gameId);
        gameList.remove(gameId);
      } else {
        // Assign next player as host
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(
            SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(game.getHost().getSessionId());
        headerAccessor.setLeaveMutable(true);

        simpMessagingTemplate.convertAndSendToUser(game.getHost().getSessionId(), "/queue/host",
            true, headerAccessor.getMessageHeaders());

        gameList.put(gameId, game.toMap());

        // Notify party members
        simpMessagingTemplate.convertAndSend("/topic/join-game/" + gameId,
            gameList.get(gameId));
      }

      // Broadcast updated info to other players browsing games.
      broadcast();
    }
  }
}
