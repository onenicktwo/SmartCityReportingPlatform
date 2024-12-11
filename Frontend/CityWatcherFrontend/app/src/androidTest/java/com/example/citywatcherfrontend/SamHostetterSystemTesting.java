package com.example.citywatcherfrontend;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SamHostetterSystemTesting {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testSuccessfulLogin() {

        // Enter valid username and password
        onView(withId(R.id.etUsername)).perform(typeText("Sam"));
        onView(withId(R.id.etPassword)).perform(typeText("Password123"), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.btnLogin)).perform(click());

        onView(withId(R.id.btnAdminView))
                .check(matches(isDisplayed()));

    }

    @Test
    public void testUnsuccessfulLogin() {

        onView(withId(R.id.etUsername)).perform(typeText("Sam"));
        onView(withId(R.id.etPassword)).perform(typeText("WrongPassword"), closeSoftKeyboard());

        onView(withId(R.id.btnLogin)).perform(click());

        onView(withId(R.id.btnLogin))
                .check(matches(isDisplayed()));

    }
    @Test
    public void testSuccessfulFetchUser() {
        ActivityScenario.launch(EditUserActivity.class);

        onView(withId(R.id.etUserId)).perform(typeText("13"), closeSoftKeyboard());

        onView(withId(R.id.btnFetchUser)).perform(click());

        onView(withId(R.id.etEditUsername))
                .check(matches(not(withText(""))));
    }

    @Test
    public void testUnsuccessfulFetchUser() {
        ActivityScenario.launch(EditUserActivity.class);

        onView(withId(R.id.etUserId)).perform(typeText("10000"), closeSoftKeyboard());

        onView(withId(R.id.btnFetchUser)).perform(click());

        onView(withId(R.id.etEditUsername))
                .check(matches(withText("")));


    }


}