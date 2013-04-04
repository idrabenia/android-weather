package idrabenia.weather.ui.gadget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import idrabenia.weather.R;
import idrabenia.weather.domain.weather.CurrentWeather;
import idrabenia.weather.service.WorldWeatherClient;
import idrabenia.weather.ui.activity.weather.WeatherActivity;
import idrabenia.weather.ui.service.WeatherService;


/**
 * @author Ilya Drabenia
 * @since 24.03.13
 */
public class WeatherGadgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WeatherService.start(context);

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, WeatherGadgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_gadget);

            SharedPreferences preferences = context.getSharedPreferences("weather.cache", Context.MODE_PRIVATE);
            String info = preferences.getString("weather_json", "");

            WorldWeatherClient weatherProvider = new WorldWeatherClient(context);
            CurrentWeather curWeather = weatherProvider.parseCurrentWeather(info);

            // Set the text
            remoteViews.setTextViewText(R.id.cur_temperature, Integer.toString(curWeather.temperature));
            remoteViews.setTextViewText(R.id.cur_weather_summary, curWeather.summary);
            remoteViews.setTextViewText(R.id.cur_wind_direction, curWeather.windDirection);

            String windSpeed = context.getString(R.string.wind_speed_pattern, curWeather.windSpeed);
            remoteViews.setTextViewText(R.id.cur_wind_speed, windSpeed);

            // Register an onClickListener
            Intent intent = new Intent(context, WeatherActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.main_root_view, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
