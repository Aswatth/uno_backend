package com.example.uno.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.services.GameService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UTGameControllerTest {

  @Mock
  GameService gameService;

  @InjectMocks
  GameController gameController;

  @Test
  void testCreateGame() {
    String gameName = "testGameName";
    int minPlayers = 2;
    String gameId = "123";
    Map<String, Object> request = Map.ofEntries(
        Map.entry("gameName", gameName),
        Map.entry("minPlayers", minPlayers)
    );

    Mockito.when(gameService.createGame(gameName, minPlayers)).thenReturn(gameId);

    assertThat(gameController.createGame(request)).isEqualTo(gameId);

  }

  @Test
  void testJoinGame() {
    String gameId = "123";
    String playerSessionId = "p123";

    Mockito.doNothing().when(gameService).joinGame(gameId, playerSessionId);

    gameController.joinGame(playerSessionId, gameId);

    Mockito.verify(gameService).joinGame(gameId, playerSessionId);
  }

  @Test
  void testBrowseGames() {
    List<Map<String, Object>> gameList = Arrays.asList(Map.ofEntries(
        Map.entry("gameName", "testGame1"),
        Map.entry("currentPlayers", Collections.singletonList("testPlayer1"))
    ), Map.ofEntries(
        Map.entry("gameName", "testGame2"),
        Map.entry("currentPlayers", Arrays.asList("testPlayer2", "testPlayer3"))
    ));

    Mockito.when(gameService.browseGames()).thenReturn(gameList);

    Mockito.verify(gameService).browseGames();

    assertThat(gameController.browseGames()).isEqualTo(gameList);
  }

  @Test
  void testStartGame() {

    String gameId = "123";

    Mockito.doNothing().when(gameService).startGame(gameId);

    gameController.startGame(gameId);

    Mockito.verify(gameService).startGame(gameId);
  }
}
