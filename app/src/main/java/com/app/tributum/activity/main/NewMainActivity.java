package com.app.tributum.activity.main;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.tributum.R;
import com.app.tributum.activity.faq.FaqActivity;
import com.app.tributum.activity.newcontract.NewContractActivity;
import com.app.tributum.activity.payments.PaymentsActivity;
import com.app.tributum.activity.vat.VatActivity;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.animation.AnimUtils;
import com.app.tributum.utils.animation.CustomAnimatorListener;

public class NewMainActivity extends AppCompatActivity implements MainView {

    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsGeneral.setAppLanguage(this, TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        setContentView(R.layout.new_activity_main);
        StatusBarUtils.makeStatusBarTransparent(this);
        ConstantsUtils.APP_START_TIME = System.currentTimeMillis();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        showSplashScreen();
        presenter = new MainPresenterImpl(this);
        presenter.onCreate(this);

        findViewById(R.id.language_text_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onLanguageClick();
            }
        });

        findViewById(R.id.english_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEnglishClick();
            }
        });

        findViewById(R.id.romanian_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onRomanianClick();
            }
        });

        findViewById(R.id.language_done_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onLanguageDoneClick();
            }
        });

        findViewById(R.id.contract_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onContractClick();
            }
        });

        findViewById(R.id.payments_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPaymentsClick();
            }
        });

        findViewById(R.id.vat_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onVatClick();
            }
        });

        findViewById(R.id.faq_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFaqClick();
            }
        });
    }

    private void showSplashScreen() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                findViewById(R.id.splash_id).setVisibility(View.GONE);
                presenter.onSplashFinished(getIntent());
            }
        }, ConstantsUtils.ONE_SECOND * 2);
    }

    @Override
    public void startContractActivity() {
        startActivity(new Intent(this, NewContractActivity.class));
    }

    @Override
    public void startPaymentsActivity() {
        startActivity(new Intent(this, PaymentsActivity.class));
    }

    @Override
    public void startVatActivity() {
        startActivity(new Intent(this, VatActivity.class));
    }

    @Override
    public void startFaqActivity() {
        startActivity(new Intent(this, FaqActivity.class));
    }

    @Override
    public void showLanguageLayout() {
        View languageLayout = findViewById(R.id.language_id);
        AnimUtils.getTranslationYAnimator(findViewById(R.id.main_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        languageLayout.setVisibility(View.VISIBLE);
                    }
                },
                0, 2500).start();
    }

    @Override
    public void setWelcomeMessage(String message) {
        TextView welcomeMessageText = findViewById(R.id.home_title_id);
        welcomeMessageText.setText(message);
    }

    @Override
    public void checkEnglishBox() {
        ((CheckBox) findViewById(R.id.english_check_id)).setChecked(true);
    }

    @Override
    public void checkRomanianBox() {
        ((CheckBox) findViewById(R.id.romanian_check_id)).setChecked(true);
    }

    @Override
    public void unCheckEnglishBox() {
        ((CheckBox) findViewById(R.id.english_check_id)).setChecked(false);
    }

    @Override
    public void unCheckRomanianBox() {
        ((CheckBox) findViewById(R.id.romanian_check_id)).setChecked(false);
    }

    @Override
    public void hideLanguageLayout() {
        View languageLayout = findViewById(R.id.language_id);
        AnimUtils.getTranslationYAnimator(findViewById(R.id.main_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new AccelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        languageLayout.setVisibility(View.GONE);
                    }
                },
                0).start();

    }

    @Override
    public void restartApp() {
        UtilsGeneral.restartAppForLanguage(this);
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }
}