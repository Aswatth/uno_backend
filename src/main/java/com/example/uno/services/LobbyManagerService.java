package com.example.uno.services;

import com.example.uno.models.Lobby;
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
public class LobbyManagerService implements ILobbyManagerService {


  @Autowired
  SimpMessagingTemplate simpMessagingTemplate;

  @Autowired
  IPlayerService playerService;

  private final ConcurrentHashMap<String, Lobby> lobbyMap;
  private final Map<String, Map<String, Object>> lobbyList;
  private final Map<String, String> playerGameMap;

  public LobbyManagerService() {
    lobbyMap = new ConcurrentHashMap<>();
    lobbyList = new HashMap<>();
    playerGameMap = new HashMap<>();
  }

  private void broadcast() {
    for (String playerSessionId : playerService.getPlayerSessionIdList()) {
      SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(
          SimpMessageType.MESSAGE);
      headerAccessor.setSessionId(playerSessionId);
      headerAccessor.setLeaveMutable(true);

      simpMessagingTemplate.convertAndSendToUser(playerSessionId, "/queue/browse-lobbies",
          browseLobbies(), headerAccessor.getMessageHeaders());
    }

  }

  @Override
  public String createLobby(String gameName, int minPlayers) {

    String gameId = UUID.randomUUID().toString();

    Lobby lobby = new Lobby(gameId, gameName, minPlayers);

    lobbyMap.put(gameId, lobby);

    return gameId;
  }


  @Override
  public List<Map<String, Object>> browseLobbies() {
    return lobbyList.values().stream().toList();
  }

  @Override
  public void joinLobby(String gameId, String playerSessionId) {
    Lobby lobby = lobbyMap.get(gameId);

    Player player = playerService.getPlayer(playerSessionId);

    lobby.addPlayer(player);
    playerGameMap.put(playerSessionId, gameId);
    lobbyMap.put(gameId, lobby);

    lobbyList.put(gameId, lobby.toMap());

    // Assigning the first player as host or a member otherwise
    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(
        SimpMessageType.MESSAGE);
    headerAccessor.setSessionId(playerSessionId);
    headerAccessor.setLeaveMutable(true);

    simpMessagingTemplate.convertAndSendToUser(playerSessionId, "/queue/host",
        lobby.isHost(playerSessionId), headerAccessor.getMessageHeaders());

    // Notify party members
    simpMessagingTemplate.convertAndSend("/topic/join-lobby/" + gameId,
        lobbyList.get(gameId));

    // Broadcast updated info to other players browsing games.
    broadcast();
  }

  @Override
  public void leaveLobby(String playerSessionId) {
    String gameId = playerGameMap.get(playerSessionId);
    if (gameId != null) {

      Lobby lobby = lobbyMap.computeIfPresent(gameId, (key, lobbyValue) -> {
        Player player = playerService.getPlayer(playerSessionId);
        lobbyValue.removePlayer(player);
        return lobbyValue;
      });

      playerGameMap.remove(playerSessionId);

      assert lobby != null;

      if (lobby.getCurrentPlayers().isEmpty()) {
        lobbyMap.remove(gameId);
        lobbyList.remove(gameId);
      } else {
        // Assign next player as host
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(
            SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(lobby.getHost().getSessionId());
        headerAccessor.setLeaveMutable(true);

        simpMessagingTemplate.convertAndSendToUser(lobby.getHost().getSessionId(), "/queue/host",
            true, headerAccessor.getMessageHeaders());

        lobbyList.put(gameId, lobby.toMap());

        // Notify party members
        simpMessagingTemplate.convertAndSend("/topic/join-lobby/" + gameId,
            lobbyList.get(gameId));
      }

      // Broadcast updated info to other players browsing games.
      broadcast();
    }
  }
}
