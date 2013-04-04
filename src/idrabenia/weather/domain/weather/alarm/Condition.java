package idrabenia.weather.domain.weather.alarm;

import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.domain.weather.WeatherItem;

import java.util.List;

/**
 * @author Ilya Drabenia
 * @since 04.04.13
 */
public interface Condition {

    boolean isPerformed(CurrentWeather curWeather, List<WeatherItem> weatherItems);

    int getMessageId();

}
