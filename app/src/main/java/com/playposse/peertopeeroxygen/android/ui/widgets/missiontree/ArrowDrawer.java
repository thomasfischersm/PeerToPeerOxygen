package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.playposse.peertopeeroxygen.android.missiondependencies.MissionAvailabilityChecker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class that draws arrows from one Button representing a mission to its children.
 */
public class ArrowDrawer {

    private static final String LOG_CAT = ArrowDrawer.class.getSimpleName();

    private static final float DEFAULT_STROKE_WIDTH_IN_DP = 2;
    private static final float ARROW_LENGTH_IN_DP = 7;
    private static final float ARROW_ANGLE_IN_DEGREES = 70;

    private static float strokeWidth;
    private static Paint lockedPaint;
    private static Paint unlockedPaint;
    private static Paint completedPaint;
    private static Paint teachablePaint;

    private static void initPaints(Context context) {
        if (lockedPaint == null) {
            strokeWidth = PixelDimensionUtil.toPx(context, DEFAULT_STROKE_WIDTH_IN_DP);

            lockedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            lockedPaint.setColor(Color.parseColor("#9E9E9E"));
            lockedPaint.setStrokeWidth(strokeWidth);
            lockedPaint.setStyle(Paint.Style.STROKE);

            unlockedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            unlockedPaint.setColor(Color.parseColor("#4CAF50"));
            unlockedPaint.setStrokeWidth(strokeWidth);
            unlockedPaint.setStyle(Paint.Style.STROKE);

            completedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            completedPaint.setColor(Color.parseColor("#212121"));
            completedPaint.setStrokeWidth(strokeWidth);
            completedPaint.setStyle(Paint.Style.STROKE);

            teachablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            teachablePaint.setColor(Color.parseColor("#03A9F4"));
            teachablePaint.setStrokeWidth(strokeWidth);
            teachablePaint.setStyle(Paint.Style.STROKE);
        }
    }


    public static void drawArrows(Context context, Canvas canvas, List<View> views, int maxColumn) {
        initPaints(context);

        Map<MissionWrapper, View> wrapperToViewMap = createWrapperToViewMap(views);

        for (MissionWrapper wrapper : wrapperToViewMap.keySet()) {
            for (MissionWrapper child : wrapper.getChildren()) {
                View childView = wrapperToViewMap.get(child);
                View parentView = wrapperToViewMap.get(wrapper);
                Log.i(LOG_CAT, "Drawing arrow from " + child.getMissionBean().getName()
                        + " (" + childView.getLeft() + ", " + childView.getTop() + ")"
                        + " to " + wrapper.getMissionBean().getName()
                        + " (" + parentView.getLeft() + ", " + parentView.getTop() + ")");
                drawArrow(
                        context,
                        canvas,
                        childView,
                        parentView,
                        wrapper.getMissionAvailability());
            }
        }
    }

    private static Map<MissionWrapper, View> createWrapperToViewMap(List<View> views) {
        Map<MissionWrapper, View> wrapperToViewMap = new HashMap<>(views.size());
        for (View view : views) {
            if (view instanceof Button) {
                MissionWrapper wrapper = (MissionWrapper) view.getTag();
                wrapperToViewMap.put(wrapper, view);
            }
        }
        return wrapperToViewMap;
    }

    private static void drawArrow(
            Context context,
            Canvas canvas,
            View childView,
            View parentView,
            MissionAvailabilityChecker.MissionAvailability missionAvailability) {

        Paint paint = getPaint(missionAvailability);

        if (childView.getX() == parentView.getX()) {
            drawArrowStraightUp(context, canvas, childView, parentView, paint);
        } else {
            drawComplexArrow(context, canvas, childView, parentView, paint);
        }
    }

    private static void drawArrowStraightUp(
            Context context,
            Canvas canvas,
            View childView,
            View parentView,
            Paint paint) {
//
//        ViewGroup.MarginLayoutParams childLayoutParams =
//                (ViewGroup.MarginLayoutParams) childView.getLayoutParams();
//        ViewGroup.MarginLayoutParams parentLayoutParams =
//                (ViewGroup.MarginLayoutParams) parentView.getLayoutParams();

        float fromX = childView.getLeft() + childView.getWidth() / 2;
        float fromY = childView.getTop() + childView.getPaddingTop();
        float toX = parentView.getLeft() + parentView.getWidth() / 2;
        float toY = parentView.getTop() + parentView.getHeight() - parentView.getPaddingBottom();

        Log.i(LOG_CAT, "Drawing straight up arrow: " + fromX + ", " + fromY + " -> " + toX + ", "
                + toY);
        drawArrow(context, canvas, paint, fromX, fromY, toX, toY);
    }

    private static void drawComplexArrow(
            Context context,
            Canvas canvas,
            View childView,
            View parentView,
            Paint paint) {

        boolean goesLeft = parentView.getLeft() <= childView.getLeft();

//        ViewGroup.MarginLayoutParams childLayoutParams =
//                (ViewGroup.MarginLayoutParams) childView.getLayoutParams();
//        ViewGroup.MarginLayoutParams parentLayoutParams =
//                (ViewGroup.MarginLayoutParams) parentView.getLayoutParams();

        float x0 = childView.getLeft() + childView.getWidth() / 2;
        float y0 = childView.getTop() + childView.getPaddingTop();

        // up
        float x1 = x0;
        float y1 = childView.getTop();

        // left/right
        float x2 = childView.getLeft() + (goesLeft ? 0 : childView.getWidth());
        float y2 = y1;

        // up
        float x3 = x2;
        float y3 = parentView.getTop() + parentView.getHeight();

        // left/right
        float x4 = parentView.getLeft() + parentView.getWidth() / 2;
        float y4 = y3;

        // last segment
        float x5 = x4;
        float y5 = parentView.getTop() + parentView.getHeight() - parentView.getPaddingBottom();

        // Build path
        Path path = new Path();
        path.moveTo(x0, y0);
        path.lineTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.lineTo(x4, y4);

        // draw lines
        canvas.drawPath(path, paint);
        drawArrow(context, canvas, paint, x4, y4, x5, y5);
    }

    private static void drawArrow(
            Context context,
            Canvas canvas,
            Paint paint,
            float fromX,
            float fromY,
            float toX,
            float toY) {

        double lineAngle = Math.atan2(toY - fromY, toX - fromX);
        double angleDelta = Math.toRadians(ARROW_ANGLE_IN_DEGREES / 2);
        float arrowLength = PixelDimensionUtil.toPx(context, ARROW_LENGTH_IN_DP);

        float arrowAX = toX - (float) (arrowLength * Math.cos(lineAngle + angleDelta));
        float arrowAY = toY - (float) (arrowLength * Math.sin(lineAngle + angleDelta));

        float arrowBX = toX - (float) (arrowLength * Math.cos(lineAngle - angleDelta));
        float arrowBY = toY - (float) (arrowLength * Math.sin(lineAngle - angleDelta));

        // Draw line.
        canvas.drawLine(fromX, fromY, toX, toY, paint);

        // Draw arrowhead.
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(toX, toY);
        path.lineTo(arrowAX, arrowAY);
        path.lineTo(arrowBX, arrowBY);
        path.lineTo(toX, toY);
        path.close();

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.STROKE);
    }

    private static Paint getPaint(
            MissionAvailabilityChecker.MissionAvailability missionAvailability) {

        switch (missionAvailability) {
            case LOCKED:
                return lockedPaint;
            case UNLOCKED:
                return unlockedPaint;
            case COMPLETED:
                return completedPaint;
            case TEACHABLE:
                return teachablePaint;
            default:
                throw new IllegalArgumentException(
                        "Unexpected mission availability: " + missionAvailability);
        }
    }
}
