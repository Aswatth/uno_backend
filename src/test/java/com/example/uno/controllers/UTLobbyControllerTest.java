package com.example.uno.controllers;

import com.example.uno.services.ILobbyService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UTLobbyControllerTest {

  @Mock
  ILobbyService lobbyService;

  @InjectMocks
  LobbyController lobbyController;

  @Test
  void testSetStatusTrue() {
    String playerSessionId = "123";
    String gameId = "g123";
    Map<String, Boolean> payload = Map.of("status", true);

    Mockito.doNothing().when(lobbyService)
        .setStatus(playerSessionId, gameId, payload.get("status"));

    lobbyController.setStatus(playerSessionId, gameId, payload);

    Mockito.verify(lobbyService).setStatus(playerSessionId, gameId, payload.get("status"));
  }

  @Test
  void testSetStatusFalse() {
    String playerSessionId = "123";
    String gameId = "g123";
    Map<String, Boolean> payload = Map.of("status", false);

    Mockito.doNothing().when(lobbyService)
        .setStatus(playerSessionId, gameId, payload.get("status"));

    lobbyController.setStatus(playerSessionId, gameId, payload);

    Mockito.verify(lobbyService).setStatus(playerSessionId, gameId, payload.get("status"));
  }

  @Test
  void testStartGame() {
    String gameId = "g123";

    Mockito.doNothing().when(lobbyService).startGame(gameId);

    lobbyController.startGame(gameId);

    Mockito.verify(lobbyService).startGame(gameId);
  }
}
