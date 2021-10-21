package com.app.tributum.activity.main;

import android.Manifest;
import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.app.tributum.R;
import com.app.tributum.activity.faq.FaqActivity;
import com.app.tributum.activity.contract.ContractActivity;
import com.app.tributum.activity.inquiry.InquiryActivity;
import com.app.tributum.activity.payments.PaymentsActivity;
import com.app.tributum.activity.vat.VatActivity;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.DialogUtils;
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.animation.AnimUtils;
import com.app.tributum.utils.animation.CustomAnimatorListener;

public class MainActivity extends AppCompatActivity implements MainView {

    private NestedScrollView scrollView;

    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UtilsGeneral.changeLocaleForContext(this, TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtils.makeStatusBarTransparent(this);
        ConstantsUtils.APP_START_TIME = System.currentTimeMillis();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        showSplashScreen();
        presenter = new MainPresenterImpl(this);
        presenter.onCreate();

        scrollView = findViewById(R.id.terms_scrollview_id).findViewById(R.id.nested_scroll_view_id);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView.getChildAt(0).getBottom()
                        <= (scrollView.getHeight() + scrollView.getScrollY())) {
                    presenter.scrollViewScrolledToBottom();
                } else {
                    presenter.scrollViewNotAtBottom();
                }
            }
        });

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

        findViewById(R.id.inquiry_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onInquiryClick();
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
                AnimUtils.getFadeOutAnimator(findViewById(R.id.splash_id),
                        AnimUtils.DURATION_200,
                        AnimUtils.NO_DELAY,
                        null, new CustomAnimatorListener() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                findViewById(R.id.splash_id).setVisibility(View.GONE);
                            }
                        }
                ).start();
                presenter.onSplashFinished(getIntent());
            }
        }, ConstantsUtils.ONE_SECOND * 2);
    }

    @Override
    public void setLanguageLabel(int languageId) {
        ((TextView) findViewById(R.id.language_text_id)).setText(getResources().getString(languageId));
    }

    @Override
    public void hideScrollToBottomButton() {
        View arrowView = findViewById(R.id.terms_scrollview_id).findViewById(R.id.scroll_toBottom_id);
        AnimUtils.getScaleAnimatorSet(arrowView,
                AnimUtils.DURATION_200,
                AnimUtils.NO_DELAY,
                null,
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        arrowView.setVisibility(View.GONE);
                    }
                },
                false,
                0).start();
    }

    @Override
    public void showScrollToBottomButton() {
        View arrowView = findViewById(R.id.terms_scrollview_id).findViewById(R.id.scroll_toBottom_id);
        AnimUtils.getScaleAnimatorSet(arrowView,
                AnimUtils.DURATION_200,
                AnimUtils.NO_DELAY,
                null,
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        arrowView.setVisibility(View.VISIBLE);
                    }
                },
                false,
                0, 1).start();
        arrowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onArrowClicked();
            }
        });
    }

    @Override
    public void scrollViewToBottom() {
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void startContractActivity() {
        startActivity(new Intent(this, ContractActivity.class));
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
    public void startInquiryActivity() {
        startActivity(new Intent(this, InquiryActivity.class));
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
    public void setWelcomeMessage(int stringId) {
        TextView welcomeMessageText = findViewById(R.id.home_title_id);
        welcomeMessageText.setText(getResources().getString(stringId));
    }

    @Override
    public void checkEnglishBox() {
        ((CheckBox) findViewById(R.id.english_check_id)).setChecked(true);
        findViewById(R.id.english_id).setBackgroundResource(R.drawable.white_rectangle_corners);
    }

    @Override
    public void checkRomanianBox() {
        ((CheckBox) findViewById(R.id.romanian_check_id)).setChecked(true);
        findViewById(R.id.romanian_id).setBackgroundResource(R.drawable.white_rectangle_corners);
    }

    @Override
    public void unCheckEnglishBox() {
        ((CheckBox) findViewById(R.id.english_check_id)).setChecked(false);
        findViewById(R.id.english_id).setBackgroundResource(R.drawable.grey_rectangle_corners);
    }

    @Override
    public void unCheckRomanianBox() {
        ((CheckBox) findViewById(R.id.romanian_check_id)).setChecked(false);
        findViewById(R.id.romanian_id).setBackgroundResource(R.drawable.grey_rectangle_corners);
    }

    @Override
    public void hideLanguageLayout() {
        View languageLayout = findViewById(R.id.language_id);
        AnimUtils.getTranslationYAnimator(findViewById(R.id.main_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        languageLayout.setVisibility(View.GONE);
                    }
                },
                0).start();

    }

    @Override
    public void showTermsAndConditionsScreen() {
        findViewById(R.id.terms_scrollview_id).setVisibility(View.VISIBLE);
        findViewById(R.id.accept_terms_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAcceptTermsClicked();
            }
        });
        findViewById(R.id.deny_terms_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.oNDenyTermsClicked();
            }
        });
    }

    @Override
    public void hideTermsAndConditionsScreen() {
        AnimUtils.getFadeOutAnimator(findViewById(R.id.terms_scrollview_id),
                AnimUtils.DURATION_200,
                AnimUtils.NO_DELAY,
                null, new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.terms_scrollview_id).setVisibility(View.GONE);
                    }
                }
        ).start();
    }

    @Override
    public void showPopup() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.are_you_sure))
                .setMessage(getString(R.string.wont_use_app))
                .setPositiveButton(getString(R.string.yes_label), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onUserDenyClicked();
                    }
                })
                .setNegativeButton(getString(R.string.no_label), null)
                .show();
    }

    @Override
    public void closeApp() {
        finish();
    }

    @Override
    public int checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    @Override
    public boolean shouldShowStorageRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public boolean shouldShowCameraRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
    }

    @Override
    public void takeUserToAppSettings() {
        DialogUtils.showPermissionDeniedDialog(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        presenter.onRequestPermissionsResult(requestCode, grantResults);
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