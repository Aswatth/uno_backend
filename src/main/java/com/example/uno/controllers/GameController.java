package com.example.uno.controllers;

import com.example.uno.models.Game;
import com.example.uno.services.IGameService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * A controller class to handle game requests.
 */
@Controller
public class GameController {

  @Autowired
  IGameService gameService;

  @MessageMapping("/game")
  @SendToUser("/queue/game")
  public String createGame(@RequestBody Map<String, Object> request) {
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
    gameService.joinGame(gameId, sessionId);
  }


}
