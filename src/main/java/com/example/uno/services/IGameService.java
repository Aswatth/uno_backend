package com.example.uno.services;

import com.example.uno.models.Card;

public interface IGameService {

  void play(String gameId, Card card);

  void drawCard(String gameId);

  void endTurn(String gameId);

  void replay(String gameId);

}
