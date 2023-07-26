package com.app.tributum.activity.contract;

import androidx.annotation.IntDef;

@IntDef
public @interface ProgressState {
    int PERSONAL = 0,
            EMPLOYMENT = 1;
}