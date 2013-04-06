package idrabenia.weather.ui.service;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;
import idrabenia.weather.R;
import idrabenia.weather.service.WorldWeatherClient;
import idrabenia.weather.service.location.LocationListener;
import idrabenia.weather.service.location.LocationService;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import static idrabenia.weather.service.ExceptionHandlingTemplate.*;

/**
 * @author Ilya Drabenia
 * @since 03.04.13
 */
public class RefreshWeatherTask extends TimerTask {
    private final Timer ownerTimer;
    private final Context context;

    public RefreshWeatherTask(Context applicationContext, Timer owner) {
        context = applicationContext;
        ownerTimer = owner;
    }

    @Override
    public void run() {
        ignoreExceptions(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                LocationService locationService = new LocationService(context);

                if (locationService.isLocationAvailable()) {
                    updateWeatherAsync(locationService);
                }
                return null;
            }
        });

        ignoreExceptions(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                scheduleUpdateTask();
                return null;
            }
        });
    }

    private void updateWeatherAsync(LocationService locationService) {
        new LocationService(context).getCurrentLocationAsync(new LocationListener() {
            @Override
            public void onLocationReceived(final Location location) {
                ignoreExceptions(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        WorldWeatherClient weatherProvider = new WorldWeatherClient(context);
                        String response = weatherProvider.queryWeatherInfo(location);

                        // cache cur weather
                        context.getSharedPreferences("weather.cache", Context.MODE_PRIVATE).edit()
                                .putString("weather_json", response).commit();
                        return null;
                    }
                });
            }
        });
    }

    private void scheduleUpdateTask() {
        String updateInterval = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("update_interval", context.getString(R.string.update_weather_delay));

        ownerTimer.schedule(new RefreshWeatherTask(context, ownerTimer), Integer.parseInt(updateInterval));
    }

}
