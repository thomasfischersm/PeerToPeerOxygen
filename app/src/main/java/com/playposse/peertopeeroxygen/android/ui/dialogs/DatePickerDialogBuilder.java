package com.playposse.peertopeeroxygen.android.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import com.playposse.peertopeeroxygen.android.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A builder that creates a dialog to pick a date.
 */
public class DatePickerDialogBuilder {

    public static void build(
            Context context,
            int titleResId,
            Calendar calendar,
            DatePickerDialogBuilder.DatePickerDialogCallback callback) {

        build(context, context.getString(titleResId), calendar, callback);
    }

    public static void build(
            Context context,
            String title,
            Calendar calendar,
            final DatePickerDialogBuilder.DatePickerDialogCallback callback) {

        final DatePicker datePicker = new DatePicker(context);
        if (calendar != null) {
            datePicker.init(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    null);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(datePicker);

        builder.setPositiveButton(
                R.string.confirm_dialog_continue_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        GregorianCalendar calendar = new GregorianCalendar(
                                datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth());
                        callback.onPickedDate(calendar);
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

    /**
     * Callback interface that notifies when the user has picked a new date.
     */
    public interface DatePickerDialogCallback {
        void onPickedDate(Calendar calendar);
    }
}
