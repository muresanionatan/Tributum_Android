package com.app.tributum.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.Settings;

import com.app.tributum.R;
import com.app.tributum.application.TributumApplication;

public class DialogUtils {

    private DialogUtils() {}

    public static void showPermissionDeniedDialog(Context context) {
        Resources resources = TributumApplication.getInstance().getResources();
        new AlertDialog.Builder(context)
                .setTitle(resources.getString(R.string.permission_denied))
                .setMessage(resources.getString(R.string.permission_denied_explained))
                .setPositiveButton(resources.getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton(resources.getString(R.string.dismiss_label), null)
                .show();
    }
}