package idrabenia.weather.ui.weather.update;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import idrabenia.weather.R;
import idrabenia.weather.domain.WeatherApplicationException;
import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.domain.weather.WeatherItem;
import idrabenia.weather.service.WorldWeatherClient;
import idrabenia.weather.service.rest.NetworkException;
import idrabenia.weather.ui.weather.WeatherActivity;
import idrabenia.weather.ui.weather.WeatherItemsAdapter;

import java.util.List;

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
            activity.setCurWeather(curWeather);

            List<WeatherItem> weatherItems = weatherClient.parseWeatherItemList(result.response);
            activity.findById(ListView.class, R.id.weather_items).setAdapter(new WeatherItemsAdapter(activity,
                    R.layout.weather_item, weatherItems));
        }

        if (result.networkException != null) {
            activity.processCriticalError(result.networkException, R.string.network_error);
        }
    }

}
