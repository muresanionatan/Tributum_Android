package com.app.tributum.activity.contract;

import androidx.annotation.IntDef;

@IntDef({PhotoCrop.ID_CAMERA, PhotoCrop.ID_SELECT,
        PhotoCrop.MARRIAGE_CAMERA, PhotoCrop.MARRIAGE_SELECT,
        PhotoCrop.PPS_CAMERA, PhotoCrop.PPS_SELECT,
        PhotoCrop.BACK_CAMERA, PhotoCrop.BACK_SELECT,
        PhotoCrop.BILL_CAMERA, PhotoCrop.BILL_SELECT,
        PhotoCrop.LETTER_CAMERA, PhotoCrop.LETTER_SELECT})
public @interface PhotoCrop {
    int ID_CAMERA = 0,
            ID_SELECT = 1,
            MARRIAGE_CAMERA = 2,
            MARRIAGE_SELECT = 3,
            PPS_CAMERA = 4,
            PPS_SELECT = 5,
            BACK_CAMERA = 6,
            BACK_SELECT = 7,
            BILL_SELECT = 8,
            BILL_CAMERA = 9,
            LETTER_SELECT = 10,
            LETTER_CAMERA = 11;
}