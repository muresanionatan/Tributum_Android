package com.app.tributum.activity.newcontract;

import androidx.annotation.IntDef;

@IntDef
public @interface MaritalStatus {
    int SINGLE = 0,
            MARRIED = 1,
            DIVORCED = 2,
            COHABITING = 3;
}