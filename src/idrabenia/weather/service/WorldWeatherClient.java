package idrabenia.weather.service;

import android.content.Context;
import android.location.Location;
import idrabenia.weather.R;
import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.domain.weather.WeatherItem;
import idrabenia.weather.service.rest.RestClientAction;
import idrabenia.weather.service.rest.RestClient;
import org.apache.commons.lang.Validate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static idrabenia.weather.service.ExceptionHandlingTemplate.withExceptionWrapper;

/**
 * @author Ilya Drabenia
 */
public class WorldWeatherClient {
    public static final String ACCESS_KEY = "b387f72545173141131603";

    private final Context context;

    public WorldWeatherClient(Context contextValue) {
        context = contextValue;
    }

    /**
     * Query weather info for specified place
     * @param location location of place weather for which will be queried
     * @throws idrabenia.weather.service.rest.NetworkException
     * @throws idrabenia.weather.service.rest.ParseResponseException
     * @return string contains requested json
     */
    public String queryWeatherInfo(Location location) {
        Validate.notNull(location);

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        final String url = context.getString(R.string.world_weather_service_url, ACCESS_KEY, latitude, longitude);

        return RestClient.withClient(new RestClientAction() {
            @Override
            public String call(RestClient client) {
                return client.getJson(url);
            }
        });
    }

    public CurrentWeather parseCurrentWeather(final String jsonString) {
        return withExceptionWrapper(new Callable<CurrentWeather>() {
            @Override
            public CurrentWeather call() throws Exception {
                CurrentWeather weather = new CurrentWeather();
                JSONObject json = new JSONObject(jsonString);

                JSONObject curConditions = json.getJSONObject("data").getJSONArray("current_condition")
                        .getJSONObject(0);
                weather.temperature = Integer.parseInt(curConditions.getString("temp_C"));
                weather.windDirection = curConditions.getString("winddir16Point");
                weather.windSpeed = Integer.parseInt(curConditions.getString("windspeedKmph"));
                weather.summary = curConditions.getJSONArray("weatherDesc").getJSONObject(0).getString("value");
                return weather;
            }
        });
    }

    public List<WeatherItem> parseWeatherItemList(final String jsonString) {
        return withExceptionWrapper(new Callable<List<WeatherItem>>() {
            @Override
            public List<WeatherItem> call() throws Exception {
                List<WeatherItem> items = new ArrayList<WeatherItem>();

                JSONArray itemJson = new JSONObject(jsonString).getJSONObject("data").getJSONArray("weather");
                for (int i = 0; i < itemJson.length(); i += 1) {
                    items.add(parseWeatherItem(itemJson.getJSONObject(i)));
                }

                return items;
            }
        });
    }

    private WeatherItem parseWeatherItem(final JSONObject curItemJson) {
        return withExceptionWrapper(new Callable<WeatherItem>() {
            @Override
            public WeatherItem call() throws Exception {
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
