package idrabenia.weather.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import idrabenia.weather.R;
import idrabenia.weather.service.ExceptionHandlingTemplate;

import java.util.concurrent.Callable;

/**
 * @author Ilya Drabenia
 * @since 23.03.13
 */
public class CrashDialogActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setMessage(getIntent().getExtras().getInt("message"))
                .setTitle(R.string.critical_error)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create().show();
    }

    public static void processCriticalError(final Activity parentActivity, final Throwable ex, final int messageId) {
        ExceptionHandlingTemplate.ignoreExceptions(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    Log.e("WeatherActivity", "Critical error", ex);

                    Intent crashIntent = new Intent(parentActivity, CrashDialogActivity.class);

                    Bundle crashBundle = new Bundle();
                    crashBundle.putInt("message", messageId);
                    crashIntent.putExtras(crashBundle);

                    parentActivity.startActivity(crashIntent);

                    return null;
                } finally {
                    // Try everything to make sure this process goes away.
                    parentActivity.finish();
                }
            }
        });
    }

    public static Thread.UncaughtExceptionHandler buildExceptionHandler(final Activity parentActivity) {
        return new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                CrashDialogActivity.processCriticalError(parentActivity, ex, R.string.unexpected_error_message);
            }
        };
    }

}