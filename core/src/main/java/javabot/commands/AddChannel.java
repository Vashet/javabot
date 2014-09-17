package javabot.commands;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.dao.ChannelDao;
import javabot.model.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import javax.inject.Inject;

@SPI({AdminCommand.class})
public class AddChannel extends AdminCommand {
    @Inject
    private ChannelDao dao;
    @Inject
    private PircBotX ircBot;

    @Param
    String name;
    @Param(defaultValue = "true", required = false)
    String logged;
    @Param(defaultValue = "", required = false)
    String password;

    @Override
    public void execute(final MessageEvent event) {
        if (name.startsWith("#")) {
            Channel channel = dao.get(name);
            final Boolean isLogged = Boolean.valueOf(logged);
            if (channel == null) {
                channel = dao.create(name, isLogged, password);
            } else {
                channel.setLogged(isLogged);
                dao.save(channel);
            }

            getBot().postMessage(event.getChannel(), event.getUser(), isLogged
                                                                      ? Sofia.adminJoiningLoggedChannel(name)
                                                                      : Sofia.adminJoiningChannel(name));
            if(channel.getKey() == null) {
                ircBot.sendIRC().joinChannel(channel.getName());
            } else {
                ircBot.sendIRC().joinChannel(channel.getName(), channel.getKey());
            }

            getBot().postMessage(ircBot.getUserChannelDao().getChannel(name), event.getUser(),
                                 Sofia.adminJoinedChannel(event.getUser().getNick()));
        } else {
            getBot().postMessage(event.getChannel(), event.getUser(), Sofia.adminBadChannelName());
        }
    }
}
