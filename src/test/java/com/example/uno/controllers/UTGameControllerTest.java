package com.example.uno.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.services.GameService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UTGameControllerTest {

  @Mock
  GameService gameService;

  @InjectMocks
  GameController gameController;

  @Test
  public void testCreateGame() {
    String gameName = "testGameName";
    int minPlayers = 2;
    String gameId = "123";
    Map<String, Object> request = new HashMap<>();
    request.put("gameName", gameName);
    request.put("minPlayers", minPlayers);

    Mockito.when(gameService.createGame(gameName, minPlayers)).thenReturn(gameId);

    assertThat(gameController.createGame(request)).isEqualTo(gameId);

  }

  @Test
  public void testJoinGame() {
    String gameId = "123";
    String playerSessionId = "p123";

    Mockito.doNothing().when(gameService).joinGame(gameId, playerSessionId);

    gameController.joinGame(playerSessionId, gameId);

    Mockito.verify(gameService).joinGame(gameId, playerSessionId);
  }

  @Test
  public void testBrowseGames() {
    List<Map<String, Object>> gameList = new ArrayList<>();
    gameList.add(new HashMap<>() {{
      put("gameName", "testGame1");
      put("currentPlayers", new ArrayList<>() {{
        add("testPlayer1");
      }});
    }});
    gameList.add(new HashMap<>() {{
      put("gameName", "testGame2");
      put("currentPlayers", new ArrayList<>() {{
        add("testPlayer2");
        add("testPlayer3");
      }});
    }});

    Mockito.when(gameService.browseGames()).thenReturn(gameList);

    assertThat(gameController.browseGames()).isEqualTo(gameList);
  }
}
