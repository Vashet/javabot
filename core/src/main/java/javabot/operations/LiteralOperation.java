package javabot.operations;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.dao.FactoidDao;
import javabot.model.Factoid;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

import javax.inject.Inject;

@SPI(BotOperation.class)
public class LiteralOperation extends BotOperation {
    @Inject
    private FactoidDao dao;

    /**
     * @see BotOperation#handleMessage(MessageEvent)
     */
    @Override
    public final boolean handleMessage(final MessageEvent event) {
        final String message = event.getMessage().toLowerCase();
        final Channel channel = event.getChannel();
        if (message.startsWith("literal ")) {
            final String key = message.substring("literal ".length());
            final Factoid factoid = dao.getFactoid(key);
            if (factoid != null) {
                getBot().postMessage(channel, event.getUser(), factoid.getValue());
            } else {
                getBot().postMessage(channel, event.getUser(), Sofia.factoidUnknown(key));
            }
        }
        return false;
    }
}
