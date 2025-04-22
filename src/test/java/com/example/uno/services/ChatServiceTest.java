package com.example.uno.services;

import com.example.uno.models.ConnectionData;
import com.example.uno.models.Lobby;
import com.example.uno.models.Message;
import com.example.uno.models.Player;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import repos.ChatRepo;
import repos.LobbyRepo;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

  @Mock
  LobbyRepo lobbyRepo;

  @Mock
  ChatRepo chatRepo;

  @Mock
  SimpMessagingTemplate simpMessagingTemplate;

  @InjectMocks
  ChatService chatService;

  @Test
  void testSendMessage() {

    String gameId = "g123";
    String playerSessionId = "p123";
    Player player = new Player(playerSessionId, "testPlayer", new ConnectionData("0.0.0.0", 1));
    Message message = new Message(player.getName(), "Hey there!");

    Lobby lobby = new Lobby(gameId, "testGame", 2);
    lobby.addPlayer(player);

    Mockito.when(lobbyRepo.get(gameId)).thenReturn(lobby);
    Mockito.when(chatRepo.get(playerSessionId)).thenReturn(List.of(message));

    chatService.sendMessage(gameId, playerSessionId, message);

    Mockito.verify(lobbyRepo).get(gameId);
    Mockito.verify(chatRepo).add(Mockito.eq(playerSessionId), Mockito.any(Message.class));

    ArgumentCaptor<MessageHeaders> messageHeadersArgumentCaptor = ArgumentCaptor.forClass(
        MessageHeaders.class);

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(Mockito.eq(playerSessionId),
            Mockito.eq("/queue/game/" + gameId + "/chat"),
            Mockito.eq(List.of(message)), messageHeadersArgumentCaptor.capture());
  }

  @Test
  void testSendMessage2Player() {
    String gameId = "g123";

    Player player1 = new Player("p1", "p1", new ConnectionData("0.0.0.0", 1));
    Player player2 = new Player("p2", "p2", new ConnectionData("0.0.0.0", 2));

    Message message1 = new Message(player1.getName(), "Hey there!");
    Message message2 = new Message(player2.getName(), "Hello!");

    Lobby lobby = new Lobby(gameId, "testGame", 2);
    lobby.addPlayer(player1);

    // Player 1 sends a message
    Mockito.when(lobbyRepo.get(gameId)).thenReturn(lobby);
    Mockito.when(chatRepo.get(player1.getSessionId())).thenReturn(List.of(message1));

    chatService.sendMessage(gameId, player1.getSessionId(), message1);

    Mockito.verify(lobbyRepo).get(gameId);
    Mockito.verify(chatRepo).add(player1.getSessionId(), message1);

    ArgumentCaptor<MessageHeaders> messageHeadersArgumentCaptor = ArgumentCaptor.forClass(
        MessageHeaders.class);

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(Mockito.eq(player1.getSessionId()),
            Mockito.eq("/queue/game/" + gameId + "/chat"),
            Mockito.eq(List.of(message1)), messageHeadersArgumentCaptor.capture());

    // Player 2 joins lobby and sends a message
    lobby.addPlayer(player2);

    Mockito.when(lobbyRepo.get(gameId)).thenReturn(lobby);
    Mockito.when(chatRepo.get(player1.getSessionId())).thenReturn(List.of(message1, message2));
    Mockito.when(chatRepo.get(player2.getSessionId())).thenReturn(List.of(message2));

    chatService.sendMessage(gameId, player2.getSessionId(), message2);

    Mockito.verify(chatRepo).add(player2.getSessionId(), message2);

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(Mockito.eq(player2.getSessionId()),
            Mockito.eq("/queue/game/" + gameId + "/chat"),
            Mockito.eq(List.of(message2)), messageHeadersArgumentCaptor.capture());

    Mockito.verify(simpMessagingTemplate)
        .convertAndSendToUser(Mockito.eq(player1.getSessionId()),
            Mockito.eq("/queue/game/" + gameId + "/chat"),
            Mockito.eq(List.of(message1, message2)), messageHeadersArgumentCaptor.capture());
  }
}
