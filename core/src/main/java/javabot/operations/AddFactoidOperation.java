package javabot.operations;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.IrcEvent;
import javabot.Message;
import javabot.dao.AdminDao;
import javabot.dao.FactoidDao;
import javabot.dao.LogsDao;
import javabot.model.Factoid;
import javabot.model.IrcUser;
import javabot.model.Logs.Type;
import javabot.operations.throttle.Throttler;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@SPI(StandardOperation.class)
public class AddFactoidOperation extends StandardOperation {
    public static final Logger log = LoggerFactory.getLogger(AddFactoidOperation.class);

    @Inject
    private FactoidDao factoidDao;

    @Inject
    private LogsDao logDao;

    @Inject
    private AdminDao adminDao;

    @Inject
    private Throttler throttler;

    @Override
    public final boolean handleMessage(final MessageEvent event) {
        String message = event.getMessage();
        final Channel channel = event.getChannel();
        final User sender = event.getUser();
        boolean handled = false;
        if (message.startsWith("no ") || message.startsWith("no, ")) {
            if (!channel.getName().startsWith("#") && !isAdminUser(event.getUser())) {
                getBot().postMessage(channel, event.getUser(), "Sorry, factoid changes are not allowed in private messages."));
                handled = true;
            } else {
                message = message.substring(2);
                if (message.startsWith(",")) {
                    message = message.substring(1);
                }
                message = message.trim();
                handled = updateFactoid(event, message);
            }
        }
        if (!handled) {
            handled = addFactoid(event, message, channel, sender);
        }
        return handled;
    }

    private boolean updateFactoid(final MessageEvent event, final String message) {
        final List<Message> responses = new ArrayList<>();
        final int is = message.indexOf(" is ");
        if (is != -1) {
            final Channel channel = event.getChannel();
            if (!channel.getName().startsWith("#") && !isAdminUser(event.getUser())) {
                getBot().postMessage(channel, event.getUser(), Sofia.factoidPrivmsgChange()));
            } else {
                String key = message.substring(0, is);
                key = key.replaceAll("^\\s+", "");
                final Factoid factoid = factoidDao.getFactoid(key);
                boolean admin = isAdminUser(event.getUser());
                if (factoid != null) {
                    if (factoid.getLocked()) {
                        if (admin) {
                            responses.add(updateExistingFactoid(event, message, factoid, is, key));
                        } else {
                            logDao.logMessage(Type.MESSAGE, event.getUser().getNick(), event.getChannel(),
                                              format(Sofia.changingLockedFactoid(event.getUser(), key)));
                            responses.add(new Message(event.getChannel(), event, Sofia.factoidLocked(event.getSender())));
                        }
                    } else {
                        responses.add(updateExistingFactoid(event, message, factoid, is, key));
                    }
                }
            }
        }
        return responses;
    }

    private List<Message> addFactoid(final IrcEvent event, final String message, final String channel,
                                     final IrcUser sender) {
        final List<Message> responses = new ArrayList<>();
        if (message.toLowerCase().contains(" is ")) {
            if (!channel.startsWith("#") && !isAdminUser(event)) {
                responses.add(new Message(channel, event, "Sorry, factoid changes are not allowed in private messages."));
            } else {
                log.error(
                             format("adding factoid because of '%s' with message '%s' from channel '%s' and user '%s'", event, message,
                                    channel, sender)
                         );
                String key = message.substring(0, message.indexOf(" is "));
                key = key.toLowerCase();
                while (key.endsWith(".") || key.endsWith("?") || key.endsWith("!")) {
                    key = key.substring(0, key.length() - 1);
                }
                final int index = message.indexOf(" is ");
                String value = null;
                if (index != -1) {
                    value = message.substring(index + 4, message.length());
                }
                if (key.trim().isEmpty()) {
                    responses.add(new Message(channel, event, Sofia.invalidFactoidName()));
                } else if (value == null || value.trim().isEmpty()) {
                    responses.add(new Message(channel, event, Sofia.invalidFactoidValue()));
                } else if (factoidDao.hasFactoid(key)) {
                    responses.add(
                                     new Message(channel, event, Sofia.factoidExists(key, sender)));
                } else {
                    if (value.startsWith("<see>")) {
                        value = value.toLowerCase();
                    }
                    factoidDao.addFactoid(sender.getNick(), key, value);
                    responses.add(new Message(channel, event, Sofia.ok(sender)));
                }
            }
        }
        return responses;
    }

    private Message updateExistingFactoid(final IrcEvent event, final String message, final Factoid factoid,
                                          final int is, final String key) {
        final String newValue = message.substring(is + 4);
        logDao.logMessage(Type.MESSAGE, event.getSender().getNick(), event.getChannel(),
                          Sofia.factoidChanged(event.getSender(), key, factoid.getValue(), newValue, event.getChannel()));
        factoid.setValue(newValue);
        factoid.setUpdated(LocalDateTime.now());
        factoid.setUserName(event.getSender().getNick());
        factoidDao.save(factoid);
        return new Message(event.getChannel(), event, Sofia.ok(event.getSender()));
    }
}
