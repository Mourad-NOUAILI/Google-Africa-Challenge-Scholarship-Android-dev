package android.sunshine.Utilities;

import android.content.Context;
import android.sunshine.data.AppDataBase;
import android.sunshine.data.WeatherEntry;
import android.sunshine.data.WeatherDao;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonUtils {
    private static final String CODE = "cod";
    private static final String DAYS_DATA = "list";

    private static AppDataBase database;


    public static void get_weather_strings_from_json(Context context, String json_forecast)
            throws JSONException {
        if (json_forecast == null)
            return;
        JSONObject forecast_json = new JSONObject(json_forecast);

        if (forecast_json.has(CODE)){
            int error_code = forecast_json.getInt(CODE);

            switch (error_code){
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    String error_message = forecast_json.getString("message");
                    return;
                default:
                    return;
            }
        }

        database = AppDataBase.get_instance(context);

        JSONObject  city_forecast = forecast_json.getJSONObject("city");
        String city = city_forecast.getString("name");
        String country = city_forecast.getString("country");

        JSONObject coord = city_forecast.getJSONObject("coord");
        double lat = coord.getDouble("lat");
        double lon = coord.getDouble("lon");

        JSONArray all_days_forcast_array = forecast_json.getJSONArray(DAYS_DATA);

        //e_details[] parse_all_days_forcast_array = null;
        int len = all_days_forcast_array.length();
        //parse_all_days_forcast_array = new e_details[len];

        for (int i = 0 ; i < len ; ++i){
            /* Get the JSON object representing the day */
            JSONObject day_forcast = all_days_forcast_array.getJSONObject(i);
            long date_time = day_forcast.getLong("dt");

            JSONObject weather_object = day_forcast.getJSONArray("weather").getJSONObject(0);
            String description = weather_object.getString("description");
            String icon = weather_object.getString("icon");

            JSONObject temp_object = day_forcast.getJSONObject("main");
            int temp = Math.round(temp_object.getInt("temp"));
            int temp_min = Math.round(temp_object.getInt("temp_min"));
            int temp_max = Math.round(temp_object.getInt("temp_max"));
            double pressure = temp_object.getDouble("pressure");
            int humidity = temp_object.getInt("humidity");


            JSONObject wind_object = day_forcast.getJSONObject("wind");
            double speed = wind_object.getDouble("speed");

            //Check if the day is rainy
            double rain_3h = 0.0;
            if (day_forcast.has("rain")) {
                JSONObject rain_object = day_forcast.getJSONObject("rain");
                if (rain_object.has("3h"))
                    rain_3h = rain_object.getDouble("3h");
            }


            String date_time_txt = unix_epoch_time_to_human_time(date_time);

            //Create a new instance of the class e_details
            WeatherEntry a_day_details = new WeatherEntry(date_time, temp, temp_min, temp_max,
                    pressure, humidity, speed, description, icon,
                    date_time_txt, rain_3h, city, country, lat, lon);
            Log.e("JSON_FORCAST", a_day_details.getDate_time_txt());

            database.weatherDao().insert_a_day(a_day_details);
        }
    }

    //https://stackoverflow.com/questions/17432735/convert-unix-time-stamp-to-date-in-java
    public static String unix_epoch_time_to_human_time(long timestamp){
        //convert seconds to milliseconds
        Date date = new java.util.Date(timestamp*1000L);
        // the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEE, MMM d, HH:mm");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+1"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}
