package com.example.uno.controllers;

import com.example.uno.services.ILobbyManagerService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

/**
 * A controller class to handle game requests.
 */
@Controller
public class LobbyManagerController {

  @Autowired
  ILobbyManagerService lobbyManagerService;

  @MessageMapping("/lobby")
  @SendToUser("/queue/lobby")
  public String createLobby(Map<String, Object> request) {
    String gameName = (String) request.get("gameName");
    int minPlayers = (int) request.get("minPlayers");

    return lobbyManagerService.createLobby(gameName, minPlayers);
  }

  @MessageMapping("/lobby/{gameId}/edit-min-players")
  public void editMinPlayers(@DestinationVariable String gameId, int minPlayers) {
    lobbyManagerService.editMinPlayers(gameId, minPlayers);
  }

  @MessageMapping("/browse-lobbies")
  @SendToUser("/queue/browse-lobbies")
  public List<Map<String, Object>> browseLobbies() {
    return lobbyManagerService.browseLobbies();
  }

  @MessageMapping("/join-lobby/{gameId}")
  @SendTo("/topic/lobby/{gameId}")
  public void joinLobby(@Header("simpSessionId") String sessionId,
      @DestinationVariable String gameId) {
    lobbyManagerService.joinLobby(gameId, sessionId);
  }

  @MessageMapping("/leave-lobby/{gameId}")
  public void leaveLobby(@Header("simpSessionId") String sessionId) {
    lobbyManagerService.leaveLobby(sessionId);
  }
}
