package javabot;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class Messages {
    List<Message> messages = new ArrayList<>();

    public List<Message> get() {
        List<Message> list = new ArrayList<>(messages);
        messages.clear();
        return list;
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

    public void add(final Message message) {
        messages.add(message);
    }
}
