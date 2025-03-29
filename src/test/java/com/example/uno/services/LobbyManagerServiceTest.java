package com.example.uno.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.models.ConnectionData;
import com.example.uno.models.Player;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class LobbyManagerServiceTest {

  @Mock
  PlayerService playerService;

  @Mock
  SimpMessagingTemplate simpMessagingTemplate;

  @InjectMocks
  LobbyManagerService lobbyManagerService;

  @Test
  void contextLoad() {
    assertThat(lobbyManagerService).isNotNull();
  }

  @Test
  void testCreateGame() {
    String gameName = "testGame";
    int minPlayers = 2;

    String gameId = lobbyManagerService.createLobby(gameName, minPlayers);
    assertThat(gameId).isNotEmpty();
  }

  @Test
  void testJoinGame() {
    Random random = new Random();
    String gameName = "testGame";
    int minPlayers = random.nextInt(7) + 1;
    String gameId = lobbyManagerService.createLobby(gameName, minPlayers);

    Player mockPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 12345));

    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(
        SimpMessageType.MESSAGE);
    headerAccessor.setSessionId(mockPlayer.getSessionId());

    Mockito.when(playerService.getPlayer("123")).thenReturn(mockPlayer);

    Mockito.when(playerService.getPlayerSessionIdList())
        .thenReturn(Collections.singletonList(mockPlayer.getSessionId()));

    lobbyManagerService.joinLobby(gameId, mockPlayer.getSessionId());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer.getSessionId(), "/queue/host", true,
            headerAccessor.
                getMessageHeaders());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSend("/topic/join-lobby/" + gameId,
            Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("currentPlayers", Collections.singletonList(mockPlayer.getName()))
            ));

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer.getSessionId(), "/queue/browse-lobbies",
            Collections.singletonList(Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("currentPlayers", Collections.singletonList(mockPlayer.getName()))
            )),
            headerAccessor.getMessageHeaders());

    assertThat(lobbyManagerService.browseLobbies()).hasSize(1);
  }

  @Test
  void testJoinGameTwoPlayers() {
    Random random = new Random();
    String gameName = "testGame";
    int minPlayers = random.nextInt(7) + 1;
    String gameId = lobbyManagerService.createLobby(gameName, minPlayers);

    Player mockPlayer1 = new Player("1", "testPlayer", new ConnectionData("0.0.0.0", 1));
    Player mockPlayer2 = new Player("2", "testPlayer", new ConnectionData("0.0.0.0", 2));

    SimpMessageHeaderAccessor headerAccessor1 = SimpMessageHeaderAccessor.create(
        SimpMessageType.MESSAGE);
    headerAccessor1.setSessionId(mockPlayer1.getSessionId());

    SimpMessageHeaderAccessor headerAccessor2 = SimpMessageHeaderAccessor.create(
        SimpMessageType.MESSAGE);
    headerAccessor2.setSessionId(mockPlayer2.getSessionId());

    Mockito.when(playerService.getPlayer("1")).thenReturn(mockPlayer1);

    Mockito.when(playerService.getPlayer("2")).thenReturn(mockPlayer2);

    Mockito.when(playerService.getPlayerSessionIdList())
        .thenReturn(Collections.singletonList(mockPlayer1.getSessionId()));

    lobbyManagerService.joinLobby(gameId, mockPlayer1.getSessionId());

    // Get host status
    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer1.getSessionId(), "/queue/host", true,
            headerAccessor1.
                getMessageHeaders());

    // Notify party members
    Mockito.verify(simpMessagingTemplate)
        .convertAndSend("/topic/join-lobby/" + gameId,
            Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("currentPlayers", Collections.singletonList(mockPlayer1.getName()))
            ));

    // Notify other players about change in party members
    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer1.getSessionId(), "/queue/browse-lobbies",
            Collections.singletonList(Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("currentPlayers", Collections.singletonList(mockPlayer1.getName()))
            )),
            headerAccessor1.getMessageHeaders());

    Mockito.when(playerService.getPlayerSessionIdList())
        .thenReturn(Arrays.asList(mockPlayer1.getSessionId(),
            mockPlayer2.getSessionId()));

    lobbyManagerService.joinLobby(gameId, mockPlayer2.getSessionId());

    // Get host status
    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer2.getSessionId(), "/queue/host", false,
            headerAccessor2.
                getMessageHeaders());

    // Notify party members
    Mockito.verify(simpMessagingTemplate)
        .convertAndSend("/topic/join-lobby/" + gameId,
            Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("currentPlayers",
                    Arrays.asList(mockPlayer1.getName(), mockPlayer2.getName())))
        );

    // Notify others players of change in party members
    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer1.getSessionId(), "/queue/browse-lobbies",
            Collections.singletonList(Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("currentPlayers",
                    Arrays.asList(mockPlayer1.getName(), mockPlayer2.getName()))
            )),
            headerAccessor1.getMessageHeaders());

    // Notify others players of change in party members
    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer2.getSessionId(), "/queue/browse-lobbies",
            Collections.singletonList(Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("currentPlayers",
                    Arrays.asList(mockPlayer1.getName(), mockPlayer2.getName()))
            )),
            headerAccessor2.getMessageHeaders());

    assertThat(lobbyManagerService.browseLobbies()).hasSize(1);
  }

  @Test
  void testLeaveGame() {
    Random random = new Random();
    String gameName = "testGame";
    int minPlayers = random.nextInt(7) + 1;
    String gameId = lobbyManagerService.createLobby(gameName, minPlayers);

    Player mockPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 12345));

    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(
        SimpMessageType.MESSAGE);
    headerAccessor.setSessionId(mockPlayer.getSessionId());

    Mockito.when(playerService.getPlayer("123")).thenReturn(mockPlayer);

    Mockito.when(playerService.getPlayerSessionIdList())
        .thenReturn(Collections.singletonList(mockPlayer.getSessionId()));

    lobbyManagerService.joinLobby(gameId, mockPlayer.getSessionId());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer.getSessionId(), "/queue/host", true,
            headerAccessor.
                getMessageHeaders());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSend("/topic/join-lobby/" + gameId,
            Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("currentPlayers", Collections.singletonList(mockPlayer.getName()))
            ));

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer.getSessionId(), "/queue/browse-lobbies",
            Collections.singletonList(Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("currentPlayers", Collections.singletonList(mockPlayer.getName()))
            )),
            headerAccessor.getMessageHeaders());

    lobbyManagerService.leaveLobby(mockPlayer.getSessionId());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer.getSessionId(), "/queue/browse-lobbies",
            Collections.emptyList(),
            headerAccessor.getMessageHeaders());

    assertThat(lobbyManagerService.browseLobbies()).isEmpty();
  }

  @Test
  void testJoinGameTwoPlayersOneLeaves() {
    Random random = new Random();
    String gameName = "testGame";
    int minPlayers = random.nextInt(7) + 1;
    String gameId = lobbyManagerService.createLobby(gameName, minPlayers);

    Player mockPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player mockPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    SimpMessageHeaderAccessor headerAccessor1 = SimpMessageHeaderAccessor.create(
        SimpMessageType.MESSAGE);
    headerAccessor1.setSessionId(mockPlayer1.getSessionId());

    SimpMessageHeaderAccessor headerAccessor2 = SimpMessageHeaderAccessor.create(
        SimpMessageType.MESSAGE);
    headerAccessor2.setSessionId(mockPlayer2.getSessionId());

    Mockito.when(playerService.getPlayer("1")).thenReturn(mockPlayer1);

    Mockito.when(playerService.getPlayer("2")).thenReturn(mockPlayer2);

    lobbyManagerService.joinLobby(gameId, mockPlayer1.getSessionId());

    lobbyManagerService.joinLobby(gameId, mockPlayer2.getSessionId());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer2.getSessionId(), "/queue/host", false,
            headerAccessor2.
                getMessageHeaders());

    Mockito.when(playerService.getPlayerSessionIdList())
        .thenReturn(Collections.singletonList(mockPlayer2.getSessionId()));

    lobbyManagerService.leaveLobby(mockPlayer1.getSessionId());

    // Update party host
    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer2.getSessionId(), "/queue/host", true,
            headerAccessor2.
                getMessageHeaders());

    // Notify party members
    Mockito.verify(simpMessagingTemplate)
        .convertAndSend("/topic/join-lobby/" + gameId,
            Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("currentPlayers",
                    Collections.singletonList(mockPlayer2.getName())))
        );

    // Notify other players of the change in party members
    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer2.getSessionId(), "/queue/browse-lobbies",
            Collections.singletonList(Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("currentPlayers",
                    Collections.singletonList(mockPlayer2.getName())))),
            headerAccessor2.getMessageHeaders());

    assertThat(lobbyManagerService.browseLobbies()).hasSize(1);
  }

  @Test
  void testBrowseGame() {
    Random random = new Random();
    String gameName = "testGame";
    int minPlayers = random.nextInt(7) + 1;
    String gameId = lobbyManagerService.createLobby(gameName, minPlayers);

    Player mockPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 12345));
    Mockito.when(playerService.getPlayer("123")).thenReturn(mockPlayer);

    lobbyManagerService.joinLobby(gameId, mockPlayer.getSessionId());

    List<Map<String, Object>> gameList = lobbyManagerService.browseLobbies();

    assertThat(gameList).hasSize(1);
    assertThat(gameList.getFirst()).containsEntry("gameId", gameId);
    assertThat(gameList.getFirst()).containsEntry("gameName", gameName);

    assertThat(gameList.getFirst()).containsEntry("currentPlayers",
        Collections.singletonList(mockPlayer.getName()));
  }
}
