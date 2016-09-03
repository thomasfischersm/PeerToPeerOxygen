package com.playposse.peertopeeroxygen.android.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.playposse.peertopeeroxygen.android.R;

/**
 * Builder that builds and executes a confirmation dialog.
 */
public final class ConfirmationDialogBuilder {

    public static void show(Context context, String message, final Runnable runnable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm_dialog_title);
        builder.setMessage(message);
        builder.setNegativeButton(
                R.string.confirm_dialog_cancel_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setPositiveButton(
                R.string.confirm_dialog_continue_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        runnable.run();
                        dialog.dismiss();
                    }
                }
        );
        builder.show();
    }
}
