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

    Player currentPlayer = game.getCurrentPlayer();
    Player nextPlayer = currentPlayer == testPlayer1 ? testPlayer2 : testPlayer1;

    if (game.getTopCard().cardValue() == Value.DRAW2) {
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
  void testPlayReverseCard() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));
    Player testPlayer3 = new Player("3", "testPlayer3", new ConnectionData("0.0.0.0", 3));

    List<Player> playerList = Arrays.asList(testPlayer1, testPlayer2, testPlayer3);

    Game game = new Game(playerList);

    game.dealCards();

    game.pass();
    game.pass();
    Player nextPlayer2 = game.getCurrentPlayer();
    game.pass();

    Card redReverseCard = new Card(Color.RED, Value.REVERSE);

    game.play(redReverseCard);

    assertThat(game.getTopCard()).isEqualTo(redReverseCard);
    assertThat(game.getCurrentPlayer()).isEqualTo(nextPlayer2);
  }

  @Test
  void testPlaySkipCard() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    List<Player> playerList = Arrays.asList(testPlayer1, testPlayer2);

    Game game = new Game(playerList);

    game.dealCards();

    Player currentPlayer = game.getCurrentPlayer();

    Card redSkipCard = new Card(Color.RED, Value.SKIP);

    game.play(redSkipCard);

    assertThat(game.getTopCard()).isEqualTo(redSkipCard);
    assertThat(game.getCurrentPlayer()).isEqualTo(currentPlayer);
  }

  @Test
  void testPlayDraw2Card() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    List<Player> playerList = Arrays.asList(testPlayer1, testPlayer2);

    Game game = new Game(playerList);

    game.dealCards();

    Card topCard = game.getTopCard();

    Player currentPlayer = game.getCurrentPlayer();
    Player nextPlayer = currentPlayer == testPlayer1 ? testPlayer2 : testPlayer1;

    Card redDraw2Card = new Card(Color.RED, Value.DRAW2);

    game.play(redDraw2Card);

    assertThat(game.getTopCard()).isEqualTo(redDraw2Card);
    assertThat(game.getCurrentPlayer()).isEqualTo(currentPlayer);

    if (topCard.cardValue() == Value.DRAW2) {
      assertThat(game.getCards(nextPlayer)).hasSize(11);
    } else {
      assertThat(game.getCards(nextPlayer)).hasSize(9);
    }
  }

  @Test
  void testPlayDraw4Card() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    List<Player> playerList = Arrays.asList(testPlayer1, testPlayer2);

    Game game = new Game(playerList);

    game.dealCards();

    Card topCard = game.getTopCard();

    Player currentPlayer = game.getCurrentPlayer();
    Player nextPlayer = currentPlayer == testPlayer1 ? testPlayer2 : testPlayer1;

    Card redDraw4Card = new Card(Color.RED, Value.DRAW4);

    game.play(redDraw4Card);

    assertThat(game.getTopCard()).isEqualTo(redDraw4Card);
    assertThat(game.getCurrentPlayer()).isEqualTo(currentPlayer);

    if (topCard.cardValue() == Value.DRAW2) {
      assertThat(game.getCards(nextPlayer)).hasSize(13);
    } else {
      assertThat(game.getCards(nextPlayer)).hasSize(11);
    }

  }

  @Test
  void testPlayWildCard() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    List<Player> playerList = Arrays.asList(testPlayer1, testPlayer2);

    Game game = new Game(playerList);

    game.dealCards();

    Card topCard = game.getTopCard();

    Player currentPlayer = game.getCurrentPlayer();
    Player nextPlayer = currentPlayer == testPlayer1 ? testPlayer2 : testPlayer1;

    Card wildRedCard = new Card(Color.RED, Value.WILD);
    game.play(wildRedCard);

    assertThat(game.getTopCard()).isEqualTo(wildRedCard);
    assertThat(game.getCurrentPlayer()).isEqualTo(nextPlayer);

    if (topCard.cardValue() == Value.DRAW2) {
      assertThat(game.getCards(nextPlayer)).hasSize(9);
    } else {
      assertThat(game.getCards(nextPlayer)).hasSize(7);
    }
  }

  @Test
  void testPlayCard() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));

    Game game = new Game(Arrays.asList(testPlayer1, testPlayer2));

    game.dealCards();

    Card topCard = game.getTopCard();

    Player currentPlayer = game.getCurrentPlayer();
    Card cardToPLay = game.getCards(currentPlayer).stream()
        .filter(f -> f.cardValue() != Value.DRAW2 && f.cardValue() != Value.DRAW4
            && f.cardValue() != Value.SKIP).findFirst()
        .get();

    Player nextPlayer = currentPlayer == testPlayer1 ? testPlayer2 : testPlayer1;

    game.play(cardToPLay);

    assertThat(game.getTopCard()).isEqualTo(cardToPLay);
    assertThat(game.getCurrentPlayer()).isEqualTo(nextPlayer);
    if (topCard.cardValue() == Value.DRAW2) {
      assertThat(game.getCards(nextPlayer)).hasSize(9);
    } else {
      assertThat(game.getCards(nextPlayer)).hasSize(7);
    }

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
