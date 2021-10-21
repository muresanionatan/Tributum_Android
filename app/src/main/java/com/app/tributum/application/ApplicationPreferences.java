package com.app.tributum.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.tributum.activity.payments.model.PaymentModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Takes care of persisting the data between sessions
 */
public class ApplicationPreferences {

    /**
     * preference name
     */
    private static final String PREFS_NAME = "gpsLink2SamplePrefs";

    /**
     * used for modifying values in a SharedPreferences prefs
     */
    private SharedPreferences.Editor prefsEditor;

    /**
     * reference to preference
     */
    private SharedPreferences prefs;

    /**
     * Default constructor.
     */
    ApplicationPreferences() {
        setDefaultValues();
    }

    /**
     * Commits the current changes to the preferences - to be called after
     * changing the preferences
     */
    public void savePreferences() {
        initPreferences();
        prefsEditor.commit();
    }

    /**
     * Sets a {@link String} preference
     *
     * @param key   - the key of the preference, defined in
     *              {@link AppKeysValues}
     * @param value - the value of the preference
     */
    public void setPreference(String key, String value) {
        initPreferences();
        prefsEditor.putString(key, value);
        savePreferences();
    }

    /**
     * Sets a boolean preference
     *
     * @param key   - the key of the preference, defined in
     *              {@link AppKeysValues}
     * @param value - the value of the preference
     */
    public void setPreference(String key, boolean value) {
        initPreferences();
        prefsEditor.putBoolean(key, value);
        savePreferences();
    }

    public void setArrayList(String key, List<PaymentModel> textList) {
        Gson gson = new Gson();
        String jsonText = gson.toJson(textList);

        initPreferences();
        prefsEditor.putString(key, jsonText);
        savePreferences();
    }

    /**
     * @return {@link String} preference for the given key or null if nothing
     * was saved
     */
    public String getStringPreference(String key) {
        initPreferences();
        try {
            return prefs.getString(key, "");
        } catch (ClassCastException ex1) {
            return String.valueOf(prefs.getInt(key, 0));
        }
    }

    /**
     * @return boolean preference for the given key or false if nothing was
     * saved
     */
    public boolean getBooleanPreference(String key) {
        initPreferences();
        return prefs.getBoolean(key, false);
    }

    public List<PaymentModel> getArrayList(String key) {
        initPreferences();
        Gson gson = new Gson();
        String jsonText = prefs.getString(key, null);
        if (jsonText != null)
            return Arrays.asList(gson.fromJson(jsonText, PaymentModel[].class));
        else
            return new ArrayList<>();
    }

    /**
     * Initializes preferences, creates the {@link android.content.SharedPreferences} and
     * {@link android.content.SharedPreferences.Editor} objects, if needed.
     */
    @SuppressWarnings("android-lint:CommitPrefEdits")
    @SuppressLint("CommitPrefEdits")
    private void initPreferences() {
        if (prefs == null) {
            prefs = TributumApplication.getInstance().getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefsEditor = prefs.edit();
        }
    }

    private void setDefaultValues() {
        // initialize with their default values
        if (!getBooleanPreference(AppKeysValues.PREFS_INITIALISED)) {
            this.setPreference(AppKeysValues.PREFS_INITIALISED, AppKeysValues.TRUE);
            this.setPreference(AppKeysValues.APP_LANGUAGE, "ro");
            this.setPreference(AppKeysValues.FIRST_TIME_USER, AppKeysValues.TRUE);
            this.setPreference(AppKeysValues.INVOICES_TAKEN, AppKeysValues.FALSE);
            this.setPreference(AppKeysValues.USER_DENIED_TERMS, AppKeysValues.FALSE);
            this.setPreference(AppKeysValues.USER_ACCEPTED_TERMS, AppKeysValues.FALSE);

            //Settings Save
            this.savePreferences();
        }
    }
}