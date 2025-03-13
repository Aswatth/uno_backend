package com.example.uno.config;

import com.example.uno.models.ConnectionData;
import com.example.uno.services.GameService;
import com.example.uno.services.PlayerService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * A listener class to derive client's header information.
 */
@Component
public class WebSocketListener {

  @Autowired
  GameService gameService;
  @Autowired
  PlayerService playerService;

  @EventListener
  public void handleClientConnectEvent(SessionConnectEvent sessionConnectEvent) {
    StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(
        sessionConnectEvent.getMessage());
    String sessionId = stompHeaderAccessor.getSessionId();

    String username = stompHeaderAccessor.getFirstNativeHeader("username");

    ConnectionData connectionData = (ConnectionData) Objects.requireNonNull(
        stompHeaderAccessor.getSessionAttributes()).get("connectionData");

    playerService.addPlayer(sessionId, username, connectionData);
  }

  @EventListener
  public void handleClientDisconnectEvent(SessionDisconnectEvent sessionDisconnectEvent) {
    StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(
        sessionDisconnectEvent.getMessage());
    gameService.leaveGame(stompHeaderAccessor.getSessionId());
    playerService.removePlayer(stompHeaderAccessor.getSessionId());

  }
}
