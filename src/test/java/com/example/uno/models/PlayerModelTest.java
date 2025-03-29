package com.example.uno.models;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerModelTest {

  private final String sessionId = "123";
  private final String playerName = "testPlayer";
  private final ConnectionData connectionData = new ConnectionData("0.0.0.0", 1234);
  private Player player;

  @BeforeEach
  void setup() {
    player = new Player(sessionId, playerName, connectionData);
  }

  @Test
  void testGetSessionId() {
    assertThat(player.getSessionId()).isEqualTo(sessionId);
  }

  @Test
  void testSetSessionId() {
    String newSessionId = "234";
    player.setSessionId(newSessionId);

    assertThat(player.getSessionId()).isEqualTo(newSessionId);
  }

  @Test
  void testGetName() {
    assertThat(player.getName()).isEqualTo(playerName);
  }

  @Test
  void testSetName() {
    String newPlayerName = "newName";
    player.setName(newPlayerName);

    assertThat(player.getName()).isEqualTo(newPlayerName);
  }

  @Test
  void testGetConnectionData() {
    assertThat(player.getConnectionData()).isEqualTo(connectionData);
  }

  @Test
  void testSetConnectionData() {
    ConnectionData newConnectionData = new ConnectionData("0.0.0.0", 12345);
    player.setConnectionData(newConnectionData);

    assertThat(player.getConnectionData()).isEqualTo(newConnectionData);
  }
}
