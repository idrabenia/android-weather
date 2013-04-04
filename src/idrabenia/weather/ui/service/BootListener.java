package idrabenia.weather.ui.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Ilya Drabenia
 * @since 24.03.13
 */
public class BootListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, WeatherService.class));
    }

}
