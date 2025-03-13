package com.example.uno.services;

import com.example.uno.models.Game;
import java.util.List;
import java.util.Map;

/**
 * An interface to specify list of initial functionalities for a game.
 */
public interface IGameService {

  String createGame(String gameName, int minPlayers);

  List<Map<String,Object>> browseGames();

  void joinGame(String gameId, String playerSessionId);
  void leaveGame(String playerSessionId);
}
