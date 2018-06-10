package android.sunshine.data;

import android.arch.lifecycle.LiveData;
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
    LiveData<WeatherEntry[]> load_all_days();

    @Query("SELECT * FROM weather_details WHERE date_time = :date_time")
    WeatherEntry load_a_day(long date_time);

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
