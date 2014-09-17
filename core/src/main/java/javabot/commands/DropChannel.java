package javabot.commands;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.dao.ChannelDao;
import javabot.model.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import javax.inject.Inject;

@SPI({AdminCommand.class})
public class DropChannel extends AdminCommand {
    @Inject
    private ChannelDao dao;

    @Inject
    private PircBotX ircBot;

    @Param
    String channel;

    @Override
    public void execute(final MessageEvent event) {
        final Channel chan = dao.get(channel);
        if (chan != null) {
            dao.delete(chan);
            getBot().postMessage(ircBot.getUserChannelDao().getChannel(channel), event.getUser(),
                                 Sofia.channelDeleted(event.getUser().getNick()));
            event.getChannel().send().part(Sofia.channelDeleted(event.getUser().getNick()));
        } else {
            getBot().postMessage(event.getChannel(), event.getUser(), Sofia.channelUnknown(channel, event.getUser().getNick()));
        }
    }
}