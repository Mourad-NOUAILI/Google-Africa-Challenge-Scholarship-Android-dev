package android.sunshine.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.sunshine.R;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

public class SettingsFragments extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();
        for (int i = 0 ; i < count ; ++i){
            Preference p = preferenceScreen.getPreference(i);
            String v = sharedPreferences.getString(p.getKey(), "");
            set_preference_summary(p, v);
        }
    }

    public void set_preference_summary(Preference p, String v){
        if (p instanceof ListPreference){
            ListPreference listPreference = (ListPreference) p;
            int pref_index = listPreference.findIndexOfValue(v);
            if (pref_index >= 0){
                listPreference.setSummary(listPreference.getEntries()[pref_index]);
            }
        }
        else if(p instanceof EditTextPreference){
            p.setSummary(v);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference preference = findPreference(s);
        if (preference != null){
            String v = sharedPreferences.getString(preference.getKey(), "");
            set_preference_summary(preference, v);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
