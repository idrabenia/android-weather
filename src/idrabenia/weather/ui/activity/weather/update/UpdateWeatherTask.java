package idrabenia.weather.ui.activity.weather.update;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import idrabenia.weather.R;
import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.domain.weather.WeatherItem;
import idrabenia.weather.service.WorldWeatherClient;
import idrabenia.weather.service.rest.NetworkException;
import idrabenia.weather.ui.activity.CrashDialogActivity;
import idrabenia.weather.ui.activity.weather.WeatherActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Ilya Drabenia
 * @since 23.03.13
 */
public class UpdateWeatherTask extends AsyncTask<Location, Integer, UpdateResult> {
    private final WorldWeatherClient weatherClient;
    private final WeatherActivity activity;

    public UpdateWeatherTask(WeatherActivity activityValue) {
        activity = activityValue;
        weatherClient = new WorldWeatherClient(activityValue);
    }

    @Override
    protected UpdateResult doInBackground(Location... params) {
        Location location = params[0];

        UpdateResult result = new UpdateResult();
        try {
            result.response = weatherClient.queryWeatherInfo(location);
        } catch (NetworkException ex) {
            result.networkException = ex;
        }

        return result;
    }

    @Override
    protected void onPostExecute(final UpdateResult result) {
        if (result.hasSuccessResult()) {
            activity.hideWaitingScreen();

            CurrentWeather curWeather = weatherClient.parseCurrentWeather(result.response);
            List<WeatherItem> items = weatherClient.parseWeatherItemList(result.response);
            activity.setWeather(curWeather, items);

            // cache cur weather
            activity.getSharedPreferences("weather.cache", Context.MODE_PRIVATE).edit()
                    .putString("weather_json", result.response).commit();

            scheduleRefreshWeatherTask();
        }

        if (result.networkException != null) {
            CrashDialogActivity.processCriticalError(activity, result.networkException, R.string.network_error);
        }
    }

    private void scheduleRefreshWeatherTask() {
        String updateInterval = PreferenceManager.getDefaultSharedPreferences(activity)
                .getString("update_interval", activity.getString(R.string.update_weather_delay));

        new Timer("Update Weather Timer", true).schedule(new TimerTask() {
            @Override
            public void run() {
                activity.refreshWeatherInfo();
            }
        }, Integer.parseInt(updateInterval));
    }

}
