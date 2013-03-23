package idrabenia.weather.service.rest;

import android.util.Log;
import org.apache.commons.lang.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Ilya Drabenia
 * @since 23.03.13
 */
public class RestClient {
    private final HttpClient httpClient;

    public RestClient() {
        httpClient = new DefaultHttpClient();
    }

    public String getJson(String requestUrl) {
        HttpResponse response;
        try {
            HttpGet getWeatherRequest = new HttpGet(requestUrl);
            response = httpClient.execute(getWeatherRequest);
        } catch (IOException ex) {
            throw new NetworkException(ex);
        }

        return toString(response);
    }

    public void dispose() {
        try {
            httpClient.getConnectionManager().shutdown();
        } catch (Exception ex) {
            Log.e(RestClient.class.toString(), "Exception in RestClient dispose", ex);
        }
    }

    private String toString(final HttpResponse response) {
        try {
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                    "UTF-8"));

            StringBuilder builder = new StringBuilder();
            String curLine;
            while ((curLine = responseReader.readLine()) != null) {
                builder.append(curLine);
            }

            return builder.toString();
        } catch (IOException ex) {
            throw new ParseResponseException(ex);
        }
    }

    public static String withClient(RestClientAction action) {
        Validate.notNull(action);

        RestClient restClient = new RestClient();
        try {
            return action.call(restClient);
        } finally {
            restClient.dispose();
        }
    }

}
