package com.app.tributum.activity.company;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.app.tributum.R;
import com.app.tributum.activity.company.model.Company;
import com.app.tributum.activity.company.model.Director;
import com.app.tributum.activity.company.model.Secretary;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.utils.CustomTextWatcher;
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.animation.AnimUtils;
import com.app.tributum.utils.animation.CustomAnimatorListener;
import com.app.tributum.utils.ui.LoadingScreen;
import com.app.tributum.utils.ui.RequestSent;
import com.app.tributum.utils.ui.UiUtils;

public class CompanyActivity extends AppCompatActivity implements CompanyView {

    private CompanyPresenterImpl presenter;

    private NestedScrollView scrollView;

    private LoadingScreen loadingScreen;

    private RequestSent requestSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsGeneral.changeLocaleForContext(this, TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        setContentView(R.layout.activity_company);
        StatusBarUtils.makeStatusBarTransparent(this);

        presenter = new CompanyPresenterImpl(this);

        setupViewsAndClicks();
    }

    @SuppressLint({"ClickableViewAccessibility", "CutPasteId"})
    private void setupViewsAndClicks() {
        scrollView = findViewById(R.id.scrollView);
        loadingScreen = new LoadingScreen(findViewById(android.R.id.content), R.drawable.ic_icon_loader_company, R.color.company_1);
        requestSent = new RequestSent(findViewById(android.R.id.content), R.drawable.request_sent_company, getString(R.string.request_sent), presenter);

        UtilsGeneral.setMaxLengthEditText(findViewById(R.id.director_1_layout_id).findViewById(R.id.director_birthday_id), 10);
        UtilsGeneral.setMaxLengthEditText(findViewById(R.id.director_2_layout_id).findViewById(R.id.director_birthday_id), 10);
        UtilsGeneral.setMaxLengthEditText(findViewById(R.id.director_3_layout_id).findViewById(R.id.director_birthday_id), 10);
        UtilsGeneral.setMaxLengthEditText(findViewById(R.id.secretary_birthday_id), 10);
        UtilsGeneral.setMaxLengthAndAllCapsToEditText(findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_edit_text), 9, true);
        UtilsGeneral.setMaxLengthAndAllCapsToEditText(findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_edit_text), 9, true);
        UtilsGeneral.setMaxLengthAndAllCapsToEditText(findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_edit_text), 9, true);
        UtilsGeneral.setMaxLengthAndAllCapsToEditText(findViewById(R.id.secretary_pps_edit_text), 9, true);

        ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_birthday_id)).addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.beforeBirthdayChanged(s.length(), R.id.director_1_layout_id);
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.afterBirthdayChanged(s, R.id.director_1_layout_id);
            }
        });

        ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_birthday_id)).addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.beforeBirthdayChanged(s.length(), R.id.director_2_layout_id);
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.afterBirthdayChanged(s, R.id.director_2_layout_id);
            }
        });

        ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_birthday_id)).addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.beforeBirthdayChanged(s.length(), R.id.director_3_layout_id);
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.afterBirthdayChanged(s, R.id.director_3_layout_id);
            }
        });

        ((EditText) findViewById(R.id.secretary_birthday_id)).addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.beforeBirthdayChanged(s.length(), R.id.secretary_birthday_id);
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.afterBirthdayChanged(s, R.id.secretary_birthday_id);
            }
        });
        findViewById(R.id.company_back_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackPressed();
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null && event.getAction() == MotionEvent.ACTION_MOVE) {
                    UtilsGeneral.hideSoftKeyboard(CompanyActivity.this);
                }
                return false;
            }
        });

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
        findViewById(R.id.agree_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onAgreeTermsClick(false);
            }
        });
        findViewById(R.id.agree_checkbox_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onAgreeTermsClick(true);
            }
        });
        findViewById(R.id.company_send_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onMainButtonClick();
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
    public void hideCompanyView() {
        AnimUtils.getTranslationXAnimator(findViewById(R.id.company_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.company_layout_id).setVisibility(View.GONE);
                    }
                },
                0, -UiUtils.getScreenWidth()).start();
    }

    @Override
    public void showCompanyView() {
        AnimUtils.getTranslationXAnimator(findViewById(R.id.company_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        findViewById(R.id.company_layout_id).setVisibility(View.VISIBLE);
                    }
                },
                0).start();
    }

    @Override
    public void hideDirectorViewToLeft() {
        AnimUtils.getTranslationXAnimator(findViewById(R.id.directors_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.directors_layout_id).setVisibility(View.GONE);
                    }
                },
                -UiUtils.getScreenWidth()).start();
    }

    @Override
    public void hideDirectorViewToRight() {
        setCompletionProgress(R.id.second_progress_id, false);
        AnimUtils.getTranslationXAnimator(findViewById(R.id.directors_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.directors_layout_id).setVisibility(View.GONE);
                    }
                },
                UiUtils.getScreenWidth()).start();
    }

    @Override
    public void showDirectorViewFromRight() {
        setCompletionProgress(R.id.second_progress_id, true);
        AnimUtils.getTranslationXAnimator(findViewById(R.id.directors_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        scrollView.scrollTo(0, 0);
                        findViewById(R.id.directors_layout_id).setVisibility(View.VISIBLE);
                    }
                },
                UiUtils.getScreenWidth(), 0).start();
    }

    @Override
    public void showDirectorViewFromLeft() {
        AnimUtils.getTranslationXAnimator(findViewById(R.id.directors_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        findViewById(R.id.directors_layout_id).setVisibility(View.VISIBLE);
                    }
                },
                0).start();
    }

    @Override
    public void hideSecretaryView() {
        setCompletionProgress(R.id.third_progress_id, false);
        AnimUtils.getTranslationXAnimator(findViewById(R.id.secretary_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.secretary_layout_id).setVisibility(View.GONE);
                    }
                },
                UiUtils.getScreenWidth()).start();
    }

    @Override
    public void showSecretaryView() {
        setCompletionProgress(R.id.third_progress_id, true);
        AnimUtils.getTranslationXAnimator(findViewById(R.id.secretary_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        scrollView.scrollTo(0, 0);
                        findViewById(R.id.secretary_layout_id).setVisibility(View.VISIBLE);
                    }
                },
                UiUtils.getScreenWidth(), 0).start();
    }

    @Override
    public void setConfirmationButtonText(int stringRes) {
        ((TextView) findViewById(R.id.company_send_text_id)).setText(stringRes);
    }

    private void setCompletionProgress(int progressId, boolean forward) {
        int progress = forward ? 100 : 0;
        AnimUtils.getProgressAnimator(findViewById(progressId),
                AnimUtils.DURATION_300,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                null,
                progress).start();
    }

    @Override
    public void hideThirdDirector() {
        ((CheckBox) findViewById(R.id.director_3_checkbox_id)).setChecked(false);
        findViewById(R.id.director_3_layout_id).setVisibility(View.GONE);
    }

    @Override
    public void showToast(int stringResource) {
        Toast.makeText(this, getString(stringResource), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void closeActivity() {
        finish();

    }

    @Override
    public void hideKeyboard() {

    }

    @Override
    public void showLoadingScreen() {
        loadingScreen.show();
    }

    @Override
    public void hideLoadingScreen() {
        loadingScreen.hide();
    }

    @Override
    public void showRequestSent() {
        requestSent.show();
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public Company getCompanyDetails() {
        return new Company(((EditText) findViewById(R.id.full_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.sur_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.email_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.phone_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.company_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.proposed_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.street_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.town_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.country_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.company_activities_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.registered_edit_text)).getText().toString().trim());
    }

    @Override
    public Director getDirector1Details() {
        return new Director(((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_first_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_sur_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_birthday_id)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_nationality_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_occupation_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_home_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_other_directorships_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_other_company_edit_text)).getText().toString().trim());
    }

    @Override
    public Director getDirector2Details() {
        return new Director(((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_first_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_sur_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_birthday_id)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_nationality_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_occupation_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_home_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_other_directorships_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_other_company_edit_text)).getText().toString().trim());
    }

    @Override
    public Director getDirector3Details() {
        return new Director(((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_first_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_sur_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_birthday_id)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_nationality_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_occupation_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_home_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_other_directorships_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_other_company_edit_text)).getText().toString().trim());
    }

    @Override
    public Secretary getSecretaryDetails() {
        return new Secretary(((EditText) findViewById(R.id.secretary_first_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.secretary_sur_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.secretary_birthday_id)).getText().toString().trim(),
                ((EditText) findViewById(R.id.secretary_email_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.secretary_pps_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.secretary_nationality_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.secretary_home_edit_text)).getText().toString().trim());
    }

    @Override
    public void setBirthdayText(String text, int id) {
        if (id != R.id.secretary_birthday_id)
            ((EditText) findViewById(id).findViewById(R.id.director_birthday_id)).setText(text);
        else
            ((EditText) findViewById(R.id.secretary_birthday_id)).setText(text);
    }

    @Override
    public void moveBirthdayCursorToEnd(int id) {
        if (id != R.id.secretary_birthday_id)
            ((EditText) findViewById(id).findViewById(R.id.director_birthday_id))
                    .setSelection(((EditText) findViewById(id).findViewById(R.id.director_birthday_id)).getText().length());
        else
            ((EditText) findViewById(R.id.secretary_birthday_id))
                    .setSelection(((EditText) findViewById(R.id.secretary_birthday_id)).getText().length());
    }

    @Override
    public void checkTheAgreeBox(boolean acceptTerms) {
        ((CheckBox) findViewById(R.id.agree_checkbox_id)).setChecked(acceptTerms);
    }
}