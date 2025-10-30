package com.app.tributum.activity.form;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({FormState.RENT, FormState.RTB, FormState.MARRIAGE, FormState.FISC_1, FormState.FISC_2})
public @interface FormState {
    int RENT = 3;
    int RTB = 4;
    int MARRIAGE = 5;
    int FISC_1 = 6;
    int FISC_2 = 7;
}