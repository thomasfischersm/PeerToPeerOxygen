package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.android.missiondependencies.MissionAvailabilityChecker;
import com.playposse.peertopeeroxygen.android.missiondependencies.MissionPlaceHolder;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.student.StudentMissionActivity;
import com.playposse.peertopeeroxygen.android.ui.GeneralImageGetter;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that converts a {@link MissionWrapper} to a {@link Button}. The class adds all the UI
 * logic to the mission button.
 */
public class MissionWrapperToButtonConverter {

    private static final String LOG_CAT = MissionWrapperToButtonConverter.class.getSimpleName();

    private static final String SPACE = " ";
    private static final int BUTTON_EXTRA_MARGIN_IN_DP = 3;

    public static Button convert(Context context, MissionWrapper wrapper) {
        Button button = new Button(context);
        Log.i(LOG_CAT, "Creating button: " + wrapper.getRow() + ", " + wrapper.getColumn() + " "
                + wrapper.getMissionBean().getName());

        // Create LayoutParams.
        GridLayout.Spec rowSpec = GridLayout.spec(wrapper.getRow());
        GridLayout.Spec columnSpec = GridLayout.spec(wrapper.getColumn(), 1, 1.0f);
        GridLayout.LayoutParams layoutParams =
                new GridLayout.LayoutParams(rowSpec, columnSpec);
        layoutParams.setGravity(Gravity.FILL);
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        int extraBottomMargin =
                (int) PixelDimensionUtil.toPx(context, BUTTON_EXTRA_MARGIN_IN_DP);
        layoutParams.setMargins(0, 0, 0, extraBottomMargin);
        button.setLayoutParams(layoutParams);

        // Set color based on availability.
        final ColorStateList backgroundTint;
        switch (wrapper.getMissionAvailability()) {
            case LOCKED:
                backgroundTint = ContextCompat.getColorStateList(context, R.color.lockedMission);
                break;
            case UNLOCKED:
                backgroundTint = ContextCompat.getColorStateList(context, R.color.readyMission);
                break;
            case COMPLETED:
                backgroundTint = ContextCompat.getColorStateList(context, R.color.learnedMission);
                break;
            case TEACHABLE:
                backgroundTint = ContextCompat.getColorStateList(context, R.color.teachableMission);
                break;
            default:
                throw new IllegalArgumentException("The mission availability is not supported: "
                        + wrapper.getMissionAvailability());
        }
        ViewCompat.setBackgroundTintList(button, backgroundTint);

        // Add text.
        button.setAllCaps(false);
        button.setText(createButtonText(context, wrapper, button.getTextSize()));
        button.setMaxWidth(0);
        button.setTag(wrapper);

        // Add click listener
        button.setOnClickListener(new MissionClickListener(context, wrapper));

        return button;
    }

    private static Spanned createButtonText(
            Context context,
            MissionWrapper wrapper,
            float textSize) {

        // Write mission name.
        StringBuilder sb = new StringBuilder();
        if (wrapper.isBossMission()) {
            sb.append(context.getString(R.string.boss_mission_caption).toUpperCase());
        }
        sb.append(wrapper.getMissionBean().getName().toUpperCase());

        // Get the strings for each point type.
        List<String> pointCostStrings = new ArrayList<>();
        addPointCostString(context, wrapper, pointCostStrings, PointType.teach);
        addPointCostString(context, wrapper, pointCostStrings, PointType.practice);
        addPointCostString(context, wrapper, pointCostStrings, PointType.heart);

        // Assemble cost strings.
        if (pointCostStrings.size() != 0) {
            sb.append(context.getString(R.string.point_cost_label));
            for (int i = 0; i < pointCostStrings.size(); i++) {
                sb.append(pointCostStrings.get(i));
                if (i + 2 < pointCostStrings.size()) {
                    sb.append(context.getString(R.string.point_cost_separator));
                }
            }
        }

        GeneralImageGetter imageGetter = new GeneralImageGetter(context, textSize);
        return Html.fromHtml(sb.toString(), imageGetter, null);
    }

    private static void addPointCostString(
            Context context,
            MissionWrapper wrapper,
            List<String> pointCostStrings,
            PointType pointType) {

        String str = getPointCostString(context, wrapper, pointType);
        if (str != null) {
            pointCostStrings.add(str);
        }
    }

    private static String getPointCostString(
            Context context,
            MissionWrapper wrapper,
            PointType pointType) {

        int pointCount = DataRepository.getPointByType(wrapper.getMissionBean(), pointType);
        if (pointCount == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (pointCount > 1) {
            sb.append(pointCount + SPACE);
        }

        final String imgSrc;
        switch (pointType) {
            case teach:
                imgSrc = GeneralImageGetter.TEACH_POINT_IMG;
                break;
            case practice:
                imgSrc = GeneralImageGetter.PRACTICE_POINT_IMG;
                break;
            case heart:
                imgSrc = GeneralImageGetter.HEART_POINT_IMG;
                break;
            default:
                throw new IllegalArgumentException("Unexpected point type: " + pointType.name());
        }
        sb.append(context.getString(R.string.point_img, imgSrc));
        return sb.toString();
    }

    /**
     * A {@link View.OnClickListener} that checks if the mission is accessible and if so opens
     * the mission.
     */
    private static class MissionClickListener implements View.OnClickListener {

        private final Context context;
        private final MissionWrapper wrapper;

        private MissionClickListener(Context context, MissionWrapper wrapper) {
            this.context = context;
            this.wrapper = wrapper;
        }

        @Override
        public void onClick(View view) {
            MissionPlaceHolder placeHolder = wrapper.getMissionPlaceHolder();
            Long missionLadderId = wrapper.getMissionLadderId();
            MissionTreeBean missionTreeBean = wrapper.getMissionTreeBean();

            MissionAvailabilityChecker.MissionAvailability availability =
                    wrapper.getMissionAvailability();
            if (availability == MissionAvailabilityChecker.MissionAvailability.LOCKED) {
                showLockReasonToast(placeHolder, missionLadderId, missionTreeBean);
            } else {
                openMissionActivity(placeHolder, missionLadderId, missionTreeBean);
            }
        }

        private void showLockReasonToast(
                MissionPlaceHolder placeHolder,
                Long missionLadderId,
                MissionTreeBean missionTreeBean) {

            String lockReasonMessage = MissionAvailabilityChecker.getLockReasonMessage(
                    context,
                    wrapper.getDataRepository(),
                    placeHolder,
                    missionLadderId,
                    missionTreeBean);
            Toast toast = Toast.makeText(context, lockReasonMessage, Toast.LENGTH_LONG);
            toast.show();
        }

        private void openMissionActivity(
                MissionPlaceHolder placeHolder,
                Long missionLadderId,
                MissionTreeBean missionTreeBean) {

            Long missionId = placeHolder.getMissionBean().getId();
            Intent intent = new Intent(context, StudentMissionActivity.class);
            intent.putExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, missionLadderId);
            intent.putExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, missionTreeBean.getId());
            intent.putExtra(ExtraConstants.EXTRA_MISSION_ID, missionId);
            context.startActivity(intent);
        }
    }
}
