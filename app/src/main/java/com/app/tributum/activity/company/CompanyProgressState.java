package com.app.tributum.activity.company;

import androidx.annotation.IntDef;

@IntDef
public @interface CompanyProgressState {
    int COMPANY = 0,
            DIRECTOR = 1,
            SECRETARY = 2;
}