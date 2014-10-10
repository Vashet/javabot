package javabot.operations;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import com.antwerkz.maven.SPI;
import javabot.IrcEvent;
import javabot.Message;
import javabot.dao.FactoidDao;
import javabot.model.Factoid;

/**
 * @author ricky_clarkson
 */
@SPI(BotOperation.class)
public class LiteralOperation extends BotOperation {
  @Inject
  private FactoidDao dao;

  /**
   * @see BotOperation#handleMessage(IrcEvent)
   */
  @Override
  public final boolean handleMessage(final MessageEvent event) {
    final String message = event.getMessage().toLowerCase();
    final String channel = event.getChannel();
    final List<Message> responses = new ArrayList<Message>();
    if (message.startsWith("literal ")) {
      final String key = message.substring("literal ".length());
      final Factoid factoid = dao.getFactoid(key);
      if (factoid != null) {
        responses.add(new Message(channel, event, factoid.getValue()));
      } else {
        responses.add(new Message(channel, event, "I have no factoid called \"" + key + "\""));
      }
    }
    return responses;
  }
}
