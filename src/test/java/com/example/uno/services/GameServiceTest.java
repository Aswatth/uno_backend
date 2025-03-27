package com.example.uno.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.models.Card;
import com.example.uno.models.ConnectionData;
import com.example.uno.models.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

  @Mock
  PlayerService playerService;

  @Mock
  SimpMessagingTemplate simpMessagingTemplate;

  @InjectMocks
  GameService gameService;

  @Test
  public void contextLoad() {
    assertThat(gameService).isNotNull();
  }

  @Test
  public void testCreateGame() {
    String gameName = "testGame";
    int minPlayers = 2;

    String gameId = gameService.createGame(gameName, minPlayers);
    assertThat(gameId).isNotEmpty();
  }

  @Test
  public void testJoinGame() {
    Random random = new Random();
    String gameName = "testGame";
    int minPlayers = random.nextInt(7) + 1;
    String gameId = gameService.createGame(gameName, minPlayers);

    Player mockPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 12345));
    Mockito.when(playerService.getPlayer("123")).thenReturn(mockPlayer);

    gameService.joinGame(gameId, mockPlayer.getSessionId());

    List<Map<String, Object>> gameList = gameService.browseGames();

    assertThat(gameList.size()).isEqualTo(1);
  }

  @Test
  public void testLeaveGame() {
    Random random = new Random();
    String gameName = "testGame";
    int minPlayers = random.nextInt(7) + 1;
    String gameId = gameService.createGame(gameName, minPlayers);

    Player mockPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 12345));
    Mockito.when(playerService.getPlayer("123")).thenReturn(mockPlayer);

    gameService.joinGame(gameId, mockPlayer.getSessionId());

    gameService.leaveGame(mockPlayer.getSessionId());

    List<Map<String, Object>> gameList = gameService.browseGames();

    assertThat(gameList.size()).isEqualTo(0);
  }

  @Test
  public void testBrowseGame() {
    Random random = new Random();
    String gameName = "testGame";
    int minPlayers = random.nextInt(7) + 1;
    String gameId = gameService.createGame(gameName, minPlayers);

    Player mockPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 12345));
    Mockito.when(playerService.getPlayer("123")).thenReturn(mockPlayer);

    gameService.joinGame(gameId, mockPlayer.getSessionId());

    List<Map<String, Object>> gameList = gameService.browseGames();

    assertThat(gameList.size()).isEqualTo(1);
    assertThat(gameList.getFirst().get("gameId")).isEqualTo(gameId);
    assertThat(gameList.getFirst().get("gameName")).isEqualTo(gameName);

    List<String> playerList = (List<String>) gameList.getFirst().get("currentPlayers");
    assertThat(playerList.size()).isEqualTo(1);
    assertThat(playerList.getFirst()).isEqualTo(mockPlayer.getName());
  }

  @Test
  public void testStartGame() {
    String gameName = "testGame";
    int minPlayers = 2;
    String gameId = gameService.createGame(gameName, minPlayers);

    Player mockPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));

    SimpMessageHeaderAccessor headerAccessor1 = SimpMessageHeaderAccessor.create(
        SimpMessageType.MESSAGE);
    headerAccessor1.setSessionId(mockPlayer1.getSessionId());

    Mockito.when(playerService.getPlayer("1")).thenReturn(mockPlayer1);

    Player mockPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    SimpMessageHeaderAccessor headerAccessor2 = SimpMessageHeaderAccessor.create(
        SimpMessageType.MESSAGE);
    headerAccessor2.setSessionId(mockPlayer2.getSessionId());

    Mockito.when(playerService.getPlayer("2")).thenReturn(mockPlayer2);

    gameService.joinGame(gameId, mockPlayer1.getSessionId());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer1.getSessionId(), "/queue/host", true,
            headerAccessor1.getMessageHeaders());

    gameService.joinGame(gameId, mockPlayer2.getSessionId());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer2.getSessionId(), "/queue/host", false,
            headerAccessor2.getMessageHeaders());

    gameService.startGame(gameId);

    ArgumentCaptor<Map<String, Object>> payload1 = ArgumentCaptor.forClass(HashMap.class);
    ArgumentCaptor<Map<String, Object>> payload2 = ArgumentCaptor.forClass(HashMap.class);

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(eq(mockPlayer1.getSessionId()), eq("/queue/game/" + gameId),
            payload1.capture(),
            eq(headerAccessor1.
                getMessageHeaders()));

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(eq(mockPlayer2.getSessionId()), eq("/queue/game/" + gameId),
            payload2.capture(),
            eq(headerAccessor2.
                getMessageHeaders()));

    assertThat(payload1.getValue().containsKey("isMyTurn")).isTrue();
    assertThat(payload2.getValue().containsKey("isMyTurn")).isTrue();

    assertThat(payload1.getValue().containsKey("cards")).isTrue();
    assertThat(((List<Card>) payload1.getValue().get("cards")).size()).isEqualTo(7);

    assertThat(payload2.getValue().containsKey("cards")).isTrue();
    assertThat(((List<Card>) payload2.getValue().get("cards")).size()).isEqualTo(7);

    assertThat(payload1.getValue().containsKey("isWinner")).isTrue();
    assertThat(payload2.getValue().containsKey("isWinner")).isTrue();

    assertThat(payload1.getValue().containsKey("otherPlayersData")).isTrue();
    List<Map<String, Object>> otherPlayerData1 = (List<Map<String, Object>>) payload1.getValue()
        .get("otherPlayersData");
    assertThat(otherPlayerData1.size()).isEqualTo(1);
    assertThat(otherPlayerData1.getFirst().containsKey("cardCount")).isTrue();
    assertThat(otherPlayerData1.getFirst().get("cardCount")).isEqualTo(7);
    assertThat(otherPlayerData1.getFirst().containsKey("name")).isTrue();
    assertThat(otherPlayerData1.getFirst().get("name")).isEqualTo(mockPlayer2.getName());
    assertThat(otherPlayerData1.getFirst().containsKey("isMyTurn")).isTrue();

    assertThat(payload2.getValue().containsKey("otherPlayersData")).isTrue();
    List<Map<String, Object>> otherPlayerData2 = (List<Map<String, Object>>) payload2.getValue()
        .get("otherPlayersData");
    assertThat(otherPlayerData2.size()).isEqualTo(1);
    assertThat(otherPlayerData2.getFirst().containsKey("cardCount")).isTrue();
    assertThat(otherPlayerData2.getFirst().get("cardCount")).isEqualTo(7);
    assertThat(otherPlayerData2.getFirst().containsKey("name")).isTrue();
    assertThat(otherPlayerData2.getFirst().get("name")).isEqualTo(mockPlayer1.getName());
    assertThat(otherPlayerData2.getFirst().containsKey("isMyTurn")).isTrue();
  }
}
