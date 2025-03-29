package com.example.uno.services;

/**
 * An interface to represent in-lobby interactions.
 */
public interface ILobbyService {

  void setReadyStatus(boolean readyStatus);

  void chat(String message);

  void startGame();
}
