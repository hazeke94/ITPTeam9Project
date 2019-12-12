package com.example.portermanagementsystem;

import android.support.test.rule.ActivityTestRule;

import com.example.portermanagementsystem.Activity.CreateJob.CreateJob1Activity;
import com.example.portermanagementsystem.Activity.LoginActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class LoginInstrumentedTest {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void LoginUITest() {
    //editTextStaffID
        //btnLogin
        onView(withId(R.id.editTextStaffID)).perform(typeText("M123"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin))
                .check(matches(isDisplayed()))
                .perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.fabRept))
                .check(matches(isDisplayed()));

        onView(withId(R.id.itemLogout)).check(matches(isDisplayed())).perform(click());
    }
}
