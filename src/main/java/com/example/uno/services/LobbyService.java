package com.example.uno.services;

import com.example.uno.models.Lobby;
import com.example.uno.models.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import repos.LobbyRepo;
import repos.PlayerRepo;

@Service
public class LobbyService implements ILobbyService {

  private PlayerRepo playerRepo;
  private LobbyRepo lobbyRepo;

  @Autowired
  SimpMessagingTemplate simpMessagingTemplate;

  public LobbyService() {
    this.playerRepo = PlayerRepo.getInstance();
    this.lobbyRepo = LobbyRepo.getInstance();
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
}
