package idrabenia.weather.ui.activity.weather.update;

import android.preference.PreferenceManager;
import android.util.Log;
import idrabenia.weather.R;
import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.domain.weather.WeatherItem;
import idrabenia.weather.ui.activity.weather.WeatherActivity;

import java.util.List;
import java.util.TimerTask;

/**
 * @author Ilya Drabenia
 * @since 31.03.13
 */
public class ScheduledUpdateWeatherTask extends UpdateWeatherTask {

    public ScheduledUpdateWeatherTask(WeatherActivity activityValue) {
        super(activityValue);
    }

    @Override
    protected void onSuccessWeatherResult(CurrentWeather curWeather, List<WeatherItem> items) {
        super.onSuccessWeatherResult(curWeather, items);

        scheduleRefreshWeatherTask();
    }

    private void scheduleRefreshWeatherTask() {
        String updateInterval = PreferenceManager.getDefaultSharedPreferences(activity)
                .getString("update_interval", activity.getString(R.string.update_weather_delay));

        try {
            activity.TIMER.schedule(new TimerTask() {
                @Override
                public void run() {
                    activity.startUpdateWeatherTask(new ScheduledUpdateWeatherTask(activity));
                }
            }, Integer.parseInt(updateInterval));
        } catch (IllegalStateException ex) {
            Log.w("Weather", "Could not schedule task on cancelled timer");
        }
    }

}
