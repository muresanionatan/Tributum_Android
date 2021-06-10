package com.app.tributum.activity.faq;

import com.app.tributum.activity.faq.model.FaqItem;
import com.app.tributum.activity.faq.model.FaqItemState;
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
        faqItems = new ArrayList<>();
        faqItems.add(new FaqItem("shjdfsjdgfhjsdgfhjsdgfhjsdgfhjsdghfsg",
                "jdhfkjshdjfhsdk jfhdsjkfhskjdfskdfhjks dfhksdfkjdhfkjsdhfkjshfk sdhfkjshdfkjshdfjk shdfj shdfk jshdkf jshdkfjshfuwehfiu sfhew hfiuasehfk uehf sirugf dsrg "));
        faqItems.add(new FaqItem("shjdfsjdgfhjsdgfhjsdgfhjsdgfhjsdghfsg",
                "8w3975c94c89237c489b2b7c4823b4798237498c2b3748cb23794cb823bx49823cv7b 4982 34798 23794 e827er f987d g98df7g9s8d7fg98sdf7gsdfg "));
        faqItems.add(new FaqItem("shjdfsjdgfhjsdgfhjsdgfhj sdkjfs kdjf hsjd fhskd jfhsj dhfkj sdhkf skjdf skjdf skjdhf sdgfhjsdghfsg",
                "8w3975c94c89237c489b2b7c4823b4798237498c2b3748cb23794cb823bx49823cv7b 4982 34798 23794 e827er f987d g98df7g9s8d7fg98sdf7gsdfg "));
        faqItems.add(new FaqItem("shjdfsjdgfhjsdgfhjsdgfhjsdgfhjsdghfsg",
                "8w3975c94c89237c489b2b7c4823b4798237498c2b3748cb23794cb823bx49823cv7b 4982 34798 23794 e827er f987d g98df7g9s8d7fg98sdf7gsdfg "));
        faqItems.add(new FaqItem("shjdfsjdgfhjsdgfhjsdgfhjsdgfhjsdghfsg",
                "8w3975c94c89237c489b2b7c4823b4798237498c2b3748cb23794cb823bx49823cv7b 4982 34798 23794 e827er f987d g98df7g9s8d7fg98sdf7gsdfg "));
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