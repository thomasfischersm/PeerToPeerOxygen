package com.playposse.peertopeeroxygen.android.util;

import android.text.Editable;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import javax.annotation.Nullable;

/**
 * A utility class for dealing with
 */
public final class StringUtil {

    private static final String DATE_TIME_SKELETON = "Mdyhm";

    @Nullable
    public static String getCleanString(TextView textView) {
        return getCleanString(textView.getText().toString());
    }

    @Nullable
    public static String getCleanString(EditText editText) {
        return getCleanString(editText.getText());
    }

    @Nullable
    public static String getCleanString(Editable editable) {
        return getCleanString(editable.toString());
    }

    @Nullable
    public static String getCleanString(String str) {
        if (str == null) {
            return null;
        } else {
            str = str.trim();
            return (str.length() > 0) ? str : null;
        }
    }

    public static boolean equals(Editable editable, @Nullable String str) {
        String editableStr = getCleanString(editable);
        str = getCleanString(str);

        if ((editableStr == null) && (str == null)) {
            return true;
        } else if ((editableStr == null) || (str == null)) {
            return false;
        } else {
            return editableStr.equals(str);
        }
    }

    public static boolean equals(EditText editText, @Nullable String str) {
        return equals(editText.getText(), str);
    }

    public static boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    public static String formatDateTime(long timeInMillis) {
        String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), DATE_TIME_SKELETON);
        return DateFormat.format(pattern, timeInMillis).toString();
    }
}
