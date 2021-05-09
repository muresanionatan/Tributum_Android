package com.example.tributum.fragment.invoices;

import androidx.annotation.IntDef;

@IntDef
public @interface InvoiceState {
    int PHOTO = 0,
            PREVIEW = 1;
}