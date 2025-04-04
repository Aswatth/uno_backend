package com.example.uno.controllers;

import com.example.uno.models.Card;
import com.example.uno.services.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

  @Autowired
  IGameService gameService;

  @MessageMapping("/game/{gameId}/play")
  public void play(@DestinationVariable String gameId, Card card) {
    gameService.play(gameId, card);
  }

  @MessageMapping("/game/{gameId}/draw")
  public void drawCard(@DestinationVariable String gameId) {
    gameService.drawCard(gameId);
  }

}
