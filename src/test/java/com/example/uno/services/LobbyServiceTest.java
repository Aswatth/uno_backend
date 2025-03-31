package com.example.uno.services;

import com.example.uno.models.ConnectionData;
import com.example.uno.models.Lobby;
import com.example.uno.models.Player;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import repos.LobbyRepo;
import repos.PlayerRepo;

@ExtendWith(MockitoExtension.class)
class LobbyServiceTest {

  @Mock
  PlayerRepo playerRepo;

  @Mock
  LobbyRepo lobbyRepo;

  @Mock
  SimpMessagingTemplate simpMessagingTemplate;

  @InjectMocks
  LobbyService lobbyService;

  @Test
  void testSetStatus() {
    Lobby lobby = new Lobby("123", "testGame", 2);
    Player player = new Player("1", "testPlayer", new ConnectionData("0.0.0.0", 1));
    lobby.addPlayer(player);

    Mockito.when(playerRepo.get(player.getSessionId())).thenReturn(player);
    Mockito.when(lobbyRepo.get(lobby.getGameId())).thenReturn(lobby);

    lobbyService.setStatus(player.getSessionId(), lobby.getGameId(), true);

    Mockito.verify(lobbyRepo).add(lobby.getGameId(), lobby);

    Mockito.verify(simpMessagingTemplate).convertAndSend(
        "/topic/lobby/" + lobby.getGameId(),
        Map.ofEntries(
            Map.entry("gameName", lobby.getGameName()),
            Map.entry("gameId", lobby.getGameId()),
            Map.entry("currentPlayers", List.of(Map.ofEntries(
                Map.entry("playerName", player.getName()),
                Map.entry("status", true)
            ))))
    );
  }
}
