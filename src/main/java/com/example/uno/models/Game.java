package com.example.uno.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * A model defining game attributes.
 */
public class Game {

  private final List<Player> currentPlayers;
  private GameStatus gameStatus;
  private final List<Card> cardList;
  private final List<Card> discardPile;
  private List<Card> drawPile;
  private final Map<Player, List<Card>> playerCardList;
  private int currentPlayerIndex;
  private int direction = 1;
  private Player winner;

  public Game(List<Player> currentPlayers) {
    this.currentPlayers = currentPlayers;
    this.gameStatus = GameStatus.WAITING;
    this.cardList = new ArrayList<>();
    this.discardPile = new ArrayList<>();
    this.drawPile = new ArrayList<>();
    this.playerCardList = new HashMap<>();
    initializeCards();
  }

  private void initializeCards() {
    for (Value value : Arrays.stream(Value.values())
        .filter(f -> (f != Value.DRAW4 && f != Value.WILD)).toList()) {
      for (Color color : Arrays.stream(Color.values()).filter(f -> f != Color.WILD).toList()) {
        cardList.add(new Card(color, value));
        if (value != Value.ZERO) {
          cardList.add(new Card(color, value));
        }
      }
    }

    cardList.add(new Card(Color.WILD, Value.WILD));
    cardList.add(new Card(Color.WILD, Value.WILD));
    cardList.add(new Card(Color.WILD, Value.WILD));
    cardList.add(new Card(Color.WILD, Value.WILD));

    cardList.add(new Card(Color.WILD, Value.DRAW4));
    cardList.add(new Card(Color.WILD, Value.DRAW4));
    cardList.add(new Card(Color.WILD, Value.DRAW4));
    cardList.add(new Card(Color.WILD, Value.DRAW4));

    Collections.shuffle(cardList);
    this.drawPile = new ArrayList<>(cardList);
  }

  public Map<Player, List<Card>> dealCards() {
    Collections.shuffle(this.cardList);
    this.drawPile = new ArrayList<>(this.cardList);

    for (Player player : currentPlayers) {
      List<Card> cardsToDeal = new ArrayList<>();

      for (int i = 1; i <= 7; ++i) {
        Card card = this.drawPile.getFirst();
        cardsToDeal.add(card);
        this.drawPile.removeFirst();
      }

      this.playerCardList.put(player, cardsToDeal);
    }

    Random random = new Random();
    this.currentPlayerIndex = random.nextInt(this.currentPlayers.size());

    Card card = this.drawPile.getFirst();
    this.drawPile.removeFirst();

    while (card.cardColor() == Color.WILD && card.cardValue() == Value.DRAW4) {
      this.drawPile.add(card);

      card = this.drawPile.getFirst();
      this.drawPile.removeFirst();
    }

    switch (card.cardValue()) {
      case REVERSE:
        direction *= -1;
        break;
      case SKIP:
        pass();
        break;
      case DRAW2:
        pass();
        draw(2);
        break;
      default:
        break;
    }

    this.discardPile.add(card);

    return playerCardList;
  }

  public List<Card> getCards(Player player) {
    return this.playerCardList.get(player);
  }

  public void pass() {
    int nextIndex = this.currentPlayerIndex + direction;

    if (nextIndex > this.currentPlayers.size() - 1) {
      nextIndex = 0;
    } else if (nextIndex < 0) {
      nextIndex = this.currentPlayers.size() - 1;
    }

    this.currentPlayerIndex = nextIndex;
  }

  public void draw(int drawCount) {
    Player player = this.currentPlayers.get(this.currentPlayerIndex);
    List<Card> cards = playerCardList.get(player);

    for (int i = 1; i <= drawCount; ++i) {
      if (this.drawPile.isEmpty()) {
        this.drawPile = new ArrayList<>(this.discardPile);
        this.discardPile.clear();

        Collections.shuffle(drawPile);
      }

      Card card = this.drawPile.getFirst();
      this.drawPile.removeFirst();

      cards.add(card);
    }

    this.playerCardList.put(player, cards);
  }

  public Player getCurrentPlayer() {
    return this.currentPlayers.get(this.currentPlayerIndex);
  }

  public Card getTopCard() {
    return this.discardPile.getLast();
  }

  public void play(Card card) {
    List<Card> playerCards = playerCardList.get(getCurrentPlayer());

    if (card.cardValue() == Value.WILD || card.cardValue() == Value.DRAW4) {
      Optional<Card> cardToRemove = playerCards.stream().filter(
              f -> f.cardValue() == card.cardValue() && f.cardColor() == Color.WILD)
          .findFirst();

      cardToRemove.ifPresent(playerCards::remove);

    } else {
      playerCards.remove(card);
    }

    playerCardList.put(getCurrentPlayer(), playerCards);

    this.discardPile.add(card);

    if (playerCards.isEmpty()) {
      winner = getCurrentPlayer();
    }

    switch (card.cardValue()) {
      case SKIP:
        pass();
        pass();
        break;
      case REVERSE:
        direction *= -1;
        pass();
        break;
      case DRAW2:
        pass();
        draw(2);
        pass();
        break;
      case DRAW4:
        pass();
        draw(4);
        pass();
        break;
      default:
        pass();
    }
  }

  public boolean isGameOver() {
    return winner != null;
  }

  public Player getWinner() {
    return winner;
  }

  public GameStatus getGameStatus() {
    return gameStatus;
  }

  public void setGameStatus(GameStatus gameStatus) {
    this.gameStatus = gameStatus;
  }
}
