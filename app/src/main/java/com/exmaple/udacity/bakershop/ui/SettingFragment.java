package com.exmaple.udacity.bakershop.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.exmaple.udacity.bakershop.R;

public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{
    SharedPreferences sharedPreferences;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.setting_fragment);
        PreferenceScreen preferenceScreen=getPreferenceScreen();
        sharedPreferences=preferenceScreen.getSharedPreferences();
        int prefCount=preferenceScreen.getPreferenceCount();
        for(int i=0;i<prefCount;++i) {
            Preference preference=preferenceScreen.getPreference(i);
            if(preference instanceof ListPreference) {
                String value=sharedPreferences.getString(preference.getKey(),getString(R.string.prefCardBlueValue));
                setPreferenceSummary(preference,value);
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void setPreferenceSummary(Preference preference,String value) {
        if(preference instanceof ListPreference) {
            ListPreference listPreference=(ListPreference)preference;
            int index=listPreference.findIndexOfValue(value);
            listPreference.setSummary(listPreference.getEntries()[index]);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference=findPreference(key);
        if(preference instanceof ListPreference) {
            if(key.equals(getString(R.string.prefCrdColorKey))) {
                String value=sharedPreferences.getString(key,getString(R.string.prefCardBlueValue));
                setPreferenceSummary(preference,value);
            }
            else if(key.equals(getString(R.string.prefSortkey))) {
                String value=sharedPreferences.getString(key,getString(R.string.prefRecipeAscValue));
                setPreferenceSummary(preference,value);
            }
        }
    }

    @Override
    public void onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}
