package javabot.operations;

import java.util.ArrayList;
import java.util.List;

import com.antwerkz.maven.SPI;
import javabot.IrcEvent;
import javabot.Message;
import javabot.dao.KarmaDao;
import javabot.model.Karma;
import org.schwering.irc.lib.IRCUser;
import org.springframework.beans.factory.annotation.Autowired;

@SPI(BotOperation.class)
public class KarmaReadOperation extends BotOperation {
    @Autowired
    private KarmaDao karmaDao;

    @Override
    public List<Message> handleMessage(final IrcEvent event) {
        final String message = event.getMessage();
        final String channel = event.getChannel();
        final IRCUser sender = event.getSender();
        final List<Message> response = new ArrayList<Message>();
        if (message.startsWith("karma ")) {
            final String nick = message.substring("karma ".length()).toLowerCase();
            final Karma karma = karmaDao.find(nick);
            if (karma != null) {
                if (nick.equalsIgnoreCase(sender.getNick())) {
                    response.add(new Message(channel, event,
                        sender + ", you have a karma level of " + karma.getValue()));
                } else {
                    response.add(new Message(channel, event,
                        nick + " has a karma level of " + karma.getValue() + ", " + sender));
                }
            } else {
                response.add(new Message(channel, event, nick + " has no karma, " + sender));
            }
        }
        return response;
    }
}