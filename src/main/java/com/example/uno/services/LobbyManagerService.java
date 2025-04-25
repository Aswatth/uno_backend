package com.example.uno.services;

import com.example.uno.models.Lobby;
import com.example.uno.models.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import repos.ChatRepo;
import repos.LobbyRepo;
import repos.PlayerLobbyRepo;
import repos.PlayerRepo;

/**
 * An implementation of IGameService interface.
 */
@Service
public class LobbyManagerService implements ILobbyManagerService {

  @Autowired
  SimpMessagingTemplate simpMessagingTemplate;

  private final Map<String, Map<String, Object>> lobbyList;

  private PlayerRepo playerRepo;
  private LobbyRepo lobbyRepo;
  private PlayerLobbyRepo playerLobbyRepo;
  private ChatRepo chatRepo;

  public LobbyManagerService() {
    lobbyList = new HashMap<>();

    playerRepo = PlayerRepo.getInstance();
    lobbyRepo = LobbyRepo.getInstance();
    playerLobbyRepo = PlayerLobbyRepo.getInstance();
    chatRepo = ChatRepo.getInstance();
  }

  private void broadcast() {
    for (String playerSessionId : playerRepo.getAllKeys()) {
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

    lobbyRepo.add(gameId, lobby);

    return gameId;
  }

  @Override
  public void editMinPlayers(String gameId, int minPlayers) {
    Lobby lobby = lobbyRepo.get(gameId);

    lobby.setMinPlayers(minPlayers);

    this.lobbyList.put(gameId, lobby.toMap());

    lobbyRepo.add(gameId, lobby);

    simpMessagingTemplate.convertAndSend("/topic/lobby/" + gameId,
        lobbyList.get(gameId));

    broadcast();
  }


  @Override
  public List<Map<String, Object>> browseLobbies() {
    return lobbyList.values().stream().toList();
  }

  @Override
  public void joinLobby(String gameId, String playerSessionId) {
    Lobby lobby = lobbyRepo.get(gameId);

    Player player = playerRepo.get(playerSessionId);

    lobby.addPlayer(player);
    playerLobbyRepo.add(playerSessionId, gameId);
    lobbyRepo.add(gameId, lobby);

    lobbyList.put(gameId, lobby.toMap());

    // Assigning the first player as host or a member otherwise
    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(
        SimpMessageType.MESSAGE);
    headerAccessor.setSessionId(playerSessionId);
    headerAccessor.setLeaveMutable(true);

    simpMessagingTemplate.convertAndSendToUser(playerSessionId, "/queue/host",
        lobby.isHost(playerSessionId), headerAccessor.getMessageHeaders());

    // Notify party members
    simpMessagingTemplate.convertAndSend("/topic/lobby/" + gameId,
        lobbyList.get(gameId));

    // Broadcast updated info to other players browsing games.
    broadcast();

    chatRepo.remove(playerSessionId);
  }

  @Override
  public void leaveLobby(String playerSessionId) {
    String gameId = playerLobbyRepo.get(playerSessionId);
    if (gameId != null) {

      Lobby lobby = lobbyRepo.getEntries().computeIfPresent(gameId, (key, lobbyValue) -> {
        Player player = playerRepo.get(playerSessionId);
        lobbyValue.removePlayer(player);
        return lobbyValue;
      });

      playerLobbyRepo.remove(playerSessionId);

      assert lobby != null;

      if (lobby.getCurrentPlayers().isEmpty()) {
        lobbyRepo.remove(gameId);
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
        simpMessagingTemplate.convertAndSend("/topic/lobby/" + gameId,
            lobbyList.get(gameId));
      }

      // Broadcast updated info to other players browsing games.
      broadcast();

      chatRepo.remove(playerSessionId);
    }
  }
}
