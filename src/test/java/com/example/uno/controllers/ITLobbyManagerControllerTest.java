package com.example.uno.controllers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.example.uno.services.LobbyManagerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import repos.LobbyRepo;
import repos.PlayerLobbyRepo;
import repos.PlayerRepo;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class ITLobbyManagerControllerTest {

  @LocalServerPort
  int port;

  static final String WEBSOCKET_URI = "ws://localhost:$PORT/uno";
  BlockingQueue<String> blockingQueue;
  WebSocketStompClient stompClient;

  @MockitoBean
  LobbyManagerService lobbyManagerService;

  @InjectMocks
  LobbyManagerController lobbyManagerController;

  class DefaultStompFrameHandler implements StompFrameHandler {

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
      return byte[].class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
      blockingQueue.offer(new String((byte[]) o));
    }
  }

  @BeforeEach
  void setup() {
    blockingQueue = new LinkedBlockingDeque<>();
    stompClient = new WebSocketStompClient(new StandardWebSocketClient());
  }

  @AfterEach
  void cleanup() {
    LobbyRepo.getInstance().getAllKeys().forEach(f -> LobbyRepo.getInstance().remove(f));
    PlayerRepo.getInstance().getAllKeys().forEach(f -> PlayerRepo.getInstance().remove(f));
    PlayerLobbyRepo.getInstance().getAllKeys()
        .forEach(f -> PlayerLobbyRepo.getInstance().remove(f));
  }

  @Test
  void contextLoad() {
    assertThat(lobbyManagerController).isNotNull();
  }

  @Test
  void testCreateLobby() throws Exception {

    String gameName = "testGame";
    int minPlayers = 2;
    String gameId = "123";

    Map<String, Object> payload = Map.ofEntries(Map.entry("gameName", gameName),
        Map.entry("minPlayers", minPlayers));

    Mockito.when(lobbyManagerService.createLobby(gameName, minPlayers)).thenReturn(gameId);

    StompSession session = stompClient
        .connectAsync(WEBSOCKET_URI.replace("$PORT", Integer.toString(port)),
            new StompSessionHandlerAdapter() {
            })
        .get(1, SECONDS);
    session.subscribe("/user/queue/lobby", new DefaultStompFrameHandler());

    session.send("/app/lobby", new ObjectMapper().writeValueAsBytes(payload));

    await().atMost(1, SECONDS).untilAsserted(() -> {
      Mockito.verify(lobbyManagerService).createLobby(gameName, minPlayers);
      assertThat(blockingQueue.poll()).isEqualTo(gameId);
    });
  }

  @Test
  void testEditMinPlayers() throws Exception {
    String gameId = "g123";
    int minPlayers = 3;

    StompSession session = stompClient
        .connectAsync(WEBSOCKET_URI.replace("$PORT", Integer.toString(port)),
            new StompSessionHandlerAdapter() {
            })
        .get(1, SECONDS);
    session.subscribe("/topic/lobby/" + gameId, new DefaultStompFrameHandler());
    session.subscribe("/user/queue/lobby", new DefaultStompFrameHandler());

    session.send("/app/lobby/" + gameId + "/edit-min-players",
        new ObjectMapper().writeValueAsBytes(minPlayers));

    await().atMost(1, SECONDS).untilAsserted(() -> {
      Mockito.verify(lobbyManagerService).editMinPlayers(gameId, minPlayers);
    });
  }

  @Test
  void testBrowseLobbies() throws Exception {
    List<Map<String, Object>> gameList = Arrays.asList(Map.ofEntries(
        Map.entry("gameName", "testGame1"),
        Map.entry("currentPlayers", Collections.singleton("testPlayer1"))), Map.ofEntries(
        Map.entry("gameName", "testGame2"),
        Map.entry("currentPlayers", Arrays.asList("testPlayer2", "testPlayer3"))));

    Mockito.when(lobbyManagerService.browseLobbies()).thenReturn(gameList);

    StompSession session = stompClient
        .connectAsync(WEBSOCKET_URI.replace("$PORT", Integer.toString(port)),
            new StompSessionHandlerAdapter() {
            })
        .get(1, SECONDS);
    session.subscribe("/user/queue/browse-lobbies", new DefaultStompFrameHandler());

    session.send("/app/browse-lobbies", new ObjectMapper().writeValueAsBytes(""));

    await().atMost(1, SECONDS).untilAsserted(() -> {
          Mockito.verify(lobbyManagerService).browseLobbies();
          assertThat(Objects.requireNonNull(blockingQueue.poll()).getBytes()).isEqualTo(
              new ObjectMapper().writeValueAsBytes(gameList));
        }
    );
  }

  @Test
  void testJoinLobby() throws Exception {
    String gameId = "123";

    StompSession session = stompClient
        .connectAsync(WEBSOCKET_URI.replace("$PORT", Integer.toString(port)),
            new StompSessionHandlerAdapter() {
            })
        .get(1, SECONDS);

    Mockito.doNothing().when(lobbyManagerService)
        .joinLobby(Mockito.eq(gameId), Mockito.anyString());

    session.subscribe("/topic/lobby/" + gameId, new DefaultStompFrameHandler());

    session.send("/app/join-lobby/" + gameId, new ObjectMapper().writeValueAsBytes(""));

    await().atMost(1, SECONDS).untilAsserted(() -> {
      Mockito.verify(lobbyManagerService)
          .joinLobby(Mockito.eq(gameId), Mockito.anyString());
    });
  }

  @Test
  void testLeaveLobby() throws Exception {
    String gameId = "123";

    StompSession session = stompClient
        .connectAsync(WEBSOCKET_URI.replace("$PORT", Integer.toString(port)),
            new StompSessionHandlerAdapter() {
            })
        .get(1, SECONDS);

    Mockito.doNothing().when(lobbyManagerService).leaveLobby(Mockito.anyString());

    session.subscribe("/topic/lobby/" + gameId, new DefaultStompFrameHandler());

    session.send("/app/leave-lobby/" + gameId, new ObjectMapper().writeValueAsBytes(""));

    await().atMost(1, SECONDS).untilAsserted(() -> {
      Mockito.verify(lobbyManagerService)
          .leaveLobby(Mockito.anyString());
    });
  }
}
