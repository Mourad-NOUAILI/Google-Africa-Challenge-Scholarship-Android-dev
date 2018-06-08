package android.sunshine.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.sunshine.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    //New url string
    private static final String WEATHER_URL =
            "http://api.openweathermap.org/data/2.5/forecast";

    //"https://andfun-weather.udacity.com/weather";

    private static final String API_KEY_PARAM = "apikey";
    private static final String QUERY_PARAM = "q";
    private static final String FORMAT_PARAM = "mode";
    private static final String UNITS_PARAM = "units";
    private static final String DAYS_PARAM = "cnt";



    public static URL build_url_bases_on_location_query(Context context){
        String unit_param = null;
        if (SharedPreferenceUtils.is_metric(context))
            unit_param = "metric";
        if(SharedPreferenceUtils.is_imperial(context))
            unit_param = "imperial";
        String location_query = SharedPreferenceUtils.load_location(context);

        Uri built_uri = Uri.parse(WEATHER_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, location_query)
                .appendQueryParameter(FORMAT_PARAM, "json")
                .appendQueryParameter(UNITS_PARAM, unit_param)
                .appendQueryParameter(DAYS_PARAM, "30")
                .appendQueryParameter(API_KEY_PARAM,"44f1bd3e5f72db2e4e1014639c8c3841")
                .build();


        try {
            URL built_url = new URL(built_uri.toString());
            return built_url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String get_response_from_url(URL url){
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (connection == null)
            return  null;

        InputStream in = null;
        try {
            in = connection.getInputStream();
            Scanner s = new Scanner(in);
            s.useDelimiter("\\A");

            boolean has_input = s.hasNext();
            if (has_input)
                return s.next().toString();
            else
                return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {connection.disconnect();
        }
    }

    public static Bitmap load_icon(String url_string){
        URL url = null;
        try {
            url = new URL (url_string);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(url.openStream());
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
