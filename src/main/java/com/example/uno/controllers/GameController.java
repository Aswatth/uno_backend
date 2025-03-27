package com.example.uno.controllers;

import com.example.uno.services.IGameService;
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
public class GameController {

  @Autowired
  IGameService gameService;

  @MessageMapping("/game")
  @SendToUser("/queue/game")
  public String createGame(Map<String, Object> request) {
    String gameName = (String) request.get("gameName");
    int minPlayers = (int) request.get("minPlayers");

    return gameService.createGame(gameName, minPlayers);
  }

  @MessageMapping("/browse-games")
  @SendToUser("/queue/browse-games")
  public List<Map<String, Object>> browseGames() {
    return gameService.browseGames();
  }

  @MessageMapping("/join-game/{gameId}")
  @SendTo("/topic/join-game/{gameId}")
  public void joinGame(@Header("simpSessionId") String sessionId,
      @DestinationVariable String gameId) {
    System.out.println("SESSION ID : " + sessionId);
    gameService.joinGame(gameId, sessionId);
  }

  @MessageMapping("/game/{gameId}")
  @SendToUser("/queue/{gameId}")
  public void startGame(@DestinationVariable String gameId) {
    gameService.startGame(gameId);
  }
}
