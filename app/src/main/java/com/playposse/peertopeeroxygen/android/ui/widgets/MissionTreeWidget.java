package com.playposse.peertopeeroxygen.android.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.android.missiondependencies.MissionAvailabilityChecker;
import com.playposse.peertopeeroxygen.android.missiondependencies.MissionPlaceHolder;
import com.playposse.peertopeeroxygen.android.missiondependencies.MissionTreeUntangler;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.student.StudentMissionActivity;
import com.playposse.peertopeeroxygen.android.ui.CanvasHelper;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.List;

import javax.annotation.Nullable;

/**
 * A widget that draws the missions of a mission tree along with arrows to show the dependencies.
 */
public class MissionTreeWidget extends View {

    private static final String LOG_CAT = MissionTreeWidget.class.getSimpleName();

    private static final int BOX_WIDTH = 125;
    private static final int BOX_HEIGHT = 80;
    private static final int MARGIN = 40;
    private static final int COLUMN_WIDTH = BOX_WIDTH + MARGIN;
    private static final int ROW_HEIGHT = BOX_HEIGHT + MARGIN;
    private static final int TEXT_PADDING = 5;
    private static final int ARROW_ANGLE = 40;
    private static final int ARROW_LENGTH = 10;
    private static final float TEXT_SIZE = 12;

    private TextPaint lockedPaint;
    private TextPaint unlockedPaint;
    private TextPaint completedPaint;
    private TextPaint teachablePaint;

    private DataRepository dataRepository;
    private Long missionLadderId;
    private Long missionTreeId;
    private MissionTreeBean missionTreeBean;
    private Bitmap drawingCache;
    private boolean asyncTaskStarted = false;
    private int desiredWidth;
    private int desiredHeight;
    private List<List<MissionPlaceHolder>> rows;
    private GestureDetector gestureDetector;

    public MissionTreeWidget(Context context) {
        super(context);
    }

    public MissionTreeWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MissionTreeWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMissionTreeBean(
            Long missionLadderId,
            final MissionTreeBean missionTreeBean,
            DataRepository dataRepository) {

        this.missionLadderId = missionLadderId;
        this.missionTreeId = missionTreeBean.getId();
        this.missionTreeBean = missionTreeBean;
        this.dataRepository = dataRepository;

        post(new Runnable() {
            @Override
            public void run() {
                MissionTreeBean[] missionTreeBeans = {missionTreeBean};
                new DrawAsyncTask().execute(missionTreeBeans);
            }
        });

        GestureDetector.SimpleOnGestureListener gestureListener =
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        return handleClick(e);
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }
                };
        gestureDetector = new GestureDetector(getContext(), gestureListener);
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
        int w = resolveSizeAndState((int) toPx(desiredWidth), widthMeasureSpec, 1);
        int h = resolveSizeAndState((int) toPx(desiredHeight), heightMeasureSpec, 1);
        setMeasuredDimension(w, h);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private boolean handleClick(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int column = (int) (x / toPx(COLUMN_WIDTH));
        int row = (int) (y / toPx(ROW_HEIGHT));

        if ((rows.size() > row) && (rows.get(row).size() > column)) {
            MissionPlaceHolder holder = rows.get(row).get(column);
            if (holder.getMissionBean() != null) {
                if (MissionAvailabilityChecker.determineAvailability(holder, missionLadderId, missionTreeBean, dataRepository)
                        == MissionAvailabilityChecker.MissionAvailability.LOCKED) {
                    String lockReasonMessage = MissionAvailabilityChecker.getLockReasonMessage(
                            getContext(),
                            dataRepository,
                            holder,
                            missionLadderId,
                            missionTreeBean);
                    Toast toast = Toast.makeText(getContext(), lockReasonMessage, Toast.LENGTH_LONG);
                    toast.show();
                    return true;
                } else {
                    Long missionId = holder.getMissionBean().getId();
                    Intent intent = new Intent(getContext(), StudentMissionActivity.class);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, missionLadderId);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, missionTreeId);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_ID, missionId);
                    getContext().startActivity(intent);
                    return true;
                }
            }

            // TODO: Show mission boss.
        }
        return false;
    }

    /**
     * {@link AsyncTask} that renders the missions. Because this could take a while, it's done in an
     * {@link AsyncTask}. Once it has been drawn, the Bitmap can be redrawn each time the
     * {@link View} is rendered.
     */
    private final class DrawAsyncTask extends AsyncTask<MissionTreeBean, Void, Bitmap> {

        final int displayWidth;

        public DrawAsyncTask() {
            Context context = MissionTreeWidget.this.getContext();
            WindowManager windowManager =
                    (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Point point = new Point();
            windowManager.getDefaultDisplay().getSize(point);
            displayWidth = point.x;
        }

        @Override
        protected Bitmap doInBackground(MissionTreeBean[] missionTreeBeans) {
            long start = System.currentTimeMillis();

            MissionTreeBean missionTreeBean = missionTreeBeans[0];
            int maxHoldersPerColumn = (int) (displayWidth / toPx(COLUMN_WIDTH));
            rows = MissionTreeUntangler.untangle(missionTreeBean, maxHoldersPerColumn);
            MissionTreeUntangler.debugDump(rows);

            desiredWidth = MissionTreeUntangler.getMaxColumns(rows) * COLUMN_WIDTH;
            desiredHeight = rows.size() * ROW_HEIGHT;

            if ((desiredHeight == 0) || (desiredWidth == 0)) {
                // There are no missions in this tree. We need a sensible minimum to avoid
                // exceptions.
                desiredHeight = 1;
                desiredWidth = 1;
            }

            Bitmap bitmap =
                    Bitmap.createBitmap(
                            (int) toPx(desiredWidth),
                            (int) toPx(desiredHeight),
                            Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            initPaints();


            // TODO: set dimensions on the view.
            drawArrows(canvas, rows);
            drawBoxes(canvas, rows, missionTreeBean.getBossMissionId());

            long end = System.currentTimeMillis();
            Log.i(LOG_CAT, "Rendering mission tree to Bitmap took " + (end - start) + "ms");
            return bitmap;
        }

        /**
         * Initializes all the {@link Paint} objects.
         */
        private void initPaints() {
            lockedPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            lockedPaint.setColor(Color.GRAY);
            lockedPaint.setTextSize(toPx(TEXT_SIZE));

            unlockedPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            unlockedPaint.setColor(Color.parseColor("#1B5E20")); // dark green
            unlockedPaint.setTextSize(toPx(TEXT_SIZE));

            completedPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            completedPaint.setColor(Color.BLACK);
            completedPaint.setTextSize(toPx(TEXT_SIZE));

            teachablePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            teachablePaint.setColor(Color.parseColor("#673AB7")); // dark purple
            teachablePaint.setTextSize(toPx(TEXT_SIZE));
        }

        private void drawArrows(Canvas canvas, List<List<MissionPlaceHolder>> rows) {
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                List<MissionPlaceHolder> row = rows.get(rowIndex);
                for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                    MissionPlaceHolder holder = row.get(columnIndex);

                    int fromX = columnIndex * COLUMN_WIDTH + (MARGIN / 2) + BOX_WIDTH / 2;
                    int fromY = rowIndex * ROW_HEIGHT + (MARGIN / 2);

                    for (MissionPlaceHolder parent : holder.getParents()) {
                        Paint paint = determineArrowPaint(holder, parent);

                        int toX = parent.getColumn() * COLUMN_WIDTH + (MARGIN / 2) + BOX_WIDTH / 2;
                        int toY = parent.getRow() * ROW_HEIGHT + (MARGIN / 2) + BOX_HEIGHT;

                        CanvasHelper.drawArrow(
                                canvas,
                                paint,
                                toPx(fromX),
                                toPx(fromY),
                                toPx(toX),
                                toPx(toY),
                                ARROW_ANGLE,
                                toPx(ARROW_LENGTH));
                    }
                }
            }
        }

        private void drawBoxes(
                Canvas canvas,
                List<List<MissionPlaceHolder>> rows,
                @Nullable Long bossMissionId) {

            int x = 0;
            int y = 0;
            int rowIndex = 0;

            for (List<MissionPlaceHolder> row : rows) {
                int columnIndex = 0;
                for (MissionPlaceHolder holder : row) {
                    TextPaint paint = determineBoxPaint(holder);

                    int upperLeftX = columnIndex * COLUMN_WIDTH + (MARGIN / 2);
                    int upperLeftY = rowIndex * ROW_HEIGHT + (MARGIN / 2);
                    int lowerRightX = upperLeftX + BOX_WIDTH;
                    int lowerRightY = upperLeftY + BOX_HEIGHT;
                    CanvasHelper.drawRect(canvas,
                            toPx(upperLeftX),
                            toPx(upperLeftY),
                            toPx(lowerRightX),
                            toPx(lowerRightY),
                            paint);

                    String txt;
                    if (holder.getMissionBean().getId().equals(bossMissionId)) {
                        txt = String.format(
                                getContext().getString(R.string.boss_label),
                                holder.getMissionBean().getName().trim());
                    } else {
                        txt = holder.getMissionBean().getName().trim();

                    }
                    txt += formatCostString(holder.getMissionBean())
                            + formatCompletionString(holder.getMissionBean());

                    int textX = upperLeftX + TEXT_PADDING;
                    int textY = upperLeftY + TEXT_PADDING;
                    CanvasHelper.drawText(
                            canvas,
                            paint,
                            toPx(textX),
                            toPx(textY),
                            (int) toPx(BOX_WIDTH - 2 * TEXT_PADDING),
                            -1,
                            txt);

                    columnIndex++;
                }

                rowIndex++;
            }
        }

        /**
         * Determines the paint for the {@link MissionPlaceHolder}.
         * <p/>
         * <ul>
         * <li>Black -> completely learned</li>
         * <li>Gray -> locked mission</li>
         * <li>Green -> mission available for learning.</li>
         * </ul>
         */
        private TextPaint determineBoxPaint(MissionPlaceHolder holder) {
            MissionAvailabilityChecker.MissionAvailability availability =
                    MissionAvailabilityChecker.determineAvailability(
                            holder,
                            missionLadderId,
                            missionTreeBean,
                            dataRepository);

            switch (availability) {
                case LOCKED:
                    return lockedPaint;
                case UNLOCKED:
                    return unlockedPaint;
                case COMPLETED:
                    return completedPaint;
                case TEACHABLE:
                    return teachablePaint;
                default:
                    throw new RuntimeException("Unexpected mission availability: " + availability);
            }
        }

        /**
         * Determines the paint of a dependency arrow.
         */
        private Paint determineArrowPaint(
                MissionPlaceHolder fromHolder,
                MissionPlaceHolder toHolder) {

            return determineBoxPaint(toHolder);
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

        private float toPx(float dp) {
            return MissionTreeWidget.this.toPx(dp);
        }
    }

    private float toPx(float dp) {
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    private String formatCostString(MissionBean missionBean) {
        StringBuilder sb = new StringBuilder("\n");

        int teachCount = formatCostString(
                sb,
                R.string.teach_point_abbreviation,
                missionBean,
                PointType.teach);
        int practiceCount = formatCostString(
                sb,
                R.string.practice_point_abbreviation,
                missionBean,
                PointType.practice);
        int heartCount = formatCostString(
                sb,
                R.string.heart_point_abbreviation,
                missionBean,
                PointType.heart);

        if (teachCount == 0) {
            return "";
        } else {
            return sb.toString();
        }
    }

    private int formatCostString(
            StringBuilder sb,
            int resId,
            MissionBean missionBean,
            PointType pointType) {

        int pointCount = DataRepository.getPointByType(missionBean, pointType);
        String teachPointAbbreviation = getContext().getString(resId);
        if (sb.length() > 1) {
            sb.append(" ");
        }
        sb.append(String.format(teachPointAbbreviation, pointCount));
        return pointCount;
    }

    private String formatCompletionString(MissionBean missionBean) {
        // TODO: Consider avoiding that this creates new entries in the UserBean.
        MissionCompletionBean completion = dataRepository.getMissionCompletion(missionBean.getId());
        if (completion.getMentorCount() > 0) {
            String mentorCountLabel = getContext().getString(R.string.mentorCountLabel);
            return String.format("\n" + mentorCountLabel, completion.getMentorCount());
        } else if (completion.getStudyCount() > 0) {
            String studyCountLabel = getContext().getString(R.string.studyCountLabel);
            return String.format(
                    "\n" + studyCountLabel,
                    completion.getStudyCount(),
                    missionBean.getMinimumStudyCount());
        } else {
            return "";
        }
    }
}
