package com.app.tributum.application;

import android.app.Application;

import com.app.tributum.utils.UtilsGeneral;

/**
 * defines application class
 */
public class FintrexApplication extends Application {

    /**
     * sample application instance
     */
    private static FintrexApplication instance;

    /**
     * application preferences
     */
    private ApplicationPreferences applicationPreferences;

    /**
     * @return a single instance of this class
     */
    public static FintrexApplication getInstance() {
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
    private static void initializeInstance(FintrexApplication application) {
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
        UtilsGeneral.changeLocaleForContext(FintrexApplication.getInstance(), FintrexAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
    }
}