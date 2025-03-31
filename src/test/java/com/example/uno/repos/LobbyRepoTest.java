package com.example.uno.repos;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.models.Lobby;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repos.LobbyRepo;

class LobbyRepoTest {

  LobbyRepo lobbyRepo;

  @BeforeEach
  void setup() {
    lobbyRepo = LobbyRepo.getInstance();
  }

  @AfterEach
  void cleanup() {
    lobbyRepo.getAllKeys().forEach(f -> lobbyRepo.remove(f));
  }

  @Test
  void testAdd() {
    Lobby lobby = new Lobby("1", "testGame", 2);
    lobbyRepo.add(lobby.getGameId(), lobby);

    assertThat(lobbyRepo.get(lobby.getGameId())).isEqualTo(lobby);

    lobbyRepo.remove(lobby.getGameId());
  }

  @Test
  void testGet() {
    Lobby lobby = new Lobby("1", "testGame", 2);
    lobbyRepo.add(lobby.getGameId(), lobby);

    assertThat(lobbyRepo.get(lobby.getGameId())).isEqualTo(lobby);
  }

  @Test
  void testGetFail() {
    assertThat(lobbyRepo.get("1")).isNull();
  }

  @Test
  void testRemoveValid() {
    Lobby lobby = new Lobby("1", "testGame", 2);
    lobbyRepo.add(lobby.getGameId(), lobby);

    assertThat(lobbyRepo.get(lobby.getGameId())).isEqualTo(lobby);

    lobbyRepo.remove(lobby.getGameId());

    assertThat(lobbyRepo.get(lobby.getGameId())).isNull();
  }

  @Test
  void testRemoveInvalid() {
    Lobby lobby = new Lobby("1", "testGame", 2);
    lobbyRepo.add(lobby.getGameId(), lobby);

    assertThat(lobbyRepo.get(lobby.getGameId())).isEqualTo(lobby);

    lobbyRepo.remove("2");

    assertThat(lobbyRepo.get(lobby.getGameId())).isNotNull();
  }

  @Test
  void testGetAllKeys() {
    Lobby lobby = new Lobby("1", "testGame", 2);
    lobbyRepo.add(lobby.getGameId(), lobby);

    assertThat(lobbyRepo.getAllKeys()).hasSize(1);
    assertThat(lobbyRepo.getAllKeys()).isEqualTo(Collections.singletonList(lobby.getGameId()));
  }

  @Test
  void testGetAllItems() {
    Lobby lobby = new Lobby("1", "testGame", 2);
    lobbyRepo.add(lobby.getGameId(), lobby);

    assertThat(lobbyRepo.getAllItems()).hasSize(1);
    assertThat(lobbyRepo.getAllItems()).isEqualTo(List.of(lobby));
  }

  @Test
  void testGetAllEntries() {
    Lobby lobby = new Lobby("1", "testGame", 2);
    lobbyRepo.add(lobby.getGameId(), lobby);

    assertThat(lobbyRepo.getEntries()).hasSize(1);
    assertThat(lobbyRepo.getEntries()).isEqualTo(Map.of(lobby.getGameId(), lobby));
  }

}
