package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.GridLayout;

import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.List;

import static android.view.View.MeasureSpec.EXACTLY;

/**
 * A widget that shows the mission tree to a student. This is the second version.
 */
public class MissionTreeWidgetV2 extends GridLayout {

    private static final String LOG_CAT = MissionTreeWidgetV2.class.getSimpleName();

    private static final int MIN_MISSION_BUTTON_WIDTH_IN_DP = 125;

    private int width = 0;

    private boolean isDirty = true;
    private boolean isInitialized = false;
    private Long missionLadderId;
    private MissionTreeBean missionTreeBean;
    private DataRepository dataRepository;


    public MissionTreeWidgetV2(Context context) {
        super(context);
    }

    public MissionTreeWidgetV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MissionTreeWidgetV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Intercepts onMeasure to find out the preferred width. That allows to determine how many
     * columns of buttons should show up. Then, the mission tree is added to this layout. Once
     * the children are added the super.onMeasure is allowed to determine the correct dimensions.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != EXACTLY) {
            throw new IllegalStateException(
                    "MissionTreeWidgetV2 didn't expect this width mode: " + widthMode);
        }
        int newWidth = MeasureSpec.getSize(widthMeasureSpec);

        if (width != newWidth) {
            width = newWidth;
        }

        if (isDirty && isInitialized) {
            int maxColumns = calculateColumnCount(width);

            removeAllViews();
            List<MissionBean> missionBeans = missionTreeBean.getMissionBeans();
            if ((missionBeans != null)
                    && (missionBeans.size() != 0)
                    && (missionTreeBean.getBossMissionId() != null)) {
                MissionTreeBuilder missionTreeBuilder = new MissionTreeBuilder(
                        missionLadderId,
                        missionTreeBean,
                        maxColumns,
                        dataRepository);
                missionTreeBuilder.populateGridLayout(getContext(), this);
            }

            isDirty = false;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(LOG_CAT, "MissionTreeWidgetV2 requested width: " + width + " actual width: "
                + getWidth());
    }

    private int calculateColumnCount(int availableWidthInPx) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float minButtonWidthInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                MIN_MISSION_BUTTON_WIDTH_IN_DP,
                displayMetrics);

        return (int) (availableWidthInPx / minButtonWidthInPx);
    }

    public void setMissionTreeBean(
            Long missionLadderId,
            MissionTreeBean missionTreeBean,
            DataRepository dataRepository) {

        this.missionLadderId = missionLadderId;
        this.missionTreeBean = missionTreeBean;
        this.dataRepository = dataRepository;

        isInitialized = true;

        requestLayout();
    }
}
