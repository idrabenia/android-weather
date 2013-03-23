package idrabenia.weather.service.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.List;

/**
 * @author Ilya Drabenia
 * @since 21.03.13
 */
public class LocationService {
    private final Context context;

    public LocationService(Context context) {
        this.context = context;
    }

    private LocationManager getLocationManager() {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    private LocationProvider getNetworkProvider() {
        return (LocationProvider) getLocationManager().getProvider(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isNetworkProviderAvailable() {
        Criteria a = new Criteria();

        List<String> enabledProviders = getLocationManager().getProviders(true);

        for (String curProvider : enabledProviders) {
            if (curProvider.equals(LocationManager.NETWORK_PROVIDER)) {
                return true;
            }
        }

        return false;
    }

    private Location getPassiveLocation() {
        return getLocationManager().getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    private void requestLocation(final LocationListener listener) {
        getLocationManager().requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0,
                new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                listener.onLocationReceived(location);

                final android.location.LocationListener self = this;
                getLocationManager().removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // NOOP
            }

            @Override
            public void onProviderEnabled(String provider) {
                // NOOP
            }

            @Override
            public void onProviderDisabled(String provider) {
                // NOOP
            }
        });
    }

    private void notifyListenerAsync(final LocationListener listener, final Location lastKnownLocation) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                listener.onLocationReceived(lastKnownLocation);
            }
        });
    }

    public void getCurrentLocationAsync(final LocationListener listener) {
        if (!isNetworkProviderAvailable()) {
            return;
        }

        final Location lastKnownLocation = getLocationManager().getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastKnownLocation != null) {
            notifyListenerAsync(listener, lastKnownLocation);
        } else {
            requestLocation(listener);
        }
    }

    public boolean isLocationAvailable() {
        return isNetworkProviderAvailable();
    }

}
