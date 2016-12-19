package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.ui.widgets.MissionTreeWidget;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.List;

/**
 * Activity that shows the student the {@link MissionTreeBean}.
 */
public class StudentMissionTreeActivity extends StudentParentActivity {

    private Long missionLadderId;

    private ViewPager missionTreePager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_mission_tree);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        missionLadderId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);

        missionTreePager = (ViewPager) findViewById(R.id.missionTreePager);
    }

    @Override
    public void receiveData(final DataRepository dataRepository) {
        missionTreePager.post(new Runnable() {
            @Override
            public void run() {
                if (missionTreePager.getHandler() != null) {
                    MissionLadderBean missionLadderBean =
                            dataRepository.getMissionLadderBean(missionLadderId);
                    MissionTreePagerAdapter pagerAdapter = new MissionTreePagerAdapter(
                            getSupportFragmentManager(),
                            missionLadderBean);
                    missionTreePager.setAdapter(pagerAdapter);
                    missionTreePager.addOnPageChangeListener(pagerAdapter);
                }
            }
        });
    }

    /**
     * A {@link android.support.v4.view.PagerAdapter} that shows a fragment for each level.
     */
    private final class MissionTreePagerAdapter
            extends FragmentStatePagerAdapter implements
            ViewPager.OnPageChangeListener {

        private final MissionLadderBean missionLadderBean;

        private MissionTreePagerAdapter(
                FragmentManager fragmentManager,
                MissionLadderBean missionLadderBean) {

            super(fragmentManager);

            this.missionLadderBean = missionLadderBean;
        }

        @Override
        public Fragment getItem(int position) {
            MissionTreeBean missionTreeBean = getMissonTreeBeanByPosition(position);
            if (missionTreeBean != null) {
                return StudentMissionTreeFragment.newInstance(
                        missionLadderBean.getId(),
                        missionTreeBean.getId());
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            List<MissionTreeBean> missionTreeBeans = missionLadderBean.getMissionTreeBeans();
            return (missionTreeBeans != null) ? missionTreeBeans.size() : 0;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Ignore.
        }

        @Override
        public void onPageSelected(int position) {
            MissionTreeBean missionTreeBean = getMissonTreeBeanByPosition(position);
            if (missionTreeBean != null) {
                String title = String.format(
                        getString(R.string.student_mission_tree_activity_title),
                        missionTreeBean.getLevel(),
                        missionTreeBean.getName());
                setTitle(title);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // Ignore.
        }

        @Nullable
        private MissionTreeBean getMissonTreeBeanByPosition(int position) {
            List<MissionTreeBean> missionTreeBeans = missionLadderBean.getMissionTreeBeans();
            if ((missionTreeBeans != null) && (position < missionTreeBeans.size())) {

                return missionTreeBeans.get(position);
            }

            return null;
        }
    }
}
