A websocket backend for the <a href="https://github.com/Aswatth/uno_frontend">UNO game.</a>

**Publishing channels:**
<table>
  <tr>
    <th>Topic name</th>
  <th>Descption</th>
  </tr>
  <tr>
    <td>/app/lobby</td>
    <td>Create a lobby with a given name and minimum players. On successful creation, a game ID would be generated and returned.</td>
  </tr>
  <tr>
    <td>/app/lobby/{gameId}/edit-min-players</td>
    <td>Edit minimum players in a lobby. <i>Note: New minimum player count cannot be lower than the current number of players.</i></td>
  </tr>
  <tr>
    <td>/app/browse-lobbies</td>
    <td>For browsing lobbies created by other players.</td>
  </tr>
  <tr>
    <td>/app/join-lobby/{gameId}</td>
    <td>Join a lobby.</td>
  </tr>
  <tr>
    <td>/app/leave-lobby/{gameId}</td>
    <td>Leave a joined lobby.</td>
  </tr>
  <tr>
    <td>/app/lobby/{gameId}/status</td>
    <td>Enables the player to set their ready status inside a lobby. <i>Host is ready by default.</i></td>
  </tr>
  <tr>
    <td>/app/lobby/{gameId}/start</td>
    <td>Allows the host to start a game once all players ready.</td>
  </tr>
  <tr>
    <td>/app/game/{gameId}/play</td>
    <td>Allows a player to play a card.</td>
  </tr>
  <tr>
    <td>/app/game/{gameId}/draw</td>
    <td>Allows a player to draw a card from the draw pile.</td>
  </tr>
  <tr>
    <td>/app/game/{gameId}/endTurn</td>
    <td>Enables a player to end their turn after drawing a card and they are not left with any valid cards to play.</td>
  </tr>
  <tr>
    <td>/app/game/{gameId}/replay</td>
    <td>Resets player status' (except host) and takes all the players back to the lobby for another game.</td>
  </tr>
  <tr>
    <td>/app//game/{gameId}/chat</td>
    <td>Allows players in a lobby to chat with each other while inside the lobby and also during the game.</td>
  </tr>
</table>

**Receiving channels:**
<table>
  <tr>
    <th>Topic name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>/user/queue/host</td>
    <td>Notfiy each player whether they are host of the current lobby.</td>
  </tr>
  <tr>
    <td>/user/queue/browse-lobbies</td>
    <td>General information about the lobby like Game name, Game Id and Players in lobby and Minimum players needed.</td>
  </tr>
  <tr>
    <td>/topic/lobby/{gameId}</td>
    <td>Detailed lobby information for players within a lobby. Along with general information, it also contains player status</td>
  </tr>
  <tr>
    <td>/user/queue/game/{gameId}</td>
    <td>Contains information about a game. Cards each player has, the top card, whose is the next player and whether a player is a winner or not.</td>
  </tr>
  <tr>
    <td>/user/queue/game/{gameId}/chat</td>
    <td>Chat messages for a lobby.</td>
  </tr>
</table>

The application was throughly tested with appropriate unit and integration tests using Jenkins.

![image](https://github.com/user-attachments/assets/14086fe6-a8df-46fe-85ba-1ab9ac0cd9ee)
