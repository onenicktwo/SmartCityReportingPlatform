package com.example.citywatcherfrontend;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;


import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import android.widget.ListView;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class SamRowlandSystemTest {
    public static final int TIMEOUT = 5000;
    private final Date commentDate = new Date();

    @Rule
    public ActivityScenarioRule<LoginOrReg> activityRule =
            new ActivityScenarioRule<>(LoginOrReg.class);


    @Before
    public void LogIn() throws InterruptedException, UiObjectNotFoundException {
        onView(withId(R.id.btnLogin)).perform(click()).check(matches(isDisplayed()));
        onView(withId(R.id.etUsername)).perform(typeText("johndoe"));
        onView(withId(R.id.etPassword)).perform(typeText("securepassword"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        Thread.sleep(TIMEOUT);
    }

    @Test
    public void makeComment() throws InterruptedException, UiObjectNotFoundException {
        onView(withId(R.id.navbar_menu)).perform(click());
        onView(withText("View Issues")).perform(click());

        Thread.sleep(TIMEOUT);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Pothole on Main Street"));
        marker.click();
        onView(withId(R.id.buttonViewIssuesPopupDetails)).perform(click());

        Thread.sleep(TIMEOUT);

        onView(withId(R.id.editAddComment)).perform(typeText("This is a Espresso test comment written at " + commentDate ), closeSoftKeyboard());
        onView(withId(R.id.buttonAddComment)).perform(click());

        device = UiDevice.getInstance(getInstrumentation());
        marker = device.findObject(new UiSelector().descriptionContains("Pothole on Main Street"));
        marker.click();
        onView(withId(R.id.buttonViewIssuesPopupDetails)).perform(click());

        Thread.sleep(TIMEOUT);
    }

    @Test
    public void editComment() throws InterruptedException, UiObjectNotFoundException {
        onView(withId(R.id.navbar_menu)).perform(click());
        onView(withText("View Issues")).perform(click());

        Thread.sleep(TIMEOUT);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Pothole on Main Street"));
        marker.click();
        onView(withId(R.id.buttonViewIssuesPopupDetails)).perform(click());

        Thread.sleep(TIMEOUT);

        onData(anything()).inAdapterView(withId(R.id.listComments)).atPosition(0).onChildView(withId(R.id.buttonEditComment)).check(matches(isDisplayed())).perform(click());
        Thread.sleep(TIMEOUT);
    }

}
