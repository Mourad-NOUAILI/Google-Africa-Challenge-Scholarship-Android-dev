package android.sunshine.Services;

import android.content.Context;
import android.os.AsyncTask;
import android.sunshine.Utilities.JsonUtils;
import android.sunshine.Utilities.NetworkUtils;
import android.sunshine.data.AppDataBase;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.json.JSONException;

import java.net.URL;

public class WeatherForcastFirebaseJobService extends JobService{
    private AsyncTask background_forcast;
    @Override
    public boolean onStartJob(final JobParameters job) {
        background_forcast = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = WeatherForcastFirebaseJobService.this;
                URL url = NetworkUtils.build_url_bases_on_location_query(context);
                String json_string = NetworkUtils.get_response_from_url(url);
                /*if (json_string == null) {
                    AppDataBase database = AppDataBase.get_instance(context);
                    database.weatherDao().delete_all();
                    return null;
                }*/
                try {
                    JsonUtils.get_weather_strings_from_json(context, json_string);
                    jobFinished(job, false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job, false);
            }
        };

        background_forcast.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (background_forcast != null)
            background_forcast.cancel(true);
        return true;
    }
}
