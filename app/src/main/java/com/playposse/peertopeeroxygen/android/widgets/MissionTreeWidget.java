package com.playposse.peertopeeroxygen.android.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.playposse.peertopeeroxygen.android.ExtraConstants;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.missiondependencies.MissionPlaceHolder;
import com.playposse.peertopeeroxygen.android.missiondependencies.MissionTreeUntangler;
import com.playposse.peertopeeroxygen.android.student.StudentMissionActivity;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.List;

/**
 * A widget that draws the missions of a mission tree along with arrows to show the dependencies.
 */
public class MissionTreeWidget extends View {

    public static final String LOG_CAT = MissionTreeWidget.class.getSimpleName();

    public static final int BOX_WIDTH = 200;
    public static final int BOX_HEIGHT = 150;
    public static final int MARGIN = 40;
    public static final int COLUMN_WIDTH = BOX_WIDTH + MARGIN;
    public static final int ROW_HEIGHT = BOX_HEIGHT + MARGIN;
    public static final int TEXT_PADDING = 5;
    public static final int ARROW_ANGLE = 40;
    public static final int ARROW_LENGTH = 10;

    private Long missionLadderId;
    private Long missionTreeId;
    private MissionTreeBean missionTreeBean;
    private Bitmap drawingCache;
    private boolean asyncTaskStarted = false;
    private int desiredWidth;
    private int desiredHeight;
    private List<List<MissionPlaceHolder>> rows;

    public MissionTreeWidget(Context context) {
        super(context);
    }

    public MissionTreeWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MissionTreeWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMissionTreeBean(Long missionLadderId, final MissionTreeBean missionTreeBean) {
        this.missionLadderId = missionLadderId;
        this.missionTreeId = missionTreeBean.getId();
        this.missionTreeBean = missionTreeBean;

        post(new Runnable() {
            @Override
            public void run() {
                MissionTreeBean[] missionTreeBeans = {missionTreeBean};
                new DrawAsyncTask().execute(missionTreeBeans);
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(LOG_CAT, "onDraw is called " + (drawingCache != null));
        if (drawingCache != null) {
            canvas.drawBitmap(drawingCache, 0, 0, null);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(LOG_CAT, "Measure is called with desiredHeight " + desiredHeight);
        int w = resolveSizeAndState(desiredWidth, widthMeasureSpec, 1);
        int h = resolveSizeAndState(desiredHeight, heightMeasureSpec, 1);
        setMeasuredDimension(w, h);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (rows == null) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            int column = (int) x / COLUMN_WIDTH;
            int row = (int) y / ROW_HEIGHT;

            if ((rows.size() > row) && (rows.get(row).size() > column)) {
                MissionPlaceHolder holder = rows.get(row).get(column);
                if (holder.getMissionBean() != null) {
                    Long missionId = holder.getMissionBean().getId();
                    Intent intent = new Intent(getContext(), StudentMissionActivity.class);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, missionLadderId);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, missionTreeId);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_ID, missionId);
                    getContext().startActivity(intent);
                    return true;
                }

                // TODO: Show mission boss.
            }
        }

        return false;
    }

    /**
     * {@link AsyncTask} that renders the missions. Because this could take a while, it's done in an
     * {@link AsyncTask}. Once it has been drawn, the Bitmap can be redrawn each time the
     * {@link View} is rendered.
     */
    private final class DrawAsyncTask extends AsyncTask<MissionTreeBean, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(MissionTreeBean[] missionTreeBeans) {
            rows = MissionTreeUntangler.untangle(missionTreeBeans[0]);
            MissionTreeUntangler.debugDump(rows);

            desiredWidth = MissionTreeUntangler.getMaxColumns(rows) * COLUMN_WIDTH;
            desiredHeight = rows.size() * ROW_HEIGHT;

            Bitmap bitmap =
                    Bitmap.createBitmap(desiredWidth, desiredHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);

            // TODO: set dimensions on the view.
            drawArrows(canvas, paint, rows);
            drawBoxes(canvas, paint, rows);

            return bitmap;
        }

        private void drawArrows(Canvas canvas, Paint paint, List<List<MissionPlaceHolder>> rows) {
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                List<MissionPlaceHolder> row = rows.get(rowIndex);
                for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                    MissionPlaceHolder holder = row.get(columnIndex);

                    int fromX = columnIndex * COLUMN_WIDTH + (MARGIN / 2) + BOX_WIDTH / 2;
                    int fromY = rowIndex * ROW_HEIGHT + (MARGIN / 2);

                    for (MissionPlaceHolder parent : holder.getParents()) {
                        int toX = parent.getColumn() * COLUMN_WIDTH + (MARGIN / 2) + BOX_WIDTH / 2;
                        int toY = parent.getRow() * ROW_HEIGHT + (MARGIN / 2) + BOX_HEIGHT;

                        double lineAngle = Math.atan2(toY - fromY, toX - fromX);
                        double angleDelta = Math.toRadians(ARROW_ANGLE / 2);

                        float arrowAX = toX - (float) (ARROW_LENGTH * Math.cos(lineAngle + angleDelta));
                        float arrowAY = toY - (float) (ARROW_LENGTH * Math.sin(lineAngle + angleDelta));

                        float arrowBX = toX - (float) (ARROW_LENGTH * Math.cos(lineAngle - angleDelta));
                        float arrowBY = toY - (float) (ARROW_LENGTH * Math.sin(lineAngle - angleDelta));

                        canvas.drawLine(fromX, fromY, toX, toY, paint);
                        canvas.drawLine(toX, toY, arrowAX, arrowAY, paint);
                        canvas.drawLine(toX, toY, arrowBX, arrowBY, paint);
                    }
                }
            }
        }

        private void drawBoxes(Canvas canvas, Paint paint, List<List<MissionPlaceHolder>> rows) {
            int x = 0;
            int y = 0;
            int rowIndex = 0;

            for (List<MissionPlaceHolder> row : rows) {
                int columnIndex = 0;
                for (MissionPlaceHolder holder : row) {
                    int upperLeftX = columnIndex * COLUMN_WIDTH + (MARGIN / 2);
                    int upperLeftY = rowIndex * ROW_HEIGHT + (MARGIN / 2);
                    int lowerRightX = upperLeftX + BOX_WIDTH;
                    int lowerRightY = upperLeftY + BOX_HEIGHT;
                    canvas.drawRect(upperLeftX, upperLeftY, lowerRightX, lowerRightY, paint);

                    final String txt;
                    if (holder.getMissionTreeBean() != null) {
                        txt = String.format(
                                getContext().getString(R.string.boss_label),
                                holder.getMissionTreeBean().getName());
                    } else {
                        txt = holder.getMissionBean().getName();
                    }

                    int textX = upperLeftX + TEXT_PADDING;
                    int textY = upperLeftY + (BOX_HEIGHT / 2) + TEXT_PADDING;
                    canvas.drawText(txt, textX, textY, paint);

                    columnIndex++;
                }

                rowIndex++;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            drawingCache = bitmap;
            Log.i(LOG_CAT, "DrawAsyncTask is done.");
            forceLayout();
            ((View) getParent().getParent().getParent()).invalidate();
            requestLayout();
            invalidate();
            ((View) getParent().getParent().getParent().getParent().getParent()).invalidate();
            ((View) getParent().getParent().getParent().getParent().getParent()).forceLayout();
            ((View) getParent().getParent().getParent().getParent().getParent()).requestLayout();
            ((View) getParent().getParent().getParent().getParent().getParent()).invalidate();
        }
    }
}
