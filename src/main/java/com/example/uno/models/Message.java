package com.example.uno.models;

public record Message(String sender, String message) {

  @Override
  public String toString() {
    return String.format("[%s]: %s", sender, message);
  }
}
