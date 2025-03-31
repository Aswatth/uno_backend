package com.example.uno.repos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repos.PlayerLobbyRepo;

class PlayerLobbyRepoTest {

  PlayerLobbyRepo playerLobbyRepo;

  @BeforeEach
  void setup() {
    playerLobbyRepo = PlayerLobbyRepo.getInstance();
  }

  @AfterEach
  void cleanup() {
    playerLobbyRepo.getAllKeys().forEach(f -> playerLobbyRepo.remove(f));
  }

  @Test
  void testAdd() {
    String gameId = "g1";
    String playerId = "p1";
    
    playerLobbyRepo.add(playerId, gameId);

    assertThat(playerLobbyRepo.get(playerId)).isEqualTo(gameId);

    playerLobbyRepo.remove(playerId);
  }

  @Test
  void testGet() {
    String gameId = "g1";
    String playerId = "p1";
    playerLobbyRepo.add(playerId, gameId);

    assertThat(playerLobbyRepo.get(playerId)).isEqualTo(gameId);
  }

  @Test
  void testGetFail() {
    assertThat(playerLobbyRepo.get("1")).isNull();
  }

  @Test
  void testRemoveValid() {
    String gameId = "g1";
    String playerId = "p1";
    playerLobbyRepo.add(playerId, gameId);

    assertThat(playerLobbyRepo.get(playerId)).isEqualTo(gameId);

    playerLobbyRepo.remove(playerId);

    assertThat(playerLobbyRepo.get(playerId)).isNull();
  }

  @Test
  void testRemoveInvalid() {
    String gameId = "g1";
    String playerId = "p1";
    playerLobbyRepo.add(playerId, gameId);

    assertThat(playerLobbyRepo.get(playerId)).isEqualTo(gameId);

    playerLobbyRepo.remove("2");

    assertThat(playerLobbyRepo.get(playerId)).isNotNull();
  }

  @Test
  void testGetAllKeys() {
    String gameId = "g1";
    String playerId = "p1";
    playerLobbyRepo.add(playerId, gameId);

    assertThat(playerLobbyRepo.getAllKeys()).hasSize(1);
    assertThat(playerLobbyRepo.getAllKeys()).isEqualTo(List.of(playerId));
  }

  @Test
  void testGetAllItems() {
    String gameId = "g1";
    String playerId = "p1";
    playerLobbyRepo.add(playerId, gameId);

    assertThat(playerLobbyRepo.getAllItems()).hasSize(1);
    assertThat(playerLobbyRepo.getAllItems()).isEqualTo(List.of(gameId));
  }

  @Test
  void testGetAllEntries() {
    String gameId = "g1";
    String playerId = "p1";
    playerLobbyRepo.add(playerId, gameId);

    assertThat(playerLobbyRepo.getEntries()).hasSize(1);
    assertThat(playerLobbyRepo.getEntries()).isEqualTo(Map.of(playerId, gameId));
  }

}
