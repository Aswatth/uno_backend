package com.example.uno.controllers;

import com.example.uno.services.ILobbyService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LobbyController {

  @Autowired
  ILobbyService lobbyService;

  @MessageMapping("/lobby/{gameId}/status")
  public void setStatus(@Header("simpSessionId") String sessionId,
      @DestinationVariable String gameId, Map<String, Boolean> payload) {
    lobbyService.setStatus(sessionId, gameId, payload.get("status"));
  }

}
