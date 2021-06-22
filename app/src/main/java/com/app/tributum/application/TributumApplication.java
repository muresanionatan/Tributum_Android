package com.app.tributum.application;

import android.app.Application;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * defines application class
 */
public class TributumApplication extends Application {

    /**
     * sample application instance
     */
    private static TributumApplication instance;

    /**
     * application preferences
     */
    private ApplicationPreferences applicationPreferences;

    /**
     * @return a single instance of this class
     */
    public static TributumApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();
    }

    /**
     * Assigns to instance an object of this class.
     */
    private static void initializeInstance(TributumApplication application) {
        instance = application;
    }

    /**
     * @return instance to the {@link ApplicationPreferences} object
     */
    public ApplicationPreferences getApplicationPreferences() {
        return applicationPreferences;
    }

    /**
     * Initializes objects that are needed in the application such as the prefs and the caches for favourite/recent dao.
     */
    private void initApplication() {
        initializeInstance(this);
        if (applicationPreferences == null)
            applicationPreferences = new ApplicationPreferences();
        Locale locale = new Locale(TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }
}