package com.example.uno.controllers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.example.uno.services.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class ITGameControllerTest {

  @LocalServerPort
  int port;

  static final String WEBSOCKET_URI = "ws://localhost:$PORT/uno";
  BlockingQueue<String> blockingQueue;
  WebSocketStompClient stompClient;

  @MockitoBean
  GameService gameService;

  @InjectMocks
  GameController gameController;

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

  @Test
  void contextLoad() {
    assertThat(gameController).isNotNull();
  }

  @Test
  void testCreateGame() throws Exception {

    String gameName = "testGame";
    int minPlayers = 2;
    String gameId = "123";

    Map<String, Object> payload = Map.ofEntries(Map.entry("gameName", gameName),
        Map.entry("minPlayers", minPlayers));

    Mockito.when(gameService.createGame(gameName, minPlayers)).thenReturn(gameId);

    StompSession session = stompClient
        .connectAsync(WEBSOCKET_URI.replace("$PORT", Integer.toString(port)),
            new StompSessionHandlerAdapter() {
            })
        .get(1, SECONDS);
    session.subscribe("/user/queue/game", new DefaultStompFrameHandler());

    session.send("/app/game", new ObjectMapper().writeValueAsBytes(payload));

    Mockito.verify(gameService).createGame(gameName, minPlayers);

    await().atMost(1, SECONDS).untilAsserted(() ->
        assertThat(blockingQueue.poll()).isEqualTo(gameId)
    );
  }

  @Test
  void testBrowseGames() throws Exception {
    List<Map<String, Object>> gameList = Arrays.asList(Map.ofEntries(
        Map.entry("gameName", "testGame1"),
        Map.entry("currentPlayers", Collections.singleton("testPlayer1"))), Map.ofEntries(
        Map.entry("gameName", "testGame2"),
        Map.entry("currentPlayers", Arrays.asList("testPlayer2", "testPlayer3"))));

    Mockito.when(gameService.browseGames()).thenReturn(gameList);

    StompSession session = stompClient
        .connectAsync(WEBSOCKET_URI.replace("$PORT", Integer.toString(port)),
            new StompSessionHandlerAdapter() {
            })
        .get(1, SECONDS);
    session.subscribe("/user/queue/browse-games", new DefaultStompFrameHandler());

    session.send("/app/browse-games", new ObjectMapper().writeValueAsBytes(""));

    Mockito.verify(gameService).browseGames();

    await().atMost(1, SECONDS).untilAsserted(() ->
        assertThat(Objects.requireNonNull(blockingQueue.poll()).getBytes()).isEqualTo(
            new ObjectMapper().writeValueAsBytes(gameList))
    );
  }

  @Test
  void testJoinGame() throws Exception {
    String gameId = "123";

    StompSession session = stompClient
        .connectAsync(WEBSOCKET_URI.replace("$PORT", Integer.toString(port)),
            new StompSessionHandlerAdapter() {
            })
        .get(1, SECONDS);

    Mockito.doNothing().when(gameService).joinGame(gameId, Mockito.anyString());

    session.subscribe("/topic/join-game/123", new DefaultStompFrameHandler());

    session.send("/app/join-game/123", new ObjectMapper().writeValueAsBytes(""));

    Mockito.verify(gameService, Mockito.timeout(1000))
        .joinGame(Mockito.eq(gameId), Mockito.anyString());
  }

  @Test
  void testStartGame() throws Exception {
    String gameId = "123";

    Mockito.doNothing().when(gameService).startGame(gameId);

    StompSession session = stompClient
        .connectAsync(WEBSOCKET_URI.replace("$PORT", Integer.toString(port)),
            new StompSessionHandlerAdapter() {
            })
        .get(1, SECONDS);

    session.subscribe("/user/queue/game/" + gameId, new DefaultStompFrameHandler());

    session.send("/app/game/" + gameId, new ObjectMapper().writeValueAsBytes(""));

    Mockito.verify(gameService, Mockito.timeout(1000)).startGame(gameId);
  }
}
