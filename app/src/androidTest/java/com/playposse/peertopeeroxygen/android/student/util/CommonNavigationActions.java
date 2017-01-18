package com.playposse.peertopeeroxygen.android.student.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.student.UiTestSuite;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * A collection of utility methods that do common navigations within the app.
 */
public class CommonNavigationActions {

    private static final String LOG_CAT = CommonNavigationActions.class.getSimpleName();

    public enum ActivityType {
        studentIntroductionDeckActivity,
        studentDomainSelectionActivity,
        studentMainActivity,
    }

    /**
     * Logs the user in. This method assumes that the user is already on the app's login page.
     */
    public static void login(ActivityType destinationActivity) throws UiObjectNotFoundException {
        Context context = InstrumentationRegistry.getContext();
        Log.i(LOG_CAT, "0. Session id: " + OxygenSharedPreferences.getSessionId(context));

        // Go from the app's login page to the FB login page.
        CurrentActivityCheck.checkStudentLoginActivity();
        onView(withId(R.id.login_button))
                .perform(click());

        // Start UI instrumentation
        final UiDevice mDevice =
                UiDevice.getInstance(getInstrumentation());
        final int timeOut = 1000 * 60;
        mDevice.wait(Until.findObject(By.clazz(WebView.class)), timeOut);

        // Enter user name.
        Log.i(LOG_CAT, "1. Session id: " + OxygenSharedPreferences.getSessionId(context));
        UiObject emailInput = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(EditText.class));
        emailInput.waitForExists(timeOut);
        emailInput.setText(UiTestSuite.getFbTestUser().getEmail());

        // Enter password.
        UiObject passwordInput = mDevice.findObject(new UiSelector()
                .instance(1)
                .className(EditText.class));
        passwordInput.waitForExists(timeOut);
        passwordInput.setText(UiTestSuite.getFbTestUser().getPassword());

        // Click submit button.
        UiObject buttonLogin = mDevice.findObject(new UiSelector()
                .instance(0)
                .className(Button.class));

        // Wait for the second FB page to show up.
        Log.i(LOG_CAT, "2. Session id: " + OxygenSharedPreferences.getSessionId(context));
        buttonLogin.waitForExists(timeOut);
        buttonLogin.clickAndWaitForNewWindow();

        // Click OK on the second FB page.
        UiObject buttonOk = mDevice.findObject(new UiSelector()
                .instance(1)
                .className(Button.class));
        buttonOk.waitForExists(timeOut);
        buttonOk.click();

        // Verify that the first real page in the app shows up.
        Log.i(LOG_CAT, "3. Session id: " + OxygenSharedPreferences.getSessionId(context));
        switch (destinationActivity) {
            case studentIntroductionDeckActivity:
                CurrentActivityCheck.checkStudentIntroductionDeckActivity();
                break;
            case studentDomainSelectionActivity:
                CurrentActivityCheck.checkStudentDomainSelectionActivity();
                break;
            case studentMainActivity:
                CurrentActivityCheck.checkStudentMainActivity();
                break;
            default:
                throw new IllegalArgumentException(
                        "Unexpected destination activity: " + destinationActivity);
        }
    }

    /**
     * Expects to be on the introduction deck, swipes to the last fragment, and closes it.
     */
    public static void moveThroughIntroductionDeck(ActivityType destinationActivity) {
        CurrentActivityCheck.checkStudentIntroductionDeckActivity();

        // Swipe left until we are on the last fragment in the ViewPager.
        for (int i = 0; i < 3; i++) {
            onView(withId(R.id.introductionSlidePager))
                    .perform(swipeLeft());
        }
        CurrentActivityCheck.checkStudentDomainSelectionActivityOnLastFragment();

        // Close the intro deck.
        onView(allOf(withId(R.id.closeIntroductionDeckButton), isDisplayed()))
                .perform(click());

        switch (destinationActivity) {
            case studentDomainSelectionActivity:
                CurrentActivityCheck.checkStudentDomainSelectionActivity();
                break;
            case studentMainActivity:
                CurrentActivityCheck.checkStudentMainActivity();
                break;
            default:
                throw new IllegalArgumentException(
                        "Unexpected destination activity: " + destinationActivity);
        }
    }

    /**
     * Creates a private domain that can be used for testing.
     *
     * <p>The method assumes that the user is on the activity to select a domain.
     */
    public static void createPrivateDomain(String domainName, String domainDescription) {
        // Click button to start creating private domain on domain selection page.
        onView(withId(R.id.createPrivateDomainButton))
                .perform(click());

        // Wait for the activity that takes the information for the new private domain.
        onView(withId(R.id.domainNameEditText))
                .check(matches(isDisplayed()));

        // Enter the domain information and submit.
        onView(withId(R.id.domainNameEditText))
                .perform(typeText(domainName));
        onView(withId(R.id.domainDescriptionEditText))
                .perform(typeText(domainDescription));
        onView(withId(R.id.createPrivateDomainButton))
                .perform(click());

        // Verify that the admin home shows up.
        onView(withId(R.id.editDomainLink))
                .check(matches(isDisplayed()));

        // Go to the domain selection page and check that the new domain exists.
        goToDomainSelectionActivity();
    }

    /**
     * Uses the options menu to go to the domain selection activity.
     */
    public static void goToDomainSelectionActivity() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.switch_domain_menu_title))
                .perform(click());
        onView(withId(R.id.createPrivateDomainButton))
                .check(matches(isDisplayed()));
    }
}
