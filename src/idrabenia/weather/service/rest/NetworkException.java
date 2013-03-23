package idrabenia.weather.service.rest;

/**
 * @author Ilya Drabenia
 * @since 23.03.13
 */
public class NetworkException extends RuntimeException {

    public NetworkException() {
    }

    public NetworkException(String detailMessage) {
        super(detailMessage);
    }

    public NetworkException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NetworkException(Throwable throwable) {
        super(throwable);
    }
}
