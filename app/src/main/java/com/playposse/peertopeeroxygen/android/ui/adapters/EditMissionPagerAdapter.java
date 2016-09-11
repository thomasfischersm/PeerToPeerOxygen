package com.playposse.peertopeeroxygen.android.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.admin.AdminEditMissionActivity;
import com.playposse.peertopeeroxygen.android.admin.AdminMissionBasicsFragment;
import com.playposse.peertopeeroxygen.android.admin.AdminMissionBuddyInstructionsFragment;
import com.playposse.peertopeeroxygen.android.admin.AdminMissionPrerequisitesFragment;
import com.playposse.peertopeeroxygen.android.admin.AdminMissionStudentInstructionsFragment;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A {@link android.support.v4.view.PagerAdapter} for the edit mission activity in the admin
 * section. It breaks the mission data into multiple fragments that can be paged through.
 */
public class EditMissionPagerAdapter
        extends FragmentPagerAdapter
        implements AdminEditMissionActivity.EditMissionFragment{

    private static final String LOG_CAT = EditMissionPagerAdapter.class.getSimpleName();

    private final List<Fragment> fragments = new ArrayList<>();
    private final Context context;

    public EditMissionPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        this.context = context;

        fragments.add(AdminMissionBasicsFragment.newInstance());
        fragments.add(AdminMissionStudentInstructionsFragment.newInstance());
        fragments.add(AdminMissionBuddyInstructionsFragment.newInstance());
        fragments.add(AdminMissionPrerequisitesFragment.newInstance());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.basics_tab_title);
            case 1:
                return context.getString(R.string.student_instructions_tab_title);
            case 2:
                return context.getString(R.string.buddy_instructions_tab_title);
            case 3:
                return context.getString(R.string.prerequisites_tab_title);
            default:
                throw new RuntimeException("Unexpected page title requested: " + position);
        }
    }

    @Override
    public void showMission(MissionTreeBean missionTreeBean, @Nullable MissionBean missionBean) {
        for (Fragment fragment : fragments) {
            AdminEditMissionActivity.EditMissionFragment editMissionFragment =
                    (AdminEditMissionActivity.EditMissionFragment) fragment;
            editMissionFragment.showMission(missionTreeBean,missionBean);
        }
    }

    @Override
    public boolean isDirty(MissionBean missionBean) {
        for (Fragment fragment : fragments) {
            AdminEditMissionActivity.EditMissionFragment editMissionFragment =
                    (AdminEditMissionActivity.EditMissionFragment) fragment;
            boolean isDirty = editMissionFragment.isDirty(missionBean);
            Log.i(LOG_CAT, "Checking " + fragment.getClass().getSimpleName()
                    + " isDirty: " + isDirty);
            if (isDirty) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void save(MissionBean missionBean) {
        for (Fragment fragment : fragments) {
            AdminEditMissionActivity.EditMissionFragment editMissionFragment =
                    (AdminEditMissionActivity.EditMissionFragment) fragment;
            editMissionFragment.save(missionBean);
        }
    }
}
