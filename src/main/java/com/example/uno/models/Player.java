package com.example.uno.models;

/**
 * A model defining player attributes.
 */
public class Player {
  private String sessionId;
  private String name;
  private ConnectionData connectionData;

  public Player(String sessionId, String name, ConnectionData connectionData) {
    this.sessionId = sessionId;
    this.name = name;
    this.connectionData = connectionData;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ConnectionData getConnectionData() {
    return connectionData;
  }

  public void setConnectionData(ConnectionData connectionData) {
    this.connectionData = connectionData;
  }

  @Override
  public String toString() {
    return "Player{" +
        "sessionId='" + sessionId + '\'' +
        ", name='" + name + '\'' +
        ", connectionData=" + connectionData +
        '}';
  }
}
