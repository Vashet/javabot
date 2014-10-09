package javabot;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import javabot.model.Config;
import org.pircbotx.Channel;
import org.pircbotx.Configuration.BotFactory;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.output.OutputRaw;

import javax.inject.Provider;
import java.io.IOException;
import java.util.Properties;

public class JavabotTestModule extends JavabotModule {
    private Provider<TestJavabot> botProvider;
    private final Messages messages = new Messages();

    @Override
    protected void configure() {
        botProvider = binder().getProvider(TestJavabot.class);
        super.configure();
    }

    @Override
    public Properties getProperties() throws IOException {
        return loadProperties("test-javabot.properties");
    }

    @Provides
    @Singleton
    public Javabot getJavabot() {
        TestJavabot testJavabot = botProvider.get();
        testJavabot.start();
        return testJavabot;
    }

    @Override
    protected PircBotX createIrcBot() {
        Config config = configDaoProvider.get().get();
        String nick = config.getNick();
        Builder<PircBotX> builder = new Builder<>()
                                        .setName(BaseTest.TEST_BOT_NICK)
                                        .setLogin(BaseTest.TEST_BOT_NICK)
                                        .addListener(getBotListener())
                                        .setServerHostname(config.getServer())
                                        .setServerPort(config.getPort())
                                        .setBotFactory(new TestBotFactory());

        return new TestPircBotX(builder);
    }

    @Provides
    public Messages messages() {
        return messages;
    }

    private static class TestPircBotX extends PircBotX {
        public TestPircBotX(final Builder<PircBotX> builder) {
            super(builder.buildConfiguration());
        }

        @Override
        protected void connect() throws IOException, IrcException {
            System.out.println("***** Testing.  Not actually connecting.");
            reconnectStopped = true;
        }
    }

    private class TestBotFactory extends BotFactory {
        @Override
        public OutputRaw createOutputRaw(final PircBotX bot) {
            return new OutputRaw(bot, 0) {
                @Override
                public void rawLine(final String line) {
                    System.out.println("line = [" + line + "]");
                }

                @Override
                public void rawLineNow(final String line) {
                    System.out.println("line = [" + line + "]");
                }

                @Override
                public void rawLineNow(final String line, final boolean resetDelay) {
                    System.out.println("line = [" + line + "], resetDelay = [" + resetDelay + "]");
                }

                @Override
                public void rawLineSplit(final String prefix,
                                         final String message,
                                         final String suffix) {
                    System.out.println("prefix = [" + prefix + "], message = [" + message + "], suffix = [" + suffix + "]");
                }
            };
        }

        @Override
        public OutputChannel createOutputChannel(final PircBotX bot, final Channel channel) {
            return new OutputChannel(bot, channel) {
                @Override
                public void message(final User user, final String message) {
                    messages.add(message);
                }

                @Override
                public void message(final String message) {
                    messages.add(message);
                }
            };
        }
    }
}
