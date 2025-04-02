package com.example.uno.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.models.ConnectionData;
import com.example.uno.models.Lobby;
import com.example.uno.models.Player;
import org.springframework.messaging.MessageHeaders;
import repos.GameRepo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
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
  GameRepo gameRepo;

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
            Map.entry("minPlayers", lobby.getMinPlayers()),
            Map.entry("currentPlayers", List.of(Map.ofEntries(
                Map.entry("playerName", player.getName()),
                Map.entry("status", true)
            ))))
    );
  }

  @Test
  void testStartGameTwoPlayers() {

    String gameId = "g123";

    Player mockPlayer1 = new Player("p1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player mockPlayer2 = new Player("p2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    Lobby lobby = new Lobby(gameId, "testGame", 2);
    lobby.addPlayer(mockPlayer1);
    lobby.addPlayer(mockPlayer2);

    Mockito.when(lobbyRepo.get(gameId)).thenReturn(lobby);

    lobbyService.startGame(gameId);

    ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(HashMap.class);

    ArgumentCaptor<MessageHeaders> messageHeadersArgumentCaptor = ArgumentCaptor.forClass(
        MessageHeaders.class);

    Mockito.verify(simpMessagingTemplate, Mockito.times(lobby.getCurrentPlayers().size()))
        .convertAndSendToUser(Mockito.anyString(),
            Mockito.eq("/queue/game/" + gameId), payloadCaptor.capture(),
            messageHeadersArgumentCaptor.capture());

    Map<String, Object> actualPayload = payloadCaptor.getValue();
    assertThat(actualPayload).containsKey("isMyTurn").containsKey("isWinner")
        .containsKey("topCard")
        .containsKey("otherPlayersInfo").containsKey("cards");

    List<Map<String, Object>> actualPayloadOtherPlayerInfo = (List<Map<String, Object>>) actualPayload.get(
        "otherPlayersInfo");
    assertThat(actualPayloadOtherPlayerInfo).hasSize(lobby.getCurrentPlayers().size() - 1);
    assertThat(actualPayloadOtherPlayerInfo.getFirst()).containsKey("isWinner")
        .containsKey("playerName")
        .containsKey("isMyTurn").containsEntry("cardCount", 7);
  }

  @Test
  void testStartGameFourPlayers() {

    String gameId = "g123";

    Player mockPlayer1 = new Player("p1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player mockPlayer2 = new Player("p2", "testPlayer2", new ConnectionData("0.0.0.0", 2));
    Player mockPlayer3 = new Player("p3", "testPlayer3", new ConnectionData("0.0.0.0", 3));
    Player mockPlayer4 = new Player("p4", "testPlayer4", new ConnectionData("0.0.0.0", 4));

    Lobby lobby = new Lobby(gameId, "testGame", 4);
    lobby.addPlayer(mockPlayer1);
    lobby.addPlayer(mockPlayer2);
    lobby.addPlayer(mockPlayer3);
    lobby.addPlayer(mockPlayer4);

    Mockito.when(lobbyRepo.get(gameId)).thenReturn(lobby);

    lobbyService.startGame(gameId);

    ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(HashMap.class);

    ArgumentCaptor<MessageHeaders> messageHeadersArgumentCaptor = ArgumentCaptor.forClass(
        MessageHeaders.class);

    Mockito.verify(simpMessagingTemplate, Mockito.times(lobby.getCurrentPlayers().size()))
        .convertAndSendToUser(Mockito.anyString(),
            Mockito.eq("/queue/game/" + gameId), payloadCaptor.capture(),
            messageHeadersArgumentCaptor.capture());

    Map<String, Object> actualPayload = payloadCaptor.getValue();
    assertThat(actualPayload).containsKey("isMyTurn").containsKey("isWinner")
        .containsKey("topCard")
        .containsKey("otherPlayersInfo").containsKey("cards");

    List<Map<String, Object>> actualPayloadOtherPlayerInfo = (List<Map<String, Object>>) actualPayload.get(
        "otherPlayersInfo");
    assertThat(actualPayloadOtherPlayerInfo).hasSize(lobby.getCurrentPlayers().size() - 1);
    assertThat(actualPayloadOtherPlayerInfo.getFirst()).containsKey("isWinner")
        .containsKey("playerName")
        .containsKey("isMyTurn").containsEntry("cardCount", 7);
  }
}
