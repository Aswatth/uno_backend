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
  private final Map<Player, Boolean> playerMap;
  private Player host;
  private String gameName;

  public Lobby(String gameId, String gameName, int minPlayers) {
    this.gameId = gameId;
    this.gameName = gameName;
    this.minPlayers = minPlayers;

    this.playerMap = new HashMap<>();
  }

  public String getGameId() {
    return gameId;
  }

  public int getMinPlayers() {
    return minPlayers;
  }

  public void setMinPlayers(int minPlayers) {
    this.minPlayers = minPlayers;
  }

  public List<Player> getCurrentPlayers() {
    return this.playerMap.keySet().stream().toList();
  }

  public void addPlayer(Player player) {
    if (this.playerMap.isEmpty()) {
      this.host = player;
      this.playerMap.put(player, true);
    } else {
      this.playerMap.put(player, false);
    }
  }

  public void setPlayerStatus(Player player, boolean isReady) {
    this.playerMap.computeIfPresent(player, (key, status) -> {
      if (player != host) {
        status = isReady;
      }
      return status;
    });
  }

  public boolean getPlayerStatus(Player player) {
    return this.playerMap.get(player);
  }

  public void removePlayer(Player player) {
    this.playerMap.remove(player);

    // Assign next player as host if host leaves the game.
    if (!this.playerMap.isEmpty() && Objects.equals(player.getSessionId(),
        host.getSessionId())) {
      Optional<Player> nextPlayer = this.playerMap.keySet().stream().findFirst();
      nextPlayer.ifPresent(value -> {
        setPlayerStatus(value, true);
        this.host = value;
      });
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
    map.put("minPlayers", minPlayers);

    List<Map<String, Object>> playerInfoList = new ArrayList<>();

    for (Map.Entry<Player, Boolean> entry : this.playerMap.entrySet()) {
      playerInfoList.add(
          Map.ofEntries(
              Map.entry("playerName", entry.getKey().getName()),
              Map.entry("status", entry.getValue())
          ));
    }

    map.put("currentPlayers", playerInfoList);

    return map;
  }
}
