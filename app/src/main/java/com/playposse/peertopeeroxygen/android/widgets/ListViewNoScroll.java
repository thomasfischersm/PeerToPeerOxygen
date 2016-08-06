package com.playposse.peertopeeroxygen.android.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * A {@link ListView} that doesn't scroll.
 */
public class ListViewNoScroll extends LinearLayout {

    public ListViewNoScroll(Context context) {
        super(context);

        setOrientation(LinearLayout.VERTICAL);
    }

    public ListViewNoScroll(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);
    }

    public ListViewNoScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(LinearLayout.VERTICAL);
    }

    public void setAdapter(ListAdapter adapter) {
        removeAllViews();

        for (int i = 0; i < adapter.getCount(); i++) {
            View childView = adapter.getView(i, null, this);
            addView(childView);
        }
    }
}
