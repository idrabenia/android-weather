package idrabenia.weather.ui.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import idrabenia.weather.R;
import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.domain.weather.WeatherItem;
import idrabenia.weather.service.WorldWeatherClient;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

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
