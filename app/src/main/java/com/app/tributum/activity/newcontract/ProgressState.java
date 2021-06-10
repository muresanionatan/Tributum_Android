package com.app.tributum.activity.newcontract;

import androidx.annotation.IntDef;

@IntDef
public @interface ProgressState {
    int PERSONAL = 0,
            EMPLOYMENT = 1,
            SIGNATURE = 2;
}