package com.example.citywatcherfrontend;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;


import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Date;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SamRowlandSystemTest {
    public static final int TIMEOUT = 5000;

    private final Date commentDate = new Date();
    private Date commentEditDate;

    @Rule
    public ActivityScenarioRule<LoginOrReg> activityRule =
            new ActivityScenarioRule<>(LoginOrReg.class);


    public void LogIn(String username, String password) throws InterruptedException, UiObjectNotFoundException {
        onView(withId(R.id.btnLogin)).perform(click()).check(matches(isDisplayed()));
        onView(withId(R.id.etUsername)).perform(typeText(username));
        onView(withId(R.id.etPassword)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        Thread.sleep(TIMEOUT);
    }

    @Test
    public void a_makeIssue() throws InterruptedException, UiObjectNotFoundException {
        LogIn("johndoe", "securepassword");

        onView(withId(R.id.navbar_menu)).perform(click());
        onView(withText("Create Issue")).perform(click());

        Thread.sleep(TIMEOUT);

        onView(withId(R.id.editIssueTitle)).perform(typeText("Espresso Test Issue"), closeSoftKeyboard());
        onView(withId(R.id.editIssueCategory)).perform(typeText("Test"), closeSoftKeyboard());
        onView(withId(R.id.editIssueLocation)).perform(typeText("725 E 13th St, Ames, IA 50010"), closeSoftKeyboard());
        onView(withId(R.id.editIssueDescription)).perform(typeText("This is an Espresso test for creating an issue."), closeSoftKeyboard());
        onView(withId(R.id.buttonSubmitIssue)).perform(click());

        Thread.sleep(TIMEOUT);

        onView(withId(R.id.navbar_menu)).perform(click());
        onView(withText("View Issues")).perform(click());

        Thread.sleep(TIMEOUT);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Espresso Test Issue"));
        marker.click();
        onView(withId(R.id.buttonViewIssuesPopupDetails)).perform(click());

        Thread.sleep(TIMEOUT);

        onView(withId(R.id.issueDetailsTitle)).check(matches(withText("Espresso Test Issue")));

    }

    @Test
    public void b_editIssue() throws InterruptedException, UiObjectNotFoundException {
        LogIn("johndoe", "securepassword");

        onView(withId(R.id.navbar_menu)).perform(click());
        onView(withText("View Issues")).perform(click());

        Thread.sleep(TIMEOUT);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Espresso Test Issue"));
        marker.click();
        onView(withId(R.id.buttonViewIssuesPopupDetails)).perform(click());

        Thread.sleep(TIMEOUT);

        onView(withId(R.id.buttonEditIssue)).perform(click());

        Thread.sleep(TIMEOUT);

        onView(withId(R.id.updateIssueDescription)).perform(clearText());
        onView(withId(R.id.updateIssueDescription)).perform(typeText("This is an Espresso test for editing an issue."), closeSoftKeyboard());
        onView(withId(R.id.buttonUpdateIssue)).perform(click());

        Thread.sleep(TIMEOUT);

        device = UiDevice.getInstance(getInstrumentation());
        marker = device.findObject(new UiSelector().descriptionContains("Espresso Test Issue"));
        marker.click();
        onView(withId(R.id.buttonViewIssuesPopupDetails)).perform(click());

        Thread.sleep(TIMEOUT);

        onView(withId(R.id.issueDetailsDescription)).check(matches(withText("This is an Espresso test for editing an issue.")));
    }

    @Test
    public void c_makeComment() throws InterruptedException, UiObjectNotFoundException {
        LogIn("johndoe", "securepassword");

        onView(withId(R.id.navbar_menu)).perform(click());
        onView(withText("View Issues")).perform(click());

        Thread.sleep(TIMEOUT);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Espresso Test Issue"));
        marker.click();
        onView(withId(R.id.buttonViewIssuesPopupDetails)).perform(click());

        Thread.sleep(TIMEOUT);

        onView(withId(R.id.editAddComment)).perform(typeText("This is an Espresso test comment written at " + commentDate ), closeSoftKeyboard());
        onView(withId(R.id.buttonAddComment)).perform(click());

        Thread.sleep(TIMEOUT);

        onView(withId(R.id.buttonViewIssuesPopupDetails)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.listComments)).atPosition(0).onChildView(withId(R.id.commentContent)).check(matches(withText("This is an Espresso test comment written at " + commentDate)));

    }

    @Test
    public void d_editComment() throws InterruptedException, UiObjectNotFoundException {
        LogIn("johndoe", "securepassword");
        commentEditDate = new Date();

        onView(withId(R.id.navbar_menu)).perform(click());
        onView(withText("View Issues")).perform(click());

        Thread.sleep(TIMEOUT);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Espresso Test Issue"));
        marker.click();
        onView(withId(R.id.buttonViewIssuesPopupDetails)).perform(click());

        Thread.sleep(TIMEOUT);

        onData(anything()).inAdapterView(withId(R.id.listComments)).atPosition(0).onChildView(withId(R.id.commentMenu)).perform(click());
        onView(withText("Edit Comment")).check(matches(isDisplayed())).perform(click());

        Thread.sleep(TIMEOUT);

        onView(withId(R.id.etEditCommentText)).perform(clearText());
        onView(withId(R.id.etEditCommentText)).perform(typeText("This is an Espresso test comment written at " + commentEditDate));
        onView(withId(R.id.btnSaveCommentChanges)).perform(click());

        Thread.sleep(TIMEOUT);

        onView(withId(R.id.buttonViewIssuesPopupDetails)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.listComments)).atPosition(0).onChildView(withId(R.id.commentContent)).check(matches(withText("This is an Espresso test comment written at " + commentEditDate)));
    }

    @Test
    public void e_reportComment() throws InterruptedException, UiObjectNotFoundException {
        LogIn("janedoe", "securepassword");

        onView(withId(R.id.navbar_menu)).perform(click());
        onView(withText("View Issues")).perform(click());

        Thread.sleep(TIMEOUT);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Espresso Test Issue"));
        marker.click();
        onView(withId(R.id.buttonViewIssuesPopupDetails)).perform(click());

        Thread.sleep(TIMEOUT);

        onData(anything()).inAdapterView(withId(R.id.listComments)).atPosition(0).onChildView(withId(R.id.commentMenu)).perform(click());
        onView(withText("Report Comment")).perform(click());

        Thread.sleep(TIMEOUT);

        onView(withId(R.id.editReportReason)).perform(typeText("This is a comment report."), closeSoftKeyboard());
        onView(withId(R.id.buttonReportComment)).perform(click());
    }

    @Test
    public void f_deleteComment() throws InterruptedException, UiObjectNotFoundException {
        LogIn("johndoe", "securepassword");

        onView(withId(R.id.navbar_menu)).perform(click());
        onView(withText("View Issues")).perform(click());

        Thread.sleep(TIMEOUT);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Espresso Test Issue"));
        marker.click();
        onView(withId(R.id.buttonViewIssuesPopupDetails)).perform(click());

        Thread.sleep(TIMEOUT);

        onData(anything()).inAdapterView(withId(R.id.listComments)).atPosition(0).onChildView(withId(R.id.commentMenu)).perform(click());
        onView(withText("Delete Comment")).perform(click());

        Thread.sleep(TIMEOUT);

        onView(withId(R.id.buttonViewIssuesPopupDetails)).perform(click());

        Thread.sleep(TIMEOUT);

        onData(anything()).inAdapterView(withId(R.id.listComments)).atPosition(0).onChildView(withId(R.id.commentContent)).check(matches(not(withText("This is an Espresso test comment written at " + commentEditDate))));
    }

    @Test
    public void g_deleteIssue() throws InterruptedException, UiObjectNotFoundException {
        LogIn("johndoe", "securepassword");

        onView(withId(R.id.navbar_menu)).perform(click());
        onView(withText("View Issues")).perform(click());

        Thread.sleep(TIMEOUT);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Espresso Test Issue"));
        marker.click();
        onView(withId(R.id.buttonViewIssuesPopupDetails)).perform(click());

        Thread.sleep(TIMEOUT);

        onView(withId(R.id.buttonDeleteIssue)).perform(click());

        Thread.sleep(TIMEOUT);


    }




}
