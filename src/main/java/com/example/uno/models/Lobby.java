package com.example.uno.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Lobby {

  private final String gameId;
  private int minPlayers;
  private final List<Player> currentPlayers;
  private Player host;
  private String gameName;

  public Lobby(String gameId, String gameName, int minPlayers) {
    this.gameId = gameId;
    this.gameName = gameName;
    this.minPlayers = minPlayers;

    this.currentPlayers = new ArrayList<>();
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
}
