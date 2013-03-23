package idrabenia.weather.ui.weather;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.*;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import idrabenia.weather.R;
import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.service.location.LocationListener;
import idrabenia.weather.service.location.LocationService;
import idrabenia.weather.ui.CrashDialogActivity;
import idrabenia.weather.ui.weather.update.UpdateWeatherTask;

public class WeatherActivity extends Activity implements LocationListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(CrashDialogActivity.buildExceptionHandler(this));

        setContentView(R.layout.waiting_screen);
        refreshWeatherInfo();
    }

    void refreshWeatherInfo() {
        LocationService locationService = new LocationService(this);

        if (locationService.isLocationAvailable()) {
            locationService.getCurrentLocationAsync(this);
        } else {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        }
    }

    @Override
    public void onLocationReceived(Location location) {
        new UpdateWeatherTask(this).execute(location);
    }

    /**
     * Calls after location provider settings dialog finished
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshWeatherInfo();
    }

    @SuppressWarnings("unchecked")
    public <T> T findById(Class<T> type, int id) {
        return (T) findViewById(id);
    }

    public void setCurWeather(CurrentWeather curWeather) {
        findById(TextView.class, R.id.cur_temperature).setText(Integer.toString(curWeather.temperature));
        findById(TextView.class, R.id.cur_weather_summary).setText(curWeather.summary);
        findById(TextView.class, R.id.cur_wind_direction).setText(curWeather.windDirection);

        String windSpeed = getString(R.string.wind_speed_pattern, curWeather.windSpeed);
        findById(TextView.class, R.id.cur_wind_speed).setText(windSpeed);
    }

    public void hideWaitingScreen() {
        if (findViewById(R.id.waiting_screen) != null) {
            setContentView(R.layout.main);

            View imageView = findById(View.class, R.id.main_root_view);
            Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.main_layout_starting);
            imageView.startAnimation(fadeInAnimation);
        }
    }

}
