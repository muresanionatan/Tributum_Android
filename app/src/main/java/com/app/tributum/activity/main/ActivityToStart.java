package com.app.tributum.activity.main;

import androidx.annotation.IntDef;

@IntDef({ActivityToStart.CONTRACT,
        ActivityToStart.VAT,
        ActivityToStart.INQUIRY,
        ActivityToStart.PPS})
public @interface ActivityToStart {
    int CONTRACT = 0,
            VAT = 1,
            INQUIRY = 2,
            PPS = 3;
}