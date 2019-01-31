package ca.btraas.comp7031assignment1;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.EspressoKey;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.support.v7.widget.SearchView;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ca.btraas.comp7031assignment1.MainActivity;
import ca.btraas.comp7031assignment1.R;
import ca.btraas.comp7031assignment1.SearchActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isJavascriptEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class MyApplicationEspressoTest {



    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);
    @Test
    public void ensureKeywordSearchWorks() {
        onView(withId(R.id.search)).perform(click());

        onView(withId(R.id.search_tag)).perform(typeText("PC"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.left)).perform(click());
        }
    }

    @Test
    public void ensureDateSearchWorks() {
        onView(withId(R.id.search)).perform(click());

        onView(withId(R.id.search_toDate)).perform(typeText("2018-12-01"), closeSoftKeyboard());
        onView(withId(R.id.search_fromDate)).perform(typeText("2018-12-31"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());


        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.left)).perform(click());
        }

        //onView(withContentDescription("Navigate up")).perform(click());
    }
}