package com.app.tributum.activity.faq;

import android.content.res.Resources;

import com.app.tributum.R;
import com.app.tributum.activity.faq.model.FaqItem;
import com.app.tributum.activity.faq.model.FaqItemState;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.FaqClickListener;

import java.util.ArrayList;
import java.util.List;

public class FaqPresenterImpl implements FaqPresenter, FaqClickListener {

    private final FaqView view;

    private List<FaqItem> faqItems;

    FaqPresenterImpl(FaqView faqView) {
        this.view = faqView;
    }

    @Override
    public void onCreate() {
        Resources resources = TributumApplication.getInstance().getResources();
        faqItems = new ArrayList<>();
        faqItems.add(new FaqItem(resources.getString(R.string.faq_question_1),
                resources.getString(R.string.faq_question_answer_1)));
        faqItems.add(new FaqItem(resources.getString(R.string.faq_question_2),
                resources.getString(R.string.faq_question_answer_2)));
        faqItems.add(new FaqItem(resources.getString(R.string.faq_question_3),
                resources.getString(R.string.faq_question_answer_3)));
        faqItems.add(new FaqItem(resources.getString(R.string.faq_question_4),
                resources.getString(R.string.faq_question_answer_4)));
        faqItems.add(new FaqItem(resources.getString(R.string.faq_question_5),
                resources.getString(R.string.faq_question_answer_5)));
        faqItems.add(new FaqItem(resources.getString(R.string.faq_question_6),
                resources.getString(R.string.faq_question_answer_6)));
    }

    public List<FaqItem> getFaqItems() {
        return faqItems;
    }

    @Override
    public void onBackPressed() {
        if (view != null)
            view.closeActivity();
    }

    @Override
    public void onItemClicked(int position) {
        if (view == null)
            return;

        if (faqItems.get(position).getState() == FaqItemState.COLLAPSE || faqItems.get(position).getState() == FaqItemState.NONE) {
            faqItems.get(position).setState(FaqItemState.EXPAND);
        } else {
            faqItems.get(position).setState(FaqItemState.COLLAPSE);
        }
        view.notifyAdapter(faqItems, position);
    }
}