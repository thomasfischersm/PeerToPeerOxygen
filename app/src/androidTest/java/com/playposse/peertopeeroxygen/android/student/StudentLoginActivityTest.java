package com.playposse.peertopeeroxygen.android.student;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.web.model.Atom;
import android.support.test.espresso.web.model.ElementReference;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
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
import com.playposse.peertopeeroxygen.android.student.util.CommonNavigationActions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webClick;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webKeys;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * A test to try logging into the application.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class StudentLoginActivityTest {

    private static final String LOG_CAT = StudentLoginActivityTest.class.getSimpleName();

    @Rule
    public ActivityTestRule<StudentLoginActivity> mActivityRule =
            new ActivityTestRule<>(StudentLoginActivity.class);

    @Test
    public void login() throws UiObjectNotFoundException {
        CommonNavigationActions.login();
        Log.i(LOG_CAT, "StudentLoginActivityTest.login has finished");
    }
}
