package javabot.operations;

import javabot.Message;
import javabot.dao.LogsDao;
import javabot.model.Logs;
import org.apache.commons.logging.Log;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class LogsOperationTest extends BaseOperationTest {
    @Inject
    private PircBotX ircBot;

    @Inject
    private LogsDao logsDao;

    @Inject
    private Datastore datastore;

    @Test
    public void testChannelLogs() throws Exception {
        Query<Logs> query = datastore.createQuery(Logs.class);
        datastore.delete(query);
        // Add a known and unique message to the logs so we can validate that we are testing against new data
        String uuid = UUID.randomUUID().toString();
        sendMessage(uuid);
        List<MessageEvent> list = sendMessage("~logs");
        Assert.assertEquals(list.isEmpty(), false);
        int listSize = list.size();
        Assert.assertTrue(list.get(listSize - 2).getMessage().contains(uuid));
        Assert.assertTrue(list.get(listSize - 1).getMessage().contains("~logs"));
    }

    @Test
    public void testNickSpecificLogsWhenNoLogsForNick() throws Exception {
        // We generate unique user names so that existing data in the DB doesn't interfere with this unit test
        String uuid = UUID.randomUUID().toString();
        List<MessageEvent> list = sendMessage("~logs " + uuid);
        int listSize = list.size();
        Assert.assertEquals(listSize, 1);
        String msg = list.get(listSize - 1).getMessage();
        Assert.assertEquals(msg.contains("No logs found for nick: " + uuid), true);
    }

    @Test
    public void testNickSpecificLogsWhenLogs() throws Exception {
        String uuid = UUID.randomUUID().toString();
        User user = new TestUser(uuid) {

        };
        getJavabot().processMessage(new MessageEvent<>(ircBot, getJavabotChannel(), user, "Hello I'm " + uuid));
        List<MessageEvent> list = sendMessage("~logs " + uuid);
        int listSize = list.size();
        Assert.assertEquals(listSize, 1);
        Assert.assertEquals(list.get(listSize - 1).getMessage().contains(uuid), true);
    }

    private class TestUser extends User {
        public TestUser(final String nick) {
            super(ircBot, ircBot.getUserChannelDao(), nick);
        }
    }
}
