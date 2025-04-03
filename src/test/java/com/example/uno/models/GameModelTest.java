package com.example.uno.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

class GameModelTest {

  @Test
  void testGenerateCardsForTwoPlayers() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    List<Player> playerList = Arrays.asList(testPlayer1, testPlayer2);

    Game game = new Game(playerList);

    Map<Player, List<Card>> playerCardList = game.dealCards();

    Card topCard = game.getTopCard();
    Player currentPlayer = game.getCurrentPlayer();
    Player nextPlayer = currentPlayer == testPlayer1 ? testPlayer2 : testPlayer1;

    if (topCard.cardValue() == Value.DRAW2) {
      assertThat(playerCardList.get(nextPlayer)).hasSize(9);
      assertThat(playerCardList.get(currentPlayer)).hasSize(7);
    } else {
      assertThat(playerCardList.get(nextPlayer)).hasSize(7);
      assertThat(playerCardList.get(currentPlayer)).hasSize(7);
    }
  }

  @Test
  void testPass() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    List<Player> playerList = Arrays.asList(testPlayer1, testPlayer2);

    Game game = new Game(playerList);

    game.dealCards();

    Player expectedNextPlayer = game.getCurrentPlayer() == testPlayer1 ? testPlayer2 : testPlayer1;

    game.pass();

    assertThat(game.getCurrentPlayer()).isEqualTo(expectedNextPlayer);
  }

  @Test
  void testGetPlayerCards() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    List<Player> playerList = Arrays.asList(testPlayer1, testPlayer2);

    Game game = new Game(playerList);

    game.dealCards();

    Player expectedNextPlayer = game.getCurrentPlayer();

    game.draw(2);

    assertThat(game.getCards(expectedNextPlayer)).hasSize(9);
  }

  @Test
  void testPlaySkipCard() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    List<Player> playerList = Arrays.asList(testPlayer1, testPlayer2);

    Game game = new Game(playerList);

    game.dealCards();

    Player currentPlayer = game.getCurrentPlayer();

    game.play(new Card(Color.RED, Value.SKIP));

    assertThat(game.getCurrentPlayer()).isEqualTo(currentPlayer);
  }

  @Test
  void testPlayDraw2Card() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    List<Player> playerList = Arrays.asList(testPlayer1, testPlayer2);

    Game game = new Game(playerList);

    game.dealCards();

    Player currentPlayer = game.getCurrentPlayer();
    Player nextPlayer = currentPlayer == testPlayer1 ? testPlayer2 : testPlayer1;

    game.play(new Card(Color.RED, Value.DRAW2));

    assertThat(game.getCurrentPlayer()).isEqualTo(currentPlayer);
    assertThat(game.getCards(nextPlayer)).hasSize(9);
  }

  @Test
  void testPlayDraw4Card() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    List<Player> playerList = Arrays.asList(testPlayer1, testPlayer2);

    Game game = new Game(playerList);

    game.dealCards();

    Player currentPlayer = game.getCurrentPlayer();
    Player nextPlayer = currentPlayer == testPlayer1 ? testPlayer2 : testPlayer1;

    game.play(new Card(Color.RED, Value.DRAW4));

    assertThat(game.getCurrentPlayer()).isEqualTo(currentPlayer);
    assertThat(game.getCards(nextPlayer)).hasSize(11);
  }

  @Test
  void testPlayWildCard() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    List<Player> playerList = Arrays.asList(testPlayer1, testPlayer2);

    Game game = new Game(playerList);

    game.dealCards();

    Player currentPlayer = game.getCurrentPlayer();
    Player nextPlayer = currentPlayer == testPlayer1 ? testPlayer2 : testPlayer1;

    game.play(new Card(Color.RED, Value.WILD));

    assertThat(game.getCurrentPlayer()).isEqualTo(nextPlayer);
    assertThat(game.getCards(nextPlayer)).hasSize(7);
  }

  @Test
  void testPlayCard() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    List<Player> playerList = Arrays.asList(testPlayer1, testPlayer2);

    Game game = new Game(playerList);

    game.dealCards();

    Player currentPlayer = game.getCurrentPlayer();
    Card cardToPLay = game.getCards(currentPlayer).stream()
        .filter(f -> f.cardValue() != Value.DRAW2 && f.cardValue() != Value.DRAW4).findFirst()
        .get();

    Player nextPlayer = currentPlayer == testPlayer1 ? testPlayer2 : testPlayer1;

    game.play(cardToPLay);

    assertThat(game.getCurrentPlayer()).isEqualTo(nextPlayer);
    assertThat(game.getCards(nextPlayer)).hasSize(7);
    assertThat(game.getCards(currentPlayer)).hasSize(6);
  }

  @Test
  void testGameOver() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    Player winner = null;
    List<Player> playerList = Arrays.asList(testPlayer1, testPlayer2);

    Game game = new Game(playerList);

    game.dealCards();

    while (!game.isGameOver()) {
      Player currentPlayer = game.getCurrentPlayer();
      List<Card> cardList = game.getCards(currentPlayer);
      if (cardList.size() == 1) {
        winner = currentPlayer;
      }

      Random random = new Random();
      int index = random.nextInt(cardList.size());

      game.play(cardList.get(index));
    }

    assertThat(game.isGameOver()).isTrue();
    assertThat(game.getWinner()).isEqualTo(winner);
  }
}
