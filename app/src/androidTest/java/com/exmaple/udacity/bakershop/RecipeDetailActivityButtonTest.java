package com.exmaple.udacity.bakershop;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.exmaple.udacity.bakershop.ui.MainActivity;
import com.exmaple.udacity.bakershop.ui.RecipeDetailStepActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
public class RecipeDetailActivityButtonTest {
    @Rule
    public ActivityTestRule<RecipeDetailStepActivity> activityTestRule1=new ActivityTestRule<RecipeDetailStepActivity>(RecipeDetailStepActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent=new Intent();
            intent.putExtra(RecipeDetailStepActivity.RECIPE_ID_EXTRA,1);
            intent.putExtra(RecipeDetailStepActivity.STEP_ID_EXTRA,1);
            intent.putExtra(MainActivity.RECIPE_NAME,"Nutella Pie");
            return intent;
        }
    };

    @Test
    public void testOnToolbarRecipeTitle() {
        onView(allOf(isAssignableFrom(TextView.class),withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText("Nutella Pie")));
    }


    @Test
    public void testOnNavigationbuttonPressed() {
        for(int i=1;i<=5;++i) {
            String nextStep=Integer.toString(i+1);
            onView(withId(R.id.buttonNextRecipeStep)).perform(scrollTo(),click());
            onView(withId(R.id.textViewRecipeDetailInstruction)).check(matches(withText(containsString(nextStep))));
        }
        for(int i=6;i>=2;--i) {
            String nextStep=Integer.toString(i-1);
            onView(withId(R.id.buttonPrevRecipeStep)).perform(scrollTo(),click());
            onView(withId(R.id.textViewRecipeDetailInstruction)).check(matches(withText(containsString(nextStep))));
        }
    }
}
