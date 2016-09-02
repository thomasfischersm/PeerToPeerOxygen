package com.playposse.peertopeeroxygen.android.util;

import android.text.Html;
import android.text.Spanned;

/**
 * A util that helps formatting text for {@link android.widget.TextView}s.
 */
public class TextFormatter {

    /**
     * Turns text into a {@link Spanned} that will format HTML tags nicely. Text line breaks are
     * converted to HTML line breaks.
     */
    public static Spanned format(String str) {
        if (str == null) {
            str = "";
        }

        str = str.replaceAll("\n", "<br>");
        return Html.fromHtml(str);
    }
}
