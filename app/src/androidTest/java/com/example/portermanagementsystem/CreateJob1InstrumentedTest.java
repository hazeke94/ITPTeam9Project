package com.example.portermanagementsystem;

import android.content.Context;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.portermanagementsystem.Activity.CreateJob.CreateJob1Activity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CreateJob1InstrumentedTest {

    @Rule
    public ActivityTestRule<CreateJob1Activity> mActivityTestRule = new ActivityTestRule<>(CreateJob1Activity.class);

    @Test
    public void createJobUITest(){
        onView(withId(R.id.editTextCaseID)).perform(typeText("I190012345"), closeSoftKeyboard());
        onView(withId(R.id.editTextDescription)).perform(typeText("testcase"), closeSoftKeyboard());
        onView(withId(R.id.spinnerFrom)).perform(typeText("L2, St Gabriel Ward (SG)"), closeSoftKeyboard());
        onView(withId(R.id.spinnerTo)).perform(typeText("L3, Our Lady Ward (OL)"), closeSoftKeyboard());
        onView(withText("L3, Our Lady Ward (OL)"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());


        onView(withId(R.id.btnNext1))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.textViewJobType))
                .check(matches(isDisplayed()));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.rb_transport)).perform(click());

        onView(withId(R.id.rb_transport)).check(matches(isChecked()));

        onView(withId(R.id.rb_xray)).check(matches(not(isChecked())));

        onView(withId(R.id.rb_Labs)).check(matches(not(isChecked())));

        onView(withId(R.id.rb_discharge)).check(matches(not(isChecked())));

        onView(withId(R.id.rb_document)).check(matches(not(isChecked())));

        onView(withId(R.id.btnNext2))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.textViewUrgencyLevel))
                .check(matches(isDisplayed()));

        onView(withId(R.id.radioButtonEmergency)).perform(click());

        onView(withId(R.id.radioButtonEmergency)).check(matches(isChecked()));

        onView(withId(R.id.radioButtonUrgent)).check(matches(not(isChecked())));

        onView(withId(R.id.radioButtonNormal)).check(matches(not(isChecked())));

        onView(withId(R.id.btnNext3))
                .check(matches(isDisplayed()))
                .perform(click());

    }

}
