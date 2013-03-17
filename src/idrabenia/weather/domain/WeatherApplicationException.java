package idrabenia.weather.domain;

/**
 * @author: Ilya Drabenia
 * @since 16.03.13
 */
public class WeatherApplicationException extends RuntimeException {

    public WeatherApplicationException() {
        super();
    }

    public WeatherApplicationException(String detailMessage) {
        super(detailMessage);
    }

    public WeatherApplicationException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public WeatherApplicationException(Throwable throwable) {
        super(throwable);
    }
}
