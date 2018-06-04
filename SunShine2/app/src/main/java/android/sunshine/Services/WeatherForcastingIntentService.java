package android.sunshine.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.sunshine.Utilities.JsonUtils;
import android.sunshine.Utilities.NetworkUtils;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.net.URL;

public class WeatherForcastingIntentService extends IntentService {

    public WeatherForcastingIntentService() {
        super("WeatherForcastingIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Context context = WeatherForcastingIntentService.this;
        URL url = NetworkUtils.build_url_bases_on_location_query(context);
        String json_string = NetworkUtils.get_response_from_url(url);
        //Log.e("json_erreur", json_string);
        try {
            JsonUtils.get_weather_strings_from_json(this, json_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
