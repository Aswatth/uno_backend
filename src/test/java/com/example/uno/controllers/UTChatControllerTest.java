package com.example.uno.controllers;

import com.example.uno.models.ConnectionData;
import com.example.uno.models.Message;
import com.example.uno.models.Player;
import com.example.uno.services.ChatService;
import com.example.uno.services.IChatService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UTChatControllerTest {

  @Mock
  ChatService chatService;

  @InjectMocks
  ChatController chatController;

  @Test
  void testSendMessage() {
    String gameId = "g123";
    Player player = new Player("p123", "testPlayer", new ConnectionData("0.0.0.0", 1));
    Message message = new Message(player.getName(), "hi there");

    chatController.sendMessage(gameId, player.getSessionId(), message);

    Mockito.verify(chatService).sendMessage(gameId, player.getSessionId(), message);
  }

}
