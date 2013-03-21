package idrabenia.weather.domain.location;

import android.content.Context;

/**
 * @author Ilya Drabenia
 * @since 21.03.13
 */
public class Location {

    public static Location getCurrentLocation(Context curContext) {
        Location coordinateLocation = CoordinateLocation.getCurrentLocation(curContext);

        if (coordinateLocation != null) {
            return coordinateLocation;
        }

        //Location countryNamelocation =
        return null;
    }

}
