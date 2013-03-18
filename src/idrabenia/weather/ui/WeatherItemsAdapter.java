package idrabenia.weather.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import idrabenia.weather.R;
import idrabenia.weather.domain.WeatherItem;
import idrabenia.weather.service.ExceptionHandlingTemplate;

import java.io.InputStream;
import java.net.URL;
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
            String imageName = curItem.imageUrl.replaceAll(".*\\/", "").replaceAll("\\..*", "");
            Resources resources = getContext().getResources();
            int imageId = resources.getIdentifier(imageName, "drawable", this.getContext().getPackageName());

            getCurrentWeatherImage(v).setImageBitmap(getResizedBitmap(BitmapFactory.decodeResource(resources,
                    imageId), 140, 140));
            setTemperature(v, curItem.minTemperature, curItem.maxTemperature);
            setItemTitle(v, curItem.date, curItem.description);
        }
        return v;
    }


    private class LoadImageTask extends AsyncTask<String, Integer, Bitmap> {
        private final View view;

        private LoadImageTask(View v) {
            view = v;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            final String url = params[0];

            return ExceptionHandlingTemplate.executeWithExceptionHandler(
                    new ExceptionHandlingTemplate.Action<Bitmap>() {
                        @Override
                        public Bitmap execute() throws Exception {
//                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//                            bmOptions.inSampleSize = 1; // 1 = 100% if you write 4 means 1/4 = 25%
                            return BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                        }
                    });
        }



        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ;
        }
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

    ImageView getCurrentWeatherImage(View v) {
        return (ImageView) v.findViewById(R.id.current_weather_image);
    }

    TextView getItemTitleLabel(View v) {
        return (TextView) v.findViewById(R.id.item_title_label);
    }

    void setItemTitle(View v, String date, String description) {
        getItemTitleLabel(v).setText(getContext().getString(R.string.location_label_pattern, date, description));
    }

    TextView getCurTemperatureLabel(View v) {
        return (TextView) v.findViewById(R.id.temperature_label);
    }

    void setTemperature(View v, int minTemperature, int maxTemperature) {
        String labelText = getContext().getString(R.string.temperature_label_pattern, minTemperature, maxTemperature);
        getCurTemperatureLabel(v).setText(labelText);
    }
}
