package com.playposse.peertopeeroxygen.android.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.missiondependencies.MissionPlaceHolder;
import com.playposse.peertopeeroxygen.android.missiondependencies.MissionTreeUntangler;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.List;

/**
 * A widget that draws the missions of a mission tree along with arrows to show the dependencies.
 */
public class MissionTreeWidget extends View {

    private MissionTreeBean missionTreeBean;
    private Bitmap drawingCache;
    private boolean asyncTaskStarted = false;

    public MissionTreeWidget(Context context) {
        super(context);
    }

    public MissionTreeWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MissionTreeWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMissionTreeBean(final MissionTreeBean missionTreeBean) {
        this.missionTreeBean = missionTreeBean;

        post(new Runnable() {
            @Override
            public void run() {
                MissionTreeBean[] missionTreeBeans = {missionTreeBean};
                new DrawAsyncTask(getWidth(), getHeight()).execute(missionTreeBeans);
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (drawingCache != null) {
            canvas.drawBitmap(drawingCache, 0, 0, null);
        }
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        resolveSizeAndState()
//    }

    /**
     * {@link AsyncTask} that renders the missions. Because this could take a while, it's done in an
     * {@link AsyncTask}. Once it has been drawn, the Bitmap can be redrawn each time the
     * {@link View} is rendered.
     */
    private final class DrawAsyncTask extends AsyncTask<MissionTreeBean, Void, Bitmap> {

        public static final int BOX_WIDTH = 300;
        public static final int BOX_HEIGHT = 200;
        public static final int MARGIN = 20;
        public static final int COLUMN_WIDTH = BOX_WIDTH + MARGIN;
        public static final int ROW_HEIGHT = BOX_HEIGHT + MARGIN;
        public static final int TEXT_PADDING = 5;

        private int width;
        private int height;

        public DrawAsyncTask(int width, int height) {
            this.height = height;
            this.width = width;
        }

        @Override
        protected Bitmap doInBackground(MissionTreeBean[] missionTreeBeans) {
            List<List<MissionPlaceHolder>> rows =
                    MissionTreeUntangler.untangle(missionTreeBeans[0]);
            // TODO: Get max column width
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);

            // TODO: set dimensions on the view.
            drawArrows(canvas, paint, rows);
            drawBoxes(canvas, paint, rows);

            return bitmap;
        }

        private void drawArrows(Canvas canvas, Paint paint, List<List<MissionPlaceHolder>> rows) {

        }

        private void drawBoxes(Canvas canvas, Paint paint, List<List<MissionPlaceHolder>> rows) {
            int x = 0;
            int y = 0;
            int rowIndex = 0;

            for (List<MissionPlaceHolder> row : rows) {
                int columnIndex = 0;
                for (MissionPlaceHolder holder : row) {
                    int upperLeftX = 0 + columnIndex * COLUMN_WIDTH + (MARGIN / 2);
                    int upperLeftY = height - rowIndex * ROW_HEIGHT - (MARGIN / 2);
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
                    int textY = upperLeftY - (BOX_HEIGHT / 2) + TEXT_PADDING;
                    canvas.drawText(txt, textX, textY, paint);

                    columnIndex++;
                }

                rowIndex++;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            drawingCache = bitmap;
            invalidate();
        }
    }
}
