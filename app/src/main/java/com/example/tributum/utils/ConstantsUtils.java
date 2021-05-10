package com.example.tributum.utils;

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

    public static final short CAMERA_REQUEST_PPS_FRONT = 1001;

    public static final short CAMERA_REQUEST_PPS_BACK = 997;

    public static final short CAMERA_REQUEST_ID = 1002;

    public static final short CAMERA_REQUEST_INVOICES_ID = 7;

    public static final byte STORAGE_PERMISSION_REQUEST_CODE_PPS_FRONT = 8;

    public static final byte STORAGE_PERMISSION_REQUEST_CODE_PPS_BACK = 6;

    public static final byte STORAGE_PERMISSION_REQUEST_CODE_ID = 9;

    public static final byte MULTIPLE_PERMISSIONS_PPS_FRONT = 10;

    public static final byte MULTIPLE_PERMISSIONS_PPS_BACK = 5;

    public static final byte MULTIPLE_PERMISSIONS_ID = 11;

    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    public static final int MAXIMUM_PICTURES_IN_ATTACHMENT = 500;

    public static final String TRIBUTUM_EMAIL = "tributum@yahoo.com";
}