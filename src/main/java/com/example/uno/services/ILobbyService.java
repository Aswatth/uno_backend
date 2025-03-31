package com.example.uno.services;

/**
 * An interface to represent in-lobby interactions.
 */
public interface ILobbyService {

  void setStatus(String playerSessionId, String gameId, boolean readyStatus);
}
