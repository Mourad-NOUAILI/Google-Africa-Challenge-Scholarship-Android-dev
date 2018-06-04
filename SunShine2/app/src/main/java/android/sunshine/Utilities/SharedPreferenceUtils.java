package android.sunshine.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.sunshine.R;
import android.support.v7.preference.PreferenceManager;

public class SharedPreferenceUtils {

    public static String load_location(Context context){
        SharedPreferences shared_preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String pref_location_key =
                context.getString(R.string.pref_location_key);
        String pref_location_defalut_value =
                context.getString(R.string.pref_location_default_value);

        return shared_preferences.getString(pref_location_key, pref_location_defalut_value);
    }

    public static boolean is_metric(Context context){
        SharedPreferences shared_preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String pref_unit_key =
                context.getString(R.string.list_pref_key);
        String pref_unit_defalut_value =
                context.getString(R.string.list_pref_default_value);

        return shared_preferences.getString(pref_unit_key, pref_unit_defalut_value)
                .equals(context.getString(R.string.pref_metric_value));
    }

    public static boolean is_imperial(Context context){
        SharedPreferences shared_preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String pref_unit_key =
                context.getString(R.string.list_pref_key);
        String pref_unit_defalut_value =
                context.getString(R.string.list_pref_default_value);

        return shared_preferences.getString(pref_unit_key, pref_unit_defalut_value)
                .equals(context.getString(R.string.pref_imperial_value));
    }

}
