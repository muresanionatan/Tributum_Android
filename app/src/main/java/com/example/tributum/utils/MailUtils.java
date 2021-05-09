package com.example.tributum.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;

import com.example.tributum.R;

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

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"muresanionatan@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject here");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "body text");
//        File root = Environment.getExternalStorageDirectory();
//        String pathToMyAttachedFile = "temp/attachement.xml";
//        File file = new File(root, pathToMyAttachedFile);
//        if (!file.exists() || !file.canRead()) {
//            return;
//        }
//        Uri uri = FileProvider.getUriForFile(
//                getActivity(),
//                "com.example.mbacc.fragment.provider", //(use your app signature + ".provider" )
//                signature);
//        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
//
//        List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(emailIntent, PackageManager.MATCH_DEFAULT_ONLY);
//        for (ResolveInfo resolveInfo : resInfoList) {
//            String packageName = resolveInfo.activityInfo.packageName;
//            getActivity().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        }
//
//        getActivity().startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}