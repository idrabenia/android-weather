package idrabenia.weather.service;

import idrabenia.weather.domain.WeatherApplicationException;

/**
 * @author: Ilya Drabenia
 * @since 16.03.13
 */
public class ExceptionHandlingTemplate {

    public interface Action<T> {

        public T execute() throws Exception;

    }

    public static <T> T executeWithExceptionHandler(Action<T> action) {
        if (action == null) {
            throw new IllegalArgumentException("action == null");
        }

        try {
            return action.execute();
        } catch (Exception ex) {
            throw new WeatherApplicationException(ex);
        }
    }

}
