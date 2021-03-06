package com.playposse.peertopeeroxygen.android.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;

import com.playposse.peertopeeroxygen.android.R;

/**
 * An {@link Html.ImageGetter} that loads the drawable images for the different point types from the
 * resources. The {@link Drawable}s are resized to be the height of text.
 */
public class GeneralImageGetter implements Html.ImageGetter {

    // Constants for the mission tree.
    public static final String TEACH_POINT_IMG = "teachPointImg";
    public static final String PRACTICE_POINT_IMG = "practicePointImg";
    public static final String HEART_POINT_IMG = "heartPointImg";

    // Constants for the introduction deck.
    public static final String TEACHER_IMG = "teacher";
    public static final String STUDENT_IMG = "student";
    public static final String ARROW_IMG = "arrow";

    private final float textSize;
    private final Context context;

    public GeneralImageGetter(Context context, float textSize) {
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
            case TEACHER_IMG:
                resourceId = R.drawable.ic_person_black_24dp;
                break;
            case STUDENT_IMG:
                resourceId = R.drawable.ic_person_outline_black_24dp;
                break;
            case ARROW_IMG:
                resourceId = R.drawable.ic_arrow_forward_black_24dp;
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
