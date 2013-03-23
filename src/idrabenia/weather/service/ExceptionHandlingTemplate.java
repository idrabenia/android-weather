package idrabenia.weather.service;

import android.util.Log;
import idrabenia.weather.domain.WeatherApplicationException;

import java.util.concurrent.Callable;

/**
 * @author: Ilya Drabenia
 * @since 16.03.13
 */
public class ExceptionHandlingTemplate {

    public static <T> T withExceptionWrapper(Callable<T> action) {
        if (action == null) {
            throw new IllegalArgumentException("action == null");
        }

        try {
            return action.call();
        } catch (Exception ex) {
            throw new WeatherApplicationException(ex);
        }
    }

    public static <T> T ignoreExceptions(Callable<T> action) {
        try {
            return action.call();
        } catch (Exception ex) {
            Log.e("", "", ex);
            return null;
        }
    }

}
