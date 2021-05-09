package com.example.tributum.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tributum.R;
import com.example.tributum.activity.contract.ContractActivity;
import com.example.tributum.application.AppKeysValues;
import com.example.tributum.application.TributumAppHelper;
import com.example.tributum.utils.MailUtils;
import com.example.tributum.utils.UtilsGeneral;

public class HomeFragment extends Fragment {

    private View languageLayout;

    private boolean isLanguagePanelVisible;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        setupViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupViews(final View view) {
        if (getActivity() == null)
            return;

        languageLayout = view.findViewById(R.id.language_layout_id);
        setLanguageLabel(view);

        view.findViewById(R.id.info_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLanguagePanel();
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.need_info))
                        .setMessage(getString(R.string.need_info_body))
                        .setPositiveButton(getString(R.string.send_email), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MailUtils.openEmailIntent(getActivity(), getResources().getString(R.string.info_email_subject),
                                        getResources().getString(R.string.info_email_message));
                            }
                        })
                        .setNegativeButton(getString(R.string.close), null)
                        .show();
            }
        });

        view.findViewById(R.id.language_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguagePopup();
            }
        });

        view.findViewById(R.id.start_new_contract_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLanguagePanel();
                startActivity(new Intent(getActivity(), ContractActivity.class));
            }
        });

        view.findViewById(R.id.home_main_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLanguagePanel();
            }
        });
    }

    private void setLanguageLabel(View view) {
        if (TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE).equals("en"))
            ((TextView) view.findViewById(R.id.language_change_id)).setText(getString(R.string.english_label));
        else
            ((TextView) view.findViewById(R.id.language_change_id)).setText(getString(R.string.romanian_label));
    }

    private void showLanguagePopup() {
        hideLanguagePanel();
        isLanguagePanelVisible = true;
        languageLayout.setVisibility(View.VISIBLE);
        languageLayout.findViewById(R.id.english_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TributumAppHelper.saveSetting(AppKeysValues.APP_LANGUAGE, "en");
                UtilsGeneral.restartAppForLanguage(getActivity());
            }
        });
        languageLayout.findViewById(R.id.romanian_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TributumAppHelper.saveSetting(AppKeysValues.APP_LANGUAGE, "ro");
                UtilsGeneral.restartAppForLanguage(getActivity());
            }
        });
    }

    private void hideLanguagePanel() {
        if (isLanguagePanelVisible) {
            isLanguagePanelVisible = false;
            languageLayout.setVisibility(View.GONE);
        }
    }
}