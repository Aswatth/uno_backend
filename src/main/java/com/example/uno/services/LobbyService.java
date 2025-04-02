package com.example.uno.services;

import com.example.uno.models.Card;
import com.example.uno.models.Game;
import com.example.uno.models.Lobby;
import com.example.uno.models.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import repos.GameRepo;
import repos.LobbyRepo;
import repos.PlayerRepo;

@Service
public class LobbyService implements ILobbyService {

  private PlayerRepo playerRepo;
  private LobbyRepo lobbyRepo;
  private GameRepo gameRepo;

  @Autowired
  SimpMessagingTemplate simpMessagingTemplate;

  public LobbyService() {
    this.playerRepo = PlayerRepo.getInstance();
    this.lobbyRepo = LobbyRepo.getInstance();
    this.gameRepo = GameRepo.getInstance();
  }

  @Override
  public void setStatus(String playerSessionId, String gameId, boolean readyStatus) {
    Player player = playerRepo.get(playerSessionId);
    Lobby lobby = lobbyRepo.get(gameId);

    lobby.setPlayerStatus(player, readyStatus);

    lobbyRepo.add(lobby.getGameId(), lobby);

    simpMessagingTemplate.convertAndSend(
        "/topic/lobby/" + gameId, lobby.toMap());
  }

  @Override
  public void startGame(String gameId) {
    Lobby lobby = this.lobbyRepo.get(gameId);
    List<Player> playerList = lobby.getCurrentPlayers();
    Game game = new Game(playerList);

    this.gameRepo.add(lobby.getGameId(), game);

    Map<Player, List<Card>> playerCardMap = game.dealCards();

    for (Player player : playerList) {
      // Set current player info
      Map<String, Object> payload = new HashMap<>();
      Player winner = game.getWinner();
      payload.put("isWinner", winner != null && winner.getSessionId().
          equals(player.getSessionId()));
      payload.put("isMyTurn", game.getCurrentPlayer().getSessionId().equals(player.getSessionId()));
      payload.put("cards", playerCardMap.get(player));
      payload.put("topCard", game.getTopCard());

      // Setting other players info
      List<Map<String, Object>> otherPlayerInfoList = new ArrayList<>();
      for (Player otherPlayer : playerList) {
        Map<String, Object> othersPlayerInfo = new HashMap<>();
        if (!Objects.equals(player.getSessionId(), otherPlayer.getSessionId())) {
          othersPlayerInfo.put("playerName", otherPlayer.getName());
          Player isOtherPlayerWinner = game.getWinner();
          othersPlayerInfo.put("isWinner",
              isOtherPlayerWinner != null && isOtherPlayerWinner.getSessionId().
                  equals(otherPlayer.getSessionId()));
          othersPlayerInfo.put("isMyTurn",
              game.getCurrentPlayer().getSessionId().equals(otherPlayer.getSessionId()));
          othersPlayerInfo.put("cardCount", playerCardMap.get(otherPlayer).size());

          otherPlayerInfoList.add(othersPlayerInfo);
        }
      }
      payload.put("otherPlayersInfo", otherPlayerInfoList);

      // Sending the info to respective player
      SimpMessageHeaderAccessor simpMessageHeaderAccessor = SimpMessageHeaderAccessor.create(
          SimpMessageType.MESSAGE);
      simpMessageHeaderAccessor.setSessionId(player.getSessionId());

      simpMessagingTemplate.convertAndSendToUser(player.getSessionId(),
          "/queue/game/" + gameId, payload, simpMessageHeaderAccessor.getMessageHeaders());
    }

  }
}
