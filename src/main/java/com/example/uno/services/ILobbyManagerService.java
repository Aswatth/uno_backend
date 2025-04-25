package com.example.uno.services;

import java.util.List;
import java.util.Map;

/**
 * An interface to specify list of initial functionalities for a game.
 */
public interface ILobbyManagerService {

  String createLobby(String gameName, int minPlayers);

  void editMinPlayers(String gameId, int minPlayers);

  List<Map<String, Object>> browseLobbies();

  void joinLobby(String gameId, String playerSessionId);

  void leaveLobby(String playerSessionId);
}
