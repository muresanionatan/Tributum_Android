package com.app.tributum.activity.faq;

import com.app.tributum.activity.faq.model.FaqItem;

import java.util.List;

public interface FaqView {

    void closeActivity();

    void notifyAdapter(List<FaqItem> faqItems, int position);
}