package com.app.tributum.utils;

import android.Manifest;

/**
 * Class containing constants
 */
public class ConstantsUtils {

    /**
     * Private constructor to prevent instantiation
     */
    private ConstantsUtils() {
    }

    public static final short ONE_SECOND = 1000;

    public static final short SELECTED_PICTURE_REQUEST_PPS_FRONT = 999;

    public static final short SELECTED_PICTURE_REQUEST_PPS_BACK = 998;

    public static final short SELECTED_PICTURE_REQUEST_ID = 1000;

    public static final short SELECTED_PICTURE_REQUEST_MARRIAGE = 900;

    public static final short SELECTED_PICTURE_REQUEST_BILL = 888;

    public static final short SELECTED_PICTURE_REQUEST_LETTER = 889;
    public static final short SELECTED_PICTURE_REQUEST_BANK = 221;
    public static final short SELECTED_PICTURE_REQUEST_KIDS = 222;
    public static final short SELECTED_PICTURE_REQUEST_EXPENSES = 223;
    public static final short SELECTED_PICTURE_REQUEST_RENT = 224;
    public static final short SELECTED_PICTURE_REQUEST_RTB = 225;
    public static final short SELECTED_PICTURE_REQUEST_MARRIAGE_FORM = 226;
    public static final short SELECTED_PICTURE_REQUEST_FISC_1 = 227;
    public static final short SELECTED_PICTURE_REQUEST_FISC_2 = 228;

    public static final short CAMERA_REQUEST_PPS_FRONT = 1001;

    public static final short CAMERA_REQUEST_PPS_BACK = 997;

    public static final short CAMERA_REQUEST_ID = 1002;

    public static final short CAMERA_REQUEST_MARRIAGE = 1003;

    public static final short CAMERA_REQUEST_BILL = 887;

    public static final short CAMERA_REQUEST_LETTER = 886;

    public static final short CAMERA_REQUEST_INVOICES_ID = 7;

    public static final short CAMERA_REQUEST_PRIVATES_ID = 8;
    public static final short CAMERA_REQUEST_BANK_ID = 234;
    public static final short CAMERA_REQUEST_KIDS_ID = 345;
    public static final short CAMERA_REQUEST_EXPENSES_ID = 456;
    public static final short CAMERA_REQUEST_MEDICAL_ID = 0123;
    public static final short CAMERA_REQUEST_RENT_ID = 567;
    public static final short CAMERA_REQUEST_RTB_ID = 678;
    public static final short CAMERA_REQUEST_MARRIAGE_ID = 789;
    public static final short CAMERA_REQUEST_FISC_1_ID = 890;
    public static final short CAMERA_REQUEST_FISC_2_ID = 901;

    public static final byte STORAGE_PERMISSION_REQUEST_CODE_VAT = 3;

    public static final byte MULTIPLE_PERMISSIONS_PPS_FRONT = 10;

    public static final byte CAMERA_REQUEST_INQUIRY = 11;

    public static final byte SELECT_REQUEST_INQUIRY = 12;

    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA};

    public static final int MAXIMUM_PICTURES_IN_ATTACHMENT = 500;

    public static final String TRIBUTUM_EMAIL = "tributum@yahoo.com";

    public static final int SELECT_PICTURES_FOR_INVOICES = 555;

    public static final int SELECT_PICTURES_FOR_PRIVATES = 777;

    public static long NOTIFICATION_INTERVAL = 1000 * 60 * 60 * 24 * 60;

    /**
     * constant that will be set when the app starts in order to ditch the first
     * notification, and show only the next ones
     */
    public static long APP_START_TIME = 0;
}