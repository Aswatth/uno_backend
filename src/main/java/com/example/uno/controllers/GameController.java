package com.example.uno.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

  @MessageMapping("/ping")
  @SendTo("/topic/ping")
  public String ping(String data) {
    return "Received-" + data;
  }

}
