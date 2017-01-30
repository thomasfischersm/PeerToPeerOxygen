package com.playposse.peertopeeroxygen.android.student;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.util.AnalyticsUtil;

import static com.playposse.peertopeeroxygen.android.util.AnalyticsUtil.ScreenName.introductionDeckSlide;

/**
 * An {@link android.app.Activity} that shows the user a deck of informational slides to swipe
 * through. It introduces the user to the application after logging in.
 */
public class StudentIntroductionDeckActivity extends StudentParentActivity {

    private static final int SLIDE_COUNT = 4;

    private ViewPager introductionSlidePager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_introduction_deck);
        super.onCreate(savedInstanceState);

        setTitle(R.string.introduction_deck_activity_title);

        introductionSlidePager = (ViewPager) findViewById(R.id.introductionSlidePager);
        IntroductionSlidePagerAdapter pagerAdapter =
                new IntroductionSlidePagerAdapter(getSupportFragmentManager());
        introductionSlidePager.setAdapter(pagerAdapter);

        introductionSlidePager.addOnPageChangeListener(new AnalyticsPageChangeListener());
    }

    /**
     * A {@link android.support.v4.view.PagerAdapter} that loads text strings from the resources to
     * show on the different slides.
     */
    private static class IntroductionSlidePagerAdapter extends FragmentPagerAdapter {

        public IntroductionSlidePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return StudentIntroductionDeckFragment
                            .newInstance(R.string.introduction_deck_slide_0, false);
                case 1:
                    return StudentIntroductionDeckFragment
                            .newInstance(R.string.introduction_deck_slide_1, false);
                case 2:
                    return StudentIntroductionDeckFragment
                            .newInstance(R.string.introduction_deck_slide_2, false);
                case 3:
                    return StudentIntroductionDeckFragment
                            .newInstance(R.string.introduction_deck_slide_3, true);
                default:
                    throw new IllegalStateException("The introduction slide pager didn't expect" +
                            " to get a request for position " + position);
            }
        }

        @Override
        public int getCount() {
            return SLIDE_COUNT;
        }
    }

    /**
     * A {@link android.support.v4.view.ViewPager.OnPageChangeListener} that reports to Analytics
     * when a new fragment is selected.
     */
    private class AnalyticsPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Nothing to do.
        }

        @Override
        public void onPageSelected(int position) {
            String screenName = introductionDeckSlide.name() + " " + position;
            AnalyticsUtil.reportScreenName(getApplication(), screenName);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // Nothing to do.
        }
    }
}
