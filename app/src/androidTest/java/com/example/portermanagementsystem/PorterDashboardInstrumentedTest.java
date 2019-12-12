package com.example.portermanagementsystem;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.portermanagementsystem.Activity.Dashboard.PorterDashboardActivity;
import com.example.portermanagementsystem.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class PorterDashboardInstrumentedTest {

    @Rule
    public ActivityTestRule<PorterDashboardActivity> mActivityTestRule = new ActivityTestRule<>(PorterDashboardActivity.class);

    @Test
    public void porterDashboardUITest(){

//        onView(withId(R.id.editTextStaffID)).perform(typeText("L123"), closeSoftKeyboard());
//        onView(withId(R.id.btnLogin))
//                .check(matches(isDisplayed()))
//                .perform(click());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.cardViewHistory)).check(matches(isDisplayed())).perform(scrollTo())
                .perform(click());
        Espresso.pressBack();

        onView(withId(R.id.itemLogout)).check(matches(isDisplayed())).perform(click());
    }
}
