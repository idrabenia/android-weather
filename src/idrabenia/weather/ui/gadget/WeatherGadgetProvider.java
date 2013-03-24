package idrabenia.weather.ui.gadget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.RemoteViews;
import idrabenia.weather.R;
import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.service.ExceptionHandlingTemplate;
import idrabenia.weather.service.WorldWeatherClient;
import idrabenia.weather.service.location.LocationListener;
import idrabenia.weather.service.location.LocationService;
import idrabenia.weather.ui.activity.WeatherActivity;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @author Ilya Drabenia
 * @since 24.03.13
 */
public class WeatherGadgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Get all ids
        ComponentName thisWidget = new ComponentName(context, WeatherGadgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_gadget);

            final CountDownLatch waitUpdateLatch = new CountDownLatch(1);
            new LocationService(context).getCurrentLocationAsync(new LocationListener() {
                @Override
                public void onLocationReceived(Location location) {
                    WorldWeatherClient weatherProvider = new WorldWeatherClient(context);
                    String info = weatherProvider.queryWeatherInfo(location);

                    CurrentWeather curWeather = weatherProvider.parseCurrentWeather(info);

                    // Set the text
                    remoteViews.setTextViewText(R.id.cur_temperature, Integer.toString(curWeather.temperature));
                    //Integer.toString(curWeather.temperature));
                    remoteViews.setTextViewText(R.id.cur_weather_summary, curWeather.summary);
                    remoteViews.setTextViewText(R.id.cur_wind_direction, curWeather.windDirection);

                    String windSpeed = context.getString(R.string.wind_speed_pattern, curWeather.windSpeed);
                    remoteViews.setTextViewText(R.id.cur_wind_speed, windSpeed);

                    waitUpdateLatch.countDown();
                }
            });
            ExceptionHandlingTemplate.ignoreExceptions(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    waitUpdateLatch.await();
                    return null;
                }
            });

            // Register an onClickListener
            Intent intent = new Intent(context, WeatherActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.main_root_view, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
