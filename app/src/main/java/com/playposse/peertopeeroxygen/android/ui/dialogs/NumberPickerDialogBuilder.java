package com.playposse.peertopeeroxygen.android.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.NumberPicker;

import com.playposse.peertopeeroxygen.android.R;

/**
 * A builder that creates a dialog to pick a number.
 */
public class NumberPickerDialogBuilder {

    public static void build(
            Context context,
            String title,
            final NumberPickerDialogCallback callback) {

        final NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(100);
        numberPicker.setWrapSelectorWheel(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(numberPicker);

        builder.setPositiveButton(
                R.string.confirm_dialog_continue_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        if (numberPicker.getValue() != 0) {
                            callback.onPickedNumber(numberPicker.getValue());
                        }
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

        builder.show();
    }

    public interface NumberPickerDialogCallback {
        void onPickedNumber(int number);
    }
}
