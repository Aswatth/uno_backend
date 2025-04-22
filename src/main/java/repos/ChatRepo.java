package repos;

import com.example.uno.models.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRepo {

  private static ChatRepo instance;
  private final Map<String, List<Message>> chatMap;

  private ChatRepo() {
    this.chatMap = new ConcurrentHashMap<>();
  }

  public static ChatRepo getInstance() {
    if (instance == null) {
      instance = new ChatRepo();
    }
    return instance;
  }

  public void add(String playerSessionId, Message message) {
    List<Message> messageList = this.get(playerSessionId);
    if (messageList == null) {
      messageList = new ArrayList<>();
    }
    messageList.add(message);
    this.chatMap.put(playerSessionId, messageList);
  }

  public List<Message> get(String playerSessionId) {
    return this.chatMap.get(playerSessionId);
  }

  public void remove(String playerSessionId) {
    this.chatMap.remove(playerSessionId);
  }
}
