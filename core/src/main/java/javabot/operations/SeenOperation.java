package javabot.operations;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.Message;
import javabot.Seen;
import javabot.dao.LogsDao;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import javax.inject.Inject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

@SPI(BotOperation.class)
public class SeenOperation extends BotOperation {
    @Inject
    private LogsDao dao;

    @Override
    public final boolean handleMessage(final MessageEvent event) {
        final String message = event.getMessage();
        final Channel channel = event.getChannel();
        if ("seen ".equalsIgnoreCase(message.substring(0, Math.min(message.length(), 5)))) {
            final String key = message.substring("seen ".length());
            if (dao.isSeen(key, channel.getName())) {
                Seen seen = dao.getSeen(key, channel.getName());
                getBot().postMessage(channel, event.getUser(),
                                     Sofia.seenLast(event.getUser().getNick(), key, DateFormat.getInstance().format(seen.getUpdated()),
                                                    seen.getMessage()));
            } else {
                getBot().postMessage(channel, event.getUser(), Sofia.seenUnknown(event.getUser().getNick(), key));
            }
            return true;
        }
        return false;
    }
}