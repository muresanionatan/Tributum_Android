package com.app.tributum.activity.faq;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tributum.R;
import com.app.tributum.activity.faq.adapter.FaqAdapter;
import com.app.tributum.activity.faq.model.FaqItem;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UtilsGeneral;

import java.util.List;

public class FaqActivity extends AppCompatActivity implements FaqView {

    private RecyclerView recyclerView;

    private FaqAdapter adapter;

    private FaqPresenterImpl presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsGeneral.changeLocaleForContext(this, TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        setContentView(R.layout.activity_faq);
        StatusBarUtils.makeStatusBarTransparent(this);

        presenter = new FaqPresenterImpl(this);
        presenter.onCreate();

        recyclerView = findViewById(R.id.faq_recyclerview_id);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new FaqAdapter(presenter.getFaqItems(), presenter);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.faq_back_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackPressed();
            }
        });
    }

    @Override
    public void notifyAdapter(List<FaqItem> faqItems, int position) {
        adapter.notifyItemChanged(position);
        adapter.setFaqItems(faqItems);
    }

    @Override
    public void closeActivity() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}