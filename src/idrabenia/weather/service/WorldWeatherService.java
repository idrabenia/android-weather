package idrabenia.weather.service;

import android.content.Context;
import idrabenia.weather.R;
import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.domain.weather.WeatherItem;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static idrabenia.weather.service.ExceptionHandlingTemplate.executeWithExceptionHandler;

/**
 * @author Ilya Drabenia
 */
public class WorldWeatherService {
    public static final String ACCESS_KEY = "b387f72545173141131603";

    private final Context context;

    public WorldWeatherService(Context contextValue) {
        context = contextValue;
    }

    public String queryWeatherInfo(final String location) {
        if (location == null || location.length() == 0) {
            throw new IllegalArgumentException("location == null || location.length() == 0");
        }

        return executeWithExceptionHandler(new ExceptionHandlingTemplate.Action<String>() {
            @Override
            public String execute() throws Exception {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet getWeatherRequest = new HttpGet(context.getString(R.string.world_weather_service_url,
                        ACCESS_KEY, location));

                HttpResponse response = httpClient.execute(getWeatherRequest);

                BufferedReader responseReader = new BufferedReader(new InputStreamReader(response.getEntity()
                        .getContent(), "UTF-8"));

                StringBuilder builder = new StringBuilder();
                String curLine;
                while ((curLine = responseReader.readLine()) != null) {
                    builder.append(curLine);
                }

                return builder.toString();
            }
        });
    }

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
