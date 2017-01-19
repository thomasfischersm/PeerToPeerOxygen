package com.playposse.peertopeeroxygen.android.student;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObjectNotFoundException;

import com.playposse.peertopeeroxygen.android.student.util.CommonNavigationActions;
import com.playposse.peertopeeroxygen.android.student.util.WipeLocalDataTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.playposse.peertopeeroxygen.android.student.util.CommonNavigationActions.ActivityType.studentDomainSelectionActivity;
import static com.playposse.peertopeeroxygen.android.student.util.CommonNavigationActions.ActivityType.studentIntroductionDeckActivity;

/**
 * A UI test that creates a private domain.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class StudentCreatePrivateDomainActivityTest {

    private static final String DOMAIN_NAME = "UI created domain";
    private static final String DOMAIN_DESCRIPTION = "A very unique and eloquent description.";

    @Rule
    public WipeLocalDataTestRule wipeLocalDataTestRule = new WipeLocalDataTestRule();

    @Rule
    public ActivityTestRule<StudentLoginActivity> mActivityRule =
            new ActivityTestRule<>(StudentLoginActivity.class);

    @Before
    public void setUp() throws UiObjectNotFoundException {
        CommonNavigationActions.login(studentIntroductionDeckActivity);
        CommonNavigationActions.moveThroughIntroductionDeck(studentDomainSelectionActivity);
    }

    @Test
    public void createPrivateDomain() {
        CommonNavigationActions.createPrivateDomain(DOMAIN_NAME, DOMAIN_DESCRIPTION);
    }
}
