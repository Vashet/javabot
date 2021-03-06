package javabot;

import com.antwerkz.sofia.Sofia;
import javabot.dao.AdminDao;
import javabot.dao.ChannelDao;
import javabot.dao.ConfigDao;
import javabot.dao.LogsDao;
import javabot.dao.NickServDao;
import javabot.model.Admin;
import javabot.model.Channel;
import javabot.model.Logs;
import javabot.model.Logs.Type;
import javabot.operations.throttle.NickServViolationException;
import javabot.operations.throttle.Throttler;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
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
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BotListener extends ListenerAdapter<PircBotX> {
    private static final Logger LOG = LoggerFactory.getLogger(BotListener.class);

    @Inject
    private Throttler throttler;

    @Inject
    private NickServDao nickServDao;

    @Inject
    private LogsDao logsDao;

    @Inject
    private ChannelDao channelDao;

    @Inject
    private AdminDao adminDao;

    @Inject
    private Provider<Javabot> javabotProvider;

    @Inject
    private ConfigDao configDao;

    private final List<String> nickServ = new ArrayList<>();

    @Inject
    private Provider<PircBotX> ircBot;

    public void log(final String string) {
        if (Javabot.LOG.isInfoEnabled()) {
            Javabot.LOG.info(string);
        }
    }

    @Override
    public void onMessage(final MessageEvent event) {
        Javabot javabot = javabotProvider.get();
        javabot.executors.execute(() -> javabot.processMessage(new Message(event.getChannel(), event.getUser(), event.getMessage())));
    }

    @Override
    public void onJoin(final JoinEvent event) {
        logsDao.logMessage(Logs.Type.JOIN, event.getChannel(), event.getUser(),
                           Sofia.userJoined(event.getUser().getNick(), event.getUser().getHostmask(),
                                            event.getChannel().getName()));
        if (adminDao.count() == 0) {
            Set<User> users = getIrcBot().getUserChannelDao().getUsers(event.getChannel());
            Admin admin = null;
            Iterator<User> iterator = users.iterator();
            while (admin == null && iterator.hasNext()) {
                final User user = iterator.next();
                if (!user.getNick().equals(javabotProvider.get().getNick())) {
                    admin = adminDao.create(user.getNick(), user.getLogin(), user.getHostmask());
                }
            }
        }
    }

    @Override
    public void onPart(final PartEvent event) {
        logsDao.logMessage(Logs.Type.PART, event.getChannel(), event.getUser(), Sofia.userParted(event.getUser().getNick(), event.getReason()));
        nickServDao.unregister(event.getUser());
    }

    @Override
    public void onQuit(final QuitEvent event) {
        logsDao.logMessage(Logs.Type.QUIT, null, event.getUser(), Sofia.userQuit(event.getUser().getNick(), event.getReason()));
        nickServDao.unregister(event.getUser());
    }

    @Override
    public void onInvite(final InviteEvent event) {
        Channel channel = channelDao.get(event.getChannel());
        if (channel != null) {
            if (channel.getKey() == null) {
                ircBot.get().sendIRC().joinChannel(channel.getName());
            } else {
                ircBot.get().sendIRC().joinChannel(channel.getName(), channel.getKey());
            }
        } else if (adminDao.count() == 0) {
            channelDao.create(event.getChannel(), true, null);
            getIrcBot().sendIRC().joinChannel(event.getChannel());
        }
    }

    @Override
    public void onConnect(final ConnectEvent event) {
        nickServDao.clear();
        event.getBot().sendIRC().message("nickserv", "identify " + configDao.get().getPassword());
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
        logsDao.logMessage(Type.NICK, null, event.getUser(), Sofia.userNickChanged(event.getOldNick(), event.getNewNick()));
        nickServDao.updateNick(event.getOldNick(), event.getNewNick());
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) {
        Javabot javabot = javabotProvider.get();
        String startStringForPm = "";
        final String message = event.getMessage();
        for (final String startString : javabot.getStartStrings()) {
            if (message.startsWith(startString)) {
                startStringForPm = startString;
            }
        }

        final String content = javabot.extractContentFromMessage(message, startStringForPm);
        if (adminDao.isAdmin(event.getUser()) || javabot.isOnCommonChannel(event.getUser())) {
            javabot.executors.execute(() -> {
                javabot.logMessage(null, event.getUser(), event.getMessage());
                try {
                    if (!throttler.isThrottled(event.getUser())) {
                        javabot.getResponses(new Message(event.getUser(), content), event.getUser());
                    }
                } catch (NickServViolationException e) {
                    event.getUser().send().message(e.getMessage());
                }
            });
        }
    }

    @Override
    public void onAction(final ActionEvent event) {
        logsDao.logMessage(Logs.Type.ACTION, event.getChannel(), event.getUser(), event.getMessage());
    }

    @Override
    public void onKick(final KickEvent event) {
        logsDao.logMessage(Logs.Type.KICK, event.getChannel(), event.getUser(),
                           String.format(" kicked %s (%s)", event.getRecipient().getNick(), event.getReason()));
    }

    public PircBotX getIrcBot() {
        return ircBot.get();
    }
}
