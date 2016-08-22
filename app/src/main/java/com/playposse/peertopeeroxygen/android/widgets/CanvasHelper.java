package com.playposse.peertopeeroxygen.android.widgets;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * Helper class to offer drawing methods that {@link Canvas} doesn't.
 */
public final class CanvasHelper {

    /**
     * Draws a rectangle.
     *
     * <p>The Canvas equivalent method fills the rectangle unless the {@link Paint} has been set to
     * stroke. However, that setting makes the {@link Paint} no longer useful for drawing text.
     */
    public static void drawRect(
            Canvas canvas,
            float upperLeftX,
            float upperLeftY,
            float lowerRightX,
            float lowerRightY,
            Paint paint) {

        canvas.drawLine(upperLeftX, upperLeftY, upperLeftX, lowerRightY, paint);
        canvas.drawLine(upperLeftX, upperLeftY, lowerRightX, upperLeftY, paint);
        canvas.drawLine(upperLeftX, lowerRightY, lowerRightX, lowerRightY, paint);
        canvas.drawLine(lowerRightX, lowerRightY, lowerRightX, upperLeftY, paint);
    }

    public static void drawArrow(
            Canvas canvas,
            Paint paint,
            float fromX,
            float fromY,
            float toX,
            float toY,
            float arrowAngle,
            float arrowLength) {
        
        double lineAngle = Math.atan2(toY - fromY, toX - fromX);
        double angleDelta = Math.toRadians(arrowAngle / 2);

        float arrowAX = toX - (float) (arrowLength * Math.cos(lineAngle + angleDelta));
        float arrowAY = toY - (float) (arrowLength * Math.sin(lineAngle + angleDelta));

        float arrowBX = toX - (float) (arrowLength * Math.cos(lineAngle - angleDelta));
        float arrowBY = toY - (float) (arrowLength * Math.sin(lineAngle - angleDelta));

        canvas.drawLine(fromX, fromY, toX, toY, paint);
        canvas.drawLine(toX, toY, arrowAX, arrowAY, paint);
        canvas.drawLine(toX, toY, arrowBX, arrowBY, paint);
    }

    public static void drawText(
            Canvas canvas,
            TextPaint paint,
            float x,
            float y,
            int width,
            float height,
            String text) {

        // TODO: Implement height cut off

        StaticLayout layout =
                new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        canvas.save();
        canvas.translate(x, y);
        layout.draw(canvas);
        canvas.restore();
    }
}
