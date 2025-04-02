package com.example.uno.repos;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.models.ConnectionData;
import com.example.uno.models.Game;
import com.example.uno.models.Player;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repos.GameRepo;

class GameRepoTest {

  private GameRepo gameRepo;
  private List<Player> playerList;
  private final String gameId = "g123";

  @BeforeEach
  void setup() {
    gameRepo = GameRepo.getInstance();
    playerList = List.of(new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1)),
        new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2)));
  }

  @AfterEach
  void cleanup() {
    gameRepo.getAllKeys().forEach(f -> gameRepo.remove(f));
  }

  @Test
  void testAdd() {
    Game game = new Game(playerList);
    gameRepo.add(gameId, game);

    assertThat(gameRepo.get(gameId)).isEqualTo(game);

    gameRepo.remove(gameId);
  }

  @Test
  void testGet() {
    Game game = new Game(playerList);
    gameRepo.add(gameId, game);

    assertThat(gameRepo.get(gameId)).isEqualTo(game);
  }

  @Test
  void testGetFail() {
    assertThat(gameRepo.get("1")).isNull();
  }

  @Test
  void testRemoveValid() {
    Game game = new Game(playerList);
    gameRepo.add(gameId, game);

    assertThat(gameRepo.get(gameId)).isEqualTo(game);

    gameRepo.remove(gameId);

    assertThat(gameRepo.get(gameId)).isNull();
  }

  @Test
  void testRemoveInvalid() {
    Game game = new Game(playerList);
    gameRepo.add(gameId, game);

    assertThat(gameRepo.get(gameId)).isEqualTo(game);

    gameRepo.remove("2");

    assertThat(gameRepo.get(gameId)).isNotNull();
  }

  @Test
  void testGetAllKeys() {
    Game game = new Game(playerList);
    gameRepo.add(gameId, game);

    assertThat(gameRepo.getAllKeys()).hasSize(1);
    assertThat(gameRepo.getAllKeys()).isEqualTo(Collections.singletonList(gameId));
  }

  @Test
  void testGetAllItems() {
    Game game = new Game(playerList);
    gameRepo.add(gameId, game);

    assertThat(gameRepo.getAllItems()).hasSize(1);
    assertThat(gameRepo.getAllItems()).isEqualTo(List.of(game));
  }

  @Test
  void testGetAllEntries() {
    Game game = new Game(playerList);
    gameRepo.add(gameId, game);

    assertThat(gameRepo.getEntries()).hasSize(1);
    assertThat(gameRepo.getEntries()).isEqualTo(Map.of(gameId, game));
  }
}
