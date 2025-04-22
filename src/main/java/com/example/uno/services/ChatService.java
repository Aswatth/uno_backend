package com.example.uno.services;

import com.example.uno.models.Lobby;
import com.example.uno.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import repos.ChatRepo;
import repos.LobbyRepo;

@Service
public class ChatService implements IChatService {

  @Autowired
  SimpMessagingTemplate simpMessagingTemplate;

  private LobbyRepo lobbyRepo;
  private ChatRepo chatRepo;

  public ChatService() {
    this.lobbyRepo = LobbyRepo.getInstance();
    this.chatRepo = ChatRepo.getInstance();
  }

  @Override
  public void sendMessage(String gameId, String playerSessionId, Message message) {
    Lobby lobby = lobbyRepo.get(gameId);

    if (lobby != null) {
      lobby.getCurrentPlayers().forEach(player -> {
        chatRepo.add(player.getSessionId(), message);

        SimpMessageHeaderAccessor simpMessageHeaderAccessor = SimpMessageHeaderAccessor.create(
            SimpMessageType.MESSAGE);
        simpMessageHeaderAccessor.setSessionId(player.getSessionId());

        simpMessagingTemplate.convertAndSendToUser(player.getSessionId(),
            "/queue/game/" + gameId + "/chat", chatRepo.get(player.getSessionId()),
            simpMessageHeaderAccessor.getMessageHeaders());
      });
    }
  }
}
