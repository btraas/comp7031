package ca.btraas.comp7031assignment1;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
public class LocationSearchEspressoTest {

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
    public void ensureLocationSearchWorks() {
        onView(withId(R.id.search)).perform(click());

        onView(withId(R.id.search_lat)).perform(typeText("50.001"), closeSoftKeyboard());
        onView(withId(R.id.search_lon)).perform(typeText("122.001"), closeSoftKeyboard());

        onView(withId(R.id.search_search)).perform(click());

        onView(withId(R.id.imageView)).check( matches(hasDrawable(true)) );
        onView(withId(R.id.imageView)).check( matches(isDisplayed()));

    }

    @Test
    public void ensureLocationNegativeSearchWorks() {
        onView(withId(R.id.search)).perform(click());

        onView(withId(R.id.search_lat)).perform(typeText("56.01"), closeSoftKeyboard());
        onView(withId(R.id.search_lon)).perform(typeText("126.01"), closeSoftKeyboard());

        onView(withId(R.id.search_search)).perform(click());

//        onView(withId(R.id.imageView)).check( matches(hasDrawable(false)) );
        onView(withId(R.id.imageView)).check( matches(not(isDisplayed())));


    }

}