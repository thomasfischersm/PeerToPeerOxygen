package com.playposse.peertopeeroxygen.android.util;

import android.text.Editable;
import android.widget.EditText;

import javax.annotation.Nullable;

/**
 * A utility class for dealing with
 */
public final class StringUtil {

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
}
