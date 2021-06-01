package com.app.tributum.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;

import com.app.tributum.R;

public class MailUtils {

    private MailUtils() {
    }

    public static void openEmailIntent(Activity activity, String subject, String bodyMessage) {
        Resources resources = activity.getResources();

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{
                "muresanionatan@gmail.com"
        });
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, bodyMessage);
        emailIntent.setType("message/rfc822");

        try {
            activity.startActivity(Intent.createChooser(emailIntent,
                    resources.getString(R.string.send_mail_using)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, resources.getString(R.string.no_email_client_installed), Toast.LENGTH_SHORT).show();
        }
    }
}