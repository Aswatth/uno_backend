package com.example.uno.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.models.ConnectionData;
import com.example.uno.models.Player;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

  @InjectMocks
  PlayerService playerService;

  @Test
  public void contextLoad() {
    assertThat(playerService).isNotNull();
  }

  @Test
  public void testAddPlayer() {
    String sessionId = "123";
    String playerName = "testPlayer";
    ConnectionData connectionData = new ConnectionData("0.0.0.0", 12345);

    playerService.addPlayer(sessionId, playerName, connectionData);

    Player player = playerService.getPlayer("123");

    assertThat(player).isNotNull();

    assertThat(player.getSessionId()).isEqualTo(sessionId);
    assertThat(player.getName()).isEqualTo(playerName);

    assertThat(player.getConnectionData().ipAddress()).isEqualTo(connectionData.ipAddress());
    assertThat(player.getConnectionData().port()).isEqualTo(connectionData.port());

  }

  @Test
  public void testRemovePlayer() {
    String sessionId = "123";
    String playerName = "testPlayer";
    ConnectionData connectionData = new ConnectionData("0.0.0.0", 12345);

    playerService.addPlayer(sessionId, playerName, connectionData);

    playerService.removePlayer(sessionId);

    Player player = playerService.getPlayer("123");

    assertThat(player).isNull();
  }

  @Test
  public void testGetPlayer() {
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
  public void testGetPlayerFailure() {
    Player player = playerService.getPlayer("123");

    assertThat(player).isNull();
  }

  @Test
  public void testGetPlayerSessionIdList() {
    int count = 5;
    for (int i = 1; i <= count; ++i) {
      String sessionId = Integer.toString(i);
      String playerName = "testPlayer" + i;
      ConnectionData connectionData = new ConnectionData("0.0.0.0", 12345);

      playerService.addPlayer(sessionId, playerName, connectionData);
    }

    List<String> sessionIdList = playerService.getPlayerSessionIdList();

    assertThat(sessionIdList.size()).isEqualTo(count);
    for (int i = 1; i <= count; ++i) {
      assertThat(sessionIdList.get(i - 1)).isEqualTo(Integer.toString(i));
    }
  }

}
