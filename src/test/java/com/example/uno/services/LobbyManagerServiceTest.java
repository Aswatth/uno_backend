package com.example.uno.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.models.ConnectionData;
import com.example.uno.models.Player;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import repos.ChatRepo;
import repos.PlayerRepo;

@ExtendWith(MockitoExtension.class)
class LobbyManagerServiceTest {

  @Mock
  PlayerRepo playerRepo;

  @Mock
  ChatRepo chatRepo;

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

    Mockito.when(playerRepo.get("123")).thenReturn(mockPlayer);

    Mockito.when(playerRepo.getAllKeys())
        .thenReturn(Collections.singletonList(mockPlayer.getSessionId()));

    lobbyManagerService.joinLobby(gameId, mockPlayer.getSessionId());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer.getSessionId(), "/queue/host", true,
            headerAccessor.
                getMessageHeaders());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSend("/topic/lobby/" + gameId,
            Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("minPlayers", minPlayers),
                Map.entry("currentPlayers", Collections.singletonList(
                    Map.ofEntries(Map.entry("playerName", mockPlayer.getName()),
                        Map.entry("status", true))))
            ));

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer.getSessionId(), "/queue/browse-lobbies",
            Collections.singletonList(Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("minPlayers", minPlayers),
                Map.entry("currentPlayers", Collections.singletonList(
                    Map.ofEntries(Map.entry("playerName", mockPlayer.getName()),
                        Map.entry("status", true))))
            )),
            headerAccessor.getMessageHeaders());

    Mockito.verify(chatRepo).remove(mockPlayer.getSessionId());

    assertThat(lobbyManagerService.browseLobbies()).hasSize(1);
  }

  @Test
  void testJoinGameTwoPlayers() {
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

    Mockito.when(playerRepo.get("1")).thenReturn(mockPlayer1);

    Mockito.when(playerRepo.get("2")).thenReturn(mockPlayer2);

    Mockito.when(playerRepo.getAllKeys())
        .thenReturn(Collections.singletonList(mockPlayer1.getSessionId()));

    lobbyManagerService.joinLobby(gameId, mockPlayer1.getSessionId());

    // Get host status
    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer1.getSessionId(), "/queue/host", true,
            headerAccessor1.
                getMessageHeaders());

    // Notify party members
    ArgumentCaptor<Map<String, Object>> argumentCaptor = ArgumentCaptor.forClass(HashMap.class);
    Mockito.verify(simpMessagingTemplate)
        .convertAndSend(Mockito.eq("/topic/lobby/" + gameId),
            argumentCaptor.capture());

    Map<String, Object> expectedPlayer1Payload = Map.ofEntries(
        Map.entry("gameId", gameId),
        Map.entry("gameName", gameName),
        Map.entry("minPlayers", minPlayers),
        Map.entry("currentPlayers", Collections.singletonList(
            Map.ofEntries(Map.entry("playerName", mockPlayer1.getName()),
                Map.entry("status", true))))
    );
    Map<String, Object> actualPlayer1Payload = argumentCaptor.getValue();
    assertThat(actualPlayer1Payload).containsKey("gameId").containsValue(gameId)
        .containsKey("gameName").containsEntry("minPlayers", minPlayers).containsValue(gameName)
        .containsKey("currentPlayers");

    assertThat(new HashSet<>(
        (List<Map<String, Object>>) actualPlayer1Payload.get("currentPlayers"))).isEqualTo(
        new HashSet<>((List<Map<String, Object>>) expectedPlayer1Payload.get("currentPlayers")));

    // Notify other players about change in party members
    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer1.getSessionId(), "/queue/browse-lobbies",
            Collections.singletonList(Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("minPlayers", minPlayers),
                Map.entry("currentPlayers", Collections.singletonList(
                    Map.ofEntries(Map.entry("playerName", mockPlayer1.getName()),
                        Map.entry("status", true))))
            )),
            headerAccessor1.getMessageHeaders());

    Mockito.when(playerRepo.getAllKeys())
        .thenReturn(Arrays.asList(mockPlayer1.getSessionId(),
            mockPlayer2.getSessionId()));

    Mockito.verify(chatRepo).remove(mockPlayer1.getSessionId());

    lobbyManagerService.joinLobby(gameId, mockPlayer2.getSessionId());

    // Get host status
    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer2.getSessionId(), "/queue/host", false,
            headerAccessor2.
                getMessageHeaders());

    // Notify party members
    argumentCaptor = ArgumentCaptor.forClass(HashMap.class);
    Mockito.verify(simpMessagingTemplate, Mockito.times(2))
        .convertAndSend(Mockito.eq("/topic/lobby/" + gameId), argumentCaptor.capture()
        );

    Map<String, Object> expectedPlayer2Payload = Map.ofEntries(
        Map.entry("gameId", gameId),
        Map.entry("gameName", gameName),
        Map.entry("minPlayers", minPlayers),
        Map.entry("currentPlayers",
            Arrays.asList(Map.ofEntries(Map.entry("playerName", mockPlayer1.getName()),
                    Map.entry("status", true)),
                Map.ofEntries(Map.entry("playerName", mockPlayer2.getName()),
                    Map.entry("status", false))))
    );

    Map<String, Object> actualPlayer2Payload = argumentCaptor.getValue();
    assertThat(actualPlayer2Payload).containsKey("gameId").containsValue(gameId)
        .containsKey("gameName").containsEntry("minPlayers", minPlayers).containsValue(gameName)
        .containsKey("currentPlayers");

    assertThat(new HashSet<>(
        (List<Map<String, Object>>) actualPlayer2Payload.get("currentPlayers"))).isEqualTo(
        new HashSet<>((List<Map<String, Object>>) expectedPlayer2Payload.get("currentPlayers")));

    // Notify others players of change in party members
    ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);

    Mockito.verify(simpMessagingTemplate, Mockito.times(2))
        .convertAndSendToUser(Mockito.eq(mockPlayer1.getSessionId()),
            Mockito.eq("/queue/browse-lobbies"),
            listArgumentCaptor.capture(),
            Mockito.eq(headerAccessor1.getMessageHeaders()));

    List<Map<String, Object>> expectedPlayer1BrowseGamesPayload = Collections.singletonList(
        Map.ofEntries(
            Map.entry("gameId", gameId),
            Map.entry("gameName", gameName),
            Map.entry("minPlayers", minPlayers),
            Map.entry("currentPlayers",
                Arrays.asList(Map.ofEntries(Map.entry("playerName", mockPlayer1.getName()),
                        Map.entry("status", true)),
                    Map.ofEntries(Map.entry("playerName", mockPlayer2.getName()),
                        Map.entry("status", false))))
        ));

    List<Map<String, Object>> actualPlayer1BrowseGamesPayload = listArgumentCaptor.getValue();
    assertThat(actualPlayer1BrowseGamesPayload).hasSize(1);
    assertThat(actualPlayer1BrowseGamesPayload.getFirst()).containsEntry("gameId", gameId);
    assertThat(actualPlayer1BrowseGamesPayload.getFirst()).containsEntry("gameName", gameName);
    assertThat(actualPlayer1BrowseGamesPayload.getFirst()).containsEntry("minPlayers", minPlayers);
    assertThat(actualPlayer1BrowseGamesPayload.getFirst()).containsKey("currentPlayers");

    assertThat(new HashSet<>(
        (List<Map<String, Object>>) actualPlayer1BrowseGamesPayload.getFirst()
            .get("currentPlayers"))).isEqualTo(
        new HashSet<>((List<Map<String, Object>>) expectedPlayer1BrowseGamesPayload.getFirst()
            .get("currentPlayers")));

    // Notify others players of change in party members
    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(Mockito.eq(mockPlayer2.getSessionId()),
            Mockito.eq("/queue/browse-lobbies"),
            listArgumentCaptor.capture(),
            Mockito.eq(headerAccessor2.getMessageHeaders()));

    List<Map<String, Object>> expectedPlayer2BrowseGamesPayload = Collections.singletonList(
        Map.ofEntries(
            Map.entry("gameId", gameId),
            Map.entry("gameName", gameName),
            Map.entry("minPlayers", minPlayers),
            Map.entry("currentPlayers",
                Arrays.asList(Map.ofEntries(Map.entry("playerName", mockPlayer1.getName()),
                        Map.entry("status", true)),
                    Map.ofEntries(Map.entry("playerName", mockPlayer2.getName()),
                        Map.entry("status", false))))
        ));

    List<Map<String, Object>> actualPlayer2BrowseGamesPayload = listArgumentCaptor.getValue();
    assertThat(actualPlayer2BrowseGamesPayload).hasSize(1);
    assertThat(actualPlayer2BrowseGamesPayload.getFirst()).containsEntry("gameId", gameId);
    assertThat(actualPlayer2BrowseGamesPayload.getFirst()).containsEntry("gameName", gameName);
    assertThat(actualPlayer2BrowseGamesPayload.getFirst()).containsEntry("minPlayers", minPlayers);
    assertThat(actualPlayer2BrowseGamesPayload.getFirst()).containsKey("currentPlayers");

    assertThat(new HashSet<>(
        (List<Map<String, Object>>) actualPlayer2BrowseGamesPayload.getFirst()
            .get("currentPlayers"))).isEqualTo(
        new HashSet<>((List<Map<String, Object>>) expectedPlayer2BrowseGamesPayload.getFirst()
            .get("currentPlayers")));

    Mockito.verify(chatRepo).remove(mockPlayer2.getSessionId());

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

    Mockito.when(playerRepo.get("123")).thenReturn(mockPlayer);

    Mockito.when(playerRepo.getAllKeys())
        .thenReturn(Collections.singletonList(mockPlayer.getSessionId()));

    lobbyManagerService.joinLobby(gameId, mockPlayer.getSessionId());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer.getSessionId(), "/queue/host", true,
            headerAccessor.
                getMessageHeaders());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSend("/topic/lobby/" + gameId,
            Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("minPlayers", minPlayers),
                Map.entry("currentPlayers", Collections.singletonList(
                    Map.ofEntries(Map.entry("playerName", mockPlayer.getName()),
                        Map.entry("status", true))))
            ));

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer.getSessionId(), "/queue/browse-lobbies",
            Collections.singletonList(Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("minPlayers", minPlayers),
                Map.entry("currentPlayers", Collections.singletonList(
                    Map.ofEntries(Map.entry("playerName", mockPlayer.getName()),
                        Map.entry("status", true))))
            )),
            headerAccessor.getMessageHeaders());

    Mockito.verify(chatRepo).remove(mockPlayer.getSessionId());

    lobbyManagerService.leaveLobby(mockPlayer.getSessionId());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer.getSessionId(), "/queue/browse-lobbies",
            Collections.emptyList(),
            headerAccessor.getMessageHeaders());

    Mockito.verify(chatRepo, Mockito.times(2)).remove(mockPlayer.getSessionId());

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

    Mockito.when(playerRepo.get("1")).thenReturn(mockPlayer1);

    Mockito.when(playerRepo.get("2")).thenReturn(mockPlayer2);

    lobbyManagerService.joinLobby(gameId, mockPlayer1.getSessionId());

    lobbyManagerService.joinLobby(gameId, mockPlayer2.getSessionId());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer2.getSessionId(), "/queue/host", false,
            headerAccessor2.
                getMessageHeaders());

    Mockito.when(playerRepo.getAllKeys())
        .thenReturn(Collections.singletonList(mockPlayer2.getSessionId()));

    Mockito.verify(chatRepo).remove(mockPlayer2.getSessionId());

    lobbyManagerService.leaveLobby(mockPlayer1.getSessionId());

    // Update party host
    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer2.getSessionId(), "/queue/host", true,
            headerAccessor2.
                getMessageHeaders());

    // Notify party members
    Mockito.verify(simpMessagingTemplate)
        .convertAndSend("/topic/lobby/" + gameId,
            Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("minPlayers", minPlayers),
                Map.entry("currentPlayers",
                    Collections.singletonList(
                        Map.ofEntries(Map.entry("playerName", mockPlayer2.getName()),
                            Map.entry("status", true)))))
        );

    // Notify other players of the change in party members
    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(mockPlayer2.getSessionId(), "/queue/browse-lobbies",
            Collections.singletonList(Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("gameName", gameName),
                Map.entry("minPlayers", minPlayers),
                Map.entry("currentPlayers",
                    Collections.singletonList(
                        Map.ofEntries(Map.entry("playerName", mockPlayer2.getName()),
                            Map.entry("status", true)))))),
            headerAccessor2.getMessageHeaders());

    assertThat(lobbyManagerService.browseLobbies()).hasSize(1);

    Mockito.verify(chatRepo, Mockito.times(2)).remove(mockPlayer1.getSessionId());
  }

  @Test
  void testBrowseGame() {
    Random random = new Random();
    String gameName = "testGame";
    int minPlayers = random.nextInt(7) + 1;
    String gameId = lobbyManagerService.createLobby(gameName, minPlayers);

    Player mockPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 12345));
    Mockito.when(playerRepo.get("123")).thenReturn(mockPlayer);

    lobbyManagerService.joinLobby(gameId, mockPlayer.getSessionId());

    List<Map<String, Object>> gameList = lobbyManagerService.browseLobbies();

    assertThat(gameList).hasSize(1);
    assertThat(gameList.getFirst()).containsEntry("gameId", gameId);
    assertThat(gameList.getFirst()).containsEntry("gameName", gameName);
    assertThat(gameList.getFirst()).containsEntry("minPlayers", minPlayers);

    assertThat(gameList.getFirst()).containsEntry("currentPlayers",
        Collections.singletonList(Map.ofEntries(Map.entry("playerName", mockPlayer.getName()),
            Map.entry("status", true))));
  }
}
