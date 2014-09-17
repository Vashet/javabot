package javabot;

import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import javabot.dao.AdminDao;
import javabot.dao.ChannelDao;
import javabot.dao.EventDao;
import javabot.dao.NickServDao;
import javabot.model.AdminEvent;
import javabot.model.AdminEvent.State;
import javabot.model.Channel;
import javabot.model.NickServInfo;
import javabot.model.UserFactory;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeTest;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"StaticNonFinalField"})
public class BaseTest {
    public static final String TEST_NICK = "jbtestuser";

    public EnumSet<State> done = EnumSet.of(State.COMPLETED, State.FAILED);

    @Inject
    private UserFactory userFactory;

    @Inject
    private EventDao eventDao;

    @Inject
    private ChannelDao channelDao;

    @Inject
    private NickServDao nickServDao;

    @Inject
    private Provider<PircBotX> ircBot;

    @Inject
    private AdminDao adminDao;

    public final String ok;

    private final User testUser;

    protected TestJavabot bot;

    private Injector injector;

    public BaseTest() {
        injector = com.google.inject.Guice.createInjector(new JavabotTestModule());
        Javabot bot = injector.getInstance(Javabot.class);
        injector.injectMembers(this);
        bot.start();
        testUser = userFactory.createUser(TEST_NICK, TEST_NICK, "hostmask");
        if (adminDao.getAdmin(testUser) == null) {
            adminDao.create(testUser.getNick(), testUser.getRealName(), testUser.getHostmask());
        }

        final String nick = TEST_NICK;
        ok = "OK, " + nick.substring(0, Math.min(nick.length(), 16)) + ".";
    }

    public User getTestUser() {
        return testUser;
    }

    public void drainMessages() {
        Awaitility.await("Draining Messages")
                  .atMost(1, TimeUnit.HOURS)
                  .pollInterval(5, TimeUnit.SECONDS)
                  .until(() -> getJavabot().getMessages().isEmpty());
    }

    public final TestJavabot getJavabot() {
        if (bot == null) {
            bot = injector.getInstance(TestJavabot.class);
            bot.start();
        }
        Channel channel = channelDao.get(getJavabotChannel().getName());
        if (channel == null) {
            channel = new Channel();
            channel.setName(getJavabotChannel().getName());
            channel.setLogged(true);
            channelDao.save(channel);
        }
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

    public static class JavabotTestModule extends JavabotModule {
        private Provider<TestJavabot> botProvider;

        @Override
        protected void configure() {
            super.configure();
            botProvider = binder().getProvider(TestJavabot.class);
        }

        @Override
        public Properties getProperties() throws IOException {
            return loadProperties("test-javabot.properties");
        }

        @Provides
        @Singleton
        public Javabot getJavabot() {
            return botProvider.get();
        }
    }
}