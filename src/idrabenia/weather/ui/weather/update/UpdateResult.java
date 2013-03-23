package idrabenia.weather.ui.weather.update;

import idrabenia.weather.service.rest.NetworkException;

/**
 * @author Ilya Drabenia
 * @since 23.03.13
 */
public class UpdateResult {
    public String response;
    public NetworkException networkException;

    public boolean hasSuccessResult() {
        return response != null && networkException == null;
    }
}
