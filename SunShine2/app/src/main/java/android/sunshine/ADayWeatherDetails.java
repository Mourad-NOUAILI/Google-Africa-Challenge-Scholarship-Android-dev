package android.sunshine;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.sunshine.Utilities.NetworkUtils;
import android.sunshine.Utilities.SharedPreferenceUtils;
import android.sunshine.data.WeatherEntry;
import android.sunshine.databinding.ActivityAdayWeatherDetailsBinding;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ADayWeatherDetails extends AppCompatActivity {
    WeatherEntry detail = null;
    //final String icon = null;
    String temp_unit;
    String speed_unit;

    ActivityAdayWeatherDetailsBinding binding;


    private AsyncTask load_icon_in_background;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aday_weather_details);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_aday_weather_details);

        ActionBar action_bar = getSupportActionBar();
        if (action_bar != null)
            action_bar.setDisplayHomeAsUpEnabled(true);

        //Recieve the Intent
        Intent from_main_activity = getIntent();

        //Check if the Intent has the sent string name
        if (from_main_activity.hasExtra("weather-details")) {

            //getSerializableExtra, because we gonna get a class.
            detail = (WeatherEntry) from_main_activity.getSerializableExtra("weather-details");

            //Get the icon in background
            load_icon_in_background = new AsyncTask() {
                Bitmap bitmap = null;
                @Override
                protected Object doInBackground(Object[] objects) {
                    bitmap = NetworkUtils.load_icon("http://openweathermap.org/img/w/"+detail.getIcon()+".png");
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    if (bitmap != null)
                        binding.aDayIconDetailsImageView.setImageBitmap(bitmap);

                }
            };

            load_icon_in_background.execute();


            temp_unit = "°";
            if (SharedPreferenceUtils.is_imperial(this))
                temp_unit = "F";

            speed_unit = "km/h";
            if (SharedPreferenceUtils.is_imperial(this))
                speed_unit = "mile/h";

            //String location = SharedPreferenceUtils.load_location(this);

            binding.aDayDateDetailsTextView.setText(detail.getDate_time_txt());
            binding.aDayCityDetailsTextView.setText(detail.getCity_name()+", ");
            binding.aDayCountryDetailsTextView.setText(detail.getCountry());
            binding.aDayDescriptionDetailsTextView.setText(detail.getDescription());
            binding.aDayMaxTempDetailsTextView.setText(detail.getTemp_max()+temp_unit);
            binding.aDayMinTempDetailsTextView.setText(detail.getTemp_min()+temp_unit);
            binding.aDayWindDetailsTextView.setText(detail.getWind_speed()+" "+speed_unit);
            binding.aDayPressureDetailsTextView.setText(detail.getPressure()+" hPa");
            binding.aDayPrecipitationDetailsTextView.setText(detail.getPrecipitation()+" mm");
            binding.aDayHumidityDetailsTextView.setText(detail.getHumidity()+"%");

        }
    }

    void share (String to_share) {
        String mime_type = "text/plain";
        String title = "Weather details for "
                + detail.getCity_name()
                + ", "
                +detail.getCountry()
                + " on "
                + detail.getDate_time_txt();
        ShareCompat.IntentBuilder
                .from(this)
                .setType(mime_type)
                .setChooserTitle(title)
                .setText(to_share)
                .startChooser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.a_day_weather_details_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        if (id == R.id.share_menu) {
            String location = detail.getCity_name() + "/" + detail.getCountry();
            String to_share = detail.getDate_time_txt() + ", "
                    +location + "\n"
                    +"Temperature: " + detail.getTemp() + " " +temp_unit + "\n"
                    +"Description: " + detail.getDescription() + "\n"
                    +"Wind: " + detail.getWind_speed() + " " + speed_unit + "\n"
                    +"Pressure: " + detail.getPressure() + " hPa\n"
                    +"Precipitation: " + detail.getPrecipitation() + " mm\n"
                    +"Humidity: " + detail.getHumidity() + " %\n";

            share(to_share);
        }

        if (id == R.id.action_settings) {
            Intent settings_activity = new Intent(this, SettingsActivity.class);
            startActivity(settings_activity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
