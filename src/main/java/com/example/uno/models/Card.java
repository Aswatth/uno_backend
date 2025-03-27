package com.example.uno.models;

public class Card {
  private final Color cardColor;
  private final Value cardValue;

  public Card(Color cardColor, Value cardValue) {
    this.cardColor = cardColor;
    this.cardValue = cardValue;
  }

  public Color getCardColor() {
    return cardColor;
  }

  public Value getCardValue() {
    return cardValue;
  }

  @Override
  public String toString() {
    return String.format("%s-%s", cardColor, cardValue);
  }
}
