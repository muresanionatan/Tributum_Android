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
        return eircode.matches("[a-zA-Z]\\d\\d\\s[a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9]");
    }
}