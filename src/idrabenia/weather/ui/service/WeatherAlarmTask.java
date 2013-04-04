package idrabenia.weather.ui.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import idrabenia.weather.R;
import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.domain.weather.WeatherItem;
import idrabenia.weather.domain.weather.alarm.*;
import idrabenia.weather.service.ExceptionHandlingTemplate;
import idrabenia.weather.service.WorldWeatherClient;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ilya Drabenia
 * @since 04.04.13
 */
public class WeatherAlarmTask extends TimerTask {
    private final Timer timer;
    private final Context context;
    private final Condition[] conditions = new Condition[] {
            new TodayColdCondition(),
            new TodayWarnCondition(),
            new TodayWindyCondition(),
            new TomorrowColdCondition(),
            new TomorrowWarmCondition()
    };
    private final AtomicInteger notificationCounter = new AtomicInteger(0);

    public WeatherAlarmTask(Context context, Timer timer) {
        this.timer = timer;
        this.context = context;
    }

    @Override
    public void run() {
        ExceptionHandlingTemplate.ignoreExceptions(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                SharedPreferences preferences = context.getSharedPreferences("weather.cache", Context.MODE_PRIVATE);
                String info = preferences.getString("weather_json", "");
                WorldWeatherClient client = new WorldWeatherClient(context);
                CurrentWeather curWeather = client.parseCurrentWeather(info);
                List<WeatherItem> weatherItems = client.parseWeatherItemList(info);

                for (Condition curCondition : conditions) {
                    if (curCondition.isPerformed(curWeather, weatherItems)) {
                        sendNotification(curCondition.getMessageId());
                    }
                }

                return null;
            }
        });

        ExceptionHandlingTemplate.ignoreExceptions(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                timer.schedule(new WeatherAlarmTask(context, timer), 1000 * 60 * 60 * 6);
                return null;
            }
        });
    }

    private void sendNotification(int messageId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Service.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationCounter.incrementAndGet(), makeNotification(messageId));
    }

    private Notification makeNotification(int messageId) {
        return new Notification.Builder(context)
                .setContentTitle("Weather Alarm")
                .setContentText(context.getString(messageId))
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .getNotification();
    }

}
