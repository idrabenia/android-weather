package idrabenia.weather.service;

import android.content.Context;
import idrabenia.weather.R;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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

}
