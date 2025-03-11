package com.example.uno.services;

import com.example.uno.models.ConnectionData;

/**
 * An interface to specify list of initial functionalities for a player.
 */
public interface IPlayerService {

  void addPlayer(String sessionId, String playerName, ConnectionData connectionData);

  void removePlayer(String sessionId);
}
