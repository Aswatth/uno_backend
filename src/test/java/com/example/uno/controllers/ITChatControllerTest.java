package com.example.uno.controllers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.example.uno.models.Message;
import com.example.uno.services.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import java.util.Map;
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
class ITChatControllerTest {

  @LocalServerPort
  int port;

  static final String WEBSOCKET_URI = "ws://localhost:$PORT/uno";
  BlockingQueue<String> blockingQueue;
  WebSocketStompClient stompClient;

  @MockitoBean
  ChatService chatService;

  @InjectMocks
  ChatController chatController;

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
    assertThat(chatController).isNotNull();
  }

  @Test
  void testSendMessage() throws Exception {
    String gameId = "g123";
    Message message = new Message("abc", "hi there!");

    StompSession session = stompClient
        .connectAsync(WEBSOCKET_URI.replace("$PORT", Integer.toString(port)),
            new StompSessionHandlerAdapter() {
            })
        .get(1, SECONDS);

    session.subscribe("/topic/game/" + gameId + "/chat",
        new ITChatControllerTest.DefaultStompFrameHandler());

    session.send("/app/game/" + gameId + "/chat",
        new ObjectMapper().writeValueAsBytes(message));

    await().atMost(1, SECONDS).untilAsserted(() -> {
      Mockito.verify(chatService)
          .sendMessage(Mockito.eq(gameId), Mockito.anyString(), Mockito.eq(message));
    });
  }

}
