package com.example.tributum.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.tributum.R;
import com.example.tributum.application.AppKeysValues;
import com.example.tributum.application.TributumAppHelper;
import com.example.tributum.fragment.FaqsFragment;
import com.example.tributum.fragment.HomeFragment;
import com.example.tributum.fragment.invoices.InvoicesFragment;
import com.example.tributum.fragment.PaymentsFragment;
import com.example.tributum.utils.ConstantsUtils;
import com.example.tributum.utils.StatusBarUtils;
import com.example.tributum.utils.UtilsGeneral;
import com.example.tributum.utils.notifications.NotificationExtra;
import com.example.tributum.utils.notifications.NotificationIntentIds;
import com.example.tributum.utils.receiver.AlarmReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private boolean twiceBackPressed;

    private BottomNavigationView bottomNavigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener listener;

    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsGeneral.setAppLanguage(this, TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        setContentView(R.layout.activity_main);
        StatusBarUtils.makeStatusBarTransparent(this);
        ConstantsUtils.APP_START_TIME = System.currentTimeMillis();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        bottomNavigationView = findViewById(R.id.bottom_navigation_id);
        listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = 0;
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        selectedFragment = new HomeFragment();
                        itemId = R.id.action_home;
                        break;
                    case R.id.action_payment:
                        selectedFragment = new PaymentsFragment();
                        itemId = R.id.action_payment;
                        break;
                    case R.id.action_invoices:
                        selectedFragment = new InvoicesFragment();
                        itemId = R.id.action_invoices;
                        break;
                    case R.id.action_faqs:
                        selectedFragment = new FaqsFragment();
                        itemId = R.id.action_faqs;
                        break;

                }
                if (selectedFragment == null)
                    return false;

                if (bottomNavigationView.getSelectedItemId() == R.id.action_invoices
                        && TributumAppHelper.getBooleanSetting(AppKeysValues.INVOICES_TAKEN)) {
                    showCloseContractDialog(selectedFragment, itemId);
                    return false;
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,
                            selectedFragment).commit();
                    return true;
                }
            }
        };

        bottomNavigationView.setOnNavigationItemSelectedListener(listener);
        chooseScreenToStartOn();

        startNotificationAlarm();
    }

    public void startNotificationAlarm() {
        if (!TributumAppHelper.getBooleanSetting(AppKeysValues.NOTIFICATION_ALARM_SET)) {
            TributumAppHelper.saveSetting(AppKeysValues.NOTIFICATION_ALARM_SET, AppKeysValues.TRUE);

            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            long interval = ConstantsUtils.NOTIFICATION_INTERVAL;

            Calendar calendar = Calendar.getInstance();
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    interval, pendingIntent);
        }
    }

    private void chooseScreenToStartOn() {
        if (getIntent() != null && getIntent().getStringExtra(NotificationExtra.OPEN) != null
                && getIntent().getStringExtra(NotificationExtra.OPEN).equals(NotificationIntentIds.VAT_INTENT)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,
                    new InvoicesFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.action_invoices);
        } else {
            setHomeFragment();
        }
    }

    private void showCloseContractDialog(Fragment selectedFragment, int itemId) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.work_in_progress))
                .setMessage(getString(R.string.contract_in_progress_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,
                                selectedFragment).commit();
                        bottomNavigationView.setOnNavigationItemSelectedListener(null);
                        bottomNavigationView.setSelectedItemId(itemId);
                        bottomNavigationView.setOnNavigationItemSelectedListener(listener);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void setHomeFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_layout,
                new HomeFragment()).commit();
    }

    public View getBottomNavigation() {
        return bottomNavigationView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_frame_layout) instanceof InvoicesFragment) {
            InvoicesFragment fragment = (InvoicesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_frame_layout);
            if (fragment.isPreview())
                fragment.clearPreview();
            else {
                if (twiceBackPressed) {
                    super.onBackPressed();
                    return;
                }
                this.twiceBackPressed = true;
                Toast.makeText(this, getString(R.string.press_back_twice), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        twiceBackPressed = false;
                    }
                }, ConstantsUtils.ONE_SECOND);
            }
        } else {
            if (twiceBackPressed) {
                super.onBackPressed();
                return;
            }
            this.twiceBackPressed = true;
            Toast.makeText(this, getString(R.string.press_back_twice), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    twiceBackPressed = false;
                }
            }, ConstantsUtils.ONE_SECOND);
        }
    }
}