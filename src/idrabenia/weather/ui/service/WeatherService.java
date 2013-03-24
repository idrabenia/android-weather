package idrabenia.weather.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Ilya Drabenia
 * @since 24.03.13
 */
public class WeatherService extends Service {

    @Override
    public void onCreate() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.w("WeatherService", "YEEEEEP! Weather service started!");
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
