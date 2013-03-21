package idrabenia.weather.ui;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
import idrabenia.weather.R;
import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.domain.weather.WeatherItem;
import idrabenia.weather.service.WorldWeatherService;

import java.util.Arrays;
import java.util.List;

public class WeatherActivity extends Activity {

    private UpdateWeatherTask updateWeatherTask = new UpdateWeatherTask();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_screen);

        refreshWeatherInfo();
    }

    void refreshWeatherInfo() {
        LocationManager locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        Location curLocation = null;
        for (String curProvider : Arrays.asList(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER,
                LocationManager.PASSIVE_PROVIDER)) {
            Location location = locationManager.getLastKnownLocation(curProvider);
            if (location != null) {
                curLocation = location;
                break;
            }
        }

        if (curLocation != null) {
            updateWeatherTask.execute(curLocation.getLatitude() + "," + curLocation.getLongitude());
        } else {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshWeatherInfo();
    }

    private class UpdateWeatherTask extends AsyncTask<String, Integer, String> {
        private final WorldWeatherService weatherService = new WorldWeatherService(WeatherActivity.this);

        @Override
        protected String doInBackground(String... params) {
            String city = params[0];

            return weatherService.queryWeatherInfo(city);
        }

        @Override
        protected void onPostExecute(final String response) {
            setContentView(R.layout.main);
            View imageView = (View) findById(View.class, R.id.main_root_view);
            Animation fadeInAnimation = AnimationUtils.loadAnimation(WeatherActivity.this, R.anim.main_layout_starting);
            imageView.startAnimation(fadeInAnimation );

            CurrentWeather curWeather = weatherService.parseCurrentWeather(response);
            setCurWeather(curWeather);

            List<WeatherItem> weatherItems = weatherService.parseWeatherItemList(response);
            findById(ListView.class, R.id.weather_items).setAdapter(new WeatherItemsAdapter(WeatherActivity.this,
                    R.layout.weather_item, weatherItems));
        }

    }

    @SuppressWarnings("unchecked")
    <T> T findById(Class<T> type, int id) {
        return (T) findViewById(id);
    }

    private void setCurWeather(CurrentWeather curWeather) {
        findById(TextView.class, R.id.cur_temperature).setText(Integer.toString(curWeather.temperature));
        findById(TextView.class, R.id.cur_weather_summary).setText(curWeather.summary);
        findById(TextView.class, R.id.cur_wind_direction).setText(curWeather.windDirection);

        String windSpeed = getString(R.string.wind_speed_pattern, curWeather.windSpeed);
        findById(TextView.class, R.id.cur_wind_speed).setText(windSpeed);
    }

}
