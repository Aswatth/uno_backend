package com.example.uno.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A model defining game attributes.
 */
public class Game {

  private String gameId;
  private int minPlayers;
  private Set<Player> currentPlayers;
  private Player host;
  private String gameName;

  public Game(String gameId, int minPlayers, String gameName) {
    this.gameId = gameId;
    this.minPlayers = minPlayers;
    this.currentPlayers = new HashSet<>();
    this.gameName = gameName;
  }

  public int getMinPlayers() {
    return minPlayers;
  }

  public void setMinPlayers(int minPlayers) {
    this.minPlayers = minPlayers;
  }

  public Set<Player> getCurrentPlayers() {
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

  @Override
  public String toString() {
    return "Game{" +
        "minPlayers=" + minPlayers +
        ", currentPlayers=" + currentPlayers +
        ", gameName='" + gameName + '\'' +
        '}';
  }
}
