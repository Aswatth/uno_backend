package com.example.uno.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.models.ConnectionData;
import com.example.uno.models.Player;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@SpringBootTest
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

}
