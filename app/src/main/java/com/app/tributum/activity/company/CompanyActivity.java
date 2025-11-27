package com.app.tributum.activity.company;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.app.tributum.R;

public class CompanyActivity extends AppCompatActivity implements CompanyView {

    private CompanyPresenterImpl presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_company);

        presenter = new CompanyPresenterImpl(this);

        setupViesAndClicks();
    }

    private void setupViesAndClicks() {
        findViewById(R.id.director_2_layout_checkbox_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onDirector2Click();
            }
        });
        findViewById(R.id.director_2_checkbox_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onDirector2Click();
            }
        });
        findViewById(R.id.director_3_layout_checkbox_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onDirector3Click();
            }
        });
        findViewById(R.id.director_3_checkbox_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onDirector3Click();
            }
        });
    }

    @Override
    public void showSecondDirector() {
        ((CheckBox) findViewById(R.id.director_2_checkbox_id)).setChecked(true);
        findViewById(R.id.director_2_layout_id).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSecondDirector() {
        ((CheckBox) findViewById(R.id.director_2_checkbox_id)).setChecked(false);
        findViewById(R.id.director_2_layout_id).setVisibility(View.GONE);
    }

    @Override
    public void showThirdDirector() {
        ((CheckBox) findViewById(R.id.director_3_checkbox_id)).setChecked(true);
        findViewById(R.id.director_3_layout_id).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideThirdDirector() {
        ((CheckBox) findViewById(R.id.director_3_checkbox_id)).setChecked(false);
        findViewById(R.id.director_3_layout_id).setVisibility(View.GONE);
    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public void closeActivity() {

    }

    @Override
    public void hideKeyboard() {

    }

    @Override
    public void showLoadingScreen() {

    }

    @Override
    public void hideLoadingScreen() {

    }

    @Override
    public void showRequestSent() {

    }
}