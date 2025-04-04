package com.example.uno.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.models.Card;
import com.example.uno.models.Color;
import com.example.uno.models.ConnectionData;
import com.example.uno.models.Game;
import com.example.uno.models.Lobby;
import com.example.uno.models.Player;
import com.example.uno.models.Value;
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
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import repos.GameRepo;
import repos.LobbyRepo;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

  @Mock
  GameRepo gameRepo;

  @Mock
  LobbyRepo lobbyRepo;

  @Mock
  SimpMessagingTemplate simpMessagingTemplate;

  @InjectMocks
  GameService gameService;

  @Test
  void testPlay() {
    String gameId = "g123";
    Lobby lobby = new Lobby(gameId, "testGame", 2);

    Player mockPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player mockPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    lobby.addPlayer(mockPlayer1);
    lobby.addPlayer(mockPlayer2);

    Game game = new Game(List.of(mockPlayer1, mockPlayer2));
    game.dealCards();

    Card card = new Card(Color.RED, Value.ONE);

    // Mock get game
    Mockito.when(gameRepo.get(gameId)).thenReturn(game);

    // Mock get Lobby
    Mockito.when(lobbyRepo.get(gameId)).thenReturn(lobby);

    gameService.play(gameId, card);

    ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(HashMap.class);
    ArgumentCaptor<MessageHeaders> messageHeadersArgumentCaptor = ArgumentCaptor.forClass(
        MessageHeaders.class);

    Mockito.verify(simpMessagingTemplate, Mockito.times(2))
        .convertAndSendToUser(Mockito.anyString(),
            Mockito.eq("/queue/game/" + gameId), payloadCaptor.capture(),
            messageHeadersArgumentCaptor.capture());

    Map<String, Object> actualPayload = payloadCaptor.getValue();
    assertThat(actualPayload).containsKey("isMyTurn").containsKey("isWinner")
        .containsEntry("topCard", game.getTopCard())
        .containsKey("otherPlayersInfo").containsKey("cards");

    List<Map<String, Object>> actualPayloadOtherPlayerInfo = (List<Map<String, Object>>) actualPayload.get(
        "otherPlayersInfo");
    assertThat(actualPayloadOtherPlayerInfo).hasSize(1);
    assertThat(actualPayloadOtherPlayerInfo.getFirst()).containsKey("isWinner")
        .containsKey("playerName")
        .containsKey("isMyTurn").containsKey("cardCount");
  }

  @Test
  void testDrawCard() {
    String gameId = "g123";
    Lobby lobby = new Lobby(gameId, "testGame", 2);

    Player mockPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player mockPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    lobby.addPlayer(mockPlayer1);
    lobby.addPlayer(mockPlayer2);

    Game game = new Game(List.of(mockPlayer1, mockPlayer2));
    game.dealCards();

    // Mock get game
    Mockito.when(gameRepo.get(gameId)).thenReturn(game);

    // Mock get Lobby
    Mockito.when(lobbyRepo.get(gameId)).thenReturn(lobby);

    gameService.drawCard(gameId);

    ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(HashMap.class);
    ArgumentCaptor<MessageHeaders> messageHeadersArgumentCaptor = ArgumentCaptor.forClass(
        MessageHeaders.class);

    Mockito.verify(simpMessagingTemplate, Mockito.times(2))
        .convertAndSendToUser(Mockito.anyString(),
            Mockito.eq("/queue/game/" + gameId), payloadCaptor.capture(),
            messageHeadersArgumentCaptor.capture());

    Map<String, Object> actualPayload = payloadCaptor.getValue();
    assertThat(actualPayload).containsKey("isMyTurn").containsKey("isWinner")
        .containsEntry("topCard", game.getTopCard())
        .containsKey("otherPlayersInfo").containsKey("cards");

    List<Map<String, Object>> actualPayloadOtherPlayerInfo = (List<Map<String, Object>>) actualPayload.get(
        "otherPlayersInfo");
    assertThat(actualPayloadOtherPlayerInfo).hasSize(1);
    assertThat(actualPayloadOtherPlayerInfo.getFirst()).containsKey("isWinner")
        .containsKey("playerName")
        .containsKey("isMyTurn").containsKey("cardCount");
  }

  @Test
  void testEndTurn() {
    String gameId = "g123";
    Lobby lobby = new Lobby(gameId, "testGame", 2);

    Player mockPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player mockPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    lobby.addPlayer(mockPlayer1);
    lobby.addPlayer(mockPlayer2);

    Game game = new Game(List.of(mockPlayer1, mockPlayer2));
    game.dealCards();

    // Mock get game
    Mockito.when(gameRepo.get(gameId)).thenReturn(game);

    // Mock get Lobby
    Mockito.when(lobbyRepo.get(gameId)).thenReturn(lobby);

    gameService.endTurn(gameId);

    ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(HashMap.class);
    ArgumentCaptor<MessageHeaders> messageHeadersArgumentCaptor = ArgumentCaptor.forClass(
        MessageHeaders.class);

    Mockito.verify(simpMessagingTemplate, Mockito.times(2))
        .convertAndSendToUser(Mockito.anyString(),
            Mockito.eq("/queue/game/" + gameId), payloadCaptor.capture(),
            messageHeadersArgumentCaptor.capture());

    Map<String, Object> actualPayload = payloadCaptor.getValue();
    assertThat(actualPayload).containsKey("isMyTurn").containsKey("isWinner")
        .containsEntry("topCard", game.getTopCard())
        .containsKey("otherPlayersInfo").containsKey("cards");

    List<Map<String, Object>> actualPayloadOtherPlayerInfo = (List<Map<String, Object>>) actualPayload.get(
        "otherPlayersInfo");
    assertThat(actualPayloadOtherPlayerInfo).hasSize(1);
    assertThat(actualPayloadOtherPlayerInfo.getFirst()).containsKey("isWinner")
        .containsKey("playerName")
        .containsKey("isMyTurn").containsKey("cardCount");
  }

  @Test
  void testReplay() {
    String gameId = "g123";
    Lobby lobby = new Lobby(gameId, "testGame", 2);

    Player mockPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player mockPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    lobby.addPlayer(mockPlayer1);
    lobby.addPlayer(mockPlayer2);

    Game game = new Game(List.of(mockPlayer1, mockPlayer2));
    game.dealCards();

    // Mock get Lobby
    Mockito.when(lobbyRepo.get(gameId)).thenReturn(lobby);

    gameService.replay(gameId);

    Mockito.verify(simpMessagingTemplate)
        .convertAndSend("/topic/lobby/" + gameId, lobby.toMap());
  }
}
