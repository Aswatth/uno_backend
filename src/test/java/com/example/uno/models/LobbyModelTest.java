package com.example.uno.models;

import static org.assertj.core.api.Assertions.assertThat;

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
  void testToMap() {
    Map<String, Object> map = lobby.toMap();
    assertThat(map).containsEntry("gameId", gameId).containsEntry("gameName", gameName);
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
}
