package com.app.tributum.utils;

public class ValidationUtils {

    private ValidationUtils() {
    }

    public static boolean isPpsValid(String pps) {
        return pps.matches("[0-9]{7}[A-Z]{2}") || pps.matches("[0-9]{7}[A-Z]{1}");
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isEircodeValid(String eircode) {
        return eircode.matches("[A-Z]{1}[0-9]{2}[A-Za-z0-9]{4}");
    }
}