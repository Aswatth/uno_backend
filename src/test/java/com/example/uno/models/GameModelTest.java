package com.example.uno.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameModelTest {

  private final String gameName = "testGame";
  private final int minPlayers = 2;
  private final String gameId = "123";
  private Game game;

  @BeforeEach
  public void setup() {
    game = new Game(gameId, minPlayers, gameName);
  }

  @Test
  public void testGameCreation() {
    assertThat(game).isNotNull();
  }

  @Test
  public void testToMap() {
    Map<String, Object> map = game.toMap();
    assertThat(map.get("gameId")).isEqualTo(gameId);
    assertThat(map.get("gameName")).isEqualTo(gameName);
  }

  @Test
  public void testGetMinPlayers() {
    assertThat(game.getMinPlayers()).isEqualTo(2);
  }

  @Test
  public void testGetGameName() {
    assertThat(game.getGameName()).isEqualTo(gameName);
  }

  @Test
  public void testGetPlayer() {
    Player testPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 1234));
    game.addPlayer(testPlayer);

    assertThat(game.getCurrentPlayers().size()).isEqualTo(1);
    assertThat(game.getCurrentPlayers().stream().findFirst().get()).isEqualTo(testPlayer);
  }

  @Test
  public void testGetPlayerAfterRemoving() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    game.removePlayer(testPlayer1);

    assertThat(game.getCurrentPlayers().size()).isEqualTo(1);
  }

  @Test
  public void testGetPlayerTwoPlayers() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    assertThat(game.getCurrentPlayers().size()).isEqualTo(2);
  }

  @Test
  public void testGetHost() {
    Player testPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 1234));
    game.addPlayer(testPlayer);

    assertThat(game.getHost()).isEqualTo(testPlayer);
  }

  @Test
  public void testIsHostTrue() {
    Player testPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 1234));
    game.addPlayer(testPlayer);

    assertThat(game.isHost(testPlayer.getSessionId())).isTrue();
  }

  @Test
  public void testIsHostFalse() {
    Player testPlayer = new Player("123", "testPlayer", new ConnectionData("0.0.0.0", 1234));
    game.addPlayer(testPlayer);

    assertThat(game.isHost("1")).isFalse();
  }


  @Test
  public void testGetHostTwoPlayers() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    assertThat(game.getHost()).isEqualTo(testPlayer1);
    assertThat(game.getHost()).isNotEqualTo(testPlayer2);
  }

  @Test
  public void testGetHostAfterRemovingAPlayer() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    game.removePlayer(testPlayer1);

    assertThat(game.getHost()).isEqualTo(testPlayer2);
  }


  @Test
  public void testIsHostTwoPlayers() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    assertThat(game.isHost(testPlayer1.getSessionId())).isTrue();
    assertThat(game.isHost(testPlayer2.getSessionId())).isFalse();
  }

  @Test
  public void testIsHostAfterRemovingAPlayer() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    game.removePlayer(testPlayer1);

    assertThat(game.isHost(testPlayer2.getSessionId())).isTrue();
  }

  @Test
  public void testSetMinPlayers() {
    int newMinPlayers = 4;
    assertThat(game.getMinPlayers()).isEqualTo(minPlayers);

    game.setMinPlayers(newMinPlayers);

    assertThat(game.getMinPlayers()).isEqualTo(newMinPlayers);
  }

  @Test
  public void testSetGameName() {
    String newGameName = "anotherTestName";
    assertThat(game.getGameName()).isEqualTo(gameName);

    game.setGameName(newGameName);

    assertThat(game.getGameName()).isEqualTo(newGameName);
  }

  @Test
  public void testGenerateCardsForTwoPlayers() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    Map<Player, List<Card>> playerCardList = game.dealCards();

    for (Map.Entry<Player, List<Card>> map : playerCardList.entrySet()) {
      assertThat(map.getValue().size()).isEqualTo(7);
    }
  }

  @Test
  public void testGenerateCardsForTenPlayers() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer2", new ConnectionData("0.0.0.0", 2));
    Player testPlayer3 = new Player("3", "testPlayer3", new ConnectionData("0.0.0.0", 3));
    Player testPlayer4 = new Player("4", "testPlayer4", new ConnectionData("0.0.0.0", 4));
    Player testPlayer5 = new Player("5", "testPlayer5", new ConnectionData("0.0.0.0", 5));
    Player testPlayer6 = new Player("6", "testPlayer6", new ConnectionData("0.0.0.0", 6));
    Player testPlayer7 = new Player("7", "testPlayer7", new ConnectionData("0.0.0.0", 7));
    Player testPlayer8 = new Player("8", "testPlayer8", new ConnectionData("0.0.0.0", 8));
    Player testPlayer9 = new Player("9", "testPlayer9", new ConnectionData("0.0.0.0", 9));
    Player testPlayer10 = new Player("10", "testPlayer10", new ConnectionData("0.0.0.0", 10));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);
    game.addPlayer(testPlayer3);
    game.addPlayer(testPlayer4);
    game.addPlayer(testPlayer5);
    game.addPlayer(testPlayer6);
    game.addPlayer(testPlayer7);
    game.addPlayer(testPlayer8);
    game.addPlayer(testPlayer9);
    game.addPlayer(testPlayer10);

    Map<Player, List<Card>> playerCardList = game.dealCards();

    for (Map.Entry<Player, List<Card>> map : playerCardList.entrySet()) {
      assertThat(map.getValue().size()).isEqualTo(7);
    }
  }

  @Test
  public void testPass() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    game.dealCards();

    Player expectedNextPlayer = game.getCurrentPlayer() == testPlayer1 ? testPlayer2 : testPlayer1;

    game.pass();

    assertThat(game.getCurrentPlayer()).isEqualTo(expectedNextPlayer);
  }

  @Test
  public void testGetPlayerCards() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    game.dealCards();

    Player expectedNextPlayer = game.getCurrentPlayer();

    game.draw(2);

    assertThat(game.getCards(expectedNextPlayer).size()).isEqualTo(9);
  }

  @Test
  public void testPlaySkipCard() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    game.dealCards();

    Player currentPlayer = game.getCurrentPlayer();

    game.play(new Card(Color.RED, Value.SKIP));

    assertThat(game.getCurrentPlayer()).isEqualTo(currentPlayer);
  }

  @Test
  public void testPlayDraw2Card() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    game.dealCards();

    Player currentPlayer = game.getCurrentPlayer();
    Player nextPlayer = currentPlayer == testPlayer1 ? testPlayer2 : testPlayer1;

    game.play(new Card(Color.RED, Value.DRAW2));

    assertThat(game.getCurrentPlayer()).isEqualTo(currentPlayer);
    assertThat(game.getCards(nextPlayer).size()).isEqualTo(9);
  }

  @Test
  public void testPlayDraw4Card() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    game.dealCards();

    Player currentPlayer = game.getCurrentPlayer();
    Player nextPlayer = currentPlayer == testPlayer1 ? testPlayer2 : testPlayer1;

    game.play(new Card(Color.RED, Value.DRAW4));

    assertThat(game.getCurrentPlayer()).isEqualTo(currentPlayer);
    assertThat(game.getCards(nextPlayer).size()).isEqualTo(11);
  }

  @Test
  public void testPlayWildCard() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    game.dealCards();

    Player currentPlayer = game.getCurrentPlayer();
    Player nextPlayer = currentPlayer == testPlayer1 ? testPlayer2 : testPlayer1;

    game.play(new Card(Color.RED, Value.WILD));

    assertThat(game.getCurrentPlayer()).isEqualTo(nextPlayer);
    assertThat(game.getCards(nextPlayer).size()).isEqualTo(7);
  }

  @Test
  public void testPlayCard() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

    game.dealCards();

    Player currentPlayer = game.getCurrentPlayer();
    Card cardToPLay = game.getCards(currentPlayer).stream()
        .filter(f -> f.getCardValue() != Value.DRAW2 && f.getCardValue() != Value.DRAW4).findFirst()
        .get();

    Player nextPlayer = currentPlayer == testPlayer1 ? testPlayer2 : testPlayer1;

    game.play(cardToPLay);

    assertThat(game.getCurrentPlayer()).isEqualTo(nextPlayer);
    assertThat(game.getCards(nextPlayer).size()).isEqualTo(7);
    assertThat(game.getCards(currentPlayer).size()).isEqualTo(6);
  }

  @Test
  public void testGameOver() {
    Player testPlayer1 = new Player("1", "testPlayer1", new ConnectionData("0.0.0.0", 1));
    Player testPlayer2 = new Player("2", "testPlayer1", new ConnectionData("0.0.0.0", 2));

    Player winner = null;
    game.addPlayer(testPlayer1);
    game.addPlayer(testPlayer2);

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
