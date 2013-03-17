package idrabenia.weather.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import idrabenia.weather.R;
import idrabenia.weather.domain.CurrentWeather;
import idrabenia.weather.domain.WeatherItem;
import idrabenia.weather.service.WorldWeatherService;
import idrabenia.weather.service.converters.WeatherParser;

import java.util.List;

public class WeatherActivity extends Activity {

    private UpdateWeatherTask updateWeatherTask = new UpdateWeatherTask();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        updateWeatherTask.execute("Minsk");
    }

    private class UpdateWeatherTask extends AsyncTask<String, Integer, String> {
        private final WeatherParser parser = new WeatherParser();

        @Override
        protected String doInBackground(String... params) {
            String city = params[0];

            return new WorldWeatherService(WeatherActivity.this.getBaseContext()).queryWeatherInfo(city);
        }

        @Override
        protected void onPostExecute(final String response) {
            setCurWeather(response);

            List<WeatherItem> weatherItems = parser.parseWeatherItemList(response);
            findById(ListView.class, R.id.weather_items).setAdapter(new WeatherItemsAdapter(WeatherActivity.this,
                    R.layout.weather_item, weatherItems));
        }

    }

    @SuppressWarnings("unchecked")
    <T> T findById(Class<T> type, int id) {
        return (T) findViewById(id);
    }

    private void setCurWeather(String response) {
        CurrentWeather curWeather = new WeatherParser().parseCurrentWeather(response);
        findById(TextView.class, R.id.cur_temperature).setText(Integer.toString(curWeather.temperature));
        findById(TextView.class, R.id.cur_weather_summary).setText(curWeather.summary);
        findById(TextView.class, R.id.cur_wind_direction).setText(curWeather.windDirection);

        String windSpeed = getString(R.string.wind_speed_pattern, curWeather.windSpeed);
        findById(TextView.class, R.id.cur_wind_speed).setText(windSpeed);
    }

}
