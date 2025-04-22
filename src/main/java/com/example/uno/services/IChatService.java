package com.example.uno.services;

import com.example.uno.models.Message;

public interface IChatService {

  void sendMessage(String gameId, String playerSessionId, Message message);

}
