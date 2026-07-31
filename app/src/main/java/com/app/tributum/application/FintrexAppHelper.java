package com.app.tributum.application;

import com.app.tributum.activity.payments.model.PaymentModel;

import java.util.List;

public class FintrexAppHelper {
    /**
     * private constructor just to make sure this class cannot be instantiated
     */
    private FintrexAppHelper() {
    }

    /**
     * Saves a {@link String} value for a setting identified by id
     *
     * @param id    the identifier for the setting
     * @param value the {@link String} value of the setting
     */
    public static void saveSetting(String id, String value) {
        ApplicationPreferences prefs = FintrexApplication.getInstance().getApplicationPreferences();
        prefs.setPreference(id, value);
        prefs.savePreferences();
    }

    /**
     * Saves a {@link Boolean} value for a setting identified by id
     *
     * @param id    the identifier for the setting
     * @param value the {@link Boolean} value of the setting
     */
    public static void saveSetting(String id, Boolean value) {
        ApplicationPreferences prefs = FintrexApplication.getInstance().getApplicationPreferences();
        prefs.setPreference(id, value);
        prefs.savePreferences();
    }

    public static void saveSetting(String id, List<PaymentModel> value) {
        ApplicationPreferences prefs = FintrexApplication.getInstance().getApplicationPreferences();
        prefs.setArrayList(id, value);
        prefs.savePreferences();
    }

    /**
     * Get the a setting which value is {@link String}
     *
     * @param id the identifier for the setting
     */
    public static String getStringSetting(String id) {
        return FintrexApplication.getInstance().getApplicationPreferences().getStringPreference(id);
    }

    /**
     * Get the a setting which value is {@link Boolean}
     *
     * @param id the identifier for the setting
     */
    public static boolean getBooleanSetting(String id) {
        return FintrexApplication.getInstance().getApplicationPreferences().getBooleanPreference(id);
    }

    public static List<PaymentModel> getListSetting(String id) {
        return FintrexApplication.getInstance().getApplicationPreferences().getArrayList(id);
    }
}