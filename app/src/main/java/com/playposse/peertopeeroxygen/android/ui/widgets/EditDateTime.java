package com.playposse.peertopeeroxygen.android.ui.widgets;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.ui.dialogs.DatePickerDialogBuilder;
import com.playposse.peertopeeroxygen.android.ui.dialogs.TimePickerDialogBuilder;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A widget that edits the date and time.
 */

public class EditDateTime
        extends LinearLayout
        implements
        DatePickerDialogBuilder.DatePickerDialogCallback,
        TimePickerDialogBuilder.TimePickerDialogCallback {

    private Calendar calendar;
    private TextView dateTextView;
    private TextView timeTextView;

    public EditDateTime(Context context) {
        super(context);
    }

    public EditDateTime(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditDateTime(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(long timeMillis) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        init(calendar);
    }


    public void init(final Calendar calendar) {
        this.calendar = calendar;

        setOrientation(HORIZONTAL);

        dateTextView = new TextView(getContext());
        dateTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1));
        addView(dateTextView);

        dateTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogBuilder.build(
                        getContext(),
                        R.string.date_dialog_title,
                        calendar,
                        EditDateTime.this);
            }
        });

        timeTextView = new TextView(getContext());
        timeTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1));
        addView(timeTextView);

        timeTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialogBuilder.build(
                        getContext(),
                        R.string.time_dialog_title,
                        calendar,
                        EditDateTime.this);
            }
        });

        if (calendar == null) {
            dateTextView.setText(R.string.set_date);
            timeTextView.setText(R.string.set_time);
        } else {
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
            dateTextView.setText(dateFormat.format(calendar.getTime()));

            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getContext());
            timeTextView.setText(timeFormat.format(calendar.getTime()));
        }
    }

    @Override
    public void onPickedDate(Calendar dateCalendar) {
        if (this.calendar == null) {
            calendar = dateCalendar;
        } else {
            calendar.set(
                    dateCalendar.get(Calendar.YEAR),
                    dateCalendar.get(Calendar.MONTH),
                    dateCalendar.get(Calendar.DAY_OF_MONTH));
        }

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
        dateTextView.setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    public void onPickedTime(Calendar timeCalendar) {
        if (this.calendar == null) {
            calendar = timeCalendar;
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
        }

        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getContext());
        timeTextView.setText(timeFormat.format(calendar.getTime()));
    }

    public Calendar getCalendar() {
        return calendar;
    }
}
