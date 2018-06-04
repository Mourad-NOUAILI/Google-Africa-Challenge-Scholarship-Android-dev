package android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.sunshine.Services.WeatherForcastFirebaseJobService;
import android.sunshine.Services.WeatherForcastingIntentService;
import android.sunshine.Utilities.NetworkUtils;
import android.sunshine.Utilities.ScheduleForcastingUtils;
import android.sunshine.Utilities.SharedPreferenceUtils;
import android.sunshine.data.AppDataBase;
import android.sunshine.data.WeatherEntry;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import android.sunshine.data.WeatherDao;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<WeatherEntry[]>{

    AppDataBase database;

    WeatherEntry[] weather_data;

    TextView tv;

    private AsyncTask database_in_background;

    private int LOADER_ID = 0;
    private static final String LOAD_FROM_DATABASE = "load";

    int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.test_text_view);

        //ScheduleForcastingUtils.schedule_forcasting(this);


            /*database_in_background = new AsyncTask() {
                @Override
                protected void onPreExecute() {

                        Intent i = new Intent(getApplicationContext(), WeatherForcastingIntentService.class);
                        startService(i);

                }

                @Override
                protected Object doInBackground(Object[] objects) {
                    database = AppDataBase.get_instance(getApplicationContext());
                    weather_data = database.weatherDao().load_all_days();
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    for (WeatherEntry e: weather_data)
                            tv.append(e.getDate_time()+ "--" + e.getCity_name()+ "\n");

                }
            };

        database_in_background.execute();*/


        LoaderManager.LoaderCallbacks<WeatherEntry[]> callback = MainActivity.this;

        Bundle load_bundle = new Bundle();
        load_bundle.putString(LOAD_FROM_DATABASE, "load-from-database");

        // Getting the support library version of the loaderManager
        LoaderManager loader_manager = getSupportLoaderManager();

        // Get the loader
        Loader<String> weather_forcast_loader = loader_manager.getLoader(LOADER_ID);

        // If the loader is null, the search is never done before
        if(weather_forcast_loader == null)
            loader_manager.initLoader(LOADER_ID, load_bundle, callback);
            // Otherwise, we had a result, just reload it.
        else
            loader_manager.restartLoader(LOADER_ID, load_bundle, callback);








        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settings_activity = new Intent(this, SettingsActivity.class);
            startActivity(settings_activity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        database_in_background = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                database = AppDataBase.get_instance(getApplicationContext());
                database.weatherDao().delete_all();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                Intent i = new Intent(getApplicationContext(), WeatherForcastingIntentService.class);
                startService(i);
            }
        };

        database_in_background.execute();
    }

    @NonNull
    @Override
    public Loader<WeatherEntry[]> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<WeatherEntry[]>(this) {
            @Override
            protected void onStartLoading() {
                Intent i = new Intent(getApplicationContext(), WeatherForcastingIntentService.class);
                startService(i);
                super.onStartLoading();
                if (args == null)
                    return;
                if (weather_data == null){
                    forceLoad();
                }
                else
                    deliverResult(weather_data);
            }

            @Override
            public void deliverResult(@Nullable WeatherEntry[] data) {
                weather_data = data;
                super.deliverResult(data);
            }

            @Nullable
            @Override
            public WeatherEntry[] loadInBackground() {
                database = AppDataBase.get_instance(getApplicationContext());
                a = database.weatherDao().count();
                weather_data = database.weatherDao().load_all_days();
                return weather_data;
            }

        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<WeatherEntry[]> loader, WeatherEntry[] data) {
        tv.setText("mmmmmm: " + a+"\n");
        if (data == null || data.length == 0)
            tv.append(data.length+"\n");
        else
            for (WeatherEntry e: data)
                tv.append(e.getDate_time()+ "--" + e.getCity_name()+ "\n");

    }

    @Override
    public void onLoaderReset(@NonNull Loader<WeatherEntry[]> loader) {

    }
}
