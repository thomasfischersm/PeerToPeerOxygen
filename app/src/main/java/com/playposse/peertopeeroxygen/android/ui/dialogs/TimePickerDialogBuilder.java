package com.playposse.peertopeeroxygen.android.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.playposse.peertopeeroxygen.android.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A builder that creates a dialog to pick a time.
 */
public class TimePickerDialogBuilder {

    public static void build(
            Context context,
            int titleResId,
            Calendar calendar,
            TimePickerDialogBuilder.TimePickerDialogCallback callback) {

        build(context, context.getString(titleResId), calendar, callback);
    }

    public static void build(
            Context context,
            String title,
            Calendar calendar,
            final TimePickerDialogBuilder.TimePickerDialogCallback callback) {

        final TimePicker timePicker = new TimePicker(context);
        if (calendar != null) {
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(timePicker);

        builder.setPositiveButton(
                R.string.confirm_dialog_continue_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        GregorianCalendar calendar = new GregorianCalendar(
                                0,
                                0,
                                0,
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute());
                        callback.onPickedTime(calendar);
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
     * Callback interface that notifies when the user has picked a new time.
     */
    public interface TimePickerDialogCallback {
        void onPickedTime(Calendar calendar);
    }
}

