package ca.btraas.comp7031assignment1;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
import android.widget.ImageView;

import junit.framework.AssertionFailedError;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class SearchEspressoTest {

    static class DrawableMatcher extends BaseMatcher<View> {

        static final int EMPTY = -1;
        static final int ANY = -2;

        int type;
        public DrawableMatcher(int type) {
            this.type = type;
        }


        @Override
        public boolean matches(Object item) {
            if(item instanceof ImageView) {
                if(type == EMPTY) {
                    return ((ImageView)item).getDrawable() == null;
                }
                if(type == ANY) {
                    return ((ImageView)item).getDrawable() != null;
                }
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText((type == EMPTY ? "NULL drawable" : "drawable"));
        }
    }

    public static Matcher<View> hasDrawable(boolean yes) {
        return new DrawableMatcher(yes ? DrawableMatcher.ANY : DrawableMatcher.EMPTY);
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);


    // ----- Begin automated tests ----- //

    @Test
    public void ensureKeywordSearchWorks() {
        onView(withId(R.id.search)).perform(click());

        onView(withId(R.id.search_tag)).perform(typeText("glass"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());

        onView(withId(R.id.imageView)).check( matches(hasDrawable(true)) );
        onView(withId(R.id.imageView)).check( matches(isDisplayed()));


    }

    @Test
    public void ensureNegativeKeywordSearchWorks() {
        onView(withId(R.id.search)).perform(click());

        onView(withId(R.id.search_tag)).perform(typeText("glaljiasdfla1234,mlaksdjfss"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());

//        onView(withId(R.id.imageView)).check( matches(hasDrawable(false)) );
        onView(withId(R.id.imageView)).check( matches(not(isDisplayed())));


    }

    @Test
    public void ensureDateSearchWorks() {
        onView(withId(R.id.search)).perform(click());

        onView(withId(R.id.search_fromDate)).perform(typeText("2019-02-01"), closeSoftKeyboard());
        onView(withId(R.id.search_toDate)).perform(typeText("2019-02-10"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());

        onView(withId(R.id.imageView)).check( matches(hasDrawable(true)) );
        onView(withId(R.id.imageView)).check( matches((isDisplayed())));


    }

    @Test
    public void ensureNegativeDateSearchWorks() {
        onView(withId(R.id.search)).perform(click());

        onView(withId(R.id.search_fromDate)).perform(typeText("2058-02-01"), closeSoftKeyboard());
        onView(withId(R.id.search_toDate)).perform(typeText("2058-02-10"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());

//        onView(withId(R.id.imageView)).check( matches(hasDrawable(false)) );
        onView(withId(R.id.imageView)).check( matches(not(isDisplayed())));


    }
}