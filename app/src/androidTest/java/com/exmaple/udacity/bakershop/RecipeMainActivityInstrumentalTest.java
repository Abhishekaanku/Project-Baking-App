package com.exmaple.udacity.bakershop;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exmaple.udacity.bakershop.ui.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class RecipeMainActivityInstrumentalTest {
    private  String[] recipeName={"Cheesecake","Nutella Pie","Brownies","Yellow Cake"};

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule=new ActivityTestRule<>(MainActivity.class);

    @Test
    public void onRecipeCardCheckRecipeName() {
        for(int i=0;i<4;++i) {
            onView(withId(R.id.recipeRecyclerView)).perform(scrollToPosition(i));
            onView(nthChildOf(withId(R.id.recipeRecyclerView), i))
                    .check(matches(hasDescendant(withText(recipeName[i]))));

        }
    }

    @Test
    public void onRecipeCardClickTestForRecipeName() {
        for(int i=0;i<4;++i) {
            onView(withId(R.id.recipeRecyclerView)).perform(scrollToPosition(i));
            onView(withId(R.id.recipeRecyclerView))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));
            onView(allOf(isAssignableFrom(TextView.class),withParent(isAssignableFrom(Toolbar.class))))
                    .check(matches(withText(recipeName[i])));
            onView(isRoot()).perform(ViewActions.pressBack());
        }

    }
    public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with "+childPosition+" child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }

                ViewGroup group = (ViewGroup) view.getParent();
                return parentMatcher.matches(view.getParent()) && group.getChildAt(childPosition).equals(view);
            }
        };
    }
}
