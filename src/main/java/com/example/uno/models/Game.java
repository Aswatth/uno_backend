package com.example.uno.models;

import java.util.HashSet;
import java.util.Set;

/**
 * A model defining game attributes.
 */
public class Game {

  int minPlayers;
  Set<String> currentPlayers;
  String gameName;

  public Game(int minPlayers, String gameName) {
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

  public Set<String> getCurrentPlayers() {
    return currentPlayers;
  }

  public void addPlayer(String playerId) {
    this.currentPlayers.add(playerId);
  }

  public void removePlayer(String playerId) {
    this.currentPlayers.remove(playerId);
  }

  public String getGameName() {
    return gameName;
  }

  public void setGameName(String gameName) {
    this.gameName = gameName;
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
