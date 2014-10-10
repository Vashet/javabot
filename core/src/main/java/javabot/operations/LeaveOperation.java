package javabot.operations;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.Message;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SPI(BotOperation.class)
public class LeaveOperation extends BotOperation {
    private static final Logger log = LoggerFactory.getLogger(LeaveOperation.class);

    @Override
    public boolean handleMessage(final Message event) {
        final String message = event.getMessage();
        final Channel channel = event.getChannel();
        final User sender = event.getUser();
        if ("leave".equals(message.toLowerCase())) {
            if (channel.getName().equalsIgnoreCase(event.getUser().getNick())) {
                getBot().postMessage(channel, event.getUser(), Sofia.leavePrivmsg(sender));
            } else {
                getBot().postMessage(channel, event.getUser(), Sofia.leaveChannel(event.getUser().getNick()));
                new Thread(() -> {
                    //                        ircBot.getUserChannelDao().getChannel(channel.getName());
                    try {
                        Thread.sleep(60000 * 15);
                    } catch (InterruptedException exception) {
                        log.error(exception.getMessage(), exception);
                    }
                    //                        getBot().getPircBot().joinChannel(channel);
                }).start();
            }
        }
        return false;
    }
}
