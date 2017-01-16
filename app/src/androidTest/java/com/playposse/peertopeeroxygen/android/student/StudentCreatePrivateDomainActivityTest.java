package com.playposse.peertopeeroxygen.android.student;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObjectNotFoundException;

import com.playposse.peertopeeroxygen.android.student.util.CommonNavigationActions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * A UI test that creates a private domain.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class StudentCreatePrivateDomainActivityTest {

    private static final String DOMAIN_NAME = "UI created domain";
    private static final String DOMAIN_DESCRIPTION = "A very unique and eloquent description.";

    @Rule
    public ActivityTestRule<StudentLoginActivity> mActivityRule =
            new ActivityTestRule<>(StudentLoginActivity.class);

    @Before
    public void setUp() throws UiObjectNotFoundException {
        CommonNavigationActions.login();
    }

    @Test
    public void createPrivateDomain() {
        CommonNavigationActions.createPrivateDomain(DOMAIN_NAME, DOMAIN_DESCRIPTION);
    }
}
