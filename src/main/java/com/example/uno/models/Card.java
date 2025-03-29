package com.example.uno.models;

public record Card(Color cardColor, Value cardValue) {

  @Override
  public String toString() {
    return String.format("%s-%s", cardColor, cardValue);
  }
}
