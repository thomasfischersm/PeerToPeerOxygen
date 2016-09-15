package com.playposse.peertopeeroxygen.android.ui.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.playposse.peertopeeroxygen.android.R;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A {@link View} that shows a number of stars that a user can click to select a rating.
 */
public class StarRatingView extends LinearLayout {

    private static final int STAR_COUNT = 5;

    private boolean editable = true;
    private int rating = 0;
    private List<ImageView> stars = new ArrayList<>(STAR_COUNT);

    public StarRatingView(Context context) {
        super(context);
        init(null);
    }

    public StarRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public StarRatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StarRatingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StarRatingView,
                0,
                0);
         editable = typedArray.getBoolean(R.styleable.StarRatingView_editable, true);

        setOrientation(HORIZONTAL);

        for (int i = 0; i < STAR_COUNT; i++) {
            ImageView star = new ImageView(getContext());
            star.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            addView(star);
            stars.add(star);

            final int position = i;
            if (editable) {
                star.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rating = position + 1;
                        updateStars();
                    }
                });
            }
        }

        updateStars();
    }

    private void updateStars() {
        for (int i = 0; i < stars.size(); i++) {
            if (i < rating) {
                stars.get(i).setImageResource(R.drawable.ic_star_black_24dp);
            } else {
                stars.get(i).setImageResource(R.drawable.ic_star_border_black_24dp);
            }
        }

        invalidate();
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
        updateStars();
    }
}
