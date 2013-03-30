package idrabenia.weather.ui.activity.weather;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import idrabenia.weather.R;
import idrabenia.weather.domain.weather.WeatherItem;

import java.util.List;

/**
 * @author Ilya Drabenia
 * @since 17.03.13
 */
public class WeatherItemsAdapter extends ArrayAdapter<WeatherItem> {

    public WeatherItemsAdapter(Context context, int textViewResourceId, List<WeatherItem> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.weather_item, null);
        }

        WeatherItem curItem = this.getItem(position);
        if (curItem != null) {
            setWeatherImage(v, curItem.imageUrl.replaceAll(".*\\/", "").replaceAll("\\..*", ""));
            setTemperature(v, curItem.minTemperature, curItem.maxTemperature);
            setItemTitle(v, curItem.date, curItem.description);
        }
        return v;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }

    ImageView getWeatherImage(View v) {
        return (ImageView) v.findViewById(R.id.current_weather_image);
    }

    void setWeatherImage(View v, String imageName) {
        Resources resources = getContext().getResources();
        int imageId = resources.getIdentifier(imageName, "drawable", this.getContext().getPackageName());

        getWeatherImage(v).setImageBitmap(getResizedBitmap(BitmapFactory.decodeResource(resources,
                imageId), 140, 140));
    }

    TextView getItemTitleLabel(View v) {
        return (TextView) v.findViewById(R.id.item_title_label);
    }

    void setItemTitle(View v, String date, String description) {
        getItemTitleLabel(v).setText(getContext().getString(R.string.location_label_pattern, date, description));
    }

    TextView getTemperatureLabel(View v) {
        return (TextView) v.findViewById(R.id.temperature_label);
    }

    void setTemperature(View v, int minTemperature, int maxTemperature) {
        String labelText = getContext().getString(R.string.temperature_label_pattern, minTemperature, maxTemperature);
        getTemperatureLabel(v).setText(labelText);
    }
}
