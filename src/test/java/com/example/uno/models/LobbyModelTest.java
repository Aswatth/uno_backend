package com.example.uno.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LobbyModelTest {

  private final String gameName = "testGame";
  private final int minPlayers = 2;
  private final String gameId = "123";
  private Lobby lobby;

  @BeforeEach
  void setup() {
    lobby = new Lobby(gameId, gameName, minPlayers);
  }

  @Test
  void testGameCreation() {
    assertThat(lobby).isNotNull();
  }

  @Test
  void testGetGameId() {
    assertThat(lobby.getGameId()).isEqualTo(gameId);
  }

  @Test
  void testToMap() {
    Map<String, Object> map = lobby.toMap();
    assertThat(map).containsEntry("gameId", gameId).containsEntry("gameName", gameName)
        .containsEntry("currentPlayers",
            Collections.emptyList());
  }

  @Test
  void testToMapWithPlayers() {
    Player player1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player player2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    lobby.addPlayer(player1);
    lobby.addPlayer(player2);

    Map<String, Object> map = lobby.toMap();

    assertThat(map).containsEntry("gameId", gameId).containsEntry("gameName", gameName)
        .containsKey("currentPlayers").containsEntry("minPlayers", minPlayers);

    List<Map<String, Object>> currentPlayerData = List.of(Map.ofEntries(
        Map.entry("playerName", player1.getName()),
        Map.entry("status", true)
    ), Map.ofEntries(
        Map.entry("playerName", player2.getName()),
        Map.entry("status", false)
    ));

    assertThat(currentPlayerData.getFirst()).containsEntry("playerName", player1.getName());
    assertThat(currentPlayerData.getFirst()).containsEntry("status", true);

    assertThat(currentPlayerData.getLast()).containsEntry("playerName", player2.getName());
    assertThat(currentPlayerData.getLast()).containsEntry("status", false);
  }

  @Test
  void testGetMinPlayers() {
    assertThat(lobby.getMinPlayers()).isEqualTo(2);
  }

  @Test
  void testGetGameName() {
    assertThat(lobby.getGameName()).isEqualTo(gameName);
  }

  @Test
  void testGetPlayer() {
    Player testPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 1234));
    lobby.addPlayer(testPlayer);

    assertThat(lobby.getCurrentPlayers()).hasSize(1);
    assertThat(lobby.getCurrentPlayers().stream().findFirst()).contains(testPlayer);
  }

  @Test
  void testGetPlayerAfterRemoving() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    lobby.addPlayer(testPlayer1);
    lobby.addPlayer(testPlayer2);

    lobby.removePlayer(testPlayer1);

    assertThat(lobby.getCurrentPlayers()).hasSize(1);
  }

  @Test
  void testGetPlayerTwoPlayers() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    lobby.addPlayer(testPlayer1);
    lobby.addPlayer(testPlayer2);

    assertThat(lobby.getCurrentPlayers()).hasSize(2);
  }

  @Test
  void testGetHost() {
    Player testPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 1234));
    lobby.addPlayer(testPlayer);

    assertThat(lobby.getHost()).isEqualTo(testPlayer);
  }

  @Test
  void testIsHostTrue() {
    Player testPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 1234));
    lobby.addPlayer(testPlayer);

    assertThat(lobby.isHost(testPlayer.getSessionId())).isTrue();
  }

  @Test
  void testIsHostFalse() {
    Player testPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 1234));
    lobby.addPlayer(testPlayer);

    assertThat(lobby.isHost("1")).isFalse();
  }


  @Test
  void testGetHostTwoPlayers() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    lobby.addPlayer(testPlayer1);
    lobby.addPlayer(testPlayer2);

    assertThat(lobby.getHost()).isEqualTo(testPlayer1);
    assertThat(lobby.getHost()).isNotEqualTo(testPlayer2);
  }

  @Test
  void testGetHostAfterRemovingAPlayer() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    lobby.addPlayer(testPlayer1);
    lobby.addPlayer(testPlayer2);

    lobby.removePlayer(testPlayer1);

    assertThat(lobby.getHost()).isEqualTo(testPlayer2);
  }


  @Test
  void testIsHostTwoPlayers() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    lobby.addPlayer(testPlayer1);
    lobby.addPlayer(testPlayer2);

    assertThat(lobby.isHost(testPlayer1.getSessionId())).isTrue();
    assertThat(lobby.isHost(testPlayer2.getSessionId())).isFalse();
  }

  @Test
  void testIsHostAfterRemovingAPlayer() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    lobby.addPlayer(testPlayer1);
    lobby.addPlayer(testPlayer2);

    lobby.removePlayer(testPlayer1);

    assertThat(lobby.isHost(testPlayer2.getSessionId())).isTrue();
  }

  @Test
  void testSetMinPlayers() {
    int newMinPlayers = 4;
    assertThat(lobby.getMinPlayers()).isEqualTo(minPlayers);

    lobby.setMinPlayers(newMinPlayers);

    assertThat(lobby.getMinPlayers()).isEqualTo(newMinPlayers);
  }

  @Test
  void testSetGameName() {
    String newGameName = "anotherTestName";
    assertThat(lobby.getGameName()).isEqualTo(gameName);

    lobby.setGameName(newGameName);

    assertThat(lobby.getGameName()).isEqualTo(newGameName);
  }

  @Test
  void testGetPlayerStatusOnePlayer() {
    Player player = new Player("1", "testPlayer", new ConnectionData("0.0.0.0", 1));

    lobby.addPlayer(player);

    assertThat(lobby.getPlayerStatus(player)).isTrue();
  }

  @Test
  void testGetPlayerStatusTwoPlayer() {
    Player player1 = new Player("1", "testPlayer", new ConnectionData("0.0.0.0", 1));
    Player player2 = new Player("2", "testPlayer", new ConnectionData("0.0.0.0", 2));

    lobby.addPlayer(player1);
    lobby.addPlayer(player2);

    assertThat(lobby.getPlayerStatus(player1)).isTrue();
    assertThat(lobby.getPlayerStatus(player2)).isFalse();
  }

  @Test
  void testGetPlayerStatusTwoPlayerOnePlayerLeaves() {
    Player player1 = new Player("1", "testPlayer", new ConnectionData("0.0.0.0", 1));
    Player player2 = new Player("2", "testPlayer", new ConnectionData("0.0.0.0", 2));

    lobby.addPlayer(player1);
    lobby.addPlayer(player2);

    assertThat(lobby.getPlayerStatus(player1)).isTrue();
    assertThat(lobby.getPlayerStatus(player2)).isFalse();

    lobby.removePlayer(player1);

    assertThat(lobby.getPlayerStatus(player2)).isTrue();
  }

  @Test
  void testSetPlayerStatusForHost() {
    Player player = new Player("1", "testPlayer", new ConnectionData("0.0.0.0", 1));

    lobby.addPlayer(player);

    lobby.setPlayerStatus(player, false);

    assertThat(lobby.getPlayerStatus(player)).isTrue();
  }

  @Test
  void testSetPlayerStatusTwoPlayer() {
    Player player1 = new Player("1", "testPlayer", new ConnectionData("0.0.0.0", 1));
    Player player2 = new Player("2", "testPlayer", new ConnectionData("0.0.0.0", 2));

    lobby.addPlayer(player1);
    lobby.addPlayer(player2);

    assertThat(lobby.getPlayerStatus(player1)).isTrue();
    assertThat(lobby.getPlayerStatus(player2)).isFalse();

    lobby.setPlayerStatus(player1, false);
    lobby.setPlayerStatus(player2, true);

    assertThat(lobby.getPlayerStatus(player1)).isTrue();
    assertThat(lobby.getPlayerStatus(player2)).isTrue();
  }
}
