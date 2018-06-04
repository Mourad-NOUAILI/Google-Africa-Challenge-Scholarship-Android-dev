package android.sunshine.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {WeatherEntry.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase{
    private final static Object LOCK = new Object();
    private static final String DATABASE_NAME = "weather_forecasting";
    private static AppDataBase db_instance;

    public static AppDataBase get_instance(Context context){
        if (db_instance == null){
            synchronized (LOCK) {
                db_instance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDataBase.class, AppDataBase.DATABASE_NAME)
                        .build();
            }
        }
        return db_instance;
    }

    public abstract WeatherDao weatherDao();
}
