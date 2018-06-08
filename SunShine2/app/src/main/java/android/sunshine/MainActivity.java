package android.sunshine;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.sunshine.Adapters.WeatherInRecyclerView;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.sunshine.data.WeatherDao;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener/*,
        LoaderManager.LoaderCallbacks<WeatherEntry[]>*/,
        WeatherInRecyclerView.a_day_weather_click{
    AppDataBase database;
    WeatherEntry[] weather_data_array;

    /*private AsyncTask database_in_background;

    private int LOADER_ID = 0;
    private static final String LOAD_FROM_DATABASE = "load";*/


    private WeatherInRecyclerView weather_adapter;
    private RecyclerView weather_recycler_view;

    private TextView forecasting_error_msg_text_view;

    // Test click on recycler view
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar action_bar = getSupportActionBar();
        if (action_bar != null){
            action_bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            action_bar.setIcon(R.drawable.ic_sunshine_icon);
        }

        weather_recycler_view = (RecyclerView)
                findViewById(R.id.weather_data_recycler_view);

        forecasting_error_msg_text_view = (TextView)
                findViewById(R.id.error_forecating_text_view);

        LinearLayoutManager linear_layout_manager =
                new LinearLayoutManager(this);

        weather_recycler_view.setLayoutManager(linear_layout_manager);

        weather_recycler_view.setHasFixedSize(true);

        weather_adapter = new WeatherInRecyclerView(this, this );
        weather_recycler_view.setAdapter(weather_adapter);

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



        Intent i = new Intent(getApplicationContext(), WeatherForcastingIntentService.class);
        startService(i);

        database = AppDataBase.get_instance(getApplicationContext());
        loag_all_weather_data();


        /*LoaderManager.LoaderCallbacks<WeatherEntry[]> callback = MainActivity.this;

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
            loader_manager.restartLoader(LOADER_ID, load_bundle, callback);*/




        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    public void loag_all_weather_data(){
        LiveData<WeatherEntry[]> weather_data = database.weatherDao().load_all_days();
        weather_data.observe(this, new Observer<WeatherEntry[]>() {
            @Override
            public void onChanged(@Nullable WeatherEntry[] weatherEntries) {
                weather_adapter.set_weather_data(weatherEntries);
                weather_data_array = weatherEntries;
            }
        });
    }

    public void show_results_views(){
        weather_recycler_view.setVisibility(View.VISIBLE);
        forecasting_error_msg_text_view.setVisibility(View.INVISIBLE);
    }

    public void show_error_views(){
        weather_recycler_view.setVisibility(View.INVISIBLE);
        forecasting_error_msg_text_view.setVisibility(View.VISIBLE);
    }

    void show_map(Uri uri_adress){
        Intent map_intent = new Intent(Intent.ACTION_VIEW);


        map_intent.setData(uri_adress);

        // Verify that this Intent can be launched and then call startActivity
        if (map_intent.resolveActivity(getPackageManager()) != null)
            startActivity(map_intent);
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

        if (id == R.id.action_show_map){
            String location = SharedPreferenceUtils.load_location(this);

            // Use Uri.Builder with the appropriate scheme and query to form the Uri for the address
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("geo")
                    .appendPath("0,0")
                    .appendQueryParameter("q", location);
            Uri uri_adress = builder.build();

            show_map(uri_adress);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        /*database_in_background = new AsyncTask() {
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

        database_in_background.execute();*/
        Intent i = new Intent(getApplicationContext(), WeatherForcastingIntentService.class);
        startService(i);

        loag_all_weather_data();
        if (weather_data_array == null)
            show_error_views();
        else
            show_results_views();
    }

   /* @NonNull
    @Override
    public Loader<WeatherEntry[]> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<WeatherEntry[]>(this) {
            @Override
            protected void onStartLoading() {
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
                weather_data = database.weatherDao().load_all_days();
                return weather_data;
            }

        };
    }*/

   /* @Override
    public void onLoadFinished(@NonNull Loader<WeatherEntry[]> loader, WeatherEntry[] data) {
        weather_adapter = new WeatherInRecyclerView(weather_data, this);
        weather_recycler_view.setAdapter(weather_adapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<WeatherEntry[]> loader) {

    }*/

    @Override
    public void on_a_day_weather_click(int position) {
        /*if (toast != null)
            toast.cancel();
        toast = Toast.makeText(this, weather_data_array[position].getDate_time_txt(), Toast.LENGTH_LONG);
        toast.show();*/
        Intent to_a_day_weather_details_activity =
                new Intent(this, ADayWeatherDetails.class);
        to_a_day_weather_details_activity.putExtra("weather-details",
                weather_data_array[position]);
        startActivity(to_a_day_weather_details_activity);
    }
}
