package android.sunshine.Utilities;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.sunshine.data.AppDataBase;
import android.sunshine.data.WeatherEntry;
import android.support.annotation.NonNull;

public class ViewModelUtils extends AndroidViewModel {

    private LiveData<WeatherEntry[]> weather_data;

    public ViewModelUtils(@NonNull Application application) {
        super(application);
        AppDataBase database = AppDataBase.get_instance(this.getApplication());
        weather_data = database.weatherDao().load_all_days();
    }

    public LiveData<WeatherEntry[]> getWeather_data() {
        return weather_data;
    }
}
