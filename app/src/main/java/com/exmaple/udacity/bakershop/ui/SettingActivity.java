package com.exmaple.udacity.bakershop.ui;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.exmaple.udacity.bakershop.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    @BindView(R.id.settingActivity)
    FrameLayout settingActivityLayout;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(sharedPreferences,getString(R.string.prefCrdColorKey));
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.prefCrdColorKey))) {
            String color=sharedPreferences.getString(key,getString(R.string.prefCardBlueValue));
            if(color.equals(getString(R.string.prefCardBlueValue))) {
                settingActivityLayout.setBackgroundResource(R.drawable.blue_background);
            }
            else if(color.equals(getString(R.string.prefCardRedValue))) {
                settingActivityLayout.setBackgroundResource(R.drawable.red_background);
            }
            else {
                settingActivityLayout.setBackgroundResource(R.drawable.green_background);
            }
        }
    }


    @Override
    protected void onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}
