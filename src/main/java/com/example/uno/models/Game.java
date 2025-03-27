package com.example.uno.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * A model defining game attributes.
 */
public class Game {

  private final String gameId;
  private int minPlayers;
  private final List<Player> currentPlayers;
  private Player host;
  private String gameName;

  private GameStatus gameStatus;
  private final List<Card> cardList;
  private final List<Card> discardPile;
  private List<Card> drawPile;
  private final Map<Player, List<Card>> playerCardList;
  private int currentPlayerIndex;
  private int direction = 1;
  private Player winner;

  public Game(String gameId, int minPlayers, String gameName) {
    this.gameId = gameId;
    this.minPlayers = minPlayers;
    this.currentPlayers = new ArrayList<>();
    this.gameName = gameName;

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

  public int getMinPlayers() {
    return minPlayers;
  }

  public void setMinPlayers(int minPlayers) {
    this.minPlayers = minPlayers;
  }

  public List<Player> getCurrentPlayers() {
    return currentPlayers;
  }

  public void addPlayer(Player player) {
    this.currentPlayers.add(player);
    if (currentPlayers.size() == 1) {
      this.host = player;
    }
  }

  public void removePlayer(Player player) {
    this.currentPlayers.remove(player);

    // Assign next player as host if host leaves the game.
    if (!this.currentPlayers.isEmpty() && Objects.equals(player.getSessionId(),
        host.getSessionId())) {
      Optional<Player> nextPlayer = this.currentPlayers.stream().findFirst();
      nextPlayer.ifPresent(value -> this.host = value);
    }
  }

  public String getGameName() {
    return gameName;
  }

  public void setGameName(String gameName) {
    this.gameName = gameName;
  }

  public Player getHost() {
    return this.host;
  }

  public boolean isHost(String playerSessionId) {
    return Objects.equals(host.getSessionId(), playerSessionId);
  }

  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();

    map.put("gameId", gameId);
    map.put("gameName", gameName);
    map.put("currentPlayers", currentPlayers.stream().map(Player::getName).toList());

    return map;
  }

  public Map<Player, List<Card>> dealCards() {
    Collections.shuffle(this.cardList);
    this.drawPile = new ArrayList<>(this.cardList);

    for (Player player : currentPlayers) {
      List<Card> cardList = new ArrayList<>();

      for (int i = 1; i <= 7; ++i) {
        Card card = this.drawPile.getFirst();
        cardList.add(card);
        this.drawPile.removeFirst();
      }

      this.playerCardList.put(player, cardList);
    }

    Random random = new Random();
    this.currentPlayerIndex = random.nextInt(this.currentPlayers.size());

    Card card = this.drawPile.getFirst();
    this.drawPile.removeFirst();

    while (card.getCardColor() != Color.WILD && card.getCardValue() != Value.DRAW4) {
      this.drawPile.add(card);

      card = this.drawPile.getFirst();
      this.drawPile.removeFirst();
    }

    switch (card.getCardValue()) {
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
    }

    this.drawPile.add(card);

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
    List<Card> cardList = playerCardList.get(getCurrentPlayer());

    if (card.getCardValue() == Value.WILD || card.getCardValue() == Value.DRAW4) {
      Optional<Card> cardToRemove = cardList.stream().filter(
              f -> f.getCardValue() == card.getCardValue() && f.getCardColor() == Color.WILD)
          .findFirst();

      cardToRemove.ifPresent(cardList::remove);

    } else {
      cardList.remove(card);
    }

    playerCardList.put(getCurrentPlayer(), cardList);

    this.discardPile.add(card);

    if (cardList.isEmpty()) {
      winner = getCurrentPlayer();
    }

    switch (card.getCardValue()) {
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

  @Override
  public String toString() {
    return "Game{" +
        "minPlayers=" + minPlayers +
        ", currentPlayers=" + currentPlayers +
        ", gameName='" + gameName + '\'' +
        '}';
  }

  public GameStatus getGameStatus() {
    return gameStatus;
  }

  public void setGameStatus(GameStatus gameStatus) {
    this.gameStatus = gameStatus;
  }
}
