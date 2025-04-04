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

@Service
public class GameService implements IGameService {

  private GameRepo gameRepo;

  private LobbyRepo lobbyRepo;

  @Autowired
  SimpMessagingTemplate simpMessagingTemplate;

  public GameService() {
    this.gameRepo = GameRepo.getInstance();
    this.lobbyRepo = LobbyRepo.getInstance();
  }

  private void broadcastToPlayers(String gameId, Game game) {
    Lobby lobby = lobbyRepo.get(gameId);
    List<Player> playerList = lobby.getCurrentPlayers();

    for (Player player : playerList) {
      // Set current player info
      Map<String, Object> payload = new HashMap<>();
      Player winner = game.getWinner();
      payload.put("isWinner", winner != null && winner.getSessionId().
          equals(player.getSessionId()));
      payload.put("isMyTurn", game.getCurrentPlayer().getSessionId().equals(player.getSessionId()));
      payload.put("cards", game.getCards(player));
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
          othersPlayerInfo.put("cardCount", game.getCards(otherPlayer).size());

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

  @Override
  public void play(String gameId, Card card) {
    Game game = gameRepo.get(gameId);

    game.play(card);

    gameRepo.add(gameId, game);

    broadcastToPlayers(gameId, game);
  }

  @Override
  public void drawCard(String gameId) {
    Game game = gameRepo.get(gameId);

    game.draw(1);

    gameRepo.add(gameId, game);

    broadcastToPlayers(gameId, game);
  }

  @Override
  public void endTurn(String gameId) {
    Game game = gameRepo.get(gameId);

    game.pass();

    gameRepo.add(gameId, game);

    broadcastToPlayers(gameId, game);
  }
}
