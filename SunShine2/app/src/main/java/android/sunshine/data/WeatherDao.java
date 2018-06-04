package android.sunshine.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface WeatherDao {
    @Query("SELECT * FROM weather_details ORDER by date_time")
    WeatherEntry[] load_all_days();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert_a_day(WeatherEntry day_details);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update_a_day(WeatherEntry day_details);

    @Delete
    void delete_a_day(WeatherEntry day_details);

    @Query("DELETE FROM weather_details")
    void delete_all();

    @Query("SELECT COUNT(date_time) FROM weather_details")
    int count();

}
