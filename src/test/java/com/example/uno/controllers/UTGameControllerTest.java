package com.example.uno.controllers;

import com.example.uno.models.Card;
import com.example.uno.models.Color;
import com.example.uno.models.Value;
import com.example.uno.services.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UTGameControllerTest {

  @Mock
  GameService gameService;

  @InjectMocks
  GameController gameController;

  @Test
  void testPlay() {
    String gameId = "g123";
    Card card = new Card(Color.RED, Value.ONE);

    Mockito.doNothing().when(gameService).play(gameId, card);

    gameController.play(gameId, card);

    Mockito.verify(gameService).play(gameId, card);
  }

  @Test
  void testDraw() {
    String gameId = "g123";

    Mockito.doNothing().when(gameService).drawCard(gameId);

    gameController.drawCard(gameId);

    Mockito.verify(gameService).drawCard(gameId);
  }

  @Test
  void testEndTurn() {
    String gameId = "g123";

    Mockito.doNothing().when(gameService).endTurn(gameId);

    gameController.endTurn(gameId);

    Mockito.verify(gameService).endTurn(gameId);
  }

  @Test
  void testReplay() {
    String gameId = "g123";

    Mockito.doNothing().when(gameService).replay(gameId);

    gameController.replay(gameId);

    Mockito.verify(gameService).replay(gameId);
  }
}
