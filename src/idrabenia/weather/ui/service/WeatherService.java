package idrabenia.weather.ui.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;

/**
 * @author Ilya Drabenia
 * @since 24.03.13
 */
public class WeatherService extends Service {
    private final Timer timer = new Timer("Weather Service Timer");

    @Override
    public void onCreate() {
        startUpdateWeatherJob();
        startWeatherAlertJob();

        super.onCreate();
    }

    private void startUpdateWeatherJob() {
        timer.schedule(new RefreshWeatherTask(getApplicationContext(), timer), 0);
    }

    private void startWeatherAlertJob() {
        timer.schedule(new WeatherAlarmTask(this, timer), 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void start(Context context) {
        context.startService(new Intent(context, WeatherService.class));
    }

}
