package android.sunshine;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.sunshine.Adapters.WeatherInRecyclerView;
import android.sunshine.Services.WeatherForcastFirebaseJobService;
import android.sunshine.Services.WeatherForcastingIntentService;
import android.sunshine.Utilities.NetworkUtils;
import android.sunshine.Utilities.NotificationUtils;
import android.sunshine.Utilities.ScheduleForcastingUtils;
import android.sunshine.Utilities.SharedPreferenceUtils;
import android.sunshine.Utilities.ViewModelUtils;
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
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        WeatherInRecyclerView.a_day_weather_click{
    AppDataBase database;
    WeatherEntry[] weather_data_array;


    private WeatherInRecyclerView weather_adapter;
    private RecyclerView weather_recycler_view;

    private TextView forecasting_error_msg_text_view;

    AsyncTask check_empty_data_in_backgroud;

    boolean notification_is_enabled;

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

        database = AppDataBase.get_instance(getApplicationContext());

        /*if (weather_data_array == null || weather_data_array.length == 0) {
            show_toast("empty");
            Intent i = new Intent(getApplicationContext(), WeatherForcastingIntentService.class);
            startService(i);
        }
        else {
            show_toast("full");
            ScheduleForcastingUtils.schedule_forcasting(getApplicationContext());
        }*/

        /*Intent i = new Intent(getApplicationContext(), WeatherForcastingIntentService.class);
        startService(i);

        setup_view_model();*/

        notification_is_enabled =
                SharedPreferenceUtils.notification_is_enabled(getApplicationContext());

        check_empty_data_in_backgroud = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                if (database.weatherDao().count() == 0){
                    Intent i = new Intent(getApplicationContext(), WeatherForcastingIntentService.class);
                    startService(i);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                setup_view_model();
                if (weather_data_array != null) {
                    ScheduleForcastingUtils.schedule_forcasting(getApplicationContext());
                }
            }
        };


        check_empty_data_in_backgroud.execute();





        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }


    public void setup_view_model(){
        ViewModelUtils view_model = ViewModelProviders.of(this).get(ViewModelUtils.class);
        view_model.getWeather_data().observe(this, new Observer<WeatherEntry[]>() {
            @Override
            public void onChanged(@Nullable WeatherEntry[] weatherEntries) {
                weather_adapter.set_weather_data(weatherEntries);
                weather_data_array = weatherEntries;
                if (notification_is_enabled)
                        NotificationUtils.notify_user_when_data_changed(getApplicationContext());
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
        Intent i = new Intent(getApplicationContext(), WeatherForcastingIntentService.class);
        startService(i);

        notification_is_enabled =
                SharedPreferenceUtils.notification_is_enabled(getApplicationContext());

        //setup_view_model();
        if (weather_data_array == null)
            show_error_views();
        else
            show_results_views();
    }

    @Override
    public void on_a_day_weather_click(int position) {
        Intent to_a_day_weather_details_activity =
                new Intent(this, ADayWeatherDetails.class);
        to_a_day_weather_details_activity.putExtra("weather-details",
                weather_data_array[position]);
        startActivity(to_a_day_weather_details_activity);
    }
}
