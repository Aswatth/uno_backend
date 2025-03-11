package com.example.uno.services;

import com.example.uno.models.Game;
import java.util.List;

/**
 * An interface to specify list of initial functionalities for a game.
 */
public interface IGameService {

  String createGame(String gameName, int minPlayers);

  List<Game> browseGames();

  void joinGame(String gameId, String playerSessionId);
}
