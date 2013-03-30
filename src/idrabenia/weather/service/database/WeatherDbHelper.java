package idrabenia.weather.service.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import idrabenia.weather.R;

/**
 * @author Ilya Drabenia
 * @since 24.03.13
 */
public class WeatherDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "weather.db";
    private static final int DATABASE_VERSION = 1;
    private final String[] DATABASE_DDL;

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DATABASE_DDL = context.getResources().getStringArray(R.array.weather_db_ddl);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String curStatement : DATABASE_DDL) {
            db.execSQL(curStatement);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do not needed
    }
}
