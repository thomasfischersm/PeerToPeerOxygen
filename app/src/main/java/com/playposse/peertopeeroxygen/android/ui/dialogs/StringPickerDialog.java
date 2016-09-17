package com.playposse.peertopeeroxygen.android.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.EditText;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.util.StringUtil;

/**
 * A builder that creates a dialog to ask the user for a string.
 */
public class StringPickerDialog {

    public static void build(
            final Context context,
            int titleResId,
            final Callback callback) {

        build(context, context.getString(titleResId), callback);
    }

    public static void build(
            final Context context,
            String title,
            final Callback callback) {

        final EditText editText = new EditText(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(editText);

        builder.setPositiveButton(
                R.string.confirm_dialog_continue_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (StringUtil.isEmpty(editText)) {
                            editText.setError(context.getString(R.string.empty_string_error));
                            return;
                        }
                        dialog.dismiss();
                        callback.onResult(StringUtil.getCleanString(editText));
                    }
                });

        builder.setNegativeButton(
                R.string.cancel_button_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    public interface Callback {
        void onResult(String str);
    }
}
