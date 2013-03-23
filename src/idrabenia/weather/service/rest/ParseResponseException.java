package idrabenia.weather.service.rest;

/**
 * @author Ilya Drabenia
 * @since 23.03.13
 */
public class ParseResponseException extends RuntimeException {

    public ParseResponseException() {
    }

    public ParseResponseException(String detailMessage) {
        super(detailMessage);
    }

    public ParseResponseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ParseResponseException(Throwable throwable) {
        super(throwable);
    }

}
