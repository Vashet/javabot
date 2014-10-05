package javabot;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import javabot.dao.AdminDao;
import javabot.dao.ChangeDao;
import javabot.dao.ChannelDao;
import javabot.dao.EventDao;
import javabot.dao.LogsDao;
import javabot.dao.NickServDao;
import javabot.model.AdminEvent;
import javabot.model.AdminEvent.State;
import javabot.model.Change;
import javabot.model.Channel;
import javabot.model.Logs;
import javabot.model.NickServInfo;
import javabot.model.UserFactory;
import org.mongodb.morphia.Datastore;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Guice;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Guice(modules = {JavabotTestModule.class})
public class BaseTest {
    public static final String TEST_NICK = "jbtestuser";

    public EnumSet<State> done = EnumSet.of(State.COMPLETED, State.FAILED);

    @Inject
    private UserFactory userFactory;

    @Inject
    private Datastore datastore;

    @Inject
    private EventDao eventDao;

    @Inject
    private ChannelDao channelDao;

    @Inject
    private LogsDao logsDao;

    @Inject
    private NickServDao nickServDao;

    @Inject
    private Provider<PircBotX> ircBot;

    @Inject
    private AdminDao adminDao;

    @Inject
    private ChangeDao changeDao;

    @Inject
    private TestJavabot bot;

    @Inject
    private Messages messages;

    public String ok;

    private User testUser;

    @BeforeTest
    public void setup() {
        bot.start();
        User testUser = getTestUser();
        if (adminDao.getAdmin(testUser) == null) {
            adminDao.create(testUser.getNick(), testUser.getRealName(), testUser.getHostmask());
        }

        Channel channel = channelDao.get(getJavabotChannel().getName());
        if (channel == null) {
            channel = new Channel();
            channel.setName(getJavabotChannel().getName());
            channel.setLogged(true);
            channelDao.save(channel);
        }

        datastore.delete(logsDao.getQuery(Logs.class));
        datastore.delete(changeDao.getQuery(Change.class));

        ok = "OK, " + TEST_NICK.substring(0, Math.min(TEST_NICK.length(), 16)) + ".";
    }

    @BeforeMethod
    public void clearMessages() {
        messages.get();
    }

    public User getTestUser() {
        if (testUser == null) {
            testUser = userFactory.createUser(TEST_NICK, TEST_NICK, "hostmask");
        }
        return testUser;
    }

    public List<Message> getMessages() {
        return messages.get();
    }

    public final TestJavabot getJavabot() {
        return bot;
    }

    public PircBotX getIrcBot() {
        return ircBot.get();
    }

    protected org.pircbotx.Channel getJavabotChannel() {
        org.pircbotx.Channel channel = getIrcBot().getUserChannelDao().getChannel("#jbunittest");
        System.out.println("channel = " + channel);
        assert channel != null;
        return channel;
    }

    @SuppressWarnings({"EmptyCatchBlock"})
    protected static void sleep(final int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException exception) {
        }
    }

    @AfterSuite
    public void shutdown() throws InterruptedException {
        if (bot != null) {
            bot.shutdown();
        }
    }

    protected void waitForEvent(final AdminEvent event, final String alias, final Duration timeout) {
        Awaitility.await(alias)
                  .atMost(timeout)
                  .pollInterval(5, TimeUnit.SECONDS)
                  .until(() -> done.contains(eventDao.find(event.getId()).getState()));
    }

    protected User registerIrcUser(final String nick, final String userName, final String host) {
        User bob = userFactory.createUser(nick, userName, host);
        NickServInfo info = new NickServInfo(bob);
        info.setRegistered(info.getRegistered().minusDays(100));
        nickServDao.clear();
        nickServDao.save(info);
        return bob;
    }

}