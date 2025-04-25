package com.example.uno.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.services.LobbyManagerService;
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
class UTLobbyManagerControllerTest {

  @Mock
  LobbyManagerService lobbyManagerService;

  @InjectMocks
  LobbyManagerController lobbyManagerController;

  @Test
  void testCreateLobby() {
    String gameName = "testGameName";
    int minPlayers = 2;
    String gameId = "123";
    Map<String, Object> request = Map.ofEntries(
        Map.entry("gameName", gameName),
        Map.entry("minPlayers", minPlayers)
    );

    Mockito.when(lobbyManagerService.createLobby(gameName, minPlayers)).thenReturn(gameId);

    assertThat(lobbyManagerController.createLobby(request)).isEqualTo(gameId);

  }

  @Test
  void testEditMinPlayers() {
    String gameId = "123";
    int minPlayers = 3;

    Mockito.doNothing().when(lobbyManagerService).editMinPlayers(gameId, minPlayers);

    lobbyManagerController.editMinPlayers(gameId, minPlayers);

    Mockito.verify(lobbyManagerService).editMinPlayers(gameId, minPlayers);
  }

  @Test
  void testJoinLobby() {
    String gameId = "123";
    String playerSessionId = "p123";

    Mockito.doNothing().when(lobbyManagerService).joinLobby(gameId, playerSessionId);

    lobbyManagerController.joinLobby(playerSessionId, gameId);

    Mockito.verify(lobbyManagerService).joinLobby(gameId, playerSessionId);
  }

  @Test
  void testLeaveLobby() {
    String playerSessionId = "p123";

    Mockito.doNothing().when(lobbyManagerService).leaveLobby(playerSessionId);

    lobbyManagerController.leaveLobby(playerSessionId);

    Mockito.verify(lobbyManagerService).leaveLobby(playerSessionId);
  }

  @Test
  void testBrowseLobbies() {
    List<Map<String, Object>> gameList = Arrays.asList(Map.ofEntries(
        Map.entry("gameName", "testGame1"),
        Map.entry("currentPlayers", Collections.singletonList("testPlayer1"))
    ), Map.ofEntries(
        Map.entry("gameName", "testGame2"),
        Map.entry("currentPlayers", Arrays.asList("testPlayer2", "testPlayer3"))
    ));

    Mockito.when(lobbyManagerService.browseLobbies()).thenReturn(gameList);

    assertThat(lobbyManagerController.browseLobbies()).isEqualTo(gameList);

    Mockito.verify(lobbyManagerService).browseLobbies();
  }
}
