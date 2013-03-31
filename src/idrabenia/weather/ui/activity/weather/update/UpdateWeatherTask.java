package idrabenia.weather.ui.activity.weather.update;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import idrabenia.weather.R;
import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.domain.weather.WeatherItem;
import idrabenia.weather.service.WorldWeatherClient;
import idrabenia.weather.service.rest.NetworkException;
import idrabenia.weather.ui.activity.CrashDialogActivity;
import idrabenia.weather.ui.activity.weather.WeatherActivity;

import java.util.List;

/**
 * @author Ilya Drabenia
 * @since 23.03.13
 */
public class UpdateWeatherTask extends AsyncTask<Location, Integer, UpdateResult> {
    protected final WorldWeatherClient weatherClient;
    protected final WeatherActivity activity;

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
            CurrentWeather curWeather = weatherClient.parseCurrentWeather(result.response);
            List<WeatherItem> items = weatherClient.parseWeatherItemList(result.response);
            onSuccessWeatherResult(curWeather, items);

            // cache cur weather
            activity.getSharedPreferences("weather.cache", Context.MODE_PRIVATE).edit()
                    .putString("weather_json", result.response).commit();
        }

        if (result.networkException != null) {
            CrashDialogActivity.processCriticalError(activity, result.networkException, R.string.network_error);
        }
    }

    protected void onSuccessWeatherResult(CurrentWeather curWeather, List<WeatherItem> items) {
        activity.hideWaitingScreen();
        activity.setWeather(curWeather, items);
        activity.setWaitingBackgroundTask(false);
    }

}
