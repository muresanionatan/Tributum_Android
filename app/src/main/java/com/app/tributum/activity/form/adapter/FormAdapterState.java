package com.app.tributum.activity.form.adapter;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({FormAdapterState.BANK, FormAdapterState.KIDS, FormAdapterState.EXPENSES, FormAdapterState.MEDICAL})
public @interface FormAdapterState {
    int BANK = 0;
    int KIDS = 1;
    int EXPENSES = 2;
    int MEDICAL = 8;
}
