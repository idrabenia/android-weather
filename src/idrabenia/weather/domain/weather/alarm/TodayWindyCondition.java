package idrabenia.weather.domain.weather.alarm;

import idrabenia.weather.R;
import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.domain.weather.WeatherItem;
import org.apache.commons.lang.Validate;

import java.util.List;

/**
 * @author Ilya Drabenia
 * @since 04.04.13
 */
public class TodayWindyCondition implements Condition {

    @Override
    public boolean isPerformed(CurrentWeather curWeather, List<WeatherItem> weatherItems) {
        Validate.notNull(curWeather);
        return curWeather.windSpeed > 25;
    }

    @Override
    public int getMessageId() {
        return R.string.today_is_windy;
    }

}
