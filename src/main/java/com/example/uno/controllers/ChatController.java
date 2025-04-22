package com.example.uno.controllers;

import com.example.uno.models.Message;
import com.example.uno.services.IChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

  @Autowired
  IChatService chatService;

  @MessageMapping("/game/{gameId}/chat")
  public void sendMessage(
      @DestinationVariable String gameId, @Header("simpSessionId") String playerSessionId
      , Message message) {
    chatService.sendMessage(gameId, playerSessionId, message);
  }

}
