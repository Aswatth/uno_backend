package com.example.uno.repos;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.uno.models.Message;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repos.ChatRepo;

class ChatRepoTest {

  private ChatRepo chatRepo;

  @BeforeEach
  void setup() {
    chatRepo = ChatRepo.getInstance();
  }

  @Test
  void testAdd() {
    String gameId = "g123";
    Message m1 = new Message("p1", "Hi");
    Message m2 = new Message("p2", "Hi");

    chatRepo.add(gameId, m1);
    chatRepo.add(gameId, m2);

    assertThat(chatRepo.get(gameId)).isEqualTo(List.of(m1, m2));

    chatRepo.remove(gameId);
  }

  @Test
  void testGet() {
    String gameId = "g123";
    Message m1 = new Message("p1", "Hi");
    Message m2 = new Message("p2", "Hi");
    Message m3 = new Message("p3", "Hi");
    Message m4 = new Message("p4", "Hi");

    chatRepo.add(gameId, m1);
    chatRepo.add(gameId, m2);
    chatRepo.add(gameId, m3);
    chatRepo.add(gameId, m4);

    assertThat(chatRepo.get(gameId)).isEqualTo(List.of(m1, m2, m3, m4));

    chatRepo.remove(gameId);
  }

  @Test
  void testGetFail() {
    assertThat(chatRepo.get("1")).isNull();
  }

  @Test
  void testRemoveValid() {
    String gameId = "g123";
    Message m1 = new Message("p1", "Hi");
    Message m2 = new Message("p2", "Hi");

    chatRepo.add(gameId, m1);
    chatRepo.add(gameId, m2);

    chatRepo.remove(gameId);

    assertThat(chatRepo.get(gameId)).isNull();
  }

  @Test
  void testRemoveInvalid() {
    String gameId = "g123";
    Message m1 = new Message("p1", "Hi");
    Message m2 = new Message("p2", "Hi");

    chatRepo.add(gameId, m1);
    chatRepo.add(gameId, m2);

    assertThat(chatRepo.get(gameId)).isEqualTo(List.of(m1, m2));

    chatRepo.remove("2");

    assertThat(chatRepo.get(gameId)).isNotNull();
  }
}
