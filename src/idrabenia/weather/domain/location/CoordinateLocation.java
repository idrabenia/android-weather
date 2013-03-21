package idrabenia.weather.domain.location;

import android.app.Service;
import android.content.Context;
import android.location.LocationManager;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ilya Drabenia
 * @since 21.03.13
 */
public class CoordinateLocation extends Location {
    public final double latitude;
    public final double longitude;

    private CoordinateLocation(double latitudeValue, double longitudeValue) {
        latitude = latitudeValue;
        longitude = longitudeValue;
    }

    public static Location getCurrentLocation(Context curContext) {
        LocationManager locationManager = (LocationManager) curContext.getSystemService(Service.LOCATION_SERVICE);

        List<String> providers = Arrays.asList(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER,
                LocationManager.PASSIVE_PROVIDER);
        for (String curProvider : providers) {
            android.location.Location location = locationManager.getLastKnownLocation(curProvider);
            if (location != null) {
                return new CoordinateLocation(location.getLatitude(), location.getLongitude());
            }
        }

        return null;
    }

}
