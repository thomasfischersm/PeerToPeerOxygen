package com.playposse.peertopeeroxygen.android.student.util;

import android.app.Activity;

import com.playposse.peertopeeroxygen.android.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

/**
 * Espresso checks to assert that a specific {@link android.app.Activity} is open.
 *
 * <p>Apparently, Espresso doesn't have a convenient method to get the current
 * {@link android.app.Activity}. There are workarounds many lines of code long to hack into
 * Espresso. The alternative is to check a for {@link android.view.View} that's unique to the
 * particular {@link Activity}. This class provides convenience methods to do that.
 */
public final class CurrentActivityCheck {

    private CurrentActivityCheck() {}

    public static void checkStudentLoginActivity() {
        onView(withId(R.id.login_button))
                .check(matches(isDisplayed()));
    }

    public static void checkStudentIntroductionDeckActivity() {
        onView(withId(R.id.introductionSlidePager))
                .check(matches(isDisplayed()));
    }

    public static void checkStudentDomainSelectionActivity() {
        onView(withId(R.id.invitationCodeEditText))
                .check(matches(isDisplayed()));
    }

    public static void checkStudentDomainSelectionActivityOnLastFragment() {
        onView(allOf(withId(R.id.closeIntroductionDeckButton), isDisplayed()))
                .check(matches(isDisplayed()));
    }

    public static void checkAdminMainActivity() {
        onView(withId(R.id.editDomainLink))
                .check(matches(isDisplayed()));
    }

    public static void checkStudentMainActivity() {
        onView(withId(R.id.missionHeadingTextView))
                .check(matches(isDisplayed()));
    }
}
