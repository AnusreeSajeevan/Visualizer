package com.example.anu.visualizerapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.anu.visualizerapp.AudioVisuals.AudioInputReader;
import com.example.anu.visualizerapp.AudioVisuals.VisualizerView;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    private static final int MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE = 88;
    private VisualizerView mVisualizerView;
    private AudioInputReader mAudioInputReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVisualizerView = (VisualizerView) findViewById(R.id.main_view);
        defaultSetup();
        setupPermissions();
    }

    private void defaultSetup() {

        /**
         * get checkbox preference from shared preference
         * and use it ti show bass
         */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mVisualizerView.setShowBass(sharedPreferences.getBoolean(getResources().getString(R.string.show_bass_pref_key),
                getResources().getBoolean(R.bool.show_bass_default_value)));

        mVisualizerView.setShowMid(sharedPreferences.getBoolean(getResources().getString(R.string.show_mid_range_pref_key),
                getResources().getBoolean(R.bool.show_mid_range_default_value)));

        mVisualizerView.setShowTreble(sharedPreferences.getBoolean(getResources().getString(R.string.show_treble_pref_key),
                getResources().getBoolean(R.bool.show_treble_default_value)));

        mVisualizerView.setMinSizeScale(1);
        mVisualizerView.setColor(getString(R.string.pref_color_red_value));
    }

    /**
     * Below this point is code you do not need to modify; it deals with permissions
     * and starting/cleaning up the AudioInputReader
     **/

    /**
     * onPause Cleanup audio stream
     **/
    @Override
    protected void onPause() {
        super.onPause();
        if (mAudioInputReader != null) {
            mAudioInputReader.shutdown(isFinishing());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAudioInputReader != null) {
            mAudioInputReader.restart();
        }
    }

    /**
     * App Permissions for Audio
     **/
    private void setupPermissions() {
        // If we don't have the record audio permission...
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // And if we're on SDK M or later...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Ask again, nicely, for the permissions.
                String[] permissionsWeNeed = new String[]{ Manifest.permission.RECORD_AUDIO };
                requestPermissions(permissionsWeNeed, MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE);
            }
        } else {
            // Otherwise, permissions were granted and we are ready to go!
            mAudioInputReader = new AudioInputReader(mVisualizerView, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The permission was granted! Start up the visualizer!
                    mAudioInputReader = new AudioInputReader(mVisualizerView, this);

                } else {
                    Toast.makeText(this, "Permission for audio not granted. Visualizer can't run.", Toast.LENGTH_LONG).show();
                    finish();
                    // The permission was denied, so we can show a message why we can't run the app
                    // and then close the app.
                }
            }
            // Other permissions could go down here

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_visualizer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase(getResources().getString(R.string.show_bass_pref_key))){
            mVisualizerView.setShowBass(sharedPreferences.getBoolean(getResources().getString(R.string.show_bass_pref_key),
                    getResources().getBoolean(R.bool.show_bass_default_value)));
        }else if (key.equalsIgnoreCase(getResources().getString(R.string.show_treble_pref_key))){
            mVisualizerView.setShowTreble(sharedPreferences.getBoolean(getResources().getString(R.string.show_treble_pref_key),
                    getResources().getBoolean(R.bool.show_treble_default_value)));
        }else if (key.equalsIgnoreCase(getResources().getString(R.string.show_mid_range_pref_key))){
            mVisualizerView.setShowMid(sharedPreferences.getBoolean(getResources().getString(R.string.show_mid_range_pref_key),
                    getResources().getBoolean(R.bool.show_mid_range_default_value)));
        }
    }

    /**
     * register MainActivity as an OnsharedPreferenceListener
     */
    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * unregister MainActivity as an OnsharedPreferenceListener
     * to avoid any memory leaks
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }
}
