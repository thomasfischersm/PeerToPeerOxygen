package com.playposse.peertopeeroxygen.android.util;

import android.text.Editable;

import javax.annotation.Nullable;

/**
 * A utility class for dealing with
 */
public final class StringUtil {

    @Nullable
    public static String getCleanString(Editable editable) {
        String str = editable.toString().trim();
        return (str.length() > 0) ? str : null;
    }

    public static boolean equals(Editable editable, @Nullable String str) {
        String cleanStr = getCleanString(editable);

        if ((cleanStr == null) && (str == null)) {
            return true;
        } else if ((cleanStr == null) || (str == null)) {
            return false;
        } else {
            return cleanStr.equals(str);
        }
    }
}
