package com.example.anu.visualizerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


/**
 * implement OnPreferenceChangeListener to indicate invalid size value
 */
public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener{


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_visualizer);

        /**
         * get the preference screen to get the number of preferences
         * iterate through all the preferences and set the summary by calling the
         * setSummary method by pasing preference and its value
         */
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();
        for (int i=0;i<count;i++){
            Preference preference = preferenceScreen.getPreference(i);
            if (!(preference instanceof CheckBoxPreference)){
                setPreferenceSummary(preference, sharedPreferences.getString(preference.getKey(), ""));
            }
        }

        /**
         * set the OnPreferenceChangeListener specifically for the Edittext preference
         */
        Preference preference = findPreference(getResources().getString(R.string.pref_size_multiplier_key));
        preference.setOnPreferenceChangeListener(this);
    }

    /**
     * method to set the user preferred preference value
     * @param preference the preference whose value is to be updated
     * @param value the user prefered value which is to be set
     */
    private void setPreferenceSummary(Preference preference, String value) {
        if (null != preference){
           if (preference instanceof ListPreference){
               ListPreference listPreference = (ListPreference) preference;
               int index = listPreference.findIndexOfValue(value);
               if (index>=0){
                    listPreference.setSummary(listPreference.getEntries()[index]);
               }
           }
           else if (preference instanceof EditTextPreference){
                preference.setSummary(value);
           }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (null != preference){
            if (!(preference instanceof CheckBoxPreference)){
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }
    }

    /**
     * register OnSharedPreferenceChangedListener
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * unregister OnSharedPreferenceChangedListener
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * this method is overriden to check the new size value
     * it convert the new size preference value to float
     * if it cannot be converted to a float value, it will show a valid error message to the user
     * if it can, it will check the new value is between 0(exclusive) and 3(inclusive), if it will return true
     * otherwise it will return false
     * @param preference
     * @param newValue
     * @return
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equalsIgnoreCase(getResources().getString(R.string.pref_size_multiplier_key))){
            Toast errToast = Toast.makeText(getActivity(), getResources().getString(R.string.size_err_msg), Toast.LENGTH_SHORT);
            try{
                String str = (String) newValue;
                float newSizeVal = Float.parseFloat(str);
                if (newSizeVal>3 || newSizeVal<=0){
                    errToast.show();
                    return false;
                }
                Log.d("checkfloatt","val : "+newSizeVal);
            }
            catch (NumberFormatException e){
                errToast.show();
                return false;
            }
            return true;
        }
        return true;
    }
}
