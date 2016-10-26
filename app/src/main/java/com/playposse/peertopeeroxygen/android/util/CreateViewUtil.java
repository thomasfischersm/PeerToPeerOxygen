package com.playposse.peertopeeroxygen.android.util;

import android.content.Context;
import android.text.Spanned;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.playposse.peertopeeroxygen.android.R;

/**
 * A utility class for creating common views.
 */
public class CreateViewUtil {

    public static TextView createTextView(Context context, int resId) {
        TextView textView = new TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(resId);
        return textView;
    }

    public static TextView createTextView(Context context, String text) {
        TextView textView = new TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(text);
        return textView;
    }

    public static TextView createTextView(Context context, Spanned text) {
        TextView textView = new TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(text);
        return textView;
    }

    public static LinearLayout createLinearLayout(Context context, int orientation) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(orientation);

        if (orientation == LinearLayout.VERTICAL) {
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        } else if (orientation == LinearLayout.HORIZONTAL) {
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            throw new RuntimeException("Unexpected orientation: " + orientation);
        }
        return linearLayout;
    }

    public static NetworkImageView createNetworkImageView(Context context, String url) {
        NetworkImageView networkImageView = new NetworkImageView(context.getApplicationContext());
        networkImageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        networkImageView.setImageUrl(url, VolleySingleton.getInstance(context).getImageLoader());
        return networkImageView;
    }
}
