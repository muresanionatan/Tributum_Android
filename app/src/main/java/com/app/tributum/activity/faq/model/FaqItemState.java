package com.app.tributum.activity.faq.model;

import androidx.annotation.IntDef;

@IntDef
public @interface FaqItemState {
    int NONE = 0,
            EXPAND = 1,
            COLLAPSE = 2;
}