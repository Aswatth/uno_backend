package com.example.uno.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GameModelTest {

  private final String gameName = "testGame";
  private final int minPlayers = 2;
  private final String gameId = "123";
  private Game game;

  @BeforeEach
  public void setup() {
    game = new Game(gameId, minPlayers, gameName);
  }

  @Test
  public void testGameCreation() {
    assertThat(game).isNotNull();
  }

  @Test
  public void testToMap() {
    Map<String, Object> map = game.toMap();
    assertThat(map.get("gameId")).isEqualTo(gameId);
    assertThat(map.get("gameName")).isEqualTo(gameName);
  }

  @Test
  public void testGetMinPlayers() {
    assertThat(game.getMinPlayers()).isEqualTo(2);
  }

  @Test
  public void testGetGameName() {
    assertThat(game.getGameName()).isEqualTo(gameName);
  }

  @Test
  public void testGetPlayer() {
    Player testPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 1234));
    game.addPlayer(testPlayer);

    assertThat(game.getCurrentPlayers().size()).isEqualTo(1);
    assertThat(game.getCurrentPlayers().stream().findFirst().get()).isEqualTo(testPlayer);
  }

  @Test
  public void testGetPlayerAfterRemoving() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    game.removePlayer(testPlayer1);

    assertThat(game.getCurrentPlayers().size()).isEqualTo(1);
  }

  @Test
  public void testGetPlayerTwoPlayers() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    assertThat(game.getCurrentPlayers().size()).isEqualTo(2);
  }

  @Test
  public void testGetHost() {
    Player testPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 1234));
    game.addPlayer(testPlayer);

    assertThat(game.getHost()).isEqualTo(testPlayer);
  }

  @Test
  public void testIsHostTrue() {
    Player testPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 1234));
    game.addPlayer(testPlayer);

    assertThat(game.isHost(testPlayer.getSessionId())).isTrue();
  }

  @Test
  public void testIsHostFalse() {
    Player testPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 1234));
    game.addPlayer(testPlayer);

    assertThat(game.isHost("1")).isFalse();
  }


  @Test
  public void testGetHostTwoPlayers() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    assertThat(game.getHost()).isEqualTo(testPlayer1);
    assertThat(game.getHost()).isNotEqualTo(testPlayer2);
  }

  @Test
  public void testGetHostAfterRemovingAPlayer() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    game.removePlayer(testPlayer1);

    assertThat(game.getHost()).isEqualTo(testPlayer2);
  }


  @Test
  public void testIsHostTwoPlayers() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    assertThat(game.isHost(testPlayer1.getSessionId())).isTrue();
    assertThat(game.isHost(testPlayer2.getSessionId())).isFalse();
  }

  @Test
  public void testIsHostAfterRemovingAPlayer() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    game.removePlayer(testPlayer1);

    assertThat(game.isHost(testPlayer2.getSessionId())).isTrue();
  }

  @Test
  public void testSetMinPlayers() {
    int newMinPlayers = 4;
    assertThat(game.getMinPlayers()).isEqualTo(minPlayers);

    game.setMinPlayers(newMinPlayers);

    assertThat(game.getMinPlayers()).isEqualTo(newMinPlayers);
  }

  @Test
  public void testSetGameName() {
    String newGameName = "anotherTestName";
    assertThat(game.getGameName()).isEqualTo(gameName);

    game.setGameName(newGameName);

    assertThat(game.getGameName()).isEqualTo(newGameName);
  }
}
