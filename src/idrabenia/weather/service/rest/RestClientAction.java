package idrabenia.weather.service.rest;

import java.util.concurrent.Callable;

/**
 * @author Ilya Drabenia
 * @since 23.03.13
 */
public interface RestClientAction {

    public String call(RestClient client);

}
