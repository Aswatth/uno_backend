package com.example.uno.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.models.ConnectionData;
import com.example.uno.models.Player;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

  @InjectMocks
  PlayerService playerService;

  @AfterEach
  void cleanup() {
    playerService.getPlayerSessionIdList().forEach(f -> playerService.removePlayer(f));
  }

  @Test
  void contextLoad() {
    assertThat(playerService).isNotNull();
  }

  @Test
  void testAddPlayer() {
    String sessionId = "123";
    String playerName = "testPlayer";
    ConnectionData connectionData = new ConnectionData("0.0.0.0", 12345);

    playerService.addPlayer(sessionId, playerName, connectionData);

    Player player = playerService.getPlayer(sessionId);

    assertThat(player.getSessionId()).isEqualTo(sessionId);
    assertThat(player.getName()).isEqualTo(playerName);
    assertThat(player.getConnectionData()).isEqualTo(connectionData);
  }

  @Test
  void testRemovePlayer() {
    String sessionId = "123";
    String playerName = "testPlayer";
    ConnectionData connectionData = new ConnectionData("0.0.0.0", 12345);

    playerService.addPlayer(sessionId, playerName, connectionData);

    playerService.removePlayer(sessionId);

    Player player = playerService.getPlayer(sessionId);

    assertThat(player).isNull();
  }

  @Test
  void testGetPlayer() {
    String sessionId = "123";
    String playerName = "testPlayer";
    ConnectionData connectionData = new ConnectionData("0.0.0.0", 12345);

    playerService.addPlayer(sessionId, playerName, connectionData);

    Player player = playerService.getPlayer("123");

    assertThat(player.getSessionId()).isEqualTo(sessionId);
    assertThat(player.getName()).isEqualTo(playerName);

    assertThat(player.getConnectionData().ipAddress()).isEqualTo(connectionData.ipAddress());
    assertThat(player.getConnectionData().port()).isEqualTo(connectionData.port());
  }

  @Test
  void testGetPlayerFailure() {
    Player player = playerService.getPlayer("123");

    assertThat(player).isNull();
  }

  @Test
  void testGetPlayerSessionIdList() {
    int count = 5;
    for (int i = 1; i <= count; ++i) {
      String sessionId = Integer.toString(i);
      String playerName = "testPlayer" + i;
      ConnectionData connectionData = new ConnectionData("0.0.0.0", 12345);

      playerService.addPlayer(sessionId, playerName, connectionData);
    }

    List<String> sessionIdList = playerService.getPlayerSessionIdList();

    assertThat(sessionIdList).hasSize(count);
    for (int i = 1; i <= count; ++i) {
      assertThat(sessionIdList.get(i - 1)).isEqualTo(Integer.toString(i));
    }
  }

}
