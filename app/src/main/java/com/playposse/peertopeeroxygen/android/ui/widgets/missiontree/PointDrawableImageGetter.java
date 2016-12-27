package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;

import com.playposse.peertopeeroxygen.android.R;

/**
 * An {@link Html.ImageGetter} that loads the drawable images for the different point types from the
 * resources. The {@link Drawable}s are resized to be the height of text.
 */
public class PointDrawableImageGetter implements Html.ImageGetter {

    public static final String TEACH_POINT_IMG = "teachPointImg";
    public static final String PRACTICE_POINT_IMG = "practicePointImg";
    public static final String HEART_POINT_IMG = "heartPointImg";

    private final float textSize;
    private final Context context;

    public PointDrawableImageGetter(Context context, float textSize) {
        this.textSize = textSize;
        this.context = context;
    }

    @Override
    public Drawable getDrawable(String src) {
        final int resourceId;
        switch (src) {
            case TEACH_POINT_IMG:
                resourceId = R.drawable.ic_people_black_24dp;
                break;
            case PRACTICE_POINT_IMG:
                resourceId = R.drawable.ic_directions_run_black_24dp;
                break;
            case HEART_POINT_IMG:
                resourceId = R.drawable.ic_favorite_black_24dp;
                break;
            default:
                throw new IllegalArgumentException("Unexpected image ID in HTML: " + src);
        }

        Drawable drawable = context.getResources().getDrawable(resourceId);
        sizeDrawableToTextSize(drawable);
        return drawable;
    }

    private void sizeDrawableToTextSize(Drawable drawable) {
        float ratio = textSize / drawable.getIntrinsicHeight();
        drawable.setBounds(
                0,
                0,
                (int) (drawable.getIntrinsicWidth() * ratio),
                (int) (drawable.getIntrinsicHeight() * ratio));
    }
}
