package javabot.operations;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.Message;
import javabot.dao.AdminDao;
import javabot.dao.ConfigDao;
import javabot.model.Config;
import javabot.model.NickRegistration;

import javax.inject.Inject;

@SPI(BotOperation.class)
public class RegisterNickOperation extends BotOperation {
    @Inject
    private AdminDao adminDao;
    @Inject
    private ConfigDao configDao;

    @Override
    public boolean handleMessage(Message event) {
        final String message = event.getMessage();
        if (message.startsWith("register ")) {
            String[] split = message.split(" ");
            if (split.length > 1) {
                String twitterName = split[1];
                NickRegistration registration = new NickRegistration(event.getUser(), twitterName);
                adminDao.save(registration);
                Config config = configDao.get();
                String eventMessage = Sofia.registerNick(config.getUrl(), registration.getUrl(), twitterName);

                getBot().postMessage(null, event.getUser(), eventMessage);
                return true;
            }
        }
        return false;
    }
}
