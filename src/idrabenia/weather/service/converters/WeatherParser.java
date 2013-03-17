package idrabenia.weather.service.converters;

import idrabenia.weather.domain.CurrentWeather;
import idrabenia.weather.domain.WeatherItem;
import idrabenia.weather.service.ExceptionHandlingTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static idrabenia.weather.service.ExceptionHandlingTemplate.executeWithExceptionHandler;

/**
 * @author: Ilya Drabenia
 * @since 16.03.13
 */
public class WeatherParser {

    public CurrentWeather parseCurrentWeather(final String jsonString) {
        return executeWithExceptionHandler(new ExceptionHandlingTemplate.Action<CurrentWeather>() {
            @Override
            public CurrentWeather execute() throws Exception {
                CurrentWeather weather = new CurrentWeather();
                JSONObject json = new JSONObject(jsonString);

                JSONObject curConditions = json.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0);
                weather.temperature = Integer.parseInt(curConditions.getString("temp_C"));
                weather.windDirection = curConditions.getString("winddir16Point");
                weather.windSpeed = Integer.parseInt(curConditions.getString("windspeedKmph"));
                weather.summary = curConditions.getJSONArray("weatherDesc").getJSONObject(0).getString("value");
                return weather;
            }
        });
    }

    public List<WeatherItem> parseWeatherItemList(final String jsonString) {
        return executeWithExceptionHandler(new ExceptionHandlingTemplate.Action<List<WeatherItem>>() {
            @Override
            public List<WeatherItem> execute() throws Exception {
                List<WeatherItem> items = new ArrayList<WeatherItem>();

                //items.add(parseCurrentWeather(jsonString));

                JSONArray itemJson = new JSONObject(jsonString).getJSONObject("data").getJSONArray("weather");
                for (int i = 0; i < itemJson.length(); i += 1) {
                    items.add(parseWeatherItem(itemJson.getJSONObject(i)));
                }

                return items;
            }
        });
    }

    private WeatherItem parseWeatherItem(final JSONObject curItemJson) {
        return executeWithExceptionHandler(new ExceptionHandlingTemplate.Action<WeatherItem>() {
            @Override
            public WeatherItem execute() throws Exception {
                WeatherItem curItem = new WeatherItem();

                curItem.maxTemperature = Integer.parseInt(curItemJson.getString("tempMaxC"));
                curItem.minTemperature = Integer.parseInt(curItemJson.getString("tempMinC"));
                curItem.imageUrl = curItemJson.getJSONArray("weatherIconUrl").getJSONObject(0).getString("value");
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(curItemJson.getString("date"));
                curItem.date = new SimpleDateFormat("E").format(date);
                curItem.description = curItemJson.getJSONArray("weatherDesc").getJSONObject(0).getString("value");

                return curItem;
            }
        });
    }

}
