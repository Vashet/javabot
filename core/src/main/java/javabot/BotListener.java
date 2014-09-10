package javabot;

import javabot.dao.ChannelDao;
import javabot.dao.LogsDao;
import javabot.dao.NickServDao;
import javabot.model.Channel;
import javabot.model.Logs;
import javabot.operations.throttle.NickServViolationException;
import javabot.operations.throttle.Throttler;
import org.pircbotx.Configuration.BotFactory;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class BotListener extends ListenerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(BotListener.class);

    @Inject
    private Throttler throttler;

    @Inject
    private NickServDao nickServDao;

    @Inject
    private LogsDao logsDao;

    @Inject
    private PircBotX ircBot;

    @Inject
    private ChannelDao channelDao;

    private final List<String> nickServ = new ArrayList<>();
    private final Javabot javabot;

    @Inject
    public BotListener(final Javabot javabot) {
        this.javabot = javabot;
    }

    public void log(final String string) {
        if (Javabot.log.isInfoEnabled()) {
            Javabot.log.info(string);
        }
    }

    public String getNick() {
        return ircBot.getNick();
    }

    @Override
    public void onMessage(final MessageEvent event) {
        javabot.executors.execute(() -> javabot.processMessage(event));
    }

    @Override
    public void onJoin(final JoinEvent event) {
        logsDao.logMessage(Logs.Type.JOIN, event.getUser().getNick(), event.getChannel().getName(),
                           ":" + event.getUser().getHostmask() + " joined the channel");
    }

    @Override
    public void onPart(final PartEvent event) {
        logsDao.logMessage(Logs.Type.PART, event.getUser().getNick(), event.getChannel().getName(),
                           ":" + event.getUser() + " parted the channel");
        nickServDao.unregister(event.getUser());
    }

    @Override
    public void onQuit(final QuitEvent event) {
        logsDao.logMessage(Logs.Type.QUIT, event.getUser().getNick(), null, "quit");
        nickServDao.unregister(event.getUser());
    }

    @Override
    public void onInvite(final InviteEvent event) {
        org.pircbotx.Channel toJoin = event.getBot().getUserChannelDao().getChannel(event.getChannel());
        final Channel channel = channelDao.get(toJoin.getName());
        if (channel != null) {
            channel.join(javabot);
            new BotFactory().createOutputChannel(event.getBot(), toJoin);
        }
    }

    @Override
    public void onConnect(final ConnectEvent event) {
        nickServDao.clear();
    }

    @Override
    public void onNotice(final NoticeEvent event) {
        if (event.getUser().getNick().equalsIgnoreCase("NickServ")) {
            String message = event.getNotice().replace("\u0002", "");
            synchronized (nickServ) {
                if (message.equals("*** End of Info ***") && !nickServ.isEmpty()) {
                    try {
                        nickServDao.process(nickServ);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    nickServ.clear();
                } else {
                    LOG.debug(message);
                    if (message.startsWith("Information on ") || !nickServ.isEmpty()) {
                        nickServ.add(message);
                    }
                }
            }
        }
    }

    @Override
    public void onNickChange(final NickChangeEvent event) {
        nickServDao.updateNick(event.getOldNick(), event.getNewNick());
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) {
        if (javabot.adminDao.isAdmin(event.getUser()) || javabot.isOnCommonChannel(event.getUser())) {
            javabot.executors.execute(() -> {
                javabot.logMessage(null, event.getUser(), event.getMessage());
                try {
                    if (!throttler.isThrottled(event.getUser())) {
                        javabot.getResponses(new MessageEvent<>(event.getBot(), null, event.getUser(), event.getMessage()), event.getUser());
                    }
                } catch (NickServViolationException e) {
                    event.getUser().send().message(e.getMessage());
                }
            });
        }
    }

    @Override
    public void onAction(final ActionEvent event) {
        logsDao.logMessage(Logs.Type.ACTION, event.getUser().getNick(), event.getChannel().getName(), event.getMessage());
    }

    @Override
    public void onKick(final KickEvent event) {
        logsDao.logMessage(Logs.Type.KICK, event.getUser().getNick(), event.getChannel().getName(),
                           String.format(" kicked %s (%s)", event.getRecipient().getNick(), event.getReason()));
    }
}
