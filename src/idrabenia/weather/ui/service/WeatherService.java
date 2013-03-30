package idrabenia.weather.ui.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import idrabenia.weather.service.WorldWeatherClient;
import idrabenia.weather.service.location.LocationListener;
import idrabenia.weather.service.location.LocationService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Ilya Drabenia
 * @since 24.03.13
 */
public class WeatherService extends Service {

    @Override
    public void onCreate() {
        final Context context = getApplicationContext();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                new LocationService(context).getCurrentLocationAsync(new LocationListener() {
                    @Override
                    public void onLocationReceived(Location location) {
                        WorldWeatherClient weatherProvider = new WorldWeatherClient(context);
                        String response = weatherProvider.queryWeatherInfo(location);

                        // cache cur weather
                        context.getSharedPreferences("weather.cache", Context.MODE_PRIVATE).edit()
                                .putString("weather_json", response).commit();
                    }
                });
            }
        }, 1000, 1000);

        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
