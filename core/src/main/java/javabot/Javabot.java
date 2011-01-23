package javabot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.persistence.NoResultException;

import ca.grimoire.maven.ArtifactDescription;
import ca.grimoire.maven.NoArtifactException;
import javabot.commands.AdminCommand;
import javabot.dao.ChannelDao;
import javabot.dao.ConfigDao;
import javabot.dao.LogsDao;
import javabot.dao.ShunDao;
import javabot.database.UpgradeScript;
import javabot.model.Config;
import javabot.operations.BotOperation;
import javabot.operations.OperationComparator;
import javabot.operations.StandardOperation;
import javabot.pircbot.PircBotJavabot;
import org.jibble.pircbot.PircBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class Javabot extends PircBot implements ApplicationContextAware {
    public static final int THROTTLE_TIME = 5 * 1000;
    public static final Logger log = LoggerFactory.getLogger(IrcLibJavabot.class);
    protected ApplicationContext context;
    private String host;
    private String password;
    private int port;
    private String[] startStrings;
    private int authWait;
    protected final List<String> ignores = new ArrayList<String>();
    @Autowired
    protected LogsDao logsDao;
    @Autowired
    ChannelDao channelDao;
    @Autowired
    private ConfigDao configDao;
    @Autowired
    protected ShunDao shunDao;
    Config config;
    private Map<String, BotOperation> operations;
    private final Set<BotOperation> activeOperations = new TreeSet<BotOperation>(new OperationComparator());
    private final List<BotOperation> standard = new ArrayList<BotOperation>();
    private String nick;

    public Javabot(final ApplicationContext applicationContext) {
        context = applicationContext;
        inject(this);
        try {
            config = configDao.get();
        } catch (NoResultException e) {
            config = configDao.create();
        }
        loadOperations(config);
        loadConfig();
        applyUpgradeScripts();
        connect();
    }

    public static void validateProperties() {
        final Properties props = new Properties();
        InputStream stream = null;
        try {
            try {
                stream = new FileInputStream("javabot.properties");
                props.load(stream);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Please define a javabot.properties file to configure the bot");
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        check(props, "javabot.server");
        check(props, "javabot.port");
        check(props, "jdbc.url");
        check(props, "jdbc.username");
        check(props, "jdbc.password");
        check(props, "jdbc.driver");
        check(props, "hibernate.dialect");
        check(props, "javabot.nick");
        check(props, "javabot.password");
        check(props, "javabot.admin.nick");
        check(props, "javabot.admin.hostmask");
        System.getProperties().putAll(props);
    }

    static void check(final Properties props, final String key) {
        if (props.get(key) == null) {
            throw new RuntimeException(String.format("Please specify the property %s in javabot.properties", key));
        }
    }

    protected abstract void connect();

    public final void inject(final Object object) {
        context.getAutowireCapableBeanFactory().autowireBean(object);
    }

    protected final void applyUpgradeScripts() {
        for (final UpgradeScript script : UpgradeScript.loadScripts()) {
            script.execute(this);
        }
    }

    public String loadVersion() {
        try {
            final ArtifactDescription javabot = ArtifactDescription.locate("javabot", "core");
            return javabot.getVersion();
        } catch (NoArtifactException nae) {
            return "UNKNOWN";
        }
    }

    public void loadConfig() {
        try {
            log.debug("Running with configuration: " + config);
            host = config.getServer();
            port = config.getPort();
            setNick(config.getNick());
            setNickPassword(config.getPassword());
            authWait = 3000;
            startStrings = new String[]{getNick(), "~"};
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String getNick() {
        return nick;
    }

    public void setNick(final String nick) {
        this.nick = nick;
    }


    @SuppressWarnings({"unchecked"})
    protected final void loadOperations(final Config config) {
        operations = new TreeMap<String, BotOperation>();
        for (final BotOperation op : BotOperation.list()) {
            inject(op);
            op.setBot(this);
            operations.put(op.getName(), op);
        }
        try {
            for (final String name : config.getOperations()) {
                enableOperation(name);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        if (config.getOperations().isEmpty()) {
            for (final BotOperation operation : operations.values()) {
                enableOperation(operation.getName());
            }
        }
        addDefaultOperations(ServiceLoader.load(AdminCommand.class));
        addDefaultOperations(ServiceLoader.load(StandardOperation.class));
        Collections.sort(standard, new Comparator<BotOperation>() {
            @Override
            public int compare(final BotOperation o1, final BotOperation o2) {
                if ("GetFactoid".equals(o1.getName())) {
                    return 1;
                }
                if ("GetFactoid".equals(o2.getName())) {
                    return -1;
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    private void addDefaultOperations(final ServiceLoader<? extends BotOperation> loader) {
        for (final BotOperation operation : loader) {
            inject(operation);
            operation.setBot(this);
            standard.add(operation);
        }
    }

    public boolean disableOperation(final String name) {
        boolean disabled = false;
        if (operations.get(name) != null) {
            activeOperations.remove(operations.get(name));
            final Config c = configDao.get();
            c.getOperations().remove(name);
            configDao.save(c);
            disabled = true;
        }
        return disabled;
    }

    public boolean enableOperation(final String name) {
        boolean enabled = false;
        final Config c = configDao.get();
        if (operations.get(name) != null) {
            activeOperations.add(operations.get(name));
            c.getOperations().add(name);
            enabled = true;
        } else {
            c.getOperations().remove(name);
        }
        configDao.save(c);
        return enabled;
    }

    public int getAuthWait() {
        return authWait;
    }

    public String getHost() {
        return host;
    }

    public Iterator<BotOperation> getOperations() {
        final List<BotOperation> ops = new ArrayList<BotOperation>(activeOperations);
        ops.addAll(standard);
        return ops.iterator();
    }

    @SuppressWarnings({"EmptyCatchBlock"})
    protected void sleep(final int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException exception) {
        }
    }

    public abstract void postMessage(final Message message);

    public abstract void postAction(final Message message);

    public String[] getStartStrings() {
        return startStrings;
    }

    public void setStartStrings(final String... startStrings) {
        final List<String> start = new ArrayList<String>();
        start.add(getNick());
        start.addAll(Arrays.asList(startStrings));
        this.startStrings = start.toArray(new String[start.size()]);
    }

    public void addIgnore(final String sender) {
        ignores.add(sender);
    }

    public abstract boolean userIsOnChannel(final IrcUser user, final String channel);

    public abstract boolean isOnSameChannelAs(final IrcUser user);

    public abstract List<Message> getResponses(final String channel, final IrcUser sender, final String thing);

    public String getNickPassword() {
        return password;
    }

    public void setNickPassword(final String password) {
        this.password = password;
    }

    public abstract IrcUser getUser(String name);

    public static void main(final String[] args) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("Starting PircBotJavabot");
        }
        validateProperties();
        new PircBotJavabot(new ClassPathXmlApplicationContext("classpath:applicationContext.xml"));
    }
}
