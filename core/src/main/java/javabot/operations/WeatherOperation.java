package javabot.operations;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.Message;
import javabot.dao.impl.WeatherDao;
import javabot.model.Weather;
import org.pircbotx.hooks.events.MessageEvent;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Gets current weather conditions for a place given as a parameter.
 */
@SPI(BotOperation.class)
public class WeatherOperation extends BotOperation {
    @Inject
    private WeatherDao weatherDao;

    @Override
    public final boolean handleMessage(final MessageEvent event) {
        final String message = event.getMessage();
        if (message.toLowerCase().startsWith("weather ")) {
            final String place = message.substring("weather ".length()).trim();
            final Weather result = weatherDao.getWeatherFor(place);
            if (result == null) {
                getBot().postMessage(event.getChannel(), event.getUser(), Sofia.weatherUnknown(place));
            } else {
                getBot().postMessage(event.getChannel(), event.getUser(), result.toString());
            }
            return true;
        }
        return false;
    }

}
