package com.example.uno.repos;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.models.ConnectionData;
import com.example.uno.models.Player;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repos.PlayerRepo;

class PlayerRepoTest {

  PlayerRepo playerRepo;

  @BeforeEach
  void setup() {
    playerRepo = PlayerRepo.getInstance();
  }

  @AfterEach
  void cleanup() {
    playerRepo.getAllKeys().forEach(f -> playerRepo.remove(f));
  }

  @Test
  void testAdd() {
    Player player = new Player("1", "testPlayer", new ConnectionData("0.0 .0 .0", 1));
    playerRepo.add(player.getSessionId(), player);

    assertThat(playerRepo.get(player.getSessionId())).isEqualTo(player);

    playerRepo.remove(player.getSessionId());
  }

  @Test
  void testGet() {
    Player player = new Player("1", "testPlayer", new ConnectionData("0.0 .0 .0", 1));
    playerRepo.add(player.getSessionId(), player);

    assertThat(playerRepo.get(player.getSessionId())).isEqualTo(player);
  }

  @Test
  void testGetFail() {
    assertThat(playerRepo.get("1")).isNull();
  }

  @Test
  void testRemoveValid() {
    Player player = new Player("1", "testPlayer", new ConnectionData("0.0 .0 .0", 1));
    playerRepo.add(player.getSessionId(), player);

    assertThat(playerRepo.get(player.getSessionId())).isEqualTo(player);

    playerRepo.remove(player.getSessionId());

    assertThat(playerRepo.get(player.getSessionId())).isNull();
  }

  @Test
  void testRemoveInvalid() {
    Player player = new Player("1", "testPlayer", new ConnectionData("0.0 .0 .0", 1));
    playerRepo.add(player.getSessionId(), player);

    assertThat(playerRepo.get(player.getSessionId())).isEqualTo(player);

    playerRepo.remove("2");

    assertThat(playerRepo.get(player.getSessionId())).isNotNull();
  }

  @Test
  void testGetAllKeys() {
    Player player = new Player("1", "testPlayer", new ConnectionData("0.0 .0 .0", 1));
    playerRepo.add(player.getSessionId(), player);

    assertThat(playerRepo.getAllKeys()).hasSize(1);
    assertThat(playerRepo.getAllKeys()).isEqualTo(Collections.singletonList(player.getSessionId()));
  }

  @Test
  void testGetAllItems() {
    Player player = new Player("1", "testPlayer", new ConnectionData("0.0 .0 .0", 1));
    playerRepo.add(player.getSessionId(), player);

    assertThat(playerRepo.getAllItems()).hasSize(1);
    assertThat(playerRepo.getAllItems()).isEqualTo(List.of(player));
  }

  @Test
  void testGetAllEntries() {
    Player player = new Player("1", "testPlayer", new ConnectionData("0.0 .0 .0", 1));
    playerRepo.add(player.getSessionId(), player);

    assertThat(playerRepo.getEntries()).hasSize(1);
    assertThat(playerRepo.getEntries()).isEqualTo(Map.of(player.getSessionId(), player));
  }

}
