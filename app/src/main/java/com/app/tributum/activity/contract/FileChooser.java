package com.app.tributum.activity.contract;

import androidx.annotation.IntDef;

@IntDef
public @interface FileChooser {
    int PPS_FRONT = 1,
            PPS_BACK = 2,
            ID = 3;
}