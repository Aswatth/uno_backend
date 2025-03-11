package com.example.uno.models;

/**
 * @param ipAddress IP address of connected client.
 * @param port      Port number of connected client.
 */
public record ConnectionData(String ipAddress, int port) {

  @Override
  public String toString() {
    return "ConnectionData{" +
        "ipAddress='" + ipAddress + '\'' +
        ", port=" + port +
        '}';
  }
}
