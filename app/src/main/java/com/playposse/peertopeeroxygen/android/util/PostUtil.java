package com.playposse.peertopeeroxygen.android.util;

import android.util.Log;
import android.view.View;

/**
 * Utility that helps with delaying a {@link Runnable} until the view is fully initialized.
 */
public class PostUtil {

    private static final String LOG_CAT = PostUtil.class.getSimpleName();

    private static final int MAX_RETRY = 10;

    public static void postUntil(
            final View view,
            final Runnable runnable,
            final Condition condition,
            final int retryMax) {

        view.post(new Runnable() {
                    @Override
                    public void run() {
                        if (condition.evaluate()) {
                            runnable.run();
                        } else if (retryMax >= 0) {
                            postUntil(view, runnable, condition, retryMax - 1);
                        } else {
                            Log.e(LOG_CAT, "Gave up trying to re-post " + view);
                        }
                    }
                });
    }

    public static void postUntilNonZeroDimension(View view, Runnable runnable) {
        postUntil(view, runnable, new NonZeroDimensionCondition(view), MAX_RETRY);
    }

    /**
     * Encapsulation of a condition that can change.
     */
    public interface Condition {
        boolean evaluate();
    }

    /**
     * A {@link Condition} that checks if the {@link View} has been measured and assigned non-zero
     * dimensions.
     */
    private static class NonZeroDimensionCondition implements Condition {

        private final View view;

        public NonZeroDimensionCondition(View view) {
            this.view = view;
        }

        @Override
        public boolean evaluate() {
            return (view.getWidth() > 0) && (view.getHeight() > 0);
        }
    }
}
