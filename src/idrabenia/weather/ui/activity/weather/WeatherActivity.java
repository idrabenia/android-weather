package idrabenia.weather.ui.activity.weather;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
import idrabenia.weather.R;
import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.domain.weather.WeatherItem;
import idrabenia.weather.service.location.LocationListener;
import idrabenia.weather.service.location.LocationService;
import idrabenia.weather.ui.activity.AnimationListenerSkeleton;
import idrabenia.weather.ui.activity.CrashDialogActivity;
import idrabenia.weather.ui.activity.SettingsActivity;
import idrabenia.weather.ui.activity.weather.update.ScheduledUpdateWeatherTask;
import idrabenia.weather.ui.activity.weather.update.UpdateWeatherTask;

import java.util.List;

public class WeatherActivity extends Activity {
    private boolean isWaitingBackgroundTask = false;

    public boolean isWaitingBackgroundTask() {
        return isWaitingBackgroundTask;
    }

    public void setWaitingBackgroundTask(boolean waitingBackgroundTask) {
        isWaitingBackgroundTask = waitingBackgroundTask;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(CrashDialogActivity.buildExceptionHandler(this));

        setContentView(R.layout.waiting_screen);
        startUpdateWeatherTask(new ScheduledUpdateWeatherTask(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_screen, menu);
        menu.findItem(R.id.menu_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return showSettings();
            }
        });

        menu.findItem(R.id.menu_refresh).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return refresh();
            }
        });

        return true;
    }

    private boolean showSettings() {
        startActivity(new Intent(WeatherActivity.this, SettingsActivity.class));
        return true;
    }

    private boolean refresh() {
        if (!isWaitingBackgroundTask()) {
            setWaitingBackgroundTask(true);

            Animation animation = AnimationUtils.loadAnimation(WeatherActivity.this, R.anim.hide_weather_screen);
            animation.setAnimationListener(new AnimationListenerSkeleton() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    setContentView(R.layout.waiting_screen);
                    startUpdateWeatherTask(new UpdateWeatherTask(WeatherActivity.this));
                }
            });
            findById(View.class, R.id.main_root_view).startAnimation(animation);
        }

        return true;
    }

    public void startUpdateWeatherTask(final UpdateWeatherTask updateWeatherTask) {
        setWaitingBackgroundTask(true);
        LocationService locationService = new LocationService(this);

        if (locationService.isLocationAvailable()) {
            locationService.getCurrentLocationAsync(new LocationListener() {
                @Override
                public void onLocationReceived(Location location) {
                    updateWeatherTask.execute(location);
                }
            });
        } else {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        }
    }

    /**
     * Calls after location provider settings dialog finished
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        startUpdateWeatherTask(new ScheduledUpdateWeatherTask(this));
    }

    @SuppressWarnings("unchecked")
    public <T> T findById(Class<T> type, int id) {
        return (T) findViewById(id);
    }

    private void setCurWeather(CurrentWeather curWeather) {
        findById(TextView.class, R.id.cur_temperature).setText(Integer.toString(curWeather.temperature));
        findById(TextView.class, R.id.cur_weather_summary).setText(curWeather.summary);
        findById(TextView.class, R.id.cur_wind_direction).setText(curWeather.windDirection);

        String windSpeed = getString(R.string.wind_speed_pattern, curWeather.windSpeed);
        findById(TextView.class, R.id.cur_wind_speed).setText(windSpeed);
    }

    public void setWeather(CurrentWeather curWeather, List<WeatherItem> items) {
        setCurWeather(curWeather);

        ListView listView = findById(ListView.class, R.id.weather_items);
        listView.setAdapter(new WeatherItemsAdapter(this, R.layout.weather_item, items));
    }

    public void hideWaitingScreen() {
        if (isWaitingScreenShown()) {
            setContentView(R.layout.weather_screen);

            Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.show_weather_screen);
            findById(View.class, R.id.main_root_view).startAnimation(fadeInAnimation);
        }
    }

    private boolean isWaitingScreenShown() {
        return findViewById(R.id.waiting_screen) != null;
    }

}
